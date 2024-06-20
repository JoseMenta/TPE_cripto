package ar.edu.itba.cripto.algorithm;

import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.exceptions.InsuficientSizeException;

import java.util.Arrays;
import java.util.Iterator;

public class LSBX implements Algorithm {
    private final static int BYTE_SIZE = 8;

    private final int n;

    public LSBX(int n) {
        if (n < 1 || n > 8) {
            throw new IllegalArgumentException("Invalid n");
        }
        this.n = n;
    }

    @Override
    public int getMaxLength(BMP bmp) {
        int bmpBytesNeededPerPayloadByte = (int) Math.ceil(BYTE_SIZE / (double) n);
        return (int) Math.floor(bmp.getData().length / (double) bmpBytesNeededPerPayloadByte);
    }

    @Override
    public BMP embed(BMP bmp, Payload payload) {
        if (getMaxLength(bmp) < payload.getTotalLength()) {
            throw new InsuficientSizeException();
        }
        final byte[] ansContent = Arrays.copyOf(bmp.getData(), bmp.getData().length);
        final byte[] payloadContent = payload.getBinary();
        int bitIndex = 0;
        // Iterate over the payload bits
        while (bitIndex < payloadContent.length * BYTE_SIZE) {
            // Get the current payload byte
            byte info = payloadContent[bitIndex / BYTE_SIZE];
            // Get the current bit from the payload byte
            int bit = (info >> (BYTE_SIZE - 1 - bitIndex % BYTE_SIZE)) & 0x01;
            // Get the index of the bmp byte to modify
            int bmpIndex = bitIndex / n;
            // Get the index of the bit inside the bmp byte
            int bmpBitIndex = bitIndex % n;
            if (bit == 0x00) {
                // If the current payload bit is 0, set the bmp bit to 0
                ansContent[bmpIndex] &= (byte) ~(0x01 << (n - 1 - bmpBitIndex));
            } else {
                // If the current payload bit is 1, set the bmp bit to 1
                ansContent[bmpIndex] |= (byte) (0x01 << (n - 1 - bmpBitIndex));
            }
            bitIndex++;
        }
        return new BMP(bmp.getSize(), ansContent, bmp.getHeader());
    }

    @Override
    public Payload recover(BMP bmp, boolean withExtension) {
        final int maxLength = getMaxLength(bmp);
        final Payload ans = new Payload();
        if (maxLength < Integer.BYTES) {
            throw new IllegalArgumentException("Can't read size from porter");
        }

        final byte[] porterData = bmp.getData();
        final ByteRecoverIterator iterator = new ByteRecoverIterator(porterData, n);

        // Read the size
        final byte[] sizeBinary = iterator.nextNBytes(Integer.BYTES);
        ans.setSize(sizeBinary);
        final int size = ans.getSize();
        if (size > maxLength) {
            throw new IllegalArgumentException("Can't read content from porter");
        }

        // Read the content
        final byte[] porterContent = iterator.nextNBytes(size);
        ans.setContent(porterContent);

        // Read the extension
        if (withExtension) {
            StringBuilder builder = new StringBuilder();
            byte character = 0;
            do {
                character = iterator.next();
                if (character != 0) {
                    builder.append((char) character);
                }
            } while (character != 0);
            ans.setExtension(builder.toString());
        } else {
            ans.setExtension(null);
        }
        return ans;
    }

    private static class ByteRecoverIterator implements Iterator<Byte> {
        private final byte[] data;
        private int bitIndex;
        private final int bitsPerByte;

        public ByteRecoverIterator(byte[] data, int bitsPerByte) {
            if (bitsPerByte < 1 || bitsPerByte > 8) {
                throw new IllegalArgumentException("Invalid bits per byte");
            }
            if (data == null) {
                throw new IllegalArgumentException("Data is null");
            }
            this.data = data;
            this.bitIndex = 0;
            this.bitsPerByte = bitsPerByte;
        }

        @Override
        public boolean hasNext() {
            return hasNBytes(1);
        }

        public boolean hasNBytes(int n) {
            int totalBits = (int) Math.ceil(data.length * bitsPerByte / (double) BYTE_SIZE) * BYTE_SIZE;
            int neededDataBits = n * BYTE_SIZE;
            return bitIndex + neededDataBits <= totalBits;
        }

        @Override
        public Byte next() {
            if (!hasNext()) {
                throw new IllegalStateException("No more elements");
            }
            byte ans = 0;
            do {
                int porterByteIndex = bitIndex / bitsPerByte;
                int porterBitIndex = bitIndex % bitsPerByte;
                int nextBit = (data[porterByteIndex] >> (bitsPerByte - 1 - porterBitIndex)) & 0x01;
                ans <<= 1;
                ans |= (byte) nextBit;
                bitIndex++;
            } while (bitIndex % BYTE_SIZE != 0);
            return ans;
        }

        public byte[] nextNBytes(int n) {
            byte[] ans = new byte[n];
            for (int i = 0; i < n; i++) {
                ans[i] = next();
            }
            return ans;
        }
    }
}
