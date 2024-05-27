package org.example;

import org.example.data.BMP;
import org.example.data.Payload;

public class LSB1 implements Algorithm{
    private static final int SIZE_BITS = 4*8;
    private static final int BYTE_SIZE = 8;

    @Override
    public BMP embed(BMP bmp, Payload payload) {
        byte[] sizeArray = payload.getSizeBinary();
        byte[] bmpData = bmp.getData();
        for (int i = 0; i < SIZE_BITS; i++) {

            int bitIndex = i % BYTE_SIZE;
            int byteIndex = i / BYTE_SIZE;

            byte myByte = sizeArray[byteIndex];
            int targetBit = sizeArray[bitIndex];

            byte bmpByte = (byte) (bmpData[] & 0xFE);
            bmpByte = (byte) (bmpByte | bit);

        }

    }

    @Override
    public Payload recover(BMP bmp) {
        return null;
    }
}
