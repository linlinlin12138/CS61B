package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B=new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int bsize=B.size();
                assertEquals(size,bsize,0.0);
            }
            else if(operationNumber==3){
                //getLast
                if(L.size()==0){
                    continue;
                }
                else{
                    int last= L.getLast();
                    int blast=B.getLast();
                    assertEquals(last,blast,0.0);
                }

            }
            else{
                //removeLast
                if(L.size()==0){
                    continue;
                }
                else{
                    int removed=L.removeLast();
                    int bremoved=B.removeLast();
                    assertEquals(removed,bremoved,0.0);
                }
            }
        }
    }
}
