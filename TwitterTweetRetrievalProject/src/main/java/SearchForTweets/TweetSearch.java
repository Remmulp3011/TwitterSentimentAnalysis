package SearchForTweets;

import twitter4j.*;

/**
 * Created by matthewplummer on 14/02/2017.
 */
public class TweetSearch {

    public static void main(String[] args) throws TwitterException {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        Query query = new Query("#TRUMP");
        QueryResult result = twitter.search(query);
        for (Status status : result.getTweets()) {
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
        }
    }
}
