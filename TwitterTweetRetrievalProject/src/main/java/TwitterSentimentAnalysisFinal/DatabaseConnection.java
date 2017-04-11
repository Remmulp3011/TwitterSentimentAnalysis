package TwitterSentimentAnalysisFinal;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by Matthew Plummer.
 * This class handles the connections to the database and resulting collections.
 * Properties for the parameters to connect to the database and collection and read in from here.
 * Other classes for further functionality is called from here
 * This class is called multiple times from different locations when and operation involving the database is needed.
 */
public class DatabaseConnection {

    public static void connection(String collectionToInsert, int numberOfTweets, String wordToSearch, boolean searchToBeDone, String searchDate) {
        Object lock1 = new Object();
        Object lock2 = new Object();
        List<String> sentimentWordDocumentList;
        List<String> sentimentPolarityDocumentList;
        Properties prop = new Properties();
        InputStream input = null;
        String username = null;
        String password = null;
        String clusterPrefix = null;
        String port = null;
        String databaseName = null;
        String absolutePathOfResourceFile = new File("src/main/resources/mongoDbConnection.properties").getAbsolutePath();

        //Get information needed to connect to the MongoDB Atlas cluster
        try {
            input = new FileInputStream(absolutePathOfResourceFile);
            // load a properties file
            prop.load(input);

            password = prop.getProperty("mongodb.password");
            username = prop.getProperty("mongodb.username");
            clusterPrefix = prop.getProperty("mongodb.clusterPrefix");
            port = prop.getProperty("mongodb.port");
            databaseName = prop.getProperty("mongodb.database");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally
        {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(username != null && password != null && clusterPrefix != null) {
            MongoClientURI uri = new MongoClientURI(
                    "mongodb://" + username + ":" + password + "@" + clusterPrefix + "-00-0bpyo.mongodb.net:" + port + "," + clusterPrefix + "-01-0bpyo.mongodb.net:" + port + "," + clusterPrefix + "-02-0bpyo.mongodb.net:" + port + "/" + databaseName + "?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");

            MongoClient mongoClient = new MongoClient(uri);
            DB db = mongoClient.getDB(databaseName);
            DBCollection twitterColl = db.getCollection(collectionToInsert);
            DBCollection sentimentColl = db.getCollection("sentiment");

            synchronized (lock1) {
                sentimentWordDocumentList = GetSentimentWords.getWords(sentimentColl);
            }
            synchronized (lock2) {
                sentimentPolarityDocumentList = GetSentimentPolarity.getPolarity(sentimentColl);
            }

            //Code here is only run if the tick box on the form is ticked indicate a search is to be carried out.
            if (searchToBeDone == true) {
                TwitterSearch.searchTwitter(twitterColl, sentimentWordDocumentList, sentimentPolarityDocumentList, wordToSearch, searchDate);
            } else {
                TwitterStream.tweetStream(twitterColl, sentimentWordDocumentList, sentimentPolarityDocumentList, numberOfTweets, wordToSearch);
            }
        }
        else{
            System.out.println("Password, username, cluster prefix, port or database name has not been entered.");
        }
    }
}
