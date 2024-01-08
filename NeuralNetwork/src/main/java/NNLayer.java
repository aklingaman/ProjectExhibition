package main.java;

import java.io.*;
public class NNLayer implements Serializable {
	public double[] biasVector;
	public double[][] weightMatrix;
	public NNLayer(int rowSize, int colSize) {
		biasVector = new double[rowSize];
		weightMatrix = new double[rowSize][colSize];	
	}
}
