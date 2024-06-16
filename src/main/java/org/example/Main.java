package org.example;

//import org.example.algorithm.Algorithm;
//import org.example.algorithm.LSB1;
//import org.example.algorithm.LSB4;
//import org.example.algorithm.LSBI;
import org.example.algorithm.Algorithm;
import org.example.algorithm.LSB1;
import org.example.algorithm.LSB4;
import org.example.algorithm.LSBI;
import org.example.data.BMP;
import org.example.data.Payload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final String INPUT_FILE = "in";
    private static final String PORTER_FILE = "p";
    private static final String OUTPUT_FILE = "out";
    private static final String STEG_ALGORITHM = "steg";
    private static final String EMBED_FLAG = "embed";
    private static final String EXTRACT_FLAG = "extract";

    private static final String LSB1_ALGORITHM = "LSB1";
    private static final String LSB4_ALGORITHM = "LSB4";
    private static final String LSBI_Algorithm = "LSBI";

    private static Algorithm getStegAlgorithm(final String algorithm) {
        return switch (algorithm){
            case LSBI_Algorithm -> new LSBI();
            case LSB4_ALGORITHM -> new LSB4();
            default -> new LSB1();
        };
    }


    public static void main(String[] args) throws IOException {

        final String input = System.getProperty(INPUT_FILE);
        final String porter = System.getProperty(PORTER_FILE);
        final String output = System.getProperty(OUTPUT_FILE);
        final String algorithmString = System.getProperty(STEG_ALGORITHM);


        byte[] porterData = Files.readAllBytes(Path.of(porter));
        final BMP porterBmp = new BMP(porterData);
        if (System.getProperty(EMBED_FLAG) != null) {
            final Algorithm algorithm = getStegAlgorithm(algorithmString);
            final BMP outBmp = algorithm.embed(porterBmp, Payload.of(Path.of(input)));
            outBmp.writeToFile(Path.of(output));
        }else if (System.getProperty(EXTRACT_FLAG) != null) {
            final Algorithm algorithm = getStegAlgorithm(algorithmString);
            final Payload outPayload = algorithm.recover(porterBmp, true);
            outPayload.writeToFile(output);
        } else {
            throw new IllegalArgumentException("Flag is missing");
        }

        System.out.println("Hello world!");
    }
}