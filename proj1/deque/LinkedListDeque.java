package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private stuffNode sentinel;
    private int size;

    private class stuffNode {
        public stuffNode prev;
        public T item;
        public stuffNode next;

        public stuffNode(stuffNode p, T i, stuffNode n) {
            prev = p;
            item = i;
            next = n;
        }

        public stuffNode(stuffNode p, stuffNode n) {
            prev = p;
            next = n;
        }
    }

    //Creates an empty deque
    public LinkedListDeque() {
        sentinel = new stuffNode(null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        stuffNode second = sentinel.next;
        stuffNode first = new stuffNode(sentinel, item, second);
        second.prev = first;
        sentinel.next = first;
        size++;
    }

    @Override
    public void addLast(T item) {
        stuffNode prevlast = sentinel.prev;
        stuffNode last = new stuffNode(prevlast, item, sentinel);
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
        for (stuffNode s = sentinel.next; s != sentinel && s != null; s = s.next) {
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
        stuffNode first = sentinel.next.next;
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
        stuffNode last = sentinel.prev.prev;
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
        stuffNode cur = sentinel.next;
        while (index > position) {
            cur = cur.next;
            position++;
        }
        return cur.item;
    }

    public T getRecursive(int index) {
        return this.recursiveHelper(index, sentinel.next);
    }

    private T recursiveHelper(int index, stuffNode cur) {
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

        public LinkedListDequeIterator() {
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
        if (obj instanceof LinkedListDeque otherDeque) {
            if (this.size() != otherDeque.size()) {
                return false;
            }
            for (int i = 0; i < this.size(); i++) {
                if (!this.get(i).equals(otherDeque.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


}