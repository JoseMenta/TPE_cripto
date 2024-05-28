package org.example;

import org.example.data.BMP;
import org.example.data.Payload;

public class LSB1 implements Algorithm{
    private static final int SIZE_BITS = 4 * 8;
    private static final int BYTE_SIZE = 8;

    @Override
    public BMP embed(BMP bmp, Payload payload) {
        byte[] sizeArray = payload.getSizeBinary();
        byte[] bmpData = bmp.getData();

        for (int i = 0; i < SIZE_BITS; i++) {
            int bitIndex = i % BYTE_SIZE;
            int byteIndex = i / BYTE_SIZE;
            byte targetByte = sizeArray[byteIndex];
            //TODO: aca creo que el shift sería (targetByte >> (7-bitIndex)), porque el primer bit es el que se obtiene haciendo 7 shifts a la derecha
            int targetBit = (targetByte >> bitIndex) & 1;
            bmpData[i] = (byte) ((bmpData[i] & 0xFE) | targetBit);
        }

        byte[] payloadData = payload.getContent();
        int offset = SIZE_BITS;

        for (int i = 0; i < payloadData.length; i++) {
            for (int j = 0; j < BYTE_SIZE; j++) {
                byte targetByte = payloadData[i];
                int targetBit = (targetByte >> j) & 1;
                bmpData[offset] = (byte) ((bmpData[offset] & 0xFE) | targetBit);
                offset++;
            }
        }

        byte[] extension = payload.getExtensionBinary();
        for (int i = 0; i < extension.length; i++) {
            for (int j = 0; j < BYTE_SIZE; j++) {
                byte targetByte = extension[i];
                int targetBit = (targetByte >> j) & 1;
                bmpData[offset] = (byte) ((bmpData[offset] & 0xFE) | targetBit);
                offset++;
            }
        }

        for (int i = 0; i < BYTE_SIZE; i++) {
            bmpData[offset] = (byte) (bmpData[offset] & 0xFE);
            offset++;
        }

        bmp.setData(bmpData);
        return bmp;
    }

    @Override
    public Payload recover(BMP bmp,boolean withExtension) {
        return null;
    }
}
