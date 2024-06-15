package org.example.crypt;

public class CryptTransformation {

    private final CryptAlgorithm cryptAlgorithm;
    private final CryptMode cryptMode;
    private final CryptPadding cryptPadding;

    public CryptTransformation(CryptAlgorithm cryptAlgorithm, CryptMode cryptMode, CryptPadding cryptPadding) {
        this.cryptAlgorithm = cryptAlgorithm;
        this.cryptMode = cryptMode;
        this.cryptPadding = cryptPadding;
    }

    public String getTransformation() {
        return cryptAlgorithm.getAlgorithm() + "/" + cryptMode.getMode() + "/" + cryptPadding.getPadding();
    }
}
