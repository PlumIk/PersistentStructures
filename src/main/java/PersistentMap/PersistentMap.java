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

    private final int length;
    private Stack<PersistentMap<K, V>> prev = new Stack<PersistentMap<K, V>>();
    private Stack<PersistentMap<K, V>> next = new Stack<PersistentMap<K, V>>();
    private TreeMap<K, MapNode<V>> data;

    /**
     * Конструктор класса. Создаёт пустой словарь.
     */
    public PersistentMap() {
        length = 0;
        data = new TreeMap<>();
    }

    private PersistentMap(Stack<PersistentMap<K, V>> prevs, Stack<PersistentMap<K, V>> nexts, TreeMap<K, MapNode<V>> data, int size) {
        this.data = data;
        length = size;
        this.prev = prevs;
        this.next = nexts;
    }

    /**
     * Возвращает длину словаря в последней версии.
     *
     * @return Длина словаря.
     */
    public int size() {
        return length;
    }

    /**
     * Возвращает, пустой ли словарь в последней версии.
     *
     * @return Пустой ли словарь.
     */
    public boolean isEmpty() {
        return length == 0;
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
            if (!node.isRemoved()) {
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
            if (!node.isRemoved()) {
                if (null == value) {
                    if (node.getObject() == null)
                        return true;
                } else {
                    if (value.equals(node.getObject()))
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
        if (!node.isRemoved()) {
            return node.getObject();
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
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        MapNode node = newData.get(key);
        int currSize = length;
        if (null == node) {
            newData.put((K) key, new MapNode<V>((V) value));
            currSize = currSize + 1;
        } else {
            if (node.isRemoved()) {
                currSize = currSize + 1;
            }
            node.setObject(value);
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, currSize);
    }

    /**
     * Удаляет сопоставление для ключа.
     *
     * @param key Ключ.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> remove(Object key) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);
        int currSize = length;
        if (null != node) {
            node.removeObject();
            currSize = currSize - 1;
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, currSize);
    }

    /**
     * Копирует все сопоставления с указанного словаря на этот словарь.
     *
     * @param m Словарь.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> putAll(Map m) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        int currSize = length;
        for (Object entry : m.entrySet()) {
            K key = ((Map.Entry<K, V>) entry).getKey();
            V value = ((Map.Entry<K, V>) entry).getValue();

            MapNode node = newData.get(key);
            if (null == node) {
                newData.put(key, new MapNode<V>(value));
                currSize = currSize + 1;
            } else {
                node.setObject(value);
            }
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, currSize);
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
            if (data.get(key).isRemoved()) {
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
            if (!entry.getValue().isRemoved()) {
                result.add(entry.getValue().getObject());
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
            if (!entry.getValue().isRemoved()) {
                result.add(new PersistentMapEntry<>(entry.getKey(), entry.getValue().getObject()));
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

        if (data.get(key) != null && !data.get(key).isRemoved()) {
            return data.get(key).getObject();
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
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        for (Map.Entry<K, MapNode<V>> entry : newData.entrySet()) {
            if (!entry.getValue().isRemoved()) {
                action.accept(entry.getKey(), entry.getValue().getObject());
            }
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, length);
    }

    /**
     * Заменяет значение каждой записи результатом вызова данной функции для этой записи.
     *
     * @param function Функция.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> replaceAll(BiFunction function) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        for (Map.Entry<K, MapNode<V>> entry : newData.entrySet()) {
            if (!entry.getValue().isRemoved()) {
                entry.getValue().setObject((V) function.apply(entry.getKey(), entry.getValue().getObject()));
            }
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, length);
    }

    /**
     * Если указанный ключ еще не связан со значением (или сопоставлен со null) связывает его с заданным значением.
     *
     * @param key   Ключ.
     * @param value Значение.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> putIfAbsent(Object key, Object value) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        Object oldValue = null;
        MapNode node = newData.get(key);
        if (null == node) {
            newData.put((K) key, new MapNode<V>((V) value));
        } else {
            oldValue = node.getObject();
            if (null == oldValue) {
                node.setObject(value);
            } else {
                return this;
            }
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, length + 1);
    }

    /**
     * Удаляет запись для указанного ключа только в том случае, если он в данный момент сопоставлен с указанным значением.
     *
     * @param key   Ключ.
     * @param value Значение.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> remove(Object key, Object value) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null != node && node.getObject().equals(value) && !node.isRemoved()) {
            node.removeObject();
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, curSize - 1);
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
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);

        if (null != node && null != node.getObject() &&
                !node.isRemoved() && node.getObject().equals(oldValue)) {
            node.setObject(newValue);
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, length);
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
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);

        if (null != node && !node.isRemoved()) {
            node.setObject(value);
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, length);
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
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null != node && !node.isRemoved() && node.getObject() != null) {
            return this;
        }

        Object value = mappingFunction.apply(key);
        if (null != value) {
            if (null == node || node.isRemoved()) {
                newData.put((K) key, new MapNode<V>((V) value));
                curSize = curSize + 1;
            } else {
                node.setObject(value);
            }
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, curSize);
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
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null == node || node.isRemoved() || node.getObject() == null) {
            return this;
        }

        Object oldValue = node.getObject();
        Object value = remappingFunction.apply(key, oldValue);
        if (null != value) {
            node.setObject(value);
        } else {
            node.removeObject();
            curSize = curSize - 1;
        }

        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, curSize);
    }

    /**
     * Пытается вычислить сопоставление для указанного ключа и его текущего сопоставленного значения.
     *
     * @param key               Ключ.
     * @param remappingFunction Функция.
     * @return Новый словарь.
     */
    public PersistentMap<K, V> compute(Object key, BiFunction remappingFunction) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);
        int curSize = size();

        Object oldValue = null;
        if (null != node && !node.isRemoved()) {
            oldValue = node.getObject();
        }

        Object value = remappingFunction.apply(key, oldValue);
        if (null != value) {
            node.setObject(value);
        } else {
            if (null != oldValue) {
                node.removeObject();
                curSize = curSize - 1;
            }
        }

        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, curSize);
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
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        ;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null == node || node.isRemoved() || node.getObject() == null) {
            node.setObject(value);
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, curSize);
        }

        Object oldValue = node.getObject();
        Object newValue = remappingFunction.apply(key, oldValue);

        if (null != newValue) {
            node.setObject(newValue);
        } else {
            node.removeObject();
            curSize = curSize - 1;
        }

        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, curSize);
    }

    public PersistentMap<K, V> Undo() {
        if (prev.isEmpty()) {
            return this;
        }
        PersistentMap<K, V> currentMap = prev.pop();
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        next.push(this);
        return new PersistentMap<K, V>(prev, next, currentMap.data, currentMap.length);
    }

    public PersistentMap<K, V> Redo() {
        if (next.isEmpty()) {
            return this;
        }
        PersistentMap<K, V> currentMap = next.pop();
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>) this.prev.clone();
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>) this.next.clone();
        prev.push(this);
        return new PersistentMap<K, V>(prev, next, currentMap.data, currentMap.length);
    }
}
