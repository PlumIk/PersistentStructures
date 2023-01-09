package PersistentList;

import java.util.TreeMap;

public class ListNode<T> {
    /** Данные в текушей ноде. */
    private TreeMap<Integer, T> data;

    /** Ссылки на предшествующие ноды в зависимости от версии. */
    private TreeMap<Integer, ListNode<T>> prevs;

    /** Ссылки на следующие значения в зависимости от версии. */
    private TreeMap<Integer, ListNode<T>> nexts;

    /**
     * Конструктрак класса.
     *
     * @param value Значение в ноде.
     * @param prev  Предыдущая нода.
     * @param next  Следующая нода.
    */
    public ListNode(T value, int version, ListNode<T> prev, ListNode<T> next) {
        data = new TreeMap<>();
        prevs = new TreeMap<>();
        nexts = new TreeMap<>();

        data.put(version, value);
        prevs.put(version, prev);
        nexts.put(version, next);
    }

    /**
     * Возвращает следующую ноду в зависимости от версии.
     *
     * @param version   Версия.
     */
    public ListNode<T> getNext(int version) {
        return nexts.floorEntry(version).getValue();
    }

    /**
     * Устанавливает следующую ноду для этой.
     *
     * @param version   Версия.
     * @param next      Следующая нода.
     */
    public void setNext(int version, ListNode<T> next) {
        nexts.put(version, next);
    }

    /**
     * Возвращает предыдущую ноду в зависимости от версии.
     *
     * @param version   Версия.
     */
    public ListNode<T> getPrev(int version) {
        return prevs.floorEntry(version).getValue();
    }

    /**
     * Устанавливает предыдущую ноду для этой.
     *
     * @param version   Версия.
     * @param prev      Следующая нода.
     */
    public void setPrev(int version, ListNode<T> prev) {
        prevs.put(version, prev);
    }

    /**
     *  Возвращает значение в версии.
     *
     * @param version Версия.
     */
    public T getValue(int version) { return data.floorEntry(version).getValue(); }

    /**
     *  Устанавливает значение значение в версии.
     *
     * @param version Версия.
     */
    public void setValue(int version, T obj) {
        data.put(version, obj);
    }
}
