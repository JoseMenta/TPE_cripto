package ar.edu.itba.cripto.input;

import lombok.Getter;
import ar.edu.itba.cripto.crypt.CryptAlgorithm;
@Getter
public enum CipherInput {
    AES_128("aes128",128,16,CryptAlgorithm.AES),
    AES_192("aes192",192,16,CryptAlgorithm.AES),
    AES_256("aes256",256,16,CryptAlgorithm.AES),
    DES("des",192,8,CryptAlgorithm.TRIPLE_DES); //TODO: revisar length

    private final String inputName;
    private final int keyLength; //in bits
    private final int ivLength; //in bytes
    private final CryptAlgorithm cryptAlgorithm;

    CipherInput(String inputName, int keyLength, int ivLength, CryptAlgorithm cryptAlgorithm) {
        this.inputName = inputName;
        this.keyLength = keyLength;
        this.ivLength = ivLength;
        this.cryptAlgorithm = cryptAlgorithm;
    }

    public static CipherInput fromString(String input) {
        if(input == null || input.isEmpty()) {
            return CipherInput.AES_128;
        }
        return switch (input.toLowerCase()){
            case "aes192" -> CipherInput.AES_192;
            case "aes256" -> CipherInput.AES_256;
            case "des" -> CipherInput.DES;
            default -> CipherInput.AES_128;
        };
    }
}
