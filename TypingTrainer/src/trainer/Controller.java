package trainer;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import org.controlsfx.control.textfield.TextFields;

public class Controller implements Initializable {

    private DBController dbController;

    @FXML
    private Label bestScoreLabel;

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
            TextFields.bindAutoCompletion(userName,users).setMaxWidth(155);

            //best score
            String bestScore = dbController.bestScore();
            if (bestScore != null){
                bestScoreLabel.setText(bestScoreLabel.getText() + bestScore);
            }
            else {
                bestScoreLabel.setVisible(false);
            }

            displayUsername.textProperty().bind(Bindings.concat("Welcome ").concat(userName.textProperty()));
            if (dbController.getCurrentUser() != null){
                userName.setText(dbController.getCurrentUser());
                userStat.getSelectionModel().select(dbController.getCurrentUser());
                getUserStatistics(null);
            }
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

    public void getUserStatistics(ActionEvent ea) {
        int[] userStatistics = new int[0];
        try {
            userStatistics = dbController.getUserStatistics(userStat.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //we need to display data
        total.setText(String.valueOf(userStatistics[0]));
        wpm.setText(String.valueOf(userStatistics[4]));
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
