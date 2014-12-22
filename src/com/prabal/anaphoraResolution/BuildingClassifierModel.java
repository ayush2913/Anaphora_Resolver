package com.prabal.anaphoraResolution;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

// This class is designed to building a hypothesis model from the training data.
// The Weka learning tool kit is used (based on Naive Bayes learning)
public class BuildingClassifierModel {

	public FilteredClassifier fc = new FilteredClassifier();
	
	
	public  FilteredClassifier train(String path){
     
        try
        {                   
           DataSource train_source = new DataSource(path);
           Instances train = train_source.getDataSet();             
           int cIdx_train=train.numAttributes()-1;
           train.setClassIndex(cIdx_train);
           String summary=train.toSummaryString();
           int num_sem=train.numInstances();
           int num_att_per_sem=train.numAttributes();
           
           System.out.println("Number of attributes in model = "+num_att_per_sem);
           System.out.println("Number of samples = " + num_sem);
           System.out.println("Summary: " + summary);
           System.out.println();
           
           
           
           NaiveBayes nb = new NaiveBayes();
          
           SMO sm = new SMO();
           IBk ibk = new IBk();
           LibSVM lsm = new LibSVM();
           
           J48 j48 = new J48();
           j48.setUnpruned(true); 
          
           StringToWordVector wf = new StringToWordVector();
           
           fc.setFilter(wf);
           fc.setClassifier(j48);
          
           //fc.setFilter(rm);
           //fc.setClassifier(adt);
           fc.buildClassifier(train);  
	   //path for WEKA model provided in next line 
           ObjectOutputStream oos = new ObjectOutputStream(new 
			  FileOutputStream("train.model")); 
           oos.writeObject(fc);
           oos.flush();
           oos.close();
           System.out.println("training done");
           
        
           
       }
       catch (Exception e) 
       {
         e.printStackTrace();
       }
       
       return fc;

  }
	
}
