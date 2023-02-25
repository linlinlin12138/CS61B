package deque;
import org.junit.Test;
import static org.junit.Assert.*;
public class ArrayDequeTest {
    @Test
    public void AddandGet(){
        ArrayDeque<Integer> a=new ArrayDeque<Integer>();
        for(int i=0;i<3;i++){
            a.addFirst(i);
        }
        for(int i=0;i<4;i++){
            a.addLast(i);
        }

        assertEquals(1,a.get(1),0.0);
        assertEquals(3,a.get(6),0.0);
    }

    @Test
    public void FillEmptyFill(){
        ArrayDeque<Integer> a=new ArrayDeque<Integer>();
        for(int i=0;i<3;i++){
            a.addFirst(i);
        }
        for(int i=0;i<5;i++){
            a.addLast(i);
        }
        assertEquals(2,a.removeFirst(),0.0);
        /*for(int i=0;i<8;i++){
            a.removeFirst();
        } */
    }
}
