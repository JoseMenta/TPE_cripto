package ar.edu.itba.cripto;

import ar.edu.itba.cripto.algorithm.Algorithm;
import ar.edu.itba.cripto.algorithm.LSB1;
import ar.edu.itba.cripto.algorithm.LSB4;
import ar.edu.itba.cripto.algorithm.LSBI;
import ar.edu.itba.cripto.crypt.CryptTransformation;
import ar.edu.itba.cripto.crypt.Cryptography;
import ar.edu.itba.cripto.crypt.CryptographyImpl;
import ar.edu.itba.cripto.crypt.PBKDF2;
import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Pair;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.input.BlockInput;
import ar.edu.itba.cripto.input.CipherInput;
import ar.edu.itba.cripto.input.SteganographyInput;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public class Main {

    private static final String INPUT_FILE = "in";
    private static final String PORTER_FILE = "p";
    private static final String OUTPUT_FILE = "out";
    private static final String STEG_ALGORITHM = "steg";
    private static final String EMBED_FLAG = "embed";
    private static final String EXTRACT_FLAG = "extract";
    private static final String PASSWORD_FLAG = "pass";
    private static final String CIPHER_FLAG = "a";
    private static final String BLOCK_FLAG = "m";


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        final String input = System.getProperty(INPUT_FILE);
        final String porter = System.getProperty(PORTER_FILE);
        final String output = System.getProperty(OUTPUT_FILE);
        final String algorithmString = System.getProperty(STEG_ALGORITHM);
        final String password = System.getProperty(PASSWORD_FLAG);
        final CipherInput cipherInput = CipherInput.fromString(System.getProperty(CIPHER_FLAG));
        final BlockInput blockInput = BlockInput.fromString(System.getProperty(BLOCK_FLAG));

        byte[] porterData = Files.readAllBytes(Path.of(porter));
        final BMP porterBmp = new BMP(porterData);
        final SteganographyInput steganographyInput = SteganographyInput.fromString(algorithmString);
        if (System.getProperty(EMBED_FLAG) != null) {
            final Algorithm algorithm = steganographyInput.getAlgorithm();
            //Si tiene que encriptar, cambia generar el payload
            final Payload payload;
            if(password!=null){
                final Payload auxPayload = Payload.of(Path.of(input)); //To get size || data || extenion
                Pair<SecretKey, IvParameterSpec> keyAndIV = PBKDF2.generateKey(password,Util.SALT,Util.ITERATIONS,cipherInput.getKeyLength(), cipherInput.getIvLength(), cipherInput.getCryptAlgorithm().getAlgorithm());
                Cryptography cryptography = new CryptographyImpl(new CryptTransformation(cipherInput.getCryptAlgorithm(),blockInput.getCryptMode()),keyAndIV.getFirst(),keyAndIV.getSecond());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = new ByteArrayInputStream(auxPayload.getBinary());
                cryptography.encrypt(inputStream,outputStream);
                byte[] encryptedData = outputStream.toByteArray();
                payload = new Payload(encryptedData.length,encryptedData, Optional.empty());
            }else {
                payload = Payload.of(Path.of(input));
            }
            final BMP outBmp = algorithm.embed(porterBmp, payload);
            outBmp.writeToFile(Path.of(output));
        }else if (System.getProperty(EXTRACT_FLAG) != null) {
            final Algorithm algorithm = steganographyInput.getAlgorithm();
            Payload outPayload = algorithm.recover(porterBmp, password==null);//extension is needed if password==null, because it is not encripted
            //Si tiene que desencriptar, lo tiene que hacer sobre el payload recuperado
            if(password!=null){
                //Desencriptar
                Pair<SecretKey, IvParameterSpec> keyAndIV = PBKDF2.generateKey(password,Util.SALT,Util.ITERATIONS,cipherInput.getKeyLength(), cipherInput.getIvLength(), cipherInput.getCryptAlgorithm().getAlgorithm());
                Cryptography cryptography = new CryptographyImpl(new CryptTransformation(cipherInput.getCryptAlgorithm(),blockInput.getCryptMode()),keyAndIV.getFirst(),keyAndIV.getSecond());
                InputStream inputStream = new ByteArrayInputStream(outPayload.getContent());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                cryptography.decrypt(inputStream,outputStream);
                outPayload = Payload.fromBytes(outputStream.toByteArray());
            }
            outPayload.writeToFile(output);
        } else {
            throw new IllegalArgumentException("Flag is missing");
        }

        System.out.println("Hello world!");
    }
}