import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Tagging {
    private static ReadModel data =  new ReadModel();
    private static HiddenMarkovModel model = data.getModel();
    private static String[] tags = (String[]) model.tagMap.keySet().toArray(new String[model.tagMap.keySet().size()]);


    public static void main(String[] args) {
        if(args[0].equals("annotate")) {
            File folder = new File(args[1]);
            File outputDir = new File(args[2]);
            List<String> filenames = new ArrayList<String>();
            for (File file : folder.listFiles()) {
                filenames.add(file.getName());
            }
            String[][] words = splitText(folder);
            Tagging v = new Tagging();
            for (int x = 0; x < words.length; x++){
                String tags = v.viterbi(words[x]);
                File outputFile = new File(outputDir + "/" + filenames.get(x));
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
        files[count] = temp.toArray(new String[temp.size()]);
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
                    double p = model.getEmiss(tags[nextState], words[output]) * model.getTrans(tags[state], tags[nextState]);
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
        for(int i = 0; i < argMax.length; i++){
            String tagged = words[i] + "/" + tags[argMax[i]] + " ";
            //System.out.print(words[i] + "/" + tags[argMax[i]] + " ");
            viterbiPath += tagged;
        }
        return viterbiPath;
    }

}
