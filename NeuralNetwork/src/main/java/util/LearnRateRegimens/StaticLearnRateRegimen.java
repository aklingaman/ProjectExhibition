package main.java.util.LearnRateRegimens;

import main.java.NeuralNet;

public class StaticLearnRateRegimen implements LearnRateRegimen {

    private double learnRate;

    public StaticLearnRateRegimen(double learnRate) {
        this.learnRate = learnRate;
    }

    @Override
    public double nextLearnRate(NeuralNet net) {
        return learnRate;
    }
}
