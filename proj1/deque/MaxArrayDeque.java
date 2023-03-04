package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFront;
    private int nextBack;
    private Comparator comp;

    private int getFront() {
        int front = nextFront + 1;
        if (front == items.length) {
            return 0;
        } else {
            return front;
        }
    }

    private int getBack() {
        int back = nextBack - 1;
        if (back < 0) {
            return items.length - 1;
        } else {
            return back;
        }
    }

    public MaxArrayDeque(Comparator<T> c) {
        new ArrayDeque();
        comp = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        int j = getFront();
        int total = 1;
        T max = items[j];
        for (int i = j + 1; total < this.size() && i < items.length; i++) {
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
        int j = getFront();
        int total = 1;
        T max = items[j];
        for (int i = j + 1; total < this.size() && i < items.length; i++) {
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
