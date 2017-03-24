package SingleAnalysis;

import java.util.*;

/**
 * Created by matthewplummer on 19/03/2017.
 */
public class SentimentAnalysis {
    public static List matchSentiment(List<String> sentimentPolarityDocumentList, List<String> sentimentWordDocumentList, String tweetText)
    {
        int overallSentimentValue = 0;
        String wordPolarity;
        Boolean matchFound;
        String sentimentWord;
        List<Object> overallSentimentAndWordsFound = new ArrayList<>();
        int numberOfMatches = 0;
        String tweetTextCheck;

        for (int sentimentAndPolarityIndex = 0; sentimentAndPolarityIndex < sentimentWordDocumentList.size(); sentimentAndPolarityIndex++) {
                    sentimentWord = sentimentWordDocumentList.get(sentimentAndPolarityIndex);

                    //Convert all to lower case as the word comparision is case sensitive.
                    tweetTextCheck = tweetText.toLowerCase();

                    Set<String> words = new HashSet<>(
                            Arrays.asList(tweetTextCheck.split(" "))
                    );

                    matchFound = words.contains(sentimentWord);

                    if (matchFound == true) {
                        //This is to stop the null pointer for when there is nothing to add in the else. Else is to add previous and current for all words to be present.
                        if(overallSentimentAndWordsFound.size() == 0)
                        {
                            overallSentimentAndWordsFound.add(0,sentimentWord);
                        }
                        else {
                            overallSentimentAndWordsFound.add(0, overallSentimentAndWordsFound.get(0) + "," + sentimentWord);
                        }
                        wordPolarity = sentimentPolarityDocumentList.get(sentimentAndPolarityIndex);
                        numberOfMatches ++;
                        if (wordPolarity.equals("positive")) {
                            overallSentimentValue += 1;
                        } else {
                            overallSentimentValue -= 1;
                        }
                    }
                }
                //This is to avoid the out of range exception where there are no matches found so nothing in index 0
                if(numberOfMatches == 0)
                {
                    overallSentimentAndWordsFound.add(0, "No sentiment present.");
                }
                overallSentimentAndWordsFound.add(1,overallSentimentValue);
                overallSentimentAndWordsFound.add(2,numberOfMatches);

                return overallSentimentAndWordsFound;
    }
}
