package data_structures.implementation;

import java.util.ArrayList;
import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {

  private Node head;
  private Lock lock = new ReentrantLock();

  public CoarseGrainedList(){
    head = new Node();
  }

  private class Node {
    T data;
    Node next;
    public Node(){
      this(null,null);
    }
    public Node(T data) {
      this(data, null);
    }
    public Node(T data, Node next) {
      this.data = data == null ? null : data;
      this.next = next;
    }
  }

  public boolean isEmpty() {
    if(head.next==null && head.data==null) {
      return true;
    }
    return false;
  }

  public void add(T t) {
    lock.lock();
    // if the list is empty the node will be inserted as the head one
    if(isEmpty()){      	
      head = new Node(t);
      lock.unlock();
      return;
    }
    Node h = head;
    // The node will be inserted as the head if contains the smallest value in the list
    if(t.compareTo(h.data) <= 0) {
      h = new Node(t, head);
      head = h;
      lock.unlock();
      return;
    }
    Node prior = h;
    while(t.compareTo(h.data) > 0) {
      // The node is inserted at last position if contains the biggest value in the list
      if(h.next == null){
        h.next = new Node(t, null);
        lock.unlock();
        return;
      }
      prior = h;
      h = h.next;
    }
    // Inserting the node in between two nodes
    Node newNode = new Node(t, h);
    prior.next = newNode;
    lock.unlock();
    return;
  }

  @Override
  public void remove(T t) {
    lock.lock();
    if(isEmpty()) {
      lock.unlock();
      return;
    }
    Node h = head;
    if(t.compareTo(h.data) == 0) {
      head = (head.next == null) ? new Node() : head.next;
      lock.unlock();
      return;
    }
    Node prior = h;
    while(t.compareTo(h.data) != 0) {
      prior = h;
      h = h.next;
      // no match was found
      if (h.next == null) {
        lock.unlock();
        return;
      }
    }
    prior.next = h.next;
    lock.unlock();
  }
  
  // public void add(T t) {
  //     throw new UnsupportedOperationException();
  // }

  // public void remove(T t) {
  //   throw new UnsupportedOperationException();
  // }

  public ArrayList<T> toArrayList() {
    lock.lock();
    // can we assume that the list is not empty?
    ArrayList<T> array = new ArrayList<T>();
    Node h = head;
    while(h.next != null) {
      array.add(h.data);
      h = h.next;
    }
    // Adding last element of the list to the arraylist
    array.add(h.data);
    lock.unlock();
    return array;
  }

  // public ArrayList<T> toArrayList() {
  //   throw new UnsupportedOperationException();
  // }
}
