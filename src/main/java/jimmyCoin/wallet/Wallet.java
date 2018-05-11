package jimmyCoin.wallet;

import jimmyCoin.JimmyChain;
import jimmyCoin.transaction.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
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
