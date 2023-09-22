package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        sortedCharFreqList = new ArrayList<CharFreq>();
        double[] ch = new double[128];
        double count = 0;

        while (StdIn.hasNextChar()){
            char c = StdIn.readChar();
            ch[c] += 1;
            count++;
        }
        //System.out.println(Arrays.toString(ch));
        //System.out.println("num of chars: " + count);

        // traverse through ch array, if a character appears at least once, get the character. find the freq. create charFreq object and add it to the sortedList

        int i = 0;
        while (i < 128){
            if (ch[i] >= 1){
                char x = (char) i;
                double numOfOccurences = ch[i];
                double freq = numOfOccurences / count;
                CharFreq f = new CharFreq(x, freq);
                sortedCharFreqList.add(f);
            }
            i++;
        }
        if (sortedCharFreqList.size() == 1){
            CharFreq onlyCharacter = sortedCharFreqList.get(0);
            int charPos = onlyCharacter.getCharacter();
            double probOcc = 0.0;
            int newCharPos;
            if (charPos != 127){
                newCharPos = charPos + 1;
            }
            //System.out.println("character pos: " + c);
            else{
                newCharPos = 0;
            }
            char newChar = (char) newCharPos;
            CharFreq newCharFreq = new CharFreq(newChar, probOcc);
            sortedCharFreqList.add(newCharFreq);
        }
        Collections.sort(sortedCharFreqList);


	/* Your code goes here */
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
        
        Queue<TreeNode> Source = new Queue<>();
        Queue<TreeNode> Target = new Queue<>();

        for (int i = 0; i < sortedCharFreqList.size(); i++){
            TreeNode newNode = new TreeNode();
            newNode.setData(sortedCharFreqList.get(i));
            Source.enqueue(newNode);
        }
        while (!Source.isEmpty() || Target.size() != 1){
            TreeNode firstNode = new TreeNode();
            TreeNode secondNode = new TreeNode();
            if (Target.size() == 0){
                firstNode = Source.dequeue();
            }
            else if (Source.size() == 0){
                firstNode = Target.dequeue();

            }
            else{
                TreeNode firstSourceNode = Source.peek();
                TreeNode firstTargetNode = Target.peek();
                double firstFreq = firstSourceNode.getData().getProbOcc();
                double secondFreq = firstTargetNode.getData().getProbOcc();
                if ((firstFreq == secondFreq) || (firstFreq < secondFreq)){
                    firstNode = Source.dequeue();
                }
                else if (secondFreq < firstFreq){
                    firstNode = Target.dequeue();
                }
            }
            if (Target.size() == 0){
                secondNode = Source.dequeue();
            }
            else if (Source.size() == 0){
                secondNode = Target.dequeue();
            }
            else{
                TreeNode firstSourceNode = Source.peek();
                TreeNode firstTargetNode = Target.peek();
                double firstFreq = firstSourceNode.getData().getProbOcc();
                double secondFreq = firstTargetNode.getData().getProbOcc();
                if ((firstFreq == secondFreq) || (firstFreq < secondFreq)){
                    secondNode = Source.dequeue();
                }
                else if (secondFreq < firstFreq){
                    secondNode = Target.dequeue();
                }
        }
            double freqSum = firstNode.getData().getProbOcc() + secondNode.getData().getProbOcc();
            CharFreq newNodeData = new CharFreq(null, freqSum);
            TreeNode newNode = new TreeNode(newNodeData, firstNode, secondNode);
            Target.enqueue(newNode);
    }
    huffmanRoot = Target.dequeue();
}

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */

    private void recursiveMethod(String codes, Character c, TreeNode t){
        if (t == null || (int) c == 0){
            return;
        }
        recursiveMethod(codes + "0", c, t.getLeft());
        if (t.getData().getCharacter() != null && (int) c == (int) t.getData().getCharacter()){
            encodings[c] = codes;
            return;
        }
        recursiveMethod(codes + "1", c, t.getRight());
    }
    public void makeEncodings() {
        encodings = new String[128];
        int i = 0;
        int size = sortedCharFreqList.size();
        String s = "";
        while (i < size){
            recursiveMethod(s, sortedCharFreqList.get(i).getCharacter(), huffmanRoot);
            i++;
        }
    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String s = "";
        while (StdIn.hasNextChar()){
            s += encodings[StdIn.readChar()];
        }
        writeBitString(encodedFile, s);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
        String code1 = readBitString(encodedFile);
        int length = code1.length();
        TreeNode root = huffmanRoot;
        int i = 0;
        while (i < length){
            char c = code1.charAt(i);
            if (c == '1'){
                root = root.getRight();
            }
            if (c == '0'){
                root = root.getLeft();
            }
            if (root.getRight() == null && root.getLeft() == null){
                StdOut.print(root.getData().getCharacter());
                root = huffmanRoot;
            }
        }
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}