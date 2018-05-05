package jimmyCoin;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class JimmyCoin  {
    private static int difficulty = 6;

    public static List<Block> blockChain = new ArrayList<Block>();

    public static void main(String[] args) {
        Block firstBlock = new Block("Hey, I'm first", "0");
        blockChain.add(firstBlock);
        firstBlock.mineBlock(difficulty);
        Block secondBlock = new Block("Hey, I'm second", getPreviousBlockHash());
        blockChain.add(secondBlock);
        secondBlock.mineBlock(difficulty);
        Block thirdBlock = new Block("Hey, I'm third", getPreviousBlockHash());
        blockChain.add(thirdBlock);
        thirdBlock.mineBlock(difficulty);

        System.out.println("Is blockchain valid: " + isChainValid());

        String blockChainGson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println(blockChainGson);
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
