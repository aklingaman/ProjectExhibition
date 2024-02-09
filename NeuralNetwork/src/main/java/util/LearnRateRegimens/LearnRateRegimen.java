package main.java.util.LearnRateRegimens;

import main.java.NeuralNet;

/**
 * Given a neural net, the regimen decides what the best learn rate is for it.
 * There are a couple of variants i am looking to implement. ( aside from a static learn rate which is used to just make the existing system compile )
 * 1. reduce learn rate on plateau ( like keras )
 * 2. reduce learn rate on a deterministic curve.
 */
public interface LearnRateRegimen {

    public double nextLearnRate(NeuralNet net);

}
