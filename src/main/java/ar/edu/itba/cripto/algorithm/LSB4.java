package ar.edu.itba.cripto.algorithm;

import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.exceptions.InsufficientSizeException;

import java.util.Arrays;

public class LSB4 implements Algorithm{

    private static final int LSB4_BYTES = 2; //bytes needed to store a byte using LSB4
    private static final int LSB4_USED_BITS = 4;

    @Override
    public int getMaxLength(BMP bmp){
        return bmp.getData().length / LSB4_BYTES;
    }

    @Override
    public BMP embed(BMP bmp, Payload payload) {
        if(getMaxLength(bmp) < payload.getTotalLength()){
            throw new InsufficientSizeException(getMaxLength(bmp));
        }
        final byte[] ansContent = Arrays.copyOf(bmp.getData(), bmp.getData().length);
        final byte[] payloadContent = payload.getBinary();
        for(int o = 0, i=0; o<bmp.getData().length && i<payloadContent.length; o+= LSB4_BYTES, i++){
            byte info = payloadContent[i];
            for(int j = 0; j< LSB4_BYTES; j++){
                // (0011 0110 >> 0) & 0x01
                ansContent[o+j] = (byte) ((ansContent[o+j] & 0xF0) | ((info >> ((LSB4_BYTES - 1 - j)*LSB4_USED_BITS)) & 0x0F));
            }
        }
        return new BMP(bmp.getSize(),ansContent,bmp.getHeader());
    }

    @Override
    public Payload recover(BMP bmp,boolean withExtension) {
        final int maxLength = getMaxLength(bmp);
        final Payload ans = new Payload();
        if(maxLength < Integer.BYTES){
            throw new IllegalArgumentException("Can't read size from porter");
        }
        //read the length
        final byte[] sizeBinary = new byte[Integer.BYTES];
        final byte[] porterData = bmp.getData();
        int p = 0; //index used for porter
        for(int i=0; i<Integer.BYTES; p+=LSB4_BYTES, i++){
            sizeBinary[i] = (byte) (((porterData[p]&0x0F) << ((LSB4_BYTES - 1)*LSB4_USED_BITS)) |
                                    ((porterData[p+1]&0x0F) << ((LSB4_BYTES - 2)*LSB4_USED_BITS)));
        }
        ans.setSize(sizeBinary);
        final int size = ans.getSize();
        if(size > maxLength - Integer.BYTES){
            throw new IllegalArgumentException("Can't read content from porter");
        }
        final byte[] content = new byte[size];
        for(int i = 0; i<size; p+=LSB4_BYTES, i++){
            content[i] = (byte) (((porterData[p]&0x0F) << ((LSB4_BYTES - 1)*LSB4_USED_BITS)) |
                    ((porterData[p+1]&0x0F) << ((LSB4_BYTES - 2)*LSB4_USED_BITS)));
        }
        ans.setContent(content);
        String extension = null;
        if(withExtension){
            StringBuilder builder = new StringBuilder();
            byte last = 0;
            do{
                last = (byte) (((porterData[p]&0x0F) << ((LSB4_BYTES - 1)*LSB4_USED_BITS)) |
                        ((porterData[p+1]&0x0F) << ((LSB4_BYTES - 2)*LSB4_USED_BITS)));
                p+=LSB4_BYTES;
                if(last!=0){
                    builder.append((char) last);
                }
            }while (last!=0);
            extension = builder.toString();
        }
        ans.setExtension(extension);
        return ans;
    }
}
