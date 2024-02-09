package main.java.util.activations;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * I cannot recommend the regular relu version because of the dead relu problem, the net tends to infinite a large % of the time,
 * and even when it doesnt, the accuracy is bad because an appreciable % of the network is dead.
 * I recommend leaky relu instead.
 *
 */
public class ReluActivationFunctionProvider implements ActivationFunctionProvider, Serializable {
    private static ReluActivationFunctionProvider instance;

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


    public static ReluActivationFunctionProvider getInstance() {
        if(instance==null) {
            instance = new ReluActivationFunctionProvider();
        }
        return instance;
    }

    private ReluActivationFunctionProvider() {

        shallow =  doubles -> activateShallow(doubles);
        primeShallow = doubles -> activatePrimeShallow(doubles);
        deep = doubles -> activateDeep(doubles);
        primeDeep = doubles -> activatePrimeDeep(doubles);
    }


    private void activateShallow(double[] input) {
        for(int i =0; i<input.length; i++) {
            input[i]=Math.max(0,input[i]);
        }
    }


    private void activatePrimeShallow(double[] input) {
        for(int i =0; i<input.length; i++) {
            input[i]=input[i]<=0?0:1;
        }
    }


    private double[] activateDeep(double[] input) {
        double[] output = new double[input.length];
        for(int i = 0; i<input.length; i++) {
            output[i] = Math.max(0,input[i]);
        }
        return output;
    }


    private double[] activatePrimeDeep(double[] input) {
        double[] output = new double[input.length];
        for(int i = 0; i<input.length; i++) {
            output[i] = input[i]<=0?0:1;
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
