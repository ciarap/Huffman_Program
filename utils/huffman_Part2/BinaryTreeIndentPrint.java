/**
 * @author Ciara Power 20072488
 *
 */
package huffman_Part2;

import huffman_Part2.Item;

class BinaryTreeIndentPrint

{

    // Root of the Binary Tree

    Item root;
    String child;



    public BinaryTreeIndentPrint()

    {

        root = null;

    }

    /*
    * prints out a tree from the root in a readable structure
    */
    
void printPreOrder(Item root, String indent,String child)

    {

        if(root == null)

            return;

        if(root.getFrequency()!=0)  // after compression
             System.out.println(""+indent+child+":"+root.getString()+" : "+root.getFrequency());
        else // after decompression , no frequencies recorded
        	System.out.println(""+indent+child+":"+root.getString());
        if(root.getLeftTree() != null){
            printPreOrder(root.getLeftTree(),indent+"   ","L");   

        }

        if(root.getRightTree() != null){

            printPreOrder(root.getRightTree(),indent+"   ","R");

        }

    }

     



}