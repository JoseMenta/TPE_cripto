package ar.edu.itba.cripto.crypt;

import lombok.Getter;

@Getter
public enum CryptAlgorithm {
    AES("AES"),
    TRIPLE_DES("DESede");

    private final String algorithm;

    CryptAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

}
