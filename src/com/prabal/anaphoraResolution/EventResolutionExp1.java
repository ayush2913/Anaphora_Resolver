package com.prabal.anaphoraResolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class EventResolutionExp1 {

	String inpFolderName;
	String outFolderName;

	EventResolutionExp1(String inpFolder, String outFolder) {
		inpFolderName = inpFolder;
		outFolderName = outFolder;
	}

	public void openFolder() throws FileNotFoundException {
		File f = new File(inpFolderName);
		File fileList[] = f.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			getDetailsAssociated(fileList[i]);
		}
	}

	public void getDetailsAssociated(File textFile)
			throws FileNotFoundException {
		System.out.println(textFile.getName());
		File outFile = new File(outFolderName + "/" + textFile.getName()
				+ ".txt");
		if (outFile.exists() == true)
			outFile.delete();

		Scanner scn = new Scanner(textFile);
		while (scn.hasNext()) {
			String word = getWord(scn.nextLine());
			if (word.length() > 0) {
				try (PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(outFolderName + "/" + textFile.getName()
								+ ".txt", true)))) {
					out.println(String.format(word));
				} catch (IOException e) {
					System.out.println("error in appending the database file");
				}
			}
		}
		scn.close();
	}

	public String getWord(String line) {
		if (line.contains("\tNNP\t") == true
				|| line.contains("\tNNPC\t") == true) {
			String word = extractWord(line);
			return word;
		} else if (line.contains("\tNN\t") == true
				|| line.contains("\tNNC\t") == true) {
			String word = extractWord(line);
			return "\t\t" + word;
		} else if (line.contains("\tVM\t") == true) {
			String word = extractWord(line);
			return "\t\t\t\t" + word;
		} else if (line.contains("\tPRP\t") == true
				&& line.contains("reftype='V'") == true) {
			String word = extractWord(line);
			return "\t\t\t\t\t\t" + word;
		} else if (line.contains("\tPRP\t") == true
				&& line.contains("reftype='N'") == true) {
			String word = extractWord(line);
			return "\t\t\t\t\t\t\t\t" + word;
		} else if (line.contains("</Sentence>") == true) {
			return "------------------------------------------------------------------------------------";
		}
		return "";
	}

	public String extractWord(String line) {
		Scanner s = new Scanner(line);
		s.useDelimiter("\t");
		s.next();
		return s.next();
	}
}
