package org.example.data;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

@Getter
public class BMP
{
      private final int size; //includes header size
      @Setter
      private byte[] data;
      private final byte[] header;

    public BMP (byte [] bmp){
        byte [] sizeInByte = Arrays.copyOfRange(bmp,2,6);
        this.size = java.nio.ByteBuffer.wrap(sizeInByte).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        // Estan en little endian
        byte [] offsetInByte = Arrays.copyOfRange(bmp,10,14);
        int offset = java.nio.ByteBuffer.wrap(offsetInByte).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        this.header = Arrays.copyOfRange(bmp,0, offset);
        this.data = Arrays.copyOfRange(bmp,offset,bmp.length);
    }

    public BMP (int size, byte [] data, byte[] header){
        this.size =  size;
        this.data = data;
        this.header = header;
    }

    public void writeToFile(Path path){
        try(OutputStream writer = Files.newOutputStream(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)){
            writer.write(this.header);
            writer.write(this.data);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

}
