package trainer;
import javafx.scene.control.*;
import trainer.DBController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {

    private DBController dbController;

    @FXML
    private Label displayUsername;

    @FXML
    private Label userLabel;
    @FXML
    private TextField userName;

    @FXML
    private ComboBox<String> userStat;
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
            List<String> users = dbController.getUsers();
            userStat.setPromptText("User Statistics");
            userStat.getItems().addAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void playGame(ActionEvent ea) throws IOException, SQLException {
        Main m = new Main();

        String username = getUser(userName.getText().trim());
        if (!username.isEmpty()){
            m.changeScene("game.fxml");
        }
    }

    public void getUserStatistics(ActionEvent ea) throws SQLException {
        int[] userStatistics = dbController.getUserStatistics(userStat.getValue());
        //we need to display data
        total.setText(String.valueOf(userStatistics[0]));
        wpm.setText(String.valueOf(Math.round(userStatistics[1]*1.0/userStatistics[3])));
        invalid.setText(String.valueOf(userStatistics[2]));
    }

    public String getUser(String username) throws SQLException {
        if (username.isEmpty()) {
            userLabel.setText("You must enter a username!");
            userLabel.setStyle("-fx-text-fill: red;");
            return username;
        }
        return dbController.getUser(username);
    }

}
