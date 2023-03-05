package tester;

import static org.junit.Assert.*;

import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.algs4.StdRandom;

public class TestArrayDequeEC {
    @Test
    public void RandomTest() {
        StudentArrayDeque<Integer> a = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> b = new ArrayDequeSolution<>();
        String log = "";
        int N = 2500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                a.addLast(randVal);
                b.addLast(randVal);
                log += "addFirst(" + randVal + ")\n";
            } else if (operationNumber == 1) {
                //addFirst
                int randVal = StdRandom.uniform(0, 100);
                a.addFirst(randVal);
                b.addFirst(randVal);
                log += "addLast(" + randVal + ")\n";
            } else if (operationNumber == 2) {
                //removeFirst
                if (a.size() == 0 || b.size() == 0) {
                    continue;
                }
                log += "removeFirst()\n";
                Integer x = a.removeFirst();
                Integer y = b.removeFirst();
                assertEquals(log, x, y);
            } else {
                //removeLast
                if (a.size() == 0 || b.size() == 0) {
                    continue;
                }
                log += "removeLast()\n";
                Integer x = a.removeLast();
                Integer y = b.removeLast();
                assertEquals(log, x, y);
            }
        }
    }
}
