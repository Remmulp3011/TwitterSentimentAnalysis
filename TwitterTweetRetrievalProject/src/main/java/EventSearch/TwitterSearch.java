package EventSearch;

import twitter4j.*;

import java.io.*;
import java.util.List;

/**
 * Created by matthewplummer on 27/03/2017.
 */

public class TwitterSearch {
    public static int maxIteration = 2;
    public static int currentLoop = 0;
    public static long maxId = 0;

    public static void main(String[] args) {
        Twitter twitter = new TwitterFactory().getInstance();
        final File file = new File("TwitterSearchForTrump.txt");
        while(maxIteration != currentLoop) {
            try (FileWriter fileWrite = new FileWriter(file, true);
                 BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
                 PrintWriter out = new PrintWriter(bufferWrite)) {
                Query query = new Query("#TRUMP AND exclude:retweets");
                query.setLang("en");
                //query.setSince("2017-03-20");
                //query.setUntil("2017-03-21");
                if(maxId != 0) {
                    query.setSinceId(maxId);
                }

                QueryResult result;
                do {
                    result = twitter.search(query);
                    List<Status> tweets = result.getTweets();
                    for (Status tweet : tweets) {
                        out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getCreatedAt() + "-" + tweet.getText() + "\n");
                        System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getCreatedAt() + "-" + tweet.getText() + "\n");
                    }
                } while ((query = result.nextQuery()) != null);

                //final QueryResult nextResult = twitter.search(query);
                maxId = result.getMaxId();
                currentLoop ++;

            } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to search tweets: " + te.getMessage());
                System.exit(-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

}
