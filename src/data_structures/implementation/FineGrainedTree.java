package data_structures.implementation;

import java.util.ArrayList;
import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {

  private TreeNode root;
  private ArrayList<T> arrayList;
  private Lock totalLock = new ReentrantLock();

  public FineGrainedTree() {
    root = null;
  }
	
  public FineGrainedTree(TreeNode root) {
    this.root = root;
  }
	
  private class TreeNode {
    T data;
    TreeNode left, right;
    Lock lock;

    public TreeNode() {
      this(null,null,null);
    }

    public TreeNode(T d) {
      this(d, null, null);
    }

    public TreeNode(T data, TreeNode left, TreeNode right) {
      this.data = data == null ? null : data;
      this.left = left;
      this.right = right;
      lock = new ReentrantLock();
    }
  }

  public void add(T data) {
    TreeNode current = null, parent = null;
    totalLock.lock();
    try {
      if (root == null) {  // Empty tree, inserting the node as root.
        root = new TreeNode(data);;
        return;
      }
    } finally {
      totalLock.unlock();
    }
    current = root;
    current.lock.lock();
    try {
      while (current != null) {
        parent = current;
        if (current.data.compareTo(data) > 0) {  // Visiting left subtree.
          current = current.left;
        } else {  // Visiting right subtree.
          current = current.right;
        }				
        if (current != null) {
          current.lock.lock();
          parent.lock.unlock();
        }
      }			
      if (parent.data.compareTo(data) > 0) {
        parent.left = new TreeNode(data);
      } else {
        parent.right = new TreeNode(data);
      }
    } finally {
      if (current != parent && current != null) {
        current.lock.unlock();
        parent.lock.unlock();
      } else {
        parent.lock.unlock();
      }
    }
  }

  private TreeNode findReplacement(TreeNode subRoot) {		
    TreeNode current = null, parent = subRoot;
    if (subRoot.left != null) {  // Visiting the left subtree.
      current = subRoot.left;
      current.lock.lock();
      while (current.right != null) {  // Exits when the biggest node in the subtree is found.
        if(parent != subRoot) {
          parent.lock.unlock();
        }
        parent = current;
        current = current.right;
        current.lock.lock();
      }
      // Setting pointers to remove replacement.
      if (current.left != null) {
        current.left.lock.lock();
      }
      if (parent == subRoot) {
        parent.left = current.left;
      } else {
        parent.right = current.left;
        parent.lock.unlock();
      }
      if (current.left != null) {
        current.left.lock.unlock();
      }
    } else if (subRoot.right != null) {  // Visiting the right subtree.
      current = subRoot.right;
      current.lock.lock();
      while (current.left != null) {  // Exits when the smallest node in the subtree is found.
        if (parent != subRoot) {
          parent.lock.unlock();
        }
        parent = current;
        current = current.left;
        current.lock.lock();
      }
      // Setting pointers to remove replacement.
      if (current.right != null) {
        current.right.lock.lock();
      }
      if (parent == subRoot)
        parent.right = current.right;
      else {
        parent.left = current.right;
        parent.lock.unlock();
      }
      if (current.right != null) {
        current.right.lock.unlock();
      }
    } else {  // No children.
      return null;
    }
    current.lock.unlock();
    return current;
  }

  public void remove(T data) {		
    TreeNode current = null, parent = null;
    int compare = 0;
    totalLock.lock();
    try {
      if (root == null) {
        return;
      }
      // The root is removed separately since it has no parent node.
      parent = current = root;
      current.lock.lock();
      try {
        if (current.data.compareTo(data) > 0) { // Visiting left subtree.
          current = current.left;
        } else if (current.data.compareTo(data) < 0) { // Visiting right subtree.
          current = current.right;
        } else {
          TreeNode replacement = findReplacement(current);				
          root = replacement;
          if (replacement != null) { // Setting replacement node pointers.
            replacement.left = current.left;
            replacement.right = current.right;
          }				
          return;
        }
      } finally {
        if (current != parent.left && current != parent.right)
          current.lock.unlock();
      }
    } finally {
      totalLock.unlock();	
    }
    current.lock.lock();  // Locking left or right child of root.
    try {
      while (current != null) { // Exits if no match is found.
        if (current.data.compareTo(data) == 0) {  // A match is found. 
          TreeNode replacement = findReplacement(current);
          // Setting parent pointers.
          if (parent.data.compareTo(data) > 0) {
            parent.left = replacement;
          } else {
            parent.right = replacement;
          }
          // Setting replacement node pointers.
          if (replacement != null) {
            replacement.left = current.left;
            replacement.right = current.right;
          }					
          return;
        }        
        parent.lock.unlock();
        parent = current;
        if (current.data.compareTo(data) > 0) { //  Visiting left subtree.
          current = current.left;
        } else if (current.data.compareTo(data) < 0) {  // Visiting right subtree
          current = current.right;
        }      
        if (current != null) {
          current.lock.lock();
        }
      }
    } finally {
      if (current != parent) {
        current.lock.unlock();
        parent.lock.unlock();
      } else {
        current.lock.unlock();
      }
    }
  }

  // Visiting the tree nodes recursively with inorder traversal 
  private void inOrder(TreeNode root){
    if (root == null) {
      return;
    }
    inOrder(root.left);
    arrayList.add(root.data);
    inOrder(root.right);
  }

  public ArrayList<T> toArrayList() {
    arrayList = new ArrayList<T>();
    inOrder(root);
    return arrayList;
  }
}
