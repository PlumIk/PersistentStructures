import PersistentList.PersistentList;
import PersistentMap.PersistentMap;
import PersistentMassive.PersistentMassive;

import java.util.Iterator;
import java.util.Set;

public class Main {

    private static void printMas(PersistentMassive massive){
        for(int i=0;i< massive.size();i++){
            System.out.println("In "+i+ " value "+ massive.get(i));
        }
        System.out.println();
    }

    private static void printList(PersistentList list){
        Iterator asd = list.iterator();
        while (asd.hasNext()){
            System.out.println("value:"+asd.next());
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
        PersistentList<Integer> a = new PersistentList<Integer>();
        a = a.add(1);
        System.out.println(a.get(0));
        printList(a);
        PersistentMap<String, String> b = new PersistentMap<String, String>();
        b = b.put("sad", "bad");
        PersistentMap<String, String> c = b.put("s", "b");
        System.out.println(b.get("sad"));
        printMap(b);
        printMap(c);
    }
}