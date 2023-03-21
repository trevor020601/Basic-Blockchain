package BasicBlockchain;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class BasicBlockchain {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();

    public static void main(String[] args) {
        blockchain.add(new Block("Block 1", "0"));
        blockchain.add(new Block("Block 2", blockchain.get(blockchain.size() - 1).hash));
        blockchain.add(new Block("Block 3", blockchain.get(blockchain.size() - 1).hash));

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

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
        }

        return true;
    }
}
