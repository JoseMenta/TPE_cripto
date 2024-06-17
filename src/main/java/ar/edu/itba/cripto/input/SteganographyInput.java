package ar.edu.itba.cripto.input;

import ar.edu.itba.cripto.algorithm.Algorithm;
import ar.edu.itba.cripto.algorithm.LSB1;
import ar.edu.itba.cripto.algorithm.LSB4;
import ar.edu.itba.cripto.algorithm.LSBI;
import lombok.Getter;

public enum SteganographyInput {
    LSB1("LSB1",new LSB1()),
    LSB4("LSB4",new LSB4()),
    LSBI("LSBI",new LSBI());

    private String name;

    @Getter
    private Algorithm algorithm;

    SteganographyInput(String name, Algorithm algorithm) {
        this.algorithm = algorithm;
        this.name = name;
    }

    public static SteganographyInput fromString(String name) {
        if(name == null || name.isEmpty()) {
            return LSB1;
        }
        return switch (name) {
            case "LSB4" -> LSB4;
            case "LSBI" -> LSBI;
            default -> LSB1;
        };
    }
}
