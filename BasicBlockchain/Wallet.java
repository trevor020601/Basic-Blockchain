package BasicBlockchain;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
    public PrivateKey priK;
    public PublicKey pubK;

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); // 256 bytes is an acceptable security level
            KeyPair kP = keyGen.generateKeyPair();

            priK = kP.getPrivate();
            pubK = kP.getPublic();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
