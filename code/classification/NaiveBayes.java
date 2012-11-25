package classification;

import java.io.*;
import java.util.Arrays;

public class NaiveBayes {
	public static void main (String [] args) {
		if (args.length < 2) {
			System.out.println("Usage: java NaiveBayes, training_file testing_file");
			System.exit(0);
		} else {
			// TODO: classify each test object
			// TODO: print out basic statistics (TP, TN, FP, FN)

			// Create a new classifier
			NBClassifier nbclass = new NBClassifier();
			
			// TODO: move the training section into a function/method?
			// TODO: add logging? 
			// read in the training file, print label, number of attributes, total examples
			System.out.format("Reading training file: %s\n", args[0]);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(args[0]));
				String line = null;
				int num_train = 0;
				int num_features = 0;
				while ((line = reader.readLine()) != null) {
					//System.out.println(line);
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
			
			System.out.println(nbclass);
		}
	}
}
