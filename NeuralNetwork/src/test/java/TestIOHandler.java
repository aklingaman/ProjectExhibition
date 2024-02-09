package test.java;

import main.java.frontFacing.IOHandler;
import main.java.NeuralNet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * This class is mostly for testing external state of the config, read/write of models, read of data sets.
 */
public class TestIOHandler {

    public static String path = System.getProperty("user.dir");
    @Test
    public void testCreateFromConfigFile() {
        String testPath = path+"/config/config.txt";
        String configuration = "mnist2by20";
        NeuralNet model = IOHandler.createFromConfigFile(testPath,configuration);
        Assert.assertTrue(model.layers.length==3); //2 hidden layers + the output layer.
        Assert.assertTrue(model.layers[0].biasVector.length==20);
        Assert.assertTrue(model.layers[2].biasVector.length==10);

    }

    @Test
    public void testWriteAndRead() {
        String testPath = path+"/config/config.txt";
        String configuration = "mnist2by20";
        NeuralNet model = IOHandler.createFromConfigFile(testPath,configuration);
        model.initialize(new Random().nextInt(),3);
        String writeReadPath = path+"/src/test/java/resources/UnitTestModelDontTouchMe";
        System.out.println(writeReadPath);
        IOHandler.writeModelToFile(model,writeReadPath);
        NeuralNet newModel = IOHandler.readModelFromFile(writeReadPath);
        Assert.assertTrue(newModel.isSameShape(model));
        Assert.assertTrue(newModel.layers[0].biasVector[0]==model.layers[0].biasVector[0]);
    }









}
