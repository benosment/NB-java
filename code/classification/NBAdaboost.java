package classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NBAdaboost {
	public static void main (String [] args) {
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;

		if (args.length < 2) {
			System.out.println("Usage: java classification.NBAdaboost training_file testing_file");
			System.exit(0);
		} else {
	
			String training_filename = args[0];
			String testing_filename = args[1];
			
			// Create a set of all attributes to be used later for Laplace smoothing
			// array of sets, each set corresponds to one feature
			ArrayList<Set<String>> feature_set = new ArrayList<Set<String>>();
			boolean initialized = false;
			BufferedReader reader;		
			int n = 0;
			try {
				reader = new BufferedReader(new FileReader(training_filename));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] splitLine = line.split("\t", -1);
					String[] features = Arrays.copyOfRange(splitLine, 1, splitLine.length); 
					if (initialized == false) {
						// create HashSets for all of our data structures
						for (int i = 0; i < features.length; i++) {
							feature_set.add(new HashSet<String>());		
						}
						initialized = true;
					}

					for (int i = 0; i < features.length; i++){
						// add feature to the feature set
						feature_set.get(i).add(features[i]);
					}
					n++;
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			// Create D, the array of weights
			ArrayList <Double> D = new ArrayList<Double>();
			for (int i = 0; i < n; i++){
				// initially each element to 1 / |D|
				D.add((1.0 / n));
			}
		
			ArrayList <NBAClassifier> classifiers = new ArrayList<NBAClassifier>();
			ArrayList <Double> alphas = new ArrayList<Double>();
			
			// create up to 8 classifiers, bail out early of accuracy is 1.0
			for (int i = 0; i < 8; i++){
				NBAClassifier nba = new NBAClassifier(feature_set);
				// create a weighted random sample based off of D
				WeightedRandomSample wrs = new WeightedRandomSample(training_filename, D);
				// sample N times
				for (int j = 0; j < n; j++) {
					String[] data = wrs.sample();
					String label = data[0];
					String[] features = Arrays.copyOfRange(data, 1, data.length); 
					TrainingExample ex = new TrainingExample(label, features);
					// train on the random sample
					nba.train(ex);
				}
				tp = 0;
				fp = 0;
				tn = 0;
				fn = 0;
				
				// calculate the probabilities for each class
				nba.calculate_probabilities();

				ArrayList<Integer> errors = new ArrayList<Integer>();
				
				// test the classifier on the original test data
				try {
					reader = new BufferedReader(new FileReader(training_filename));
					String line = null;
					while ((line = reader.readLine()) != null) {
						String[] splitLine = line.split("\t", -1);
						String label = splitLine[0];
						String predicted_label;
						String[] features = Arrays.copyOfRange(splitLine, 1, splitLine.length); 
											predicted_label = nba.classify(features);
						if (label.equals("+1")) {
							if (predicted_label.equals("+1")) {
								tp += 1;
								errors.add(0);
							} else {
								fn += 1;
								errors.add(-1);
							}
						} else {
							if (predicted_label.equals("-1")) {
								tn += 1;
								errors.add(0);
							} else {
								fp += 1;
								errors.add(-1);
							}
						}
					}
					reader.close();
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}				

				// calculate accuracy
				double accuracy = (double)(tp + tn) / (tp + fp + tn + fn);
				
				// calculate alpha
				double alpha = 0.5 * Math.log(accuracy / Math.max((1.0-accuracy), 1e-16));
				
				// readjust weights
				double d_sum = 0.0, new_weight;				
				System.out.format("TP: %d TN %d FP %d FN %d\n", tp, tn, fp, fn);								
				for (int k = 0; k < errors.size(); k++) {
					if (errors.get(k) == -1) {
						// prediction was incorrect, increase weight
						new_weight = D.get(k) * Math.exp(alpha);
						d_sum += new_weight;
						D.set(k, new_weight);
					} else {
						// prediction was correct, decrease weight
						new_weight = D.get(k) * Math.exp(-alpha);
						d_sum += new_weight;
						D.set(k, new_weight);
					}
				}
				
				// normalize D to sum to 1
				for (int k = 0; k < D.size(); k++) {
					D.set(k, D.get(k)/d_sum);
				}

				// add classifier, alpha to the list
				classifiers.add(nba);
				alphas.add(alpha);
				
				if ((fp == 0) && (fn == 0)) {
					break;
				}
			}
			
			// reset counts
			tp = 0;
			fp = 0;
			tn = 0;
			fn = 0;		
			
			// classify each test instance
			System.out.format("Reading test file: %s\n", testing_filename);
			try {
				reader = new BufferedReader(new FileReader(testing_filename));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] splitLine = line.split("\t", -1);
					String label = splitLine[0];
					String predicted_label;

					String[] features = Arrays.copyOfRange(splitLine, 1, splitLine.length); 
					predicted_label = ensemble_classify(classifiers, alphas, features);
					if (label.equals("+1")) {
						if (predicted_label.equals("+1")) {
							tp += 1;
						} else {
							fn += 1;
						}
					} else {
						if (predicted_label.equals("-1")) {
							tn += 1;
						} else {
							fp += 1;
						}
					}
				}
				reader.close();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			// true positive, false negative, false positive and true negative.

			//System.out.format("%d\n%d\n%d\n%d\n", tp, fn, fp, tn);
			
			System.out.format("True Positive: %d\n", tp);
			System.out.format("True Negative: %d\n", tn);
			System.out.format("False Positive: %d\n", fp);
			System.out.format("False Negative: %d\n", fn);
			System.out.format("Accuracy: %f\n", (double)(tp+tn)/(tp+tn+fp+fn));
			System.out.format("Error Rate: %f\n", (double)(fp+fn)/(tp+tn+fp+fn));
			System.out.format("Sensitivity: %f\n", (double)(tp)/(tp+fn));
			System.out.format("Specificity: %f\n", (double)(tn)/(tn+fn));
			System.out.format("Precision: %f\n", (double)(tp)/(tp+fp));
			double precision = (double)(tp) / (tp + fp);
			double recall = (double)(tp) / (tp + fn);
			double f = (2 * precision * recall) / (precision + recall);
			double f_beta_0_5 = ((1 + (0.5)*(0.5)) * precision * recall) / (0.5 * 0.5 * precision + recall);
			double f_beta_2 = ((1 + (2)*(2)) * precision * recall) / (2 * 2 * precision + recall);
			System.out.format("F1 score: %f\n", f);
			System.out.format("F beta (0.5): %f\n", f_beta_0_5);
			System.out.format("F beta (2): %f\n", f_beta_2);		
		}
	}

	private static String ensemble_classify(
			ArrayList<NBAClassifier> classifiers, ArrayList<Double> alphas,
			String[] features) {
		double prediction = 0.0;
		String predicted_label;
		
		// have each classifier predict the label
		for (int i = 0; i < classifiers.size(); i++) {
			predicted_label = classifiers.get(i).classify(features);
			if (predicted_label == "+1") {
				prediction += 1.0 * alphas.get(i);
			} else {
				prediction -= 1.0 * alphas.get(i);
			}
		}
		
		if (prediction > 0.0) {
			return "+1";
		} else {
			return "-1";
		}
	}
}
