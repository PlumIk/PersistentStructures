import PersistentList.PersistentList;
import PersistentMassive.PersistentMassive;

public class Main {

    private static void printMas(PersistentMassive massive){
        for(int i=0;i< massive.size();i++){
            System.out.println("In "+i+ " value "+ massive.get(i));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        PersistentMassive<Integer> a = new PersistentMassive<Integer>();
        a = a.set(0,1);
        a=a.add(2);
        a=a.add(3);
        printMas(a);
        var b = a.Undo();
        printMas(b);
        b=b.Undo();
        printMas(b);
        printMas(b.Undo());
        printMas(b.Redo());
        var c = b.add(15);
        printMas(c);
        printMas(b.Redo());
    }
}