package classification;

import java.util.Arrays;

public class TrainingExample {
	String label;
	String[] features;
	
	public TrainingExample(String _label, String[] _features) {
		label = _label;
		features = _features;
	}

	public String getLabel() {
		return label;
	}

	public String[] getFeatures() {
		return features;
	}

	@Override
	public String toString() {
		return "TrainingExample [label=" + label + ", features="
				+ Arrays.toString(features) + "]";
	}
}
