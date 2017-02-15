/**
 * Created by matthewplummer on 14/02/2017.
 */

package StreamTweets;

import twitter4j.*;

import java.io.IOException;

public abstract class TweetStream implements StatusListener {

    public static void main(String[] args) throws TwitterException, IOException{

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener(){

            public void onStatus(Status status) {
                System.out.println(status.getCreatedAt() + "," + status.getText() + "\n");
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        FilterQuery tweetFilterQuery = new FilterQuery();

        String keywords[] = {"#TRUMP"};
        tweetFilterQuery.track(keywords);
        tweetFilterQuery.language(new String[]{"en"});
        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilterQuery);
    }

}
