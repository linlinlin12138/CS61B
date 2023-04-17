package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V>, Iterable<K>{
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int initialSize;
    private double maxLoad;

    private int size;

    /** Constructors */
    public MyHashMap() {
        initialSize=16;
        buckets=new Collection[initialSize];
        maxLoad=0.75;
        size=0;
    }

    public MyHashMap(int initialSize) {
        this.initialSize=initialSize;
        buckets=new Collection[initialSize];
        maxLoad=0.75;
        size=0;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize=initialSize;
        buckets=new Collection[initialSize];
        this.maxLoad=maxLoad;
        size=0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    @Override
    public void clear() {
        /*for(Collection<Node> bucket : buckets) {
            bucket=null;
        }*/
        /*for(int i=0;i<size;i++){
            buckets[i]=null;
        }*/
        size=0;
        buckets=new Collection[initialSize];
    }

    @Override
    public boolean containsKey(K key) {
        int pos=Math.floorMod(key.hashCode(), initialSize);
        Collection<Node> ll=buckets[pos];
        if(ll==null){
            return false;
        }
        for(Node node:ll){
            if(node.key.equals(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int pos=Math.floorMod(key.hashCode(), initialSize);
        Collection<Node> ll=buckets[pos];
        if(ll==null){
            return null;
        }
        for(Node node:ll){
            if(node.key.equals(key)){
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private void resize(int newSize){
        Collection<Node> []newBuckets=new Collection[newSize];
        for(Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node node : bucket) {
                    int newPos = Math.floorMod(node.key.hashCode(), newSize);
                    if(newBuckets[newPos]==null){
                        newBuckets[newPos] = createBucket();
                    }
                    newBuckets[newPos].add(node);
                }
            }
        }
        initialSize=newSize;
        buckets=newBuckets;
    }


    @Override
    public void put(K key, V value) {
        if((size+1)/initialSize>maxLoad){
            resize(2*initialSize);
        }
        int pos=Math.floorMod(key.hashCode(), initialSize);
        Node newNode=createNode(key,value);
        if(buckets[pos]==null){
            buckets[pos]=createBucket();
        }
        else{
            for(Node node:buckets[pos]){
                if(node.key==key){
                    node.value=value;
                    return;
                }
            }
        }
        buckets[pos].add(newNode);
        size++;
    }

    @Override
    public Set<K> keySet() {
        Set<K> kSet=new HashSet<>();
        for(Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node node : bucket) {
                    kSet.add(node.key);
                }
            }
        }
        return kSet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
       return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K>{
        private int pos;
        public MyHashMapIterator(){
            pos=0;
        }
        public boolean hasNext(){
            return pos<size;
        }

        public K next(){
            pos+=1;
            for(Collection<Node> bucket : buckets) {
                int cur=0;
                if (bucket != null) {
                    for (Node node : bucket) {
                        cur++;
                        if(cur==pos-1){
                            return node.key;
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }


    // Your code won't compile until you do so!

}
