package main.java.util.activations;


import org.jblas.DoubleMatrix;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ActivationFunctionProvider {

    public Consumer<DoubleMatrix> shallow();
    public Consumer<DoubleMatrix> shallowPrime();

    public Function<DoubleMatrix, DoubleMatrix> deep();
    public Function<DoubleMatrix, DoubleMatrix> deepPrime();



}
