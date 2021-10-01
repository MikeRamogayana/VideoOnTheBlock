package datastructure;

public class Node<T> {
	
	private T element = null;
	private Node<T> next = null;
	
	public Node() {}
	
	public Node(T element, Node<T> next) {
		this.element = element;
		this.next = next;
	}
	
	public T getElement() {
		return element;
	}
	
	public Node<T> getNext() {
		return next;
	}
	
	public void setElement(T element) {
		this.element = element;
	}
	
	public void setNext(Node<T> next) {
		this.next = next;
	}
	
}
