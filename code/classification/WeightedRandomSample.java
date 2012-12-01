package classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class WeightedRandomSample {

	ArrayList<String[]> data;
	ArrayList<Double> weights;
	ArrayList<Double> d_save;
	public WeightedRandomSample(String training_filename, ArrayList<Double> d) {
		// read the file, store it in a array
		BufferedReader reader;
		data = new ArrayList<String[]>();
		try {
			reader = new BufferedReader(new FileReader(training_filename));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] splitLine = line.split("\t", -1);
				data.add(splitLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		d_save = d;
		// construct a summation of all weights
		weights = new ArrayList<Double>();
		Double weight_sum = 0.0;
		for (Double weight : d) {
			weight_sum += weight;
			weights.add(weight_sum);
		}
	}
	
	public String[] sample() {
		Double random_weight = Math.random();
		
		int i = 0;
		try {
			while (random_weight > weights.get(i)) {
				i++;
			}
		} catch (Exception e){
			System.out.println(i);
			System.out.println(weights.get(i-1));
			System.out.println(random_weight);
			System.out.println(d_save.get(i-1));
		}
		
		return data.get(i);
	}
}
