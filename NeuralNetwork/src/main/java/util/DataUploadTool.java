package main.java.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import main.java.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Used to provide mongo DB with the raw image data.
 *
 */
public class DataUploadTool {
    public static Logger LOG = LogManager.getLogger();


    public static void main(String[] args) {
        ServerApi serverAPI = ServerApi.builder().version(ServerApiVersion.V1).build();
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(args[0]))
                .serverApi(serverAPI)
                .codecRegistry(codecRegistry)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        if(mongoClient.listDatabaseNames().first()==null) {
            LOG.error("Could not connect to DB");
        } else {

        }

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/mnist_train.csv"));
            String line;
            int imageIDNo = 1;
            MongoDatabase db = mongoClient.getDatabase("Neural_Network_DB");
            MongoCollection<Image> images = db.getCollection("Image",Image.class);
            List<Image> imageList = new ArrayList<>();
            while((line = br.readLine())!=null) {
                String[] vals = line.split(",");
                double[] pixels = new double[784];
                for(int i = 0; i<784; i++ ) {
                    pixels[i] = 1.0*Integer.parseInt(vals[i+1])/255;
                }
                Image i = new Image(Integer.parseInt(vals[0]),pixels,"train",imageIDNo++);
                imageList.add(i);

            }
            images.insertMany(imageList);
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }








    }






}
