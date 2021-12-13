package trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;
import javafx.scene.control.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    @FXML
    private Label timeLabel;
    @FXML
    private Label displayUsername;

    @FXML
    private Label wordsAmount;
    @FXML
    private Spinner<Integer> wordsAmountSpin;
    public static Spinner<Integer> wordsAmountSpinner;

    @FXML
    private Text total;
    @FXML
    private Text wpm;
    @FXML
    private Text invalid;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Controller.wordsAmountSpinner = wordsAmountSpin;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100, 10);
        Controller.wordsAmountSpinner.setValueFactory(valueFactory);

        File newFile = new File("username.txt");
        if (newFile.length() != 0) {
            Scanner reader = null;
            try {
                reader = new Scanner(newFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String data = reader.nextLine();
            displayUsername.setText("Welcome, "+data);

        }
        // set the day
        Date date = new Date();
        Locale locale = new Locale("en");
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        String strDay = formatter.format(date);

        timeLabel.setText("Today is " + strDay);

        // we need to display data
        int[] data = FileHandling.sumUpNumbers("src/data");
        total.setText(String.valueOf(data[0]));
        wpm.setText(String.valueOf(Math.round(data[1]*1.0/data[3])));
        invalid.setText(String.valueOf(data[2]));
    }

    public void playGame(ActionEvent ea) throws IOException {
        Main m = new Main();

        File newFile = new File("username.txt");
        if (newFile.length() == 0) {
            try {
                m.changeScene("popup.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            m.changeScene("game.fxml");
        }

    }
}
