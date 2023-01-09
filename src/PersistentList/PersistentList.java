package PersistentList;
import PersistentMassive.PersistentMassive;
import Utils.Exceptions;

import java.util.*;
import java.util.function.UnaryOperator;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class PersistentList<T> implements List {
    private int currentVersion = 0;
    private TreeMap<Integer, Integer> lengths;
    private TreeMap<Integer, ListNode<T>> heads;
    private TreeMap<Integer, ListNode<T>> tails;

    /**
     * Конструктор класса. Создаёт пустой лист.
     */
    public PersistentList() {
        heads = new TreeMap<>();
        tails = new TreeMap<>();
        lengths = new TreeMap<>();
        lengths.put(0, 0);
    }

    /**
     * Конструктор класса. Создаёт лист на основе коллекции.
     *
     * @param c Коллекция.
     */
    public PersistentList(Collection<T> c) {
        heads = new TreeMap<>();
        tails = new TreeMap<>();
        lengths = new TreeMap<>();
        lengths.put(0, c.size());

        for (T value : c) {
            add(value);
            currentVersion--;
        }
        currentVersion++;
    }

    /**
     * Возвращает текущую версию листа.
     */
    public int getVersion(){
        return currentVersion;
    }

    /**
     * Возвращает длину листа в выбранной версии.
     *
     * @param version   Версия.
     * @return          Длина листа.
     */
    public int size(int version) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        return lengths.floorEntry(version).getValue();
    }

    /**
     * Возвращает длину листа в последней версии.
     *
     * @return Длина листа.
     */
    @Override
    public int size() {
        return size(currentVersion);
    }

    /**
     * Возвращает, пустой ли лист в выбранной версии.
     *
     * @param version   Версия.
     * @return          Пустой ли лист.
     */
    public boolean isEmpty(int version) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        return size(version) == 0;
    }

    /**
     * Возвращает, пустой ли лист в последней версии.
     *
     * @return Пустой ли лист.
     */
    @Override
    public boolean isEmpty() {
        return isEmpty(currentVersion);
    }

    /**
     * Возвращает, содержит ли лист выбранной версии значение.
     *
     * @param o         Значение.
     * @param version   Версия.
     * @return          true, если содержит. Иначе false.
     */
    public boolean contains(Object o, int version) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        if (isEmpty(version))
            return false;

        ListNode<T> current = heads.floorEntry(version).getValue();
        for (int i = 0; i < size(version); i++) {
            if (current.getValue(version) == null) {
                if (o == null) return true;
            } else {
                if (current.getValue(version).equals(o))
                    return true;
            }
            current = current.getNext(version);
        }
        return false;
    }

    /**
     * Возвращает, содержит ли лист выбранной версии значение.
     *
     * @param o         Значение.
     * @return          true, если содержит. Иначе false.
     */
    @Override
    public boolean contains(Object o) {
        return contains(o, currentVersion);
    }

    /**
     * Добавляет значение в конец листа.
     *
     * @param o     Значение.
     * @return      true, если значение было добавлено. Иначе false.
     */
    @Override
    public boolean add(Object o) {
        currentVersion++;
        if (heads.size() == 0 || heads.floorEntry(currentVersion).getValue() == null) {
            ListNode<T> current = new ListNode<>((T) o, currentVersion, null, null);
            heads.put(currentVersion, current);
            tails.put(currentVersion, current);
            lengths.put(currentVersion, 1);
        } else {
            ListNode<T> prev = tails.floorEntry(currentVersion).getValue();
            ListNode<T> current = new ListNode<T>((T) o, currentVersion, prev, null);
            prev.setNext(currentVersion, current);

            tails.put(currentVersion, current);
            lengths.put(currentVersion, size(currentVersion) + 1);
        }
        return true;
    }

    /**
     * Удаляет значение с конеца листа.
     *
     * @param o     Значение.
     * @return      true, если значение было удалено. Иначе false.
     */
    @Override
    public boolean remove(Object o) {
        currentVersion++;
        ListNode<T> current = heads.floorEntry(currentVersion).getValue();
        for (int i = 0; i < size(currentVersion); i++) {
            if (current.getValue(currentVersion).equals(o)) {
                ListNode<T> prevEl = current.getPrev(currentVersion);
                ListNode<T> nextEl = current.getNext(currentVersion);
                if (prevEl != null) {
                    prevEl.setNext(currentVersion, nextEl);
                } else {
                    heads.put(currentVersion, nextEl);
                }
                if (nextEl != null) {
                    nextEl.setPrev(currentVersion, prevEl);
                } else {
                    tails.put(currentVersion, prevEl);
                }
                lengths.put(currentVersion, size(currentVersion) - 1);
                return true;
            }
            current = current.getNext(currentVersion);
        }
        return false;
    }

    /**
     * Добавляет коллекция в лист начмная с определённого значения.
     *
     * @param index     Индекс, с которого начинается добавление.
     * @param c         Коллекция.
     * @return          true, если элементы были добавлены. Иначе false.
     */
    @Override
    public boolean addAll(int index, Collection c) {
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException(Exceptions.LIST_INDEX_OUT_OF_BOUNDS);
        if (c.isEmpty())
            return false;
        currentVersion++;

        ListNode<T> current = null;
        ListNode<T> prev = null;
        if (!(heads.size() == 0 || heads.floorEntry(currentVersion).getValue() == null)) {
            current = heads.floorEntry(currentVersion).getValue();

            if (index == size()) {
                prev = tails.floorEntry(currentVersion).getValue();
                current = null;
            } else {
                for (int i = 0; i < index; i++) {
                    current = current.getNext(currentVersion);
                }

                prev = current.getPrev(currentVersion);
            }
        }
        ListNode<T> newEl = null;
        for (Object o : c) {
            newEl = new ListNode<T>((T)o, currentVersion, prev, current);
            if (null != prev) {
                prev.setNext(currentVersion, newEl);
            } else {
                heads.put(currentVersion, newEl);
            }
            if (null != current) {
                current.setPrev(currentVersion, newEl);
            } else {
                tails.put(currentVersion, newEl);
            }
            prev = newEl;
        }

        lengths.put(currentVersion, size(currentVersion) + c.size());
        return true;
    }

    /**
     * Добавляет коллекцию в конец листа.
     *
     * @param c     Коллекция.
     * @return      true, если элементы были добавлены. Иначе false.
     */
    @Override
    public boolean addAll(Collection c) {
        return addAll(size(), c);
    }

    /**
     * Удаляет все эелементы из коллекции, содержащиеся в листе.
     *
     * @param c     Коллекция.
     * @return      true, если было удалено хоть одно значение, иначе false.
     */
    @Override
    public boolean removeAll(Collection c) {
        boolean isChanged = false;
        for (Object o : c) {
            while (remove(o)) {
                isChanged = true;
                currentVersion--;
            }
        }
        return isChanged;
    }

    /**
     * Возвращает, содержит ли лист определённой версии все значения из коллекции.
     *
     * @param c         Коллекция.
     * @param version   Версия.
     * @return          true, если содержит. Иначе False.
     */
    public boolean containsAll(Collection c, int version) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        for (Object o : c) {
            if (!contains(o, version))
                return false;
        }
        return true;
    }

    /**
     * Возвращает, содержит ли лист все значения из коллекции.
     *
     * @param c         Коллекция.
     * @return          true, если содержит. Иначе False.
     */
    @Override
    public boolean containsAll(Collection c) {
        return containsAll(c, currentVersion);
    }

    /**
     * Возвращает значение по индексу для выбранной версии.
     *
     * @param index     Индекс.
     * @param version   Версия.
     * @return          Значение.
     */
    public Object get(int index, int version) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException(Exceptions.LIST_INDEX_OUT_OF_BOUNDS);

        ListNode<T> current = heads.floorEntry(version).getValue();
        for (int i = 0; i < size(); i++) {
            if (i == index)
                return current.getValue(version);
            current = current.getNext(version);
        }
        return null;
    }

    /**
     * Возвращает значение по индексу.
     *
     * @param index     Индекс.
     * @return          Значение.
     */
    @Override
    public Object get(int index) {
        return get(index, currentVersion);
    }

    /**
     * Заменяет значение в листе по индексу.
     *
     * @param index     Индекс.
     * @param element   Новое значение.
     * @return          Предыдущее значение.
     */
    @Override
    public Object set(int index, Object element) {
        currentVersion++;
        ListNode<T> current = heads.floorEntry(currentVersion).getValue();
        for (int i = 0; i < index; i++) {
            current = current.getNext(currentVersion);
        }
        Object prevObj = current.getValue(currentVersion);
        current.setValue(currentVersion, (T) element);

        return prevObj;
    }


    /**
     * Добавляет в лист значение на определённое место(не переписывает прошлое, а вставляет новое, сдвигая на 1 элемент)
     *
     * @param index     Индекс.
     * @param element   Добавленный элемент.
     */
    @Override
    public void add(int index, Object element) {
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException(Exceptions.LIST_INDEX_OUT_OF_BOUNDS);

        currentVersion++;
        ListNode<T> current = heads.floorEntry(currentVersion).getValue();
        for (int i = 0; i < index; i++) {
            current = current.getNext(currentVersion);
        }

        ListNode<T> prev = current.getPrev(currentVersion);
        ListNode<T> newEl = new ListNode<T>((T)element, currentVersion, prev, current);
        if (null != prev) {
            prev.setNext(currentVersion, newEl);
        } else {
            heads.put(currentVersion, newEl);
        }
        current.setPrev(currentVersion, newEl);
        lengths.put(currentVersion, size(currentVersion) + 1);
    }

    /**
     * Удаляет элемент по индексу.
     *
     * @param index     Индекс.
     * @return          Удалённый элемент.
     */
    @Override
    public Object remove(int index) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException(Exceptions.LIST_INDEX_OUT_OF_BOUNDS);

        ListNode<T> current = heads.floorEntry(currentVersion).getValue();
        for (int i = 0; i < index; i++) {
            current = current.getNext(currentVersion);
        }

        ListNode<T> prevEl = current.getPrev(currentVersion);
        ListNode<T> nextEl = current.getNext(currentVersion);
        currentVersion++;
        if (prevEl != null) {
            prevEl.setNext(currentVersion, nextEl);
        } else {
            heads.put(currentVersion, nextEl);
        }
        if (nextEl != null) {
            nextEl.setPrev(currentVersion, prevEl);
        } else {
            tails.put(currentVersion, prevEl);
        }
        lengths.put(currentVersion, size(currentVersion) - 1);

        return current.getValue(currentVersion--);
    }

    /**
     * Возвращает номер элемента в листе определённой версии.
     *
     * @param o         Элемент.
     * @param version   Версия.
     * @return          Номер элемента или -1, если такого элемента не содержиться.
     */
    public int indexOf(Object o, int version) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);

        int result = -1;
        if (isEmpty(version)) {
            return result;
        }

        ListNode<T> current = heads.floorEntry(version).getValue();
        for (int ind = 0; ind < size(version); ind++) {
            if (current.getValue(version).equals(o)) {
                result = ind;
                break;
            }
            current = current.getNext(version);
        }
        return result;
    }

    /**
     * Возвращает номер элемента в листе.
     *
     * @param o         Элемент.
     * @return          Номер элемента или -1, если такого элемента не содержиться.
     */
    @Override
    public int indexOf(Object o) {
        return indexOf(o, currentVersion);
    }

    public Iterator iterator(int version) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        return new Iterator() {
            ListIterator listIterator = versionedListIterator(version);

            @Override
            public boolean hasNext() {
                return listIterator.hasNext();
            }

            @Override
            public Object next() {
                return listIterator.next();
            }
        };
    }


    @Override
    public Iterator iterator() {
        return iterator(currentVersion);
    }

    public ListIterator versionedListIterator(int version, int index) {
        if (version < 0 || version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        int size = size(version);
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException(Exceptions.LIST_INDEX_OUT_OF_BOUNDS);
        System.out.println(heads.get(0)+" "+ version);
        return new ListIterator() {
            int currIndex = index - 1;
            int _version = version;
            ListNode currElement = heads.floorEntry(currentVersion).getValue();

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
                    if (currIndex >= 0)
                        currElement = currElement.getNext(_version);
                    currIndex++;
                    return currElement.getValue(_version);
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
                    if (currIndex < size)
                        currElement = currElement.getPrev(_version);
                    currIndex--;
                    return currElement.getValue(_version);
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

    /**
     * Returns a list iterator over the elements in the specified version of this list (in proper sequence).
     * @return a list iterator over the elements in the specified version of this list (in proper sequence)
     */
    public ListIterator versionedListIterator(int version) {
        return versionedListIterator(version, 0);
    }

    /**
     * Returns a list iterator over the elements in the current version of this list (in proper sequence).
     * @return a list iterator over the elements in the current version of this list (in proper sequence)
     */
    @Override
    public ListIterator listIterator() {
        return versionedListIterator(currentVersion, 0);
    }

    /**
     * Returns a list iterator over the elements in the current version of this list (in proper sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be returned by an initial call to next.
     * An initial call to previous would return the element with the specified index minus one.
     * @param index index of the first element to be returned from the list iterator (by a call to next)
     * @return a list iterator over the elements in this list (in proper sequence), starting at the specified position in the list
     */
    @Override
    public ListIterator listIterator(int index) {
        return versionedListIterator(currentVersion, index);
    }

    /**
     * Возвращает массив на основе версии.
     *
     * @param version   Версия.
     * @return          Массив.
     */
    public PersistentMassive<T> getArray(int version){
        return  new PersistentMassive<T>( heads.floorEntry(version).getValue(), version);
    }

    /**
     * Возвращает массив на основе последней версии.
     *
     * @return          Массив.
     */
    public PersistentMassive<T> getArray(){
        return getArray(currentVersion);
    }


    @Override
    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator spliterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator operator) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void sort(Comparator c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public int lastIndexOf(Object o, int version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }
}
