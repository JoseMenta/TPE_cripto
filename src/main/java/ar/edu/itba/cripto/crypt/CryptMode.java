package ar.edu.itba.cripto.crypt;

public enum CryptMode {
    ECB("ECB"),
    CBC("CBC"),
    CFB("CFB"),
    OFB("OFB");

    private final String mode;

    CryptMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
