package classification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			// Create D, the array of weights
			ArrayList <Double> D = new ArrayList<Double>();
			for (int i = 0; i < feature_set.size(); i++){
				// initially each element to 1 / |D|
				D.add((1.0 / feature_set.size()));
			}
			
			// create a list of classifiers
			ArrayList <NBAClassifier> classifiers = new ArrayList<NBAClassifier>();
			
			// create up to 8 classifiers, bail out early of accuracy is 1.0
			for (int i = 0; i < 8; i++){
				NBAClassifier nba = new NBAClassifier(feature_set);
				// create a weighted random sample based off of D
				WeightedRandomSample wrs = new WeightedRandomSample(training_filename, D);

				// TODO sample N times
				// TODO train on the random sample
				// TODO get tp, tn, fp, fn, errors from test
				// TODO calculate accuracy
				// TODO calculate alpha
				// TODO readjust weights
				
				// TODO test ensemble
			}
			
//			// read in the training file, print label, number of attributes, total examples
//			System.out.format("Reading training file: %s\n", args[0]);
//			try {
//				BufferedReader reader = new BufferedReader(new FileReader(args[0]));
//				String line = null;
//				int num_train = 0;
//				int num_features = 0;
//				while ((line = reader.readLine()) != null) {
//					String[] splitLine = line.split("\t", -1);
//					num_features = splitLine.length - 1;
//					num_train++;
//					String label = splitLine[0];
//					// TODO need a better way of doing this
//					String[] features = Arrays.copyOfRange(splitLine, 1, splitLine.length); 
//					TrainingExample train_ex = new TrainingExample(label, features);
//					nbclass.train(train_ex);
//				}
//				reader.close();
//				System.out.format("Total of %d training examples with %d features\n", num_train, num_features);
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//			
//			// calculate the probabilities for each class
//			nbclass.calculate_probabilities();
//			
//			// classify each test instance
//			System.out.format("Reading test file: %s\n", args[1]);
//			try {
//				BufferedReader reader = new BufferedReader(new FileReader(args[1]));
//				String line = null;
//				while ((line = reader.readLine()) != null) {
//					String[] splitLine = line.split("\t", -1);
//					String label = splitLine[0];
//					String predicted_label;
//					// TODO need a better way of doing this
//					String[] features = Arrays.copyOfRange(splitLine, 1, splitLine.length); 
//										predicted_label = nbclass.classify(features);
//					if (label.equals("+1")) {
//						if (predicted_label.equals("+1")) {
//							tp += 1;
//						} else {
//							fn += 1;
//						}
//					} else {
//						if (predicted_label.equals("-1")) {
//							tn += 1;
//						} else {
//							fp += 1;
//						}
//					}
//				}
//				reader.close();
//				
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//			
			//System.out.println(nbclass);
			// true positive, false negative, false positive and true negative.
			//System.out.format("TP: %d TN %d FP %d FN %d\n", tp, tn, fp, fn);
			System.out.format("%d\n%d\n%d\n%d\n", tp, fn, fp, tn);
		}
	}
}
