package org.example.crypt;

import java.io.InputStream;
import java.io.OutputStream;

public interface Cryptography {
    void encrypt(InputStream in, OutputStream out);

    void decrypt(InputStream in, OutputStream out);
}

