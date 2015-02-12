package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class EventResolverTraining{
	String trainDirectoryPath;
	int dataSize;
	String anaName, anaGender, anaNumber, anaPerson, anaTam, anaCase,
	       anaDrel, anaChunkID, anaRef;
	String antGender, antNumber, antPerson, antCase, antSentence, antChunkID;
	int sententialDist, verbalDist, antOverlap;
	String onlyMember, mainVerb;
	String antClassValue;
	ArrayList<String> sentenceStack;
	int sentenceIndex;
	ArrayList<ArrayList<String>> entityOverlap;
	
	EventResolverTraining(String path, int dataSz){
		dataSize = dataSz;
		trainDirectoryPath = path;
		designStructureFile();
		designTrainingFile();
		openDirectory();
	}
	
	public void designStructureFile(){
		File structureFile = new File("eventStructure.arff");
		if(structureFile.exists())
			structureFile.delete();
		try {
			FileWriter fw = new FileWriter("eventStructure.arff", true);
			fw.write("@relation 'anaphora-antecedent'\n\n");
			fw.write("@attribute anaphora_name string\n");
			fw.write("@attribute anaphora_gender {m,f,any,X}\n");
			fw.write("@attribute anaphora_number {sg,pl,any,X}\n");
			fw.write("@attribute anaphora_person {1,1h,2,2h,3,3h,any,X}\n");
			fw.write("@attribute anaphora_TAM {0,meM,ne,ke,kA,ko,kO,"
					+"se,me,eM,X}\n");
			fw.write("@attribute anaphora_case {d,o,any,X}\n");
			fw.write("@attribute anaphora_drel{k1,k2,k7,k7p,k7a,k7t,r6"
					+",nmod,vmod,OTH}\n");
			fw.write("@attribute anaphora_only_member {1,0}\n");
			fw.write("@attribute antecedent_gender {m,f,any,X}\n");
			fw.write("@attribute antecedent_number {sg,pl,any,X}\n");
			fw.write("@attribute antecedent_person {1,1h,3,3h,2,2h,any,X}\n");
			fw.write("@attribute antecedent_case {o,d,any,X}\n");
			fw.write("@attribute verbal_group_distance numeric\n");
			fw.write("@attribute main_verb {1,0}\n");
			fw.write("@attribute sentential_distance numeric\n");
			//fw.write("@attribute NP_overlapping numeric\n");
			fw.write("@attribute class {1,0}\n\n");
			fw.write("@data\n\n");
			fw.close();
		} catch (IOException e) {
			System.out.println("structure file not opening !");
		}
	}
	
	public void designTrainingFile(){
		File train = new File("eventTrain.arff");
		if(train.exists())
			train.delete();
		try {
			FileWriter fw = new FileWriter("eventTrain.arff", true);
			fw.write("@relation 'anaphora-antecedent'\n\n");
			fw.write("@attribute anaphora_name string\n");
			fw.write("@attribute anaphora_gender {m,f,any,X}\n");
			fw.write("@attribute anaphora_number {sg,pl,any,X}\n");
			fw.write("@attribute anaphora_person {1,1h,2,2h,3,3h,any,X}\n");
			fw.write("@attribute anaphora_TAM {0,meM,ne,ke,kA,ko,kO,"
					+"se,me,eM,X}\n");
			fw.write("@attribute anaphora_case {d,o,any,X}\n");
			fw.write("@attribute anaphora_drel{k1,k2,k7,k7p,k7a,k7t,r6"
					+",nmod,vmod,OTH}\n");
			fw.write("@attribute anaphora_only_member {1,0}\n");
			fw.write("@attribute antecedent_gender {m,f,any,X}\n");
			fw.write("@attribute antecedent_number {sg,pl,any,X}\n");
			fw.write("@attribute antecedent_person {1,1h,3,3h,2,2h,any,X}\n");
			fw.write("@attribute antecedent_case {o,d,any,X}\n");
			fw.write("@attribute verbal_group_distance numeric\n");
			fw.write("@attribute main_verb {1,0}\n");
			fw.write("@attribute sentential_distance numeric\n");
			//fw.write("@attribute NP_overlapping numeric\n");
			fw.write("@attribute class {1,0}\n\n");
			fw.write("@data\n\n");
			fw.close();
		} catch (IOException e) {
			System.out.println("event training file not opening !");
		}
	}
	public void initializeAnaphoraValues(){
		anaName = anaGender = anaNumber = anaPerson = anaTam = anaCase =
			anaDrel =  "";
		verbalDist = -1;
		onlyMember = "0";		
	}

	public void initializeAntecedentValues(){
		antGender = antNumber = antPerson = antCase = "";
		sententialDist = antOverlap = 0;
		mainVerb = antClassValue = "0";
	}

	public void openDirectory(){
		File directory = new File(trainDirectoryPath);
		File fileList[] = directory.listFiles();
		for(int i = 0; i< dataSize; i++){
			//System.out.println(fileList[i].getName());
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
			if(content.get(i).contains("<Sentence id=")){
				anaphoraSentenceIndex = i;
				sentenceStack.add(content.get(i));
			}
			if(content.get(i).contains("((") &&
					content.get(i).contains("name=")){
				int chunkHeadIndex = i;	
				chunkData = new ArrayList<String>();
				while(!content.get(i).contains("))")){	
					chunkData.add(content.get(i));
					i++;
				}
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
					if(kiCase == true)
						continue;
					extractAnaphoraFeatures(chunkData);
					sentenceIndex = sentenceStack.size()-1;
					getPossibleAntecedentChunks(content, 
							chunkHeadIndex);
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
					chunkID)))
				return true;
		}
		return false;
	}

	public void extractAntecedentFeatures(ArrayList<String> ante){
		
		initializeAntecedentValues();
		if(!ante.get(0).contains("drel") &&
				ante.get(0).contains("stype=") &&
				ante.get(0).contains("name=")){
			mainVerb = "1";	
		}
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
		try{
			FileWriter fw = new FileWriter("eventTrain.arff", true);
			fw.write(anaName+","+anaGender+","+anaNumber+","+
				anaPerson+","+anaTam+","+anaCase+","+anaDrel+","
				+onlyMember+","+antGender+","+antNumber+","+
				antPerson+","+antCase+","+verbalDist+","+
				mainVerb+","+sententialDist
				+","+antClassValue+"\n");
			fw.close();
		}catch(Exception e){
			System.out.println("error in opening training file !");
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
