package SingleAnalysis;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import twitter4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by matthewplummer on 19/03/2017.
 */
public class TweetRetrieval {
    public static String tweetText, formattedTweetDate;
    public static Date tweetDate;
    public static int currentNumberTweetsRetrieved;

    static Object lock3 = new Object();
    static int overallSentimentValue;
    static String tweetPolarity;
    static List<Object> overallSentimentAndWordsFound = new ArrayList<>();
    static String sentimentWordsFound;

    public static void tweetStream(final DBCollection twitterColl, final List<String> sentimentWordDocumentList, final List<String> sentimentPolarityDocumentList) {
        currentNumberTweetsRetrieved = 0;
        System.out.print("Enter number of Tweets to retrieve (higher the number, longer it will take): ");
        Scanner sc = new Scanner(System.in);
        final int numberTweetsToRetrieve = sc.nextInt();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter word or hash tag to search for, for multiple separate with a comma: ");
        String searchWord = null;
        try {
            searchWord = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String searchWords[] = {searchWord};

        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener() {

            public void onStatus(Status status) {
                //Check if it is a retweet, if so skip it
                if (!status.isRetweet()) {
                    if(currentNumberTweetsRetrieved < numberTweetsToRetrieve) {
                        //Get information stated, this case is the date and text of tweet
                        tweetDate = status.getCreatedAt();
                        tweetText = status.getText();
                        formattedTweetDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(tweetDate);//format the date from 'day  MMM DD hh:mm:ss GMT yyyy'

                        synchronized (lock3)
                        {
                            overallSentimentAndWordsFound = SentimentAnalysis.matchSentiment(sentimentPolarityDocumentList, sentimentWordDocumentList, tweetText);
                        }


                        overallSentimentValue = (int) overallSentimentAndWordsFound.get(1);
                        sentimentWordsFound = (String) overallSentimentAndWordsFound.get(0);

                        if (overallSentimentValue > 0) {
                            tweetPolarity = "Positive";
                        } else if (overallSentimentValue < 0) {
                            tweetPolarity = "Negative";
                        } else {
                            tweetPolarity = "Neutral";
                        }

                        BasicDBObjectBuilder documentBuilderDetail = BasicDBObjectBuilder.start()
                                .add("tweetDate", formattedTweetDate)
                                .add("tweetText", tweetText)
                                .add("sentimentFound", sentimentWordsFound)
                                .add("overallSentiment", overallSentimentValue)
                                .add("tweetPolarity", tweetPolarity);

                        twitterColl.insert(documentBuilderDetail.get());
                        currentNumberTweetsRetrieved+=1;
                        System.out.println("Number of Tweets retrieved: " + currentNumberTweetsRetrieved + "/" + numberTweetsToRetrieve);
                    }
                }
                if(currentNumberTweetsRetrieved == numberTweetsToRetrieve)
                {
                    System.out.print("Number of specified tweets reached. Analysis complete.");
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
