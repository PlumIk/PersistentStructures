package PersistentMap;

import Utils.Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PersistentMap<K, V> implements Map {
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
    private Stack <PersistentMap<K, V>> prev = new Stack<>();
    private Stack <PersistentMap <K, V>> next =  new Stack<>();
    private TreeMap<K, MapNode<V>> data;

    /**
     * Constructs an empty persistent map.
     */
    public PersistentMap() {
        length = 0;
        data = new TreeMap<>();
    }

    private PersistentMap(Stack <PersistentMap <K, V>> prevs, Stack <PersistentMap <K, V>> nexts, TreeMap<K, MapNode<V>> data, int size){
        this.data = data;
        length = size;
        this.prev=prevs;
        this.next=nexts;
    }

    /**
     * Returns the number of elements in the current version of this map.
     * @return number of elements in the current version of this map.
     */
    @Override
    public int size() {
        return length;
    }

    /**
     * Returns true if the current version of this map contains no elements.
     * @return true if the current version of this map contains no elements, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return length ==0;
    }


    /**
     * Returns true if current version of map contains a mapping for the specified key.
     * More formally, returns true if and only if current version of map contains a mapping for a key k such that (key==null ? k==null : key.equals(k)).
     * (There can be at most one such mapping.)
     * @param key key whose presence in this map is to be tested
     * @return true if this version of map contains a mapping for the specified key
     */
    @Override
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
     * Returns true if current version of map maps one or more keys to the specified value.
     * More formally, returns true if and only if current version of map contains at least one mapping to a value v such that (value==null ? v==null : value.equals(v)).
     * This operation will probably require time linear in the map size for most implementations of the Map interface.
     * @param value value whose presence in current version of map is to be tested
     * @return if current version of map maps one or more keys to the specified value
     */
    @Override
    public boolean containsValue(Object value) {
        for (MapNode<V> node: data.values()) {
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
     * Returns the value to which the specified key is mapped, or null if current version of map contains no mapping for the key.
     * More formally, if current version of map contains a mapping from a key k to a value v such that (key==null ? k==null : key.equals(k)), then this method returns v; otherwise it returns null.
     * (There can be at most one such mapping.)
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if current version of map contains no mapping for the key
     */
    @Override
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
     * Associates the specified value with the specified key in current version of map (optional operation).
     * If the map previously contained a mapping for the key, the old value is replaced by the specified value.
     * (A map m is said to contain a mapping for a key k if and only if m.containsKey(k) would return true.)
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key.
     */
    @Override
    public PersistentMap<K, V> put(Object key, Object value) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);
        MapNode node = newData.get(key);
        int currSize = length;
        if (null == node) {
            newData.put((K)key, new MapNode<V>((V)value));
            currSize = currSize + 1;
        } else {
            node.setObject(value);
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, currSize);
    }

    @Override
    public PersistentMap<K, V> remove(Object key) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        MapNode node = newData.get(key);
        int currSize = length;
        if (null != node) {
            node.removeObject();
            currSize = currSize - 1;
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, currSize);
    }

    // ???
    public PersistentMap<K, V> putAllMap(Map m) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
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
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, currSize);
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {throw new UnsupportedOperationException();}

    @Override
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

    @Override
    public Collection values() {
        LinkedList<V> result = new LinkedList<V>();
        for (Map.Entry<K, MapNode<V>> entry : data.entrySet()) {
            if (!entry.getValue().isRemoved()) {
                result.add(entry.getValue().getObject());
            }
        }
        return result;
    }

    @Override
    public Set<Entry> entrySet() {
        Set<Entry> result = new HashSet<>();

        for (Map.Entry<K, MapNode<V>> entry : data.entrySet()) {
            if (!entry.getValue().isRemoved()) {
                result.add(new PersistentMapEntry<>(entry.getKey(), entry.getValue().getObject()));
            }
        }
        return result;
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {

        if (data.get(key) != null && !data.get(key).isRemoved()) {
            return data.get(key).getObject();
        }
        return defaultValue;
    }

    // ???
    public PersistentMap<K, V> forEachMap(BiConsumer action) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        for (Map.Entry<K, MapNode<V>> entry : newData.entrySet()) {
            if (!entry.getValue().isRemoved()) {
                action.accept(entry.getKey(), entry.getValue().getObject());
            }
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, length);
    }
    @Override
    public void forEach(BiConsumer action) {
        throw new UnsupportedOperationException();
    }

    public PersistentMap<K, V> replaceAllMap(BiFunction function) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        for (Map.Entry<K, MapNode<V>> entry : newData.entrySet()) {
            if (!entry.getValue().isRemoved()) {
                entry.getValue().setObject((V)function.apply(entry.getKey(), entry.getValue().getObject()));
            }
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, length);
    }
    @Override
    public void replaceAll(BiFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PersistentMap<K, V> putIfAbsent(Object key, Object value) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        Object oldValue = null;
        MapNode node = newData.get(key);
        if (null == node) {
            newData.put((K)key, new MapNode<V>((V)value));
        } else {
            oldValue = node.getObject();
            if (null == oldValue) {
                node.setObject(value);
            } else {
                return this;
            }
        }
        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, length + 1);
    }


    public PersistentMap<K, V> removeMap(Object key, Object value) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null != node && node.getObject().equals(value) && !node.isRemoved()) {
            node.removeObject();
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, curSize - 1);
        }
        return this;
    }
    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public PersistentMap<K, V> replaceMap(Object key, Object oldValue, Object newValue) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        MapNode node = newData.get(key);

        if (null != node && null != node.getObject() &&
                !node.isRemoved() && node.getObject().equals(oldValue)) {
            node.setObject(newValue);
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, length);
        }
        return this;
    }
    @Override
    public boolean replace(Object key, Object oldValue, Object newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PersistentMap<K, V> replace(Object key, Object value) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        MapNode node = newData.get(key);

        if (null != node && !node.isRemoved()) {
            node.setObject(value);
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, length);
        }
        return this;
    }

    @Override
    public PersistentMap<K, V> computeIfAbsent(Object key, Function mappingFunction) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null != node && !node.isRemoved() && node.getObject() != null) {
            return this;
        }

        Object value = mappingFunction.apply(key);
        if (null != value) {
            if (null == node) {
                newData.put((K)key, new MapNode<V>((V)value));
                curSize = curSize + 1;
            } else {
                node.setObject(value);
            }
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
            return new PersistentMap<K, V>(prev, next, newData, curSize);
        }

        return this;
    }

    @Override
    public PersistentMap<K, V> computeIfPresent(Object key, BiFunction remappingFunction) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null == node || node.isRemoved() || node.getObject() == null) {
            return null;
        }

        Object oldValue = node.getObject();
        Object value = remappingFunction.apply(key, oldValue);
        if (null != value) {
            node.setObject(value);
        } else {
            node.removeObject();
            curSize = curSize - 1;
        }

        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, curSize);
    }

    @Override
    public PersistentMap<K, V> compute(Object key, BiFunction remappingFunction) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
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

        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, curSize);
    }

    @Override
    public PersistentMap<K, V> merge(Object key, Object value, BiFunction remappingFunction) {
        TreeMap<K, MapNode<V>> newData = new TreeMap<>(data);;
        MapNode node = newData.get(key);
        int curSize = size();

        if (null == node || node.isRemoved() || node.getObject() == null) {
            node.setObject(value);
            Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
            prev.push(this);
            Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
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

        Stack<PersistentMap<K, V>> prev = (Stack<PersistentMap<K, V>>)this.prev.clone();
        prev.push(this);
        Stack<PersistentMap<K, V>> next = (Stack<PersistentMap<K, V>>)this.next.clone();
        return new PersistentMap<K, V>(prev, next, newData, curSize);
    }
}
