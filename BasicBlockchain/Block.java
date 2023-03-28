package BasicBlockchain;
import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    private String merkleRoot;
    public ArrayList<Transaction> trans = new ArrayList<Transaction>(); // simple messages
    private long timeStamp; // num ms since 1/1/1970
    private int iterationStart; // In reality each miner would start iterating from a random point

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + 
                                                        Integer.toString(iterationStart) + merkleRoot);
        return calculatedHash;
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(trans);
        // Create a string with difficulty * 0
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            iterationStart++;
            hash = calculateHash();
        }
        System.out.println("Block Mined! Hash: " + hash);
    }

    public boolean addTransaction(Transaction t) {
        // Process transaction and check validity; ignore if genesis block
        if(t == null) 
            return false;

        if(previousHash != "0") {
            if(t.processTransaction() != true) {
                System.out.println("Transaction falied to process. Transaction Denied!");
                return false;
            }
        }

        trans.add(t);
        System.out.println("Transaction successfully added to the block!");
        return true;
    }
}