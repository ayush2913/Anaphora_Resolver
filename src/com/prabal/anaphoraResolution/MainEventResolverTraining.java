package com.prabal.anaphoraResolution;

public class MainEventResolverTraining{
	public static void main(String args[]){
//		EventResolverTraining train = new
//			EventResolverTraining("/media/DriveE/NLP/dataFiles/"
//					+"train/");
//		EventResolverBuildModel model = new EventResolverBuildModel();
//		model.train("eventTrain.arff");
		EventResolverTesting test = new EventResolverTesting("/media/"
				+ "DriveE/NLP/dataFiles/test/",0);
	}
}
