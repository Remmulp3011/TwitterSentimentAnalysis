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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

    public static void main(String[] args) {
        UserInterfaceProcess myForm = new UserInterfaceProcess();
    }

    /*public void barProgress(int progress)
    {
        final float percentage = (progress*100/numberOfTweetsUsable);
        String percentageWord = String.valueOf(percentage);
        PercentageCompelete.setText(percentageWord);
       *//* Runnable runner = new Runnable()
        {
            public void run() {
                    //progressBar.setValue(percentage);
                    progressBar.setString(percentage + "%");
                    progressBar.repaint();
                    System.out.println("PROGRESS BAR VALUE IS>>>>" + progressBar.getValue());
                    System.out.println("PROGRESS BAR STRING IS>>>>" + progressBar.getString());
            }
        };
        Thread t = new Thread(runner, "Code Executor");
        t.start();*//*
    }*/

    public UserInterfaceProcess() {
        super("Twitter sentiment analysis");

        synchronized (lock9) {
            getCollections();
        }

        setContentPane(panel1);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!wordsToSearch.getText().equals("") && !collectionsList.getSelectedItem().equals("") && !numberOfTweets.getText().equals("")) {

                    wordsToSearchUsable = wordsToSearch.getText();
                    collectionToInsertIntoUsable = (String) collectionsList.getSelectedItem();
                    numberOfTweetsUsable = Integer.parseInt(numberOfTweets.getText());

                    //If the tick box is ticked the code to run the search will be carried out.
                            informationLabel.setText("Running analysis... searching Twitter for past tweets using: \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                        informationLabel2.setText("Check analytics tool for results.");


                    if (searchTickBox.isSelected()) {
                        searchToBeDone = true;
                        synchronized (lock7) {
                            DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone);
                        }
                        searchToBeDone = false;
                    }

                    errorLabel.setText(" ");
                    informationLabel.setText("Running analysis... retrieving " + numberOfTweetsUsable + " live Tweets, streaming for \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                    informationLabel2.setText("Check analytics tool for results.");
                    DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone);
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
                    if (!wordsToSearch.getText().equals("") && !collectionsList.getSelectedItem().equals("")) {
                        wordsToSearchUsable = wordsToSearch.getText();
                        collectionToInsertIntoUsable = (String) collectionsList.getSelectedItem();
                        errorLabel.setText(" ");
                        informationLabel.setText("Running analysis... searching Twitter for past tweets using: \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                        informationLabel2.setText("Check analytics tool for results.");
                        searchToBeDone = true;
                        DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable, searchToBeDone);
                        searchToBeDone = false;
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
