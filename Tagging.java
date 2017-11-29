import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Tagging {
    private static ReadModel data =  new ReadModel();
    private static HiddenMarkovModel model = data.getModel();
    private static String[] words = { "I", "drink", "tea" }; //file input
    private static String[] states = { "#", "NN", "VB" };
    private static double[] start_p = { 0.3, 0.4, 0.3 };
    private static double[][] trans_p = { { 0.2, 0.2, 0.6 }, { 0.4, 0.1, 0.5 }, { 0.1, 0.8, 0.1 } };
    private static double[][] emit_p = { { 0.01, 0.02, 0.02 }, { 0.8, 0.01, 0.5 }, { 0.19, 0.97, 0.48 } };

    public static void main(String[] args) {
        //ReadModel data = new ReadModel();
        //HiddenMarkovModel model = data.getModel();
        //System.out.println(model.tagMap);
        Tagging v = new Tagging();
        int[] tags = v.viterbi(words, states, start_p, trans_p, emit_p);
        /**BufferedReader br = null;
        FileReader fr = null;
        File inputDir = new File(args[1]);
        File outputDir = new File(args[2]);**/

    }

    private static class Node{
        public int[] path;
        public double prob;

        public Node(int[] path, double prob){
            this.path = deepCopyIntArray(path);
            this.prob = prob;
        }
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

    public static int[] viterbi(String[] words, String[] states, double[] start_p, double[][] trans_p, double[][] emit_p){
        Node[] n = new Node[states.length];
        for (int st = 0; st < states.length; st ++){
            int[] intArray = new int[1];
            intArray[0] = st;
            n[st] = new Node(intArray, start_p[st] * emit_p[st][0]);
        }

        for (int output = 1; output < words.length; output++){
            Node[] n2 = new Node[states.length];
            for(int nextState = 0; nextState < states.length; nextState++){
                int[] argMax = new int[0];
                double valMax = 0;
                for(int st = 0; st < states.length; st++){
                    int[] path = deepCopyIntArray(n[st].path);
                    double prob = n[st].prob;
                    double p = emit_p[nextState][output] * trans_p[st][nextState];
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
        for(int st = 0; st < states.length; st++){
            int[] path = deepCopyIntArray(n[st].path);
            double prob = n[st].prob;
            if(prob > valMax){
                argMax = deepCopyIntArray(path);
                valMax = prob;
            }
        }
        String viterbiPath = "";
        for(int i = 0; i < argMax.length; i++){
            String tags = words[i] + "/" + states[argMax[i]] + " ";
            System.out.print(words[i] + "/" + states[argMax[i]] + " ");
        }
        return argMax;
    }

}
