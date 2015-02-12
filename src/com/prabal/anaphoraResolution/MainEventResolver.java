package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainEventResolver{
	public static void main(String args[]){
		File learningFile = new File("learningDataEvent.csv");
		if(learningFile.exists())
			learningFile.delete();
		try {
			FileWriter fw = new FileWriter("learningDataEvent.csv", true);
			fw.write("m,Jtrain,Jcv\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int trainingDataSize = 0;
		File f = new File("/media/DriveE/NLP/dataFiles/"
					+"train/");
		File fileList[] = f.listFiles();
		trainingDataSize = fileList.length;
		for(int m = 10; m < trainingDataSize; m+=20){
			m = trainingDataSize;
			EventResolverTraining train = new
				EventResolverTraining("/media/DriveE/NLP/dataFiles/"
						+"train/", m);
			EventResolverBuildModel model = new EventResolverBuildModel();
			model.train("eventTrain.arff");
			EventResolverTesting testTrain = new EventResolverTesting(
					"/media/DriveE/NLP/dataFiles/"
						+"train/",
					m);
			EventResolverTesting testCV = new EventResolverTesting("/media/"
					+ "DriveE/NLP/dataFiles/test/", 0);
			System.out.println(m+","+testTrain.costFunctionValue+
					","+testCV.costFunctionValue);
			try {
				FileWriter fw = new FileWriter("learningDataEvent.csv", true);
				fw.write(m+","+testTrain.costFunctionValue+
					","+testCV.costFunctionValue+"\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
