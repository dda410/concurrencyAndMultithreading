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
    try {
      // Adding head node to empty list.
      if (isEmpty()) {
        head = new Node(t);
        return;
      }
      // Adding as first node.
      if (x.data != null && t.compareTo(x.data) < 0) {
        head = new Node(t, x);
        return;
      }
      Node nextNode = null;
      while (x.next != null) {
        nextNode = x.next;
        nextNode.lock.lock();
        try {
          // Adding to intermidiate position.
          if (t.compareTo(nextNode.data) < 0) {
            x.next = new Node (t, nextNode);
            return;
          }
          x.lock.unlock();
          x = nextNode;
        } finally {
          if (x != nextNode)
            nextNode.lock.unlock();
        }
      }
      // Adding as last node.
      x.next = new Node(t, null);
    } finally {
      x.lock.unlock();
    }
  }

  public void remove(T t) {
    Node x = head;
    x.lock.lock();
    try {
      // Removing first node.
      if (! isEmpty()) {
        head = new Node();
        return;
      }
      Node nextNode = null;
      while (x.next != null) {
        nextNode = x.next;
        nextNode.lock.lock();
        try {
          // Removing intermidiate node.
          if (t.compareTo(nextNode.data) == 0) {
            x.next = x.next.next;
            return;
          }
          x.lock.unlock();
          x = nextNode;
        } finally {
          if (x != nextNode)
            nextNode.lock.unlock();
        }
      }
    } finally {
      x.lock.unlock();
    }
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
