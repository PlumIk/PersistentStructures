package PersistentMassive;

import PersistentList.ListNode;
import Share.Node;

import java.util.ArrayList;
import java.util.Stack;

public class PersistentMassive <T>{
    private final int length;

    /** Данные в зависимости от версии. */
    private final ArrayList<Node<T>> data=new ArrayList<>();;

    private Stack <PersistentMassive <T>> prev = new Stack<>();
    private Stack <PersistentMassive <T>> next =  new Stack<>();

    /**
     * Конструктор класса. Создаёт массив с одним элементом null.
     */
    public PersistentMassive() {
        data.add(new Node<T>());
        length =1;
    }

    /**
     * Конструктор класса. Создаёт массив заданной длины.
     *
     * @param capacity Длина.
     */
    public PersistentMassive(int capacity) {
        for (int i = 0; i < capacity; i++) {
            data.add(new Node<T>());
        }
        length = capacity;
    }

    public PersistentMassive(ListNode<T> node){
        int length=0;
        while (node!=null){
            data.add(node);
            length++;
            node=node.getNext();
        }
        this.length=length;
    }

   private PersistentMassive(Stack <PersistentMassive <T>> prevs, Stack <PersistentMassive <T>> nexts, Node<T> value, int index, int last){
       PersistentMassive<T> prev = prevs.pop();
       for (int i = 0; i < last; i++) {
           data.add(new Node<>(prev.get(i)));
       }

       if(value!=null) {
           if (index < prev.length) {
               data.add(index, value);
           } else {
               data.add(value);
               last++;
           }
       }
       length = last;
       this.prev=prevs;
       this.next=nexts;
   }

    /**
     * Возвращает значение на выбранном месте в выбранной версии.
     *
     * @param index     Место значения.
     * @return          Значение.
     */
    public T get(int index) {
        return data.get(index).getData();
    }

    /**
     * Вставляет на выбранное место значение.
     *
     * @param index Место значения.
     * @param value Значение.
     * @return      Новый массив.
     */
    public PersistentMassive<T> set(int index, T value) {
        Stack <PersistentMassive <T>> prev = (Stack <PersistentMassive <T>>) this.prev.clone();
        Stack <PersistentMassive <T>> next = (Stack <PersistentMassive <T>>) this.next.clone();
        prev.push(this);
        prev.push(this);
        return new PersistentMassive<T>(prev,next, new Node<>(value), index, length);
    }

    /**
     * Возвращает длину массива в последней версии..
     *
     * @return длина массива.
     */
    public int size() {return length;}

    /**
     * Добавляет значение в конец массива.
     *
     * @param   value значение.
     * @return  Новый массив.
     */
    public PersistentMassive<T> add(T value) {
        Stack <PersistentMassive <T>> prev = (Stack <PersistentMassive <T>>) this.prev.clone();
        Stack <PersistentMassive <T>> next = (Stack <PersistentMassive <T>>) this.next.clone();
        prev.push(this);
        prev.push(this);
        return new PersistentMassive<T>(prev,next,new Node<>(value), length, length);
    }

    /**
     * Удаляет значение в с конца массива.
     *
     * @return Номер новой версии массива.
     */
    public PersistentMassive<T> remove() {
        Stack <PersistentMassive <T>> prev = (Stack <PersistentMassive <T>>) this.prev.clone();
        Stack <PersistentMassive <T>> next = (Stack <PersistentMassive <T>>) this.next.clone();
        prev.push(this);
        prev.push(this);
        return new PersistentMassive<T>(prev,next,null, length, length -1);
    }

    public PersistentMassive<T> Undo(){
        if(prev.isEmpty()){
            return this;
        }
        Stack <PersistentMassive <T>> prev = (Stack <PersistentMassive <T>>) this.prev.clone();
        Stack <PersistentMassive <T>> next = (Stack <PersistentMassive <T>>) this.next.clone();
        next.push(this);
        return new PersistentMassive<T>(prev, next, null, prev.lastElement().length, prev.lastElement().length);
    }

    public  PersistentMassive<T> Redo(){
        if(next.isEmpty()){
            return this;
        }
        Stack <PersistentMassive <T>> prev = (Stack <PersistentMassive <T>>) this.prev.clone();
        Stack <PersistentMassive <T>> next = (Stack <PersistentMassive <T>>) this.next.clone();
        prev.push(next.pop());
        return new PersistentMassive<T>(prev, next, null,prev.lastElement().length, prev.lastElement().length);
    }
}
