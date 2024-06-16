package ar.edu.itba.cripto.input;

import lombok.Getter;
import ar.edu.itba.cripto.crypt.CryptMode;

public enum BlockInput {
    ECB("ecb",CryptMode.ECB),
    CFB("cfb",CryptMode.CFB),
    OFB("ofb",CryptMode.OFB),
    CBC("cbc",CryptMode.CBC);

    private String inputName;

    @Getter
    private CryptMode cryptMode;

    BlockInput(String inputName, CryptMode cryptMode) {
        this.inputName = inputName;
        this.cryptMode = cryptMode;
    }

    public static BlockInput fromString(String inputName) {
        if (inputName == null || inputName.isEmpty()) {
            return CBC;
        }
        return switch (inputName.toLowerCase()) {
            case "ecb" -> ECB;
            case "cfb" -> CFB;
            case "ofb" -> OFB;
            default -> CBC;
        };
    }


}
