package jimmyCoin.util;


import jimmyCoin.HashEncoder;
import jimmyCoin.transaction.Transaction;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CoinUtils {

    public static byte[] applyECDSASig(PrivateKey privateKey, String input){
        Signature dsa;
        byte[] output = new byte[0];

        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature){
        try {
            Signature ecdsaVerifier = Signature.getInstance("ECDSA", "BC");
            ecdsaVerifier.initVerify(publicKey);
            ecdsaVerifier.update(data.getBytes());
            return ecdsaVerifier.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(List<Transaction> transactions){
        int count = transactions.size();
        List<String> previousTreeLayer = initLayerList(transactions);
        List<String> treeLayer = previousTreeLayer;
        while (count > 1){
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(HashEncoder.encode(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    private static List<String> initLayerList(List<Transaction> transactions){
        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions
                ) {
            previousTreeLayer.add(transaction.getId());
        }
        return previousTreeLayer;
    }
}
