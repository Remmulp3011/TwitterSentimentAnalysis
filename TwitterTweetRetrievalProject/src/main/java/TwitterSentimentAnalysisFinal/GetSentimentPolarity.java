package TwitterSentimentAnalysisFinal;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthewplummer on 19/03/2017.
 */
public class GetSentimentPolarity {

    public static List<String> sentimentPolarityDocumentList = new ArrayList<>();

    public static List<String> getPolarity(DBCollection sentimentColl){

        //To be used to filter down to specific fields
        BasicDBObject allQuery = new BasicDBObject();
        BasicDBObject searchField = new BasicDBObject();

        //Filter for field
        searchField.clear();//remove previous
        searchField.put("polarity", 1);
        DBCursor sentimentPolarityDataCursor = sentimentColl.find(allQuery, searchField);
        //loading in polarity table
        while (sentimentPolarityDataCursor.hasNext()) {
            BasicDBObject currentDocument = (BasicDBObject) sentimentPolarityDataCursor.next();
            sentimentPolarityDocumentList.add(currentDocument.getString("polarity")); //do this to only get the polarity
        }

        return sentimentPolarityDocumentList;
    }
}
