package org.example.crypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class CryptographyImpl implements Cryptography {
    private static final int BUFFER_SIZE = 1024;

    private final CryptTransformation cryptTransformation;
    private final Key key;

    public CryptographyImpl(CryptTransformation cryptTransformation, Key key) {
        this.cryptTransformation = cryptTransformation;
        this.key = key;
    }

    private void execute(Cipher cipher, InputStream in, OutputStream out) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                out.write(output);
            }
        }
        byte[] output = cipher.doFinal();
        if (output != null) {
            out.write(output);
        }
    }

    @Override
    public void encrypt(InputStream in, OutputStream out) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, IOException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cryptTransformation.getTransformation());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        execute(cipher, in, out);
    }

    @Override
    public void decrypt(InputStream in, OutputStream out) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, IOException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cryptTransformation.getTransformation());
        cipher.init(Cipher.DECRYPT_MODE, key);
        execute(cipher, in, out);
    }
}
