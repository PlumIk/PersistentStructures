package PersistentMap;

public class MapNode<T> {
    private boolean isRemoved;
    private T object;

    MapNode() {
        object = null;
        isRemoved = false;
    }

    MapNode(T obj) {
        object = obj;
        isRemoved = false;
    }

    MapNode(T obj, boolean removed) {
        object = obj;
        isRemoved = removed;
    }

    public T getObject() { return object; }

    public void setObject(T obj) {
        object = obj;
        isRemoved = false;
    }

    public void removeObject() {
        object = null;
        isRemoved = true;
    }

    public boolean isRemoved() {
        return isRemoved;
    }
}
