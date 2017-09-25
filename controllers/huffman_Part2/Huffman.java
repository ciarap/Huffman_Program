/**
 * @author Ciara Power 20072488
 *
 */
package huffman_Part2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import huffman_Part2.BinaryTreeIndentPrint;
import huffman_Part2.FileReader;
import huffman_Part2.FileWriter;
import huffman_Part2.Item;

public class Huffman {

	private static boolean debug=true;

	private HashMap<String,Integer> charMap=new HashMap<>(); // used for frequency count
	private static HashMap<String,String> binaryMap=new HashMap<>();  // binary conversions

	private static Huffman huffman=new Huffman();
	private static FileReader fileReader;
	private static FileWriter fileWriter;
	

	private static JFileChooser fc;

	private static PriorityQueue<Item> queue=new PriorityQueue<Item>();
	private static BinaryTreeIndentPrint printer=new BinaryTreeIndentPrint();
	private Item root;  // the root of the tree

	private static String binaryTreeString="";  // full huffman code tree in binary
	private static String dataForWriteOut="";  // total string of binary digits to be written to a file

	private static String eof="*";  // enf od file char needed for compression and decompression
	private static String identifier="0CADD099";
	long elapsed;  //time elapsed for compression or decompression
	double newFileSize;  //newly created file size
	double originalFileSize;  // file opened size before compression/decompression


	public static void main(String[] args) throws IOException{
		Shell shell = ShellFactory.createConsoleShell("lm", "Welcome to the Huffman Compression/Decompression Program - ?help for instructions",huffman);
		shell.commandLoop();
	}
	public Huffman() {
		fc= new JFileChooser(System.getProperty("user.dir")+"\\textFiles");  // starts at directory of textFiles folder in this project folder
		FileFilter filter = new FileNameExtensionFilter("Text File","txt");  // only shows text files in file browser
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);  // dont have option in dropdown for all files to be shown

	}
	/*
	 * Method acts on input to shell console for compression
	 */
	@Command (description="Select File For Compression")  
	public void fileSelectCompress () throws Exception
	{
		int returned=fc.showOpenDialog(null);  //get file to open
		if(JFileChooser.CANCEL_OPTION==returned){  //if the cancel button was pressed cancel out of program
			JOptionPane.showMessageDialog(fc, "Program Cancelled");
		}
		else if(fc.getSelectedFile().exists() && fc.getSelectedFile().length()>0)  // if the file exists and contains data
			compressFile(fc.getSelectedFile()); //continue with compression
		else{  // doesnt exist or empty file
			JOptionPane.showMessageDialog(fc, "Please Choose a File That Exists, and contains data!");
			fileSelectCompress();  //repeat 
		}
	}
	
	/*
	 * Method to display text from any text file
	 */
	@Command (description="Display File Text")  
	public void displayFileText () throws Exception
	{
		int returned=fc.showOpenDialog(null);  //choose file
		if(JFileChooser.CANCEL_OPTION==returned){  //if the cancel button was pressed cancel out of program
			JOptionPane.showMessageDialog(fc, "Program Cancelled");
		}
		else if(fc.getSelectedFile().exists() && fc.getSelectedFile().length()>0){  // if the file exists and contains data
			fileReader=new FileReader(fc.getSelectedFile());
			String data=fileReader.readTextFile(fc.getSelectedFile()); //get data from file
			if(data.length()==0){  // if it was not plain text then data will show empty having read the unempty file
				data=fileReader.readCompressedFile(fc.getSelectedFile()); // compressed file so get binary string instead
			}
			while(data.length()>0){  // while data left to print
				if(data.length()>=100){  // if greater than 100 in length
				System.out.println(data.substring(0,100));  //print on line
				data=data.substring(100);  //cut off what was printed
				}
				else { //shorter than 100 
					System.out.println(data); //print whatever is left in string
					data=""; // empty
				}
			}
		}
		else{  // doesnt exist or empty file
			JOptionPane.showMessageDialog(fc, "Please Choose a File That Exists, and contains data!");
			displayFileText();  //repeat 
		}
	}
	

	/*
	 * Method acts on input to display the most recently generated tree
	 */
	@Command (description="Display Recently Generated Tree")  
	public void displayRecentlyGeneratedTree () throws Exception
	{
		if(root!=null) printer.printPreOrder(root," ","B");   // if there is a tree stored, root will have a value , then print tree
		else System.out.println("Please compress/decompress a file first to display the tree");  // no root value yet so no tree
	}

	/*
	 * Method acts on input to display statistics from most recent compression/decompression
	 */
	@Command (description="Display Recent File Statistics") 
	public void displayRecentFileStatistics() throws Exception
	{
		if(root!=null){ // if there is a root stored, there is statistics stored also
			System.out.println("---------\nOriginal File Size: "+originalFileSize+
					" bytes\nNew File Size: "+newFileSize+" bytes\nTime elapsed: "+elapsed+"ms\nFile Size Improvement: "+((originalFileSize-newFileSize)/originalFileSize)*100+"%");

		}
		else System.out.println("Please compress/decompress a file first to get statistics");  //no info stored
	}

	/*
	 * Method acts on input to display the most recent frequency table (after compression)
	 */
	@Command (description="Display Recent Character Frequency Table") 
	public void displayRecentCharFreq() throws Exception
	{
		if(!charMap.isEmpty()){ // if not an empty freq table
			displayFreqMap();
		}
		else System.out.println("Please compress a file first to get character frequencies");  // empty freq table
	}

	/*
	 * Method acts on input to display most recent binary mappings of characters
	 */
	@Command (description="Display Recent Binary Mappings") 
	public void displayRecentBinMap() throws Exception
	{
		if(!binaryMap.isEmpty()){  // if not an empty map
			displayBinaryMap();
		}
		else System.out.println("Please compress a file first to get binary mappings");  // empty map
	}

	/*
	 * method acts on input to decompress a file
	 */
	@Command (description="Select File For Decompression")  //enter file name/path
	public void fileSelectDecompress () throws Exception
	{
		int returned= fc.showOpenDialog(null);  // file chooser for open file, returns value for approve,cancel option pressed
		if(JFileChooser.CANCEL_OPTION==returned){  //cancel was pressed
			JOptionPane.showMessageDialog(fc, "Program Cancelled");   
		}
		else if(fc.getSelectedFile().exists()&& fc.getSelectedFile().length()>0)   // file exists and file is not empty
			decompressFile(fc.getSelectedFile());  //continue with decompression
		else{
			JOptionPane.showMessageDialog(fc, "Please Choose a File That Exists, and contains data!");
			fileSelectDecompress();  // loop until valid file selected for decompression
		}   
	}

	/*
	 * Method to compress a file, with input parameter the file to be opened
	 */
	public void compressFile(File file) throws Exception{
		reset();  // reset method called
		System.out.println("Compressing...");
		long start=System.currentTimeMillis();  //start timer

		fileReader=new FileReader(file);  // to read the file chosen for compression
		originalFileSize=file.length();    // original size in bytes
		String fileText=fileReader.readTextFile(file);  // create string from file text
		fileText+=eof;   // end of file character added to data so to input into tree 

		readString(fileText);  // parse the string
		if (debug) displayFreqMap();  

		createItems();   // create items 
		createTree();  //create the tree structure

		String binary="";  //used in creating binary representation for each leaf
		createBinaryMappings(root,binary);  // goes through tree to create binary mappings
		if (debug) displayBinaryMap();

		convertTreeToString(root); // convert tree to binary string 

		if(debug) System.out.println("--------------------------");
		int i = Integer.parseInt(identifier, 16);  // get the hex string as integer
		String HexToBinaryString = Integer.toBinaryString(i);  //change the integer to binary string
		while(HexToBinaryString.length()<identifier.length()*4){   //if there is less than 4 bits for each letter from string, leading 0's cut out
			HexToBinaryString= "0"+HexToBinaryString;  //add in 0
		}
		dataForWriteOut+=HexToBinaryString;   // "0CADD099" in binary as identifier
		if(debug) System.out.println("Header Identifier:"+ dataForWriteOut);

		String treeLength=Integer.toBinaryString(binaryTreeString.length());  // convert the length of the tree binary string to a binary string
		while(treeLength.length()<16){  // make the binary string for tree length be 16 bits (allows for large trees)
			treeLength= "0"+treeLength;
		}
		dataForWriteOut+=treeLength;  // add tree length after identifier
		if(debug) System.out.println("Binary Tree Compressed Binary Size: "+binaryTreeString.length()+"\nBinary Size: "+treeLength);

		dataForWriteOut+=binaryTreeString; //added on tree binary string
		if (debug) System.out.println("Added Binary Tree: "+dataForWriteOut);

		dataForWriteOut+=convertDataToBinaryString(fileText); // text data from file in binary string
		if (debug) System.out.println("Added data: "+dataForWriteOut);

		if(debug) System.out.println("--------------------------");

		long beforeSaveChoice=System.currentTimeMillis();
		int returned= fc.showSaveDialog(null);   // open dialog file chooser for choosing the save file
		long afterSaveChoice=System.currentTimeMillis();
		if(JFileChooser.CANCEL_OPTION==returned){
			JOptionPane.showMessageDialog(fc, "Program Cancelled");
		}
		else{
			
			File compressedFile= checkSaveFileType(fc.getSelectedFile().getName());  // checks the file name to check its text file, and to add .txt suffix if not 
			fileWriter=new FileWriter(compressedFile); // file writer for the chosen saved file
			fileWriter.writeToFile(dataForWriteOut);  // write the data (identifier,tree length,data,eof,padding 0's )

			newFileSize=compressedFile.length(); // size of the new file created
			long end=System.currentTimeMillis();  //end time
			elapsed= end-start-(afterSaveChoice-beforeSaveChoice);  //elapsed time since start of compression (not incl time spent in filechooser because user dependant)

			System.out.println("---------\nOriginal File Size: "+originalFileSize+
					" bytes\nCompressed File Size: "+newFileSize+" bytes\nTime elapsed: "+elapsed+"ms\nSpace Saving: "+((originalFileSize-newFileSize)/originalFileSize)*100+"%");
			fileWriter.end();
		}
		fileReader.end(); 
	}

	
	/*
	 * Method to check the file type for saving is a txt file ( must be checked if file name is input as text to dialog)
	 * Takes input parameter of new file name
	 */
	private File checkSaveFileType(String name) {
		File newFile;
		if(name.length()>=4){ // if the file name is greater than 4 (long enough to have .txt)
			if(name.substring(name.length()-4).equals(".txt")){ // if the last 4 chars is ".txt" 
				if (debug) System.out.println("Save File Is TXT"); 
				newFile=fc.getSelectedFile();  // set the file as the name entered it is acceptable
			}
			else {
				newFile=new File("textFiles\\"+name+".txt");  // .txt isnt included, so add the path to go into textFiles folder, and add .txt on end
				if (debug) System.out.println("Save File Is Long Enough But NOT TXT");
			}
		}
		else {
			newFile=new File("textFiles\\"+name+".txt"); //.txt isnt included, so add the path to go into textFiles folder, and add .txt on end
			if (debug) System.out.println("Save File Is short so NOT TXT");
		}

		return newFile;
	}

	/*
	 * method to reset global variables (to reset after each compression/decompression event)
	 */
	public void reset() {
		charMap.clear();
		binaryMap.clear();
		queue.clear();
		binaryTreeString="";
		dataForWriteOut="";
        root=null;
	}

	/*
	 * Method to decompress a compressed file taken in as parameter
	 */
	public void decompressFile(File file) throws IOException {
		reset();  //reset values from previous compression/decompressions
		System.out.println("Decompressing...");
		long start=System.currentTimeMillis(); //start timer
		fileReader=new FileReader(file);  //to read the file input as parameter to be opened

		originalFileSize=file.length(); // compressed file size in bytes
		if (debug) System.out.println("Compressed Size: "+originalFileSize+" bytes");
		String readInBinary=fileReader.readCompressedFile(file);  // returns the read in binary as string
		if(debug)System.out.println("Binary read in: "+readInBinary);

		int decimal = Integer.parseInt(readInBinary.substring(0, 32),2);  // converts first 32 digits from base 2 to an integer
		if(debug)System.out.println("Read In Header Decimal Format: "+decimal);
		String hexStr = Integer.toString(decimal,16).toUpperCase(); // converts binary string to base 16 HEX string uppercase
		while(hexStr.length()<8)  hexStr="0"+hexStr; // if shorter than 8 chars, leading 0's were cut off , so add back on 
		if(debug)System.out.println("Read In Header Hex Format: "+hexStr); 

		if(hexStr.equals(identifier)){  // if the identifier is correct ( file was compressed by this program)
			readInBinary=readInBinary.substring(32); // cut out the identifier we have already checked it - not needed anymore
			readInBinary=removeAndParseTreeBinary(readInBinary);  // pass rest of data to method that deals with the tree data 
			if(debug) System.out.println("Data Before Decode: "+readInBinary);

			long beforeSaveChoice=System.currentTimeMillis();
			int returned=fc.showSaveDialog(null);   // opens file chooser to choose a file to save decompression to, returns if approve or cancel pressed as int
			long afterSaveChoice=System.currentTimeMillis();
			if(JFileChooser.CANCEL_OPTION==returned){  //cancel pressed
				JOptionPane.showMessageDialog(fc, "Program Cancelled");
			}else{
				File decompressedFile= checkSaveFileType(fc.getSelectedFile().getName()); // check the file has .txt suffix, add .txt if not
				fileWriter=new FileWriter(decompressedFile);
				decodeData(readInBinary,fileWriter); // decode the data section of the compressed binary string 

				long end=System.currentTimeMillis();  // end time
				elapsed=end-start-(afterSaveChoice-beforeSaveChoice);  //get time decompressing (not incl time spent in filechooser because user dependant)

				newFileSize=decompressedFile.length(); //size in bytes of new file

				System.out.println("---------\nCompressed File Size: "+originalFileSize+
						" bytes\nDecompressed File Size: "+newFileSize+" bytes\nTime elapsed: "+elapsed+"ms\nFile Size Improvement: "+((originalFileSize-newFileSize)/originalFileSize)*100+"%");
				fileWriter.end();
			}
			fileReader.end();

		}
		else{
			System.out.println("Incorrect Identifier - Please Choose a file compressed by this program!");  // wrong identifier on compressed file read in, program decompression ends
		}

	}

	/*
	 * Method to remove the tree data in the string of file data passed in as parameter, and use it to create tree
	 */
	private String removeAndParseTreeBinary(String readInBinary) {
		short treeLength= Short.parseShort(readInBinary.substring(0,16),2); // treelength is the 16 digits, convert to short value
		if(debug) System.out.println("Tree Length: "+treeLength); 
		binaryTreeString=readInBinary.substring(16,16+treeLength); // get substring from index 16 til 16 + how many chars in tree data
		if(debug) System.out.println("Tree Binary Code: "+binaryTreeString+"\nTree Binary Code Length: "+binaryTreeString.length());
		createTreeFromBinary(root); // creates the tree from binary string , passes null as no root node yet
		if (debug)printer.printPreOrder(root,"","B");
		return readInBinary.substring(16+treeLength);  // return the remaining binary string (without tree length and tree binary)
	}

	/*
	 * Method to create the tree structure from the binary string read from compressed file
	 */
	private void createTreeFromBinary(Item node) {
		if(node==null){  // if a null value passed in, no tree nodes created yet so create an empty root node 
			root=new Item("",null,null);
			createTreeFromBinary(root); // pass the empty root node to same method - recursion
		}
		else{ 
			if(binaryTreeString.charAt(0)=='1' && node.getLeftTree()==null){  // if value is 1 and no left tree attached yet, next step is traverse left
				if (debug)System.out.println("LEFT");
				node.setString("parent");  // not a leaf so set string as parent for ease of reading tree display 
				node.setLeftTree(new Item("",null,null));  // want to traverse left so create empty item for left tree
				binaryTreeString=binaryTreeString.substring(1); // take off the digit just checked from string
				createTreeFromBinary(node.getLeftTree()); // recurse for left tree item
				if(debug && binaryTreeString.length()==0) System.out.println("LEFT,Tree Binary Empty");
			}
			if(node.getLeftTree()!=null){   // left has already been created for this item , so must go right
				if (debug)System.out.println("RIGHT");
				if( binaryTreeString.charAt(0)=='1'){  // if digit is 1 then non leaf item
					node.setString("parent");  //set to parent
					node.setRightTree(new Item("",null,null)); //want to traverse right tree next
					binaryTreeString=binaryTreeString.substring(1); //cut off digit just checked
					createTreeFromBinary(node.getRightTree());  //recurse for right tree
					if(debug && binaryTreeString.length()==0) System.out.println("RIGHT,Tree Binary Empty");
				}
			}
			if (binaryTreeString.length()!=0){  // if there is data left to parse 
				if(binaryTreeString.charAt(0)=='0'){ // if digit is 0 then leaf
					if (debug)System.out.println(binaryTreeString.substring(1,9));
					node.setString(""+(char)Byte.parseByte (binaryTreeString.substring(1,9),2)); // sting for leaf character converted from byte to char (ascii used here)
					node.setLeftTree(null);  //leaves have no subtrees
					node.setRightTree(null);
					binaryTreeString=binaryTreeString.substring(9); // remove digits relating to leaf item
					
					if(debug && binaryTreeString.length()==0)System.out.println("ITEM,Tree Binary Just Emptied\nBinary Tree String:"+binaryTreeString);
				}
			}
		}

	}

	/*
	 * Method to decode the message part of the binary string read in , and write to a file
	 */
	private void decodeData(String readInBinary,FileWriter fileWriter) throws IOException{
		Item parent=root;  // set the parent as the root node for starters
		boolean eofFound=false;  // eof hasnt been reached yet
		if (debug)System.out.println(eof); 
		while(!eofFound){  //continue until eof found
			if (debug)System.out.println("Data: "+readInBinary);
			if(readInBinary.charAt(0)=='0'){   // 0 represents go left
				Item child=parent.getLeftTree();  // left child tree
				if(child.getLeftTree()==null && child.getRightTree()==null){  //leaf node
					if(child.getString().equals(eof)){  // if the leaf is eof 
						eofFound=true;
						if (debug)System.out.println("EOF Found: "+child.getString());
						break;
					}
					else  // not eof in leaf
					if(debug) System.out.println("Write: "+(char)child.getString().charAt(0));
					fileWriter.writeToDecompressedFile((char)child.getString().charAt(0));  // write the character tothe file
					parent=root;  // go back up to the start for next sequence
				}
				else{
					parent=child;  // not a leaf so continure traversing down this path , go from the child node next time
				}
			}
			else if(readInBinary.charAt(0)=='1'){  // 1 represents go right
				Item child=parent.getRightTree(); // right child
				if(child.getRightTree()==null && child.getLeftTree()==null){  //leaf 
					if(child.getString().equals(eof)){  //eof leaf found
						eofFound=true;
						if (debug)System.out.println("EOF Found: "+child.getString());
						break;
					}
					else{ // not eof 
					if(debug) System.out.println("Write: "+(char)child.getString().charAt(0));
					fileWriter.writeToDecompressedFile((char)child.getString().charAt(0)); //write character to file
					parent=root;  // start from root again for next sequence
					}
				}
				else{
					parent=child; // keep traversing down this path
				}
			}
			readInBinary=readInBinary.substring(1);  // take off digit just checked 
		}
	}

	/*
	 * Method to convert the data read from the file to a string of the corresponding binary digits from huffman code
	 */
	public String convertDataToBinaryString(String data) {
		String binaryData="";
		for(int i=0;i<data.length();i++){  //goes through whole string
			if(data.charAt(i)==' '){    // if the data is a space, I used _ in freq tables
				binaryData+=binaryMap.get("_");
			}
			else{
				binaryData+=binaryMap.get(""+data.charAt(i));  //get binary code for character 
			}
		}
		if(debug) System.out.println("Data in Binary: "+binaryData);
		return binaryData;
	}


	/*
	 * traversal down the tree from root , adds a "1" to string if a node is not a leaf, adds "0" and the character
	 *  ascii binary string if it is a leaf
	 */

	private void convertTreeToString(Item root) throws IOException {
		if(debug)System.out.println("Updated Tree: "+binaryTreeString);
		if (root.getLeftTree()==null && root.getRightTree()==null){  // leaf node
			binaryTreeString+="0";  // represents leaf
			String charAscii=Integer.toBinaryString((int)root.getString().charAt(0)); // get binary string value of the integer value of char (ascii)
			while(charAscii.length()<8) charAscii="0"+charAscii;  // add back on leading 0's
			binaryTreeString+=charAscii;  //binary string for character added 
			if (debug)System.out.println(root.getString()+":"+charAscii); 

		}
		if(root.getLeftTree() != null){   // has a left tree
			binaryTreeString+="1";  //not a leaf
			convertTreeToString(root.getLeftTree());  //keep traversing down left side
		}
		if(root.getRightTree() != null){  // gets here after checking left tree (if any)
			binaryTreeString+="1";
			convertTreeToString(root.getRightTree()); // traverse right tree
		}
	}


	public HashMap<String, Integer> getCharMap() {
		return charMap;
	}



	public static HashMap<String, String> getBinaryMap() {
		return binaryMap;
	}



	public static String getBinaryTreeString() {
		return binaryTreeString;
	}



	public static String getDataForWriteOut() {
		return dataForWriteOut;
	}

	/*
	 * Displays the binary mapping of each char
	 */
	private void displayBinaryMap() {
		for(String character:binaryMap.keySet()){
			System.out.println(character+" : "+ binaryMap.get(character));
		}
	}

	/*
	 * Displays the frequency mapping of each char
	 */
	private void displayFreqMap() {
		for(String character:charMap.keySet()){
			System.out.println(character+" : "+ charMap.get(character));
		}
	}

	/*
	 * when queue is built, this method creates the tree structure
	 */
	public void createTree() {
		while(queue.size()>1){ // more than 1 in queue
			if(debug)System.out.println( "UPDATED QUEUE: "+queue.toString());
			Item smallest= queue.remove();  //smallest in queue based on freq
			Item secondSmallest=queue.remove(); // second smallest in queue based on freq
			String combinedString=smallest.getString()+secondSmallest.getString(); //combining strings
			int combinedFreq=smallest.getFrequency()+secondSmallest.getFrequency(); //combining frequencies
			Item combination=new Item(combinedString,combinedFreq,secondSmallest,smallest);  //create combination item with smallest and 
			//second smallest as left and right trees
			queue.add(combination);  // add the combo item back to queue
		}
		root=queue.remove();
		if(debug)printer.printPreOrder(root," ","B");  // print the tree in a readable format
	}

	public static PriorityQueue<Item> getQueue() {
		return queue;
	}

	/*
	 * For each string in the frequency table, create an item with null children for now
	 */
	public void createItems() {
		for(String string:charMap.keySet()){
			queue.add(new Item(string,charMap.get(string),null,null));  //null children until tree structure created
		}
	}

	/*
	 * traverses pre-order through the tree, adding 1 or 0 to a string depending on direction
	 * The binary value is then put into a map with the item string as key
	 */
	private void createBinaryMappings(Item item,String binary){
		if(item.getLeftTree() != null){
			createBinaryMappings(item.getLeftTree(),binary+"0");
		}

		if(item.getRightTree() != null){
			createBinaryMappings(item.getRightTree(),binary+"1");

		}
		if (item.getLeftTree()==null && item.getRightTree()==null){
			binaryMap.put(item.getString(),binary);  //leaf node , put the values into map
		}
	}

	/*
	 * Reads the string passed as a parameter, which is the data string read from the file
	 * For each character, jump to the check method which deals with frequencies
	 */
	public void readString(String string){
		for (int i=0;i<string.length();i++){
			if(string.charAt(i)==' '){
				check('_');
			}
			else{
				check(string.charAt(i));
			}
		}
	}

	/*
	 * if the char is already in the freq table, replace it with same char but incremented freq value
	 * otherwise, just enter the character with freq of 1
	 */
	public void check(char character) {
		if(charMap.isEmpty()){
			charMap.put(""+character,1);
		}
		else{
			if(charMap.containsKey(""+character)){
				charMap.replace(""+character,charMap.get(""+character)+1);
			}
			else{
				charMap.put(""+character,1);
			}
		}
	}
}

