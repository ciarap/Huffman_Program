 package huffman_Part2;
 /*
 * @author Ciara Power 20072488
 *
 */

public class Item implements Comparable<Item>{

	private String string="";
	private int frequency=0;
	private Item leftTree=null;
	private Item rightTree=null;
	
	
	/*
	 * Basic getters and setters for fields
	 */
	public String getString() {
		return string;
	}
	public void setString(String character) {
		this.string = character;
	}
	
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public Item getLeftTree() {
		return leftTree;
	}
	public void setLeftTree(Item left) {
		this.leftTree = left;
	}
	
	public Item getRightTree() {
		return rightTree;
	}
	public void setRightTree(Item right) {
		this.rightTree = right;
	}
	
	/*
	 * constructor for an item 
	 */
	public Item(String string, int frequency, Item left, Item right) {
		this.string = string;
		this.frequency = frequency;
		this.leftTree = left;
		this.rightTree = right;
	}
	
	public Item(String string, Item left, Item right) {
		this.string = string;
		this.leftTree = left;
		this.rightTree = right;
	}
	
	/*
	 * compares items based on frequencies
	 */
	@Override
	public int compareTo(Item item2) {
		int returnVal;
		if (frequency<(item2).getFrequency()){
			 returnVal=-1;
		}
		else if (frequency>(item2).getFrequency()){
		 returnVal= 1;
		}
		else {
			 returnVal= 0;
		}
	return returnVal;
	}
	
	
	@Override
	public String toString() {
		String itemString="Item [character=" + string + ", frequency=" + frequency ;
		if(leftTree!=null){
			itemString+=", left=" + leftTree.getString();
		}
		else{
			itemString+=", left=" + leftTree;
		}
		if(rightTree!=null){
			itemString+=", right=" + rightTree.getString()+"]\n";
		}
		else{
			itemString+=", right=" + rightTree+"]\n";
		}
		return itemString;
	}
	
	
	
}
