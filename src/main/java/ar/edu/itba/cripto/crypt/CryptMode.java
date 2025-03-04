package ar.edu.itba.cripto.crypt;

import lombok.Getter;

@Getter
public enum CryptMode {
    ECB("ECB"),
    CBC("CBC"),
    CFB("CFB8"), //Use 8 bits for CFB
    OFB("OFB");

    private final String mode;

    CryptMode(String mode) {
        this.mode = mode;
    }

}
