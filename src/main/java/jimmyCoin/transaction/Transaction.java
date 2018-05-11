package jimmyCoin.transaction;

import jimmyCoin.HashEncoder;
import jimmyCoin.JimmyChain;
import jimmyCoin.util.CoinUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private String transactionId;
    private PublicKey sender;
    private PublicKey recipient;
    private double value;
    private byte[] signature;

    private List<TransactionInput> inputs = new ArrayList<>();
    private List<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, double value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction(){
        if (!verifySignature()){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        gatherTransactionInputs();

        double inputsValue = getInputsValue();
        if (notEnoughInputs(inputsValue)){
            System.out.println("#Transaction Inputs to small: " + inputsValue);
            return false;
        }

        generateTransactionOutputs(inputsValue);
        addOutputsToChain();
        removeInputFromChain();
        return true;
    }

    private double getInputsValue() {
        double total = 0;
        for (TransactionInput input : inputs
             ) {
            if (transactionCantBeFound(input)){
                continue;
            }
            total += input.getUTXO().getValue();
        }
        return total;
    }

    private double getOutputsValue() {
        double total = 0;
        for (TransactionOutput output : outputs
                ) {
            total += output.getValue();
        }
        return total;
    }

    private void removeInputFromChain() {
        for (TransactionInput input : inputs
             ) {
            if (transactionCantBeFound(input)){
                continue;
            }
            JimmyChain.UTXOs.remove(input.getUTXO().getId());
        }
    }

    private boolean transactionCantBeFound(TransactionInput input) {
        return input.getUTXO() == null;
    }

    private void addOutputsToChain() {
        for (TransactionOutput output : outputs
             ) {
            JimmyChain.UTXOs.put(output.getId(), output);
        }
    }

    private void generateTransactionOutputs(double inputsValue) {
        double leftOver = inputsValue - value;
        transactionId = calculateId();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
    }

    private boolean notEnoughInputs(double inputsValue) {
        return inputsValue < JimmyChain.minimumTransaction;
    }

    private void gatherTransactionInputs() {
        for (TransactionInput transactionInput : inputs
             ) {
            transactionInput.setUTXO(JimmyChain.UTXOs.get(transactionInput.getTransactionOutputId()));
        }
    }

    public String calculateId(){
        sequence++;
        return HashEncoder.encode(
                getData()
        );
    }

    public void generateSignature(PrivateKey privateKey){
        String data = getData();
        signature = CoinUtils.applyECDSASig(privateKey,data);
    }

    private String getData() {
        return CoinUtils.getStringFromKey(sender) +  CoinUtils.getStringFromKey(recipient)
                + value;
    }

    public boolean verifySignature(){
        String data = getData();
        return CoinUtils.verifyECDSASig(sender, data, signature);
    }

}
