package MongoDB;

import com.mongodb.*;


import javax.swing.text.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthewplummer on 10/03/2017.
 */
public class MatchSentiment {


    public static void main(String[] args) {
        List<DBObject> twitterDocumentList = new ArrayList<>();
        List<DBObject> sentimentWordDocumentList = new ArrayList<>();
        List<DBObject> sentimentPolarityDocumentList = new ArrayList<>();
        //Connect to Database
        try {
            // To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
            // if it's a member of a replica set:
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("TwitterAnalysis");
            DBCollection twitterColl = db.getCollection("twitter_data"); //define twitter collection
            DBCollection sentimentColl = db.getCollection("sentiment"); //define sentiment collection

            //To be used to filter down to specific fields
            BasicDBObject allQuery = new BasicDBObject();
            BasicDBObject fields = new BasicDBObject();

            fields.put("word", 1);

            DBCursor sentimentWordDataCursor = sentimentColl.find(allQuery,fields);
            while( sentimentWordDataCursor.hasNext() ) {
                DBObject currentDocument = sentimentWordDataCursor.next();
                sentimentWordDocumentList.add(currentDocument);
            }
            for(int i = 0; i < sentimentWordDocumentList.size(); i++) {
                System.out.println(sentimentWordDocumentList.get(i));
            }

            fields.put("polarity", 1);

            DBCursor sentimentPolarityDataCursor = sentimentColl.find(allQuery,fields);
            while( sentimentPolarityDataCursor.hasNext() ) {
                DBObject currentDocument = sentimentPolarityDataCursor.next();
                sentimentPolarityDocumentList.add(currentDocument);
            }
            for(int i = 0; i < sentimentPolarityDocumentList.size(); i++) {
                System.out.println(sentimentPolarityDocumentList.get(i));
            }

            DBCursor twitterDataCursor = twitterColl.find();
            while( twitterDataCursor.hasNext() ) {
                DBObject currentDocument = twitterDataCursor.next();
                twitterDocumentList.add(currentDocument);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
