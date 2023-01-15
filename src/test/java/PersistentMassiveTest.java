import PersistentList.ListNode;
import PersistentMap.PersistentMap;
import PersistentMassive.PersistentMassive;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersistentMassiveTest {
    @Test
    public void check() {
        PersistentMassive<String> a = new PersistentMassive<String>();
        a = a.add("2");
        assertEquals(2, a.size());
        a = a.set(2, "4");
        assertEquals("4", a.get(2));
        assertEquals(3, a.size());
        a = a.remove();
        assertEquals(2, a.size());
        PersistentMassive<String> b = new PersistentMassive<String>(3);
        assertEquals(3, b.size());
        ListNode<String> l = new ListNode<String>("we", null, null);
        PersistentMassive<String> c = new PersistentMassive<String>(l);
        assertEquals(1, c.size());
    }

    @Test
    public void undo() {
        PersistentMassive<String> a = new PersistentMassive<String>();
        a = a.add("2");
        a = a.add("3");
        a = a.Undo();
        assertEquals(2, a.size());
        a = a.Undo();
        assertEquals(1, a.size());
        a = a.Undo();
        assertEquals(1, a.size());
    }

    @Test
    public void repo() {
        PersistentMassive<String> a = new PersistentMassive<String>();
        a = a.add("2");
        a = a.add("3");
        a = a.Redo();
        assertEquals(3, a.size());
        a = a.Undo();
        assertEquals(2, a.size());
        a = a.Redo();
        assertEquals(3, a.size());
    }
}
