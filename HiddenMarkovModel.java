import java.io.*;
import java.util.*;

public class HiddenMarkovModel implements Serializable {
	public HashMap<String,Integer> tagMap;				//mappt Tags auf Array-Index
	public double[] p_start;							//enthält Startwahrscheinlichkeiten für alle Tags
	public double[][] p_trans;							//enthält Transitionswahrscheinlichkeiten vom 1. Tag zum 2. Tag
	public HashMap[] p_emiss;							//enthält für jedes Tag eine HashMap mit den Emissionswahrscheinlichkeiten
	public double[] p_unseenEmiss;
	public HashSet<String> wordSet;
	public HashMap[] simulateUnseen;

	HiddenMarkovModel() {
		this.tagMap = new HashMap<String,Integer>();
		this.p_start = new double[93];
		this.p_trans = new double[93][93];
		this.p_emiss = new HashMap[93];
		this.p_unseenEmiss = new double[93];
		this.wordSet = new HashSet<String>();
		this.simulateUnseen = new HashMap[93];
		for (int i=0; i<93; i++) {
			this.p_emiss[i] = new HashMap<String,Double>();
			this.simulateUnseen[i] = new HashMap<String,Integer>();
		}
	}

	public int containsEmiss(String tag, String word) {
		if (p_emiss[tagMap.get(tag)].containsKey(word)) {
			return 1;
		} else {
			return 0;
		}
	}

	public int setTag(String tag, int val) {
		if (tagMap.containsKey(tag)) {
			return 0;
		} else {
			tagMap.put(tag, val);
			return 1;
		}
	}

	public void setStart(String tag, double val) {
		p_start[tagMap.get(tag)] = val;
	}

	public void setTrans(String tag1, String tag2, double val) {
		p_trans[tagMap.get(tag1)][tagMap.get(tag2)] = val;
	}

	public void setEmiss(String tag, String word, double val, int split) {
		wordSet.add(word);
		p_emiss[tagMap.get(tag)].put(word, val);
		if (simulateUnseen[tagMap.get(tag)].containsKey(word)) {
			if ((((Integer) simulateUnseen[tagMap.get(tag)].get(word)).intValue() == 0) && (split == 1)) {
				simulateUnseen[tagMap.get(tag)].put(word, 2);
			} else if ((((Integer) simulateUnseen[tagMap.get(tag)].get(word)).intValue() == 1) && (split == 0)) {
				simulateUnseen[tagMap.get(tag)].put(word, 2);
			}
		} else {
			simulateUnseen[tagMap.get(tag)].put(word, split);
		}
	}

	public int getArrayIndex(String tag) {
		return tagMap.get(tag);
	}

	public double getStart(String tag) {
		return p_start[tagMap.get(tag)];
	}

	public double getTrans(String tag1, String tag2) {
		return p_trans[tagMap.get(tag1)][tagMap.get(tag2)];
	}

	public double getEmiss(String tag, String word) {
		if (p_emiss[tagMap.get(tag)].containsKey(word)) {
			return ((Double)p_emiss[tagMap.get(tag)].get(word)).doubleValue();
		} else if (wordSet.contains(word)) {
			return p_unseenEmiss[tagMap.get(tag)];
		} else {
			return (1.0/93.0);
		}
	}

	public void calcProbabilities(int sentenceCount, int[] tagCount, int transCount) {
		double x = 0.5;
		double y = 0.0;
		for (int i=0; i<93; i++) {
			y = 0.0;
			Iterator it0 = simulateUnseen[i].entrySet().iterator();
			while (it0.hasNext()) {
				Map.Entry pair = (Map.Entry)it0.next();
				if (((Integer)pair.getValue()).intValue() == 0) {
					y++;
				}
			}
			y = y / wordSet.size();
			p_start[i] = (p_start[i] + 1) / (sentenceCount + 93);
			p_unseenEmiss[i] = y / (tagCount[i] + (y * wordSet.size()));
			Iterator it = p_emiss[i].entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				p_emiss[i].put(pair.getKey(), (((Double)pair.getValue()).doubleValue() + y) / (tagCount[i] + (y * wordSet.size())));
			}
			for (int j=0; j<93; j++) {
				p_trans[i][j] = (p_trans[i][j] + x) / (transCount + (x*93*93));
			}
		}
	}
}