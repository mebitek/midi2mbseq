package main.java.mebitek.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


/**
 * Helper class that writes a sequencer line into a print writer
 *
 * @author Claudio Melis
 */
public class MbseqFileWriter {

	private static final String ENCODE = "UTF-8";

	private PrintWriter writer;

	/**
	 * Creates a new instance
	 *
	 * @param fileName the filename
	 */
	public MbseqFileWriter(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		this.writer = new PrintWriter(fileName, ENCODE);
	}

	/**
	 * Print a line
	 *
	 * @param line the line to be printed
	 */
	public void print(String line) {
		this.writer.print(line);
	}

	/**
	 * Print new line
	 */
	public void println() {
		this.writer.println();
	}

	/**
	 * Close the writer
	 */
	public void close() {
		this.writer.close();
	}

	/**
	 * Initialize the line with the sequence number
	 *
	 * @param index the sequence number
	 */
	public void initLine(int index) {
		this.print(index + ":");
	}
}