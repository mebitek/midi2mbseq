package com.mebitek.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class MseqFileWriter {

	private static final String ENCODE = "UTF-8";

	private PrintWriter writer;

	public MseqFileWriter(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		this.writer = new PrintWriter(fileName, ENCODE);
	}

	public void print(String line) {
		this.writer.print(line);
	}

	public void println() {
		this.writer.println();
	}


	public void close() {
		this.writer.close();
	}

	public void initLine(int index) {
		this.print(index+":");
	}

}
