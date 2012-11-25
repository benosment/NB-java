package classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NBClassifier {
	int n; // total number of training instances
	int num_pos; // positive training instances
	int num_neg; // negative training instances
	// array of hash tables for the positive frequency
	ArrayList<HashMap<String, Integer>> pos_freq; 
	// array of hash tables for the negative frequency
	ArrayList<HashMap<String, Integer>> neg_freq;
	// array of sets, each set corresponds to one feature
	ArrayList<HashSet<String>> feature_set;
	// array of hash tables for probability
	ArrayList<HashMap<String, Double>> p;
	// TODO -- just use 1? negative is 1 - p?
	boolean initialized = false;
	
	public NBClassifier() {
		// initialize the frequency counts and data structures
		n = 0; 
		num_pos = 0; 
		num_neg = 0;
		pos_freq = new ArrayList<HashMap<String, Integer>>();
		neg_freq = new ArrayList<HashMap<String, Integer>>();
		feature_set = new ArrayList<HashSet<String>>();
		p = new ArrayList<HashMap<String, Double>>();
	}
	
	public void train(TrainingExample train_ex) {
		ArrayList<HashMap<String, Integer>> freq; 
		if (initialized == false) {
			// create HashMaps/Sets for all of our data structures
			for (int i = 0; i < train_ex.getFeatures().length; i++) {
				pos_freq.add(new HashMap<String, Integer>());
				neg_freq.add(new HashMap<String, Integer>());
				feature_set.add(new HashSet<String>());
				p.add(new HashMap<String, Double>());
			}
			initialized = true;
		}

		if (train_ex.getLabel().equals("+1")) {
			num_pos++;
			freq = pos_freq;
		} else {
			num_neg++;
			freq = neg_freq;
		}

		// update the frequency count for the class
		String[] features = train_ex.getFeatures();			
		for (int i = 0; i < features.length; i++){
			if (freq.get(i).containsKey(features[i])) {
				freq.get(i).put(features[i], freq.get(i).get(features[i])+ 1);
			} else {
				// if we haven't see it before, initialize first occurrence
				freq.get(i).put(features[i], 1);
			}
			// add feature to the feature set
			feature_set.get(i).add(features[i]);
		}

		n++; 
	}

	@Override
	public String toString() {
		return "NBClassifier [n=" + n + ", num_pos=" + num_pos + ", num_neg="
				+ num_neg + ", pos_freq=" + pos_freq + ", neg_freq="
				+ neg_freq + ", feature_set=" + feature_set + "]";
	}
}
