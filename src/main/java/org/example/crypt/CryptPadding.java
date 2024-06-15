package org.example.crypt;

public enum CryptPadding {
    NO_PADDING("NoPadding"),
    PKCS5_PADDING("PKCS5Padding");

    private final String padding;

    CryptPadding(String padding) {
        this.padding = padding;
    }

    public String getPadding() {
        return padding;
    }
}
