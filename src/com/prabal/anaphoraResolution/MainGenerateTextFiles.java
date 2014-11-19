package com.prabal.anaphoraResolution;

import java.io.FileNotFoundException;

public class MainGenerateTextFiles {

	public static void main(String args[]) throws FileNotFoundException{
		
		GenerateTextFiles ob = new GenerateTextFiles("/media/DriveE/NLP/dataFiles/testing/","/media/DriveE/NLP/dataFiles/texts");
		ob.openFolder();
	}
}
