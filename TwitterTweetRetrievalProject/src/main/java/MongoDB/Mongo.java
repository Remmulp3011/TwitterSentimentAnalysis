package MongoDB;

/**
 * Created by matthewplummer on 01/03/2017.
 */
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Mongo {
    public static String tweetDate, tweetText;

    public static void main (String[] args) {
        //Connect to Database
        try {
            // To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
            // if it's a member of a replica set:
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("TwitterAnalysis");
            DBCollection coll = db.getCollection("twitter_data");

            //Read and split the text file for date and text
            try {
                Scanner read = new Scanner(new File("TwitterStreamForTrump.txt"));
                read.useDelimiter("SPLIT HERE");

                while (read.hasNext()) {
                    tweetDate = read.next();
                    tweetText = read.next();
                    tweetDate = tweetDate.replace("]","");
                    tweetDate = tweetDate.replace("[","");

                    BasicDBObjectBuilder documentBuilderDetail = BasicDBObjectBuilder.start()
                    .add("tweetDate", tweetDate)
                    .add("tweetText", tweetText)
                    .add("sentimentFound", "NULL")
                    .add("overallSentiment", "NULL");

                    coll.insert(documentBuilderDetail.get());
                }
                System.out.println(coll);
                read.close();
            }
            catch (IOException e)
            {
                System.out.print(e);
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }

    }

}

