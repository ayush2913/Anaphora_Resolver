package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class EventAnaphoraResolver{
	int dataSize;
	int truePositive, falsePositive, falseNegative, trueNegative;
	String trainDirectoryPath;
	String anaName, anaGender, anaNumber, anaPerson, anaTam, anaCase,
	       anaDrel, anaChunkID, anaRef;
	String predResult;
	String antGender, antNumber, antPerson, antCase, antSentence, antChunkID;
	//String antVerbType;
	String antecedentString;
	int sententialDist, verbalDist, antOverlap;
	String onlyMember, mainVerb;
	String antClassValue;
	DataSource test_source;
	Instances test;
	public FilteredClassifier fClass;
	double pred;
	Instance instance;
	private double conf[];
	String predictedResult;
	ArrayList<String> sentenceStack;
	int sentenceIndex;
	double costFunctionValue;
	ArrayList<ArrayList<String>> entityOverlap;
	ArrayList<String> newAnnotatedData = new ArrayList<String>();
	public static void main(String args[]){
		EventAnaphoraResolver ob = new EventAnaphoraResolver(
				"/home/prabal/AR_testing/classify", 0);
	}
	EventAnaphoraResolver(String path, int dataSz){
		dataSize = dataSz;
		costFunctionValue = 0.0;
		newAnnotatedData = new ArrayList<String>();
		predResult = "";
		truePositive = falsePositive = falseNegative = trueNegative = 0;
		trainDirectoryPath = path;
		readModel();
		//designStructureFile();
		//designTrainingFile();
		openDirectory();
		displayResults();
		calculateCostFunction();
	}
	
	// This function reads the model for the testing
		public void readModel() {
			try {
				ObjectInputStream oin = new ObjectInputStream(new 
						FileInputStream(
							"/home/prabal/AR_testing/trained_models/eventResolver.model"));// TRAINED MODEL
				fClass = (FilteredClassifier) oin.readObject();
				test_source = new DataSource( // File for structure
						"/home/prabal/AR_testing/trained_models/eventStructure.arff");
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				test = test_source.getStructure();
				int cIdx_train = test.numAttributes() - 1;
				test.setClassIndex(cIdx_train);
				instance = new Instance(test.numAttributes());
				//System.out.println("Instance is "+instance.toString());
				instance.setDataset(test);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		public void displayResults(){
			for(int i = 0; i < newAnnotatedData.size(); i++)
				System.out.println(newAnnotatedData.get(i));
			File f = new File("/home/prabal/AR_testing/resolver/inputfile.txt");
			if(f.exists())
				f.delete();
			try {
				FileWriter fw = new FileWriter("/home/prabal/AR_testing/resolver/inputfile.txt", true);
				for(int i = 0; i< newAnnotatedData.size(); i++)
					fw.write(newAnnotatedData.get(i)+"\n");
				fw.close();
			} catch (IOException e) {
				System.out.println("structure file not opening !");
			}
		}
		public void calculateCostFunction(){
			costFunctionValue = costFunctionValue/(2*dataSize);
			//System.out.println(costFunctionValue);
		}
	
	public void initializeAnaphoraValues(){
		anaName = anaGender = anaNumber = anaPerson = anaTam = anaCase =
			anaDrel =  "";
		verbalDist = -1;
		onlyMember = "0";		
	}

	public void initializeAntecedentValues(){
		antGender = antNumber = antPerson = antCase = "";
				//antVerbType= "";
		antecedentString = "";
		sententialDist = antOverlap = 0;
		mainVerb = antClassValue= "0";
	}

	public void openDirectory(){
		File directory = new File(trainDirectoryPath);
		File fileList[] = directory.listFiles();
		if(dataSize < 10)
			dataSize = fileList.length;
		for(int i = 0; i< dataSize; i++){
			openFiles(fileList[i]);
		}
	}

	public void openFiles(File f){
		entityOverlap = new ArrayList<ArrayList<String>>();
		sentenceStack = new ArrayList<String>();
		EventResolutionExp2 overlap= new EventResolutionExp2(f);
	       	entityOverlap = overlap.relationDegree;
		ArrayList<String> fileContent = new
			ArrayList<String>();
		Scanner scn = null;
		try{
			scn = new Scanner(f);
		}catch(Exception e){
			System.out.println("File not found in the directory");
		}
		while(scn.hasNext()){
			fileContent.add(scn.nextLine());
		}
		scn.close();
		extractData(fileContent);
	}
	
	public void extractData(ArrayList<String> content){
		int anaphoraSentenceIndex = 0;
		ArrayList<String> chunkData = new ArrayList<String>();
		for(int i = 0; i < content.size(); i++){
			//System.out.println(content.get(i));
			if(content.get(i).contains("<Sentence id=")){
				anaphoraSentenceIndex = i;
				sentenceStack.add(content.get(i));
				newAnnotatedData.add(content.get(i));
			}
			if(content.get(i).contains("((") &&
					content.get(i).contains("name=")){
				int chunkHeadIndex = i;	
				chunkData = new ArrayList<String>();
				while(!content.get(i).contains("))")){	
					newAnnotatedData.add(content.get(i));
					chunkData.add(content.get(i));
					i++;
				}
				newAnnotatedData.add(content.get(i));
				anaRef = "";
				boolean anaphoraFlag = 
					checkAnaphoraChunk(chunkData);
				initializeAnaphoraValues();
				if(anaphoraFlag == true){
					SSFextract anaphoraHead = new SSFextract(
							chunkData.get(0));
					String anaphoraID = anaphoraHead.headChunkID;
					boolean kiCase = checkForKiCase(content,
							anaphoraSentenceIndex, anaphoraID);
					if(kiCase == true){
						//System.out.println(anaRef+">>"+antSentence+">"
						//		+antChunkID);
						continue;
					}
					extractAnaphoraFeatures(chunkData);
					sentenceIndex = sentenceStack.size()-1;
					//System.out.println();
					getPossibleAntecedentChunks(content, 
							chunkHeadIndex);
				annotateReferent();
				}	
			}
		}
	}
	
	public boolean checkForKiCase(ArrayList<String> content, int index,
			String chunkID){
		for(int i = index; i < content.size(); i++){
			if(content.get(i).contains("/Sentence"))
				return false;
			if(content.get(i).contains(String.format("drel='rs:%s'", 
					chunkID))){
				SSFextract header = new SSFextract(content.get(i));
				String kiID = header.headChunkID;
				//System.out.println(">>"+kiID);
				for(int j = index; j < content.size(); j++){
					//System.out.println(content.get(j));
					if(content.get(j).contains("/Sentence"))
						break;
					if(content.get(j).matches(String.format("(.*)drel='(.*):%s'(.*)", 
							kiID)) && (content.get(j).contains("VGF")
									|| content.get(j).contains("CCP"))){
						//System.out.println(content.get(j));
						header = new SSFextract(content.get(j));
						//System.out.println(">>>>"+header.headChunkID+" "+anaRef);
						predResult = "ref='"+anaRef+"'";
						annotateReferent();
						applyKiRules(anaRef, header.headChunkID);
						break;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public void annotateReferent(){
		
		for(int k = newAnnotatedData.size()-1; k >= 0; k--){
			if(newAnnotatedData.get(k).contains("\tPRP\t")){
				String line = newAnnotatedData.get(k);
				line = line.substring(0, line.length()-1);
				line = line + " "+predResult+">";
				newAnnotatedData.set(k, line);
				break;
			}
		}
	}
	
	public void applyKiRules(String anaRef, String header){
		if(anaRef.equalsIgnoreCase(header)){
			truePositive ++;
		}
		else{
			falsePositive ++;
			costFunctionValue ++;
		}
	}

	public void extractAntecedentFeatures(ArrayList<String> ante){
		
		initializeAntecedentValues();
		if(!ante.get(0).contains("drel") &&
				ante.get(0).contains("stype=") &&
				ante.get(0).contains("name=")){
			mainVerb = "1";	
		}
		Scanner s = new Scanner(ante.get(1));
		s.useDelimiter("\t");
		s.next();
		antecedentString = s.next();
//		JHWNL.initialize();
//		long[] synsetOffsets;
//		
//		try {
//				//	 Look up the word for all POS tags
//				IndexWordSet demoIWSet = Dictionary.getInstance().lookupAllIndexWords(antecedentString);				
//				//	 Note: Use lookupAllMorphedIndexWords() to look up morphed form of the input word for all POS tags				
//				IndexWord[] demoIndexWord = new IndexWord[demoIWSet.size()];
//				demoIndexWord  = demoIWSet.getIndexWordArray();
//				for ( int i = 0;i < demoIndexWord.length;i++ ) {
//					int size = demoIndexWord[i].getSenseCount();
//					//System.out.println("Sense Count is " + size);	
//					synsetOffsets = demoIndexWord[i].getSynsetOffsets();
////					for ( int k = 0 ;k < size; k++ ) {
////						System.out.println("Offsets[" + k +"] " + synsetOffsets[k]);	
////					}
//
//					Synset[] synsetArray = demoIndexWord[i].getSenses(); 
//						//System.out.println("Synset [" + k +"] "+ synsetArray[k]);
//						//System.out.println("Synset POS: " + synsetArray[k].getPOS());
//						Pointer[] pointers = synsetArray[0].getPointers();
//						//System.out.println("Synset Num Pointers:" + pointers.length);
//						if(pointers[0].getType().equals(PointerType.ONTO_NODES)) {	// For ontology relation
//							String temp = pointers[0].getType() + " : "  + Dictionary.getInstance().getOntoSynset(pointers[0].getOntoPointer()).getOntoNodes();
//							if (temp.contains("(Verb)")){
//								//System.out.println(pointers[0].getType() + " : "  + Dictionary.getInstance().getOntoSynset(pointers[0].getOntoPointer()).getOntoNodes());
//								if(temp.contains("VOS-PHY-ST"))
//									antVerbType = "VOS-PHY-ST";
//								else if(temp.contains("VOS-MNT-ST"))
//									antVerbType ="VOS-MNT-ST";
//								else if(temp.contains("VOA-COMM"))
//									antVerbType ="VOA-COMM";
//								else if(temp.contains("VOA-ACT"))
//									antVerbType ="VOA-ACT";
//								else if(temp.contains("VOO"))
//									antVerbType ="VOO";
//								else if(temp.contains("VOA-"))
//									antVerbType ="OTH";
//								else if(temp.contains("VOS"))
//									antVerbType ="VOS";
//								else if(temp.contains("bodily action"))
//									antVerbType = "BOA";
//							}
//						}
////						for (int j = 0; j < pointers.length; j++) {							
////							if(pointers[j].getType().equals(PointerType.ONTO_NODES)) {	// For ontology relation
////								String temp = pointers[j].getType() + " : "  + Dictionary.getInstance().getOntoSynset(pointers[j].getOntoPointer()).getOntoNodes();
////								if (temp.contains("(Verb)")){
////									System.out.println(pointers[j].getType() + " : "  + Dictionary.getInstance().getOntoSynset(pointers[j].getOntoPointer()).getOntoNodes());
////									break;
////								}
////							} //else {
////								//System.out.println(pointers[j].getType() + " : "  + pointers[j].getTargetSynset());
////							//}							
////						}
//						
//				}
//		} catch (JHWNLException e) {
//			System.err.println("Internal Error raised from API.");
//			e.printStackTrace();
//		}
//		if(antVerbType.equalsIgnoreCase(""))
//			antVerbType = "X";
		SSFextract anteHead = new SSFextract(ante.get(0));
		antChunkID = anteHead.headChunkID;
		for(int i = 1; i < ante.size(); i++){
			if(ante.get(i).contains("\tVM\t")){
				SSFextract anteData = new
					SSFextract(ante.get(i));
				antGender = anteData.gender;
				antNumber = anteData.number;
				antPerson = anteData.person;
				antCase = anteData.caseMarker;
				sententialDist = sentenceStack.size()-sentenceIndex-2;
				antOverlap = entityOverlapCheck(sentenceStack.size()-1, 
						sentenceIndex);
				if(anaRef.matches(String.format("(.*)%s(.*)%s", 
						antSentence, antChunkID))){
					antClassValue = "1";
				}
				displayInstance();
				antSentence = antChunkID = "";
				break;
			}
		}
	}
	
	public int entityOverlapCheck(int anaphoraIndex, int antecedentIndex){
		ArrayList<String> sentenceData = entityOverlap.get(anaphoraIndex);
		int antecedentSentence = 0;
		int overlapDegree = 0;
		Scanner scn = new Scanner(sentenceStack.get(antecedentIndex));
		scn.useDelimiter("\t|\\s|'");
		while(scn.hasNext()){
			String word = scn.next();
			if(word.equalsIgnoreCase("id="))
				antecedentSentence = Integer.parseInt(scn.next());
		}
		scn.close();
		for(int i = 1; i < sentenceData.size(); i++){
			if(sentenceData.get(i).matches(String.format("%s:(.*)", 
					antecedentSentence))){
				Scanner s = new Scanner(sentenceData.get(i));
				s.useDelimiter(":");
				s.next();
				overlapDegree = Integer.parseInt(s.next());
				s.close();
			}
		}
		return overlapDegree;
	}
	
	public void displayInstance(){
		rationalizeGender(); rationalizeNumber(); rationalizePerson(); 
		rationalizeCase(); rationalizeDrel(); rationalizeTam();
//		System.out.println(anaName+","+anaGender+","+anaNumber+","+
//				anaPerson+","+anaTam+","+anaCase+","+anaDrel+","
//				+onlyMember+","+antGender+","+antNumber+","+
//				antPerson+","+antCase+","+verbalDist+","+
//				mainVerb+","+sententialDist+","+antOverlap
//				+","+antClassValue);
		
		prediction();
//		try{
//			FileWriter fw = new FileWriter("eventTrain.arff", true);
//			fw.write(anaName+","+anaGender+","+anaNumber+","+
//				anaPerson+","+anaTam+","+anaCase+","+anaDrel+","
//				+onlyMember+","+antGender+","+antNumber+","+
//				antPerson+","+antCase+","+verbalDist+","+
//				mainVerb+","+sententialDist+","+antOverlap
//				+","+antClassValue+"\n");
//			fw.close();
//		}catch(Exception e){
//			System.out.println("error in opening training file !");
//		}
	}

public void prediction(){
		
		instance.setValue(test.attribute(0), anaName);
		instance.setValue(test.attribute(1), anaGender);
		instance.setValue(test.attribute(2), anaNumber);
		instance.setValue(test.attribute(3), anaPerson);
		instance.setValue(test.attribute(4), anaTam);
		instance.setValue(test.attribute(5), anaCase);
		instance.setValue(test.attribute(6), anaDrel);
		instance.setValue(test.attribute(7), onlyMember);
		instance.setValue(test.attribute(8), antGender);
		instance.setValue(test.attribute(9), antNumber);
		instance.setValue(test.attribute(10), antPerson);
		instance.setValue(test.attribute(11), antCase);
		instance.setValue(test.attribute(12), verbalDist);
		instance.setValue(test.attribute(13), mainVerb);
		instance.setValue(test.attribute(14), sententialDist);
		instance.setValue(test.attribute(15), antOverlap);
		//instance.setValue(test.attribute(16), antVerbType);
		try {
			pred = fClass.classifyInstance(instance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			conf = fClass.distributionForInstance(instance);
//			for(int o=0; o<conf.length;o++)
//				System.out.println(conf[o]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		predictedResult = test.classAttribute().value((int) pred);
		if(predictedResult != null){
			if(predictedResult.equalsIgnoreCase("1")){
				
				predResult = "ref='..%"+antSentence+"%"+antChunkID+"'";
			}
		}
		
		predictedResult = test.classAttribute().value((int) pred);
		if(predictedResult.equalsIgnoreCase("1") &&
				antClassValue.equalsIgnoreCase("1")){
			truePositive ++;
		}
		else if(predictedResult.equalsIgnoreCase("1") &&
				antClassValue.equalsIgnoreCase("0")){
			falsePositive ++;
			costFunctionValue ++;
		}
		else if(predictedResult.equalsIgnoreCase("0") &&
				antClassValue.equalsIgnoreCase("1")){
			falseNegative ++;
			costFunctionValue ++;
		}
		else if(predictedResult.equalsIgnoreCase("0") &&
				antClassValue.equalsIgnoreCase("0")){
			trueNegative ++;
		}
	}

	public void getPossibleAntecedentChunks(
			ArrayList<String> content, int anaphoraIndex){
		int allChunkSize = 0;
		for(int i = anaphoraIndex; i >= 0; i--){
			if(content.get(i).contains("/Sentence")){
				sentenceIndex--;
			}
			if(content.get(i).contains("))")){
				ArrayList<String> chunk = new
					ArrayList<String>();
				while(content.get(i).contains("((") ==
						false){
					i--;
					chunk.add(content.get(i));
				}
				if(content.get(i).contains("name='VGF")){
					verbalDist ++;
					Scanner s = new Scanner(sentenceStack.get(
							sentenceIndex));
					s.useDelimiter("\t|\\s|'");
					while(s.hasNext()){
						String word = s.next();
						if(word.equalsIgnoreCase("id="))
							antSentence = s.next();
					}
					s.close();
					
					ArrayList<String> reverseChunk = new
						ArrayList<String>();
					for(int j = chunk.size()-1; j >= 0; j--)
						reverseChunk.add(chunk.get(j));
					if(sentenceIndex != (sentenceStack.size()-1))
						allChunkSize++;
					else
						continue;
					if(allChunkSize <= 5){
						extractAntecedentFeatures(reverseChunk);
					}
					else
						return;
				}
			}
		}
	}
	public boolean checkAnaphoraChunk(ArrayList<String> chunkData){
		for(int i = 0; i < chunkData.size(); i++){
			if(chunkData.get(i).contains("reftype='V'")){
				Scanner scn = new Scanner(chunkData.get(i));
				scn.useDelimiter("\t|\\s|'");
				while(scn.hasNext()){
					String word = scn.next();
					if(word.equalsIgnoreCase("ref="))
						anaRef = scn.next();
				}
				scn.close();
				return true;
			}
		}
		return false;
	}
	
	public void extractAnaphoraFeatures(ArrayList<String> chunkData){
		SSFextract headData = new SSFextract(chunkData.get(0));
		anaDrel = headData.drel;
		for(int i = 1; i < chunkData.size(); i++){
			if(chunkData.get(i).contains("reftype='V'")){
				SSFextract anaData = new
					SSFextract(chunkData.get(i));
				anaName = anaData.word;
				anaGender = anaData.gender;
				anaNumber = anaData.number;
				anaPerson = anaData.person;
				anaCase = anaData.caseMarker;
				anaTam = anaData.tamWx;
				break;
			}
		}
		if(chunkData.size() <= 2)
			onlyMember = "1";
	}
	
	public void rationalizeGender(){
		if(!antGender.equalsIgnoreCase("m") && 
				!antGender.equalsIgnoreCase("f") &&
				!antGender.equalsIgnoreCase("any"))
			antGender = "X";
		if(!anaGender.equalsIgnoreCase("m") &&
				!anaGender.equalsIgnoreCase("f") &&
				!anaGender.equalsIgnoreCase("any"))
			anaGender = "X";
	}
	
	public void rationalizeNumber(){
		if(!antNumber.equalsIgnoreCase("sg") &&
				!antNumber.equalsIgnoreCase("pl") &&
				!antNumber.equalsIgnoreCase("any"))
			antNumber = "X";
		if(!anaNumber.equalsIgnoreCase("sg") &&
				!anaNumber.equalsIgnoreCase("pl") &&
				!anaNumber.equalsIgnoreCase("any"))
			anaNumber = "X";
	}
	
	public void rationalizePerson(){
		if(!antPerson.equalsIgnoreCase("3") &&
				!antPerson.equalsIgnoreCase("3h") &&
				!antPerson.equalsIgnoreCase("1") &&
				!antPerson.equalsIgnoreCase("1h") &&
				!antPerson.equalsIgnoreCase("2") &&
				!antPerson.equalsIgnoreCase("2h") &&
				!antPerson.equalsIgnoreCase("any"))
			antPerson = "X";
		if(!anaPerson.equalsIgnoreCase("3") &&
				!anaPerson.equalsIgnoreCase("3h") &&
				!anaPerson.equalsIgnoreCase("1") &&
				!anaPerson.equalsIgnoreCase("1h") &&
				!anaPerson.equalsIgnoreCase("2") &&
				!anaPerson.equalsIgnoreCase("2h") &&
				!anaPerson.equalsIgnoreCase("any"))
			anaPerson = "X";
	}
	
	public void rationalizeCase(){
		if(!antCase.equalsIgnoreCase("o") &&
				!antCase.equalsIgnoreCase("d") &&
				!antCase.equalsIgnoreCase("any"))
			antCase = "X";
		if(!anaCase.equalsIgnoreCase("o") &&
				!anaCase.equalsIgnoreCase("d") &&
				!anaCase.equalsIgnoreCase("any"))
			anaCase = "X";
	}
	
	public void rationalizeDrel(){
		if (!anaDrel.equalsIgnoreCase("k1") &&
				!anaDrel.equalsIgnoreCase("k2") &&
				!anaDrel.equalsIgnoreCase("k7t") &&
				!anaDrel.equalsIgnoreCase("k7p") &&
				!anaDrel.equalsIgnoreCase("k7a") &&
				!anaDrel.equalsIgnoreCase("k7") &&
				!anaDrel.equalsIgnoreCase("r6") &&
				!anaDrel.equalsIgnoreCase("nmod") &&
				!anaDrel.equalsIgnoreCase("vmod"))
			anaDrel = "OTH";
	}
	
	public void rationalizeTam(){
		if(!anaTam.equalsIgnoreCase("0") &&
				!anaTam.equalsIgnoreCase("meM") &&
				!anaTam.equalsIgnoreCase("ne") &&
				!anaTam.equalsIgnoreCase("ke") &&
				!anaTam.equalsIgnoreCase("kA") &&
				!anaTam.equalsIgnoreCase("ko") &&
				!anaTam.equalsIgnoreCase("kO") &&
				!anaTam.equalsIgnoreCase("se") &&
				!anaTam.equalsIgnoreCase("me") &&
				!anaTam.equalsIgnoreCase("eM")){
			anaTam = "X";
		}
	}
}
