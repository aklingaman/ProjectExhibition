package main.java;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Driver {
	public static Logger LOG = LogManager.getLogger();
	public static String path = System.getProperty("user.dir");

	public static void main(String[] args) {
		LOG.info("Basic Neural Network made by Adam Klingaman");
		LOG.info("Type help for help or info for more info needed to get started for yourself. ");

		Scanner sc = new Scanner(System.in);
		String ans;
		while((ans=sc.nextLine())!=null) {
			String[] tokens = ans.split(" ");
			tokens[0] = tokens[0].toLowerCase();
			switch(tokens[0]) {
				case "create":     create(tokens);   break;
				case "train":      train(tokens);    break;
				case "test" :      test(tokens);     break;
				case "experiment": experiment();     break;
				case "help":       help();           break;
				case "metadata":   metadata(tokens); break;
				case "info":       info();           break;
				case "exit":       exit();
				default:  LOG.info("Not an option");
			} 
		}		
	}

	//attempt to create a NN given the following name and configuration, returns success or fail. 
	public static void create(String[] tokens) {
		String name = tokens[1];
		String configuration = tokens[2];
		NeuralNet model = IOHandler.createFromConfigFile(path+"/config/config.txt", configuration);
		if(tokens.length==4) {
			model.initialize(Integer.parseInt(tokens[3]));
		} else {
			model.initialize();
		}
		IOHandler.writeModelToFile(model,path+"/models/"+name);
		LOG.info("Successfully created a model and serialized it under models");
	}

	public static void train(String[] tokens){
		String nnPath = tokens[1];
		List<Image> trainingSet = IOHandler.collectImagesIntoDataSet(path+"/data/mnist_train.csv");
		if(trainingSet==null||trainingSet.size()==0) {
			LOG.error("Unable to get training data");
			return;
		}
		LOG.info("Succesfully managed to obtain training data with: "+trainingSet.size()+" data points");
		NeuralNet model = IOHandler.readModelFromFile(path+"/models/"+nnPath);
		if(model==null) {
			LOG.error("Unable to find file");
			System.exit(1);
		}
		LOG.info("Succesfully managed to obtain the model from file");
		LOG.info("Do not close this until it says training complete or progress will be lost.");
		int printFrequency = 500;  //1 out of every PrintFrequency buckets, gets some stuff printed.
		int epochCount = 5000;
		long startTime = System.currentTimeMillis();
		int trainingSetSize = trainingSet.size();	
		int bucketSize = 100;
		model.setLearnRate(0.01);
		ArrayList<Image> bucket = new ArrayList<Image>();
		for(int i = 0; i<epochCount; i++) {
			bucket.clear();
			int size = 0;
			while(size++<bucketSize) { 
				int random = (int)(Math.random()*trainingSetSize); 
				bucket.add(trainingSet.get(random));
			}
			double cost = model.train(bucket);
			if(i%printFrequency==0) {
				LOG.info("Training bucket num: " + i+" avg cost: " + cost);
			}
		}	 
		LOG.info("Training complete, total time: " + 1.0*(System.currentTimeMillis()-startTime)/1000 + " seconds.");
		IOHandler.writeModelToFile(model,path + "/models/" + nnPath);
	}
	public static void test(String[] tokens) {
		String nnPath = tokens[1];
		List<Image> testSet = IOHandler.collectImagesIntoDataSet(path+"/data/mnist_test.csv");
		if(testSet==null||testSet.size()==0) { 
			LOG.error("Error getting test data");
			return;
		}
		LOG.info("Successfully obtained testing data");
		NeuralNet model = IOHandler.readModelFromFile(path+"/models/"+nnPath);
		if(model!=null) {
			LOG.info("Successfully obtained model");
		} else {
			LOG.error("Unable to find file");
			System.exit(1);
		}
		//Begin testing procedure
		int count = 0;
		int correct = 0;
		int[] guesses = new int[10];
		int[] actual = new int[10];
		for(Image i : testSet) {
			int prediction = LinAlg.vote(model.fastForwardProp(i.data));
			count++;
			guesses[prediction]++;
			actual[i.label]++;
			if(prediction==i.label) {
				correct++;
			} 
		}
		String guessesDist = Arrays.stream(guesses).mapToObj(g->""+g+": "+guesses[g]).collect(Collectors.joining(", "));
		String actualDist = Arrays.stream(actual).mapToObj(a->""+a+": "+actual[a]).collect(Collectors.joining(", "));
		LOG.info("Test results: ");
		LOG.info("Distribution of model guesses: ");
		LOG.info(guessesDist);
		LOG.info("");
		LOG.info("Distribution of actual images: ");
		LOG.info(actualDist);
		LOG.info("");
		LOG.info("Model classified {} correctly out of {} testing records",correct,count);
		LOG.info(100.0*correct/count+"% accuracy.");
	}

	//Dummy function used for messing around with stuff. Used the word experiment to distinguish from test. 	
	public static void experiment() {
		LOG.info("Current experiment: making a bunch of identical NN's except with different learning rates to see which ones can converge to a solution");
		double[] learnRates = {0.0001,0.0005, 0.001, 0.005, 0.01,0.05,0.1,0.5};
		List<Image> trainingSet = IOHandler.collectImagesIntoDataSet(path+"/data/mnist_train.csv");
		for(int i = 0; i<learnRates.length; i++) {
			NeuralNet model = IOHandler.createFromConfigFile(path+"/config/config.txt", "mnist2by20");
			for(int j = 0; j<1; j++) {
				for(int k = 0; k<10000; k++) { 
					ArrayList<Image> bucket = new ArrayList<Image>();
					int size = 0;
					while(size<100) { //Change the 100 to change how big the buckets are.
						int random = (int)(Math.random()*60000); //60000 is the number of elements in the training set. 
						bucket.add(trainingSet.get(random));
						size++;
					}
					model.setLearnRate(learnRates[i]);	
					double cost = model.train(bucket);
				}
				List<Image> testSet = IOHandler.collectImagesIntoDataSet(path+"/data/mnist_test.csv");
				int count = testSet.size();
				int correct = 0;
				for(Image l : testSet) {
					int prediction = LinAlg.vote(model.fastForwardProp(l.data));
					if(prediction == l.label) {
						correct++;
					}
				}
				double accuracy = 1.0*correct/count;
				LOG.info("learnRate: " + learnRates[i] + " run " + j + "/5" + ", Accuracy: " + accuracy);
			}
		}
	}
	public static void help() {
		LOG.info("Usage:\n\tcreate <filename> <configuration>\n\ttrain <filename>\n\ttest <filename>\n\texit\n\texperiment\n\tmetadata <filename>\n\tinfo\n");
	}

	public static void info() {
		LOG.info("This Neural net works off of CSV files, in the format provided by https://pjreddie.com/projects/mnist-in-csv/");
		LOG.info("I cannot provide this in git for it to work out of the box because it exceeds file size restrictions. ");
		LOG.info("Sample configurations and their names are located in config directory");
		LOG.info("Experiment is just a dummy function that can be edited to do whatever you want with it.");
		LOG.info("For now, improper usage will dump excess tokens, or just crash. ");
		LOG.info("Also note that the way Serializable works, any changes to NN's source will invalidate any models you have stored.");
		LOG.info("Metadata will spit out all info needed to uniquely identify a neural net by its values.");
	}




	//prints out an overview of a NN's characteristic meta params. Does this by actually creating the NN, so its expensive, so dont spam it. 
	public static void metadata (String[] tokens) {
		LOG.info(IOHandler.readModelFromFile(path+"/models/"+tokens[1]).metadata());
	}

	public static void exit(){
		System.exit(0);
	}

}
