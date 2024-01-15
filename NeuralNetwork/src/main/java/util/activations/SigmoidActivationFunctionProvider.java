package main.java.util.activations;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

public class SigmoidActivationFunctionProvider implements ActivationFunctionProvider, Serializable {

        private static SigmoidActivationFunctionProvider instance;

        private SerializableConsumer shallow;
        private SerializableConsumer primeShallow;
        private SerializableFunction deep;
        private SerializableFunction primeDeep;

        //Java requires that all anonymous classes/lambdas implement serializable, which consumer and function do not.
        //Its helpful to just pretend this doesnt exist and that the consumers and functions are just vanilla.
        //if serializable is ever removed in favor of some other way to store the data
        //( for instance if a visualization tool is implemented ), clean this all up.
        interface SerializableConsumer extends Consumer<double[]>, Serializable {}
        interface SerializableFunction extends Function<double[],double[]>, Serializable {}


        public static SigmoidActivationFunctionProvider getInstance() {
            if(instance==null) {
                instance = new SigmoidActivationFunctionProvider();
            }
            return instance;
        }

        private SigmoidActivationFunctionProvider() {

            shallow =  doubles -> activateShallow(doubles);
            primeShallow = doubles -> activatePrimeShallow(doubles);
            deep = doubles -> activateDeep(doubles);
            primeDeep = doubles -> activatePrimeDeep(doubles);
        }


    private void activateShallow(double[] input) {
        for(int i = 0; i<input.length; i++) {
            input[i] = 1.0/(1.0+(Math.pow(Math.E,input[i]*-1.0)));
        }
    }


    private void activatePrimeShallow(double[] input) {
        for(int i = 0; i<input.length; i++) {
            double sigmoid = 1.0/(1.0+(Math.pow(Math.E,input[i]*-1.0)));
            //double sigmoid = a[i]/(1+Math.abs(a[i]));
            input[i] = sigmoid*(1-sigmoid);
        }
    }


    private double[] activateDeep(double[] input) {
        double[] output = new double[input.length];
        for(int i = 0; i<input.length; i++) {
            output[i] = 1.0/(1.0+(Math.pow(Math.E,input[i]*-1.0)));
        }
        return output;
    }


    private double[] activatePrimeDeep(double[] input) {
        double[] output = new double[input.length];
        for(int i = 0; i<input.length; i++) {
            double sigmoid = 1.0/(1.0+(Math.pow(Math.E,input[i]*-1.0)));
            //double sigmoid = a[i]/(1+Math.abs(a[i]));
            output[i] = sigmoid*(1-sigmoid);
        }
        return output;
    }

    @Override
    public Consumer<double[]> shallow() {
        return shallow;
    }

    @Override
    public Consumer<double[]> shallowPrime() {
        return primeShallow;
    }

    @Override
    public Function<double[], double[]> deep() {
        return deep;
    }

    @Override
    public Function<double[], double[]> deepPrime() {
        return primeDeep;
    }
}
