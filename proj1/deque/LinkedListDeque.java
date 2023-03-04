package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private StuffNode sentinel;
    private int size;

    private class StuffNode {
        private StuffNode prev;
        private T item;
        private StuffNode next;

        StuffNode(StuffNode p, T i, StuffNode n) {
            prev = p;
            item = i;
            next = n;
        }

        StuffNode(StuffNode p, StuffNode n) {
            prev = p;
            next = n;
        }
    }

    //Creates an empty deque
    public LinkedListDeque() {
        sentinel = new StuffNode(null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        StuffNode second = sentinel.next;
        StuffNode first = new StuffNode(sentinel, item, second);
        second.prev = first;
        sentinel.next = first;
        size++;
    }

    @Override
    public void addLast(T item) {
        StuffNode prevlast = sentinel.prev;
        StuffNode last = new StuffNode(prevlast, item, sentinel);
        prevlast.next = last;
        sentinel.prev = last;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (StuffNode s = sentinel.next; s != sentinel && s != null; s = s.next) {
            System.out.print(s.item + " ");
        }
        System.out.println("");

    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T removed = sentinel.next.item;
        StuffNode first = sentinel.next.next;
        sentinel.next = first;
        first.prev = sentinel;
        size--;
        return removed;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T removed = sentinel.prev.item;
        StuffNode last = sentinel.prev.prev;
        sentinel.prev = last;
        last.next = sentinel;
        size--;
        return removed;
    }


    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int position = 0;
        StuffNode cur = sentinel.next;
        while (index > position) {
            cur = cur.next;
            position++;
        }
        return cur.item;
    }

    public T getRecursive(int index) {
        return this.recursiveHelper(index, sentinel.next);
    }

    private T recursiveHelper(int index, StuffNode cur) {
        if (index < 0 || cur == sentinel) {
            return null;
        }
        if (index == 0) {
            return cur.item;
        }
        return recursiveHelper(index - 1, cur.next);
    }


    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos;

        LinkedListDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos++;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Deque)) {
            return false;
        }
        Deque<T> otherDeque = (Deque<T>) obj;

        if (this.size() != otherDeque.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (otherDeque.get(i) == null) {
                return false;
            }
            if (!this.get(i).equals(otherDeque.get(i))) {
                return false;
            }
        }
        return true;
    }
}

