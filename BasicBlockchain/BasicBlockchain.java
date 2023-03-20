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
}
