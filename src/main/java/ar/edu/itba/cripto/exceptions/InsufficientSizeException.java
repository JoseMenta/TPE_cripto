package ar.edu.itba.cripto.exceptions;

public class InsufficientSizeException extends RuntimeException{

    public InsufficientSizeException(int maxSize) {
        super(String.format("The file could not be embedded in the porter. The maximum capacity admitted is %d",maxSize));
    }
}
