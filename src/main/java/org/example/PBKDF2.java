package org.example;

import org.example.data.Pair;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Base64;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

public class PBKDF2 {

    // Key length is in bits and IV length is in bytes
    public static Pair<SecretKey,IvParameterSpec> generateKey(String password, byte[] salt, int iterations, int keyLength, int IVLength, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {

        int keyTemLength = keyLength + IVLength * 8; // longitud total en bits (para 64 caracteres en Base64)

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyTemLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        // Dividir el hash en key y iv
        byte[] keyBytes = Arrays.copyOf(hash, keyLength/8);
        byte[] iv = Arrays.copyOfRange(hash, keyLength/8, hash.length);

        SecretKey key = new SecretKeySpec(keyBytes, algorithm);
        IvParameterSpec IV = new IvParameterSpec(iv);

        return new Pair<>(key, IV);

    }
    private static void printBytesHex(byte[] bytes){
        for (byte b : bytes){
            System.out.print(String.format("%02x", b));
        }
        System.out.println();
}
}
