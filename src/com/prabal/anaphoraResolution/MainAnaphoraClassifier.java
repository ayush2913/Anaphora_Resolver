package com.prabal.anaphoraResolution;

public class MainAnaphoraClassifier {
	
	public static void main(String args[]){
		AnaphoraClassifier ob = new AnaphoraClassifier("/home/prabal/AR_testing/full");
		ob.readModel();
		ob.getFileList();
		ob.getFileContent();
		ob.displayFile();
	}

}
