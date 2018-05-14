package jimmyCoin;

import jimmyCoin.transaction.Transaction;
import jimmyCoin.transaction.TransactionInput;
import jimmyCoin.transaction.TransactionOutput;
import jimmyCoin.wallet.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JimmyChain  {
    private static int difficulty = 3;
    private static Wallet walletA;
    private static Wallet walletB;

    private static List<Block> blockChain = new ArrayList<>();
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();
    public static double minimumTransaction = 0.1;
    private static Transaction genesisTransaction;



    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        Block genesis = createGenesisBlock(coinbase);


        //testing
        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();


//        System.out.println("Private and public keys: ");
//
//        System.out.println(CoinUtils.getStringFromKey(walletA.getPrivateKey()));
//        System.out.println(CoinUtils.getStringFromKey(walletA.getPublicKey()));
//
//        Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
//        transaction.generateSignature(walletA.getPrivateKey());
//
//        System.out.println("Is signature verified");
//        System.out.println(transaction.verifySignature());

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

    private static Block createGenesisBlock(Wallet coinbase) {
        createGenesisTransaction(coinbase);

        System.out.println("Creating and Mining Genesis block... ");

        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        return genesis;
    }

    private static void createGenesisTransaction(Wallet coinbase) {
        genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100, null);
        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        genesisTransaction.setId("0");
        genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(),
                genesisTransaction.getId()));
        final TransactionOutput firstOutput = genesisTransaction.getOutputs().get(0);
        UTXOs.put(firstOutput.getId(), firstOutput);
    }

    private static String getPreviousBlockHash() {
        return blockChain.get(blockChain.size() - 1).getHash();
    }

    public static Boolean isChainValid(){
        Block previous = null;
        Block current = null;

        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        Map<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
        final TransactionOutput firstOutput = genesisTransaction.getOutputs().get(0);
        tempUTXOs.put(firstOutput.getId(), firstOutput);



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
            if (stratOfHashIsNotValid(current, hashTarget)){
                System.out.println("#This block hasn't been mined");
                return false;
            }
        }

        TransactionOutput tempOutput;

        for(int t=0; t < current.getTransactions().size(); t++) {
            Transaction currentTransaction = current.getTransactions().get(t);

            if(!currentTransaction.verifySignature()) {
                System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                return false;
            }
            if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                return false;
            }

            for(TransactionInput input: currentTransaction.getInputs()) {
                tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                if(tempOutput == null) {
                    System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                    return false;
                }

                if(input.getUTXO().getValue() != tempOutput.getValue()) {
                    System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                    return false;
                }

                tempUTXOs.remove(input.getTransactionOutputId());
            }

            for(TransactionOutput output: currentTransaction.getOutputs()) {
                tempUTXOs.put(output.getId(), output);
            }

            final TransactionOutput transactionOutput = currentTransaction.getOutputs().get(0);

            if( transactionOutput.getRecipient() != currentTransaction.getRecipient()) {
                System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                return false;
            }
            final TransactionOutput secondOutput = currentTransaction.getOutputs().get(1);
            if( secondOutput.getRecipient() != currentTransaction.getRecipient()) {
                System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                return false;
            }

        }

            System.out.println("Blockchain is valid");
            return true;
    }

    private static boolean stratOfHashIsNotValid(Block current, String hashTarget) {
        return !current.getHash().substring(0, difficulty).equals(hashTarget);
    }

    private static boolean isPreviousHashBroken(Block previous, Block current) {
        return !previous.getHash().equals(current.getPreviousHash());
    }

    private static boolean isHashBroken(Block current) {
        return !current.getHash().equals(current.calculateHash());
    }

    private static void addBlock(Block block) {
        block.mineBlock(difficulty);
        blockChain.add(block);
    }
}
