package trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;

public class PopupController {

    @FXML
    private TextField username;

    public void submit(ActionEvent ae) throws IOException {
        String name = username.getText();
        FileWriter myObj = new FileWriter("username.txt");
        myObj.write(name);
        myObj.close();

        Main m = new Main();
        m.changeScene("trainer.fxml");
    }

}
