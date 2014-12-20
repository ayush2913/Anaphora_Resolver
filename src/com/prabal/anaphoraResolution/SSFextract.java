package com.prabal.anaphoraResolution;

import java.util.Scanner;

// This class uses a line containing some linguistic information in a SSF
// (Shakti Standard Format) and gives the corresponding information stores
// in it such as the gender, number, position of the word in the sentence.
public class SSFextract {
	String inputLine;
	char lineType;
	String chunkPOS, headChunkID, drel, parentID;
	String word, subPosTag, root, wordGroup, gender, number, person, caseMarker
	, tamHindi, tamWx; 
	int position;
	
	// This is a class constructor that is used to set the values of the string
	// line from SSF and the type of line (header or non-header) taken as 
	// arguments
	SSFextract(String line, char flag){
		inputLine = line;
		lineType = flag;
		chunkPOS = headChunkID = drel = parentID = "";
		word = subPosTag = root = wordGroup = gender = number = person =
				caseMarker = tamHindi = tamWx = "";
		position = 0;
		callExtractFunctions();
	}
	
	// This function is used to call two different functions depending upon
	// the type of line (header or non-header)
	public void callExtractFunctions(){
		if(lineType == 'h' || lineType == 'H')
			extractMembersHead(inputLine);
		else
			extractMembersNonHead(inputLine);
	}
	
	// This function is used to extract values: ID of the chunk, dependency
	// relation with parent chunk and the chunk ID of the parent chunk
	public void extractMembersHead(String line){
		Scanner scn = new Scanner(line);
		scn.useDelimiter("\t|:|\\s|'");
		scn.next();scn.next();
		chunkPOS = scn.next();
		while(scn.hasNext()){
			String word = scn.next();
			if(word.equalsIgnoreCase("name=")==true)
				headChunkID = scn.next();
			if(word.equalsIgnoreCase("drel=")==true){
				drel = scn.next();
				parentID = scn.next();
			}
		}
		scn.close();
	}
	
	// This function is used to extract values: root of the word, word group to 
	// which it belongs, gender, number, person, case (direct or oblique),
	// TAM in both Hindi and WX format and the position of the word in the 
	// sentence
	public void extractMembersNonHead(String line){
		Scanner scn = new Scanner(line);
		scn.useDelimiter("\t|:|\\s|'|,");
		scn.next();scn.next();
		subPosTag = scn.next();
		while(scn.hasNext()){
			String wrd = scn.next();
			if(wrd.equalsIgnoreCase("af=")==true){
				root = scn.next();
				wordGroup = scn.next();
				gender = scn.next();
				number = scn.next();
				person = scn.next();
				caseMarker = scn.next();
				tamHindi = scn.next();
				tamWx = scn.next();
			}
			if(wrd.equalsIgnoreCase("name=")==true)
				word = scn.next();
			if(wrd.equalsIgnoreCase("posn=")==true){
				position = Integer.parseInt(scn.next());
				position = (int) (position*0.1);
			}
		}
		scn.close();
	}
}
