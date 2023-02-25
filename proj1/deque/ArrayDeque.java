package deque;

public class ArrayDeque <T>{
    private T[] items;
    private int size;
    private int front;
    private int back;
    public ArrayDeque(){
        items=(T[]) new Object[8];
        size=0;
        front=4;
        back=5;
    }

    public void resize(int capacity){
        T[] a=(T[]) new Object[capacity];
    }

    public void addFirst(T item){
        if(size==items.length){
            resize(2*size);
        }
        items[front]=item;
        if(front==0){
            front=items.length-1;
        }
        else{
            front--;
        }
        size++;
    }

    public void addLast(T item){
        if(size==items.length){
            resize(2*size);
        }
        items[back]=item;
        size++;
        if(back==items.length-1){
            back=0;
        }
        else {
            back++;
        }
    }

    public boolean isEmpty(){
        if(size==0){
            return true;
        }
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int i=front;
        int total=0;
        while(i<items.length){
            System.out.print(items[i]);
            total++;
        }
        int j=0;
        while(total<size){
            System.out.print(items[j]);
            j++;
            total++;
        }
        System.out.println("");
    }
    public T removeLast(){
        if(size==0){
            return null;
        }
        T removed=items[back-1];
        size--;
        back--;
        return removed;
    }
    public T removeFirst(){
        if(size==0){
            return null;
        }
        T removed=items[front+1];
        size--;
        front++;
        return removed;
    }

    public T get(int index){
        if(front+1+index<items.length){
            return items[front+1+index];
        }
        else if(items.length-front-1-index>=0){
            return items[items.length-front-1-index];
        }
        else{
            return null;
        }
    }


}
