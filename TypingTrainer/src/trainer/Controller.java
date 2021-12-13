package trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
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

    private String username;

    @FXML
    private Label displayUsername;

    @FXML
    private Label userLabel;
    @FXML
    private TextField userName;

    @FXML
    private Text total;
    @FXML
    private Text wpm;
    @FXML
    private Text invalid;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // set the day
        Date date = new Date();
        Locale locale = new Locale("en");
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        String strDay = formatter.format(date);

        // we need to display data
        int[] data = FileHandling.sumUpNumbers("src/data");
        total.setText(String.valueOf(data[0]));
        wpm.setText(String.valueOf(Math.round(data[1]*1.0/data[3])));
        invalid.setText(String.valueOf(data[2]));
    }

    public void playGame(ActionEvent ea) throws IOException {
        Main m = new Main();

        username = userName.getText().trim();
        if (username.isEmpty()) {
            userLabel.setText("You must enter a username!");
            userLabel.setStyle("-fx-text-fill: red;");
        }
        else {
            m.changeScene("game.fxml");
        }

    }

    public void getUserStatistics(ActionEvent ea) throws IOException {
        username = userName.getText().trim();
        if (username.isEmpty()) {
            userLabel.setText("You must enter a username!");
            userLabel.setStyle("-fx-text-fill: red;");
        }

    }

}
