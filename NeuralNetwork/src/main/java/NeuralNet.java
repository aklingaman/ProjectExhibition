package main.java;//Version 2 of the NN class. Supports 2 different types of NN objects. One is the actual NN, the other is just a container to store changes to a NN for use with backprop.

import main.java.util.LearnRateRegimens.LearnRateRegimen;
import main.java.util.LinAlg;
import main.java.util.activations.ActivationFunctionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.io.*;

public class NeuralNet implements Serializable{
	public static Logger LOG = LogManager.getLogger();
	private int HLS, HLQ, insize, outsize;
	private LearnRateRegimen learnRateRegimen;
	public NNLayer[] layers;
	private ActivationFunctionProvider activationFunction;

	/**
	 * Creates a new neural net. YOU MUST INVOKE INITIALIZE IF YOU WANT THE NET TO HAVE ANY VALUES.
	 *
	 *
	 * @param inputSize: # of elements in input vector. (standard mnist would be 28*28 = 784)
	 * @param HLS: hidden layer size
	 * @param HLQ: hidden layer quantity
	 * @param outputSize: output vector size ( number of possible ways to classify the deck )
	 * @param learnRateRegimen
	 */
    public NeuralNet(int inputSize, int HLS, int HLQ, int outputSize, LearnRateRegimen learnRateRegimen, ActivationFunctionProvider activationFunction) {
		if(inputSize<1 || HLS < 1 || HLQ < 1 || outputSize < 2 || activationFunction==null) {
			LOG.error("NN initializer failed sanity check on input parameters.");
			System.exit(1); 
		}        
		this.insize = inputSize;
		this.outsize = outputSize;
		this.HLS = HLS;
		this.HLQ = HLQ;
		this.learnRateRegimen = learnRateRegimen;
		this.activationFunction=activationFunction;
		layers = new NNLayer[HLQ+1]; //+1 is for the output layer.
		for(int i = 0; i<=HLQ; i++) {
			if(i == 0) {
				layers[i] = new NNLayer(HLS,inputSize);
				continue;
			}
			if(i==HLQ) {
				layers[i] = new NNLayer(outputSize,HLS);
				continue;
			}
			layers[i] = new NNLayer(HLS,HLS);
		}
    }

	/**
	 * Initializes a neural net.
	 * @param randomSeed randomness seed. Same seed same numbers. Put null if you dont want to supply for true randomness.
	 * @param randomSpread changes how far the numbers will be from 0, uniform dist from (-spread,+spread)
	 */
	public void initialize(Integer randomSeed, double randomSpread) {
		Random rd = randomSeed==null?new Random():new Random(randomSeed);
		for(int i = 0; i<layers.length; i++) {
			double[][] weight = layers[i].weightMatrix;
			double[] bias = layers[i].biasVector;
			for(int j = 0; j<weight.length; j++) {
				for(int k = 0; k<weight[j].length; k++) {
					weight[j][k] = (rd.nextDouble()*(randomSpread*2))-randomSpread;
				}
			}
			for(int j = 0; j<bias.length; j++) {
				bias[j] = (rd.nextDouble()*(randomSpread*2))-randomSpread;
			}
		}
	}

	/**
	 * Creates a new main.java.NeuralNet that has NO VALUES, but is the same size/shape as the caller.
	 *
	 *
	 * @Return: an EMPTY net.
	 */
	public NeuralNet spawnContainerNet() {
		return new NeuralNet(insize,HLS,HLQ,outsize,learnRateRegimen,activationFunction);
	}

	public double train(List<Image> bucket) {
		NeuralNet grandDelta  = spawnContainerNet(); //Expresses the entire buckets desired changes
		NeuralNet transferNet = spawnContainerNet(); //Expresses one element of the bucket's desired changes.
        double cost = 0.0; //error metric, used for printing.
		int size = bucket.size(); 
		for(int i = 0; i<size; i++) { 
            Image image = bucket.get(i);
			computeDelta(image,transferNet);
            grandDelta.combineNeuralNets(transferNet); 
			double[] output = fastForwardProp(image.data); //held for optional printing
            cost+=cost(output,image.label);
			//LOG.info(Arrays.toString(output));
        }   
        cost/=bucket.size();
		grandDelta.multiplyNNByScalar(-1.0*learnRateRegimen.nextLearnRate(this)/bucket.size()); //Scales the delta NN by the learn rate factor.
		combineNeuralNets(grandDelta);
		return cost;
	}

    //Computes the desired changes to the neuralnet for a particular input, and stores into a delta NN container.
    public void computeDelta(Image image, NeuralNet containerNet) {
        NeuralNet delta = containerNet;
		double[][] weightedActivations = new double[HLQ+1][];	//Holds our delta l's
		double[][] activations = new double[HLQ+1][]; //jagged array, each subarray has different size
		//Forward pass, we use this instead of forwardprop() because this holds onto intermediate data rather than just output
		for(int i = 0; i<=HLQ; i++) {
			double[] previousActivation = (i==0)? image.data : weightedActivations[i-1];
			activations[i] = LinAlg.matrixVectorMult(layers[i].weightMatrix, previousActivation);
			LinAlg.vectorAdditionShallow(activations[i],layers[i].biasVector);
			weightedActivations[i] = activationFunction.deep().apply(activations[i]);

			//LinAlg.sigmoidVectorShallow(activations[i]);
		}

		LOG.trace("Output weighted activations (label {}): {}",image.label,weightedActivations[HLQ]);

		activationFunction.shallow().accept(activations[HLQ]);
		weightedActivations[HLQ][image.label] -= 1; //aL-y
		LinAlg.hardamadShallow(activations[HLQ], weightedActivations[HLQ]); //delta capital L
		LOG.trace("Output activation errors (label {}): {}",image.label,activations[HLQ]);
		//LOG.trace("----------------------");
		//Backprop pass
        for(int i = HLQ-1; i>=0; i--) {		
			weightedActivations[i] = LinAlg.matrixVectorMultTranspose(layers[i+1].weightMatrix,activations[i+1]);
			activationFunction.shallowPrime().accept(activations[i]);
			LinAlg.hardamadShallow(activations[i],weightedActivations[i]);
		}	
		//output
		for(int i = 0; i<HLQ; i++) { 
			for(int j = 0; j<delta.layers[i].biasVector.length; j++) {
				delta.layers[i].biasVector[j] = activations[i][j];
			}
			double[] activation = (i==0)? image.data : activations[i-1];     
			for(int j = 0; j<delta.layers[i].weightMatrix.length; j++) { 
				for(int k = 0; k<delta.layers[i].weightMatrix[j].length; k++) {
					delta.layers[i].weightMatrix[j][k] = activation[k]*activations[i][j];
				}
			}
		}
    }

	/**
	 * "Adds" 2 neural nets together.
	 *
 	 * @param delta
	 */
    public void combineNeuralNets(NeuralNet delta) {
		for(int i = 0; i<layers.length; i++) {
			for(int j = 0; j<layers[i].weightMatrix.length; j++) {
				for(int k = 0; k<layers[i].weightMatrix[j].length; k++) {
					layers[i].weightMatrix[j][k] += delta.layers[i].weightMatrix[j][k];
				}
			}
			for(int j = 0; j<layers[i].biasVector.length; j++) {
				layers[i].biasVector[j]+=delta.layers[i].biasVector[j];
			}
		}
    }

	//Multiplies a NN's weights and biases by a factor. This is used by the training function because we need to apply a learning rate that changes how fast changes get made. 
	public void multiplyNNByScalar(double factor) {
		for(int i = 0; i<layers.length; i++) {
			double[][] weight = layers[i].weightMatrix;
			double[] bias = layers[i].biasVector;
			for(int j = 0; j<bias.length; j++) {
				bias[j]*=factor;
			}
			for(int j = 0; j<weight.length; j++) {
				for(int k = 0; k<weight[j].length; k++) {
					weight[j][k]*=factor;
				}
			}
		}			
	}

	/**
	 * Puts an image through the NN, and gets the output expressed as a vector.
	 * Does not hold onto any intermediate vectors.
	 *
	 * @param image - the image you wish to classify
	 * @return - vector prediction of the image.
	 */
	public double[] fastForwardProp(double[] image) {
        if(image.length!=insize) {
            LOG.error("Bad image size passed to prediction");
        }

		
		double[] activation = image;
		for(int i = 0; i<HLQ+1; i++) {
			activation = LinAlg.matrixVectorMult(layers[i].weightMatrix,activation);
			LinAlg.vectorAdditionShallow(activation, layers[i].biasVector);
			activationFunction.shallow().accept(activation);
		}
		return activation;
    }


	/**
	 * Returns the "cost" of a particular guess vector. This cost is expressed as the square of the differences between the desired value and observed value.
	 * A perfect guess will have a cost of 0.
	 * This value is used as a diagnostic, but is COMPUTATIONALLY EXPENSIVE IN MASS QUANTITIES, so is only done in debug mode.
	 *
	 * @param prediction - output vector of a data point being passed through NN
	 * @param realAns - the actual label of what the value should be
	 * @return - decimal value expressing cost, lower is better.
	 */
	public static double cost(double[] prediction, int realAns){
		double ret = 0;
        for(int i = 0; i<prediction.length; i++) {
			double val = prediction[i]-(i==realAns?1:0);
        	ret+=val*val; //Math.pow(val,2) is more expensive since it has to deal with general case, and we dont.
        }
        return ret/2;
    }

	/**
	 * Compares if 2 neural nets have the same shape. Used to know whether we can "add" 2 nets together.
	 * Does not compare learn rates.
	 *
	 * @param comparedNet
	 * @return true iff nets have same "shape" (HLQ,HLS,insize,outsize)
	 */
	public boolean isSameShape(NeuralNet comparedNet) {
		if( this.HLQ!=comparedNet.HLQ         ||
			this.HLS!=comparedNet.HLS         ||
			this.insize!=comparedNet.insize   ||
			this.outsize!=comparedNet.outsize
		) {
			return false;
		}
		return true;
	}

	//Returns a String containing a lot of metadata, used by drivers metadata function to print to user in command line
	public String metadata() {
		StringBuilder sb = new StringBuilder();
		sb.append("Input Layer Size: "      + this.insize    +"\n");
		sb.append("Hidden Layer Size: "     + this.HLS       +"\n");
		sb.append("Hidden Layer Quantity: " + this.HLQ       +"\n");
		sb.append("Output Layer Size: "     + this.outsize   +"\n");
		sb.append("Learn rate: "            + this.learnRateRegimen.toString() +"\n");
		return sb.toString();
	}

}
