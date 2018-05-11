package jimmyCoin;

import com.google.gson.GsonBuilder;
import jimmyCoin.transaction.Transaction;
import jimmyCoin.transaction.TransactionOutput;
import jimmyCoin.util.CoinUtils;
import jimmyCoin.wallet.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JimmyCoin  {
    private static int difficulty = 6;
    private static Wallet walletA;
    private static Wallet walletB;

    private static List<Block> blockChain = new ArrayList<>();
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();
    public static float minimumTransaction = 0.1f;



    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        System.out.println("Private and public keys: ");

        System.out.println(CoinUtils.getStringFromKey(walletA.getPrivateKey()));
        System.out.println(CoinUtils.getStringFromKey(walletA.getPublicKey()));

        Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
        transaction.generateSignature(walletA.getPrivateKey());

        System.out.println("Is signature verified");
        System.out.println(transaction.verifySignature());

        //        Block firstBlock = new Block("Hey, I'm first", "0");
//        blockChain.add(firstBlock);
//        firstBlock.mineBlock(difficulty);
//        Block secondBlock = new Block("Hey, I'm second", getPreviousBlockHash());
//        blockChain.add(secondBlock);
//        secondBlock.mineBlock(difficulty);
//        Block thirdBlock = new Block("Hey, I'm third", getPreviousBlockHash());
//        blockChain.add(thirdBlock);
//        thirdBlock.mineBlock(difficulty);
//
//        System.out.println("Is blockchain valid: " + isChainValid());
//
//        String blockChainGson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
//        System.out.println(blockChainGson);
    }

    private static String getPreviousBlockHash() {
        return blockChain.get(blockChain.size() - 1).getHash();
    }

    public static Boolean isChainValid(){
        Block previous;
        Block current;

        for (int i = 1; i < blockChain.size(); i++) {
            current = blockChain.get(i);
            previous = blockChain.get(i - 1);
            if (isHashBroken(current)){
                System.out.println("Current block hash is broken");
                return false;
            }
            if(isPreviousHashBroken(previous, current)){
                System.out.println("Previous hash is not equals to registered previous hash!");
                return false;
            }
        }

        return true;
    }

    private static boolean isPreviousHashBroken(Block previous, Block current) {
        return previous.getHash().equals(current.getPreviousHash());
    }

    private static boolean isHashBroken(Block current) {
        return !current.getHash().equals(current.calculateHash());
    }
}
