package data_structures.implementation;

import java.util.ArrayList;
import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {

  private Node head;
  
  public FineGrainedList(){
    head = new Node();
  }

  private class Node {
    T data;
    Node next;
    Lock lock;
    public Node(){
      this(null,null);
    }
    public Node(T data) {
      this(data, null);
    }
    public Node(T data, Node next) {
      this.data = data == null ? null : data;
      this.next = next;
      lock = new ReentrantLock();
    }
  }

  public boolean isEmpty() {
    if (head.next == null && head.data == null) {
      return true;
    }
    return false;
  }

  public void add(T t) {
    // System.out.println("\nAdding ...");

    // if the list is empty the node will be inserted as the head one
    if (isEmpty()) {      	
      head = new Node(t);
      return;
    }
    Node h = head;
    h.lock.lock();
    // The node will be inserted as the head if contains the smallest value in the list
    if (t.compareTo(head.data) <= 0) {
      head = new Node(t, head);
      h.lock.unlock();
      return;
    }
    Node prior = h;
    while (t.compareTo(h.data) > 0) {
      // The node is inserted at last position if contains the biggest value in the list
      if(h.next == null) {
        h.next = new Node(t, null);
        if (prior != h) {
          prior.lock.unlock();
        }
        h.lock.unlock();
        return;
      }
      if (prior != h) {
        prior.lock.unlock();
      }
      prior = h;
      h = h.next;
      h.lock.lock();
    }
    // Inserting the node in between two nodes
    Node newNode = new Node(t, h);
    prior.next = newNode;
    prior.lock.unlock();
    h.lock.unlock();
    return;
  }

  public void remove(T t) {
    Node h = head;
    h.lock.lock();
    // Nothing to remove if it is an empty list.
    if (isEmpty()) {
      return;
    }
    // The head of the list needs to be removed.
    if (t.compareTo(h.data) == 0) {
      if (h.next != null) {
        h.next.lock.lock();
      }
      head = (head.next == null) ? new Node() : head.next;
      if (h.next != null) {
        h.next.lock.unlock();
      }
      h.lock.unlock();
      return;
    }
    Node prior = h;
    while(t.compareTo(h.data) != 0) {
      // no match was found
      if (h.next == null) {
        if (prior != h) {
          prior.lock.unlock();
        }
        h.lock.unlock();
        return;
      }
      if (prior != h) {
        prior.lock.unlock();
      }
      prior = h;
      h = h.next;
      h.lock.lock();
    }
    // Removing element between other two.
    if (h.next != null) {
      h.next.lock.lock();
    }
    prior.next = h.next;
    if (h.next != null) {
      h.next.lock.unlock();
    }
    h.lock.unlock();
    prior.lock.unlock();
  }

  public ArrayList<T> toArrayList() {
    ArrayList<T> array = new ArrayList<T>();
    if (isEmpty()) {
      return array;
    }
    Node h = head;
    while (h.next != null) {
      array.add(h.data);
      h = h.next;
    }
    // Adding last element of the list to the arraylist
    array.add(h.data);
    return array;
  }
}
