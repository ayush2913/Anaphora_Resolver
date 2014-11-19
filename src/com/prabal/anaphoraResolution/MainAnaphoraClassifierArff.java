package com.prabal.anaphoraResolution;

public class MainAnaphoraClassifierArff {
	
	public static void main(String args[]){
		
//		AnaphoraClassifierArff ob1 = new AnaphoraClassifierArff("/media/DriveE/NLP/dataFiles/testing/");
//		ob1.designstructureOfArff();
//		ob1.designFormat();
//		ob1.getFileList();
//		ob1.getFileContent();
		
//		BuildingClassifierModel ob2 = new BuildingClassifierModel();
//		ob2.train("trainFile.arff");
		
		TestClassifier ob3 = new TestClassifier("/media/DriveE/NLP/dataFiles/test/");
		ob3.readModel();
		ob3.getFileList();
		ob3.getFileContent();
		ob3.displayResults();
	}

}
