package org.example;

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
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "margarita";
        byte[] salt = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; // Salt de 8 bytes
        int iterations = 10000;
        int keyLength = 256 + 16*8; // longitud total en bits (para 64 caracteres en Base64)

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        // Dividir el hash en key y iv
        byte[] keyBytes = Arrays.copyOf(hash, 32); // 32 bytes para AES-256
        byte[] iv = Arrays.copyOfRange(hash, 32, 48); // 16 bytes para IV

        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec IV = new IvParameterSpec(iv);

        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        String base64IV = Base64.getEncoder().encodeToString(IV.getIV());

        System.out.println("Clave: " + base64Key);
        System.out.println("IV: " + base64IV);
    }
}
