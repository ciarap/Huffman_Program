 /**
 * @author Ciara Power 20072488
 *
 **/
package huffman_Part2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;


public class FileReader {
	
	public static FileInputStream in;
	private boolean debug=true;
	
	public FileReader(File file) throws FileNotFoundException{
	in = new FileInputStream(file);
	}
	
	/*
	 * method to read the txt file and return the text data as string , takes filename in as parameter
	 */
	public String readTextFile(File inFile) throws Exception{   
		String data=""; // will end up as full data text file as string
		Scanner inLine = new Scanner(inFile);
		while (inLine.hasNextLine()) {     //while the txt file has not reached an end
			String textLine = inLine.nextLine();
			if (debug) System.out.println("Line: "+textLine);
			data+=textLine;  // add that line of text to string 
		}
		inLine.close(); // close scanner
		if (debug) System.out.println("Full String From File: "+data);
		return data;
	}
	
	
	/*
	 * method called from main program to end the file output stream
	 */
	public void end() throws IOException {
		in.close();
		
	}

	public String readCompressedFile(File inFile) throws IOException {
		String data=""; // will end up as full data text file as string
		byte byteArray[] = new byte[(int) inFile.length()];  // creates byte array for byte length of file
		in.read(byteArray);
		
		if(debug)System.out.println("Available:"+in.available()+"\nRead in: "+in.read(byteArray));
		
		for(byte b:byteArray){ // loops for amount of bytes in array created previously 
			String byteString=Integer.toBinaryString(Byte.toUnsignedInt(b)); // converts byte to int, and int to binary string
			while(byteString.length()<8)  // adds on leading 0's that may have been disregarded in conversions
				byteString="0"+byteString;
			if(debug)System.out.println(byteString);
			data+=byteString; // add to data which will be returned
		}
		if(debug)System.out.println(data.length());
		return data;
	}
}
