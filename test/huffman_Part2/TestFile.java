 /**
 * @author Ciara Power 20072488
 *
 */
package huffman_Part2;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import huffman_Part2.FileReader;
import huffman_Part2.FileWriter;

public class TestFile {
	private FileWriter fileWriter;
	private FileReader fileReader;
	
	@Test
	public void testRead() throws Exception {
		File in=new File("textFiles\\testData.txt");
		fileReader=new FileReader(in);
		assertEquals("test",fileReader.readTextFile(in));
		
		
	}

	@Test
	public void testWrite() throws Exception {
		
		fileWriter=new FileWriter(new File("textFiles\\testOutput.txt"));
		
		fileWriter.writeToFile("000100001");
		fileReader=new FileReader(new File("textFiles\\testOutput.txt"));
		assertEquals("0001000010000000",fileReader.readCompressedFile(new File("textFiles\\testOutput.txt")));  // with padded 0's
		
		fileWriter=new FileWriter(new File("textFiles\\testOutput.txt"));
		fileWriter.writeToFile("011");
		fileReader=new FileReader(new File("textFiles\\testOutput.txt"));
		assertEquals("01100000",fileReader.readCompressedFile(new File("textFiles\\testOutput.txt")));  // with padded 0's
		
		fileWriter=new FileWriter(new File("textFiles\\testOutput.txt"));
		fileWriter.writeToFile("01111110");
		fileReader=new FileReader(new File("textFiles\\testOutput.txt"));
		assertEquals("01111110",fileReader.readCompressedFile(new File("textFiles\\testOutput.txt")));  // no padding for 8 digits
		
		
		
	}

}
