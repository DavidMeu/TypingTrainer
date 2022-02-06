package trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;


public class UserProgress implements Initializable {

    private DBController dbController;

    @FXML
    private NumberAxis correctWordsAxis;

    @FXML
    private CategoryAxis dateAxis;

    @FXML
    private LineChart<String,Integer> progressChart;

    @FXML
    private ComboBox<String> userProg;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            dbController = DBController.getInstance();
            userProg.setPromptText("User Progress");
            List<String> users = dbController.getUsers();
            userProg.getItems().addAll(users);

            progressChart.setAnimated(false);
            progressChart.setCreateSymbols(false);
            progressChart.setLegendVisible(false);
            progressChart.getXAxis().setTickLabelsVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getUserProgressData(ActionEvent ea) {
        try {
            progressChart.getData().clear();
            XYChart.Series<String, Integer> userProgress = dbController.getUserProgress(userProg.getValue());
            progressChart.getData().add(userProgress);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void toMainMenu(ActionEvent ae) throws IOException {
        Main m = new Main();
        m.changeScene("main.fxml");
    }

}
