package SearchForTweets;

import twitter4j.*;
import java.io.*;

/**
 * Created by matthewplummer on 14/02/2017.
 */

public class TweetSearch {

    public static void main(String[] args) throws TwitterException {
        final File file = new File("TwitterSearchForTrump.txt");

        try (FileWriter fileWrite = new FileWriter(file, true);
             BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
             PrintWriter out = new PrintWriter(bufferWrite)) {
            // The factory instance is re-useable and thread safe.
            Twitter twitter = TwitterFactory.getSingleton();
            Query query = new Query("#TRUMP AND lang:en AND exclude:retweets");
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                out.println(status.getCreatedAt() + "," + status.getText());
                System.out.println(status.getCreatedAt() + "," + status.getText() + "\n");
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
