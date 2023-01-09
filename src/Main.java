import PersistentList.PersistentList;
import PersistentMassive.PersistentMassive;

public class Main {


    public static void main(String[] args) {
        PersistentList<Integer> a = new PersistentList<Integer>();
        a.add(2);
        for(int i=0;i<8;i++){
            a.add(i+1);
        }
        a.add(3,13);

        for(Object o: a){
            System.out.println("In value:"+o);
        }
        System.out.println();
        var b= a.getArray();
        for(int i=0;i<b.size();i++){
            System.out.println("value:"+b.get(i));
        }
    }
}