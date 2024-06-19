package ar.edu.itba.cripto.crypt;

public class CryptTransformation {

    private final CryptAlgorithm cryptAlgorithm;
    private final CryptMode cryptMode;

    public CryptTransformation(CryptAlgorithm cryptAlgorithm, CryptMode cryptMode) {
        this.cryptAlgorithm = cryptAlgorithm;
        this.cryptMode = cryptMode;
    }

    public String getTransformation() {
        return cryptAlgorithm.getAlgorithm() + "/" + cryptMode.getMode() + "/" + "PKCS5Padding"; //NoPadding
    }

    public boolean isECB(){
        return cryptMode.equals(CryptMode.ECB);
    }
}
