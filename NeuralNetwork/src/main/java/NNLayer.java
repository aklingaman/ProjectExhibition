package main.java;

import org.jblas.DoubleMatrix;

import java.io.*;
public class NNLayer implements Serializable {
	public DoubleMatrix biasVector;
	public DoubleMatrix weightMatrix;
	public NNLayer(int rowSize, int colSize) {
		biasVector = new DoubleMatrix(rowSize,1);
		weightMatrix = new DoubleMatrix(rowSize,colSize);
	}
}
