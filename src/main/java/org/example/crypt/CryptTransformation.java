package org.example.crypt;

public class CryptTransformation {
    private static final String NO_PADDING = "NoPadding";

    private final CryptAlgorithm cryptAlgorithm;
    private final CryptMode cryptMode;

    public CryptTransformation(CryptAlgorithm cryptAlgorithm, CryptMode cryptMode) {
        this.cryptAlgorithm = cryptAlgorithm;
        this.cryptMode = cryptMode;
    }

    public String getTransformation() {
        return cryptAlgorithm.getAlgorithm() + "/" + cryptMode.getMode() + "/" + NO_PADDING;
    }
}
