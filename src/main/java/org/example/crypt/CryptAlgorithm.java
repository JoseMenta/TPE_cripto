package org.example.crypt;

public enum CryptAlgorithm {
    AES("AES"),
    TRIPLE_DES("DESede");

    private final String algorithm;

    CryptAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
