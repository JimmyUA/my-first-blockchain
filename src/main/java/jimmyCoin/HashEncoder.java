package jimmyCoin;

import org.apache.commons.codec.digest.DigestUtils;

public class HashEncoder {
    public static String encode(String input){
        return DigestUtils.sha256Hex(input);
    }
}
