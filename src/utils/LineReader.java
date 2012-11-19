/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leo
 */
public class LineReader {
	public static String[] readFile(String name) throws FileNotFoundException{
		List<String> content = new ArrayList<String>();
		System.out.println("about to read a file");
		File file = new File(name);
		System.out.println("file is located at : " + file.getAbsolutePath());
		BufferedReader buffRead = new BufferedReader(new FileReader(file));
		try {
			String line;
			while ((line = buffRead.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					content.add(line.trim());
				}
			}
			buffRead.close();
		} catch (IOException ex) {
			Logger.getLogger(LineReader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return content.toArray(new String[1]);
	}
}
