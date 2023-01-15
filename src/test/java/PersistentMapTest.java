import PersistentMap.PersistentMap;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class PersistentMapTest {

    @Test
    public void check() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        assertEquals(1, a.size());
        a = a.put("sad", "ba");
        assertEquals("ba", a.get("sad"));
        assertNull(a.get("sa"));
        a = a.remove("sad");
        a = a.put("sad", "ba");
        assertEquals("ba", a.get("sad"));
        assertFalse(a.isEmpty());
        assertTrue(a.containsKey("sad"));
        assertFalse(a.containsKey("s"));
        a = a.put("n", null);
        assertTrue(a.containsValue(null));
        assertTrue(a.containsValue("ba"));
        assertFalse(a.containsValue("sad"));
    }

    @Test
    public void remove() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.remove("sad");
        assertEquals(0, a.size());
    }

    @Test
    public void putAll() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        Map<String, String> m = new HashMap<>();
        m.put("sad", "bad");
        m.put("s", "b");
        a = a.putAll(m);
        assertEquals(2, a.size());
    }

    @Test
    public void keySet() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        Set k = a.keySet();
        assertEquals(1, k.size());
    }

    @Test
    public void values() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        Collection v = a.values();
        assertEquals(1, v.size());
    }

    @Test
    public void entrySet() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        Set v = a.entrySet();
        assertEquals(1, v.size());
    }

    @Test
    public void getOrDefault() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        assertEquals("bad", a.getOrDefault("sad", "gad"));
        assertEquals("gad", a.getOrDefault("s", "gad"));
    }

    @Test
    public void forEach() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.forEach((k,v) -> System.out.println("key: "+k+" value:"+v));
    }

    @Test
    public void replaceAll() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.replaceAll((k ,v) -> {return "baba";});
        assertEquals("baba", a.get("sad"));
    }

    @Test
    public void putIfAbsent() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.putIfAbsent("sa", "s");
        assertEquals(2, a.size());
        a = a.putIfAbsent("sa", "ss");
        assertEquals(2, a.size());
        a = a.putIfAbsent("s", "ss");
        assertEquals(3, a.size());
    }

    @Test
    public void remove2() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.remove("sad", "ba");
        assertEquals(1, a.size());
        a = a.remove("sad", "bad");
        assertEquals(0, a.size());
    }

    @Test
    public void replace() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.replace("sad", "ba", "sa");
        assertEquals("bad", a.get("sad"));
        a = a.replace("sad", "bad", "gad");
        assertEquals("gad", a.get("sad"));
    }

    @Test
    public void replace2() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.replace("s", "ba");
        assertNull(a.get("s"));
        a = a.replace("sad", "gad");
        assertEquals("gad", a.get("sad"));
    }

    @Test
    public void computeIfAbsent() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.put("sx", null);
        a = a.remove("s");
        a = a.computeIfAbsent("sx", (k) -> {
            return null;
        });
        assertEquals(2, a.size());
        a = a.computeIfAbsent("sx", (k) -> {
                return "baba";
        });
        assertEquals(2, a.size());
        a = a.computeIfAbsent("s", (k) -> {
                return "baba";
        });
        assertEquals(3, a.size());
        a = a.computeIfAbsent("sad", (k) -> {
                return "baba";
        });
        assertEquals(3, a.size());
    }

    @Test
    public void computeIfPresent() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.computeIfPresent("s", (k, v) -> {
            return "baba";
        });
        assertEquals("bad", a.get("sad"));
        a = a.computeIfPresent("sad", (k, v) -> {
            return "baba";
        });
        assertEquals("baba", a.get("sad"));
        a = a.computeIfPresent("sad", (k, v) -> {
            return null;
        });
        assertEquals(0, a.size());
    }

    @Test
    public void compute() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.compute("s", (k, v) -> {
            return "baba";
        });
        assertEquals("bad", a.get("sad"));
        a = a.compute("sad", (k, v) -> {
            return "baba";
        });
        assertEquals("baba", a.get("sad"));
        a = a.compute("sad", (k, v) -> {
            return null;
        });
        assertEquals(0, a.size());
    }

    @Test
    public void merge() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.remove("s");
        a = a.merge("s", "sa", (k, v) -> {
            return "baba";
        });
        assertEquals("bad", a.get("sad"));
        a = a.merge("sad", "bad", (k, v) -> {
            return "baba";
        });
        assertEquals("baba", a.get("sad"));
        a = a.merge("sad", "fa", (k, v) -> {
            return null;
        });
        assertEquals(0, a.size());
    }

    @Test
    public void undo() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.Undo();
        assertEquals(1, a.size());
        a = a.Undo();
        a = a.Undo();
        assertEquals(0, a.size());
    }

    @Test
    public void redo() {
        PersistentMap<String, String> a = new PersistentMap<String, String>();
        a = a.put("sad", "bad");
        a = a.put("s", "b");
        a = a.Redo();
        assertEquals(2, a.size());
        a = a.Undo();
        assertEquals(1, a.size());
        a = a.Redo();
        assertEquals(2, a.size());
    }
}