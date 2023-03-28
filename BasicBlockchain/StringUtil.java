package BasicBlockchain;
import java.security.MessageDigest;
import java.security.*;
import java.util.ArrayList;
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

    public static String getMerkleRoot(ArrayList<Transaction> trans) {
        int count = trans.size();
        ArrayList<String> prevTreeLayer = new ArrayList<String>();
        for (Transaction t : trans) {
            prevTreeLayer.add(t.tId);
        }
        ArrayList<String> treeLayer = prevTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i = 1; i < prevTreeLayer.size(); i++) {
                treeLayer.add(applySha256(prevTreeLayer.get(i - 1) + prevTreeLayer.get(i)));
            }
            count = treeLayer.size();
            prevTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}
