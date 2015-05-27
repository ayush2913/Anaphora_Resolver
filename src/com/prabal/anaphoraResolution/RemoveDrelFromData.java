package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class RemoveDrelFromData {
	String inputFolderPath;
	
	public static void main(String args[]){
		RemoveDrelFromData obj = new RemoveDrelFromData(
				"/home/prabal/AR_testing/AR");
		obj.getFileList();
	}
	
	RemoveDrelFromData(String folderPath){
		inputFolderPath = folderPath;
	}
	
	public void getFileList(){
		File inpFolder = new File(inputFolderPath);
		File[] fileList = inpFolder.listFiles();
		openFiles(fileList);
	}
	
	public void openFiles(File[] fileList){
		for(int i = 0; i< fileList.length; i++){
			File dataFile = new File(fileList[i].getPath());
			Scanner scn = null;
			try {
				scn = new Scanner(dataFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			ArrayList<String> fileContent = new ArrayList<String>();
			while(scn.hasNext()){
				fileContent.add(scn.nextLine());
			}
			fileContent = removeDrels(fileContent);
			try{
				File writeFile = new File(
						"/home/prabal/AR_testing/AR1"
						+fileList[i].getName());
				if(writeFile.exists())
					writeFile.delete();
				FileWriter fw = new FileWriter(
						"/home/prabal/AR_testing/AR1/"
						+fileList[i].getName(), true);
				for(int j = 0; j < fileContent.size(); j++){
					fw.write(fileContent.get(j)+"\n");
				}
				fw.close();
			}catch(Exception e){
				System.out.println("writing file not found");
			}
		}
	}
	
	public ArrayList<String> removeDrels(ArrayList<String> fileContent){
		ArrayList<String> drelRemoved = new ArrayList<String>();
		for(int i = 0; i < fileContent.size(); i++){
			if(fileContent.get(i).contains("drel=")){
				String line = "";
				Scanner scn = new Scanner(fileContent.get(i));
				scn.useDelimiter(" ");
				while(scn.hasNext()){
					String word = scn.next();
					if(word.contains("drel=")){
						if(word.endsWith(">"))
							line = line + ">";
					}
					else
						line = line + " " +word; 
				}
				scn.close();
				drelRemoved.add(line);
			}
			else
				drelRemoved.add(fileContent.get(i));
		}
		return drelRemoved;
	}
}