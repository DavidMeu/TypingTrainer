package trainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;


public class UserProgress implements Initializable {

    @FXML
    private LineChart progressChart;

    private DBController dbController;
    private Controller controller;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            controller = new Controller();
            String username = controller.getUser(controller.userName.getText().trim());
            dbController = DBController.getInstance();
            int userData;
            userData = dbController.userProgressData(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void parsingUserProgress(){
        //Get current user progress data
    }

    public void toMainMenu(ActionEvent ae) throws IOException {
        Main m = new Main();
        m.changeScene("main.fxml");
    }

}
