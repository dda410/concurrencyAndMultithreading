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

  public void lockNode(Node x) {
    if (x != null) {
      x.lock.lock();
    }
  }

  public void unlockNode(Node x) {
    if (x != null) {
      x.lock.unlock();
    }
  }

  public void add(T o) {
    Node x = head;
    x.lock.lock();
    if (isEmpty()) {
      head = new Node(o);
      x.lock.unlock();
      return;
    }
    while (x.next != null) {
      x.next.lock.lock();
      if (o.compareTo(x.next.data) < 0) {
        x.next = new Node (o, x.next);
        x.lock.unlock();
        x.next.next.lock.unlock();
        return;
      }
      x.lock.unlock();
      x = x.next;
    }
    x.next = new Node(o, null);
    x.lock.unlock();
  }

  public boolean remove(T o) {
    Node x = head;
    x.lock.lock();
    while (x.next != null) {
      x.next.lock.lock();
      if (o.compareTo(x.next.data) == 0) {
        unlink(x);
        return true;
      }
      x.lock.unlock();
      x = x.next;
    }
    x.lock.unlock();
    return false;
  }

  public void remove2(T t) {
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
      h.next.lock.lock();
      h = h.next;
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
