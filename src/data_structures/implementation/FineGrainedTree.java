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

  public boolean isEmpty(){
    return root == null? true : false;
  }

  public void add(T data) {
    TreeNode curNode = null, parentNode = null;
    totalLock.lock();
    if (root == null) {  // Empty tree, inserting the node as root.
      root = new TreeNode(data);;
      totalLock.unlock();
      return;
    }
    curNode = root;
    curNode.lock.lock();
    totalLock.unlock();
    while (curNode != null) {
      parentNode = curNode;
      if (curNode.data.compareTo(data) > 0) {  // Visiting left subtree.
        curNode = curNode.left;
      } else {  // Visiting right subtree.
        curNode = curNode.right;
      }				
      if (curNode != null) {
        curNode.lock.lock();
        parentNode.lock.unlock();
      }
    }			
    if (parentNode.data.compareTo(data) > 0) {
      parentNode.left = new TreeNode(data);
    } else {
      parentNode.right = new TreeNode(data);
    }
    parentNode.lock.unlock();
  }




  private TreeNode findReplacement(TreeNode subRoot) {		
    TreeNode curNode = null;
    TreeNode parentNode = null;
    
    if(subRoot.left != null) {
      //Find the "biggest" node in the left subtree as the replacement
      parentNode = subRoot;
      curNode = subRoot.left;
      curNode.lock.lock();
      while(curNode.right != null) {
        if(parentNode != subRoot) {
          parentNode.lock.unlock();
        }
        parentNode = curNode;
        curNode = curNode.right;
        curNode.lock.lock();
      }
      if (curNode.left != null) {
        curNode.left.lock.lock();
      }
      if (parentNode == subRoot) {
        parentNode.left = curNode.left;
      } else {
        parentNode.right = curNode.left;
        parentNode.lock.unlock();
      }
      if (curNode.left != null) {
        curNode.left.lock.unlock();
      }
      curNode.lock.unlock();
    } else if (subRoot.right != null) {
      //Find the "smallest" node in the right subtree as the replacement
      parentNode = subRoot;
      curNode = subRoot.right;
      curNode.lock.lock();
      while (curNode.left != null) {
        if(parentNode != subRoot) {
          parentNode.lock.unlock();
        }
        parentNode = curNode;
        curNode = curNode.left;
        curNode.lock.lock();
      }
      if(curNode.right != null) {
        curNode.right.lock.lock();
      }
      if(parentNode == subRoot)
        parentNode.right = curNode.right;
      else {
        parentNode.left = curNode.right;
        parentNode.lock.unlock();
      }
      if(curNode.right != null) {
        curNode.right.lock.unlock();
      }
      curNode.lock.unlock();
    } else {
      //No children, no replacement needed
      return null;
    }
    return curNode;
  }

  public void remove(T data) {		
    TreeNode curNode = null;
    TreeNode parentNode = null;
    int compare = 0;
    int oldCompare = 0;		
    totalLock.lock();
    if(root != null) {
      //Tree is not empty, search for the passed data.  Start by checking
      //the root separately.
      curNode = root;
      parentNode = curNode;
      curNode.lock.lock();
      compare = curNode.data.compareTo(data);
      if(compare > 0) {
        //root is "bigger" than passed data, search the left subtree
        curNode = curNode.left;
        oldCompare = compare;
      } else if(compare < 0) {
        //root is "smaller" than passed data, search the right subtree
        curNode = curNode.right;
        oldCompare = compare;
      } else {
        //Found the specified data, remove it from the tree
        TreeNode replacement = findReplacement(curNode);				
        root = replacement;
        if(replacement != null) {
          replacement.left = curNode.left;
          replacement.right = curNode.right;
        }				
        curNode.lock.unlock();
        totalLock.unlock();
        return;
      }
      curNode.lock.lock();
      totalLock.unlock();
			
      while(true) {
        compare = curNode.data.compareTo(data);
        if(compare != 0) {
          parentNode.lock.unlock();
          parentNode = curNode;
          if(compare > 0) {
            //curNode is "bigger" than passed data, search the left
            //subtree
            curNode = curNode.left;
            oldCompare = compare;
          } else if(compare < 0) {
            //curNode is "smaller" than passed data, search the right
            //subtree
            curNode = curNode.right;
            oldCompare = compare;
          }
        } else {
          //Found the specified data, remove it from the tree
          TreeNode replacement = findReplacement(curNode);
					
          //Set the parent pointer to the new child
          if (oldCompare > 0) {
            parentNode.left = replacement;
          } else {
            parentNode.right = replacement;
          }					
          //Replace curNode with replacement
          if (replacement != null) {
            replacement.left = curNode.left;
            replacement.right = curNode.right;
          }					
          curNode.lock.unlock();
          parentNode.lock.unlock();
          return;
        }
				
        if (curNode == null) {
          break;
        } else {
          curNode.lock.lock();
        }
      } // closing the while
    } else {
      // Tree is empty
      // headLock.unlock();
      return;
    }
		
    //The specified data was not in the tree
    parentNode.lock.unlock();
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
