package jimmyCoin.transaction;

import jimmyCoin.HashEncoder;
import jimmyCoin.util.CoinUtils;

import java.security.PublicKey;

public class TransactionOutput {
    private String id;
    private PublicKey recepient;
    private double value;
    private String parentTransactionId;

    public TransactionOutput(PublicKey recepient, double value, String parentTransactionId) {
        this.recepient = recepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        id = HashEncoder.encode(CoinUtils.getStringFromKey(recepient)
                + value + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey){
        return publicKey == recepient;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }
}
