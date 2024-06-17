package ar.edu.itba.cripto.input;

import lombok.Getter;
import ar.edu.itba.cripto.crypt.CryptMode;

import java.util.Arrays;


public enum BlockInput {
    ECB("ecb",CryptMode.ECB),
    CFB("cfb",CryptMode.CFB),
    OFB("ofb",CryptMode.OFB),
    CBC("cbc",CryptMode.CBC);

    private final String inputName;

    @Getter
    private final CryptMode cryptMode;

    BlockInput(String inputName, CryptMode cryptMode) {
        this.inputName = inputName;
        this.cryptMode = cryptMode;
    }

    public static BlockInput fromString(String inputName) {
        return Arrays.stream(BlockInput.values())
                .filter(blockInput -> blockInput.inputName.equalsIgnoreCase(inputName))
                .findFirst()
                .orElse(CBC);
    }


}
