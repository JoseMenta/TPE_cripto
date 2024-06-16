package org.example.data;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;


@Getter
public class Payload {
    private int size;

    @Setter
    private byte[] content;
    private Optional<String> extension;

    private static Optional<String> getExtension(Path path){
        final String name = path.getFileName().toString();
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1 || dotIndex == name.length() - 1) {
            return Optional.empty();
        }
        return Optional.of("."+name.substring(dotIndex + 1));
    }

    public static Payload of(Path path) throws IOException {
        final byte[] payloadData = Files.readAllBytes(path);
        return new Payload(payloadData.length,payloadData,getExtension(path));
    }

    public void writeToFile(String filename){
        if(extension.isEmpty()){
            throw new IllegalStateException();
        }
        try(OutputStream writer = Files.newOutputStream(
                Path.of(filename+extension.get()),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)){
            writer.write(this.content);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    public Payload() {
        extension = Optional.empty();
    }

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
        return this.extension.map(e ->
                Arrays.copyOf(e.getBytes(StandardCharsets.US_ASCII),e.length()+1) //add 0 byte
        ).orElse(new byte[0]);
    }

    public void setSize(byte[] sizeBinary){
        ByteBuffer buffer = ByteBuffer.wrap(sizeBinary);
        this.size = buffer.getInt();
    }

    public void setExtension(String extension) {
        this.extension = Optional.ofNullable(extension);
    }

    public int getTotalLength(){
        // size || content || ext || \0
        return Integer.BYTES + content.length + extension.map(String::length).orElse(0) + 1;
    }

    public byte[] getBinary(){
        //Joins the info in a byte array
        byte[] size = getSizeBinary();
        byte[] extension = getExtensionBinary();
        byte[] ans = new byte[size.length + this.content.length + extension.length];
        int curr = 0;
        System.arraycopy(size,0,ans,0,size.length);
        curr += size.length;
        System.arraycopy(this.content,0,ans,curr,this.content.length);
        curr += this.content.length;
        System.arraycopy(extension,0,ans,curr,extension.length);
        return ans;
    }


}
