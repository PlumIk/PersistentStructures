package PersistentList;
import PersistentMassive.PersistentMassive;
import Utils.Exceptions;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class PersistentList<T> {
    private final int length;
    private  ListNode<T> head;
    private ListNode<T> tail;
    private Stack <PersistentList<T>> prev = new Stack<>();
    private Stack <PersistentList <T>> next =  new Stack<>();

    /**
     * Конструктор класса. Создаёт пустой лист.
     */
    public PersistentList() {
        length = 0;
    }

    /**
     * Конструктор класса. Создаёт лист на основе коллекции.
     *
     * @param c Коллекция.
     */
    public PersistentList(Collection<T> c) {
        length = c.size();
        ListNode<T> node = null;
        for (T value : c) {
            ListNode<T> subNode= new ListNode<T>(value, node,null);
            if(node!=null){
                node.setNext(subNode);
            }
            node=subNode;
            if(head == null){
                head = node;
            }
            tail=node;
        }
    }

    private PersistentList(Stack <PersistentList <T>> prevs, Stack <PersistentList <T>> nexts, ListNode<T> head, ListNode<T> tail, int size){
        this.head = head;
        this.tail = tail;
        length = size;
        this.prev=prevs;
        this.next=nexts;
    }

    /**
     * Возвращает длину листа в последней версии.
     *
     * @return Длина листа.
     */
    public int size() {
        return length;
    }

    /**
     * Возвращает, пустой ли лист в последней версии.
     *
     * @return Пустой ли лист.
     */
    public boolean isEmpty() {
        return length ==0;
    }

    /**
     * Возвращает, содержит ли лист выбранной версии значение.
     *
     * @param o         Значение.
     * @return          true, если содержит. Иначе false.
     */
    public boolean contains(Object o) {
        ListNode<T> current = head;
        for (int i = 0; i < length; i++) {
            if (current.getData() == null) {
                if (o == null) return true;
            } else {
                if (current.getData().equals(o))
                    return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Добавляет значение в конец листа.
     *
     * @param o     Значение.
     * @return      true, если значение было добавлено. Иначе false.
     */
    public PersistentList<T> add(T o) {
        if(head==null){
            ListNode<T> current = new ListNode<T>(o,null, null);
            Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
            prev.push(this);
            Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
            return new PersistentList<T>(prev, next,current, current, 1);
        }
        ListNode<T> current = head;
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        for (int i = 0; i < length; i++) {
            ListNode<T> subCurrent = new ListNode<T>(current.getData(), subTail, null);
            if(subTail!=null){
                subTail.setNext(subCurrent);
            }
            if(subHead==null){
                subHead = subCurrent;
            }
            subTail=subCurrent;
            current = current.getNext();
        }
        ListNode<T> subCurrent = new ListNode<T>(o, subTail, null);
        subTail.setNext(subCurrent);
        subTail=subCurrent;
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
        return new PersistentList<T>(prev, next,subHead, subTail, length +1);
    }

    /**
     * Удаляет значение из листа.
     *
     * @param o     Значение.
     * @return      true, если значение было удалено. Иначе false.
     */
    public PersistentList<T> remove(Object o) {
        if (!contains(o)){
            return this;
        }
        ListNode<T> current = head;
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        int subAdd=0;
        for (int i = 0; i < length; i++) {
            if(!current.getData().equals(o)) {
                ListNode<T> subCurrent = new ListNode<T>(current.getData(), subTail, null);
                if (subTail != null) {
                    subTail.setNext(subCurrent);
                }
                if (subHead == null) {
                    subHead = subCurrent;
                }
                subTail = subCurrent;
            }else{
                subAdd--;
            }
            current = current.getNext();
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
        return new PersistentList<T>(prev, next,subHead, subTail, length +subAdd);
    }

    /**
     * Добавляет коллекция в лист начмная с определённого значения.
     *
     * @param index     Индекс, с которого начинается добавление.
     * @param c         Коллекция.
     * @return          Новый лист.
     */
    public PersistentList<T> addAll(int index, Collection<T> c) {
        ListNode<T> current = head;
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        int addLen=0;
        for (int i = 0; i < length; i++) {

            if(i==index){
                for (T value : c) {
                    ListNode<T> subCurrent= new ListNode<T>(value, subTail,null);
                    if(subTail!=null){
                        subTail.setNext(subCurrent);
                    }
                    if(subHead==null){
                        subHead = subCurrent;
                    }
                    subTail=subCurrent;
                    addLen++;
                }
            }

            ListNode<T> subCurrent = new ListNode<T>(current.getData(), subTail, null);
            if(subTail!=null){
                subTail.setNext(subCurrent);
            }
            if(subHead==null){
                subHead = subCurrent;
            }
            subTail=subCurrent;
            current = current.getNext();
        }

        if(length ==index){
            for (T value : c) {
                ListNode<T> subCurrent= new ListNode<T>(value, subTail,null);
                if(subTail!=null){
                    subTail.setNext(subCurrent);
                }
                if(subHead==null){
                    subHead = subCurrent;
                }
                subTail=subCurrent;
                addLen++;
            }
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
        return new PersistentList<T>(prev, next,subHead, subTail, length +addLen);
    }

    /**
     * Добавляет коллекцию в конец листа.
     *
     * @param c     Коллекция.
     * @return      Новый лист.
     */
    public PersistentList<T> addAll(Collection<T> c) {
        return addAll(length, c);
    }

    /**
     * Возвращает, содержит ли лист все значения из коллекции.
     *
     * @param c         Коллекция.
     * @return          true, если содержит. Иначе False.
     */
    public boolean containsAll(Collection<T> c) {
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    /**
     * Удаляет все эелементы из коллекции, содержащиеся в листе.
     *
     * @param c     Коллекция.
     * @return      true, если было удалено хоть одно значение, иначе false.
     */
    public PersistentList<T> removeAll(Collection<T> c) {
        boolean containAny=false;
        for (Object o : c) {
            if (contains(o))
                containAny = true;
        }
        if(!containAny){
            return this;
        }
        ListNode<T> current = head;
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        int subAdd=0;
        for (int i = 0; i < length; i++) {
            boolean remove = false;
            for (Object o : c) {
                remove = remove||current.getData().equals(o);
            }

            if(remove){
                subAdd--;
            }else{
                ListNode<T> subCurrent = new ListNode<T>(current.getData(), subTail, null);
                if (subTail != null) {
                    subTail.setNext(subCurrent);
                }
                if (subHead == null) {
                    subHead = subCurrent;
                }
                subTail = subCurrent;
            }
            current = current.getNext();
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
        return new PersistentList<T>(prev, next,subHead, subTail, length +subAdd);
    }

    /**
     * Возвращает значение по индексу.
     *
     * @param index     Индекс.
     * @return          Значение.
     */
    public Object get(int index) {
        if(index<0||index>= length){
            return Exceptions.LIST_INDEX_OUT_OF_BOUNDS;
        }
        else if(index==0){
            return head.getData();
        } else if (index== length -1) {
            return tail.getData();
        }else {
            ListNode<T> current = head;
            for(int i=0;i<index;i++){
                current=head.getNext();
            }
            return current.getData();
        }
    }

    /**
     * Заменяет значение в листе по индексу.
     *
     * @param index     Индекс.
     * @param element   Новое значение.
     * @return          Предыдущее значение.
     */
    public PersistentList<T>  set(int index, T element) {
        ListNode<T> current = head;
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        for (int i = 0; i < length; i++) {
            ListNode<T> subCurrent;
            if(i==index){
                subCurrent= new ListNode<T>(element, subTail, null);
            }else{
                subCurrent= new ListNode<T>(current.getData(), subTail, null);
            }

            if(subTail!=null){
                subTail.setNext(subCurrent);
            }
            if(subHead==null){
                subHead = subCurrent;
            }
            subTail=subCurrent;
            current = current.getNext();
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
        return new PersistentList<T>(prev, next,subHead, subTail, length);
    }


    /**
     * Добавляет в лист значение на определённое место(не переписывает прошлое, а вставляет новое, сдвигая на 1 элемент)
     *
     * @param index     Индекс.
     * @param element   Добавленный элемент.
     */
    public PersistentList<T>  add(int index, T element) {
        ListNode<T> current = head;
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        for (int i = 0; i < length; i++) {
            ListNode<T> subCurrent;
            if(i==index){
                subCurrent= new ListNode<T>(element, subTail, null);
                if(subTail!=null){
                    subTail.setNext(subCurrent);
                }
                if(subHead==null){
                    subHead = subCurrent;
                }
                subTail=subCurrent;
            }

            subCurrent= new ListNode<T>(current.getData(), subTail, null);
            if(subTail!=null){
                subTail.setNext(subCurrent);
            }
            if(subHead==null){
                subHead = subCurrent;
            }
            subTail=subCurrent;
            current = current.getNext();
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
        return new PersistentList<T>(prev, next,subHead, subTail, length +1);
    }

    /**
     * Удаляет элемент по индексу.
     *
     * @param index     Индекс.
     * @return          Удалённый элемент.
     */
    public PersistentList<T>  remove(int index) {
        ListNode<T> current = head;
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        for (int i = 0; i < length; i++) {
            if(i!=index) {
                ListNode<T> subCurrent;
                subCurrent = new ListNode<T>(current.getData(), subTail, null);
                if (subTail != null) {
                    subTail.setNext(subCurrent);
                }
                if (subHead == null) {
                    subHead = subCurrent;
                }
                subTail = subCurrent;
            }
            current = current.getNext();
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>)this.next.clone();
        return new PersistentList<T>(prev, next,subHead, subTail, length -1);
    }

    /**
     * Возвращает номер элемента в листе.
     *
     * @param o         Элемент.
     * @return          Номер элемента или -1, если такого элемента не содержиться.
     */
    public int indexOf(Object o) {
        if(!contains(o)){
            return -1;
        }
        ListNode<T> node = head;
        int i = 0;
        while (!node.getData().equals(o)){
            i++;
            node = node.getNext();
        }
        return i;
    }

    public Iterator iterator(int index) {
        return new ListIterator() {
            int currIndex = index-1;
            ListNode currElement = head;

            {
                for (int i = 0; i < index; i++) {
                    currElement = head.getNext();
                }
            }


            @Override
            public boolean hasNext() {
                if (currIndex < 0) return currElement != null;
                return currElement.getNext() != null;
            }

            @Override
            public Object next() {
                if (hasNext()) {
                    if (currIndex >= 0)
                        currElement = currElement.getNext();
                    currIndex++;
                    return currElement.getData();
                } else
                    throw new NoSuchElementException(Exceptions.NO_SUCH_ELEMENT);
            }

            @Override
            public boolean hasPrevious() {
                if (currIndex >= length) return currElement != null;
                return currElement.getPrev() != null;
            }

            @Override
            public Object previous() {
                if (hasPrevious()) {
                    if (currIndex < length)
                        currElement = currElement.getPrev();
                    currIndex--;
                    return currElement.getData();
                } else
                    throw new NoSuchElementException(Exceptions.NO_SUCH_ELEMENT);
            }

            @Override
            public int nextIndex() {
                return min(currIndex + 1, length);
            }

            @Override
            public int previousIndex() {
                return max(currIndex - 1, -1);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Object o) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterator iterator(){
        return iterator(0);
    }

    public PersistentList<T> Undo() {
        if (prev.isEmpty()) {
            return this;
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>) this.next.clone();
        PersistentList<T> currentList = prev.pop();
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        if (!currentList.isEmpty()) {
            for (int i = 0; i < currentList.size(); i++) {
                ListNode<T> subCurrent;
                subCurrent = new ListNode<T>((T)currentList.get(i), subTail, null);
                if (subTail != null) {
                    subTail.setNext(subCurrent);
                }
                if (subHead == null) {
                    subHead = subCurrent;
                }
                subTail = subCurrent;
            }
        }
        next.push(this);
        return  new PersistentList<T>(prev,next,subHead, subTail, currentList.size());
    }

    public PersistentList<T> Redo() {
        if (next.isEmpty()) {
            return this;
        }
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>) this.next.clone();
        PersistentList<T> currentList = next.pop();
        ListNode<T> subHead = null;
        ListNode<T> subTail = null;
        if (!currentList.isEmpty()) {
            for (int i = 0; i < currentList.size(); i++) {
                ListNode<T> subCurrent;
                subCurrent = new ListNode<T>((T)currentList.get(i), subTail, null);
                if (subTail != null) {
                    subTail.setNext(subCurrent);
                }
                if (subHead == null) {
                    subHead = subCurrent;
                }
                subTail = subCurrent;
            }
        }
        prev.push(this);
        return  new PersistentList<T>(prev,next,subHead, subTail, currentList.size());
    }

}
