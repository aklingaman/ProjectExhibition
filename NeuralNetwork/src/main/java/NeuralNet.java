package main.java;//Version 2 of the NN class. Supports 2 different types of NN objects. One is the actual NN, the other is just a container to store changes to a NN for use with backprop.

import main.java.util.LearnRateRegimens.LearnRateRegimen;
import main.java.util.LinAlg;
import main.java.util.activations.ActivationFunctionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;

import java.util.*;
import java.io.*;

public class NeuralNet implements Serializable{
	public static Logger LOG = LogManager.getLogger();
	private int HLS, layerCount, insize, outsize;
	private LearnRateRegimen learnRateRegimen;
	public NNLayer[] layers;
	private ActivationFunctionProvider activationFunction;

	/**
	 * Creates a new neural net. YOU MUST INVOKE INITIALIZE IF YOU WANT THE NET TO HAVE ANY VALUES.
	 *
	 *
	 * @param inputSize: # of elements in input vector. (standard mnist would be 28*28 = 784)
	 * @param HLS: hidden layer size
	 * @param layerCount: hidden layer quantity
	 * @param outputSize: output vector size ( number of possible ways to classify the deck )
	 * @param learnRateRegimen
	 */
    public NeuralNet(int inputSize, int HLS, int layerCount, int outputSize, LearnRateRegimen learnRateRegimen, ActivationFunctionProvider activationFunction) {
		if(inputSize<1 || HLS < 1 || layerCount < 1 || outputSize < 2 || activationFunction==null) {
			LOG.error("NN initializer failed sanity check on input parameters.");
			System.exit(1);
		}
		this.insize = inputSize;
		this.outsize = outputSize;
		this.HLS = HLS;
		this.layerCount = layerCount;
		this.learnRateRegimen = learnRateRegimen;
		this.activationFunction=activationFunction;
		layers = new NNLayer[layerCount];
		for(int i = 0; i< layerCount; i++) {
			if(i == 0) {
				layers[i] = new NNLayer(HLS,inputSize);
				continue;
			}
			if(i+1== layerCount) {
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
			DoubleMatrix weight = layers[i].weightMatrix;
			DoubleMatrix bias = layers[i].biasVector;
			for(int j = 0; j<weight.rows; j++) {
				for(int k = 0; k<weight.columns; k++) {
					weight.put(j,k, (rd.nextDouble()*(randomSpread*2))-randomSpread);
				}
			}
			for(int j = 0; j<bias.length; j++) {
				bias.put(j,(rd.nextDouble()*(randomSpread*2))-randomSpread);
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
		return new NeuralNet(insize,HLS, layerCount,outsize,learnRateRegimen,activationFunction);
	}

	public double train(List<Image> bucket) {
		NeuralNet grandDelta  = spawnContainerNet(); //Expresses the entire buckets desired changes
		NeuralNet transferNet = spawnContainerNet(); //Expresses one element of the bucket's desired changes.
        double cost = 0.0; //error metric, used for printing.
		int bucketSize = bucket.size();

		for(int i = 0; i<bucketSize; i++) {
            Image image = bucket.get(i);
			computeDelta(image,transferNet);

			grandDelta.combineNeuralNets(transferNet);
			DoubleMatrix output = fastForwardProp(image.getVectorizedImage()); //held for optional printing

            cost+= LinAlg.cost(output,image.label);

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
		List<DoubleMatrix> weightedActivations = new ArrayList<DoubleMatrix>();	//Holds our delta l's
		List<DoubleMatrix> activations = new ArrayList<DoubleMatrix>(); //jagged array, each subarray has different size



		//Forward pass, we use this instead of forwardprop() because this holds onto intermediate data rather than just output
		int layerIterationCount = 0;
		DoubleMatrix previousActivation = image.getVectorizedImage();
		do {
            activations.add(layers[layerIterationCount].weightMatrix.mmul(previousActivation));
            activations.get(layerIterationCount).add(layers[layerIterationCount].biasVector);
			//activations holds our z^(x,l)

			weightedActivations.add(activationFunction.deep().apply(activations.get(layerIterationCount)));
            //weighted activations holds our a^(x,l)

            layerIterationCount++;
			previousActivation = weightedActivations.get(layerIterationCount-1);
		} while(layerIterationCount< layerCount);


        //prime activation function onto output activation.
        activationFunction.shallowPrime().accept(activations.get(layerCount -1));

        //gradient of cost function is a^(x,L)-y. With y being a vector of all 0 except 1 on correct label.
        //I will insert this directly into the weighted activation since we dont need it anymore.

		//We no longer care about the actual weighted activations.
		//But the errors have the same dimensions, so we reuse the matrix for them.
		List<DoubleMatrix> activationErrors = weightedActivations;

		double alyAdjustment = activationErrors.get(layerCount -1).get(image.label)-1;
		activationErrors.get(layerCount -1).put(image.label,alyAdjustment);

        //this is our delta capital L ( error in the output )
		activationErrors.get(layerCount -1).muli(activations.get(layerCount -1));


		//input layer l = 0
		//w(0) //layers.get(0)
		//b(0) //layers.get(0)
		//z(0) //activation.get(0)
		//a(0) //weightedActivation.get(0)


		//Backpropagation of error pass.
        for(int i = layerCount-1; i>0; i-- ) {

			DoubleMatrix error = layers[i].weightMatrix.transpose().mmul(activationErrors.get(i));

			previousActivation = weightedActivations.get(i-1);
			activationFunction.shallowPrime().accept(previousActivation);
			error.muli(previousActivation);
			activationErrors.set(i-1,error);
        }
		//output
		for(int i = 0; i<layerCount; i++) {
			//The error in the bias of this layer is the error in the activation

			delta.layers[i].biasVector=activationErrors.get(i);


			DoubleMatrix activation = (i==0)? image.getVectorizedImage() : activations.get(i-1);
			DoubleMatrix activationError = activationErrors.get(i);
			for(int j = 0; j<layers[i].weightMatrix.rows; j++) {
				for(int k = 0; k<layers[i].weightMatrix.columns; k++) {

					double val = activation.get(k)
							*activationError.get(j);
					delta.layers[i].weightMatrix.put(j,k,val);
				}
			}
		}
		if(!delta.isSameShape(this)) {
			LOG.fatal("Net ended up changing shape");
		}
    }

	/**
	 * "Adds" 2 neural nets together.
	 *
 	 * @param delta
	 */
    public void combineNeuralNets(NeuralNet delta) {
		for(int i = 0; i<layers.length; i++) {
			layers[i].weightMatrix.addi(delta.layers[i].weightMatrix);
			layers[i].biasVector.addi(delta.layers[i].biasVector);
		}
    }

	//Multiplies a NN's weights and biases by a factor. This is used by the training function because we need to apply a learning rate that changes how fast changes get made.
	public void multiplyNNByScalar(double factor) {
		for(int i = 0; i<layers.length; i++) {
			DoubleMatrix weight = layers[i].weightMatrix;
			DoubleMatrix bias = layers[i].biasVector;
			bias.mulColumn(0,factor);
			for(int j = 0; j<weight.columns; j++) {
				weight.mulColumn(j,factor);
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
	public DoubleMatrix fastForwardProp(DoubleMatrix image) {
        if(image.length!=insize) {
            LOG.error("Bad image size passed to prediction");
        }

		DoubleMatrix activation = image;
		for(int i = 0; i< layerCount; i++) {
			activation = layers[i].weightMatrix.mmul(activation);
			activation.add(layers[i].biasVector);
			activationFunction.shallow().accept(activation);
		}
		return activation;
    }


	/**
	 * Compares if 2 neural nets have the same shape. Used to know whether we can "add" 2 nets together.
	 * Does not compare learn rates.
	 *
	 * @param comparedNet
	 * @return true iff nets have same "shape" (HLQ,HLS,insize,outsize)
	 */
	public boolean isSameShape(NeuralNet comparedNet) {
		if( this.layerCount !=comparedNet.layerCount ||
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
		sb.append("Hidden Layer Quantity: " + this.layerCount +"\n");
		sb.append("Output Layer Size: "     + this.outsize   +"\n");
		sb.append("Learn rate: "            + this.learnRateRegimen.toString() +"\n");
		return sb.toString();
	}

}
