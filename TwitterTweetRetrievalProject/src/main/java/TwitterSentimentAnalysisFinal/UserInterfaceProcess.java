package TwitterSentimentAnalysisFinal;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by matthewplummer on 22/03/2017.
 */
public class UserInterfaceProcess extends JFrame {
    private JButton submit;
    private JPanel panel1;
    private JTextField wordsToSearch;
    private JLabel numberOfTweetsText;
    private JLabel wordsToSearchText;
    private JLabel collectionLabel;
    private JLabel errorLabel;
    private JLabel informationLabel;
    private JTextField numberOfTweets;
    private JLabel informationLabel2;
    private JCheckBox searchTickBox;
    private JButton searchButton;
    private JComboBox collectionsList;
    private JButton refreshCollectionList;
    private JTextPane InformationPane;
    private JFormattedTextField dateFormattedTextField;
    private JLabel datePickerLabel;
    public static int numberOfTweetsUsable;
    public String wordsToSearchUsable;
    public String collectionToInsertIntoUsable;
    public boolean searchToBeDone = false;
    static Object lock7 = new Object();
    static Object lock9 = new Object();
    Properties prop = new Properties();
    InputStream input = null;
    String username = null;
    String password = null;
    String clusterPrefix = null;
    String port = null;
    String databaseName = null;
    String absolutePathOfResourceFile = new File("src/main/resources/mongoDbConnection.properties").getAbsolutePath();
    String collectionName;
    List<String> list;
    Set<String> colls;
    String dateFormatSearchUsable = null;
    String datePattern = "\\d{4}-\\d{2}-\\d{2}";

    public static void main(String[] args) {
        UserInterfaceProcess myForm = new UserInterfaceProcess();
    }

    public UserInterfaceProcess() {
        super("Twitter sentiment analysis");

        synchronized (lock9) {
            getCollections();
        }
        setContentPane(panel1);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        dateFormattedTextField.setText(dateFormat.format(date));

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!wordsToSearch.getText().equals("") && !collectionsList.getSelectedItem().equals("")) {
                        wordsToSearchUsable = wordsToSearch.getText();
                        collectionToInsertIntoUsable = (String) collectionsList.getSelectedItem();
                        numberOfTweetsUsable = Integer.parseInt(numberOfTweets.getText());



                    boolean error = false;
                    if(searchTickBox.isSelected()) {
                            if (!numberOfTweets.getText().equals("")) {
                                errorLabel.setText("");
                                dateFormatSearchUsable = dateFormattedTextField.getText();
                                if (dateFormatSearchUsable.matches(datePattern)) {
                                    //If the tick box is ticked the code to run the search will be carried out.
                                    informationLabel.setText("Running analysis... searching Twitter for past tweets using: \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                                    informationLabel2.setText("Check analytics tool for results.");
                                    searchToBeDone = true;
                                    synchronized (lock7) {
                                        DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone, dateFormatSearchUsable);
                                    }
                                    searchToBeDone = false;
                                }
                                else
                                {
                                    errorLabel.setText("Enter a date in the format yyyy-mm-dd");
                                    error = true;
                                }
                            }
                            else {
                                errorLabel.setText("Enter a date in the format yyyy-mm-dd");
                                error = true;
                            }
                        }

                        if(error == false) {
                            errorLabel.setText(" ");
                            informationLabel.setText("Running analysis... retrieving " + numberOfTweetsUsable + " live Tweets, streaming for \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                            informationLabel2.setText("Check analytics tool for results.");
                            DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone, dateFormatSearchUsable);
                        }

                }
                else
                {
                    informationLabel.setText(" ");
                    informationLabel2.setText(" ");
                    errorLabel.setText("Complete all fields before submitting.");
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if (!wordsToSearch.getText().equals("") && !collectionsList.getSelectedItem().equals("") && !dateFormattedTextField.getText().equals("")) {
                        errorLabel.setText("");
                        dateFormatSearchUsable = dateFormattedTextField.getText();
                        if(dateFormatSearchUsable.matches(datePattern)) {
                            errorLabel.setText("");
                            wordsToSearchUsable = wordsToSearch.getText();
                            collectionToInsertIntoUsable = (String) collectionsList.getSelectedItem();
                            errorLabel.setText(" ");
                            informationLabel.setText("Running analysis... searching Twitter for past tweets using: \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                            informationLabel2.setText("Check analytics tool for results.");
                            searchToBeDone = true;
                            DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone, dateFormatSearchUsable);
                            searchToBeDone = false;
                        }
                        else
                        {
                            errorLabel.setText("Enter a date in the format yyyy-mm-dd");
                        }
                    }
                    else
                    {
                        informationLabel.setText(" ");
                        informationLabel2.setText(" ");
                        errorLabel.setText("Complete collection and search phrase fields before submitting.");
                    }
                }
        });
        refreshCollectionList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCollections();
            }
        });
        searchTickBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(searchTickBox.isSelected()) {
                    dateFormattedTextField.setEditable(true);
                    dateFormattedTextField.setEnabled(true);
                    searchButton.setEnabled(true);
                    submit.setText("Execute Twitter search AND stream");
                }
                else
                {
                    dateFormattedTextField.setEditable(false);
                    dateFormattedTextField.setEnabled(false);
                    searchButton.setEnabled(false);
                    submit.setText("Execute Twitter stream");
                }
            }
        });
    }

    public void getCollections(){
        //Get information needed to connect to the MongoDB Atlas cluster
        try {
            input = new FileInputStream(absolutePathOfResourceFile);
            // load a properties file
            prop.load(input);

            password = prop.getProperty("mongodb.password");
            username = prop.getProperty("mongodb.username");
            clusterPrefix = prop.getProperty("mongodb.clusterPrefix");
            port = prop.getProperty("mongodb.port");
            databaseName = prop.getProperty("mongodb.database");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally
        {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(username != null && password != null && clusterPrefix != null) {
            MongoClientURI uri = new MongoClientURI(
                    "mongodb://" + username + ":" + password + "@" + clusterPrefix + "-00-0bpyo.mongodb.net:" + port + "," + clusterPrefix + "-01-0bpyo.mongodb.net:" + port + "," + clusterPrefix + "-02-0bpyo.mongodb.net:" + port + "/" + databaseName + "?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");

            MongoClient mongoClient = new MongoClient(uri);
            DB db = mongoClient.getDB(databaseName);
            colls = db.getCollectionNames();
            list = new ArrayList<>(colls);

            collectionsList.removeAllItems();

            for(int i = 0; i < list.size(); i++)
            {
                collectionName = list.get(i).replace(",","");
                collectionName = list.get(i).replace("[","");
                collectionName = list.get(i).replace("]","");
                collectionsList.addItem(collectionName);
            }
        }

    }
}
