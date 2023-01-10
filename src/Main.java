import PersistentList.PersistentList;
import PersistentMassive.PersistentMassive;

import java.util.Iterator;

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

    public static void main(String[] args) {
        PersistentList<Integer> a = new PersistentList<Integer>();
        a = a.add(1);
        System.out.println(a.get(0));
        printList(a);
    }
}