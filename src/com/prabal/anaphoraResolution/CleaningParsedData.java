package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class CleaningParsedData {
	String inputDirectoryPath;
	
	public static void main(String args[]){
		CleaningParsedData obj = new CleaningParsedData(
				"/media/DriveE/NLP/dataFiles/parsed_train_data");
	}
	
	CleaningParsedData(String path){
		inputDirectoryPath = path;
		openDirectory();
	}
	
	public void openDirectory(){
		File dir = new File(inputDirectoryPath);
		File[] fileList = dir.listFiles();
		for(int i = 0; i < fileList.length; i++)
			cleanFileData(fileList[i]);
	}
	
	public void cleanFileData(File fileData){
		System.out.println(fileData.getName());
		ArrayList<String> fileContent = new ArrayList<String>();
		ArrayList<String> cleanedContent = new ArrayList<String>();
		Scanner scn = null;
		try{
			scn = new Scanner(fileData);
		}catch(Exception e){
			System.out.println("Error in opening the files");
		}
		while(scn.hasNext())
			fileContent.add(scn.nextLine());
		scn.close();
		for(int i = 0; i < fileContent.size(); i++){
			if(fileContent.get(i).contains("Conversion failed")){
				cleanedContent.set(cleanedContent.size()-1, "  ");
				i+=2;
			}
			else
				cleanedContent.add(fileContent.get(i));
		}
		
		File writeFile = new File(fileData.getAbsolutePath());
		if(writeFile.exists())
			writeFile.delete();
		try{
			FileWriter fw = new FileWriter(
					fileData.getAbsolutePath(), true);
			for(int i = 0; i < cleanedContent.size(); i++)
				fw.write(cleanedContent.get(i)+"\n");
			fw.close();
		}catch(Exception e){
			System.out.println("Error in opening the write file");
		}
	}
}
