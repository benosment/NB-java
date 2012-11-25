package classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NBClassifier {
	int n; // total number of training instances
	int num_pos; // positive training instances
	int num_neg; // negative training instances
	// array of hash tables for the positive frequency
	ArrayList<Map<String, Integer>> pos_freq; 
	// array of hash tables for the negative frequency
	ArrayList<Map<String, Integer>> neg_freq;
	// array of sets, each set corresponds to one feature
	ArrayList<Set<String>> feature_set;
	// array of hash tables for probability
	ArrayList<Map<String, Double>> p_pos;
	ArrayList<Map<String, Double>> p_neg;
	boolean initialized = false;
	double p_pos_class; // probability of a positive class 
	double p_neg_class; // probability of a negative class
	
	public NBClassifier() {
		// initialize the frequency counts and data structures
		n = 0; 
		num_pos = 0; 
		num_neg = 0;
		pos_freq = new ArrayList<Map<String, Integer>>();
		neg_freq = new ArrayList<Map<String, Integer>>();
		feature_set = new ArrayList<Set<String>>();
		p_pos = new ArrayList<Map<String, Double>>();
		p_neg = new ArrayList<Map<String, Double>>();
	}
	
	public void train(TrainingExample train_ex) {
		ArrayList<Map<String, Integer>> freq; 
		if (initialized == false) {
			// create HashMaps/Sets for all of our data structures
			for (int i = 0; i < train_ex.getFeatures().length; i++) {
				pos_freq.add(new HashMap<String, Integer>());
				neg_freq.add(new HashMap<String, Integer>());
				feature_set.add(new HashSet<String>());
				p_pos.add(new HashMap<String, Double>());
				p_neg.add(new HashMap<String, Double>());				
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
				freq.get(i).put(features[i], freq.get(i).get(features[i]) + 1);
			} else {
				// if we haven't see it before, initialize first occurrence
				freq.get(i).put(features[i], 1);
			}
			// add feature to the feature set
			feature_set.get(i).add(features[i]);
		}

		n++; 
	}

	public void calculate_probabilities() {
		// TODO: is log correct here
		p_pos_class = Math.log((double)num_pos / n);
		p_neg_class = Math.log((double)num_neg / n);
		
		// just calculate positive frequent, negative is the complement
		for (int i = 0; i < pos_freq.size(); i++) {
			// for each feature, update probability
			int denominator = num_pos + feature_set.get(i).size();
			Iterator<String> iter = feature_set.get(i).iterator();
			while (iter.hasNext()){
				int numerator = 1; // Laplacian smoothing
				String feature = iter.next();
				if (pos_freq.get(i).containsKey(feature)) {
					numerator += pos_freq.get(i).get(feature);
				}
				
				// update probability, use logarithms to avoid underflow
				p_pos.get(i).put(feature, Math.log((double)numerator/denominator));
			}
		}
		
		for (int i = 0; i < neg_freq.size(); i++) {
			// for each feature, update probability
			int denominator = num_neg + feature_set.get(i).size();
			Iterator<String> iter = feature_set.get(i).iterator();
			while (iter.hasNext()){
				int numerator = 1; // Laplacian smoothing
				String feature = iter.next();
				if (neg_freq.get(i).containsKey(feature)) {
					numerator += neg_freq.get(i).get(feature);
				}
				
				// update probability, use logarithms to avoid underflow
				p_neg.get(i).put(feature, Math.log((double)numerator/denominator));
			}
		}
	}
	
	
	@Override
	public String toString() {
		return "NBClassifier [n=" + n + ", num_pos=" + num_pos + ", num_neg="
				+ num_neg + ", pos_freq=" + pos_freq + ", neg_freq="
				+ neg_freq + ", feature_set=" + feature_set
				+ ", p_pos_class=" + p_pos_class + ", p_neg_class=" + 
				p_neg_class + ", p_pos=" + p_pos + ", p_neg=" + p_neg + "]";
	}

	public String classify(String[] features) {
		double pos_prob = p_pos_class;
		double neg_prob = p_neg_class;
		for (int i = 0; i < features.length; i++){
			pos_prob += p_pos.get(i).get(features[i]);
			neg_prob += p_neg.get(i).get(features[i]);
		}
		
		if (pos_prob > neg_prob) {
			return "+1";
		} else {
			return "-1";
		}
	}
}
