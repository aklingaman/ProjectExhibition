package main.java;

import org.jblas.DoubleMatrix;

/**
 * POJO Container class for training/test data.
 */
public class Image {
    public int label;
    public double[][] data;
    private DoubleMatrix vectorizedImage;
    public Image(int label, double[][] data) {
        this.label = label;
        this.data = data;
    }
    public DoubleMatrix getVectorizedImage() {
        if(vectorizedImage!=null) {
            return vectorizedImage;
        }

        double[] imageAsVector = new double[data.length*data[0].length];
        int count = 0;
        for(int i = 0; i<data.length; i++) {
            for(int j = 0; j<data[0].length; j++) {
                imageAsVector[count++]=data[i][j];
            }
        }

        vectorizedImage=new DoubleMatrix(imageAsVector);
        return vectorizedImage;
    }

}
