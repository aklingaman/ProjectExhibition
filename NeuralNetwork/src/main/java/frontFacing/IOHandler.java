package main.java.frontFacing; /**
 * This class handles IO as well as loading the values from the config for neural net specs.
 */

import main.java.Image;
import main.java.NeuralNet;
import main.java.util.LearnRateRegimens.StaticLearnRateRegimen;
import main.java.util.activations.ActivationFunctionProvider;
import main.java.util.activations.LeakyReluActivationFunctionProvider;
import main.java.util.activations.ReluActivationFunctionProvider;
import main.java.util.activations.SigmoidActivationFunctionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.io.*;
public class IOHandler {
    public static Logger LOG = LogManager.getLogger();


	//Takes in a configuration file that outlines the NN structure, and returns an NN using that structure. 	
	//Format: name: abc, inputsize: 784, hlquantity: 2, hlsize: 20, outputsize: 10
	//TODO: push learnrate to config file.
	public static NeuralNet createFromConfigFile(String path, String inputName) {
		try {	
            BufferedReader br = new BufferedReader(new FileReader(path));
			String line, name;

			//These values fail sanity check of NN, so that malformed config crashes.
			int inputsize = -1;
			int hlquantity = -1;
			int hlsize = -1;
			int outputsize = -1;
            ActivationFunctionProvider activationFunc = null;

			while((line = br.readLine()) !=null) {
				if(line.charAt(0)=='#') {
				    continue;
                }
			    String[] tokens = line.split(", ");
				if(tokens.length!=6) {
					continue;
				}
				if(!tokens[0].split(": ")[1].equals(inputName)) {
					continue; 
				}
				for(int i = 1; i<tokens.length; i++) {
					String[] chunks = tokens[i].split(": ");
					if(chunks.length!=2) {
						LOG.info("error in config reading, wrong num: "+ chunks.length);
						continue;
					}
					switch(i) {
						case 1: inputsize  = Integer.parseInt(chunks[1]); break;
						case 2: hlquantity = Integer.parseInt(chunks[1]); break;
						case 3: hlsize     = Integer.parseInt(chunks[1]); break;
						case 4: outputsize = Integer.parseInt(chunks[1]); break;
                        case 5:
                            switch(chunks[1]) {
                                case "sigmoid":
                                    activationFunc = SigmoidActivationFunctionProvider.getInstance();
                                    break;
                                case "relu":
                                    activationFunc = ReluActivationFunctionProvider.getInstance();
                                    break;
                                case "leakyrelu":
                                    activationFunc = LeakyReluActivationFunctionProvider.getInstance();
                                    break;

                            }

						
					}	
				}
				NeuralNet ret = new NeuralNet(inputsize, hlsize, hlquantity, outputsize, new StaticLearnRateRegimen(0.05), activationFunc);
				return ret;
			}
            LOG.error("Could not find config with name: {}",inputName);
			System.exit(1);
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	//reads in an already existing NN using serializable.
    public static NeuralNet readModelFromFile(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            NeuralNet model = (NeuralNet) in.readObject();
            in.close();
            return model;
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
       return null; 
    }

    public static void writeModelToFile(NeuralNet model, String path) {
        LOG.trace("Writing model to path - {}",path);
	    try {
            File f = new File(path);
            f.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f,false));
            out.writeObject(model);
            out.flush();
            return;
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return;
    }

    /**
     * Collects all the images stored in a CSV file into a data set, for use with either training or test data.
     * @param path - path to the CSV file.
     * @return List of images.
     */
	public static List<Image> collectImagesIntoDataSet(String path) {
        ArrayList<Image> dataSet = new ArrayList<Image>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while((line = br.readLine())!=null) {
                String[] vals = line.split(",");
                double[] pixels = new double[784];
                for(int i = 0; i<784; i++ ) {
                    pixels[i] = 1.0*Integer.parseInt(vals[i+1])/255;
                } 
                dataSet.add(new Image(Integer.parseInt(vals[0]),pixels)); 
            }
            return dataSet;
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null; //Needed for compilation only
    }




    
}
