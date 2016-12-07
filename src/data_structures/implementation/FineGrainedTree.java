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

  public void add(T data) {
    System.out.println(data);
    TreeNode newNode = new TreeNode(data);
    TreeNode curNode = null;
    TreeNode parentNode = null;
    int compare = 0;		
    // lock.lock();
    if (root == null) {
      //The tree is empty, insert the new node as the root
      root = newNode;
      // root.lock.unlock();
    } else {
      //The tree is not empty, find a location to insert the new node
      curNode = root;
      curNode.lock.lock();
      // curNode.lock();
      // root.lock.unlock();
      while (true) {
        parentNode = curNode;
        compare = curNode.data.compareTo(data);
        if (compare > 0) {
          //curNode is "bigger" than newNode, enter left subtree
          curNode = curNode.left;
        } else {
          //curNode is "smaller" than newNode, enter right subtree
          curNode = curNode.right;
        }				
        //Check to see if we've found our location.  If not, continue
        //traversing the tree; else, break out of the loop
        if (curNode == null) {
          break;
        } else {
          curNode.lock.lock();
          parentNode.lock.unlock();
        }
      }
			
      //Insert the node into the tree
      if (compare > 0) {
        parentNode.left = newNode;
      } else {
        parentNode.right = newNode;
      }
      parentNode.lock.unlock();
    }
  }

  public void add2(T e) {
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
