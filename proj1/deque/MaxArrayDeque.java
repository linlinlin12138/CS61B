package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private T[] items;
    private int size;
    private int front;
    private int back;
    private Comparator comp;
    public MaxArrayDeque(Comparator<T> c){
        items=(T[]) new Object[8];
        size=0;
        front=4;
        back=5;
        comp=c;
    }

    public T max(){
        if(isEmpty()){
            return null;
        }
        int j=0;
        while(items[j]==null){
           j++;
        }
        T max=items[j];
        for(int i=1;i<items.length;i++){
            if(items[i]!=null){
                if(comp.compare(items[i],max)>0){
                    max=items[i];
                }
            }

        }
        return max;

    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        Comparator newComp=c;
        int j=0;
        while(items[j]==null){
            j++;
        }
        T max=items[j];
        for(int i=1;i<items.length;i++){
            if(items[i]!=null){
                if(newComp.compare(items[i],max)>0){
                    max=items[i];
                }
            }

        }
        return max;
    }


}
