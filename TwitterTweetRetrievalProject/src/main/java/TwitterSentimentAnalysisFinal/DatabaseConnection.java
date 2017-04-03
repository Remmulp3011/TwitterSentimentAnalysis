package TwitterSentimentAnalysisFinal;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.List;

/**
 * Created by matthewplummer on 19/03/2017.
 */
public class DatabaseConnection {

    public static void connection(String collectionToInsert, int numberOfTweets, String wordToSearch) {
        Object lock1 = new Object();
        Object lock2 = new Object();
        List<String> sentimentWordDocumentList;
        List<String> sentimentPolarityDocumentList;

        MongoClientURI uri = new MongoClientURI(
                "mongodb://mplummer:matthew17@cluster0-shard-00-00-0bpyo.mongodb.net:27017,cluster0-shard-00-01-0bpyo.mongodb.net:27017,cluster0-shard-00-02-0bpyo.mongodb.net:27017/TwitterAnalysis?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");

        MongoClient mongoClient = new MongoClient(uri);
        DB db = mongoClient.getDB("TwitterAnalysis");
        DBCollection twitterColl = db.getCollection(collectionToInsert);
        DBCollection sentimentColl = db.getCollection("sentiment");

        synchronized (lock1) {
            sentimentWordDocumentList = GetSentimentWords.getWords(sentimentColl);
        }
        synchronized (lock2)
        {
            sentimentPolarityDocumentList = GetSentimentPolarity.getPolarity(sentimentColl);
        }
        TwitterStream.tweetStream(twitterColl,sentimentWordDocumentList,sentimentPolarityDocumentList, numberOfTweets, wordToSearch);
    }
}
