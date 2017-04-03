package TwitterSentimentAnalysisFinal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by matthewplummer on 22/03/2017.
 */
public class TwitterForm {
    private JButton submit;
    private JPanel panel1;
    private JTextField wordsToSearch;
    private JLabel numberOfTweetsText;
    private JLabel wordsToSearchText;
    private JTextField collectionToInsert;
    private JLabel collectionLabel;
    private JLabel errorLabel;
    private JLabel informationLabel;
    private JTextField numberOfTweets;
    private JLabel informationLabel2;
    public static int numberOfTweetsUsable;
    public String wordsToSearchUsable;
    public String collectionToInsertIntoUsable;

    public static void main(String[] args) {
        JFrame frame = new JFrame("TwitterForm");
        frame.setContentPane(new TwitterForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        new TwitterForm();
    }

    public TwitterForm() {
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!wordsToSearch.getText().equals("") && !collectionToInsert.getText().equals("")) {
                    wordsToSearchUsable = wordsToSearch.getText();
                    numberOfTweetsUsable = Integer.parseInt(numberOfTweets.getText());
                    collectionToInsertIntoUsable = collectionToInsert.getText();
                    errorLabel.setText(" ");
                    informationLabel.setText("Running analysis... retrieving " + numberOfTweetsUsable + " Tweets, searching for \"" + wordsToSearchUsable + "\" and inserting into \"" + collectionToInsertIntoUsable + "\".");
                    informationLabel2.setText("Check analytics tool for results.");
                    DatabaseConnection.connection(collectionToInsertIntoUsable, numberOfTweetsUsable, wordsToSearchUsable);
                }
                else
                {
                    informationLabel.setText(" ");
                    informationLabel2.setText(" ");
                    errorLabel.setText("Complete all fields before submitting.");
                }
            }
        });
    }
}
