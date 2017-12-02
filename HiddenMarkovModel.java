import java.io.*;
import java.util.*;

public class HiddenMarkovModel implements Serializable {
	public HashMap<String,Integer> tagMap;				//mappt Tags auf Array-Index
	public double[] p_start;							//enthält Startwahrscheinlichkeiten für alle Tags
	public double[][] p_trans;							//enthält Transitionswahrscheinlichkeiten vom 1. Tag zum 2. Tag
	public HashMap[] p_emiss;							//enthält für jedes Tag eine HashMap mit den Emissionswahrscheinlichkeiten

	HiddenMarkovModel() {
		this.tagMap = new HashMap<String,Integer>();
		this.p_start = new double[93];
		this.p_trans = new double[93][93];
		this.p_emiss = new HashMap[93];
		for (int i=0; i<93; i++) {
			this.p_emiss[i] = new HashMap<String,Double>();
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

	public void setEmiss(String tag, String word, double val) {
		p_emiss[tagMap.get(tag)].put(word, val);
	}

	public double getStart(String tag) {
		return p_start[tagMap.get(tag)];
	}

	public double getTrans(String tag1, String tag2) {
		return p_trans[tagMap.get(tag1)][tagMap.get(tag2)];
	}

	public double getEmiss(String tag, String word) {
		return ((Double)p_emiss[tagMap.get(tag)].get(word)).doubleValue();
	}

	public void calcProbabilities(int sentenceCount, int tagCount, int transCount) {
		for (int i=0; i<93; i++) {
			p_start[i] = p_start[i]/sentenceCount;
			Iterator it = p_emiss[i].entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				p_emiss[i].put(pair.getKey(), ((Double)pair.getValue()).doubleValue()/tagCount);		//cast unsafe?
			}
			for (int j=0; j<93; j++) {
				p_trans[i][j] = p_trans[i][j]/transCount;
			}
		}
	}
}