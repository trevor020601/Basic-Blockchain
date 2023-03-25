package BasicBlockchain;
import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String tId; // hash of transaction
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature; // prevents unauthorized spending so only the owner can spend their coins 
                             // and no outside tampering with submitted transactions

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // Count for number of transactions generated

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++; // Increment sequence to avoid 2 identical transactions having the same hash

        return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + 
                                        Float.toString(value) + sequence);
    }

    // Signs the data
    public void generateSignature(PrivateKey pK) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applySigECDSA(pK, data);
    }

    // Verifies data to ensure nothing has changed
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifySigECDSA(sender, data, signature);
    }
}
