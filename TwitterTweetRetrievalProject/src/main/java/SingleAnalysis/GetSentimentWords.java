package SingleAnalysis;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthewplummer on 19/03/2017.
 */
public class GetSentimentWords {

    public static List<String> sentimentWordDocumentList = new ArrayList<>();

    public static List<String> getWords(DBCollection sentimentColl){

        //To be used to filter down to specific fields
        BasicDBObject allQuery = new BasicDBObject();
        BasicDBObject searchField = new BasicDBObject();

        //Filter for field
        searchField.clear();//ensure it is empty
        searchField.put("word", 1);
        DBCursor sentimentWordDataCursor = sentimentColl.find(allQuery, searchField);
        //loading in sentiment table
        while (sentimentWordDataCursor.hasNext()) {
            BasicDBObject currentDocument = (BasicDBObject) sentimentWordDataCursor.next();
            sentimentWordDocumentList.add(currentDocument.getString("word")); //do this to only get the word
        }

        return sentimentWordDocumentList;
    }
}
