import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.*;

public class SetTest {
    @Test
    public void isEmpty1() {
        SetImpl<Integer> set = new SetImpl<>();
        assertTrue(set.isEmpty());
    }

    @Test
    public void isEmpty2() {
        SetImpl<Integer> set = new SetImpl<>();
        set.add(2);
        assertFalse(set.isEmpty());
        set.remove(2);
        assertTrue(set.isEmpty());
    }

    @Test
    public void add1() {
        SetImpl<Integer> set = new SetImpl<>();
        assertTrue(set.add(1));
        assertFalse(set.add(1));
        assertTrue(set.add(2));
        assertFalse(set.add(1));
        assertFalse(set.add(2));
    }

    @Test
    public void add2() {
        SetImpl<Integer> set = new SetImpl<>();
        assertTrue(set.add(1));
        assertFalse(set.add(1));
        set.remove(1);
        assertTrue(set.add(1));
    }

    @Test
    public void remove1() {
        SetImpl<Integer> set = new SetImpl<>();
        set.add(1);
        assertTrue(set.remove(1));
        assertFalse(set.remove(1));
    }

    @Test
    public void addContains1() {
        SetImpl<Integer> set = new SetImpl<>();
        assertFalse(set.contains(2));
        set.add(2);
        assertTrue(set.contains(2));
        set.add(1);
        assertTrue(set.contains(1));
    }

    @Test
    public void addContains2() {
        SetImpl<Integer> set = new SetImpl<>();
        set.add(2);
        set.add(3);
        set.add(4);

        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
    }

    @Test
    public void addContains3() {
        SetImpl<Integer> set = new SetImpl<>();
        set.add(2);
        set.add(3);
        set.add(4);
        set.remove(3);

        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
        assertTrue(set.contains(4));
    }

    @Test
    public void iterator() {
        SetImpl<Integer> set = new SetImpl<>();
        set.add(2);
        set.add(3);
        set.add(4);
        set.remove(3);

        Iterator<Integer> it = set.iterator();
        ArrayList<Integer> elems = new ArrayList<>();
        it.forEachRemaining(elems::add);
        Collections.sort(elems);

        assertEquals(Arrays.asList(2, 4), elems);
    }

    @Test
    public void iterator2() {
        SetImpl<Integer> set = new SetImpl<>();
        set.add(2);
        set.add(3);
        set.add(4);
        set.remove(3);
        set.add(3);
        set.add(5);
        set.remove(4);
        set.add(8);
        set.add(5);
        set.remove(4);
        set.remove(4);

        Iterator<Integer> it = set.iterator();
        ArrayList<Integer> elems = new ArrayList<>();
        it.forEachRemaining(elems::add);
        Collections.sort(elems);

        assertEquals(Arrays.asList(2, 3, 5, 8), elems);
    }
}