package ar.edu.itba.cripto.input;

import ar.edu.itba.cripto.algorithm.*;
import lombok.Getter;

import java.util.Arrays;

public enum SteganographyInput {
    LSB1("LSB1",new LSB1()),
    LSB4("LSB4",new LSB4()),
    LSBI("LSBI",new LSBI());

    private final String name;

    @Getter
    private final Algorithm algorithm;

    SteganographyInput(String name, Algorithm algorithm) {
        this.algorithm = algorithm;
        this.name = name;
    }

    public static SteganographyInput fromString(String name) {
        return Arrays.stream(SteganographyInput.values())
                .filter(i -> i.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(LSB1);
    }
}
