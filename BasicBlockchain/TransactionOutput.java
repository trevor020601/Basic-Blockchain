package BasicBlockchain;

import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey recipient; // owner
    public float amount;
    public String parentTransactionId; // id of transaction the output was created in
    
    public TransactionOutput(PublicKey recipient, float amount, String parentTransactionId) {
        this.recipient = recipient;
        this.amount = amount;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + 
                                            Float.toString(amount) + parentTransactionId);
    }

    public boolean ownerValidation(PublicKey pubK) {
        return (pubK == recipient);
    }
}
