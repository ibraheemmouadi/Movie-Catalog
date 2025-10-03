package Entities;

import java.util.ArrayList;

public class AVLTree<T extends Comparable<T>> {

    public class Node {
        T data;
        Node left, right;
        int height;

        Node(T data) {
            this.data = data;
            left = null;
            right = null;
            height = 1;
        }
    }

    private Node root;

    public Node find(T data) {
        return find(root, data);
    }

    private Node find(Node node, T data) {
        if (node == null || data.equals(node.data)) {
            return node;
        }

        if (data.compareTo(node.data) < 0) {
            return find(node.left, data);
        } else {
            return find(node.right, data);
        }
    }

    public int height() throws NullPointerException {
        return height(root);
    }

    public void insert(T data) {
        root = insert(root, data);
    }

    private Node insert(Node node, T data) {
        if (node == null)
            return new Node(data);

        if (data.compareTo(node.data) < 0) {
            node.left = insert(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = insert(node.right, data);
        } else {
            return node;
        }

        updateHeight(node);
        return reBalance(node);
    }

    public void delete(T data) {
        root = delete(root, data);
    }

    private Node delete(Node node, T data) {
        if (node == null)
            return null;

        if (data.compareTo(node.data) < 0) {
            node.left = delete(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = delete(node.right, data);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                Node minNode = getMinValueNode(node.right);
                node.data = minNode.data;
                node.right = delete(node.right, minNode.data);
            }
        }

        if (node == null)
            return null;

        updateHeight(node);
        return reBalance(node);
    }

    private Node getMinValueNode(Node node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    private Node reBalance(Node node) {
        int balance = getBalanceFactor(node);

        if (balance > 1) {
            if (getBalanceFactor(node.left) < 0)
                node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1) {
            if (getBalanceFactor(node.right) > 0)
                node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int getBalanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    public void printInOrder() {
        inOrder(root);
    }

    private void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.print(node.data + " ");
            inOrder(node.right);
        }
    }

    public void printPreOrder() {
        preOrder(root);
    }

    private void preOrder(Node node) {
        if (node != null) {
            System.out.print(node.data + " ");
            preOrder(node.left);
            preOrder(node.right);
        }
    }

    public void printPostOrder() {
        postOrder(root);
    }

    private void postOrder(Node node) {
        if (node != null) {
            postOrder(node.left);
            postOrder(node.right);
            System.out.print(node.data + " ");
        }
    }

    private void toList(Node node, ArrayList<T> list) {
        if (node != null) {
            toList(node.left, list);
            list.add(node.data);
            toList(node.right, list);
        }

    }

    public ArrayList<T> toList() {
        ArrayList<T> list = new ArrayList<>();
        toList(root, list);
        return list;
    }

}
