package org.example.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.InputStream;
import java.io.OutputStream;

public class CryptographyImpl implements Cryptography {
    private Cipher cipher;

    public CryptographyImpl(String algorithm, SecretKey key, IvParameterSpec iv) {
        this.cipher = Cipher.getInstance();
        this.cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    }


    @Override
    public void encrypt(InputStream in, OutputStream out) {

    }

    @Override
    public void decrypt(InputStream in, OutputStream out) {

    }
}
