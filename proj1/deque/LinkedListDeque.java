package deque;
public class LinkedListDeque<T>{
    private stuffNode sentinel;
    private int size;
    private class stuffNode{
        public stuffNode prev;
        public T item;
        public stuffNode next;
        public stuffNode(stuffNode p,T i,stuffNode n){
            prev=p;
            item=i;
            next=n;
        }

        public stuffNode(stuffNode p,stuffNode n){
            prev=p;
            next=n;
        }
    }
    //Creates an empty deque
    public LinkedListDeque(){
        sentinel=new stuffNode(null,null);
        sentinel.prev=sentinel;
        sentinel.next=sentinel;
        size=0;
    }

    public void addFirst(T item) {
        stuffNode second=sentinel.next;
        stuffNode first=new stuffNode(sentinel,item,second);
        second.prev=first;
        sentinel.next=first;
        size++;
    }

    public void addLast(T item){
        stuffNode prevlast=sentinel.prev;
        stuffNode last=new stuffNode(prevlast,item,sentinel);
        prevlast.next=last;
        sentinel.prev=last;
        size++;
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
        for(stuffNode s=sentinel.next;s!=sentinel&&s!=null;s=s.next){
            System.out.print(s.item+" ");
        }
        System.out.println("");

    }

    public T removeFirst(){
        if(size==0){
            return null;
        }
        T removed=sentinel.next.item;
        stuffNode first=sentinel.next.next;
        sentinel.next=first;
        first.prev=sentinel;
        size--;
        return removed;
    }

    public T removeLast() {
        if(size==0){
            return null;
        }
        T removed=sentinel.prev.item;
        stuffNode last=sentinel.prev.prev;
        sentinel.prev=last;
        last.next=sentinel;
        size--;
        return removed;
    }

    public T get(int index){
        if(index>=size||index<0){
            return null;
        }
        int position=0;
        stuffNode cur=sentinel.next;
        while(index>position){
            cur=cur.next;
            position++;
        }
        return cur.item;
    }

    public T getRecursive(int index,stuffNode cur){
        if(index<0){
            return null;
        }
        if(index==0){
            return cur.item;
        }
        return this.getRecursive(index-1,cur.next);
    }









}