package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

public class GenerateTextFiles {

	String inputFolder;
	String outputFolder;
	
	GenerateTextFiles(String input, String output){
		
		inputFolder = input;
		outputFolder = output;
	}
	
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
	
	public void extractText(File file) throws FileNotFoundException{
		
		Scanner scn = new Scanner(file);
		Formatter fmt = new Formatter(new File(outputFolder+"/"+file.getName()));
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
			if(line.contains("fs ")==true && line.contains("Sentence")==false && line.isEmpty()==false){
				Scanner s = new Scanner(line);
				s.useDelimiter("\t|\\s");
				s.next();
				String word = s.next();
				if(word.equalsIgnoreCase("।")==true || word.equalsIgnoreCase(".")==true){
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
