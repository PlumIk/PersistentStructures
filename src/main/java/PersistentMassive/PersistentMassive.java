package PersistentMassive;

import PersistentList.ListNode;
import PersistentMap.MapNode;
import PersistentMap.PersistentMap;
import Share.Node;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

public class PersistentMassive<T> {

    /**
     * Данные в зависимости от версии.
     */
    private ArrayList<Node<T>> data = new ArrayList<>();
    private int version = 0;
    private TreeMap<Integer, Integer> versionsLengths;
    private ArrayList<Integer> versions;
    private Stack<PersistentMassive<T>> prev = new Stack<>();
    private Stack<PersistentMassive<T>> next = new Stack<>();

    /**
     * Конструктор класса. Создаёт массив с одним элементом null.
     */
    public PersistentMassive() {
        data.add(new Node<>());
        data.get(0).setObject(version, null);
        versionsLengths = new TreeMap<>();
        versionsLengths.put(version, 1);
        versions = new ArrayList<Integer>();
        versions.add(version);
    }

    /**
     * Конструктор класса. Создаёт массив заданной длины.
     *
     * @param capacity Длина.
     */
    public PersistentMassive(int capacity) {
        for (int i = 0; i < capacity; i++) {
            data.add(new Node<>());
            data.get(i).setObject(version, null);
        }
        versionsLengths = new TreeMap<>();
        versionsLengths.put(version, capacity);
        versions = new ArrayList<Integer>();
        versions.add(version);
    }

    /*private PersistentMassive(Stack<PersistentMassive<T>> prevs, Stack<PersistentMassive<T>> nexts, Node<T> value, int index, int last) {
        PersistentMassive<T> prev = prevs.pop();
        for (int i = 0; i < last; i++) {
            data.add(new Node<>(prev.get(i)));
        }

        if (value != null) {
            if (index < prev.length) {
                data.add(index, value);
            } else {
                data.add(value);
                last++;
            }
        }
        this.prev = prevs;
        this.next = nexts;
    }*/

    private PersistentMassive(Stack<PersistentMassive<T>> prevs, Stack<PersistentMassive<T>> nexts, ArrayList<Node<T>> data, int version, TreeMap<Integer, Integer> versionsLengths, ArrayList<Integer> versions) {
        this.versionsLengths = versionsLengths;
        this.data = data;
        this.prev = prevs;
        this.next = nexts;
        this.version = version;
        this.versions = versions;
    }

    /**
     * Возвращает значение на выбранном месте в выбранной версии.
     *
     * @param index Место значения.
     * @return Значение.
     */
    public T get(int index) {
        Node<T> node = data.get(index);
        for (int i = versions.size()-1; i >= 0; i--) {
            Integer v = versions.get(i);
            if (!node.isRemoved(v) && node.isVersion(v))  {
                return node.getData(version);
            }
        }
        return this.get(index+1);
    }

    /**
     * Вставляет на выбранное место значение.
     *
     * @param index Место значения.
     * @param value Значение.
     * @return Новый массив.
     */
    public PersistentMassive<T> set(int index, T value) {
        Stack<PersistentMassive<T>> prev = (Stack<PersistentMassive<T>>) this.prev.clone();
        Stack<PersistentMassive<T>> next = (Stack<PersistentMassive<T>>) this.next.clone();
        prev.push(this);
        //prev.push(this);
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        data.get(index).setObject(newVersion, value);
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        return new PersistentMassive<T>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Возвращает длину массива в последней версии..
     *
     * @return длина массива.
     */
    public int size() {
        return versionsLengths.floorEntry(version).getValue();
    }

    /**
     * Добавляет значение в конец массива.
     *
     * @param value значение.
     * @return Новый массив.
     */
    public PersistentMassive<T> add(T value) {
        Stack<PersistentMassive<T>> prev = (Stack<PersistentMassive<T>>) this.prev.clone();
        Stack<PersistentMassive<T>> next = (Stack<PersistentMassive<T>>) this.next.clone();
        prev.push(this);
        //prev.push(this);

        int curLen = size();
        if (curLen >= data.size()) {
            data.add(new Node<>());
        }
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        data.get(curLen).setObject(newVersion, value);
        versionsLengths.put(newVersion, curLen + 1);
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        return new PersistentMassive<T>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Удаляет значение в с конца массива.
     *
     * @return Номер новой версии массива.
     */
    public PersistentMassive<T> remove() {
        Stack<PersistentMassive<T>> prev = (Stack<PersistentMassive<T>>) this.prev.clone();
        Stack<PersistentMassive<T>> next = (Stack<PersistentMassive<T>>) this.next.clone();
        prev.push(this);
        //prev.push(this);
        int curLen = size();
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        versionsLengths.put(newVersion, curLen - 1);
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        return new PersistentMassive<T>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    public PersistentMassive<T> Undo() {
        if (prev.isEmpty()) {
            return this;
        }

        PersistentMassive<T> currentMas = prev.pop();
        Stack<PersistentMassive<T>> prev = (Stack<PersistentMassive<T>>) this.prev.clone();
        Stack<PersistentMassive<T>> next = (Stack<PersistentMassive<T>>) this.next.clone();
        next.push(this);
        return new PersistentMassive<T>(prev, next, currentMas.data, currentMas.version, currentMas.versionsLengths, currentMas.versions);
    }

    public PersistentMassive<T> Redo() {
        if (next.isEmpty()) {
            return this;
        }

        PersistentMassive<T> currentMas = next.pop();
        Stack<PersistentMassive<T>> prev = (Stack<PersistentMassive<T>>) this.prev.clone();
        Stack<PersistentMassive<T>> next = (Stack<PersistentMassive<T>>) this.next.clone();
        prev.push(this);
        return new PersistentMassive<T>(prev, next, currentMas.data, currentMas.version, currentMas.versionsLengths, currentMas.versions);
    }
}
