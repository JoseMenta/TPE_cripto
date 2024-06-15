package org.example.crypt;

public enum CryptAlgorithm {
    AES("AES"),
    TRIPLE_DES("TripleDES");

    private final String algorithm;

    CryptAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
