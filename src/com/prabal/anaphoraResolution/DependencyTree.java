package com.prabal.anaphoraResolution;

import java.awt.ScrollPane;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

public class DependencyTree extends JFrame {

	String inputFolderName;
	ArrayList<String> fileContent;
	JTree visualTree;
	DefaultMutableTreeNode mainNode;
	DefaultMutableTreeNode folderNode;

	DependencyTree(String inputFolder) {
		inputFolderName = inputFolder;
		fileContent = new ArrayList<String>();
		mainNode = null;
		folderNode = new DefaultMutableTreeNode("data");
	}

	public void openFiles() throws FileNotFoundException {

		File folder = new File(inputFolderName);
		File[] fileList = folder.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			mainNode = new DefaultMutableTreeNode(fileList[i].getName());
			folderNode.add(mainNode);
			getDependencyTrees(fileList[i]);
		}
		visualTree = new JTree(folderNode);
		add(new JScrollPane(visualTree));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Dependency Trees");
		this.pack();
		this.setVisible(true);
	}

	public void getDependencyTrees(File f) throws FileNotFoundException {

		fileContent = new ArrayList<String>();
		Scanner scn = new Scanner(f);
		while (scn.hasNext()) {
			fileContent.add(scn.nextLine());
		}
		scn.close();
		extractTrees(fileContent);
	}

	public void extractTrees(ArrayList<String> content) {

		String sentenceID = "";
		ArrayList<String> sentence = new ArrayList<String>();
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i).matches("<Sentence id=(.*)") == true) {
				sentenceID = content.get(i);
				i++;
				while (content.get(i).contains("</Sentence>") == false) {
					sentence.add(content.get(i));
					i++;
				}
			}
			if (sentence.isEmpty() == false) {
				System.out.println(sentenceID);
				DefaultMutableTreeNode sentenceLevel = new DefaultMutableTreeNode(sentenceID);
				ArrayList<DefaultMutableTreeNode> shoots=getDependencies(sentence,sentenceLevel);
				for(int j=0;j<shoots.size();j++)
					sentenceLevel.add(shoots.get(j));
				mainNode.add(sentenceLevel);
				sentence = new ArrayList<String>();
			}

		}
	}

	public ArrayList<DefaultMutableTreeNode> getDependencies(ArrayList<String> sentence,
			DefaultMutableTreeNode sentenceLevel) {

		String chunkID = "";
		String chunk = "";
		String rel = "";
		ArrayList<DefaultMutableTreeNode> nodes=new ArrayList<DefaultMutableTreeNode>();
		for(int i=0;i<sentence.size();i++){
			
			if(sentence.get(i).contains("((")==true && sentence.get(i).contains("drel=")==false){
				chunk=getChunk(sentence,i);
				chunkID=getChunkId(sentence.get(i));
				String ch = chunkID+" ("+chunk+")";
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(ch);
				getShoots(chunkID,sentence,node);
				nodes.add(node);
				chunkID="";
				chunk="";
				rel="";
			}
		}
		return nodes;
	}
	
	public void getShoots(String chunkID,ArrayList<String>sentence,DefaultMutableTreeNode root){
		
		for(int i=0;i<sentence.size();i++){
			if(sentence.get(i).matches(String.format("(.*) drel='(.*):%s'(.*)",
					chunkID))==true){
				String chunkid=getChunkId(sentence.get(i));
				String chunk=getChunk(sentence,i);
				String relation=getRelation(sentence.get(i));
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(chunkid+"->"+relation+" ["+chunk+"]");
				root.add(node);
				getShoots(chunkid,sentence,node);
			}
		}
	}
		
	public String getRelation(String line) {

		Scanner sc = new Scanner(line);
		sc.useDelimiter("\t|\\s|'|:");
		while (sc.hasNext()) {
			if (sc.next().equalsIgnoreCase("drel=") == true)
				return sc.next();
		}
		return "";
	}

	public String getChunk(ArrayList<String> sentence, int index) {

		String chunk = "";
		for (int i = index + 1; sentence.get(i).contains("))") == false; i++) {
			Scanner sc = new Scanner(sentence.get(i));
			sc.useDelimiter("\\s|\t|'");
			sc.next();
			chunk = chunk + sc.next() + " ";
		}
		return chunk.trim();
	}

	public String getChunkId(String line) {

		Scanner sc = new Scanner(line);
		sc.useDelimiter("\t|\\s|'");
		while (sc.hasNext()) {
			if (sc.next().equalsIgnoreCase("name=") == true)
				return sc.next();
		}
		return "";
	}
}
