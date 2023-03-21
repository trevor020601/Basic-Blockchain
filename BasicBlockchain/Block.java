package BasicBlockchain;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    private String data; // simple message
    private long timeStamp; // num ms since 1/1/1970
    private int iterationStart; // In reality each miner would start iterating from a random point

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + 
                                                        Integer.toString(iterationStart) + data);
        return calculatedHash;
    }

    public void mineBlock(int difficulty) {
        // Create a string with difficulty * 0
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            iterationStart++;
            hash = calculateHash();
        }
        System.out.println("Block Mined! Hash: " + hash);
    }
}