package ar.edu.itba.cripto.algorithm;

import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Payload;

public interface Algorithm {
    BMP embed(BMP bmp, Payload payload);

    Payload recover(BMP bmp, boolean withExtension);

    int getMaxLength(BMP bmp);
}




