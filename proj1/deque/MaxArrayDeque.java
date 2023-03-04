package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFront;
    private int nextBack;
    private Comparator comp;

    public MaxArrayDeque(Comparator<T> c) {
        items = (T[]) new Object[8];
        size = 0;
        nextFront = 4;
        nextBack = 5;
        comp = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        int j = getFront(nextFront);
        int total = 1;
        T max = items[j];
        for (int i = j + 1; i < items.length; i++) {
            if (comp.compare(items[i], max) > 0) {
                max = items[i];
            }
            total++;
        }
        for (int i = 0; total < this.size(); i++) {
            if (comp.compare(items[i], max) > 0) {
                max = items[i];
            }
            total++;
        }
        return max;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        int j = getFront(nextFront);
        int total = 1;
        T max = items[j];
        for (int i = j + 1; i < items.length; i++) {
            if (c.compare(items[i], max) > 0) {
                max = items[i];
            }
            total++;
        }
        for (int i = 0; total < this.size(); i++) {
            if (c.compare(items[i], max) > 0) {
                max = items[i];
            }
            total++;
        }
        return max;
    }


}
