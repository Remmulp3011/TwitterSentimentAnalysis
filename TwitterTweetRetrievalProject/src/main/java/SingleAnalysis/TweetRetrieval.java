package SingleAnalysis;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import twitter4j.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matthewplummer on 19/03/2017.
 */
public class TweetRetrieval {
    public static String tweetText, formattedTweetDate;
    public static Date tweetDate;
    static Object lock3 = new Object();
    static int overallSentimentValue;
    static String tweetPolarity;
    static List<Object> overallSentimentAndWordsFound = new ArrayList<>();
    static String sentimentWordsFound;
    static int numberOfMatches;

    public static void tweetStream(final DBCollection twitterColl, final List<String> sentimentWordDocumentList, final List<String> sentimentPolarityDocumentList, final int numberOfTweets, String wordsToSearch) {
        final int[] currentNumberTweetsRetrieved = {0};
        final int numberTweetsToRetrieve = numberOfTweets;
        String searchWords[] = {wordsToSearch};

        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener() {

            public void onStatus(Status status) {
                //Check if it is a retweet, if so skip it
                if (!status.isRetweet()) {
                    if(currentNumberTweetsRetrieved[0] < numberTweetsToRetrieve) {
                        //Get information stated, this case is the date and text of tweet
                        tweetDate = status.getCreatedAt();
                        tweetText = status.getText();
                        formattedTweetDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(tweetDate);//format the date from 'day  MMM DD hh:mm:ss GMT yyyy'

                        synchronized (lock3)
                        {
                            overallSentimentAndWordsFound = SentimentAnalysis.matchSentiment(sentimentPolarityDocumentList, sentimentWordDocumentList, tweetText);
                        }


                        sentimentWordsFound = (String) overallSentimentAndWordsFound.get(0);
                        overallSentimentValue = (int) overallSentimentAndWordsFound.get(1);
                        numberOfMatches = (int) overallSentimentAndWordsFound.get(2);

                        if (overallSentimentValue > 0 && numberOfMatches > 0) {
                            tweetPolarity = "Positive";
                        } else if (overallSentimentValue < 0 && numberOfMatches > 0) {
                            tweetPolarity = "Negative";
                        }
                        //If there are matches but over all is 0 i.e. 1 positive = +1 and 1 negative = -1 overall 0 but not neutral. =balanced
                        else if(numberOfMatches > 0 && overallSentimentValue == 0)
                        {
                            tweetPolarity = "Balanced";
                        }else if(numberOfMatches == 0)
                        {
                            tweetPolarity = "Neutral";
                        }


                        BasicDBObjectBuilder documentBuilderDetail = BasicDBObjectBuilder.start()
                                .add("tweetDate", formattedTweetDate)
                                .add("tweetText", tweetText)
                                .add("sentimentFound", sentimentWordsFound)
                                .add("overallSentiment", overallSentimentValue)
                                .add("tweetPolarity", tweetPolarity);

                        twitterColl.insert(documentBuilderDetail.get());
                        currentNumberTweetsRetrieved[0] +=1;
                        System.out.println("Number of Tweets retrieved: " + currentNumberTweetsRetrieved[0] + "/" + numberTweetsToRetrieve);
                    }
                }
                if(currentNumberTweetsRetrieved[0] == numberTweetsToRetrieve)
                {
                    System.out.print("Number of specified tweets reached. Analysis complete.");
                    twitterStream.clearListeners();
                    twitterStream.shutdown();
                }
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

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
         //Call listen which will get tweets and apply filter (called once and onStatus is looped)
        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilterQuery);
    }
}
