import PersistentList.PersistentList;
import PersistentMap.PersistentMap;
import PersistentMassive.PersistentMassive;
import Utils.Exceptions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import static org.junit.Assert.*;

public class PersistentListTest {
    @Test
    public void check() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add(null);
        a.contains("s");
        assertEquals(3, a.size());
        assertFalse(a.isEmpty());
        Collection<String> c = new LinkedList<String>();
        c.add("s");
        c.add("s");
        PersistentList<String> b = new PersistentList<String>(c);
        assertEquals(2, b.size());
    }

    @Test
    public void remove() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        a = a.remove("sad");
        assertEquals(2, a.size());
        a = a.remove("sad");
        assertEquals(2, a.size());
    }

    @Test
    public void addAll() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        Collection<String> c = new LinkedList<String>();
        c.add("s");
        c.add("s");
        a = a.addAll(0, c);
        assertEquals(5, a.size());
        a = a.addAll(5, c);
        assertEquals(7, a.size());
        PersistentList<String> b = new PersistentList<String>();
        b = b.addAll(0, c);
        assertEquals(2, b.size());
    }

    @Test
    public void addAll2() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        Collection<String> c = new LinkedList<String>();
        c.add("s");
        c.add("s");
        a = a.addAll(c);
        assertEquals(5, a.size());
    }

    @Test
    public void containsAll() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        Collection<String> c = new LinkedList<String>();
        c.add("sad");
        c.add("bad");
        assertTrue(a.containsAll(c));
        c.add("s");
        assertFalse(a.containsAll(c));
    }

    @Test
    public void removeAll() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        Collection<String> c = new LinkedList<String>();
        c.add("s");
        c.add("bad");
        a = a.removeAll(c);
        assertEquals(2, a.size());
        c = new LinkedList<String>();
        c.add("s");
        a = a.removeAll(c);
        assertEquals(2, a.size());
    }

    @Test
    public void get() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        assertEquals("bad", a.get(1));
        assertEquals("sad", a.get(0));
        assertEquals("null", a.get(2));
        try {
            a.get(-1);
        } catch (Exception thrown) {
            assertEquals(Exceptions.LIST_INDEX_OUT_OF_BOUNDS, thrown.getMessage());
        }
    }

    @Test
    public void set() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        a = a.set(1,"gad");
        assertEquals("gad", a.get(1));
    }

    @Test
    public void add() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        a = a.add(1,"gad");
        assertEquals(4, a.size());
        a = a.add(0,"gad");
        assertEquals(5, a.size());
    }

    @Test
    public void remove2() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        a = a.remove(1);
        assertEquals(2, a.size());
    }

    @Test
    public void indexOf() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        assertEquals(2, a.indexOf("null"));
        assertEquals(-1, a.indexOf("s"));
    }

    @Test
    public void iterator() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        ListIterator i = (ListIterator) a.iterator();
        i = (ListIterator)a.iterator(1);
        assertTrue(i.hasNext());
        assertEquals("null", i.next());
        try {
            i.next();
        } catch (Exception thrown) {
            assertEquals(Exceptions.NO_SUCH_ELEMENT, thrown.getMessage());
        }
        assertEquals(2, i.nextIndex());
        assertEquals(0, i.previousIndex());
        assertEquals("bad", i.previous());
        i.previous();
        try {
            i.previous();
        } catch (Exception thrown) {
            assertEquals(Exceptions.NO_SUCH_ELEMENT, thrown.getMessage());
        }
        try {
            i.remove();
        } catch (Exception thrown) {
            assertNotEquals("", thrown.getMessage());
        }
        try {
            i.set("");
        } catch (Exception thrown) {
            assertNotEquals("", thrown.getMessage());
        }
        try {
            i.add("");
        } catch (Exception thrown) {
            assertNotEquals("", thrown.getMessage());
        }
    }

    @Test
    public void redo() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        a = a.Redo();
        assertEquals(3, a.size());
        a = a.Undo();
        assertEquals(2, a.size());
        a = a.Redo();
        assertEquals(3, a.size());
    }

    @Test
    public void undo() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        a = a.Undo();
        assertEquals(2, a.size());
        a = a.Undo();
        a = a.Undo();
        a = a.Undo();
        assertEquals(0, a.size());
    }

    @Test
    public void toArray() {
        PersistentList<String> a = new PersistentList<String>();
        a = a.add("sad");
        a = a.add("bad");
        a = a.add("null");
        PersistentMassive<String> b = a.toArray();
        assertEquals(3, b.size());
    }
}
