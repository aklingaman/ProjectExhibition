package main.java.util;

import main.java.Image;
import org.jblas.DoubleMatrix;

/**
 * Stores everything needed to know about a particular image that was ran against a neural net.
 *
 *
 *
 */
public class TestRunDataPoint {

    private final Image image;
    private final DoubleMatrix guess;
    private Integer maximalElement;
    private Double cost;

    public TestRunDataPoint(Image image, DoubleMatrix guess) {
        this.image=image;
        this.guess=guess;
    }

    public Image getImage() {
        return image;
    }


    public DoubleMatrix getGuess() {
        return guess;
    }


    public int getMaximalElement() {
        if(maximalElement==null) {
            maximalElement = guess.argmax();
        }
        return maximalElement;
    }

    public boolean wasGuessCorrect() {
        return image.label==getMaximalElement();
    }

    public int getCorrectAnswer() {
        return image.label;
    }
    public double getCost() {
        if(cost==null) {
            cost = LinAlg.cost(getGuess(),getCorrectAnswer());
        }
        return cost;
    }



}
