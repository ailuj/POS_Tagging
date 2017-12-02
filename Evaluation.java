import java.io.*;
import java.util.ArrayList;

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
					 sentences.add(s);
				 }
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 return sentences;
	 }

	 private static void buildFolds(ArrayList<String> sentences, File outputs){
		 int j=0;
		 for (int i=0;i<10;i++){
			 System.out.println("Building Fold "+i);
			 String trainTitle="Fold_"+i+"_train";
			 String testTitle="Fold_"+i+"_test";
			 String trainSentences="";
			 String testSentences="";
			 try {
				 PrintWriter trainWriter = new PrintWriter("./"+outputs+"/"+trainTitle, "UTF-8");
				 PrintWriter testWriter = new PrintWriter("./"+outputs+"/"+testTitle, "UTF-8");
				 for (String sentence : sentences) {
					 if (j % 10 == i) {
						 testWriter.write(sentence+"\n");
					 } else {
						 trainWriter.write(sentence+"\n");
					 }
					 j++;
				 }
				 trainWriter.close();
				 testWriter.close();
			 }
			 catch (Exception e){
				 e.printStackTrace();
			 }


		 }
	 }

	 private static double crossValidation(File outputs){
		 //todo
		 return 0.0;
	 }

	public static void main(String[] args) {
		FileReader fr = null;
		File folder = new File(args[0]);
		File outputs = new File(args[1]);
		ArrayList<String> sentences=new ArrayList<>();
		sentences = extractSentencesFromFiles(folder);
		buildFolds(sentences, outputs);
		double accuracy=crossValidation(outputs);
	}
}
