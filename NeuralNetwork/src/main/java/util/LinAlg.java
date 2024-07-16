package main.java.util;//Contains a bunch of static linear algebra functions, primarily split on a shallow copy and a deep copy.
//Most of these functions get run millions of times, so no sanity checks. 
//Shallow functions will perform onto the first vector passed, deep allocates new memory and returns it.

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;

import java.util.*;

public abstract class LinAlg {
    public static Logger LOG = LogManager.getLogger();

    public static void hardamadShallow(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= b[i];
        }
    }

    public static double[] hardamadDeep(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] * b[i];
        }
        return c;
    }

    public static void vectorAdditionShallow(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    public static double[] vectorAdditionDeep(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }


    //Performs matrix vector mult but on the matrix' transpose. More efficient than actually calculating the transpose	
    public static double[] matrixVectorMultTranspose(double[][] matrix, double[] vector) {
        double[] ans = new double[matrix[0].length];
        for (int i = 0; i < matrix[0].length; i++) {
            ans[i] = 0;
            for (int j = 0; j < matrix.length; j++) {
                double a = matrix[j][i];
                double b = vector[j];
                ans[i] += matrix[j][i] * vector[j];
            }
        }
        return ans;
    }


    public static double[][] computeTranspose(double[][] matrix) {
        double[][] transposeMatrix = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < transposeMatrix.length; i++) {
            for (int j = 0; j < transposeMatrix[0].length; j++) {
                transposeMatrix[i][j] = matrix[j][i];
            }
        }
        return transposeMatrix;
    }


    public static void reluShallowVector(double[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] < 0) {
                a[i] = 0;
            }
        }
    }

    public static double[] reluDeepVector(double[] a) {
        double[] b = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i] > 0 ? a[i] : 0;
        }
        return b;
    }

    public static void reluPrimeShallowVector(double[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i] > 0 ? 1 : 0;
        }
    }

    /**
     * Returns the "cost" of a particular guess vector. This cost is expressed as the square of the differences between the desired value and observed value.
     * A perfect guess will have a cost of 0.
     * This value is used as a diagnostic, but is COMPUTATIONALLY EXPENSIVE IN MASS QUANTITIES, so is only done in debug mode.
     *
     * @param prediction - output vector of a data point being passed through NN
     * @param realAns    - the actual label of what the value should be
     * @return - decimal value expressing cost, lower is better.
     */
    public static double cost(DoubleMatrix prediction, int realAns) {
        double ret = 0;
        for (int i = 0; i < prediction.length; i++) {
            double val = prediction.get(i) - (i == realAns ? 1 : 0);
            ret += val * val; //Math.pow(val,2) is more expensive since it has to deal with general case, and we dont.
        }
        return ret / 2;
    }
}

