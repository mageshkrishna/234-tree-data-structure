import java.util.*;
class DataItem {
  int dataItem;

  public DataItem(int val) {
    dataItem = val;
  }

  public void display() {
    System.out.println("The Value is: " + dataItem);
  }
}
class SiblingResult {
  private Node siblingNode;
  private String message;

  public SiblingResult(Node siblingNode, String message) {
    this.siblingNode = siblingNode;
    this.message = message;
  }

  public Node getSiblingNode() {
    return siblingNode;
  }

  public String getdirection() {
    return message;
  }
}

class Node {
  private static final int order = 4;
  private Node[] childArray = new Node[order];
  private DataItem[] dataArray = new DataItem[order - 1];
  private Node parent;
  private int numItems = 0;

  public Node getParent() {
    return parent;
  }

  public Node getChild(int num) {
    if (num < 0 || num >= order) {
      System.out.println("Invalid child number");
      return null;
    }
    return childArray[num];
  }

  public boolean isLeaf() {
    return (childArray[0] == null);
  }

  public int insertItem(int data) {
    if (numItems >= order - 1) {
      System.out.println("DataArray is full");
      return -1;
    }
    DataItem newItem = new DataItem(data);
    int index = numItems;

    while (index > 0 && dataArray[index - 1].dataItem > data) {
      dataArray[index] = dataArray[index - 1];
      index--;
    }

    dataArray[index] = newItem;
    numItems++;
    System.out.println("The data is successfully inserted at index: " + index);
    return index;
  }

  public DataItem removeItem() {
    if (numItems == 0) {
      System.out.println("DataArray is empty");
      return null;
    }
    DataItem temp = dataArray[numItems - 1];
    dataArray[numItems - 1] = null;
    numItems--;
    System.out.println("The data is successfully removed");
    return temp;
  }

  public DataItem removeGivenData(int data) {
    int index = findItem(data);
    if (index == -1) {
      System.out.println("Item not found");
      return null;
    }
    DataItem removedItem = dataArray[index];
    for (int i = index; i < numItems - 1; i++) {
      dataArray[i] = dataArray[i + 1];
    }
    dataArray[numItems - 1] = null;
    numItems--;
    System.out.println("The data is successfully removed");
    return removedItem;
  }

  public DataItem removeItemAtFirst() {
    if (numItems == 0) {
      System.out.println("DataArray is empty");
      return null;
    }
    DataItem removedItem = dataArray[0];
    for (int i = 0; i < numItems - 1; i++) {
      dataArray[i] = dataArray[i + 1];
    }
    dataArray[numItems - 1] = null;
    numItems--;
    System.out.println("The first data item is successfully removed");
    return removedItem;
  }

  public int findItem(int data) {
    for (int i = 0; i < numItems; i++) {
      if (dataArray[i] != null && dataArray[i].dataItem == data) {
        return i;
      }
    }
    return -1;
  }

  public void connectChild(int childNum, Node child) {
    if (childNum < 0 || childNum >= order) {
      System.out.println("Invalid child number");
      return;
    }
    childArray[childNum] = child;
    if (child != null) {
      child.parent = this;
    }
  }

  public Node disconnectChild(int childNum) {
    if (childNum < 0 || childNum >= order) {
      System.out.println("Invalid child number");
      return null;
    }
    Node temp = childArray[childNum];
    childArray[childNum] = null;
    return temp;
  }

  public int numOfItems() {
    return numItems;
  }

  public boolean isFull() {
    return (numItems == order - 1);
  }

  public void display() {
    for (int i = 0; i < numItems; i++) {
      dataArray[i].display();
    }

  }

  public DataItem getData(int index) {
    if (index < 0 || index >= numItems) {
      System.out.println("Invalid index");
      return null;
    }
    return dataArray[index];
  }
  public int getChildIndex(Node child) {
    for (int i = 0; i <= numItems; i++) {
      if (childArray[i] == child) {
        return i;
      }
    }
    return 10;
  }
}

public class Main {

  private static Node root = new Node();

// start of insert code
  public void insertNode(int data) {
    Node currNode = root;
    while (true) {
      if (currNode.isFull()) {
        split(currNode);
        currNode = currNode.getParent();
        currNode = getNextChild(data, currNode);
      } else if (currNode.isLeaf()) {
        break;
      } else {
        currNode = getNextChild(data, currNode);
      }
    }
    currNode.insertItem(data);
  }

  public void split(Node thisNode) {
    DataItem itemb, itemc;
    Node child2, child3, parent;
    itemc = thisNode.removeItem();
    itemb = thisNode.removeItem();
    child2 = thisNode.disconnectChild(2);
    child3 = thisNode.disconnectChild(3);

    Node rightNode = new Node();

    if (thisNode == root) {
      root = new Node();
      parent = root;
      root.connectChild(0, thisNode);
    } else {
      parent = thisNode.getParent();
    }

    int index = parent.insertItem(itemb.dataItem);
    int n = parent.numOfItems();
    for (int j = n - 1; j > index; j--) {
      Node temp = parent.disconnectChild(j);
      parent.connectChild(j + 1, temp);
    }
    parent.connectChild(index + 1, rightNode);
    rightNode.insertItem(itemc.dataItem);
    rightNode.connectChild(0, child2);
    rightNode.connectChild(1, child3);
  }

  public Node getNextChild(int value, Node thisNode) {
    int num = thisNode.numOfItems();
    for (int i = 0; i < num; i++) {
      if (value < thisNode.getData(i).dataItem) {
        return thisNode.getChild(i);
      }
    }
    return thisNode.getChild(num);
  }
// end of insert code 

// start of delete code
 public void delete(int val, Node currNode) {
    if (currNode == null) {
      currNode = findItem(val); // Find the node containing the value if currNode is null
    }
    if (currNode == null) {
      System.out.println("Node not present deeltion failed");
      return;
    }
    if (currNode == root && currNode.isLeaf()) {
      currNode.removeGivenData(val);
      return;
    }
    if (currNode.isLeaf()) {
      // Handle deletion in leaf nodes
      if (currNode.numOfItems() >= 2) {
        currNode.removeGivenData(val);
        return;
      } else {
        SiblingResult result = getMySiblingSize(currNode);
        Node siblingNode = result.getSiblingNode();
        String direction = result.getdirection();

        if (siblingNode != null) {
          Node parent = currNode.getParent();
          int childIndex = parent.getChildIndex(currNode);
          DataItem parentData;

          if (direction.equals("Left")) {
            // Borrow from the left sibling
            parentData = parent.getData(childIndex - 1);
            currNode.insertItem(parent.removeGivenData(parentData.dataItem).dataItem);
            parent.insertItem(siblingNode.removeItem().dataItem);
          } else if (direction.equals("Right")) {
            // Borrow from the right sibling
            parentData = parent.getData(childIndex);
            currNode.insertItem(parent.removeGivenData(parentData.dataItem).dataItem);
            parent.insertItem(siblingNode.removeItemAtFirst().dataItem);
          }

          currNode.removeGivenData(val);
          return;
        } else {
          mergeWithSiblings(currNode);
        }
      }
    } else {
      // Handle deletion in internal nodes
      String direction = findinternalsiblings(currNode, val);
      int index = currNode.findItem(val);

      if (direction.equals("Left")) {
        Node child = currNode.getChild(index);
        DataItem data = child.getData(child.numOfItems() - 1); // Get the predecessor

        currNode.removeGivenData(val); // Remove val from currNode
        currNode.insertItem(data.dataItem); // Insert predecessor in currNode

        delete(data.dataItem, child); // Recursively delete the predecessor
      } else if (direction.equals("Right")) {
        Node child = currNode.getChild(index + 1);
        DataItem data = child.getData(0); // Get the successor

        currNode.removeGivenData(val); // Remove val from currNode
        currNode.insertItem(data.dataItem); // Insert successor in currNode

        delete(data.dataItem, child); // Recursively delete the successor
      } else {
        // Merge case when both children have only 1 key
        Node leftChild = currNode.getChild(index);
        Node rightChild = currNode.getChild(index + 1);

        leftChild.insertItem(currNode.removeGivenData(val).dataItem); // Move value from currNode to leftChild
        leftChild.insertItem(rightChild.removeItem().dataItem); // Move right child's first item to leftChild

        // Reconnect children
        int i = index + 1;
        for (; i <= currNode.numOfItems(); i++) {
          Node child = currNode.getChild(i + 1);
          currNode.connectChild(i, child);
        }
        currNode.disconnectChild(i); // Remove the last child reference
        delete(val, leftChild);

      }
    }
  }
  public SiblingResult getMySiblingSize(Node node) {
    Node parent = node.getParent();
    int parentSize = parent.numOfItems();
    int index = 0;

    // Find the index of the current node in its parent's children array
    for (int i = 0; i <= parentSize; i++) {
      if (parent.getChild(i) == node) {
        index = i;
        break;
      }
    }

    // Check the left sibling
    if (index != 0) {
      Node leftSibling = parent.getChild(index - 1);
      int size = leftSibling.numOfItems();
      if (size >= 2) {
        return new SiblingResult(leftSibling, "Left");
      }
    }

    // Check the right sibling
    if (index != parentSize) {
      Node rightSibling = parent.getChild(index + 1);
      int size = rightSibling.numOfItems();
      if (size >= 2) {
        return new SiblingResult(rightSibling, "Right");
      }
    }

    // If neither sibling has enough items, return null and a message
    return new SiblingResult(null, "No sibling has enough items");
  }

  public void mergeWithSiblings(Node node) {
    if (node == null) {
      return;
    }

    Node parent = node.getParent();
    if (parent == null) {
      return; // Safety check
    }

    int size = parent.numOfItems(); // Number of items in the parent node (size starts from 1)
    int childIndex = parent.getChildIndex(node); // Index of the current node as a child

    // Ensure childIndex is within the valid range
    if (childIndex >= 0 && childIndex <= size) {
      if (childIndex > 0) {
        // Merge with the left sibling
        DataItem parentData = parent.getData(childIndex - 1);
        parent.removeGivenData(parentData.dataItem);
        parent.getChild(childIndex - 1).insertItem(parentData.dataItem);
      } else {
        // Merge with the right sibling
        DataItem parentData = parent.removeGivenData(parent.getData(0).dataItem);
        parent.getChild(1).insertItem(parentData.dataItem);
      }

      // Shift children to the left
      for (int i = childIndex; i < size; i++) {
        Node nextChild = parent.getChild(i + 1);
        parent.connectChild(i, nextChild);
      }
      parent.disconnectChild(size); // Disconnect the last child
    } else {
      System.out.println("Child index out of bounds");
    }
  }

  public String findinternalsiblings(Node node, int val) {
    int index = node.findItem(val);
    int leftsize = node.getChild(index).numOfItems();
    int rightsize = node.getChild(index + 1).numOfItems();
    if (leftsize > 1) {
      return "Left";
    } else if (rightsize > 1) {
      return "Right";
    } else {
      return "Merge";
    }
  }

  public Node findItem(int val) {
    if (root == null) {
      return null; // If the tree is empty
    }
    Node currNode = root;
    int childNumber;

    while (true) {
      // Check if the current node is internal and has only one item
      if (currNode.numOfItems() == 1 && !currNode.isLeaf()) {
        Node leftChild = currNode.getChild(0);
        Node rightChild = currNode.getChild(1);

        // Check if both children have one item each
        if (leftChild.numOfItems() == 1 && rightChild.numOfItems() == 1) {
          // Move items from children to the current node
          currNode.insertItem(leftChild.removeItem().dataItem);
          currNode.insertItem(rightChild.removeItem().dataItem);

          // Preserve the left and right grandchildren
          Node leftGrandChild0 = leftChild.getChild(0);
          Node leftGrandChild1 = leftChild.getChild(1);
          Node rightGrandChild0 = rightChild.getChild(0);
          Node rightGrandChild1 = rightChild.getChild(1);

          // Disconnect the merged children
          currNode.disconnectChild(1); // Disconnect rightChild
          currNode.disconnectChild(0); // Disconnect leftChild

          // Reconnect the grandchildren as children of the current node
          currNode.connectChild(0, leftGrandChild0);
          currNode.connectChild(1, leftGrandChild1);
          currNode.connectChild(2, rightGrandChild0);
          currNode.connectChild(3, rightGrandChild1);
        }
      }
   
      // Try to find the item in the current node
      if ((childNumber = currNode.findItem(val)) != -1) {
        return currNode; // Item found, return the current node
      } else if (currNode.isLeaf()) {
        return null; // Reached a leaf node and item is not found
      } else {
        // Move to the appropriate child
        currNode = getNextChild(val, currNode);
      }
    }
  }

//end of delete code
  public void inorder(Node node) {
    if (node == null) {

    } else {
      int index = 0;
      while (index < node.numOfItems()) {
        if (!node.isLeaf()) {
          inorder(node.getChild(index));
        }
        System.out.println(node.getData(index).dataItem);
        index++;
      }
      if (!node.isLeaf()) {
        inorder(node.getChild(index));
      }

    }
  }
  public void preorder(Node node) {
    if (node == null) {

    } else {
      int index = 0;
      while (index < node.numOfItems()) {
        System.out.println(node.getData(index).dataItem);
        index++;
      }
      int j = 0;
      if (!node.isLeaf()) {
        while (node.getChild(j) != null && j <= 4) {
          preorder(node.getChild(j));
          j++;
        }
      }

    }
  }
  public void postorder(Node node) {
    if (node == null) {

    } else {

      int j = 0;
      if (!node.isLeaf()) {
        while (node.getChild(j) != null && j <= 4) {
          preorder(node.getChild(j));
          j++;
        }
      }
      int index = 0;
      while (index < node.numOfItems()) {
        System.out.println(node.getData(index).dataItem);
        index++;
      }

    }
  }
  public void displayTree() {
    Queue < Node > queue = new LinkedList < > ();
    queue.add(root);
    int level = 0;
    while (!queue.isEmpty()) {
      int nodesInLevel = queue.size();
      System.out.print("Level " + level + ": ");
      while (nodesInLevel > 0) {
        Node node = queue.poll();
        node.display();
        for (int i = 0; i <= node.numOfItems(); i++) {
          if (node.getChild(i) != null) {
            queue.add(node.getChild(i));
          }
        }
        nodesInLevel--;
      }
      level++;
      System.out.println();
    }
  }
  public static void main(String[] args) {
    Main tree = new Main();
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("\n==== 2-3-4 Tree Operations ====");
      System.out.println("1. Insert");
      System.out.println("2. Delete");
      System.out.println("3. Pre-order Traversal");
      System.out.println("4. In-order Traversal");
      System.out.println("5. Post-order Traversal");
      System.out.println("6. Display Tree");
      System.out.println("7. Exit");
      System.out.print("Enter your choice (1-7): ");
      int choice = -1;
      try {
        choice = scanner.nextInt();
      } catch (InputMismatchException e) {
        System.out.println("Invalid input! Please enter a number between 1 and 7.");
        scanner.next(); // Clear the invalid input
        continue;
      }

      switch (choice) {
      case 1:
        System.out.print("Enter value to insert: ");
        int insertValue;
        try {
          insertValue = scanner.nextInt();
          tree.insertNode(insertValue);
          System.out.println("Inserted " + insertValue + " into the tree.");
        } catch (InputMismatchException e) {
          System.out.println("Invalid input! Please enter an integer value.");
          scanner.next();
        }
        break;
      case 2:
        System.out.print("Enter value to delete: ");
        int deleteValue;
        try {
          deleteValue = scanner.nextInt();
          tree.delete(deleteValue, null);
        } catch (InputMismatchException e) {
          System.out.println("Invalid input! Please enter an integer value.");
          scanner.next();
        }
        break;
      case 3:
        System.out.println("Pre-order Traversal:");
        tree.preorder(root);
        System.out.println();
        break;
      case 4:
        System.out.println("In-order Traversal:");
        tree.inorder(root);
        System.out.println();
        break;
      case 5:
        System.out.println("Post-order Traversal:");
        tree.postorder(root);
        System.out.println();
        break;
      case 6:
        System.out.println("Tree Structure:");
        tree.displayTree();
        break;
      case 7:
        System.out.println("Exiting the program. Goodbye!");
        scanner.close();
        System.exit(0);
      default:
        System.out.println("Invalid choice! Please select a number between 1 and 7.");
      }
    }
  }
}
