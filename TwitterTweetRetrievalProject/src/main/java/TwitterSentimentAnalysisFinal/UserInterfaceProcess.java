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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

/**
 * Created by Matthew Plummer.
 * All user interface elements are handled here.
 * This includes the limitations for the API call window on Twitter of 180 requests per 15 minutes.
 * User inputs are passed to other classes from here in order to run the application.
 * The application is run from here, this is the entry point for the programme.
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
    private JLabel timerLabel;
    private JLabel numberOfRequestsLimit;
    private static int numberOfTweetsUsable;
    private String wordsToSearchUsable;
    private String collectionToInsertIntoUsable;
    private boolean searchToBeDone = false;
    private static final Object lock7 = new Object();
    private static final Object lock9 = new Object();
    private Properties prop = new Properties();
    private InputStream input = null;
    private String username = null;
    private String password = null;
    private String clusterPrefix = null;
    private String port = null;
    private String databaseName = null;
    private String absolutePathOfResourceFile = new File("src/main/resources/mongoDbConnection.properties").getAbsolutePath();
    private String collectionName;
    private List<String> list;
    private Set<String> collections;
    private String dateFormatSearchUsable = null;
    private String datePattern = "\\d{4}-\\d{2}-\\d{2}";
    private boolean timerRunning = false;
    private static int counter = 900;

    public static void main(String[] args) {
        UserInterfaceProcess myForm = new UserInterfaceProcess();
    }

    private UserInterfaceProcess() {
        super("Twitter sentiment analysis");

        synchronized (lock9) {
            getCollections();
        }
        setContentPane(panel1);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        dateFormattedTextField.setText(dateFormat.format(date));

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!wordsToSearch.getText().equals("") && !collectionsList.getSelectedItem().equals("") && !numberOfTweets.getText().equals("")) {
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
                                    if(!timerRunning) {
                                        counter = 900;
                                        timer();
                                    }
                                    synchronized (lock7) {
                                        DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone, dateFormatSearchUsable);
                                    }
                                    searchToBeDone = false;
                                }
                                else {
                                    errorLabel.setText("Enter a date in the format yyyy-mm-dd");
                                    error = true;
                                }
                            }
                            else {
                                errorLabel.setText("Enter a date in the format yyyy-mm-dd");
                                error = true;
                            }
                        }

                        if(!error) {
                            errorLabel.setText(" ");
                                informationLabel.setText("Running analysis... retrieving " + numberOfTweetsUsable + " live Tweets, streaming for \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                                informationLabel2.setText("Check analytics tool for results.");
                                DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone, dateFormatSearchUsable);
                        }
                }
                else {
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
                            if(!timerRunning) {
                                counter = 900;
                                timer();
                            }
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
                        else {
                            errorLabel.setText("Enter a date in the format yyyy-mm-dd");
                        }
                    }
                    else {
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
                else {
                    dateFormattedTextField.setEditable(false);
                    dateFormattedTextField.setEnabled(false);
                    searchButton.setEnabled(false);
                    submit.setText("Execute Twitter stream");
                }
            }
        });
    }

    private void getCollections(){
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
            collections = db.getCollectionNames();
            list = new ArrayList<>(collections);

            collectionsList.removeAllItems();

            for (String aList : list) {
                collectionName = aList.replace(",", "");
                collectionName = aList.replace("[", "");
                collectionName = aList.replace("]", "");
                collectionsList.addItem(collectionName);
            }
        }
    }

    private void timer()
    {
        final int[] minutesRemainder = new int[1];
        final int[] wholeMinutes = new int[1];
        timerRunning = true;

        final Timer timer = new Timer("MyTimer");//create a new Timer

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter--;//increments the counter
                minutesRemainder[0] = counter % 60; //gets the seconds for the minute
                wholeMinutes[0] = (int) Math.floor(counter/60); //rounds the minutes to the minute
                if(wholeMinutes[0] < 10 && minutesRemainder[0] >= 10) {
                    timerLabel.setText("Timer for request limit:" + "0" + wholeMinutes[0] + ":" + minutesRemainder[0]);
                }
                else if(minutesRemainder[0] < 10 && wholeMinutes[0] >= 10) {
                    timerLabel.setText("Timer for request limit:" + wholeMinutes[0] + ":" + "0" + minutesRemainder[0]);
                }
                else if(minutesRemainder[0] < 10 && wholeMinutes[0] < 10) {
                    timerLabel.setText("Timer for request limit:" + "0" + wholeMinutes[0] + ":" + "0" + minutesRemainder[0]);
                }
                else {
                    timerLabel.setText("Timer for request limit:" + wholeMinutes[0] + ":" + minutesRemainder[0]);
                }
                numberOfRequestsLimit.setText("Number of requests: " + String.valueOf(TwitterSearch.requests) + "/170");
                if(counter <= 0) {
                    numberOfRequestsLimit.setText("Number of requests: 0/170");
                    timerRunning = false;
                    TwitterSearch.requests = 0;
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 30, 1000);//this line starts the timer at the same time its executed
    }
}
