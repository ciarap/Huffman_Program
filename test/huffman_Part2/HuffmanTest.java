 /**
 * @author Ciara Power 20072488
 *
 */
package huffman_Part2;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import huffman_Part2.Huffman;

public class HuffmanTest {
	private static Huffman huffman=new Huffman();
	private FileReader fileReader;
	
	
	
	@Test
	public void testReadString() {
		huffman.readString("ABCD");
		huffman.createItems();
		assertTrue(huffman.getCharMap().containsKey("A"));
		assertTrue(huffman.getCharMap().containsKey("B"));
		assertTrue(huffman.getCharMap().containsKey("C"));
		assertTrue(huffman.getCharMap().containsKey("D"));
		assertFalse(huffman.getCharMap().containsKey("E"));
		assertEquals(huffman.getCharMap().keySet().size(),4);
	}
	
	@Test
	public void testItemCreate(){
		huffman.readString("ABCD");
		huffman.createItems();
		assertEquals(4,Huffman.getQueue().size());
		assertEquals("A",Huffman.getQueue().peek().getString());
	}
	
	@Test
	public void testCompression() throws Exception{
		File in=new File("textFiles\\testData.txt");
		huffman.compressFile(in);
	    in=new File("textFiles\\testDataCompressed.txt");
		fileReader=new FileReader(in);
		assertEquals(13,in.length());  // compressed should be 13 bytes 
	}
	
	@Test
	public void testDecompression() throws Exception{
		File original=new File("textFiles\\testData.txt");
		File in=new File("textFiles\\testDataCompressed.txt");
		huffman.decompressFile(in);
	    in=new File("textFiles\\testDataDecompressed.txt");
		fileReader=new FileReader(in);
		assertEquals("test",fileReader.readTextFile(in));
		assertEquals(original.length(),in.length());  // original and decompressed same size
	}
	
	@After
	public void teardown(){
		huffman=new Huffman();
	}
}

