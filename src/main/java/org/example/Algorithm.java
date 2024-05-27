package org.example;

import org.example.data.BMP;
import org.example.data.Payload;

public interface Algorithm {
    BMP embed(BMP bmp, Payload payload);

    Payload recover(BMP bmp);
}

