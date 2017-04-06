package TwitterSentimentAnalysisFinal;

import com.mongodb.DBCollection;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matthewplummer on 03/04/2017.
 */
public class TwitterSearch {
    static Object lock5 = new Object();
    static Object lock6 = new Object();
    static List<Object> overallSentimentAndWordsFound = new ArrayList<>();
    static boolean analysisAndInsertCompleted = false;
    static boolean searchCompleted = false;
    static String tweetText;
    static Date tweetDate;


    public static boolean searchTwitter(DBCollection twitterColl, final List<String> sentimentWordDocumentList, final List<String> sentimentPolarityDocumentList, String wordsToSearch) {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(wordsToSearch);
        //query.setSince("2017-04-03");
        query.setUntil("2017-04-04"); //--USE THIS FOR SPECIFIC DATE TO RETRIEVE
        query.setLang("en");
        QueryResult result;
        int Count=0;
        int requests = 0;
        try {
            do {
                result = twitter.search(query);
                requests ++;
                System.out.println("NUMBER OF REQUESTS = " + requests);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {

                    tweetText = tweet.getText();
                    tweetDate = tweet.getCreatedAt();

                    synchronized (lock5) {
                        overallSentimentAndWordsFound = SentimentAnalysis.matchSentiment(sentimentPolarityDocumentList, sentimentWordDocumentList, tweetText);
                    }

                    synchronized (lock6)
                    {
                        analysisAndInsertCompleted = TweetAnalysisAndInsert.analysisAndInsert(overallSentimentAndWordsFound, tweetDate, tweetText, twitterColl);
                    }
                    System.out.println("NUMBER OF TWEETS RETRIEVED = " + Count);
                    Count++;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //This keeps the code iterating as long as there is another Tweet
            //the request clause is needed to ensure that the request limit is not exceeded causing the code to be locked out.
            while ((query = result.nextQuery()) != null && requests < 41);
            System.out.println("NUMBER OF TWEETS = " + Count);
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        searchCompleted = true;
        return searchCompleted;
    }
}
