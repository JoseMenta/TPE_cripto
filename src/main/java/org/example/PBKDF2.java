package org.example;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Struct;
import java.util.Base64;

public class PBKDF2 {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "margarita";
        byte [] salt = new byte[8]; // Salt de 8 byte
        int iterations = 100000;
        int keyLength = 256; // longitud de la clave en bits

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();


        // Codificar la clave derivada en base64 para una representación más amigable
        String base64Hash = Base64.getEncoder().encodeToString(hash);
        System.out.println("Clave derivada: " + base64Hash);
    }
}
