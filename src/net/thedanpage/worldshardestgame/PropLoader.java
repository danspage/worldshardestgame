package net.thedanpage.worldshardestgame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropLoader {
	
	public static String loadProperty(String property, String filename) {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();           
		InputStream stream = loader.getResourceAsStream(filename);
		try {
			prop.load(stream);
			return prop.getProperty(property);
		} catch (IOException e) {
			TextFileWriter.appendToFile(Game.logFilePath, Game.getStringFromStackTrace(e));
		}
		return null;
	}
	
}
