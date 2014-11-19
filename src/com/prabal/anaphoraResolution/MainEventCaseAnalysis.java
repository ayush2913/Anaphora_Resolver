package com.prabal.anaphoraResolution;

import java.io.FileNotFoundException;

public class MainEventCaseAnalysis {
	
	public static void main(String args[]) throws FileNotFoundException{
		
		EventCaseAnalysis ob = new EventCaseAnalysis("/media/DriveE/NLP/dataFiles/train");
		ob.openFolder();
	}

}
