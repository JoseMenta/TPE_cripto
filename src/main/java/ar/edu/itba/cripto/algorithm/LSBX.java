package ar.edu.itba.cripto.algorithm;

import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.exceptions.InsuficientSizeException;

import java.util.Arrays;

public class LSBX implements Algorithm {
    private final static int BYTE_SIZE = 8;

    private final int n;
    private final int bmpBytesNeededPerPayloadByte;

    public LSBX(int n) {
        if (n < 1 || n > 8) {
            throw new IllegalArgumentException("Invalid n");
        }
        this.n = n;
        this.bmpBytesNeededPerPayloadByte = (int) Math.ceil(8.0 / n);
    }

    @Override
    public int getMaxLength(BMP bmp) {
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

    private byte[] recoverBytes(byte[] porter, int length, int bitIndexOffset) {
        final byte[] ans = new byte[length];
        int bitIndex = bitIndexOffset;
        byte currentByte = 0;
        for (; bitIndex < length * BYTE_SIZE; bitIndex++) {
            int porterByteIndex = bitIndex / n;
            int porterBitIndex = bitIndex % n;
            int nextBit = (porter[porterByteIndex] >> (n - 1 - porterBitIndex)) & 0x01;
            currentByte <<= 1;
            currentByte |= (byte) nextBit;
            if (bitIndex % BYTE_SIZE == BYTE_SIZE - 1) {
                int ansByteIndex = length - 1 - bitIndex / BYTE_SIZE;
                ans[ansByteIndex] = currentByte;
                currentByte = 0;
            }
        }
        return ans;
    }

    @Override
    public Payload recover(BMP bmp, boolean withExtension) {
        final int maxLength = getMaxLength(bmp);
        final Payload ans = new Payload();
        if (maxLength < Integer.BYTES) {
            throw new IllegalArgumentException("Can't read size from porter");
        }

        final byte[] porterData = bmp.getData();

        // Read the length
        final byte[] sizeBinary = recoverBytes(porterData, Integer.BYTES, 0);
        ans.setSize(sizeBinary);
        final int size = ans.getSize();
        if (size > maxLength) {
            throw new IllegalArgumentException("Can't read content from porter");
        }

        // Read the content
        final byte[] porterContent = recoverBytes(porterData, size, Integer.BYTES * BYTE_SIZE);
        ans.setContent(porterContent);

        // Read the extension
        if (withExtension) {
            StringBuilder builder = new StringBuilder();
            int offset = (Integer.BYTES + size) * BYTE_SIZE;
            while (true) {
                byte character = recoverBytes(porterData, 1, offset)[0];
                if (character == 0) {
                    break;
                }
                builder.append((char) character);
                offset += BYTE_SIZE;
            }
            ans.setExtension(builder.toString());
        } else {
            ans.setExtension(null);
        }
        return ans;
    }
}
