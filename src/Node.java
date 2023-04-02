/**
 * Node class for Huffman Tree
 */
class Node {
    /**
     * The character of the node
     */
    private String c;
    /**
     * The frequency of the character
     */
    private int frequency;
    /*
     * The left child of the node
     */
    private Node left;
    /**
     * The right child of the node
     */
    private Node right;
    /**
     * The parent of the node
     */
    private Node parent;
    /**
     * The code of the node
     */
    private String code;

    public Node(String c, int frequency) {
        this.c = c;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.code = "";
    }

    public Node(String c, int frequency, Node left, Node right) {
        this.c = c;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
        this.parent = null;
        this.code = "";
    }

    /**
     * Get the character of the node
     * 
     * @return
     */
    public String getChar() {
        return c;
    }

    /**
     * Set the character of the node
     * 
     * @param c
     */
    public void setChar(String c) {
        this.c = c;
    }

    /**
     * Get the frequency of the node
     * 
     * @return frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Set the frequency of the node
     * 
     * @param frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Get the left child of the node
     * 
     * @return left
     */
    public Node getLeft() {
        return left;
    }

    /**
     * Set the left child of the node
     * 
     * @param left
     */
    public void setLeft(Node left) {
        this.left = left;
    }

    /**
     * Get the right child of the node
     * 
     * @return right
     */
    public Node getRight() {
        return right;
    }

    /**
     * Set the right child of the node
     * 
     * @param right
     */
    public void setRight(Node right) {
        this.right = right;
    }

    /**
     * Get the parent of the node
     * 
     * @return
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Set the parent of the node
     * 
     * @param parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Get the code of the node
     * 
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the code of the node
     * 
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Check if the node is a leaf
     * 
     * @param other
     * @return
     */
    public boolean isLessThan(Node other) {
        return this.frequency < other.frequency;
    }
}