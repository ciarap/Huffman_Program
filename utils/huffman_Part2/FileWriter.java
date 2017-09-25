/**
 * @author Ciara Power 20072488
 **/
package huffman_Part2;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter {
	public static FileOutputStream out;
	private boolean debug=true;
	
	public FileWriter(File file) throws FileNotFoundException{
		out = new FileOutputStream(file);
		}
	
	/*
	 *  taking a binary string as parameter, writes it to a text file
	 */
	public void writeToFile(String binary) throws IOException {
		if (debug) System.out.println("To Be Parsed: "+binary);
		
		String eightBinaryString="";    // write 8 bits at one time (byte)
		
		if(binary.length()<8){    // if less than 8 string bits left in string
			eightBinaryString= binary;   // add whatever bits left 
			while (eightBinaryString.length()<8){  // add 0 until the byte is complete for padding
				eightBinaryString+="0";
			}
		}
		else{
		eightBinaryString= binary.substring(0,8);    // take first 8 digits of the string
		}
		if (debug) System.out.println("Byte Binary String: "+eightBinaryString);
		int val= Integer.parseUnsignedInt(eightBinaryString,2); // converts the binary string to an integer
		if (debug) System.out.println("Integer val of Binary String: "+val);
		byte b=(byte) val;  // converts the int to a byte
		out.write(b); // write the byte to a file
		
		if(binary.length()>8){  // if there is more than the 8 digits just checked left in the string
		binary=binary.substring(8);  // set it to be from index 8 
		writeToFile(binary);   // recurse for this shorter string of binary 
		}
	}

	/*
	 * Method to write chars to a file as compressed file data is decompressed
	 */
	public void writeToDecompressedFile(char letter) throws IOException {
		if (debug) System.out.println("To Be Written Out: "+letter);
		if(letter=='_'){ // _ used for space ' ' in program for ease of reading in displays
			out.write(' '); // write as ' ' to equal actual file
		}
		else out.write(letter); // write char (default in ascii)
	}

	/*
	 * method called from main program to end the file output stream
	 */
	public void end() throws IOException {
		out.close();
		
	}
}
