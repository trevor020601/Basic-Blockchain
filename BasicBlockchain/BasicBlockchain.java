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
    public static int difficulty = 3;
    public static float minTrans = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        // Set up Bouncey Castle as the security provider
        // Adding blocks to the blockchain arraylist
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Creating Wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinBase = new Wallet();

        // Create genesis transaction and send 100 coins to WalletA
        genesisTransaction = new Transaction(coinBase.pubK, walletA.pubK, 100f, null);
        genesisTransaction.generateSignature(coinBase.priK); // Sign genesis transaction
        genesisTransaction.tId = "0"; // manually set transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.tId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis Block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // Testing
        Block b1 = new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is attempting to send funds (40) to WalletB...");
        b1.addTransaction(walletA.sendFunds(walletB.pubK, 40f));
        addBlock(b1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletB's balance is: " + walletB.getBalance());

        Block b2 = new Block(b1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        b2.addTransaction(walletA.sendFunds(walletB.pubK, 1000f));
        addBlock(b2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletB's balance is: " + walletB.getBalance());

        Block b3 = new Block(b2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		b3.addTransaction(walletB.sendFunds( walletA.pubK, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletB's balance is: " + walletB.getBalance());

        isChainValid();

        //System.out.println("Wallet A private key: " + StringUtil.getStringFromKey(walletA.priK));
        //System.out.println("Wallet B public key: " + StringUtil.getStringFromKey(walletA.pubK));

        //Transaction transaction = new Transaction(walletA.pubK, walletB.pubK, 5, null);
        //transaction.generateSignature(walletA.priK);

        //System.out.println("Is signature verified: " + transaction.verifySignature());

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
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); // temporary unspent transactions
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

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

            // Loop through blockchain transactions
            TransactionOutput tempOutput;
            for(int t = 0; t < currentBlock.trans.size(); t++) {
                Transaction currentT = currentBlock.trans.get(t);

                if(!currentT.verifySignature()) {
                    System.out.println("Signature on Transaction(" + t + ") is Invalid!");
                    return false;
                }

                if(currentT.getInputsValue() != currentT.getOutputsValue()) {
                    System.out.println("Inputs are not equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentT.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("Referenced input on Transaction(" + t + ") is missing!");
                        return false;
                    }

                    if(input.UTXO.amount != tempOutput.amount) {
                        System.out.println("Referenced input Transaction(" + t + ") value is invalid!");
						return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output : currentT.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if(currentT.outputs.get(0).recipient != currentT.recipient) {
                    System.out.println("Transaction(" + t + ") output recipient is not who it should be!");
					return false;
                }

                if(currentT.outputs.get(1).recipient != currentT.sender) {
                    System.out.println("Transaction(" + t + ") output 'change' is not sender!");
					return false;
                }
            }

            System.out.println("Blockchain is Valid!");
            return true;
        }

        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
