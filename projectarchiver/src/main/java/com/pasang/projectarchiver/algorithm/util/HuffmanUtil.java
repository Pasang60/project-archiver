package com.pasang.projectarchiver.algorithm.util;


import java.io.*;
import java.util.*;

public class HuffmanUtil {

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

        return new Result(encoded.toString(), treeBuilder.toString(), text.length() * 8, encoded.length());
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
}

