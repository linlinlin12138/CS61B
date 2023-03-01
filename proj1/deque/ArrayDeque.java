package deque;

public class ArrayDeque <T> implements Deque<T>{
    private T[] items;
    private int size;
    private int nextFront;
    private int nextBack;
    public ArrayDeque(){
        items=(T[]) new Object[8];
        size=0;
        nextFront=4;
        nextBack=5;
    }

    private int getFront(int nextFront){
        int front=nextFront+1;
        if(front==items.length){
            return 0;
        }
        else{
            return front;
        }
    }

    private int getBack(int nextBack){
        int back=nextBack-1;
        if(back<0){
            return items.length-1;
        }
        else{
            return back;
        }
    }

    public void resize(int capacity){
        T[] a=(T[]) new Object[capacity];
        int i=getFront(nextFront);
        int total=0;
        int newindex=0;
        while(i<items.length && size>total){
            a[newindex]=items[i];
            i++;
            total++;
            newindex++;
        }
        int j=0;
        while(total<size) {
            a[newindex] = items[j];
            j++;
            total++;
            newindex++;
        }
        items=a;
        nextFront=items.length-1;
        nextBack=newindex;
    }

    public void addFirst(T item){
        if((size+1)*4>items.length) {
            resize(4 * (size + 1));
        }
        items[nextFront]=item;
        size++;
        if(nextFront==0){
            nextFront=items.length-1;
        }
        else{
            nextFront--;
        }
    }

    public void addLast(T item){
        if((size+1)*4>items.length) {
            resize(4 * (size + 1));
        }
        items[nextBack]=item;
        size++;
        if(nextBack==items.length-1){
            nextBack=0;
        }
        else {
            nextBack++;
        }
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int i=getFront(nextFront);
        int total=0;
        while(i<items.length){
            System.out.print(items[i]);
            i++;
            total++;
        }
        int j=0;
        while(total<size){
            System.out.print(items[j]);
            j++;
            total++;
        }
        System.out.println();
    }
    public T removeLast(){
        if(items.length>=16&&items.length==4*size){
            resize((size-1)*4);
        }
        T removed;
        if(size==0){
            return null;
        }
        int back=getBack(nextBack);
        removed=items[back];
        nextBack=back;
        size--;
        return removed;
    }
    public T removeFirst(){
        if(items.length>=16&&items.length==4*size){
            resize((size-1)*4);
        }
        T removed;
        if(size==0){
            return null;
        }
        int front=getFront(nextFront);
        removed=items[front];
        nextFront=front;
        size--;
        return removed;
    }

    public T get(int index){
        if(nextFront+1+index<items.length){
            return items[nextFront+1+index];
        }
        else if(index-items.length+nextFront+1>=0){
            return items[index-items.length+nextFront+1];
        }
        else{
            return null;
        }
    }


}
