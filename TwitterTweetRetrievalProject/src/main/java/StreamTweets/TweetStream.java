/**
 * Created by matthewplummer on 14/02/2017.
 */

package StreamTweets;

import twitter4j.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class TweetStream implements StatusListener {

    public static String tweetText, formattedTweetDate;
    public static Date tweetDate;

    public static void main(String[] args) throws TwitterException, IOException{

        //Specify file, if not exist create
        final File file = new File("TwitterStreamForTrump.txt");
        String searchWords[] = {"#Trump"};

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener(){

            public void onStatus(Status status) {
                //Check if it is a retweet, if so skip it
                if(status.isRetweet() == false) {
                    try (FileWriter fileWrite = new FileWriter(file, true);
                         BufferedWriter bufferWrite = new BufferedWriter(fileWrite);

                         //build the text to append
                         PrintWriter writeDetails = new PrintWriter(bufferWrite)) {
                            //Get information stated, this case is the date and text of tweet
                            tweetDate = status.getCreatedAt();
                            tweetText = status.getText();
                            formattedTweetDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(tweetDate);//format the date from 'day  MMM DD hh:mm:ss GMT yyyy'
                            System.out.println(formattedTweetDate + "\n"); //used for debugging only
                            List<String> lines = Arrays.asList(formattedTweetDate + "SPLIT HERE" + tweetText + "SPLIT HERE" + "\n"); //Build array to populate to txt file
                            writeDetails.println(lines);//Write details to txt file
                    }
                    catch (IOException e) {
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
        tweetFilterQuery.track(searchWords);//filter on words specified
        tweetFilterQuery.language(new String[]{"en"});//show only english tweets
        //Call listen which will write the results and apply filter
        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilterQuery);
    }

}
