package main.java.util.activations;

import org.jblas.DoubleMatrix;

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
    interface SerializableConsumer extends Consumer<DoubleMatrix>, Serializable {}
    interface SerializableFunction extends Function<DoubleMatrix,DoubleMatrix>, Serializable {}


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


    private void activateShallow(DoubleMatrix input) {
        for(int i =0; i<input.length; i++) {
            input.put(i,Math.max(0,input.get(i)));
        }
    }


    private void activatePrimeShallow(DoubleMatrix input) {
        for(int i =0; i<input.length; i++) {
            input.put(i,input.get(i)<=0?0:1);
        }
    }


    private DoubleMatrix activateDeep(DoubleMatrix input) {
        DoubleMatrix output = new DoubleMatrix(input.rows,input.columns);
        for(int i = 0; i<input.length; i++) {
            output.put(i, Math.max(0,input.get(i)));
        }
        return output;
    }


    private DoubleMatrix activatePrimeDeep(DoubleMatrix input) {
        DoubleMatrix output = new DoubleMatrix(input.rows,input.columns);
        for(int i = 0; i<input.length; i++) {
            output.put(i, input.get(i)<=0?0:1);
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
