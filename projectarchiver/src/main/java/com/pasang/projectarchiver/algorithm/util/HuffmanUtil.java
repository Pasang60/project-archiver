package com.pasang.projectarchiver.algorithm.util;

import java.util.*;

public class HuffmanUtil {

    public static class Result {
        public final String encodedData;
        public final String huffmanTree;
        public final int originalSize;
        public final int compressedSize;

        public Result(String encodedData, String huffmanTree, int originalSize, int compressedSize) {
            this.encodedData = encodedData;
            this.huffmanTree = huffmanTree;
            this.originalSize = originalSize;
            this.compressedSize = compressedSize;
        }
    }

    public static Result compress(String text) {
        // Step 1: Frequency map
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Step 2: Build priority queue
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (var e : freqMap.entrySet()) {
            pq.add(new Node(e.getKey(), e.getValue()));
        }

        // Step 3: Build Huffman tree
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(left.freq + right.freq, left, right);
            pq.add(parent);
        }
        Node root = pq.poll();

        // Step 4: Create codes
        Map<Character, String> huffmanCodes = new HashMap<>();
        buildCode(root, "", huffmanCodes);

        // Step 5: Encode input text
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(huffmanCodes.get(c));
        }

        // Step 6: Serialize tree
        StringBuilder treeBuilder = new StringBuilder();
        serializeTree(root, treeBuilder);

        int originalSize = text.length(); // Original size in bytes
        int compressedSize = (encoded.length() + treeBuilder.length()) / 8; // Compressed size in bytes

        // Check if compression is effective
        if (compressedSize >= originalSize) {
            return new Result(text, null, originalSize, originalSize); // Return original data
        }

        return new Result(encoded.toString(), treeBuilder.toString(), originalSize, compressedSize);
    }

    public static String decompress(String encodedData, String serializedTree) {
        // Step 1: Deserialize tree
        int[] index = {0};
        Node root = deserializeTree(serializedTree, index);

        // Step 2: Decode the encoded data
        StringBuilder decoded = new StringBuilder();
        Node current = root;
        for (char bit : encodedData.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current.left == null && current.right == null) {
                decoded.append(current.ch);
                current = root;
            }
        }

        return decoded.toString();
    }

    private static void buildCode(Node node, String code, Map<Character, String> map) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            map.put(node.ch, code);
        }
        buildCode(node.left, code + "0", map);
        buildCode(node.right, code + "1", map);
    }

    private static void serializeTree(Node node, StringBuilder builder) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            builder.append('1').append(node.ch);
        } else {
            builder.append('0');
            serializeTree(node.left, builder);
            serializeTree(node.right, builder);
        }
    }

    private static Node deserializeTree(String tree, int[] index) {
        if (index[0] >= tree.length()) return null;

        char flag = tree.charAt(index[0]++);
        if (flag == '1') {
            char ch = tree.charAt(index[0]++);
            return new Node(ch, 0);
        }

        Node left = deserializeTree(tree, index);
        Node right = deserializeTree(tree, index);
        return new Node(0, left, right);
    }

    static class Node {
        char ch;
        int freq;
        Node left, right;
        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }
        Node(int freq, Node left, Node right) {
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
    }
}
