package com.prabal.anaphoraResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class AnaphoraClassifierArff {

	String inputDir;
	String anaphoraString;
	String drel, root, pos, gender, number, person, relation, preposition,
			lastword, result;
	int position;

	File[] fileList;

	AnaphoraClassifierArff(String directoryName) {

		inputDir = directoryName;

		anaphoraString = drel = root = pos = gender = number = person = relation = preposition = lastword = result = "";
		position = 0;

	}
	
	public void designstructureOfArff(){
		
		File structure = new File("trainStructure.arff");
		if(structure.exists()==true)
			structure.delete();
		FileWriter fw;
		try {
			fw = new FileWriter("trainStructure.arff", true);
			fw.write("@relation 'anaphora-antecedent'\n\n");
			fw.write("@attribute anaphora string\n");
			fw.write("@attribute tree_relation {k1,k2,k7,k7p,k7t,r6,nmod,vmod,OTH}\n");
			fw.write("@attribute gender {m,f,any,X}\n");
			fw.write("@attribute number {sg,pl,any,X}\n");
			fw.write("@attribute person {1,1h,3,3h,2,2h,any,X}\n");
			fw.write("@attribute relation {o,d,any,X}\n");
			fw.write("@attribute preposition {0,meM,ne,ke,kA,ko,kO,se,me,eM,X}\n");
			fw.write("@attribute position numeric\n");
			fw.write("@attribute last_word {Y,N}\n");
			fw.write("@attribute class {N,V,U}\n\n");
			fw.write("@data\n\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void designFormat() {

		File trainFile = new File("trainFile.arff");
		if (trainFile.exists() == true)
			trainFile.delete();

		FileWriter fw;
		try {
			fw = new FileWriter("trainFile.arff", true);
			fw.write("@relation 'anaphora-antecedent'\n\n");
			fw.write("@attribute anaphora string\n");
			fw.write("@attribute tree_relation {k1,k2,k7,k7p,k7t,r6,nmod,vmod,OTH}\n");
			fw.write("@attribute gender {m,f,any,X}\n");
			fw.write("@attribute number {sg,pl,any,X}\n");
			fw.write("@attribute person {1,1h,3,3h,2,2h,any,X}\n");
			fw.write("@attribute relation {o,d,any,X}\n");
			fw.write("@attribute preposition {0,meM,ne,ke,kA,ko,kO,se,me,eM,X}\n");
			fw.write("@attribute position numeric\n");
			fw.write("@attribute last_word {Y,N}\n");
			fw.write("@attribute class {N,V,U}\n\n");
			fw.write("@data\n\n");
			fw.close();
		} catch (IOException e) {
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
					//rationalizePreposition();
					// System.out.println(anaphoraString + "," + drel + "," +
					// root
					// + "," + pos + "," + gender + "," + number + ","
					// + person + "," + relation + "," +preposition+","+
					// position + ","
					// + lastword + "," + result);
					//

					System.out.println(anaphoraString);
					FileWriter fw;
					try {
						fw = new FileWriter("trainFile.arff", true);
						fw.write(anaphoraString + "," + drel + "," + gender
								+ "," + number + "," + person + "," + relation
								+ "," + preposition + "," + position + ","
								+ lastword + "," + result);
						fw.write("\n");
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				initializeAllFeatures();
				chunk = new ArrayList<String>();
			}
		}

	}
	
	public void rationalizePreposition(){
		
		if(preposition.equalsIgnoreCase("X")==false && preposition.equalsIgnoreCase("0")==false &&
				preposition.equalsIgnoreCase("meM")==false && preposition.equalsIgnoreCase("ne")==false &&
				preposition.equalsIgnoreCase("ke")==false && preposition.equalsIgnoreCase("kA")==false &&
				preposition.equalsIgnoreCase("ko")==false && preposition.equalsIgnoreCase("se")==false)
			preposition = preposition+"__OTH";
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

		anaphoraString = drel = root = pos = gender = number = person = relation = preposition = lastword = result = "";
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

}
