package data_structures.implementation;

import java.util.ArrayList;
import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {

  private TreeNode root;
  private ArrayList<T> arrayList;

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

  public boolean isEmpty(){
    return root == null? true : false;
  }

  public void add(T e) {
    root = add(root, e, root);
  }

  // Adding a node recursively
  private TreeNode add(TreeNode root, T e, TreeNode parent) {
    if (root == null) {
      return new TreeNode(e);
    }
    if (e.compareTo(root.data) < 0) {
      root.lock.lock();
      root.left = add(root.left, e, root);
      root.lock.unlock();
    } else {
      root.lock.lock();
      root.right = add(root.right, e, root);
      root.lock.unlock();
    }
    return root;
  }

  public void remove(T t) {
    throw new UnsupportedOperationException();
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
