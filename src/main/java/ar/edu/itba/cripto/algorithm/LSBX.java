package ar.edu.itba.cripto.algorithm;

import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.exceptions.InsuficientSizeException;

import java.util.Arrays;

public class LSBX implements Algorithm {

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
        while (bitIndex < payloadContent.length * 8) {
            // Get the current payload byte
            byte info = payloadContent[bitIndex / 8];
            // Get the current bit from the payload byte
            int bit = (info >> (7 - bitIndex % 8)) & 0x01;
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

        // Read the length
        final byte[] sizeBinary = new byte[Integer.BYTES];
        int bitIndex = 0; // Index used for sizeBinary
        while (bitIndex < Integer.BYTES * 8) {
            int porterByteIndex = bitIndex / n;
            int porterBitIndex = bitIndex % n;
            int sizeBinaryIndex = bitIndex / 8;
            int sizeBinaryBitIndex = bitIndex % 8;



            bitIndex++;
        }

    }
}
