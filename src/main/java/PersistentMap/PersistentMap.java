package PersistentMap;

import PersistentList.PersistentList;
import PersistentMassive.PersistentMassive;
import Utils.Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PersistentMap<K, V> {
    private class PersistentMapEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public PersistentMapEntry() {
            this.key = null;
            this.value = null;
        }

        public PersistentMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    private int version;
    private Stack<PersistentMap<K, V>> prev = new Stack<PersistentMap<K, V>>();
    private Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
    private TreeMap<K,MapNode<V>> data;
    private TreeMap<Integer, Integer> versionsLengths;
    private ArrayList<Integer> versions;

    /**
     * Конструктор класса. Создаёт пустой словарь.
     */
    public PersistentMap() {
        version = 0;
        data = new TreeMap<>();
        versions = new ArrayList<Integer>();
        versions.add(0);
        this.versionsLengths = new TreeMap<>();
        versionsLengths.put(0, 0);
    }

    private PersistentMap(Stack<PersistentMap<K, V>> prevs, Stack<PersistentMap<K, V>> nexts, TreeMap<K, MapNode<V>> data, int version, TreeMap<Integer, Integer> versionsLengths, ArrayList<Integer> versions) {
        this.versionsLengths = versionsLengths;
        this.data = data;
        this.prev = prevs;
        this.next = nexts;
        this.version = version;
        this.versions = versions;
    }

    /**
     * Возвращает длину словаря в последней версии.
     *
     * @return Длина словаря.
     */
    public int size() {
        return versionsLengths.floorEntry(version).getValue();
    }

    /**
     * Возвращает, пустой ли словарь в последней версии.
     *
     * @return Пустой ли словарь.
     */
    public boolean isEmpty() {
        return size() == 0;
    }


    /**
     * Возвращает, содержит ли словарь выбранной версии ключ.
     *
     * @param key Ключ.
     * @return true, если содержит. Иначе false.
     */
    public boolean containsKey(Object key) {
        if (data.containsKey(key)) {
            MapNode node = data.get(key);
            if (!node.isRemoved(version)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает, содержит ли словарь выбранной версии значение.
     *
     * @param value Значение.
     * @return true, если содержит. Иначе false.
     */
    public boolean containsValue(Object value) {
        for (MapNode<V> node : data.values()) {
            if (!node.isRemoved(version)) {
                if (null == value) {
                    if (node.getObject(version) == null)
                        return true;
                } else {
                    if (value.equals(node.getObject(version)))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Возвращает значение по ключу.
     *
     * @param key Ключ.
     * @return Значение.
     */
    public Object get(Object key) {
        if (!data.containsKey(key))
            return null;
        MapNode node = data.get(key);
        for (int i = versions.size()-1; i >= 0; i--) {
            Integer v = versions.get(i);
            if (node.isVersion(v))  {
                if (!node.isRemoved(v))  {
                    return node.getObject(v);
                }
                return null;
            }
        }
        return null;
    }

    /**
     * Связывает указанное значение с указанным ключом.
     *
     * @param key   Ключ.
     * @param value Значение.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> put(Object key, Object value) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();;
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        if (null == node) {
            data.put((K) key, new MapNode<V>((V) value, newVersion));
            versionsLengths.put(newVersion, currSize + 1);
        } else {;
            node.setObject(newVersion, value);
            if (currSize == 0) {
                currSize++;
            }
            versionsLengths.put(newVersion, currSize);
        }
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Удаляет сопоставление для ключа.
     *
     * @param key Ключ.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> remove(Object key) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();;
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        if (null != node) {
            node.removeObject(newVersion);
            int currSize = versionsLengths.floorEntry(newVersion).getValue();
            versionsLengths.put(newVersion, currSize - 1);
            ArrayList<Integer> newVersions = new ArrayList<>(versions);
            newVersions.add(newVersion);
            return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
        }
        return this;
    }

    /**
     * Копирует все сопоставления с указанного словаря на этот словарь.
     *
     * @param m Словарь.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> putAll(Map m) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();;
        int newVersion = versionsLengths.lastKey();
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        newVersion++;
        for (Object entry : m.entrySet()) {
            K key = ((Map.Entry<K, V>) entry).getKey();
            V value = ((Map.Entry<K, V>) entry).getValue();

            MapNode node = data.get(key);
            if (null == node) {
                data.put(key, new MapNode<V>(value, newVersion));
                currSize++;
            } else {
                node.setObject(newVersion, value);
            }
        }
        versionsLengths.put(newVersion, currSize);
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Возвращает набор ключей, содержащихся на этом словаре.
     *
     * @return Набор ключей.
     */
    public Set keySet() {
        Set<K> keys = data.keySet();
        Set<K> resultKeys = new HashSet<>(data.keySet());

        for (K key : keys) {
            if (this.get(key) == null) {
                resultKeys.remove(key);
            }
        }
        return resultKeys;
    }

    /**
     * Возвращает коллекцию значений, содержащихся на этом словаре.
     *
     * @return Коллекцию значений.
     */
    public Collection values() {
        LinkedList<V> result = new LinkedList<V>();
        for (Map.Entry<K, MapNode<V>> entry : data.entrySet()) {
            if (!entry.getValue().isRemoved(version)) {
                result.add(entry.getValue().getObject(version));
            }
        }
        return result;
    }

    /**
     * Возвращает набор сопоставлений, содержащихся в этом словаре.
     *
     * @return Набор сопоставалений.
     */
    public Set<Map.Entry> entrySet() {
        Set<Map.Entry> result = new HashSet<>();

        for (Map.Entry<K, MapNode<V>> entry : data.entrySet()) {
            if (!entry.getValue().isRemoved(version)) {
                result.add(new PersistentMapEntry<>(entry.getKey(), entry.getValue().getObject(version)));
            }
        }
        return result;
    }

    /**
     * Возвращает значение, которому сопоставлен указанный ключ, или defaultValue если этот словарь не содержит сопоставления для ключа.
     *
     * @param key          Ключ.
     * @param defaultValue Значение.
     * @return Значение.
     */
    public Object getOrDefault(Object key, Object defaultValue) {
        if (data.get(key) != null && !data.get(key).isRemoved(version)) {
            return data.get(key).getObject(version);
        }
        return defaultValue;
    }

    /**
     * Выполняет данное действие для каждой записи в этом словаре до тех пор, пока все записи не будут обработаны.
     *
     * @param action Действие.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> forEach(BiConsumer action) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        int newVersion = versionsLengths.lastKey();
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        newVersion++;
        for (Map.Entry<K, MapNode<V>> entry : data.entrySet()) {
            if (!entry.getValue().isRemoved(version)) {
                action.accept(entry.getKey(), entry.getValue().getObject(version));
            }
        }
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        versionsLengths.put(newVersion, currSize);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Заменяет значение каждой записи результатом вызова данной функции для этой записи.
     *
     * @param function Функция.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> replaceAll(BiFunction function) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        int newVersion = versionsLengths.lastKey();
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        newVersion++;
        for (Map.Entry<K, MapNode<V>> entry : data.entrySet()) {
            if (!entry.getValue().isRemoved(version)) {
                entry.getValue().setObject(newVersion, (V)function.apply(entry.getKey(), entry.getValue().getObject(version)));
            }
        }
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        versionsLengths.put(newVersion, currSize);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Если указанный ключ еще не связан со значением (или сопоставлен со null) связывает его с заданным значением.
     *
     * @param key   Ключ.
     * @param value Значение.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> putIfAbsent(Object key, Object value) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        Object oldValue = null;
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        newVersion++;
        if (null == node) {
            data.put((K)key, new MapNode<V>((V)value, newVersion));
            currSize++;
        } else {
            oldValue = node.getObject(version);
            if (null == oldValue) {
                if (node.isRemoved(version)) {
                    currSize++;
                }
                node.setObject(newVersion, value);
            } else {
                return this;
            }
        }
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        versionsLengths.put(newVersion, currSize);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Удаляет запись для указанного ключа только в том случае, если он в данный момент сопоставлен с указанным значением.
     *
     * @param key   Ключ.
     * @param value Значение.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> remove(Object key, Object value) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        MapNode node = data.get(key);
        int curSize = size();
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        if (null != node && node.getObject(version).equals(value) && !node.isRemoved(version)) {
            node.removeObject(newVersion);
            versionsLengths.put(newVersion, curSize - 1);
            ArrayList<Integer> newVersions = new ArrayList<>(versions);
            newVersions.add(newVersion);
            return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
        }
        return this;
    }

    /**
     * Заменяет запись для указанного ключа только в том случае, если в данный момент она сопоставлена с указанным значением.
     *
     * @param key      Ключ.
     * @param oldValue Старое значение.
     * @param newValue Новое значение.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> replace(Object key, Object oldValue, Object newValue) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        newVersion++;
        if (null != node && null != node.getObject(version) &&
                !node.isRemoved(version) && node.getObject(version).equals(oldValue)) {
            node.setObject(newVersion, newValue);
            ArrayList<Integer> newVersions = new ArrayList<>(versions);
            newVersions.add(newVersion);
            versionsLengths.put(newVersion, currSize);
            return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
        }
        return this;
    }

    /**
     * Заменяет запись для указанного ключа только в том случае, если он в данный момент сопоставлен с некоторым значением.
     *
     * @param key   Ключ.
     * @param value Значение.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> replace(Object key, Object value) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths,versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        newVersion++;
        if (null != node && !node.isRemoved(version)) {
            node.setObject(newVersion, value);
            ArrayList<Integer> newVersions = new ArrayList<>(versions);
            newVersions.add(newVersion);
            versionsLengths.put(newVersion, currSize);
            return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
        }
        return this;
    }

    /**
     * Если указанный ключ еще не связан со значением (или сопоставлен со null), пытается вычислить его значение с помощью данной функции отображения.
     *
     * @param key             Ключ.
     * @param mappingFunction Функция отображения.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> computeIfAbsent(Object key, Function mappingFunction) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        if (null != node && !node.isRemoved(version) && node.getObject(version) != null) {
            return this;
        }

        Object value = mappingFunction.apply(key);
        if (null != value) {
            if (null == node) {
                data.put((K)key, new MapNode<V>((V)value, newVersion));
                currSize++;
            } else {
                if (node.isRemoved(version)) {
                    currSize++;
                }
                node.setObject(newVersion, value);
            }
            ArrayList<Integer> newVersions = new ArrayList<>(versions);
            newVersions.add(newVersion);
            versionsLengths.put(newVersion, currSize);
            return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
        }

        return this;
    }

    /**
     * Если значение для указанного ключа присутствует и не равно нулю, выполняется попытка вычислить новое сопоставление с учетом ключа и его текущего сопоставленного значения.
     *
     * @param key               Ключ.
     * @param remappingFunction Функция.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> computeIfPresent(Object key, BiFunction remappingFunction) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        if (null == node || node.isRemoved(version) || node.getObject(version) == null) {
            return this;
        }

        Object oldValue = node.getObject(version);
        Object value = remappingFunction.apply(key, oldValue);
        if (null != value) {
            node.setObject(newVersion, value);
        } else {
            node.removeObject(newVersion);
            currSize--;
        }
        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        versionsLengths.put(newVersion, currSize);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * Пытается вычислить сопоставление для указанного ключа и его текущего сопоставленного значения.
     *
     * @param key               Ключ.
     * @param remappingFunction Функция.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> compute(Object key, BiFunction remappingFunction) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        Object oldValue = null;
        if (null != node && !node.isRemoved(version)) {
            oldValue = node.getObject(version);
        }

        Object value = remappingFunction.apply(key, oldValue);
        if (null != value) {
            node.setObject(newVersion, value);
        } else {
            if (null != oldValue) {
                node.removeObject(newVersion);
                currSize--;
            }
        }

        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        versionsLengths.put(newVersion, currSize);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    /**
     * СЕсли указанный ключ еще не связан со значением или связан с null, свяжите его с заданным ненулевым значением. В противном случае заменяет связанное значение результатами данной функции переназначения или удаляет, если результат null.
     *
     * @param key               Ключ.
     * @param value             Значение.
     * @param remappingFunction Функция.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> merge(Object key, Object value, BiFunction remappingFunction) {
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(new PersistentMap<K, V>(prev, next, data, version, versionsLengths, versions));
        Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
        MapNode node = data.get(key);
        int newVersion = versionsLengths.lastKey();
        newVersion++;
        int currSize = versionsLengths.floorEntry(newVersion).getValue();
        if (null == node || node.isRemoved(version) || node.getObject(version) == null) {
            node.setObject(newVersion, value);
            ArrayList<Integer> newVersions = new ArrayList<>(versions);
            newVersions.add(newVersion);
            versionsLengths.put(newVersion, currSize);
            return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
        }

        Object oldValue = node.getObject(version);
        Object newValue = remappingFunction.apply(key, oldValue);

        if (null != newValue) {
            node.setObject(newVersion, newValue);
        } else {
            node.removeObject(newVersion);
            currSize--;
        }

        ArrayList<Integer> newVersions = new ArrayList<>(versions);
        newVersions.add(newVersion);
        versionsLengths.put(newVersion, currSize);
        return new PersistentMap<K, V>(prev, next, data, newVersion, versionsLengths, newVersions);
    }

    public PersistentMap<K, V> Undo() {
        if (prev.isEmpty()) {
            return this;
        }
        PersistentMap<K, V> currentMap = prev.pop();
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        next.push(this);
        return new PersistentMap<K, V>(prev, next, currentMap.data, currentMap.version, currentMap.versionsLengths, currentMap.versions);
    }

    public PersistentMap<K, V> Redo() {
        if (next.isEmpty()) {
            return this;
        }
        PersistentMap<K, V> currentMap = next.pop();
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        prev.push(this);
        return new PersistentMap<K, V>(prev, next, currentMap.data, currentMap.version, currentMap.versionsLengths, currentMap.versions);
    }
}
