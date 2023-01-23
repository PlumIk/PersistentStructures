import PersistentList.PersistentList;
import PersistentMap.PersistentMap;
import PersistentMassive.PersistentMassive;

import java.util.*;

public class Main {

    private static void printMas(PersistentMassive massive){
        for(int i=0;i< massive.size();i++){
            System.out.println("In "+i+ " value "+ massive.get(i));
        }
        System.out.println();
    }

    private static void printList(PersistentList list){
        System.out.println(list.size());
        Iterator asd = list.iterator();
        while (asd.hasNext()){
            Object z = asd.next();
            System.out.println("value:"+z);
        }
        System.out.println();
    }

    private static void printMap(PersistentMap map){
        Set keys = map.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            Object k = it.next();
            Object v = map.get(k);
            System.out.println("key: "+k+" , value: "+v);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("list");
        PersistentList<Integer> a = new PersistentList<Integer>();
        a = a.add(1);
        PersistentList<Integer> p = a.add(2);
        a = a.add(3);
        printList(a);
        printList(p);
        a = a.add(3);
        p = p.add(7);
        printList(a);
        printList(p);
        a = a.Undo();
        printList(a);
        /*Object x = 3;
        a = a.remove(x);
        printList(a);
        a = a.add(1, 2);
        printList(a);
        a = a.remove(1);
        printList(a);
        System.out.println(a.indexOf(x));
        /*a = a.Undo();
        printList(a);
        a = a.Redo();
        printList(a);
        Collection<Integer> col = new LinkedList<Integer>();
        col.add(1);
        col.add(2);
        a = a.addAll(col);
        printList(a);
*/
        System.out.println("mas");
        PersistentMassive<Integer> b = new PersistentMassive<Integer>();
        b=b.add(1);
        b=b.add(2);
        b = b.set(1,3);
        printMas(b);
        /*b=b.add(4);
        printMas(b);
        PersistentMassive<Integer> z = b.add(5);
        printMas(z);
        b = b.Undo();
        b = b.add(7);
        printMas(b);
        printMas(z);
*/
        System.out.println("map");
        PersistentMap<String, String> c = new PersistentMap<String, String>();
        c = c.put("sad", "bad");
        printMap(c);
        PersistentMap<String, String> d = c.put("s", "b");
        System.out.println(c.get("sad"));
        c = c.put("sed", "bad");
        printMap(c);
        d = d.put("s", "bc");
        d = d.put("se", "bdc");
        printMap(d);
        d = d.Undo();
        PersistentMap<String, String> q = d.put("sz", "ba");
        printMap(q);
        printMap(d);
        d = d.Redo();
        printMap(d);
       /* Collection v = d.values();
        System.out.println(v);*/

        /*System.out.println("map2");
        PersistentMap<String, Integer> n = new PersistentMap<String, Integer>();
        n = n.put("sad", 50);
        n = n.put("bad", 20);
        printMap(n);
        n = n.computeIfPresent("sad", (k,q) -> {return (Integer)q/2;});
        printMap(n);*/

       /* PersistentMap<String, String> c = new PersistentMap<String, String>();
        c = c.put("sad", "bad");
        c = c.put("s", "bc");
        c = c.put("se", "bdc");
        printMap(c);
        c = c.Undo();
        printMap(c);
        PersistentMap<String, String> q = c.put("sz", "ba");
        printMap(q);
        printMap(c);
        c = c.put("sda", "xfff");
        printMap(c);*/

        /*PersistentMap<String, String> c = new PersistentMap<String, String>();
        c = c.put("sad", "bad");
        c = c.put("s", "bc");
        PersistentMap<String, String> x = c.put("s", "sad");
        printMap(c);
        printMap(x);
        c = c.put("sad", "sasa");
        printMap(c);*/
    }
}