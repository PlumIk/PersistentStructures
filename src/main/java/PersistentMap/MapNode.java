package PersistentMap;

import java.util.TreeMap;

public class MapNode<T> {
    private class InnerNode<T> {
        private boolean isRemoved;
        private T object;

        InnerNode() {
            object = null;
            isRemoved = false;
        }

        InnerNode(T obj) {
            object = obj;
            isRemoved = false;
        }

        InnerNode(T obj, boolean removed) {
            object = obj;
            isRemoved = removed;
        }

        public T getObject() { return object; }

        public boolean isRemoved() {
            return isRemoved;
        }
    }

    private TreeMap<Integer, InnerNode<T>> data;

    MapNode() {
        data = new TreeMap<>();
    }

    MapNode(T object, int version) {
        data = new TreeMap<>();
        setObject(version, object);
    }

    public T getObject(int version) {
        return data.floorEntry(version).getValue().getObject();
    }

    public boolean isVersion(int version) {
        return data.get(version) != null;
    }

    public void setObject(int version, T obj) {
        data.put(version, new InnerNode<>(obj));
    }

    public void removeObject(int version) {
        data.put(version, new InnerNode<>(null, true));
    }

    public boolean isRemoved(int version) {
        return data.floorEntry(version) == null || data.floorEntry(version).getValue().isRemoved();
    }
}
