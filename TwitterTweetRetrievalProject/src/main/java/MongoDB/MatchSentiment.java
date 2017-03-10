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
        List<String> twitterDocumentList = new ArrayList<>();
        List<String> sentimentWordDocumentList = new ArrayList<>();
        List<String> sentimentPolarityDocumentList = new ArrayList<>();
        int overallSentimentValue;
        String wordPolarity;
        Boolean matchFound;
        String twitterText;
        String tweetId;
        String sentimentWord;

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

            //Filter for field
            BasicDBObject wordField = new BasicDBObject();
            wordField.put("word", 1);
            DBCursor sentimentWordDataCursor = sentimentColl.find(allQuery,wordField);
            while( sentimentWordDataCursor.hasNext() ) {
                BasicDBObject currentDocument =  (BasicDBObject) sentimentWordDataCursor.next();
                sentimentWordDocumentList.add(currentDocument.getString("word")); //do this to only get the word
            }
            for(int i = 0; i < sentimentWordDocumentList.size(); i++) {
                System.out.println(sentimentWordDocumentList.get(i));
            }

            //Filter for field
            BasicDBObject polarityField = new BasicDBObject();
            polarityField.put("polarity", 1);
            DBCursor sentimentPolarityDataCursor = sentimentColl.find(allQuery,polarityField);
            while( sentimentPolarityDataCursor.hasNext() ) {
                BasicDBObject currentDocument = (BasicDBObject) sentimentPolarityDataCursor.next();
                sentimentPolarityDocumentList.add(currentDocument.getString("polarity")); //do this to only get the polarity
            }
            for(int i = 0; i < sentimentPolarityDocumentList.size(); i++) {
                System.out.println(sentimentPolarityDocumentList.get(i));
            }

            DBCursor twitterDataCursor = twitterColl.find();
            while( twitterDataCursor.hasNext() ) {
                BasicDBObject currentDocument = (BasicDBObject) twitterDataCursor.next();
                twitterDocumentList.add(currentDocument.getString("_id"));
                twitterDocumentList.add(currentDocument.getString("tweetText"));
                twitterDocumentList.add(currentDocument.getString("tweetDate"));
                twitterDocumentList.add(currentDocument.getString("sentimentFound"));
                twitterDocumentList.add(currentDocument.getString("overallSentiment"));
                //At end before next iteration be sure to clear the list.
                overallSentimentValue = 0;
                tweetId = twitterDocumentList.get(0);

                for(int sentimentAndPolarityIndex = 0; sentimentAndPolarityIndex < sentimentWordDocumentList.size(); sentimentAndPolarityIndex++)
                {
                    twitterText = twitterDocumentList.get(1);
                    sentimentWord = sentimentWordDocumentList.get(sentimentAndPolarityIndex);
                    matchFound = twitterText.contains(sentimentWord);
                    if(matchFound == true)
                    {
                        System.out.println("MATCH FOUND!!");
                        wordPolarity = sentimentPolarityDocumentList.get(sentimentAndPolarityIndex);

/*                        BasicDBObject newTwitterDataDocument = new BasicDBObject();

                        //Key to change and value to change to. Use $set to only change specified key value
                        newTwitterDataDocument.append("$set", new BasicDBObject().append("sentimentFound", sentimentWordDocumentList.get(sentimentAndPolarityIndex) + ","));

                        tweetId = twitterDocumentList.get(0);
                        //Find the id that needs to be changed
                        BasicDBObject searchQuery = new BasicDBObject().append("_id", tweetId);
                        //Find the id and change the value to stated
                        twitterColl.insert(searchQuery,newTwitterDataDocument);*/

                        if(wordPolarity.equals("positive"))
                        {
                            overallSentimentValue += 1;
                        }
                        else
                        {
                            overallSentimentValue -= 1;
                        }
                    }
                }
                BasicDBObject newTwitterDataDocument = new BasicDBObject();

                //Key to change and value to change to. Use $set to only change specified key value
                newTwitterDataDocument.append("$set", new BasicDBObject().append("overallSentiment", overallSentimentValue));
                //Find the id that needs to be changed
                BasicDBObject searchQuery = new BasicDBObject().append("_id", tweetId);
                //Find the id and change the value to stated
                twitterColl.update(searchQuery,newTwitterDataDocument);

                twitterDocumentList.clear();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
