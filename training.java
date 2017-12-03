import java.io.*;
import java.util.*;

public class training {
	public static void main(String[] args) {
		if (args[0].equals("train")) {
			BufferedReader br = null;
			FileReader fr = null;
			File folder = new File(args[1]);
			boolean found_start = false;
			int sentenceCount = 0;
			int tags0 = 0;
			int[] tags = new int[93];
			int distinctTags = 0;
			int transCount = 0;
			HiddenMarkovModel model = new HiddenMarkovModel();
			for (File file : folder.listFiles()) {
				try {
					br = new BufferedReader(new FileReader(file));
					String s = null;
					String word = null;
					String tag = null;
					String lastTag = null;
					while ((s = br.readLine()) != null) {
						found_start = false;
						if (s.equals("")) {
							continue;
						}
						String[] parts = s.split("\\s+");
						for (int i=0; i<parts.length; i++) {
							if (!parts[i].equals("")) {
								tags0++;
								lastTag = tag;
								word = parts[i].substring(0,parts[i].lastIndexOf("/"));
								tag = parts[i].substring(parts[i].lastIndexOf("/")+1);
								if (model.setTag(tag, distinctTags) == 1) {
									distinctTags++;
								}
								tags[model.getArrayIndex(tag)]++;
								if (model.containsEmiss(tag, word) == 0) {
									model.setEmiss(tag, word, 1);
								} else {
									model.setEmiss(tag, word, model.getEmiss(tag, word)+1);
								}
								if (!found_start) {					//Satzanfang -> p_start
									found_start = true;
									sentenceCount++;
									model.setStart(tag, model.getStart(tag)+1);
								} else {							//im Satz -> p_trans
									if (lastTag != null) {
										transCount++;
										model.setTrans(lastTag, tag, model.getTrans(lastTag, tag)+1);
									}
								}
							}
						}
					}
					if (br != null) {
						br.close();
					}
					if (fr != null) {
						fr.close();
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			model.calcProbabilities(sentenceCount, tags, transCount);
			try {
	        	FileOutputStream fileOut = new FileOutputStream("./trainedModel.ser");
	         	ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         	out.writeObject(model);
	         	out.close();
	        	fileOut.close();
	        } catch (IOException e) {
	         	e.printStackTrace();
	      	}
	      	/*System.out.println("Start: " + model.getStart("at") + ", " + model.getStart("rb") + ", " + model.getStart("nn") + ", " + model.getStart("ppss"));
	      	System.out.println("Trans: " + model.getTrans("at", "at") + ", " + model.getTrans("rb", "at") + ", " + model.getTrans("at", "nn") + ", " + model.getTrans("ppss", "nn"));
	      	System.out.println("Emiss: " + model.getEmiss("at", "the") + ", " + model.getEmiss("hvz", "has") + ", " + model.getEmiss("nn", "city") + ", " + model.getEmiss("ppss", "they"));*/
		} else if (args[0].equals("annotate")) {
			/*HiddenMarkovModel model2 = null;
	      	try {
	         	FileInputStream fileIn = new FileInputStream("./trainedModel.ser");
	         	ObjectInputStream in = new ObjectInputStream(fileIn);
	         	model2 = (HiddenMarkovModel) in.readObject();
	         	in.close();
	         	fileIn.close();
	      	} catch (IOException i) {
	         	i.printStackTrace();
	      	} catch (ClassNotFoundException c) {
	        	System.out.println("HiddenMarkovModel class not found");
	        	c.printStackTrace();
	      	}
	      	System.out.println("Start: " + model2.getStart("at") + ", " + model2.getStart("rb") + ", " + model2.getStart("nn") + ", " + model2.getStart("ppss"));
	      	System.out.println("Trans: " + model2.getTrans("at", "at") + ", " + model2.getTrans("rb", "at") + ", " + model2.getTrans("at", "nn") + ", " + model2.getTrans("ppss", "nn"));
	      	System.out.println("Emiss: " + model2.getEmiss("at", "the") + ", " + model2.getEmiss("hvz", "has") + ", " + model2.getEmiss("nn", "city") + ", " + model2.getEmiss("ppss", "they"));*/
		}
	}
}