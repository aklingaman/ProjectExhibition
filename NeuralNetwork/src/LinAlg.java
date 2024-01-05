//Contains a bunch of static linear algebra functions, primarily split on a shallow copy and a deep copy. 
//Most of these functions get run millions of times, so no sanity checks. 
//Shallow functions will perform onto the first vector passed, deep allocates new memory and returns it.
import java.util.*;
public abstract class LinAlg {

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

    //These next functions perform sigmoid function operations on the entire vector.

    public static void sigmoidVectorShallow(double[] a) {
        for(int i = 0; i<a.length; i++) {
            a[i] = 1.0/(1.0+(Math.pow(Math.E,a[i]*-1.0)));
            //a[i] = a[i]/(1+Math.abs(a[i]));
        }
    }
    public static double[] sigmoidVectorDeep(double[] a) {
        double[] b = new double[a.length];
        for(int i = 0; i<a.length; i++) {
            b[i] = 1.0/(1.0+(Math.pow(Math.E,a[i]*-1.0)));	
            //b[i] = a[i]/(1+Math.abs(a[i]));
        }
        return b;
    }
    public static void sigmoidPrimeVectorShallow(double[] a) {
        for(int i = 0; i<a.length; i++) {
            double sigmoid = 1.0/(1.0+(Math.pow(Math.E,a[i]*-1.0)));
            //double sigmoid = a[i]/(1+Math.abs(a[i]));
            a[i] = sigmoid*(1-sigmoid);
        }
    }
    public static double[] sigmoidPrimeVectorDeep(double[] a) {
        double[] b = new double[a.length];
        for(int i = 0; i<a.length; i++) {
            double sigmoid = 1.0/(1.0+(Math.pow(Math.E,a[i]*-1.0)));
            //double sigmoid = a[i]/(1+Math.abs(a[i]));
            b[i] = sigmoid*(1-sigmoid);
        }
        return b;
    }	


    //TODO: Junit.
    //Testing main
    public static void main(String[] args) {
        matrixVectorTransposeMultEqualsTransposeThenMatrixVectorMult();
    }    
    

    public static void matrixVectorTransposeMultEqualsTransposeThenMatrixVectorMult() {
        double[][] test = new double[5][3];
        double[][] test2 = new double[5][3];
        double[] vector = new double[5];
        Random rd = new Random(1);
        Random rd2 = new Random(1);
        for(int i = 0; i<test.length; i++) {
            for(int j = 0; j<test[0].length; j++) {
                test[i][j] = rd.nextDouble();
                test2[i][j] = rd2.nextDouble();
            }
        }
        for(int i = 0; i<vector.length; i++) {
            vector[i] = rd.nextDouble();
        }
        
        double[] ans = matrixVectorMultTranspose(test,vector);



        double[][] test2Transpose = computeTranspose(test2);        
        double[] ans2 = matrixVectorMult(test2Transpose,vector);
        for(int i = 0; i<ans.length; i++){
            System.out.println(Arrays.toString(ans));
        }
        for(int i = 0; i<ans2.length; i++){
            System.out.println(Arrays.toString(ans2));
        }
        
    }

}

