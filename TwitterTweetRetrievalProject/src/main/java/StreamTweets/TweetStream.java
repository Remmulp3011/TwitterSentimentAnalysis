/**
 * Created by matthewplummer on 14/02/2017.
 */

package StreamTweets;

import twitter4j.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public abstract class TweetStream implements StatusListener {

    public static void main(String[] args) throws TwitterException, IOException{

        final File file = new File("TwitterStreamForTrump.txt");
        String searchWords[] = {"#Trump"};

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener(){

            public void onStatus(Status status) {
                if(status.isRetweet() == false) {
                    try (FileWriter fileWrite = new FileWriter(file, true);
                         BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
                         PrintWriter out = new PrintWriter(bufferWrite)) {
                        System.out.println(status.getCreatedAt() + "," + status.getText() + "\n");
                        List<String> lines = Arrays.asList(status.getCreatedAt() + "," + status.getText() + "\n");
                        out.println(lines);
                    } catch (IOException e) {
                        System.out.print(e);
                    }
                }
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
        tweetFilterQuery.track(searchWords);
        tweetFilterQuery.language(new String[]{"en"});
        //Call listen which will write the results
        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilterQuery);
    }

}
