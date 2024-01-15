package main.java.util.activations;


import java.util.function.Consumer;
import java.util.function.Function;

public interface ActivationFunctionProvider {

    public Consumer<double[]> shallow();
    public Consumer<double[]> shallowPrime();

    public Function<double[], double[]> deep();
    public Function<double[], double[]> deepPrime();



}
