package Share;

public class Node<T> {
     protected T data;
    public Node(){}

    public Node(T newData){
        data = newData;
    }

    public T getData() {return data;}

}
