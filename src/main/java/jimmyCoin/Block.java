package jimmyCoin;

import jimmyCoin.transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

    private String hash;
    private String previousHash;
    private String merkleRoot;
    private List<Transaction> transactions = new ArrayList<>();
    private long timeStamp;
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        return HashEncoder.encode(
                previousHash + timeStamp + nonce + merkleRoot
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

    public boolean addTransaction(Transaction transaction){
        if (transaction == null){
            return false;
        }
        if (previousHash != "0"){
            if (transaction.processTransaction() != true){
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
