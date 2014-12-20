package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

// This class can be used to generate raw text files from SSF data. Generation
// of such a file has been done to view the content of the entire corpus with
// ease, which can be helpful to analyze the inter-sentential relations
public class GenerateTextFiles {

	String inputFolder;
	String outputFolder;
	
	// This is a constructor ot set the path to directory from which the 
	// files has to be read and the path to the directory in which the raw
	// text files has to be stored	
	GenerateTextFiles(String input, String output){
		
		inputFolder = input;
		outputFolder = output;
	}
	
	// This function is used to open each file in the directory and call the
	// extractText() function for each of the SSF file
	public void openFolder() throws FileNotFoundException{
			
		File folder = new File(inputFolder);
		File[] fileList = folder.listFiles();
		
		File outFolder = new File(outputFolder);
		File[] outLists = outFolder.listFiles();
		for(int i=0;i<outLists.length;i++){
			outLists[i].delete();
		}
		for(int i=0; i<fileList.length;i++){
			extractText(fileList[i]);
		}
	}
	
	//This function is used to to extract just the Hindi data (raw data) in
	//the inout file and store the extracted data into another text file in
	//the output folder
	public void extractText(File file) throws FileNotFoundException{
		
		Scanner scn = new Scanner(file);
		Formatter fmt = new Formatter(new File(outputFolder+"/"+
					file.getName()));
		while(scn.hasNext()){
			String line = scn.nextLine();
			if(line.contains("</Sentence>")==true){
				System.out.println();
				fmt.format("\n");
			}
			if(line.contains("))")==true){
				System.out.print("))"+" ");
				fmt.format(")) ");
			}
			if(line.contains("fs ")==true && 
					line.contains("Sentence")==false && 
					line.isEmpty()==false){
				Scanner s = new Scanner(line);
				s.useDelimiter("\t|\\s");
				s.next();
				String word = s.next();
				if(word.equalsIgnoreCase("।")==true || 
					       word.equalsIgnoreCase(".")==true){
					System.out.print(word+" ");
					fmt.format("%s ", word);
				}
				else{
					System.out.print(word+" ");
					fmt.format("%s ", word);
				}
			}
				
		}
		System.out.println("\n");
		scn.close();
		fmt.close();
	}
}
