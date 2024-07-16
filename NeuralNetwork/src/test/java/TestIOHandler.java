

import main.java.Image;
import main.java.frontFacing.IOHandler;
import main.java.NeuralNet;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
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
        Assert.assertTrue(model.layers.length==2);
        Assert.assertTrue(model.layers[0].biasVector.length==20);
        Assert.assertTrue(model.layers[1].biasVector.length==10);

    }

    @Test
    public void testWriteAndRead() {
        String testPath = path+"/config/config.txt";
        String configuration = "mnist2by20";
        NeuralNet model = IOHandler.createFromConfigFile(testPath,configuration);
        model.initialize(new Random().nextInt(),3);
        String writeReadPath = path+"\\src\\test\\resources\\UnitTestModelDontTouchMe";
        System.out.println(writeReadPath);
        IOHandler.writeModelToFile(model,writeReadPath);
        NeuralNet newModel = IOHandler.readModelFromFile(writeReadPath);
        Assert.assertTrue(newModel.isSameShape(model));
        Assert.assertTrue(newModel.layers[0].biasVector.get(0)==model.layers[0].biasVector.get(0));
    }


    @Test
    public void testImageCreation() {
        System.out.println(System.getProperty("user.dir"));
        String path = System.getProperty("user.dir")+
                File.separator+"src"+
                File.separator+"test"+
                File.separator+"resources"+
                File.separator+"mnist_unitTest.csv";
        List<Image> images = IOHandler.collectImagesIntoDataSet(path);
        Assert.assertTrue(images.size()!=0);
        Assert.assertTrue(images.get(0).getVectorizedImage()!=null);
    }









}
