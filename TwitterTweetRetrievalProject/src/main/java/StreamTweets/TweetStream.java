/**
 * Created by matthewplummer on 14/02/2017.
 */

package StreamTweets;

import twitter4j.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class TweetStream implements StatusListener {

    public static void main(String[] args) throws TwitterException, IOException{

        final Path file = Paths.get("TwitterStreamForTrump.txt");

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener(){

            public void onStatus(Status status) {
                try {
                    System.out.println(status.getCreatedAt() + "," + status.getText() + "\n");
                    List<String> lines = Arrays.asList(status.getCreatedAt() + "," + status.getText() + "\n");
                    //Creates and writes to file in the working directory i.e. the location of the project.
                    Files.write(file, lines, Charset.forName("UTF-8"));
                }
                catch(IOException e){
                    System.out.print(e);
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
        String keywords[] = {"#Trump"};
        tweetFilterQuery.track(keywords);
        tweetFilterQuery.language(new String[]{"en"});
        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilterQuery);
    }

}
