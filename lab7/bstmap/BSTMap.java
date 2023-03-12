package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;
    private int size;

    private class BSTNode {
        private K key;
        private V val;
        private BSTNode left, right;

        public BSTNode(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }

    public BSTMap() {
        size = 0;
    }

    private void clear(BSTNode n) {
        if (n == null) {
            return;
        }
        if (n.left == null && n.right == null) {
            n = null;
            return;
        }
        clear(n.left);
        n.left = null;
        clear(n.right);
        n.right = null;
    }


    @Override
    public void clear() {
        clear(root);
        root = null;
        size = 0;
    }

    private boolean containsKey(K key, BSTNode node) {
        if (node == null) {
            return false;
        }
        int cmp = node.key.compareTo(key);
        if (cmp == 0) {
            return true;
        }
        if (cmp < 0) {
            return containsKey(key, node.right);
        } else {
            return containsKey(key, node.left);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(key, root);
    }

    private V get(K key, BSTNode node) {
        if (node == null) {
            return null;
        }
        int cmp = node.key.compareTo(key);
        if (cmp == 0) {
            return node.val;
        }
        if (cmp < 0) {
            return get(key, node.right);
        } else {
            return get(key, node.left);
        }
    }

    @Override
    public V get(K key) {
        return get(key, root);
    }

    @Override
    public int size() {
        if (root == null) {
            return 0;
        }
        return size;
    }

    private BSTNode put(K key, V value, BSTNode n) {
        if (n == null) {
            size += 1;
            return new BSTNode(key, value);
        }
        int cmp = n.key.compareTo(key);
        if (cmp < 0) {
            n.right = put(key, value, n.right);
        } else {
            n.left = put(key, value, n.left);
        }
        return n;
    }

    @Override
    public void put(K key, V value) {
        root = put(key, value, root);
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }


    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    private void printInorder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInorder(node.left);
        System.out.println(node.key + ":" + node.val);
        printInorder(node.right);
    }

    public void printInorder() {
        printInorder(root);
    }
}
