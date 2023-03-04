package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFront;
    private int nextBack;
    private Comparator<T> comp;

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
        items = (T[]) new Object[8];
        size = 0;
        nextFront = 4;
        nextBack = 5;
        comp = c;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        int maxIndex = 0;
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        int maxIndex = 0;
        for (int i = 1; i < size(); i++) {
            if (comp.compare(get(i), get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }

}
