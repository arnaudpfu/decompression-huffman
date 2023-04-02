import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Decompressor Huffman class
 * 
 * The Huffman algorithm is a lossless data compression algorithm.
 * The idea is to assign variable-length codes to input characters,
 * lengths of the assigned codes are based on the frequencies of corresponding
 * characters.
 * The most frequent character gets the smallest code and the least frequent
 * character gets the largest code.
 */
class Decompressor {

    /**
     * The path of the file to decompress
     */
    private String filePath;
    /**
     * The path of the file containing the frequency of each character
     */
    private String freqFilePath;
    /**
     * The frequency of each character
     */
    private Map<String, Integer> alphabetFrequency;
    /**
     * The encoding table
     */
    private Map<String, String> encodingTable;

    public Decompressor(String filePath, String freqFilePath) {
        this.filePath = filePath;
        this.freqFilePath = freqFilePath;
        this.alphabetFrequency = new HashMap<String, Integer>();
        this.encodingTable = new HashMap<String, String>();
    }

    /**
     * Read the alphabet frequency from the file
     * 
     * @return alphabetFrequency
     */
    private Map<String, Integer> readAlphabetFrequency() {
        Map<String, Integer> alphabetFrequency = new HashMap<String, Integer>();
        File file = new File(this.freqFilePath);
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line;
            int i = -1;
            while ((line = br.readLine()) != null) {
                i++;
                if (i == 0)
                    continue;

                String[] parts = line.split(" ");

                if (parts.length == 2) {
                    String c = parts[0];
                    String frequency = parts[1];
                    alphabetFrequency.put(c, Integer.parseInt(frequency));
                } else if (parts.length == 3) {
                    String c = " ";
                    String frequency = parts[parts.length - 1];
                    alphabetFrequency.put(c, Integer.parseInt(frequency));
                } else {
                    System.out.println(Arrays.toString(parts));
                    System.out.println(line);
                    throw new IOException("Invalid file format");
                }
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        alphabetFrequency = new TreeMap<String, Integer>(alphabetFrequency);

        return alphabetFrequency;
    }

    /**
     * Get the two smallest nodes from a list of nodes
     * 
     * @param nodes
     * @return the two smallest nodes
     */
    private Node[] getTwoSmallestNodes(ArrayList<Node> nodes) {
        Node minNode1 = nodes.get(0);
        Node minNode2 = nodes.get(1);
        if (minNode2.getFrequency() < minNode1.getFrequency()) {
            Node temp = minNode1;
            minNode1 = minNode2;
            minNode2 = temp;
        }

        for (Node node : nodes.subList(2, nodes.size())) {
            if (node.getFrequency() < minNode1.getFrequency()) {
                minNode2 = minNode1;
                minNode1 = node;
            } else if (node.getFrequency() < minNode2.getFrequency()) {
                minNode2 = node;
            }
        }

        return new Node[] { minNode1, minNode2 };
    }

    /**
     * Merge two nodes
     * 
     * @param nodes    the list of nodes
     * @param minNode1 the first node to merge
     * @param minNode2 the second node to merge
     */
    private void mergeNodes(ArrayList<Node> nodes, Node minNode1, Node minNode2) {
        Node parent = new Node("", minNode1.getFrequency() + minNode2.getFrequency(),
                minNode1, minNode2);
        minNode1.setParent(parent);
        minNode1.setCode("0");
        minNode2.setParent(parent);
        minNode2.setCode("1");

        nodes.remove(minNode1);
        nodes.remove(minNode2);
        nodes.add(parent);
    }

    /**
     * Create the Huffman tree
     * 
     * @param alphabet_frequency the frequency of each character
     * @return the root of the Huffman tree
     */
    private Node createTree(Map<String, Integer> alphabet_frequency) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (String character : alphabet_frequency.keySet()) {
            Node node = new Node(character, alphabet_frequency.get(character));
            nodes.add(node);
        }

        while (nodes.size() > 1) {
            Collections.sort(nodes, new Comparator<Node>() {
                @Override
                public int compare(Node node1, Node node2) {
                    return node2.getFrequency() - node1.getFrequency();
                }
            });
            Node[] minNodes = this.getTwoSmallestNodes(nodes);
            this.mergeNodes(nodes, minNodes[0], minNodes[1]);
        }

        return nodes.get(0);
    }

    /**
     * Build the encoding table
     * 
     * @param node
     * @param code
     */
    private void buildEncodingTable(Node node, String code) {
        if (node.getChar() != "") {
            this.encodingTable.put((code), node.getChar());
        } else {
            this.buildEncodingTable(node.getLeft(), code + "0");
            this.buildEncodingTable(node.getRight(), code + "1");
        }
    }

    /**
     * Remove padding from the encoded (remove useless 0s)
     * 
     * @param paddedEncodedText the encoded text
     * @return encoded text without padding
     */
    public String removePadding(String paddedEncodedText) {
        String paddedInfo = paddedEncodedText.substring(0, 8);
        int extraPadding = Integer.parseInt(paddedInfo, 2);

        paddedEncodedText = paddedEncodedText.substring(8);
        String encoded_text = paddedEncodedText.substring(0, paddedEncodedText.length() - extraPadding);

        return encoded_text;
    }

    /**
     * Read content from the compressed file
     * 
     * @return the content of the compressed file
     */
    private String getFileContent() {
        String binaryCode = "";

        try {
            // Open the binary file for reading
            FileInputStream input = new FileInputStream(this.filePath);

            // Read the file byte by byte
            int value;
            StringBuilder binaryString = new StringBuilder();
            while ((value = input.read()) != -1) {
                // Convert the byte to a binary string of 0s and 1s
                String byteString = Integer.toBinaryString(value);
                // Add leading zeros if necessary to make it 8 bits long
                byteString = String.format("%8s", byteString).replace(' ', '0');
                // Add the binary string to the overall binary string
                binaryString.append(byteString);
            }
            // Close the input stream
            input.close();

            binaryCode = binaryString.toString();
        } catch (IOException e) {
            System.err.println("Error reading binary file: " + e.getMessage());
        }

        return this.removePadding(binaryCode);
    }

    /**
     * Decode the text using the encoding table
     * 
     * @param binaryCode the encoded text
     * @return the decoded text
     */
    public String decode(String binaryCode) {
        String encoded = "";
        String code = "";

        for (int i = 0; i < binaryCode.length(); i++) {
            code += binaryCode.charAt(i);
            if (this.encodingTable.containsKey(code)) {
                encoded += this.encodingTable.get(code);
                code = "";
            }
        }

        return encoded;
    }

    /**
     * Write the decompressed file
     * 
     * @param outputPath the path of the decompressed file
     * @param decoded    the decoded text
     */
    private void writeDecompressedFile(String outputPath, String decoded) {
        try {
            // Open the text file for writing
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

            writer.write(decoded.replace("\\n", "\n"));

            // Close the writer
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing text file: " + e.getMessage());
        }
    }

    /**
     * Round a double value
     * 
     * @param value  the value to round
     * @param places the number of decimal places
     * @return the rounded value
     */
    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        long pow = (long) Math.pow(10, places);
        return (double) Math.round(value * pow) / pow;
    }

    /**
     * Compute the decompression ratio
     * 
     * @param outputPath the path of the decompressed file
     * @return the decompression ratio
     */
    private double computeDecompressionRatio(String outputPath) {
        File compressedFile = new File(this.filePath);
        File decompressedFile = new File(outputPath);

        double compressedFileSize = compressedFile.length();
        double decompressedFileSize = decompressedFile.length();

        return this.round((1 - (decompressedFileSize / compressedFileSize)) * 100, 2);
    }

    /**
     * Compute the average size of a character in the compressed file
     * 
     * @return
     */
    private double computeAverageCompressedSizePerChar() {
        File compressedFile = new File(this.filePath);
        double compressedFileSize = compressedFile.length() * 8; // in bits

        double nbChars = 0;
        for (int frequency : this.alphabetFrequency.values()) {
            nbChars += frequency;
        }

        return this.round((compressedFileSize / nbChars), 2);
    }

    /**
     * Decompress the file
     */
    public void decompress() {
        this.alphabetFrequency = this.readAlphabetFrequency();
        Node tree = this.createTree(this.alphabetFrequency);

        this.buildEncodingTable(tree, "");

        String binaryCode = this.getFileContent();
        String encoded = this.decode(binaryCode);

        String outputPath = this.filePath.replace("_comp.bin", ".txt");
        this.writeDecompressedFile(outputPath, encoded);

        System.out.println("Decompression ratio: " + this.computeDecompressionRatio(outputPath) + "%");
        System.out.println(
                "Size of a character before decompression: " + this.computeAverageCompressedSizePerChar()
                        + " bits");
    }
}