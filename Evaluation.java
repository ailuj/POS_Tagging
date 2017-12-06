import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static java.lang.System.exit;

/**
 * Created by martin on 01.12.17.
 */
public class Evaluation {

	 private static ArrayList<String> extractSentencesFromFiles(File folder) {
		 BufferedReader br = null;
		 ArrayList<String> sentences = new ArrayList<>();
		 for (File file : folder.listFiles()) {
			 System.out.println("Reading in File " + file.getName());
			 try {
				 br = new BufferedReader(new FileReader(file));
				 String s = null;

				 while ((s = br.readLine()) != null) {
					 if (s.equals("")) {
						 continue;
					 }
					 while(!s.substring(s.length()-1).equals(".")) {
						 String temp=null;
						 if((temp = br.readLine()) != null) {
							 s = s + temp;
						 }
						 else{
							 break;
						 }
					 }
					 sentences.add(s);
				 }
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 return sentences;
	 }

	 private static void buildFolds(ArrayList<String> sentences, File outputs){
		 for (int i=0;i<10;i++){
			 int j=0;
			 System.out.println("Building Fold "+i);
			 String trainTitle="Fold_"+i+"_train";
			 String testTitle="Fold_"+i+"_test";
			 String evalTitle="Fold_"+i+"_validation";

			 try {
				  new File("./"+outputs+"/"+trainTitle).mkdirs();
				  new File("./"+outputs+"/"+evalTitle).mkdirs();
				  new File("./"+outputs+"/"+testTitle).mkdirs();
				 PrintWriter trainWriter = new PrintWriter("./"+outputs+"/"+trainTitle+"/train", "UTF-8");
				 PrintWriter testfileWriter = new PrintWriter("./"+outputs+"/"+testTitle+"/test", "UTF-8");
				 PrintWriter validationWriter = new PrintWriter("./"+outputs+"/"+evalTitle+"/validation", "UTF-8");
				 for (String sentence : sentences) {
					 String[] sentenceWords=sentence.split("\\s+");
					 if (j % 10 == i) {
						 //write into validation file for accuracy calc
						 String cleanedSentence = "";
						 for (int k = 0; k < sentenceWords.length; k++) {
							 if(!sentenceWords[k].equals("")) {
								 cleanedSentence = cleanedSentence + sentenceWords[k] + " ";
							 }
						 }

						 validationWriter.write(cleanedSentence + "\n\n");

						 //write taglessvariant into test file
						 cleanedSentence = "";
						 for (int k = 0; k < sentenceWords.length; k++) {
							 if(!sentenceWords[k].equals("")) {
								 String wordWithoutTags = sentenceWords[k];
								 wordWithoutTags = wordWithoutTags.substring(0, wordWithoutTags.lastIndexOf("/"));
								 cleanedSentence = cleanedSentence + wordWithoutTags + " ";
							 }
						 }
						 testfileWriter.write(cleanedSentence + "\n\n");

					 } else {
						 //write into trainingfile
						 String cleanedSentence="";
						 for (int k = 0; k < sentenceWords.length; k++) {
							 if(!sentenceWords[k].equals("")) {
								 cleanedSentence = cleanedSentence + sentenceWords[k] + " ";
							 }
						 }
						 trainWriter.write(cleanedSentence + "\n\n");
					 }
					 j++;
				 }
				 trainWriter.close();
				 testfileWriter.close();
				 validationWriter.close();
			 }
			 catch (Exception e){
				 e.printStackTrace();
				 exit(-1);
			 }
		 }
	 }

	 private static double crossValidation(File outputs){
		 double averageAccuracy=0.0;
		 for (int i=0;i<10;i++){
			 System.out.println("Evaluating Fold "+i);
			 //train the model
			 System.out.println(">Training");
			 String[] trainArguments={"train","./"+outputs+"/Fold_"+i+"_train/"};
			 uebung2_group1.main(trainArguments);

			 System.out.println(">Annotating");
			 String[] testArguments={"annotate","./"+outputs+"/Fold_"+i+"_test/","./"+outputs};

			 uebung2_group1.main(testArguments);

			 //read in test file and output file for accuracy calculation
			 System.out.println(">Calculating Accuracy");
			 try {
				 //dummy copy of output file into output dir until tagging has output file
				 /*Files.copy(new File("./"+outputs+"/Fold_"+i+"_test/test").toPath(),
						 new File("./"+outputs+"/test").toPath(),
						 StandardCopyOption.REPLACE_EXISTING);
				*/
				 BufferedReader validationFile = new BufferedReader(
						 new FileReader("./" + outputs + "/Fold_" + i + "_validation/validation"));
				 BufferedReader outputFile = new BufferedReader(
						 new FileReader("./" + outputs + "/test"));

				 //removing tags for file
/*
				 String s = null;
				 String tag = null;
				 ArrayList<String> testTags = new ArrayList<>();
				 ArrayList<String> outputTags = new ArrayList<>();

				 String annotatedFileString = "";
				 String validationFileString = "";
				 while ((s = outputFile.readLine()) != null) {
					 annotatedFileString = annotatedFileString + s;
				 }

				 while ((s = validationFile.readLine()) != null) {
					 validationFileString = validationFileString + s;
				 }

				 String[] annotatedFileWords = annotatedFileString.split("\\s+");
				 String[] validationFileWords = validationFileString.split("\\s+");

				 ArrayList<String> annotatedTags = new ArrayList<>();
				 ArrayList<String> validationTags = new ArrayList<>();

				 for (int pos = 0; pos < annotatedFileWords.length; pos++) {
					 if (!annotatedFileWords[pos].equals("")) {
						 tag = annotatedFileWords[pos].substring(annotatedFileWords[pos].lastIndexOf("/") + 1);
						 annotatedTags.add(tag);
					 }
				 }
				 for (int pos = 0; pos < validationFileWords.length; pos++) {
					 if (!validationFileWords[pos].equals("")) {
						 tag = validationFileWords[pos].substring(validationFileWords[pos].lastIndexOf("/") + 1);
						 validationTags.add(tag);
					 }
				 }
				 System.out.println("ANN: " + annotatedTags.size() + " VAL: " + validationTags.size());

			 }*/

				 String s = null;
				 String tag = null;
				 ArrayList<String> testTags = new ArrayList<>();
				 ArrayList<String> outputTags = new ArrayList<>();

				 while ((s = validationFile.readLine()) != null) {
					 String[] parts = s.split("\\s+");
					 for (int pos = 0; pos < parts.length; pos++) {
						 if (!parts[pos].equals("")) {
							 tag = parts[pos].substring(parts[pos].lastIndexOf("/") + 1);
							 testTags.add(tag);
						 }
					 }
				 }

				 while ((s = outputFile.readLine()) != null) {
					 String[] parts = s.split("\\s+");
					 for (int pos = 0; pos < parts.length; pos++) {
						 if (!parts[pos].equals("")) {
							 tag = parts[pos].substring(parts[pos].lastIndexOf("/") + 1);
							 outputTags.add(tag);
						 }
					 }
				 }

				 System.out.println("Tag List Sizes: " + testTags.size() + " " + outputTags.size());
				 double accuracy = calculateAccuracy(testTags, outputTags);
				 System.out.println("Accuracy :" + accuracy);
				 averageAccuracy += accuracy;
			 }
			 catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		 return averageAccuracy/10.0;
	 }

	protected static double calculateAccuracy(ArrayList<String> first, ArrayList<String> second){
		if (first.size()!=second.size()){
			System.out.println("Tag Lists differ in Length! Aborting Calculation");
			return 0.0;
		}
		double totalTags=first.size();
		double correctTags=0;
		for (int i = 0; i < first.size(); i++) {
			if (first.get(i).equals(second.get(i))){
				correctTags++;
			}
		}
		return correctTags/totalTags;

	}

	public static void main(String[] args) {
		//calculates accuracy,
		//given that the command line specifications of learning and tagging for the command line arguments hold
		FileReader fr = null;
		File inputs = new File(args[0]);
		File outputs = new File(args[1]);
		ArrayList<String> sentences=extractSentencesFromFiles(inputs);
		buildFolds(sentences, outputs);
		double avgAccuracy=crossValidation(outputs);
		System.out.println("Average Accuracy: "+avgAccuracy);
	}
}
