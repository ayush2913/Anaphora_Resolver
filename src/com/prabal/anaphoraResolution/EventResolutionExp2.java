package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class EventResolutionExp2 {
	String inputDir;
	ArrayList<ArrayList<String>>fileVerbRelations;
	ArrayList<ArrayList<String>>relationDegree;
	
	EventResolutionExp2(String dirName){	
		inputDir = dirName;
		relationDegree = new ArrayList<ArrayList<String>>();
		fileVerbRelations = new ArrayList<ArrayList<String>>();
	}
	
	public void openDir() throws FileNotFoundException{
		File f = new File(inputDir);
		File[] fileList = f.listFiles();
		for(int i=0;i<fileList.length;i++){
			System.out.println(fileList[i].getName());
			openFiles(fileList[i]);
			getResults();
			relationDegree = new ArrayList<ArrayList<String>>();
			fileVerbRelations = new ArrayList<ArrayList<String>>();
		}
	}
	
	public void getResults(){
		for(int i=0;i<fileVerbRelations.size();i++){
			ArrayList<String> sent = new ArrayList<String>();
			sent.add(fileVerbRelations.get(i).get(0));
			relationDegree.add(sent);
		}
		getInterSentenceRelations(fileVerbRelations);
		for(int i=0;i<relationDegree.size();i++){
			System.out.println(relationDegree.get(i));
		}
	}
	public void getInterSentenceRelations(ArrayList<ArrayList<String>> verbRelations){
		int counter=0;
		ArrayList<ArrayList<String>>relations = verbRelations;
		for(int i=1;i<relations.size();i++){
			for(int j=0;j<i;j++){
				counter=0;
				for(int k=1;k<relations.get(i).size();k++){
					for(int l=1;l<relations.get(j).size();l++){
						if(relations.get(i).get(k).equalsIgnoreCase(relations.get(j).get(l))==true){
							counter++;
						}
					}
				}
				if(counter!=0){
					relationDegree.get(i).add(getSentenceID(relations.get(j).get(0))+":"+counter);
				}
			}
		}
	}
	
	public String getSentenceID(String line){
		Scanner s = new Scanner(line);
		s.useDelimiter("\\s|\t|'");
		while(s.hasNext()){
			if(s.next().equalsIgnoreCase("id=")==true)
				return s.next().trim();
		}
		return "";
	}
	public void openFiles(File file) throws FileNotFoundException{
		ArrayList<String> fileContent = new ArrayList<String>();
		Scanner scn = new Scanner(file);
		while(scn.hasNext()){
			fileContent.add(scn.nextLine());
		}
		scn.close();
		extractSentences(fileContent);
	}
	
	public void extractSentences(ArrayList<String> fileContent){
		ArrayList<String>sentenceContent = new ArrayList<String>();
		for(int i=0;i<fileContent.size();i++){
			if(fileContent.get(i).contains("Sentence id")==true){
				while(fileContent.get(i).contains("</Sentence>")==false){
					sentenceContent.add(fileContent.get(i));
					i++;
				}
				extractChunks(sentenceContent);
				sentenceContent = new ArrayList<String>();
			}
		}
	}
	public void extractChunks(ArrayList<String> sentenceContent){
		ArrayList<String> mainVerbHeaders = new ArrayList<String>();
		ArrayList<String> otherVerbHeaders = new ArrayList<String>();
		for(int i=0;i<sentenceContent.size();i++){
			if(sentenceContent.get(i).contains("drel")==false && sentenceContent.get(i).contains("stype='declarative'")==true && sentenceContent.get(i).contains("VGF")==true){
				mainVerbHeaders.add(getChunkID(sentenceContent.get(i)));
				
			}
			else if(sentenceContent.get(i).contains("((")==true && sentenceContent.get(i).contains("stype='declarative'")==true){
				otherVerbHeaders.add(getChunkID(sentenceContent.get(i)));
			}
		}
		ArrayList<String>data = new ArrayList<String>();
		data.add(sentenceContent.get(0));
		data.addAll(getNounPhrases(sentenceContent,mainVerbHeaders));
		data.addAll(getNounPhrases(sentenceContent,otherVerbHeaders));
		fileVerbRelations.add(data);
	}
	
	public ArrayList<String> getNounPhrases(ArrayList<String>sentenceContent,ArrayList<String>verbHeaders){
		ArrayList<String>words = new ArrayList<String>();
		ArrayList<String>chunkIDs = new ArrayList<String>();
		for(int i=0;i<verbHeaders.size();i++){
			for(int j=0;j<sentenceContent.size();j++){
				if(sentenceContent.get(j).matches(String.format("(.*)\tNP\t(.*)drel='(.*):%s'(.*)", verbHeaders.get(i)))){
					chunkIDs.add(getChunkID(sentenceContent.get(j)));
					while(sentenceContent.get(j).contains("))")==false){
						j++;
						if(sentenceContent.get(j).matches("(.*)\tNP\t(.*)")==true || sentenceContent.get(j).matches("(.*)\tNNP\t(.*)")==true || sentenceContent.get(j).matches("(.*)\tNNC\t(.*)")==true || sentenceContent.get(j).matches("(.*)\tNNPC\t(.*)")==true){
							Scanner sc = new Scanner(sentenceContent.get(j));
							sc.useDelimiter("\t|\\s|=");
							sc.next();
							words.add(sc.next());
						}
					}
				}
			}
		}
		for(int i=0;i<chunkIDs.size();i++){
			for(int j=0;j<sentenceContent.size();j++){
				if(sentenceContent.get(j).matches(String.format("(.*)\tNP\t(.*)drel='(.*):%s'(.*)", chunkIDs.get(i)))){
					while(sentenceContent.get(j).contains("))")==false){
						j++;
						if(sentenceContent.get(j).matches("(.*)\tNP\t(.*)")==true || sentenceContent.get(j).matches("(.*)\tNNP\t(.*)")==true || sentenceContent.get(j).matches("(.*)\tNNC\t(.*)")==true || sentenceContent.get(j).matches("(.*)\tNNPC\t(.*)")==true){
							Scanner sc = new Scanner(sentenceContent.get(j));
							sc.useDelimiter("\t|\\s|=");
							sc.next();
							words.add(sc.next());
						}
					}
				}
			}
		}
		return words;
	}
	public ArrayList<String> getHeaders(ArrayList<String>sentenceContent,String headID){
		ArrayList<String> header = new ArrayList<String>();
		for(int i=0;i<sentenceContent.size();i++){
			System.out.println("entered");
			if(sentenceContent.get(i).matches(String.format("(.*)drel=(.*):%s(.*)", headID))==true){
				System.out.println(sentenceContent.get(i));
			}
		}
		return header;
	}
	public String getChunkID(String line){
		Scanner sc = new Scanner(line);
		sc.useDelimiter("\t|\\s|'");
		while(sc.hasNext()){
			if(sc.next().equalsIgnoreCase("name=")==true)
				return sc.next();
		}
		return "";
	}
}
