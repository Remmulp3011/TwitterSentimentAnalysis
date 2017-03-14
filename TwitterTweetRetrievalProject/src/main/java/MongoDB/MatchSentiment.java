/*
package MongoDB;

import com.mongodb.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by matthewplummer on 10/03/2017.
 *//*

public class MatchSentiment {

    public static void main(String[] args) {
        List<String> twitterDocumentList = new ArrayList<>();
        List<String> sentimentWordDocumentList = new ArrayList<>();
        List<String> sentimentPolarityDocumentList = new ArrayList<>();
        int overallSentimentValue;
        String wordPolarity;
        Boolean matchFound;
        String twitterText;
        String tweetId; //See notes to find out why this is not used. Being left in for further implementation.
        String sentimentWord;
        int sentimentCounterPositive = 0;
        int sentimentCounterNegative = 0;
        int totalDocuments = 0;
        int sentimentCounterNeutral = 0;

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
                totalDocuments += 1;
                BasicDBObject currentDocument = (BasicDBObject) twitterDataCursor.next();
                twitterDocumentList.add(currentDocument.getString("_id"));
                twitterDocumentList.add(currentDocument.getString("tweetText"));
                twitterDocumentList.add(currentDocument.getString("tweetDate"));
                twitterDocumentList.add(currentDocument.getString("sentimentFound"));
                twitterDocumentList.add(currentDocument.getString("overallSentiment"));

                overallSentimentValue = 0;//reset the sentiment value
                tweetId = twitterDocumentList.get(0);
                twitterText = twitterDocumentList.get(1);

                for (int sentimentAndPolarityIndex = 0; sentimentAndPolarityIndex < sentimentWordDocumentList.size(); sentimentAndPolarityIndex++) {
                    sentimentWord = sentimentWordDocumentList.get(sentimentAndPolarityIndex);
                    matchFound = twitterText.contains(sentimentWord);

                    if (matchFound == true) {

                        wordPolarity = sentimentPolarityDocumentList.get(sentimentAndPolarityIndex);

                        */
/*
                        Wont work due to the face that with each iteration when using $set it will be replaced.
                        Therefore the value will not be added so a record will not be made it will be replaced each time.
                        If $set is not used a new document will be created with the specified contents copied and the change made to the copy for the field.
                        *//*


*/
/*                        BasicDBObject newTwitterDataDocument = new BasicDBObject();

                        //Key to change and value to change to. Use $set to only change specified key value
                        newTwitterDataDocument.append("$set", new BasicDBObject().append("sentimentFound", sentimentWordDocumentList.get(sentimentAndPolarityIndex) + ","));

                        tweetId = twitterDocumentList.get(0);

                        //Find the tweet that needs to be changed, see explanation below as to why tweetText is used.
                        BasicDBObject searchQuery = new BasicDBObject().append("tweetText", twitterText);
                        //Find the id and change the value to stated
                        twitterColl.insert(searchQuery,newTwitterDataDocument);*//*


                        if (wordPolarity.equals("positive")) {
                            overallSentimentValue += 1;
                        } else {
                            overallSentimentValue -= 1;
                        }
                    }
                }
                if(overallSentimentValue > 0)
                {
                    sentimentCounterPositive += 1;
                }
                else if (overallSentimentValue < 0)
                {
                    sentimentCounterNegative += 1;
                }
                else
                {
                    sentimentCounterNeutral += 1;
                }

                BasicDBObject newTwitterDataDocument = new BasicDBObject();

                //Key to change and value to change to. Use $set to only change specified key value
                newTwitterDataDocument.append("$set", new BasicDBObject().append("overallSentiment", overallSentimentValue));
                //Construct the string that needs to be searched in order to find the correct id
                */
/*
                Having to do twitter text due to the face that quotations are added to the parameter.
                e.g.for searching for ID you need: ObjectID("+tweetId+")
                "ObjectID(\""+tweetId+"\")" when declared will become "ObjectID(\"+tweetId+\")" due to the addition quotations.
                Therefore as it is not in the correct for with the addition quotes it will not be found.
                Solution could be to change code to create my own ID system so a random object ID is not created.
                For now the twitterText field will be used as it is unique for each tweet.
                In the rare occasion someone will copy and paste a Tweet both will be the same and both will be updated each time they are met.
                *//*

                //Find the tweet that needs to be changed
                BasicDBObject searchQuery = new BasicDBObject().append("tweetText", twitterText);
                //Find the id and change the value to that stated
                twitterColl.update(searchQuery, newTwitterDataDocument);

                twitterDocumentList.clear();//clear the array list for the Twitter data for next iteration
            }
            System.out.println("Total number of tweets = " + totalDocuments);
            System.out.println("Number of tweets positive = " + sentimentCounterPositive);
            System.out.println("Number of tweets negative = " + sentimentCounterNegative);
            System.out.println("Number of tweets neutral = " + sentimentCounterNeutral);
            System.out.println("Percentage of tweets positive = " + ((sentimentCounterPositive/totalDocuments) * 100) + "%");
            System.out.println("Percentage of tweets negative = " + ((sentimentCounterNegative/totalDocuments) * 100) + "%");
            System.out.println("Percentage of tweets neutral = " + ((sentimentCounterNeutral/totalDocuments) * 100) + "%");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}*/
