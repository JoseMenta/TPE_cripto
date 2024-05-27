package org.example.data;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;


@Getter
public class Payload {
    private int size;
    private byte[] content;
    private Optional<String> extension;

    public Payload(int size, byte[] content, Optional<String> extension) {
        this.size = size;
        this.content = content;
        this.extension = extension;
    }


    public byte[] getSizeBinary(){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(this.size);
        return buffer.array();
    }

    public byte[] getExtensionBinary(){
        
    }

    public void setSize(byte[] sizeBinary){
        ByteBuffer buffer = ByteBuffer.wrap(sizeBinary);
        this.size = buffer.getInt();
    }

    public int getTotalLength(){
        // size || content || ext || \0
        return Integer.BYTES + content.length + extension.map(String::length).orElse(0) + 1;
    }

    public byte[] getBinary(){
        //Joins the info in a byte array
        byte[] size = getSizeBinary();
        byte[] extension =
    }


}
