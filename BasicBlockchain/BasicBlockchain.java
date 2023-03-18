package BasicBlockchain;

public class BasicBlockchain {
    public static void main(String[] args) {
        Block firstBlock = new Block("First Block", "0");
        System.out.println("Hash for Block 1: " + firstBlock.hash);

        Block secondBlock = new Block("Second Block", "1");
        System.out.println("Hash for Block 2: " + secondBlock.hash);

        Block thirdBlock = new Block("Third Block", "2");
        System.out.println("Hash for Block 3: " + thirdBlock.hash);
    }
}
