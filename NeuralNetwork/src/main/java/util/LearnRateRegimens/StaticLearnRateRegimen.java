package main.java.util.LearnRateRegimens;

import main.java.NeuralNet;

import java.io.Serializable;

public class StaticLearnRateRegimen implements LearnRateRegimen, Serializable {

    private double learnRate;

    public StaticLearnRateRegimen(double learnRate) {
        this.learnRate = learnRate;
    }

    @Override
    public double nextLearnRate(NeuralNet net) {
        return learnRate;
    }
}
