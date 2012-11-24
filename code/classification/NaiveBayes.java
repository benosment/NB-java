package classification;

import java.io.*;

public class NaiveBayes {
	public static void main (String [] args) {
		if (args.length < 2) {
			System.out.println("Usage: java NaiveBayes, training_file testing_file");
			System.exit(0);
		} else {
			// TODO: initialize a classifier object?
			// TODO: read the training file, create a training object for each instance
			// TODO: read the test file, create a test object for each instace
			// TODO: add training object to the classifier
			// TODO: classify each test object
			// TODO: print out basic statistics (TP, TN, FP, FN)
			
			// read in the training file, print label, number of attributes, total examples
			System.out.format("Reading training file: %s\n", args[0]);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(args[0]));
				String line = null;
				int num_train = 0;
				int num_attributes = 0;
				while ((line = reader.readLine()) != null) {
					//System.out.println(line);
					String[] splitLine = line.split("\t", -1);
					num_attributes = splitLine.length;
					num_train++;
				}
				reader.close();
				System.out.format("Total of %d training examples with %d attributes\n", num_train, num_attributes);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
