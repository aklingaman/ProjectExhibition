package main.java.frontFacing;

import main.java.NeuralNet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class NeuralNetworkFactory {
    public static Logger LOG = LogManager.getLogger();
    private final IOHandler ioHandler;

    public NeuralNetworkFactory(IOHandler ioHandler) {
        this.ioHandler = ioHandler;
    }


    public NeuralNet provideFromConfig(String name, String config){
       Map<String, String> neuralNetConfig = ioHandler.getNeuralNetConfigFromDB(config);
       LOG.error("Not Implemented");
       return null;
    }





}
