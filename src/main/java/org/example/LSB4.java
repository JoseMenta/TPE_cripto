package org.example;

import org.example.data.BMP;
import org.example.data.Payload;
import org.example.exceptions.InsuficientSizeException;

public class LSB4 implements Algorithm{

    private static final int CONTENT_BYTES_PER_INFO_BYTE = 2;

    private int getMaxLength(BMP bmp){
        return bmp.getData().length / CONTENT_BYTES_PER_INFO_BYTE;
    }

    @Override
    public BMP embed(BMP bmp, Payload payload) {
        if(getMaxLength(bmp) < payload.getTotalLength()){
            throw new InsuficientSizeException();
        }


    }

    @Override
    public Payload recover(BMP bmp) {
        return null;
    }
}
