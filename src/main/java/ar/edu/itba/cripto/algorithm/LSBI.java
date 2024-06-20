package ar.edu.itba.cripto.algorithm;

import lombok.Getter;
import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.exceptions.InsuficientSizeException;

import java.util.Arrays;

public class LSBI implements Algorithm{

    private byte getLSB(byte b){
        return (byte) (b & 0x01);
    }
    private static final int LSBI_BYTES = 8 ;

    private static final int PATTERN_COUNT = 4;
    @Override
    public BMP embed(BMP bmp, Payload payload) {
        if(getMaxLength(bmp) < payload.getTotalLength()){
            throw new InsuficientSizeException();
        }
        final byte[] ansContent = Arrays.copyOf(bmp.getData(), bmp.getData().length);
        final byte[] payloadContent = payload.getBinary();
        final PatternChange[] patternChanges = new PatternChange[]{new PatternChange("00"),new PatternChange("01"),new PatternChange("10"), new PatternChange("11")};
        int o = 4; //Dejamos el espacio para guardar con LSB1
        //Es Blue, Green, Red, Blue, Green
        //Estamos en Green
        //Si o%3==2
        for(int i=0; o<bmp.getData().length && i<payloadContent.length;i++) {
            byte infoByte = payloadContent[i];
            for (int j = 0; j < LSBI_BYTES; o++){
                if(o%3==2){ //No se escriben Red
                    continue;
                }
                byte prev = getLSB(ansContent[o]);//Guardamos lo que tenía antes
                ansContent[o] = (byte) ((ansContent[o] & 0XFE) | ((infoByte >> (LSBI_BYTES - 1 -j)) & 0x01));
                byte curr = getLSB(ansContent[o]);
                int index = PatternChange.getIndex(ansContent[o]);
                if(prev!=curr){
                    patternChanges[index].incrementChanged();
                }
                patternChanges[index].incrementCount();
                j++;
            }
        }
        //Shift changed patterns
        o=4;
        for(int i=0; o<bmp.getData().length && i<payloadContent.length;i++) {
            for (int j = 0; j < LSBI_BYTES; o++){
                if(o%3==2){ //No se escriben Red
                    continue;
                }
                int index = PatternChange.getIndex(ansContent[o]);
                if (patternChanges[index].mustShift()){
                    ansContent[o] =(byte) (ansContent[o] ^ 0x01);
                }
                j++;

            }
        }
        for(int i = 0; i<PATTERN_COUNT; i++){
            if(patternChanges[i].mustShift()){
                ansContent[i] = (byte) (ansContent[i] | 0x01);
            }else{
                ansContent[i] = (byte) (ansContent[i] & 0xFE);
            }
        }

         return new BMP(bmp.getSize(),ansContent,bmp.getHeader());
    }

    @Override
    public Payload recover(BMP bmp,boolean withExtension) {
//        int maxLength = Integer.MAX_VALUE;//TODO
        final Payload ans = new Payload();
//        if(maxLength < PATTERN_COUNT){ //No puede almacenar los patrones
//            throw new IllegalArgumentException("Can't read patterns from porter");
//        }
        final byte[] porterData = bmp.getData();
        boolean[] changed = new boolean[PATTERN_COUNT];
        for(int i = 0; i<PATTERN_COUNT; i++){
            if(getLSB(porterData[i])==1){
                changed[i] = true;
            }else{
                changed[i] = false;
            }
        }
        int o = 4;
//        maxLength -= PATTERN_COUNT;
//        if(maxLength < Integer.BYTES){
//            throw new IllegalArgumentException("Can't read size from porter");
//        }
        //Read size
        final byte[] sizeBinary = new byte[Integer.BYTES];
        for(int i=0; i<Integer.BYTES; i++){
            for(int j = 0; j<LSBI_BYTES; o++){
                if(o%3==2){
                    continue;
                }
                if(changed[PatternChange.getIndex(porterData[o])]){
                    sizeBinary[i] = (byte) (sizeBinary[i] | (((porterData[o] & 0x01)^0x01) << (LSBI_BYTES - 1 - j)));
                }else{
                    sizeBinary[i] = (byte) (sizeBinary[i] | ((porterData[o] & 0x01) << (LSBI_BYTES - 1 - j)));
                }
                j++;
            }
        }
        ans.setSize(sizeBinary);

        //Read content
        final int size = ans.getSize();
//        maxLength -= Integer.BYTES * LSBI_BYTES;
        final byte [] content = new byte[size];
        for(int i=0; i<size; i++){
            for(int j = 0; j<LSBI_BYTES; o++){
                if(o%3==2){
                    continue;
                }
                if(changed[PatternChange.getIndex(porterData[o])]){
                     content[i] = (byte) (content[i] | (((porterData[o] & 0x01)^0x01) << (LSBI_BYTES - 1 - j)));
                }else{
                    content[i] = (byte) (content[i] | ((porterData[o] & 0x01) << (LSBI_BYTES - 1 - j)));
                }
                j++;
            }
        }
        ans.setContent(content);

        //Read extension
        String extension = null;
        if(withExtension){
            StringBuilder builder = new StringBuilder();
            byte last = 0;
            do{
                last = 0;
                for(int j=0; j < LSBI_BYTES; o++){
                    if(o%3==2){
                        continue;
                    }
                    if(changed[PatternChange.getIndex(porterData[o])]){
                        last = (byte) (last | (((porterData[o] & 0x01)^0x01) << (LSBI_BYTES - 1 - j)));
                    }else{
                        last = (byte) (last | (((porterData[o] & 0x01)) << (LSBI_BYTES - 1 - j)));
                    }
                    j++;
                }
                if(last!=0){
                    builder.append((char) last);
                }
            }while (last!=0);
            extension = builder.toString();
        }
        ans.setExtension(extension);
        return ans;
    }

    @Override
    public int getMaxLength(BMP bmp) {
        // As we only use the blue and green channels, we can store 2 bits per pixel
        // So for every 3 bytes we can only use 2 bytes, that is we can store 2 bits per 3 bytes
        // So for each payload byte we need 4 * 3 bytes
        return bmp.getData().length / (4 * 3);
    }


    @Getter
    private static class PatternChange{
        int count=0;
        int changed = 0;
        String pattern;
        public PatternChange( String pattern) {
            this.pattern = pattern;
        }

        public boolean mustShift(){
            return (changed/((double)count) ) > 0.5; //TODO: revisar si 0.5
        }

        public void incrementCount(){
            count++;
        }

        public void incrementChanged(){
            changed++;
        }

        public static int getIndex(byte b){
            return (b & 0x06)>>1;
        }
    }

}
