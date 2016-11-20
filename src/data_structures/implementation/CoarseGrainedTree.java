package data_structures.implementation;

import java.util.ArrayList;
import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {
  private TreeNode root;
  private ArrayList<T> arrayList;
  private Lock lock = new ReentrantLock();

  public CoarseGrainedTree() {
    root = null;
  }
	
  public CoarseGrainedTree(TreeNode root) {
    this.root = root;
  }
	
  private class TreeNode {
    T data;
    TreeNode left, right;

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
    }
  }

  public boolean isEmpty(){
    return root == null? true : false;
  }

  public void add(T e) {
    lock.lock();
    root = add(root, e);
    lock.unlock();
  }

  // Adding a node recursively
  private TreeNode add(TreeNode root, T e) {
    if(root == null)
      return new TreeNode(e);
    if(e.compareTo(root.data) < 0) {
      root.left = add(root.left, e);
    } else {
      root.right = add(root.right, e);
    }
    return root;
  }

  public T minimum(TreeNode root){
    return root.left == null ? root.data : minimum(root.left);
  }

  public void remove(T e) {
    lock.lock();
    root = remove(root, e);
    lock.unlock();
  }

  // Removing a node recursively
  private TreeNode remove(TreeNode root, T e) {
    if(root == null) {
      throw new Error("The element cannot be removed because it does not exist");
    }
    if(e.compareTo(root.data) < 0) {
      root.left = remove(root.left, e);
    } else {
      if(e.compareTo(root.data) > 0) {
        root.right = remove(root.right, e);
      } else {
        if(root.left == null) {
          root = root.right;
        } else {
          if(root.right == null) {
            root = root.left;
          } else {
            root.data = minimum(root.right);
            root.right = remove(root.right, root.data);
          }
        }
      }
    }
    return root;
  }

  // Visiting the tree nodes recursively with inorder traversal 
  private void inOrder(TreeNode root){
    if(root == null) {
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
