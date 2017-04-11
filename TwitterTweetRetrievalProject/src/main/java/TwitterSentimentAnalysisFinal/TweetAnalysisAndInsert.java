package TwitterSentimentAnalysisFinal;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Matthew Plummer.
 * This class will determine the polarity of the tweet based upon the value produced for the overall sentiment.
 * The simplified date is calculated and stored in this class.
 * At this point all analysed data with regards to each tweet is inserted into the specified collection.
 */
public class TweetAnalysisAndInsert {
    static String sentimentWordsFound, formattedTweetDate, simplifiedTweetDate;
    static int numberOfMatches;
    static int overallSentimentValue;
    static String tweetPolarity;
    static boolean analysisAndInsertCompleted = false;

    public static boolean analysisAndInsert(List<Object> overallSentimentAndWordsFound, Date tweetDate, String tweetText, DBCollection twitterColl) {
        try {
        sentimentWordsFound = (String) overallSentimentAndWordsFound.get(0);
        overallSentimentValue = (int) overallSentimentAndWordsFound.get(1);
        numberOfMatches = (int) overallSentimentAndWordsFound.get(2);

        if (overallSentimentValue > 0 && numberOfMatches > 0) {
            tweetPolarity = "Positive";
        } else if (overallSentimentValue < 0 && numberOfMatches > 0) {
            tweetPolarity = "Negative";
        }
        //If there are matches but over all is 0 i.e. 1 positive = +1 and 1 negative = -1 overall 0 but not neutral. =balanced
        else if (numberOfMatches > 0 && overallSentimentValue == 0) {
            tweetPolarity = "Balanced";
        } else if (numberOfMatches == 0) {
            tweetPolarity = "Neutral";
        }

        /*Calendar cal = Calendar.getInstance();
          cal.setTime(tweetDate);
          cal.add(Calendar.DATE, -1);
          Date dateMinus2 = cal.getTime();*/

        formattedTweetDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(tweetDate);//format the date from 'day  MMM DD hh:mm:ss GMT yyyy'
        simplifiedTweetDate = new SimpleDateFormat("dd-MM-yyyy").format(tweetDate);

        BasicDBObjectBuilder documentBuilderDetail = BasicDBObjectBuilder.start()
                .add("simplifiedDate", simplifiedTweetDate)
                .add("tweetDate", formattedTweetDate)
                .add("tweetText", tweetText)
                .add("sentimentFound", sentimentWordsFound)
                .add("overallSentiment", overallSentimentValue)
                .add("tweetPolarity", tweetPolarity);

            twitterColl.insert(documentBuilderDetail.get());

            analysisAndInsertCompleted = true;
        }
        catch (Exception e)
        {
            System.out.println("ERROR: Analysis and/ or insertion of tweet not completed correctly.");
        }

        return analysisAndInsertCompleted;
    }
}
