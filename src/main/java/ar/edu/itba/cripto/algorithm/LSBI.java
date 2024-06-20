package ar.edu.itba.cripto.algorithm;

import lombok.Getter;
import ar.edu.itba.cripto.data.BMP;
import ar.edu.itba.cripto.data.Payload;
import ar.edu.itba.cripto.exceptions.InsuficientSizeException;

import java.util.*;


public class LSBI implements Algorithm {

    private static final int LSBI_BYTES = 8 ;
    private static final int MASK = 0b110;
    private static final List<Integer> PATTERNS = List.of(0b000, 0b010, 0b100, 0b110);
    private static final int PATTERN_COUNT = PATTERNS.size();
    private static final int BMP_BYTES_PER_PAYLOAD_BYTE = 4 * 3;

    private byte getLSB(byte b){
        return (byte) (b & 0x01);
    }

    //Es Blue, Green, Red, Blue, Green, Red, ...
    private boolean isRedChannel(int i){
        return i % 3 == 2;
    }

    private int getPatternIndex(byte pattern){
        return (pattern & MASK) >> 1;
    }

    @Override
    public BMP embed(BMP bmp, Payload payload) {
        if(getMaxLength(bmp) < payload.getTotalLength()){
            throw new InsuficientSizeException();
        }
        final byte[] ansContent = Arrays.copyOf(bmp.getData(), bmp.getData().length);
        final byte[] payloadContent = payload.getBinary();
        final PatternChanges patternChanges = new PatternChanges(MASK, PATTERNS);
        // final PatternChange[] patternChanges = new PatternChange[]{new PatternChange("00"),new PatternChange("01"),new PatternChange("10"), new PatternChange("11")};
        int o = 4; //Dejamos el espacio para guardar con LSB1
        for(int i=0; o<bmp.getData().length && i<payloadContent.length;i++) {
            byte infoByte = payloadContent[i];
            for (int j = 0; j < LSBI_BYTES; o++){
                if(isRedChannel(o)){ //No se escriben Red
                    continue;
                }
                byte prev = getLSB(ansContent[o]);//Guardamos lo que tenía antes
                ansContent[o] = (byte) ((ansContent[o] & 0XFE) | ((infoByte >> (LSBI_BYTES - 1 -j)) & 0x01));
                byte curr = getLSB(ansContent[o]);
                PatternChange patternChange = patternChanges.getPatternChange(ansContent[o]);
                // int index = PatternChange.getIndex(ansContent[o]);
                if(prev!=curr){
                    // patternChanges[index].incrementChanged();
                    patternChange.incrementChanged();
                }
                // patternChanges[index].incrementCount();
                patternChange.incrementCount();
                j++;
            }
        }
        //Shift changed patterns
        o=4;
        for(int i=0; o<bmp.getData().length && i<payloadContent.length;i++) {
            for (int j = 0; j < LSBI_BYTES; o++){
                if(isRedChannel(o)){ //No se escriben Red
                    continue;
                }
                PatternChange patternChange = patternChanges.getPatternChange(ansContent[o]);
                // int index = PatternChange.getIndex(ansContent[o]);
                // if (patternChanges[index].mustShift()){
                if (patternChange.mustShift()){
                    ansContent[o] =(byte) (ansContent[o] ^ 0x01);
                }
                j++;

            }
        }
        for (PatternChange patternChange : patternChanges.getPatternChanges()) {
            int index = getPatternIndex(patternChange.getPattern());
            if (patternChange.mustShift()) {
                ansContent[index] = (byte) (ansContent[index] | 0x01);
            } else {
                ansContent[index] = (byte) (ansContent[index] & 0xFE);
            }
        }
//        for(int i = 0; i<PATTERN_COUNT; i++){
//            if(patternChanges[i].mustShift()){
//                ansContent[i] = (byte) (ansContent[i] | 0x01);
//            }else{
//                ansContent[i] = (byte) (ansContent[i] & 0xFE);
//            }
//        }

         return new BMP(bmp.getSize(),ansContent,bmp.getHeader());
    }

    @Override
    public Payload recover(BMP bmp,boolean withExtension) {
        final PatternChanges patternChanges = new PatternChanges(MASK, PATTERNS);
        int maxLength = getMaxLength(bmp);
        final Payload ans = new Payload();
        if(maxLength < PATTERN_COUNT){ //No puede almacenar los patrones
            throw new IllegalArgumentException("Can't read patterns from porter");
        }
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
        maxLength -= PATTERN_COUNT;
        if(maxLength < Integer.BYTES){
            throw new IllegalArgumentException("Can't read size from porter");
        }

        ByteRecoverIterator iterator = new ByteRecoverIterator(porterData, patternChanges, changed, o);
        final byte[] sizeBinary = iterator.nextNBytes(Integer.BYTES);
        //Read size
        //final byte[] sizeBinary = new byte[Integer.BYTES];
        //for(int i=0; i<Integer.BYTES; i++, o+=BMP_BYTES_PER_PAYLOAD_BYTE){
        //    sizeBinary[i] = recoverByte(porterData, o, patternChanges, changed);
        //}
        ans.setSize(sizeBinary);

        //Read content
        final int size = ans.getSize();
        maxLength -= Integer.BYTES;
        if(size > maxLength){
            throw new IllegalArgumentException("Can't read content from porter");
        }
        final byte[] content = iterator.nextNBytes(size);
        //final byte [] content = new byte[size];
        //for(int i=0; i<size; i++, o+=BMP_BYTES_PER_PAYLOAD_BYTE){
        //    content[i] = recoverByte(porterData, o, patternChanges, changed);
        //}
        ans.setContent(content);

        //Read extension
        String extension = null;
        if(withExtension){
            StringBuilder builder = new StringBuilder();
            byte last = 0;
            do{
                last = iterator.next();
                //last = recoverByte(porterData, o, patternChanges, changed);
                //o+=BMP_BYTES_PER_PAYLOAD_BYTE;
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
        return bmp.getData().length / BMP_BYTES_PER_PAYLOAD_BYTE;
    }

    private static class PatternChange {
        int count = 0;
        int changed = 0;
        @Getter
        final byte pattern;

        public PatternChange(byte pattern) {
            this.pattern = pattern;
        }

        public boolean mustShift() {
            return (changed / ((double) count)) > 0.5; //TODO: revisar si 0.5
        }

        public void incrementCount() {
            count++;
        }

        public void incrementChanged() {
            changed++;
        }
    }

    private static class PatternChanges {

        private final Map<Byte, PatternChange> patternChanges;
        private final byte mask;

        public PatternChanges(int mask, Iterable<Integer> patterns) {
            this.patternChanges = new HashMap<>();
            for (Integer pattern : patterns) {
                byte patternByte = (byte) (pattern.byteValue() & mask);
                this.patternChanges.put(patternByte, new PatternChange(patternByte));
            }
            this.mask = (byte) mask;
        }

        public PatternChange getPatternChange(byte b) {
            byte pattern = (byte) (b & mask);
            if (!patternChanges.containsKey(pattern)) {
                throw new IllegalArgumentException("Pattern not found");
            }
            return patternChanges.get(pattern);
        }

        public Iterable<PatternChange> getPatternChanges() {
            return patternChanges.values();
        }
    }

    private class ByteRecoverIterator implements Iterator<Byte> {

        private final byte[] porterData;
        private int porterIndex;
        private final PatternChanges patternChanges;
        private final boolean[] changed;

        public ByteRecoverIterator(byte[] porterData, PatternChanges patternChanges, boolean[] changed, int initialIndex) {
            this.porterData = porterData;
            this.porterIndex = initialIndex;
            this.patternChanges = patternChanges;
            this.changed = changed;
        }

        @Override
        public boolean hasNext() {
            return this.porterIndex + BMP_BYTES_PER_PAYLOAD_BYTE <= this.porterData.length;
        }

        @Override
        public Byte next() {
            if (!hasNext()) {
                throw new IllegalStateException("No more bytes to read");
            }
            byte ans = 0;
            for (int j = 0; j < LSBI_BYTES; this.porterIndex++) {
                if (isRedChannel(this.porterIndex)) {
                    continue;
                }
                int index = getPatternIndex(this.patternChanges.getPatternChange(this.porterData[this.porterIndex]).getPattern());
                byte recoveredBit = (byte) ((this.porterData[this.porterIndex] & 0x01) ^ (this.changed[index] ? 0x01 : 0x00));
                ans = (byte) (ans | (recoveredBit << (LSBI_BYTES - 1 - j)));
                j++;
            }
            return ans;
        }

        public byte[] nextNBytes(int n) {
            byte[] ans = new byte[n];
            for (int i = 0; i < n; i++) {
                ans[i] = next();
            }
            return ans;
        }
    }

//    @Getter
//    private static class PatternChange{
//        int count=0;
//        int changed = 0;
//        String pattern;
//        public PatternChange( String pattern) {
//            this.pattern = pattern;
//        }
//
//        public boolean mustShift(){
//            return (changed/((double)count) ) > 0.5; //TODO: revisar si 0.5
//        }
//
//        public void incrementCount(){
//            count++;
//        }
//
//        public void incrementChanged(){
//            changed++;
//        }
//
//        public static int getIndex(byte b){
//            return (b & 0x06)>>1;
//        }
//    }

}
