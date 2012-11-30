package classification;

import java.io.*;
import java.util.Arrays;

public class NaiveBayes {
	public static void main (String [] args) {
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;

		if (args.length < 2) {
			System.out.println("Usage: java classification.NaiveBayes training_file testing_file");
			System.exit(0);
		} else {
			// Create a new classifier
			NBClassifier nbclass = new NBClassifier();
			

			// read in the training file, print label, number of attributes, total examples
			System.out.format("Reading training file: %s\n", args[0]);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(args[0]));
				String line = null;
				int num_train = 0;
				int num_features = 0;
				while ((line = reader.readLine()) != null) {
					String[] splitLine = line.split("\t", -1);
					num_features = splitLine.length - 1;
					num_train++;
					String label = splitLine[0];
					// TODO need a better way of doing this
					String[] features = Arrays.copyOfRange(splitLine, 1, splitLine.length); 
					TrainingExample train_ex = new TrainingExample(label, features);
					nbclass.train(train_ex);
				}
				reader.close();
				System.out.format("Total of %d training examples with %d features\n", num_train, num_features);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			// calculate the probabilities for each class
			nbclass.calculate_probabilities();
			
			// classify each test instance
			System.out.format("Reading test file: %s\n", args[1]);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(args[1]));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] splitLine = line.split("\t", -1);
					String label = splitLine[0];
					String predicted_label;
					// TODO need a better way of doing this
					String[] features = Arrays.copyOfRange(splitLine, 1, splitLine.length); 
										predicted_label = nbclass.classify(features);
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
			
			//System.out.println(nbclass);
			// true positive, false negative, false positive and true negative.
			//System.out.format("TP: %d TN %d FP %d FN %d\n", tp, tn, fp, fn);
			System.out.format("%d\n%d\n%d\n%d\n", tp, fn, fp, tn);
		}
	}
}
