package org.example.crypt;

public class CryptTransformation {

    private CryptAlgorithm cryptAlgorithm;
    private CryptMode cryptMode;

    public CryptTransformation(CryptAlgorithm cryptAlgorithm, CryptMode cryptMode) {
        this.cryptAlgorithm = cryptAlgorithm;
        this.cryptMode = cryptMode;
    }

    public String getTransformation() {
        return cryptAlgorithm.getAlgorithm() + "/" + cryptMode.getMode();
    }
}
