package PersistentList;

import Share.Node;

public class ListNode<T> extends Node<T> {

    /** Ссылки на предшествующие ноды в зависимости от версии. */
    private ListNode<T> prev;

    /** Ссылки на следующие значения в зависимости от версии. */
    private ListNode<T> next;

    /**
     * Конструктрак класса.
     *
     * @param value Значение в ноде.
     * @param prev  Предыдущая нода.
     * @param next  Следующая нода.
    */
    public ListNode(T value, ListNode<T> prev, ListNode<T> next) {
        data = value;
        this.prev=prev;
        this.next=next;
    }

    public void setNext(ListNode<T> next){
        if(this.next==null) this.next=next;
    }

    public void setPrev(ListNode<T> prev){
        if(this.prev==null) this.prev=prev;
    }

    public ListNode<T> getNext(){return next;}
    public ListNode<T> getPrev(){return prev;}
}
