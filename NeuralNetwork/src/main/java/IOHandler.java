package main.java; /**
 * This class handles IO as well as loading the values from the config for neural net specs.
 */

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

			while((line = br.readLine()) !=null) {
				String[] tokens = line.split(", ");
				if(tokens.length!=5) {
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
					int val = Integer.parseInt(chunks[1]);
					switch(i) {
						case 1: inputsize  = val; break;
						case 2: hlquantity = val; break;
						case 3: hlsize     = val; break;
						case 4: outputsize = val; break;
						
					}	
				}
				NeuralNet ret = new NeuralNet(inputsize, hlsize, hlquantity, outputsize, 0.05);
				return ret;
			}

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
