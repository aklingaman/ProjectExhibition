package main.java.util.activations;

import org.jblas.DoubleMatrix;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

public class LeakyReluActivationFunctionProvider implements ActivationFunctionProvider, Serializable {
    private static LeakyReluActivationFunctionProvider instance;

    private SerializableConsumer shallow;
    private SerializableConsumer primeShallow;
    private SerializableFunction deep;
    private SerializableFunction primeDeep;

    //Java requires that all anonymous classes/lambdas implement serializable, which consumer and function do not.
    //Its helpful to just pretend this doesnt exist and that the consumers and functions are just vanilla.
    //if serializable is ever removed in favor of some other way to store the data
    //( for instance if a visualization tool is implemented ), clean this all up.
    interface SerializableConsumer extends Consumer<DoubleMatrix>, Serializable {}
    interface SerializableFunction extends Function<DoubleMatrix,DoubleMatrix>, Serializable {}


    public static LeakyReluActivationFunctionProvider getInstance() {
        if(instance==null) {
            instance = new LeakyReluActivationFunctionProvider();
        }
        return instance;
    }

    private LeakyReluActivationFunctionProvider() {

        shallow =  doubles -> activateShallow(doubles);
        primeShallow = doubles -> activatePrimeShallow(doubles);
        deep = doubles -> activateDeep(doubles);
        primeDeep = doubles -> activatePrimeDeep(doubles);
    }


    private void activateShallow(DoubleMatrix input) {

        for(int i = 0; i<input.length; i++) {
            input.put(i,input.get(i)>0 ? input.get(i) : 0.01*input.get(i));
        }

    }


    private void activatePrimeShallow(DoubleMatrix input) {
        for(int i = 0; i<input.length; i++) {
            input.put(i,input.get(i)<=0?0.01:1);
        }
    }


    private DoubleMatrix activateDeep(DoubleMatrix input) {
        DoubleMatrix output = new DoubleMatrix(input.rows,input.columns);
        for(int i = 0; i<input.length; i++) {
            output.put(i,input.get(i)>0 ? input.get(i) : 0.01*input.get(i));
        }
        return output;
    }


    private DoubleMatrix activatePrimeDeep(DoubleMatrix input) {
        DoubleMatrix output = new DoubleMatrix(input.rows,input.columns);

        for(int i = 0; i<input.length; i++) {
            output.put(i, input.get(i)<=0?0.01:1);
        }
        return output;
    }

    @Override
    public Consumer<DoubleMatrix> shallow() {
        return shallow;
    }

    @Override
    public Consumer<DoubleMatrix> shallowPrime() {
        return primeShallow;
    }

    @Override
    public Function<DoubleMatrix, DoubleMatrix> deep() {
        return deep;
    }

    @Override
    public Function<DoubleMatrix, DoubleMatrix> deepPrime() {
        return primeDeep;
    }
}
