package net.thedanpage.worldshardestgame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class LogZipper {
	public static void zipLog() {
		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fos = new FileOutputStream(
					Game.logFilePath + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(Game.logFilePath.replace(
					System.getProperty("user.home")
							+ "/worldshardestgame/logs/", ""));
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(Game.logFilePath);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
			zos.closeEntry();

			// remember close it
			zos.close();
			
			if (new File(Game.logFilePath).exists()) new File(Game.logFilePath).delete();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void unzipLog() {
		try {
			ZipFile zipFile = new ZipFile(Game.logFilePath + ".zip");
			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();
	
				String name = zipEntry.getName();
	
				File file = new File(name);
				if (name.endsWith("/")) {
					file.mkdirs();
					continue;
				}
	
				File parent = file.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}
	
				InputStream is = zipFile.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = is.read(bytes)) >= 0) {
					fos.write(bytes, 0, length);
				}
				is.close();
				fos.close();
	
			}
			zipFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void unzip(String destinationFolder, String zipFile) {
		File directory = new File(destinationFolder);
        
		// if the output directory doesn't exist, create it
		if(!directory.exists()) 
			directory.mkdirs();

		// buffer for read and write data to file
		byte[] buffer = new byte[2048];
        
		try {
			FileInputStream fInput = new FileInputStream(zipFile);
			ZipInputStream zipInput = new ZipInputStream(fInput);
            
			ZipEntry entry = zipInput.getNextEntry();
            
			while(entry != null){
				String entryName = entry.getName();
				File file = new File(destinationFolder + File.separator + entryName);
                
				System.out.println("Unzipping file " + entryName + " to " + file.getAbsolutePath());
                
				// create the directories of the zip directory
				if(entry.isDirectory()) {
					File newDir = new File(file.getAbsolutePath());
					if(!newDir.exists()) {
						boolean success = newDir.mkdirs();
						if(success == false) {
							System.out.println("Problem creating Folder");
						}
					}
                }
				else {
					FileOutputStream fOutput = new FileOutputStream(file);
					int count = 0;
					while ((count = zipInput.read(buffer)) > 0) {
						// write 'count' bytes to the file output stream
						fOutput.write(buffer, 0, count);
					}
					fOutput.close();
				}
				// close ZipEntry and take the next one
				zipInput.closeEntry();
				entry = zipInput.getNextEntry();
			}
            
			// close the last ZipEntry
			zipInput.closeEntry();
            
			zipInput.close();
			fInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}