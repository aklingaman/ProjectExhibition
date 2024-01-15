package main.java.util;//Contains a bunch of static linear algebra functions, primarily split on a shallow copy and a deep copy.
//Most of these functions get run millions of times, so no sanity checks. 
//Shallow functions will perform onto the first vector passed, deep allocates new memory and returns it.
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
public abstract class LinAlg {
    public static Logger LOG = LogManager.getLogger();
    public static void hardamadShallow(double[] a, double[] b) {
        for(int i = 0; i<a.length; i++) {
            a[i]*=b[i];
        }	
    }

    public static double[] hardamadDeep(double[] a, double[] b) {
        double[] c = new double[a.length];
        for(int i = 0; i<a.length; i++) {
            c[i] = a[i]*b[i];
        }	
        return c;		
    }

    public static void vectorAdditionShallow(double[] a, double[] b) {
        for(int i = 0; i<a.length; i++) {
            a[i]+=b[i];
        }	
    }

    public static double[] vectorAdditionDeep(double[] a, double[] b) {
        double[] c = new double[a.length];
        for(int i = 0; i<a.length; i++) {
            c[i] = a[i]+b[i];
        }	
        return c;		
    }

    //returns index of maximal element passed. Ties broken by lowest index.
    public static int vote(double[] a) {
        int max = 0;
        for(int i = 1; i<a.length; i++) {
            if(a[i]>a[max]) { max = i; }
        }		
        return max;
    }	

    public static double[] matrixVectorMult(double[][] matrix, double[] vector) {
        double[] ans = new double[matrix.length]; 
        for(int i = 0; i<matrix.length; i++) { 
            ans[i] = 0;
            for(int j = 0; j<matrix[i].length; j++) { 
                ans[i] += matrix[i][j]*vector[j];
            }
        }
        return ans;
    }

    //Performs matrix vector mult but on the matrix' transpose. More efficient than actually calculating the transpose	
    public static double[] matrixVectorMultTranspose(double[][] matrix, double[] vector) {
        double[] ans = new double[matrix[0].length]; 
        for(int i = 0; i<matrix[0].length; i++) { 
            ans[i] = 0; 
            for(int j = 0; j<matrix.length; j++) { 
                double a = matrix[j][i];
                double b = vector[j]; 
                ans[i]+=matrix[j][i]*vector[j];
            }
        }
        return ans;	
    }


    public static double[][] computeTranspose(double[][] matrix) {
        double[][] transposeMatrix = new double[matrix[0].length][matrix.length];
        for(int i = 0; i<transposeMatrix.length; i++) {
            for(int j = 0; j<transposeMatrix[0].length; j++) {
                transposeMatrix[i][j] = matrix[j][i];
            }
        }
        return transposeMatrix; 
    }



    public static void reluShallowVector(double[] a) {
        for(int i = 0; i<a.length; i++) {
            if(a[i]<0) {
                a[i]=0;
            }
        }
    }

    public static double[] reluDeepVector(double[] a) {
        double[] b = new double[a.length];
        for(int i = 0; i<a.length; i++) {
            b[i] = a[i]>0?a[i]:0;
        }
        return b;
    }

    public static void reluPrimeShallowVector(double[] a) {
        for(int i = 0; i<a.length; i++) {
            a[i]=a[i]>0?1:0;
        }
    }

}

