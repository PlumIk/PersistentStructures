package PersistentList;

import PersistentMap.PersistentMap;
import PersistentMassive.PersistentMassive;
import Utils.Exceptions;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class PersistentList<T> {
    private TreeMap<Integer, PersistentListNode<T>> head;
    private TreeMap<Integer, PersistentListNode<T>> tail;
    private Stack<PersistentList<T>> prev = new Stack<>();
    private Stack<PersistentList<T>> next = new Stack<>();
    private TreeMap<Integer, Integer> versionsLengths;
    private ArrayList<Integer> versions;
    private int version;

    /**
     * Конструктор класса. Создаёт пустой лист.
     */
    public PersistentList() {
        head = new TreeMap<>();
        tail = new TreeMap<>();
        versions = new ArrayList<Integer>();
        versions.add(0);
        versionsLengths = new TreeMap<>();
        versionsLengths.put(0, 0);
        version = 0;
    }

    /**
     * Конструктор класса. Создаёт лист на основе коллекции.
     *
     * @param c Коллекция.
     */
    public PersistentList(Collection<T> c) {
        head = new TreeMap<>();
        tail = new TreeMap<>();
        versionsLengths = new TreeMap<>();
        versionsLengths.put(0, c.size());
        versions = new ArrayList<Integer>();
        versions.add(0);
        version = 0;

        for (T value : c) {
            if (head.size() == 0 || head.floorEntry(version).getValue() == null) {
                PersistentListNode<T> current = new PersistentListNode<T>(value, version, null, null);
                head.put(version, current);
                tail.put(version, current);
                continue;
            }
            PersistentListNode<T> prevTail = tail.floorEntry(version).getValue();
            PersistentListNode<T> current = new PersistentListNode<T>(value, version, prevTail, null);
            prevTail.setNext(version, current);

            tail.put(version, current);
        }
    }

    private PersistentList(Stack<PersistentList<T>> prevs, Stack<PersistentList<T>> nexts, TreeMap<Integer, PersistentListNode<T>> head, TreeMap<Integer, PersistentListNode<T>> tail, int version, TreeMap<Integer, Integer> versionsLengths, ArrayList<Integer> versions) {
        this.head = head;
        this.tail = tail;
        this.prev = prevs;
        this.next = nexts;
        this.versionsLengths = versionsLengths;
        this.version = version;
        this.versions = versions;
    }

    /**
     * Возвращает длину листа в последней версии.
     *
     * @return Длина листа.
     */
    public int size() {
        return versionsLengths.floorEntry(version).getValue();
    }

    public int size(int v) {
        if (v < 0 || v > version)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        return versionsLengths.floorEntry(v).getValue();
    }

    /**
     * Возвращает, пустой ли лист в последней версии.
     *
     * @return Пустой ли лист.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Возвращает, содержит ли лист выбранной версии значение.
     *
     * @param o Значение.
     * @return true, если содержит. Иначе false.
     */
    public boolean contains(Object o) {
        if (isEmpty())
            return false;

        PersistentListNode<T> current = head.floorEntry(version).getValue();
        for (int i = 0; i < size(); i++) {
            if (current.getObject(version) == null) {
                if (o == null) return true;
            } else {
                if (current.getObject(version).equals(o))
                    return true;
            }
            current = current.getNext(version);
        }
        return false;
    }

    /**
     * Добавляет значение в конец листа.
     *
     * @param o Значение.
     * @return true, если значение было добавлено. Иначе false.
     */
    public PersistentList<T> add(T o) {
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = new Stack<PersistentList<T>>();
        prev.push(new PersistentList<T>(prev, next, head, tail, version, versionsLengths, versions));
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        if (head.size() == 0 || head.floorEntry(version).getValue() == null) {
            PersistentListNode<T> current = new PersistentListNode<T>(o, newVersion, null, null);
            head.put(newVersion, current);
            tail.put(newVersion, current);
            versionsLengths.put(newVersion, 1);
            return new PersistentList<T>(prev, next, head, tail, newVersion, versionsLengths, newVersions);
        }
        PersistentListNode<T> prevTail = tail.floorEntry(version).getValue();
        PersistentListNode<T> current = new PersistentListNode<T>(o, newVersion, prevTail, null);
        prevTail.setNext(newVersion, current);

        tail.put(newVersion, current);
        versionsLengths.put(newVersion, size() + 1);
        return new PersistentList<T>(prev, next, head, tail, newVersion, versionsLengths, newVersions);
    }

    /**
     * Удаляет значение из листа.
     *
     * @param o Значение.
     * @return true, если значение было удалено. Иначе false.
     */
    public PersistentList<T> remove(Object o) {
        if (isEmpty())
            return this;

        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = new Stack<PersistentList<T>>();;
        prev.push(new PersistentList<T>(prev, next, head, tail, version, versionsLengths, versions));
        PersistentListNode<T> current = head.floorEntry(version).getValue();
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        for (int i = 0; i < size(); i++) {
            if (current.getObject(version).equals(o)) {
                PersistentListNode<T> prevEl = current.getPrev(version);
                PersistentListNode<T> nextEl = current.getNext(version);
                if (prevEl != null) {
                    prevEl.setNext(newVersion, nextEl);
                } else {
                    head.put(newVersion, nextEl);
                }
                if (nextEl != null) {
                    nextEl.setPrev(newVersion, prevEl);
                } else {
                    tail.put(newVersion, prevEl);
                }
                versionsLengths.put(newVersion, size() - 1);
                ArrayList<Integer> newVersions = new ArrayList<>(versions);
                newVersions.add(newVersion);
                return new PersistentList<T>(prev, next, head, tail, newVersion, versionsLengths, newVersions);
            }
            current = current.getNext(version);
        }
        return this;
    }

    /**
     * Добавляет коллекция в лист начмная с определённого значения.
     *
     * @param index Индекс, с которого начинается добавление.
     * @param c     Коллекция.
     * @return Новый лист.
     */
    public PersistentList<T> addAll(int index, Collection<T> c) {
        if (c.isEmpty())
            return this;
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        Stack<PersistentList<T>> prevs = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> nexts = new Stack<PersistentList<T>>();;
        prev.push(new PersistentList<T>(prevs, nexts, head, tail, version, versionsLengths, versions));
        PersistentListNode<T> current = null;
        PersistentListNode<T> prev = null;
        if (!(head.size() == 0 || head.floorEntry(version).getValue() == null)) {
            current = head.floorEntry(version).getValue();

            if (index == size()) {
                prev = tail.floorEntry(version).getValue();
                current = null;
            } else {
                for (int i = 0; i < index; i++) {
                    current = current.getNext(version);
                }

                prev = current.getPrev(version);
            }
        }
        PersistentListNode<T> newEl = null;
        for (Object o : c) {
            newEl = new PersistentListNode<T>((T)o, newVersion, prev, current);
            if (null != prev) {
                prev.setNext(newVersion, newEl);
            } else {
                head.put(newVersion, newEl);
            }
            if (null != current) {
                current.setPrev(newVersion, newEl);
            } else {
                tail.put(newVersion, newEl);
            }
            prev = newEl;
        }

        versionsLengths.put(newVersion, size(version) + c.size());
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        return new PersistentList<T>(prevs, nexts, head, tail, newVersion, versionsLengths, newVersions);
    }

    /**
     * Добавляет коллекцию в конец листа.
     *
     * @param c Коллекция.
     * @return Новый лист.
     */
    public PersistentList<T> addAll(Collection<T> c) {
        return addAll(size(), c);
    }

    /**
     * Возвращает, содержит ли лист все значения из коллекции.
     *
     * @param c Коллекция.
     * @return true, если содержит. Иначе False.
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
     * @param c Коллекция.
     * @return true, если было удалено хоть одно значение, иначе false.
     */
    public PersistentList<T> removeAll(Collection<T> c) {
        if (isEmpty())
            return this;
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = new Stack<PersistentList<T>>();;
        prev.push(new PersistentList<T>(prev, next, head, tail, version, versionsLengths, versions));
        PersistentList<T> res = this;
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        boolean containAny = false;
        for (Object o : c) {
            if (contains(o))
                while (remove(o) != res) {
                    PersistentListNode<T> current = head.floorEntry(version).getValue();
                    for (int i = 0; i < size(); i++) {
                        if (current.getObject(version).equals(o)) {
                            PersistentListNode<T> prevEl = current.getPrev(version);
                            PersistentListNode<T> nextEl = current.getNext(version);
                            if (prevEl != null) {
                                prevEl.setNext(newVersion, nextEl);
                            } else {
                                head.put(newVersion, nextEl);
                            }
                            if (nextEl != null) {
                                nextEl.setPrev(newVersion, prevEl);
                            } else {
                                tail.put(newVersion, prevEl);
                            }
                            if (versionsLengths.get(newVersion) == null)  {
                                versionsLengths.put(newVersion, size() - 1);
                            } else {
                                versionsLengths.put(newVersion, versionsLengths.get(newVersion) - 1);
                            }

                            res = new PersistentList<T>(prev, next, head, tail, newVersion, versionsLengths, newVersions);
                            continue;
                        }
                        current = current.getNext(version);
                    }
                }
        }
        return res;
    }

    /**
     * Возвращает значение по индексу.
     *
     * @param index Индекс.
     * @return Значение.
     */
    public Object get(int index) {
        if (index < 0 || index >= size())
            return Exceptions.LIST_INDEX_OUT_OF_BOUNDS;

        PersistentListNode<T> current = head.floorEntry(version).getValue();
        for (int i = 0; i < size(); i++) {
            if (i == index)
                return current.getObject(version);
            current = current.getNext(version);
        }
        return null;
    }

    /**
     * Заменяет значение в листе по индексу.
     *
     * @param index   Индекс.
     * @param element Новое значение.
     * @return Предыдущее значение.
     */
    public PersistentList<T> set(int index, T element) {
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = new Stack<PersistentList<T>>();;
        prev.push(new PersistentList<T>(prev, next, head, tail, version, versionsLengths, versions));
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);

        PersistentListNode<T> current = head.floorEntry(version).getValue();
        for (int i = 0; i < index; i++) {
            current = current.getNext(version);
        }
        current.setObject(newVersion, (T) element);

        return new PersistentList<T>(prev, next, head, tail, newVersion, versionsLengths, newVersions);
    }


    /**
     * Добавляет в лист значение на определённое место(не переписывает прошлое, а вставляет новое, сдвигая на 1 элемент)
     *
     * @param index   Индекс.
     * @param element Добавленный элемент.
     */
    public PersistentList<T> add(int index, T element) {
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = new Stack<PersistentList<T>>();;
        prev.push(new PersistentList<T>(prev, next, head, tail, version, versionsLengths, versions));
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);

        if (index == size()) {
            return add(element);
        }

        PersistentListNode<T> current = head.floorEntry(version).getValue();
        for (int i = 0; i < index; i++) {
            current = current.getNext(version);
        }

        PersistentListNode<T> prevs = current.getPrev(version);
        PersistentListNode<T> newEl = new PersistentListNode<T>(element, newVersion, prevs, current);
        if (null != prevs) {
            prevs.setNext(newVersion, newEl);
        } else {
            head.put(newVersion, newEl);
        }
        current.setPrev(newVersion, newEl);
        versionsLengths.put(version, size(version) + 1);
        return new PersistentList<T>(prev, next, head, tail, newVersion, versionsLengths, newVersions);
    }

    /**
     * Удаляет элемент по индексу.
     *
     * @param index Индекс.
     * @return Удалённый элемент.
     */
    public PersistentList<T> remove(int index) {
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = new Stack<PersistentList<T>>();;
        prev.push(new PersistentList<T>(prev, next, head, tail, version, versionsLengths, versions));
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);

        PersistentListNode<T> current = head.floorEntry(version).getValue();
        for (int i = 0; i < index; i++) {
            current = current.getNext(version);
        }

        PersistentListNode<T> prevEl = current.getPrev(version);
        PersistentListNode<T> nextEl = current.getNext(version);
        if (prevEl != null) {
            prevEl.setNext(newVersion, nextEl);
        } else {
            head.put(newVersion, nextEl);
        }
        if (nextEl != null) {
            nextEl.setPrev(newVersion, prevEl);
        } else {
            tail.put(newVersion, prevEl);
        }
        versionsLengths.put(newVersion, size() - 1);

        return new PersistentList<T>(prev, next, head, tail, newVersion, versionsLengths, newVersions);
    }

    /**
     * Возвращает номер элемента в листе.
     *
     * @param o Элемент.
     * @return Номер элемента или -1, если такого элемента не содержиться.
     */
    public int indexOf(Object o) {
        int result = -1;
        if (isEmpty()) {
            return result;
        }

        PersistentListNode<T> current = head.floorEntry(version).getValue();
        for (int ind = 0; ind < size(); ind++) {
            if (current.getObject(version).equals(o)) {
                result = ind;
                break;
            }
            current = current.getNext(version);
        }
        return result;
    }

    public Iterator iterator(int index) {
        int size = size(version);
        return new ListIterator() {
            int currIndex = index - 1;

            int _version = version;
            PersistentListNode currElement = head.floorEntry(_version).getValue();

            {
                for (int i = 0; i < index; i++) {
                    currElement = currElement.getNext(_version);
                }
            }


            @Override
            public boolean hasNext() {
                if (currIndex < 0) return currElement != null;
                return currElement.getNext(_version) != null;
            }

            @Override
            public Object next() {
                if (hasNext()) {
                    if (currIndex >= 0) {
                        for (int i = versions.size() - 1; i >= 0; i--) {
                            Integer v = versions.get(i);
                            if (currElement.getNextIsVersion(v) != null) {
                                currElement = currElement.getNextIsVersion(v);
                                break;
                            }
                        }
                    }
                    currIndex++;
                    for (int i = versions.size()-1; i >= 0; i--) {
                        Integer v = versions.get(i);
                        if (currElement.isVersion(v))
                        {
                            return currElement.getObject(v);
                        }
                    }
                    return null;
                } else
                    throw new NoSuchElementException(Exceptions.NO_SUCH_ELEMENT);
            }

            @Override
            public boolean hasPrevious() {
                if (currIndex >= size) return currElement != null;
                return currElement.getPrev(_version) != null;
            }

            @Override
            public Object previous() {
                if (hasPrevious()) {
                    if (currIndex < size) {
                        for (int i = versions.size() - 1; i >= 0; i--) {
                            Integer v = versions.get(i);
                            if (currElement.getPrevIsVersion(v) != null) {
                                currElement = currElement.getPrevIsVersion(v);
                                break;
                            }
                        }
                    }
                    currIndex--;
                    for (int i = versions.size()-1; i >= 0; i--) {
                        Integer v = versions.get(i);
                        if (currElement.isVersion(v))
                        {
                            return currElement.getObject(v);
                        }
                    }
                    return null;
                } else
                    throw new NoSuchElementException(Exceptions.NO_SUCH_ELEMENT);
            }

            @Override
            public int nextIndex() {
                return min(currIndex + 1, size);
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

    public Iterator iterator() {
        return iterator(0);
    }

    public PersistentList<T> Undo() {
        if (prev.isEmpty()) {
            return this;
        }
        PersistentList<T> currentList = prev.pop();
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>) this.next.clone();
        next.push(this);
        return new  PersistentList<T>(prev, next, currentList.head, currentList.tail, currentList.version, currentList.versionsLengths, currentList.versions);
    }

    public PersistentList<T> Redo() {
        if (next.isEmpty()) {
            return this;
        }
        PersistentList<T> currentList = next.pop();
        Stack<PersistentList<T>> prev = (Stack<PersistentList<T>>) this.prev.clone();
        Stack<PersistentList<T>> next = (Stack<PersistentList<T>>) this.next.clone();
        prev.push(this);
        return new  PersistentList<T>(prev, next, currentList.head, currentList.tail, currentList.version, currentList.versionsLengths, currentList.versions);
    }

    /**
     * Возвращает копию листа в виде массива.
     *
     * @return Массив.
     */
    /*public PersistentMassive<T> toArray() {
        return new PersistentMassive<T>(this.head);
    }*/

}
