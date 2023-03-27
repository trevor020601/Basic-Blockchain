package BasicBlockchain;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey priK;
    public PublicKey pubK;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

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

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : BasicBlockchain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.ownerValidation(pubK)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.amount;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recip, float value) {
        if(getBalance() < value) {
            System.out.println("Not enough funds to send transaction. Transaction Denied!");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.amount;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value)
                break;
        }

        Transaction newTransaction = new Transaction(pubK, recip, value, inputs);
        newTransaction.generateSignature(priK);

        for (TransactionInput transactionInput : inputs) {
            UTXOs.remove(transactionInput.transactionOutputId);
        }
        
        return newTransaction;
    }
}
