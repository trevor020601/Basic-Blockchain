package BasicBlockchain;
import java.security.MessageDigest;
import java.security.*;
import java.util.Base64;

public class StringUtil {
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for(int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Applies the ECDSA signature and returns the bytes
    public static byte[] applySigECDSA(PrivateKey pK, String input) {
        Signature dsa;
        byte[] output = new byte[0];

        try {
            // SIGNING OCCURS HEAR
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(pK);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSignature = dsa.sign();
            output = realSignature;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    public static boolean verifySigECDSA(PublicKey pubK, String data, byte[] signature) {
        try {
            // USE THE PUBLIC KEY TO VERIFY SIGNATURE
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(pubK);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
