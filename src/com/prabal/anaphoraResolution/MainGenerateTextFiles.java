package com.prabal.anaphoraResolution;

import java.io.FileNotFoundException;

public class MainGenerateTextFiles {

	public static void main(String args[]) throws FileNotFoundException{
		
		GenerateTextFiles ob = new
			GenerateTextFiles("/home/prabal/AR_testing/shallow",
					"/home/prabal/Pictures/data/output"
					);
		ob.openFolder();
	}
}
