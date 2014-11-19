package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TestClassifier {

	int correctPredictions;
	int totalPredictions;
	int entityCases;
	int eventCases;
	int unknownCases;
	int correctUnknownCases;
	int correctEntityCases;
	int correctEventCases;
	DataSource test_source;
	Instances test;
	public FilteredClassifier fClass;
	double pred;
	Instance instance;
	private double conf[];
	String predictedResult;

	String inputDir;
	String anaphoraString;
	String sentenceID;
	String chunkID,drel, root, pos, gender, number, person, relation, preposition,
			lastword, result;
	int position;

	File[] fileList;

	TestClassifier(String directoryName) {
		
		predictedResult="";
		correctPredictions=totalPredictions=0;
		
		entityCases = eventCases = correctEntityCases = correctEventCases =unknownCases=correctUnknownCases= 0;

		inputDir = directoryName;

		anaphoraString = sentenceID= chunkID= drel = root = pos = gender = number = person = relation = preposition = lastword = result = "";
		position = 0;

	}
	
	public void readModel() {// to read the model for the machine learning

		try {
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
					"train.model"));// TRAINED
			// MODEL
			fClass = (FilteredClassifier) oin.readObject();
			test_source = new DataSource( // FILE FOR STRUCTURE OF INSTANCE
					"trainStructure.arff");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			test = test_source.getStructure();
			int cIdx_train = test.numAttributes() - 1;
			test.setClassIndex(cIdx_train);
			instance = new Instance(test.numAttributes());
			// System.out.println("Instance is  "+instance.toString());
			instance.setDataset(test);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getFileList() {

		File dir = new File(inputDir);
		if (dir.exists() == false) {
			System.out.println("Directory does not exist!!");
		} else {
			fileList = dir.listFiles();
		}
	}

	public void getFileContent() {

		for (int i = 0; i < fileList.length; i++) {
			System.out.println(fileList[i].getName());

			extractChunks(fileList[i]);

		}
	}
	
	public void displayResults(){
		
		System.out.println(correctPredictions);
		System.out.println(totalPredictions);
		double precision = (double) correctPredictions/totalPredictions;
		System.out.println("Entity-->"+entityCases+"-->"+correctEntityCases);
		System.out.println("Event-->"+eventCases+"-->"+correctEventCases);
		System.out.println("Unknown-->"+unknownCases+"-->"+correctUnknownCases);
		System.out.println(precision);
	}

	public void extractChunks(File file) {

		ArrayList<String> chunk = new ArrayList<String>();
		Scanner scn = null;
		try {
			scn = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (scn.hasNext()) {

			String line = scn.nextLine();
			if(line.contains("<Sentence id=")==true){
				sentenceID=line;
			}
			if (line.contains("((") == true
					&& line.contains("<fs name=") == true
					&& line.contains("drel=") == true) {
				while (line.contains("))") == false) {
					chunk.add(line);
					line = scn.nextLine();
				}
			}

			chunk.add("))");

			if (checkForAnaphora(chunk) == true) {
				getContentForArff(chunk);
				checkForNullValues();


					rationalizeDrel();
					// rationalizePreposition();
					// System.out.println(anaphoraString + "," + drel + "," +
					// root
					// + "," + pos + "," + gender + "," + number + ","
					// + person + "," + relation + "," +preposition+","+
					// position + ","
					// + lastword + "," + result);
					//
					
					prediction();
					totalPredictions++;
					if(result.equalsIgnoreCase("N")==true)
						entityCases++;
					if(result.equalsIgnoreCase("V")==true)
						eventCases++;
					if(result.equalsIgnoreCase("U")==true)
						unknownCases++;
					
					String ruleResult=applyRulesAnaphoraRoot(root);
					boolean kiRule=applyRulesForKi(file,sentenceID,chunkID);
					if(ruleResult.equalsIgnoreCase("N")==true)
						predictedResult="N";
					if(ruleResult.equalsIgnoreCase("V")==true)
						predictedResult="V";
					if(kiRule==true)
						predictedResult="V";
					System.out.println(predictedResult);
					if(predictedResult.equalsIgnoreCase("N")==true && result.equalsIgnoreCase("N")==true)
						correctEntityCases++;
					if(predictedResult.equalsIgnoreCase("V")==true && result.equalsIgnoreCase("V")==true)
						correctEventCases++;
					if(predictedResult.equalsIgnoreCase("U")==true && result.equalsIgnoreCase("U")==true)
						correctUnknownCases++;
					if(predictedResult.equalsIgnoreCase(result)==true)
						correctPredictions++;
					else
						System.out.println("incorrect");
					predictedResult="";
					
				initializeAllFeatures();
				chunk = new ArrayList<String>();
			}
		}

	}

	public void rationalizePreposition() {

		if (preposition.equalsIgnoreCase("X") == false
				&& preposition.equalsIgnoreCase("0") == false
				&& preposition.equalsIgnoreCase("meM") == false
				&& preposition.equalsIgnoreCase("ne") == false
				&& preposition.equalsIgnoreCase("ke") == false
				&& preposition.equalsIgnoreCase("kA") == false
				&& preposition.equalsIgnoreCase("ko") == false
				&& preposition.equalsIgnoreCase("se") == false)
			preposition = preposition + "__OTH";
	}

	public void rationalizeDrel() {

		if (drel.equalsIgnoreCase("k1") == false
				&& drel.equalsIgnoreCase("k2") == false
				&& drel.equalsIgnoreCase("k7t") == false
				&& drel.equalsIgnoreCase("k7p") == false
				&& drel.equalsIgnoreCase("k7") == false
				&& drel.equalsIgnoreCase("r6") == false
				&& drel.equalsIgnoreCase("nmod") == false
				&& drel.equalsIgnoreCase("vmod") == false)
			drel = "OTH";

	}

	public void checkForNullValues() {

		if (gender.equalsIgnoreCase("") == true)
			gender = "X";
		if (number.equalsIgnoreCase("") == true)
			number = "X";
		if (person.equalsIgnoreCase("") == true)
			person = "X";
		if (relation.equalsIgnoreCase("") == true)
			relation = "X";
		if (preposition.equalsIgnoreCase("") == true)
			preposition = "X";
	}

	public void initializeAllFeatures() {

		anaphoraString = chunkID=drel = root = pos = gender = number = person = relation = preposition = lastword = result = "";
		position = 0;

	}

	public boolean checkForAnaphora(ArrayList<String> chunk) {

		for (int i = 0; i < chunk.size(); i++) {
			if (chunk.get(i).contains("reftype=") == true) {
				return true;
			}
		}
		return false;
	}

	public void getContentForArff(ArrayList<String> chunk) {

		for (int i = 0; i < chunk.size(); i++) {
			if (chunk.get(i).contains("<fs name=") == true) {

				Scanner scn = null;

				scn = new Scanner(chunk.get(i));

				scn.useDelimiter("'|\\s|:|_|-");
				while (scn.hasNext()) {

					String word = scn.next();
					if(word.equalsIgnoreCase("name=")==true){
						chunkID=scn.next();
					}
					if (word.equalsIgnoreCase("drel=") == true) {
						drel = scn.next();
					}
				}
			}

			if (chunk.get(i).contains("reftype=") == true) {

				Scanner scn = new Scanner(chunk.get(i));
				scn.useDelimiter("\\s|'|,");

				while (scn.hasNext()) {

					String word = scn.next();
					if (word.equalsIgnoreCase("af=") == true) {
						root = scn.next();
						pos = scn.next();
						gender = scn.next();
						number = scn.next();
						person = scn.next();
						relation = scn.next();
						scn.next();
						preposition = scn.next();
					}
					if (word.equalsIgnoreCase("name=") == true)
						anaphoraString = scn.next();

					if (word.equalsIgnoreCase("posn=") == true)
						position = Integer.parseInt(scn.next());

					if (word.equalsIgnoreCase("reftype=") == true) {
						result = scn.next();
						i++;
						if (chunk.get(i).contains("))") == true)
							lastword = "Y";
						else
							lastword = "N";

						return;
					}
				}
			}
		}

	}

	public void prediction(){
		
		instance.setValue(test.attribute(0), anaphoraString);
		instance.setValue(test.attribute(1), drel);
		instance.setValue(test.attribute(2), gender);
		instance.setValue(test.attribute(3), number);
		instance.setValue(test.attribute(4), person);
		instance.setValue(test.attribute(5), relation);
		instance.setValue(test.attribute(6), preposition);
		instance.setValue(test.attribute(7), position);
		instance.setValue(test.attribute(8), lastword);
		
		try {
			pred = fClass.classifyInstance(instance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			conf = fClass.distributionForInstance(instance);
			//for(int o=0; o<conf.length;o++)
				//System.out.println(conf[o]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		predictedResult = test.classAttribute().value((int) pred);
		
		System.out.println(anaphoraString+"---->"+root);
		System.out.print(result+"-->");
	}
	public boolean applyRulesForKi(File file,String sentenceID,String chunkID){
		Scanner scn=null;
		try {
			scn=new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(scn.hasNext()){
			String line=scn.nextLine();
			if(line.equalsIgnoreCase(sentenceID)==true){
				line=scn.nextLine();
				while(line.contains("Sentence")==false){
					line=scn.nextLine();
					if(line.contains("CCP")==true && line.contains(String.format("drel='rs:%s'", chunkID))==true){
						System.out.println("enter condition");
						return true;
					}
				}
				break;
			}
		}
		return false;
	}
	public String applyRulesAnaphoraRoot(String root){
		
		if(root.equalsIgnoreCase("हम")==true ||
				root.equalsIgnoreCase("मैं")==true ||
				root.equalsIgnoreCase("तु")==true ||
				root.equalsIgnoreCase("तुम")==true ||
				root.equalsIgnoreCase("आप")==true ||
				root.equalsIgnoreCase("वह")==true ||
				root.equalsIgnoreCase("वे")==true ||
				root.equalsIgnoreCase("जो")==true ||
				root.equalsIgnoreCase("वहाँ")==true ||
				root.equalsIgnoreCase("जहाँ")==true ||
				root.equalsIgnoreCase("यहाँ")==true
				 ){
			
			return "N";
			
		}
		
		if(root.equalsIgnoreCase("इसलिये")==true){
			return "V";
		}
		
		return "";
	}
	

}
