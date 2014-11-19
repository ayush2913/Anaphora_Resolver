package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class EventCaseAnalysis {

	String inputFolderName;
	File fileList[];
	ArrayList<String> fileContent;
	int totalCases;
	int correctCases;
	int relationCases;
	int relationCorrectCases;

	EventCaseAnalysis(String inputFolder) {
		inputFolderName = inputFolder;
		fileContent = new ArrayList<String>();
		totalCases=0;correctCases=0;relationCases=0;relationCorrectCases=0;
	}

	public void openFolder() throws FileNotFoundException {

		File f = new File(inputFolderName);
		fileList = f.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			System.out.println(fileList[i].getName());
			getFileContent(fileList[i]);
		}
		displayResults();
	}
	
	public void displayResults(){
		System.out.println("total cases= "+totalCases);
		System.out.println("correct cases= "+correctCases);
		System.out.println("relation cases= "+relationCases);
		System.out.println("correct relation cases= "+relationCorrectCases);
		System.out.println((correctCases/(double)totalCases));
	}

	public void getFileContent(File file) throws FileNotFoundException {

		fileContent = new ArrayList<String>();
		Scanner scn = new Scanner(file);
		while (scn.hasNext()) {
			fileContent.add(scn.nextLine());
		}
		scn.close();
		analyzeFile(fileContent);
	}

	public void analyzeFile(ArrayList<String> content) {

		int sentenceHead = 0;
		int chunkHead = 0;
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i).contains("<Sentence") == true) {
				sentenceHead = i;
			}
			if (content.get(i).contains("((") == true) {
				chunkHead = i;
			}

			if (content.get(i).contains("reftype='V'") == true) {
				totalCases++;
				System.out.println(content.get(sentenceHead));
				System.out.println(content.get(i));
				String antecedent="";
				String prediction="";
				Scanner scn =  new Scanner(content.get(i));
				scn.useDelimiter("'|\\s|\t");
				while(scn.hasNext()){
					if(scn.next().equalsIgnoreCase("ref=")==true)
						antecedent=scn.next();
				}
				boolean check=checkForTreeRelations(content, sentenceHead, chunkHead, i);
				if(check==true){
					prediction = checkForSameSentenceVerb(content,sentenceHead,chunkHead);
					relationCases++;
					if(prediction.equalsIgnoreCase(antecedent)==true){
						correctCases++; relationCorrectCases++;
						System.out.println("true relation cases");
					}
					else{
						System.out.println("false relation case");
					}
				}
				else{
					prediction = checkForLastVerb(content,sentenceHead,chunkHead);
					if(prediction.equalsIgnoreCase(antecedent)==true){
						correctCases++;
						System.out.println("true case");
					}
					else
						System.out.println("false case");
				}
				System.out.println(prediction);
			}
		}
	}
	
	public String checkForSameSentenceVerb(ArrayList<String>content,int sentenceHead, int chunkHead){
		
		String antecedent="";
		for(int i=chunkHead; content.get(i).contains("</Sentence>")==false;i++){
			if(content.get(i).matches(".* name='VGF.*")==true){
				Scanner s = new Scanner(content.get(i));
				s.useDelimiter("'|\t|\\s");
				while(s.hasNext()){
					if(s.next().equalsIgnoreCase("name=")==true)
						antecedent=s.next();
				}
			}
		}
		return antecedent;
	}
	public String checkForLastVerb(ArrayList<String>content,int sentenceHead, int chunkHead){
		
		String sentenceId="";
		Scanner scn = new Scanner(content.get(sentenceHead));
		scn.useDelimiter("'|\\s|\t");
		while(scn.hasNext()){
			if(scn.next().equalsIgnoreCase("id=")==true){
				sentenceId=scn.next();
			}
				
		}
		scn.close();
		int id = Integer.parseInt(sentenceId);
		if(id>1){
			id--;
		}
		for(int i=sentenceHead;i>=0;i--){
			if(content.get(i).matches(".* name='VGF.*")==true){
				Scanner s = new Scanner(content.get(i));
				s.useDelimiter("'|\t|\\s");
				while(s.hasNext()){
					if(s.next().equalsIgnoreCase("name=")==true)
						return "..%"+String.format("%d",id)+"%"+s.next();
				}
			}
		}
		return "";
	}

	public boolean checkForTreeRelations(ArrayList<String> content,
			int sentenceHead, int chunkHead, int anaphoraLine) {

		String anaphoraId = "";
		Scanner s = new Scanner(content.get(chunkHead));
		s.useDelimiter("'|\t|\\s");
		while (s.hasNext()) {
			if (s.next().equalsIgnoreCase("name=") == true) { 
				anaphoraId = s.next();
			}
		}
		for (int i = sentenceHead; content.get(i).contains("</Sentence>") == false; i++) {
			if (i == chunkHead)
				continue;

			if (content.get(i).matches(
					String.format(".* drel='.*.:%s'.*", anaphoraId)) == true && content.get(i).contains("CCP")==true) {
				
				System.out.println("relation exits");
				return true;
			}
		}
		return false;
	}
}
