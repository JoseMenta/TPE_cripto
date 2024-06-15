package org.example.algorithm;

import org.example.data.BMP;
import org.example.data.Payload;
import org.example.exceptions.InsuficientSizeException;

public class LSB1 implements Algorithm{
    private static final int SIZE_BITS = 4 * 8;
    private static final int BYTE_SIZE = 8;

    @Override
    public int getMaxLength(BMP bmp){
        return bmp.getData().length / BYTE_SIZE;
    }

    @Override
    public BMP embed(BMP bmp, Payload payload) {

        if(getMaxLength(bmp) < payload.getTotalLength()){
            throw new InsuficientSizeException();
        }

        byte[] sizeArray = payload.getSizeBinary();
        byte[] bmpData = bmp.getData();

        for (int i = 0; i < SIZE_BITS; i++) {
            int bitIndex = i % BYTE_SIZE;
            int byteIndex = i / BYTE_SIZE;
            byte targetByte = sizeArray[byteIndex];
            //FIX: sería (targetByte >> (7-bitIndex)), porque el primer bit es el que se obtiene haciendo 7 shifts a la derecha
            int targetBit = (targetByte >> (7- bitIndex)) & 0x01;
            bmpData[i] = (byte) ((bmpData[i] & 0xFE) | targetBit);
        }

        byte[] payloadData = payload.getContent();
        int offset = SIZE_BITS;

        for (int i = 0; i < payloadData.length; i++) {
            for (int j = 0; j < BYTE_SIZE; j++) {
                byte targetByte = payloadData[i];
                // FIX: Misma correccion que arriba
                int targetBit = (targetByte >> (7 - j)) & 0x01;
                bmpData[offset] = (byte) ((bmpData[offset] & 0xFE) | targetBit);
                offset++;
            }
        }

        byte[] extension = payload.getExtensionBinary();
        for (int i = 0; i < extension.length; i++) {
            for (int j = 0; j < BYTE_SIZE; j++) {
                byte targetByte = extension[i];
                // FIX: Misma correccion que arriba
                int targetBit = (targetByte >> (7-j)) & 0x01;
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
    public Payload recover(BMP bmp, boolean withExtension) {
        final int maxLength = getMaxLength(bmp);
        final Payload ans = new Payload();
        if(maxLength < Integer.BYTES){
            throw new IllegalArgumentException("Can't read size from porter");
        }

        final byte[] sizeBinary = new byte[Integer.BYTES];
        final byte[] porterData = bmp.getData();
        int p = 0; //index used for porter
        for (int i = 0; i < Integer.BYTES; i++) {
            sizeBinary[i] = 0;
            for (int j = 0; j < BYTE_SIZE; j++) {
                sizeBinary[i] = (byte) ((sizeBinary[i] << 1) | (porterData[p] & 0x01));
                p++;
            }
        }
        ans.setSize(sizeBinary);
        final int size = ans.getSize();
        if(size < maxLength){
            throw new IllegalArgumentException("Can't read content from porter");
        }
        final byte[] content = new byte[size];
        for (int i = 0; i < size; i++) {
            content[i] = 0;
            for (int j = 0; j < BYTE_SIZE; j++) {
                content[i] = (byte) ((content[i] << 1) | (porterData[p] & 0x01));
                p++;
            }
        }
        ans.setContent(content);
        String extension = null;
        if(withExtension){
            StringBuilder builder = new StringBuilder();
            byte last = 0;
            do{
                for (int j = 0; j < BYTE_SIZE; j++) {
                    last = (byte) ((last << 1) | (porterData[p] & 0x01));
                    p++;
                }
                if(last!=0){
                    builder.append((char) last);
                }
            } while (last != 0);
            extension = builder.toString();
        }
        ans.setExtension(extension);
        return ans;
    }
}
