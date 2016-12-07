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
    Node x = head;
    x.lock.lock();
    if (isEmpty()) {
      head = new Node(t);
      x.lock.unlock();
      return;
    }
    while (x.next != null) {
      x.next.lock.lock();
      if (t.compareTo(x.next.data) < 0) {
        x.next = new Node (t, x.next);
        x.lock.unlock();
        x.next.next.lock.unlock();
        return;
      }
      x.lock.unlock();
      x = x.next;
    }
    x.next = new Node(t, null);
    x.lock.unlock();
  }

  public void remove(T t) {
    Node x = head;
    x.lock.lock();
    if (! isEmpty() && t.compareTo(x.data) == 0) {
      head = new Node();
      x.lock.unlock();
      return;
    }
    while (x.next != null) {
      x.next.lock.lock();
      if (t.compareTo(x.next.data) == 0) {
        Node tmp = x.next;
        x.next = x.next.next;
        x.lock.unlock();
        tmp.lock.unlock();
        return;
      }
      x.lock.unlock();
      x = x.next;
    }
    x.lock.unlock();
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
