package PersistentMassive;

import PersistentList.ListNode;
import Utils.Exceptions;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class PersistentMassive <T>{

    /** Текущая версия массива. */
    private int currentVersion = 0;

    /** Длина массива в зависимости от версии. */
    private TreeMap<Integer, Integer> lengths;

    /** Данные в зависимости от версии. */
    private ArrayList<TreeMap <Integer, T>> data;

    /**
     * Конструктор класса. Создаёт массив с одним элементом null.
     */
    public PersistentMassive() {
        data = new ArrayList<>();
        data.add(new TreeMap<>());
        data.get(0).put(currentVersion, null);
        lengths = new TreeMap<>();
        lengths.put(currentVersion, 1);
    }

    /**
     * Конструктор класса. Создаёт массив заданной длины.
     *
     * @param capacity Длина.
     */
    public PersistentMassive(int capacity) {
        data = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            data.add(new TreeMap<>());
            data.get(i).put(currentVersion, null);
        }
        lengths = new TreeMap<>();
        lengths.put(currentVersion, capacity);
    }

    /**
     * Конструктор класса. Создаёт массив наоснове листа.
     *
     * @param node      Головная нода.
     * @param version   Версия ноды.
     */
    public PersistentMassive(ListNode<T> node, int version){
        data = new ArrayList<>();
        int i=0;
        while (node!=null){
            data.add(new TreeMap<>());
            data.get(i).put(currentVersion, node.getValue(version));
            i++;
            node=node.getNext(version);
        }
        lengths = new TreeMap<>();
        lengths.put(currentVersion, i);

    }

    /**
     * Возвращает значение на выбранном месте в выбранной версии.
     *
     * @param index     Место значения.
     * @param version   Версия.
     * @return          Значение.
     */
    public T get(int index, int version) {
        if (version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        if (lengths.floorEntry(version).getValue() <= index)
            throw new ArrayIndexOutOfBoundsException(Exceptions.ARRAY_INDEX_OUT_OF_BOUNDS);
        return data.get(index).floorEntry(version).getValue();
    }

    /**
     * Возвращает текущую версию массива.
     */
    public int getVersion(){
        return currentVersion;
    }

    /**
     * Возвращает значение на выбранном месте в последней версии.
     *
     * @param index Место значения.
     * @return      Значение.
     */
    public T get(int index) {
        return get(index, currentVersion);
    }

    /**
     * Вставляет на выбранное место значение.
     *
     * @param index Место значения.
     * @param value Значение.
     * @return      Номер новой версии массива.
     */
    public int set(int index, T value) {
        int curLen = lengths.floorEntry(currentVersion).getValue();
        if (curLen <= index)
            throw new ArrayIndexOutOfBoundsException(Exceptions.ARRAY_INDEX_OUT_OF_BOUNDS);
        currentVersion++;
        data.get(index).put(currentVersion, value);
        return currentVersion;
    }

    /**
     * Возвращает длину массива в выбранной версии.
     *
     * @param version   Версия.
     * @return          Длина массива.
     */
    public int size(int version) {
        if (version > currentVersion)
            throw new NoSuchElementException(Exceptions.NO_SUCH_VERSION);
        return lengths.floorEntry(version).getValue();
    }

    /**
     * Возвращает длину массива в последней версии..
     *
     * @return длина массива.
     */
    public int size() {
        return size(currentVersion);
    }

    /**
     * Добавляет значение в конец массива.
     *
     * @param   value значение.
     * @return  Номер новой версии массива.
     */
    public int add(T value) {
        int curLen = size();
        if (curLen >= data.size()) {
            data.add(new TreeMap<>());
        }
        currentVersion++;
        data.get(curLen).put(currentVersion, value);
        lengths.put(currentVersion, curLen + 1);
        return currentVersion;
    }

    /**
     * Удаляет значение в с конца массива.
     *
     * @return Номер новой версии массива.
     */
    public int remove() {
        int curLen = size();
        if (curLen == 0) {
            throw new ArrayIndexOutOfBoundsException(Exceptions.NOTHING_TO_REMOVE);
        }
        currentVersion++;
        lengths.put(currentVersion, curLen - 1);
        return currentVersion;
    }
}
