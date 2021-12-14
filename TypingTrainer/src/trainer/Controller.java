package trainer;
import trainer.DBController;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    private DBController dbController;

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
        try {
            dbController = DBController.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void playGame(ActionEvent ea) throws IOException, SQLException {
        Main m = new Main();

        String username = fetchUser(userName.getText().trim());
        if (!username.isEmpty()){
            m.changeScene("game.fxml");
        }
    }

    public void getUserStatistics(ActionEvent ea) throws SQLException {
        fetchUserStatistics(userName.getText().trim());

    }

    public String fetchUser(String username) throws SQLException {
        if (username.isEmpty()) {
            userLabel.setText("You must enter a username!");
            userLabel.setStyle("-fx-text-fill: red;");
            return username;
        }
        return dbController.getUser(username);
    }

    public void fetchUserStatistics(String username) throws SQLException {
        int[] data = new int[4];
        if (username.isEmpty()) {
            userLabel.setText("You must enter a username!");
            userLabel.setStyle("-fx-text-fill: red;");
        }
        else {
            data = dbController.getUserStatistics(username);
        }
        //we need to display data
        total.setText(String.valueOf(data[0]));
        wpm.setText(String.valueOf(Math.round(data[1]*1.0/data[3])));
        invalid.setText(String.valueOf(data[2]));
    }

}
