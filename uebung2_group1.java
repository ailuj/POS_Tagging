import java.io.*;
import java.util.*;
import java.math.*;

public class uebung2_group1 {
	private static ReadModel data =  new ReadModel();
    private static HiddenMarkovModel model = null;
    private static String[] tags = null;


	public static void main(String[] args) {
		if (args[0].equals("train")) {
			BufferedReader br = null;
			FileReader fr = null;
			File folder = new File(args[1]);
			boolean found_start = false;
			int sentenceCount = 0;
			int tags0 = 0;
			int[] tagCount = new int[93];
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
								tagCount[model.getArrayIndex(tag)]++;
								if (model.containsEmiss(tag, word) == 0) {
									model.setEmiss(tag, word, 1, sentenceCount%2);
								} else {
									model.setEmiss(tag, word, model.getEmiss(tag, word)+1, sentenceCount%2);
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
			model.calcProbabilities(sentenceCount, tagCount, transCount);
			try {
	        	FileOutputStream fileOut = new FileOutputStream("./trainedModel.ser");
	         	ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         	out.writeObject(model);
	         	out.close();
	        	fileOut.close();
	        } catch (IOException e) {
	         	e.printStackTrace();
	      	}
		} else if (args[0].equals("annotate")) {
			model = data.getModel();
			tags = (String[]) model.tagMap.keySet().toArray(new String[model.tagMap.keySet().size()]);
			File folder = new File(args[1]);
            File outputDir = new File(args[2]);
            List<String> filenames = new ArrayList<String>();
            for (File file : folder.listFiles()) {
                filenames.add(file.getName());
            }
            String[][] words = splitText(folder);
            Tagging v = new Tagging();
            for(int y = 0; y < words.length; y++){
                List<String> temp = new ArrayList<String>();
                for (int x = 0; x < words[y].length; x++){
                    temp.add(words[y][x]);
                }
                String tags = v.viterbi(temp.toArray(new String[temp.size()]));
                File outputFile = new File(outputDir + "/" + filenames.get(y));
                writeLineToFile(outputFile, tags);
            }

		}
	}

	private static void writeLineToFile(File output, String taggedText){
        try{
            FileOutputStream out = new FileOutputStream(output);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(taggedText);
            bw.close();
        } catch(IOException ex){
            System.out.println("Something went wrong!");
        }
    }

	private static class Node{
        public int[] path;
        public double prob;

        public Node(int[] path, double prob){
            this.path = deepCopyIntArray(path);
            this.prob = prob;
        }
    }

    private static String[][] splitText(File folder){
        BufferedReader br = null;
        FileReader fr = null;
        String[][] files = new String[folder.listFiles().length][];
        int count = 0;
        for (File file : folder.listFiles()) {
            List<String> temp = new ArrayList<String>();
            try {
                br = new BufferedReader(new FileReader(file));
                String s = null;
                while ((s = br.readLine()) != null) {
                    if (s.equals("")) {
                        continue;
                    }
                    String[] parts = s.split("\\s+");
                    for(int x = 0; x < parts.length; x++){
                        temp.add(parts[x]);
                    }
                }
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        } catch(IOException e) {
            String[] parts = null;
            e.printStackTrace();
        }
        System.out.println(temp.toString());
        files[count] = temp.toArray(new String[temp.size()]);
        //System.out.println(files[count].toString());
        count += 1;
        }
        return files;
    }

    private static int[] deepCopyIntArray(int[] oldArray){
        int[] newArray = new int[oldArray.length];
        for(int i = 0; i < oldArray.length; i++){
            newArray[i] = oldArray[i];
        }
        return newArray;
    }

    private static int[] deepCopyIntArray(int[] oldArray, int newVal){
        int[] newArray = new int[oldArray.length + 1];
        for(int i = 0; i < oldArray.length; i++){
            newArray[i] = oldArray[i];
        }
        newArray[oldArray.length] = newVal;
        return newArray;
    }

    public static String viterbi(String[] words){
        Node[] n = new Node[tags.length];
        for (int state = 0; state < tags.length; state++){
            int[] intArray = new int[1];
            intArray[0] = state;
            n[state] = new Node(intArray, model.getStart(tags[state]) * model.getEmiss(tags[state], words[0]));
        }

        for (int output = 1; output < words.length; output++){
            Node[] n2 = new Node[tags.length];
            for (int nextState = 0; nextState < tags.length; nextState++){
                int[] argMax = new int[0];
                double valMax = 0;
                for (int state = 0; state < tags.length; state++){
                    int[] path = deepCopyIntArray(n[state].path);
                    double prob = n[state].prob;
                    double p = Math.log(model.getEmiss(tags[nextState], words[output])) + Math.log(model.getTrans(tags[state], tags[nextState]));
                    System.out.println(p);
                    prob *= p;
                    if(prob > valMax){
                        if(path.length == words.length){
                            argMax = deepCopyIntArray(path);
                        } else{
                            argMax = deepCopyIntArray(path, nextState);
                        }
                        valMax = prob;
                    }
                }
                n2[nextState] = new Node(argMax, valMax);
            }
            n = n2;
        }
        int[] argMax = new int[0];
        double valMax = 0;
        for (int state = 0; state < tags.length; state++){
            int[] path = deepCopyIntArray(n[state].path);
            double prob = n[state].prob;
            if(prob > valMax){
                argMax = deepCopyIntArray(path);
                valMax = prob;
            }
        }
        String viterbiPath = "";
        //System.out.println(words.length);
        for(int i = 0; i < argMax.length; i++){
            String tagged = words[i] + "/" + tags[argMax[i]] + " ";
            //System.out.print(words[i] + "/" + tags[argMax[i]] + " ");
            viterbiPath += tagged;
        }
        return viterbiPath;
    }
}