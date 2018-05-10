package jimmyCoin.wallet;

import jimmyCoin.JimmyChain;
import jimmyCoin.transaction.Transaction;
import jimmyCoin.transaction.TransactionInput;
import jimmyCoin.transaction.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private Map<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, secureRandom);
            KeyPair keyPair = keyGen.generateKeyPair();

            setPublicAndPrivateKeys(keyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getBalance(){
        double total = 0;
        for (Map.Entry<String, TransactionOutput> entry : JimmyChain.UTXOs.entrySet()
             ) {
            TransactionOutput UTXO = entry.getValue();
            if(UTXO.isMine(publicKey)){
                UTXOs.put(UTXO.getId(), UTXO);
            }
            total += UTXO.getValue();
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recirient, double value){
        if(notEnoughFunds(value)){
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        List<TransactionInput> inputs = initInputsList(value);
        removeInpunsFromUTXos(inputs);
        Transaction newTransaction = new Transaction(publicKey, recirient, value, inputs);

        return newTransaction;
    }

    private void removeInpunsFromUTXos(List<TransactionInput> inputs) {
        for (TransactionInput input : inputs
             ) {
            UTXOs.remove(input.getTransactionOutputId());
        }
    }

    private List<TransactionInput> initInputsList(Double value){
        List<TransactionInput> inputs = new ArrayList<>();
        double total = 0;
        for (Map.Entry<String, TransactionOutput> entry : UTXOs.entrySet()
                ) {
            TransactionOutput UTXO = entry.getValue();
            total += UTXO.getValue();
            inputs.add(new TransactionInput(UTXO.getId()));
            if (enoughFundsToCoverValue(value, total)){
                break;
            }
        }
        return inputs;
    }

    private boolean enoughFundsToCoverValue(Double value, double total) {
        return total > value;
    }

    private boolean notEnoughFunds(double value) {
        return getBalance() < value;
    }

    private void setPublicAndPrivateKeys(KeyPair keyPair) {
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
