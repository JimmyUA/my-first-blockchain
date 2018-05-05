package jimmyCoin;

import java.util.Date;

public class Block {

    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String previousHash, String data) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        return HashEncoder.encode(
                previousHash + timeStamp + nonce + data
        );
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void mineBlock(int dificulty){
        String target = new String(new char[dificulty]).replace('\0', '0');
        while (isBlockValid(dificulty, target)){
            nonce++;
            hash = calculateHash();
        }

        System.out.println("Block mined " + hash);
    }

    private boolean isBlockValid(int difficulty, String target) {
        return !hash.substring(0, difficulty).equals(target);
    }
}
