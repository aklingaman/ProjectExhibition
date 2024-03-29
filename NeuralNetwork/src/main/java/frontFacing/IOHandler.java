package main.java.frontFacing; /**
 * This class handles IO as well as loading the values from the config for neural net specs.
 */

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import main.java.Image;
import main.java.NeuralNet;
import main.java.util.LearnRateRegimens.StaticLearnRateRegimen;
import main.java.util.activations.ActivationFunctionProvider;
import main.java.util.activations.LeakyReluActivationFunctionProvider;
import main.java.util.activations.ReluActivationFunctionProvider;
import main.java.util.activations.SigmoidActivationFunctionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.*;
import java.io.*;
public class IOHandler {
    public static Logger LOG = LogManager.getLogger();
    private ServerApi serverAPI;
    private MongoClientSettings settings;
    private MongoClient mongoClient;

    public IOHandler(String connectionString) {
        serverAPI = ServerApi.builder().version(ServerApiVersion.V1).build();
        settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverAPI)
                .build();

        mongoClient = MongoClients.create(settings);
        String DB = mongoClient.listDatabaseNames().first();
        LOG.info(DB);
        if(mongoClient.listDatabaseNames().first()==null) {
            LOG.error("Could not connect to DB");
        } else {

        }
    }


    //TODO: probably actually use mongo's k,v pair type that the BSON will store, but we will cross that bridge when we get to it.
    public Map<String,String> getNeuralNetConfigFromDB(String configName) {
        //fetch config with that name from DB.

        //we will NOT save this NN into the DB as things like experiment make temp NN's that dont survive past the run.
        LOG.error("Not implemented");
        return null;
    }

    public NeuralNet getNeuralNet(String name) {
        //fetch json from db, turn into NN, return.
        LOG.error("Not implemented");
        return null;

    }

    public void writeNeuralNetToDB(NeuralNet neuralNet) {
        LOG.error("Not implemented");
    }

    public List<Image> getDataSet(String dataSetName){
        LOG.error("Not implemented");
        return null;
    }





    
}
