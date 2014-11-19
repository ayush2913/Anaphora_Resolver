package com.prabal.anaphoraResolution;

import java.io.FileNotFoundException;

public class MainDependencyTree {

	public static void main(String args[]) throws FileNotFoundException{
		
		DependencyTree ob = new DependencyTree("/media/DriveE/NLP/dataFiles/testing");
		ob.openFiles();
	}
}
