package com.prabal.anaphoraResolution;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.*;
public class MainEventResolutionExp2 {
	
	public static void main(String args[]) throws FileNotFoundException{
		File dir = new File("/media/DriveE/NLP/dataFiles/prog_test");
		System.out.println(dir.getPath());
		File[] list = dir.listFiles();
		System.out.println(list[0]);
		EventResolutionExp2 ob = new
			EventResolutionExp2(list[0]);
		
		System.out.println(ob.relationDegree.size());
		ArrayList<ArrayList<String>> relations = new
			ArrayList<ArrayList<String>>();
		relations = ob.relationDegree;
		for(int i = 0; i < relations.size(); i++){
			System.out.println(relations.get(i));
		}
	}
}
