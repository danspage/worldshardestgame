package net.thedanpage.worldshardestgame;

//  JavaFileAppendFileWriterExample.java
//  Created by <a href="http://alvinalexander.com" title="http://alvinalexander.com">http://alvinalexander.com</a>

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileWriter {

	static BufferedWriter bw = null;

	public static void appendToFile(String filepath, String s) {
		try {
			// APPEND MODE SET HERE
			bw = new BufferedWriter(new FileWriter(filepath, true));
			bw.write(s);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			System.out.println(e);
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally
	}

} // end class