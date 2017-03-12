package MongoDB;

import com.mongodb.*;

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
            BasicDBObject searchField = new BasicDBObject();

            //Filter for field
            searchField.clear();//ensure it is empty
            searchField.put("word", 1);
            DBCursor sentimentWordDataCursor = sentimentColl.find(allQuery, searchField);
            //loading in sentiment table
            while (sentimentWordDataCursor.hasNext()) {
                BasicDBObject currentDocument = (BasicDBObject) sentimentWordDataCursor.next();
                sentimentWordDocumentList.add(currentDocument.getString("word")); //do this to only get the word
            }

            //Filter for field
            searchField.clear();//remove previous
            searchField.put("polarity", 1);
            DBCursor sentimentPolarityDataCursor = sentimentColl.find(allQuery, searchField);
            //loading in polarity table
            while (sentimentPolarityDataCursor.hasNext()) {
                BasicDBObject currentDocument = (BasicDBObject) sentimentPolarityDataCursor.next();
                sentimentPolarityDocumentList.add(currentDocument.getString("polarity")); //do this to only get the polarity
            }

            DBCursor twitterDataCursor = twitterColl.find();
            //loading in Twitter table
            while (twitterDataCursor.hasNext()) {
                BasicDBObject currentDocument = (BasicDBObject) twitterDataCursor.next();
                twitterDocumentList.add(currentDocument.getString("_id"));
                twitterDocumentList.add(currentDocument.getString("tweetText"));
                twitterDocumentList.add(currentDocument.getString("tweetDate"));
                twitterDocumentList.add(currentDocument.getString("sentimentFound"));
                twitterDocumentList.add(currentDocument.getString("overallSentiment"));

                overallSentimentValue = 0;//reset the sentiment value
                tweetId = twitterDocumentList.get(0);

                for (int sentimentAndPolarityIndex = 0; sentimentAndPolarityIndex < sentimentWordDocumentList.size(); sentimentAndPolarityIndex++) {
                    twitterText = twitterDocumentList.get(1);
                    sentimentWord = sentimentWordDocumentList.get(sentimentAndPolarityIndex);
                    matchFound = twitterText.contains(sentimentWord);

                    if (matchFound == true) {

                        System.out.println("MATCH FOUND!!");//used for debugging

                        wordPolarity = sentimentPolarityDocumentList.get(sentimentAndPolarityIndex);
                        /*
                        BasicDBObject newTwitterDataDocument = new BasicDBObject();

                        //Key to change and value to change to. Use $set to only change specified key value
                        newTwitterDataDocument.append("$set", new BasicDBObject().append("sentimentFound", sentimentWordDocumentList.get(sentimentAndPolarityIndex) + ","));

                        tweetId = twitterDocumentList.get(0);
                        //Find the id that needs to be changed
                        BasicDBObject searchQuery = new BasicDBObject().append("_id", tweetId);
                        //Find the id and change the value to stated
                        twitterColl.insert(searchQuery,newTwitterDataDocument);
                        */

                        if (wordPolarity.equals("positive")) {
                            overallSentimentValue += 1;
                        } else {
                            overallSentimentValue -= 1;
                        }
                    }
                }
                BasicDBObject newTwitterDataDocument = new BasicDBObject();

                //Key to change and value to change to. Use $set to only change specified key value
                newTwitterDataDocument.append("$set", new BasicDBObject().append("overallSentiment", overallSentimentValue));
                //Find the id that needs to be changed
                BasicDBObject searchQuery = new BasicDBObject().append("_id", tweetId);
                //Find the id and change the value to that stated
                twitterColl.update(searchQuery, newTwitterDataDocument);

                twitterDocumentList.clear();//clear the array list for the Twitter data for next iteration
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}