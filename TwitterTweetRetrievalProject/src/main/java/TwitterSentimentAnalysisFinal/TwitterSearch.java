package TwitterSentimentAnalysisFinal;

import twitter4j.*;

import java.io.*;
import java.util.List;

/**
 * Created by matthewplummer on 03/04/2017.
 */
public class TwitterSearch {
    public static void main(String[] args) {
        Twitter twitter = new TwitterFactory().getInstance();
        final File file = new File("TestSearchForTrump.txt");
        Query query = new Query("Trump");
        //query.setSince("2017-04-01");
        //query.setUntil("2017-04-03");
        query.setLang("en");
        QueryResult result;
        int Count=0;
        int requests = 0;
        try (FileWriter fileWrite = new FileWriter(file, true);
             BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
             PrintWriter out = new PrintWriter(bufferWrite)) {
            do {
                result = twitter.search(query);
                requests ++;
                System.out.println("NUMBER OF REQUESTS = " + requests);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getCreatedAt() + "-" + tweet.getText() + "\n");
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getCreatedAt() + "-" + tweet.getText() + "\n");
                    Count++;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while ((query = result.nextQuery()) != null);
            System.out.println("NUMBER OF TWEETS = " + Count);
        }
        catch (IOException i)
        {
            i.printStackTrace();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
