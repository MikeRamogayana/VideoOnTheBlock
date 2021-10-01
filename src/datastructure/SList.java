package datastructure;

import java.util.ArrayList;
import java.util.Iterator;

public class SList<T> implements Iterable<T> {
	
	protected Node<T> head = null;
	protected int size = 0;
	
	/**
	 * Default Constructor
	 */
	public SList() {}
	
	/**
	 * Construct Blockchain given a list of blocks
	 * 
	 * @param elements
	 */
	public SList(T[] elements) {
		for(T element: elements) {
			insert(element);
		}
	}

	/**
	 * Inserts a new block to the blockchain
	 * 
	 * @param element
	 */
	protected void insert(T element) {
		if(size == 0) {
			head = new Node<T>(element, null);
		} else {
			Node<T> newnode = new Node<>(element, head);
			head = newnode;
		}
		size++;
	}
	
	@Override
	public Iterator<T> iterator() {
		ArrayList<T> list = new ArrayList<>();
		Node<T> current = head;
		while(current.getNext() != null) {
			list.add(current.getElement());
			current = current.getNext();
		}
		list.add(current.getElement());
		return list.iterator();
	}
}
