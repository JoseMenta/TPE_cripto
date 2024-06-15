package org.example.algorithm;

import org.example.data.BMP;
import org.example.data.Payload;

public class LSBI implements Algorithm{
    @Override
    public BMP embed(BMP bmp, Payload payload) {
        return null;
    }

    @Override
    public Payload recover(BMP bmp,boolean withExtension) {
        return null;
    }

    @Override
    public int getMaxLength(BMP bmp) {
        return 0;
    }
}
