package BasicBlockchain;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.GsonBuilder;
import java.security.Security;

public class BasicBlockchain {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // List of all unspent transactions

    /*
     * At the harder difficulties solutions may require more than integer.MAX_VALUE, 
     * miners can then try changing the timestamp.
     * 
     * Low difficulty like 1 or 2 can be solved nearly instantly on most computers;
     * 4–6 for testing
     */
    public static int difficulty = 5;
    public static float minTrans = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {
        // Set up Bouncey Castle as the security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        System.out.println("Wallet A private key: " + StringUtil.getStringFromKey(walletA.priK));
        System.out.println("Wallet B public key: " + StringUtil.getStringFromKey(walletA.pubK));

        Transaction transaction = new Transaction(walletA.pubK, walletB.pubK, 5, null);
        transaction.generateSignature(walletA.priK);

        System.out.println("Is signature verified: " + transaction.verifySignature());

        /*blockchain.add(new Block("Block 1", "0"));
        System.out.println("Mining Block 1...");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Block 2", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Mining Block 2...");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Block 3", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Mining Block 3...");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe Block Chain: ");
        System.out.println(blockchainJson);*/
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for(int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // Compare the registered hash to the calculated hash
            if(!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current hashes not equal!");
                return false;
            }

            // Compare the previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous hashes not equal!");
                return false;
            }

            if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block has NOT been mined!");
                return false;
            }
        }

        return true;
    }
}
