package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class CleaningParsedDataAlgo2 {
	
	String inputFolderPath;
	
	public static void main(String args[]){
		CleaningParsedDataAlgo2 obj = new CleaningParsedDataAlgo2(
				"/media/DriveE/NLP/dataFiles/parsed_train_data");
		obj.openDirectory();
	}
	
	CleaningParsedDataAlgo2(String inpDir){
		inputFolderPath = inpDir;
	}
	
	public void openDirectory(){
		File inputDir = new File(inputFolderPath);
		File[] fileList = inputDir.listFiles();
		for(int i = 0; i < fileList.length; i++)
			openFile(fileList[i]);
	}
	
	public void openFile(File SSFfile){
		ArrayList<String> fileContent = new ArrayList<String>();
		Scanner scn = null;
		try{
			scn = new Scanner(SSFfile);
		}catch(Exception e){
			System.out.println("Error in reading the file");
		}
		while(scn.hasNext())
			fileContent.add(scn.nextLine());
		scn.close();
		if(SSFfile.exists())
			SSFfile.delete();
		getRedundantSentencePositions(fileContent, 
				SSFfile.getAbsolutePath());
	}
	
	public void getRedundantSentencePositions(ArrayList<String> fileContent, 
			String filePath){
		ArrayList<String> cleanedData = new ArrayList<String>();
		for(int i = 0; i < fileContent.size(); i++){
			if(fileContent.get(i).contains("<Sentence id=") &&
					!fileContent.get(i+1).contains("(("))
				continue;
			else
				cleanedData.add(fileContent.get(i));
		}
		cleanSSFfile(cleanedData, filePath);
	}
	public void cleanSSFfile(ArrayList<String> cleanedData, String filePath){
		try{
			FileWriter fw = new FileWriter(filePath, true);
			for(int i = 0; i < cleanedData.size(); i++)
				fw.write(cleanedData.get(i)+"\n");
			fw.close();
		}catch(Exception e){
			System.out.println("Error in opening the writing file");
		}
	}
}
