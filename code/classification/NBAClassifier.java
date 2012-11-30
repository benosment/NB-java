package classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NBAClassifier extends NBClassifier {

	public NBAClassifier(ArrayList<Set<String>> _feature_set) {
		super();
		feature_set = _feature_set;
	}

	public void train(TrainingExample train_ex) {
		ArrayList<Map<String, Integer>> freq; 
		if (initialized == false) {
			// create HashMaps/Sets for all of our data structures
			for (int i = 0; i < train_ex.getFeatures().length; i++) {
				pos_freq.add(new HashMap<String, Integer>());
				neg_freq.add(new HashMap<String, Integer>());
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
		}

		n++; 
	}
}
