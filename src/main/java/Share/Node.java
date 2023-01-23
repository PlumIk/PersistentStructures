package Share;

import PersistentMap.MapNode;

import java.util.TreeMap;

public class Node<T> {
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

    protected T data;
    protected TreeMap<Integer, InnerNode<T>> d;
    public Node() {
        d = new TreeMap<>();
    }

    public Node(T object, int version) {
        d = new TreeMap<>();
        setObject(version, object);
    }

    public T getData(int version) {
        return d.floorEntry(version).getValue().getObject();
    }

    public boolean isVersion(int version) {
        return d.get(version) != null;
    }

    public void setObject(int version, T obj) {
        d.put(version, new InnerNode<>(obj));
    }

    public void removeObject(int version) {
        d.put(version, new InnerNode<>(null, true));
    }

    public boolean isRemoved(int version) {
        return d.floorEntry(version) == null || d.floorEntry(version).getValue().isRemoved();
    }

}
