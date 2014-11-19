package com.prabal.anaphoraResolution;

import java.io.FileNotFoundException;

public class MainEventResolutionExp2 {
	
	public static void main(String args[]) throws FileNotFoundException{
		EventResolutionExp2 ob = new EventResolutionExp2("/media/DriveE/NLP/dataFiles/Exp2/");
		ob.openDir();
	}

}
