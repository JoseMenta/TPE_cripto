package org.example.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
public class BMP
{
      private final int size;
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
        this.data = Arrays.copyOfRange(bmp,offset,size - offset);
    }

    public BMP (int size, byte [] data, byte[] header){
        this.size =  size;
        this.data = data;
        this.header = header;
    }

}
