package ar.edu.itba.cripto;

import ar.edu.itba.cripto.algorithm.Algorithm;
import ar.edu.itba.cripto.crypt.CryptTransformation;
import ar.edu.itba.cripto.crypt.Cryptography;
import ar.edu.itba.cripto.crypt.CryptographyImpl;
import ar.edu.itba.cripto.crypt.PBKDF2;
import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Pair;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.exceptions.InsufficientSizeException;
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

        final String porter = Optional.ofNullable(System.getProperty(PORTER_FILE)).orElseThrow(() -> new IllegalArgumentException("Porter file is missing"));
        final String output = Optional.ofNullable(System.getProperty(OUTPUT_FILE)).orElseThrow(() -> new IllegalArgumentException("Output file is missing"));
        final Optional<String> password = Optional.ofNullable(System.getProperty(PASSWORD_FLAG));
        final Algorithm algorithm = Optional.ofNullable(System.getProperty(STEG_ALGORITHM))
                .map(SteganographyInput::fromString)
                .map(SteganographyInput::getAlgorithm)
                .orElseThrow(() -> new IllegalArgumentException("Steg algorithm is missing"));

        byte[] porterData = Files.readAllBytes(Path.of(porter));
        final BMP porterBmp = new BMP(porterData);

        if (System.getProperty(EMBED_FLAG) != null) {
            final Path inputPath = Optional.ofNullable(System.getProperty(INPUT_FILE))
                    .map(Path::of)
                    .orElseThrow(() -> new IllegalArgumentException("Input file is missing"));
            //Si tiene que encriptar, cambia generar el payload
            Payload payload;
            if(password.isPresent()){
                //Encriptar
                final BlockInput blockInput = BlockInput.fromString(System.getProperty(BLOCK_FLAG));
                final CipherInput cipherInput = CipherInput.fromString(System.getProperty(CIPHER_FLAG));
                final CryptTransformation cryptTransformation = new CryptTransformation(cipherInput.getCryptAlgorithm(), blockInput.getCryptMode());
                final Payload auxPayload = Payload.of(inputPath); //To get size || data || extenion
                Pair<SecretKey, IvParameterSpec> keyAndIV = PBKDF2.generateKey(password.get(),Util.SALT,Util.ITERATIONS,cipherInput.getKeyLength(), cipherInput.getIvLength(), cipherInput.getCryptAlgorithm().getAlgorithm());
                Cryptography cryptography = new CryptographyImpl(cryptTransformation, keyAndIV.getFirst(), keyAndIV.getSecond());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = new ByteArrayInputStream(auxPayload.getBinary());
                cryptography.encrypt(inputStream,outputStream);
                byte[] encryptedData = outputStream.toByteArray();
                payload = new Payload(encryptedData.length,encryptedData, Optional.empty());
            }else {
                payload = Payload.of(inputPath);
            }
            try{
                final BMP outBmp = algorithm.embed(porterBmp, payload);
                outBmp.writeToFile(Path.of(output));
            }catch (InsufficientSizeException e){
                System.err.println(e.getMessage());
                throw e;
            }
        }else if (System.getProperty(EXTRACT_FLAG) != null) {
            Payload outPayload = algorithm.recover(porterBmp, password.isEmpty());//extension is needed if password==null, because it is not encripted
            //Si tiene que desencriptar, lo tiene que hacer sobre el payload recuperado
            if(password.isPresent()){
                //Desencriptar
                final BlockInput blockInput = BlockInput.fromString(System.getProperty(BLOCK_FLAG));
                final CipherInput cipherInput = CipherInput.fromString(System.getProperty(CIPHER_FLAG));
                final CryptTransformation cryptTransformation = new CryptTransformation(cipherInput.getCryptAlgorithm(), blockInput.getCryptMode());
                Pair<SecretKey, IvParameterSpec> keyAndIV = PBKDF2.generateKey(password.get(),Util.SALT,Util.ITERATIONS,cipherInput.getKeyLength(), cipherInput.getIvLength(), cipherInput.getCryptAlgorithm().getAlgorithm());
                Cryptography cryptography = new CryptographyImpl(cryptTransformation, keyAndIV.getFirst(), keyAndIV.getSecond());
                InputStream inputStream = new ByteArrayInputStream(outPayload.getContent());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                cryptography.decrypt(inputStream,outputStream);
                outPayload = Payload.fromBytes(outputStream.toByteArray());
            }
            outPayload.writeToFile(output);
        } else {
            throw new IllegalArgumentException("Flag is missing");
        }
    }
}