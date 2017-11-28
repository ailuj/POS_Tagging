import java.io.*;
import java.util.*;

public class training {
	public static void main(String[] args) {
		BufferedReader br = null;
		FileReader fr = null;
		File folder = new File(args[0]);
		boolean found_start = false;
		int sentenceCount = 0;
		int tags = 0;
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
							tags++;
							lastTag = tag;
							word = parts[i].substring(0,parts[i].lastIndexOf("/"));
							tag = parts[i].substring(parts[i].lastIndexOf("/")+1);
							if (model.setTag(tag, distinctTags) == 1) {
								distinctTags++;
							}
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
		//bis hier fertig
		try {
        	FileOutputStream fileOut = new FileOutputStream("./trainedModel.ser");
         	ObjectOutputStream out = new ObjectOutputStream(fileOut);
         	out.writeObject(model);
         	out.close();
         	fileOut.close();
      	} catch (IOException e) {
         	e.printStackTrace();
      	}
	}
}