package trainer;
import java.sql.SQLException;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameController extends Controller {


    private int wordCounter = 0;
    private int first = 1;
    private int timer = 10;
    private DBController dbController;

    private File saveData;

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @FXML
    public Text seconds;
    @FXML
    private Text wordsPerMin;
    @FXML
    private Text accuracy;
    @FXML
    private Text programWord;
    @FXML
    private Text secondProgramWord;

    @FXML
    private TextField userWord;

    @FXML
    private ImageView correct;
    @FXML
    private ImageView wrong;

    @FXML
    private Button playAgain;

    ArrayList<String> words = new ArrayList<>();

    // add words to array list
    public void addToList() throws IOException {
        String urlString = "http://web.stanford.edu/class/archive/cs/cs106l/cs106l.1102/assignments/dictionary.txt";
        URL url = new URL(urlString);
        Scanner scannerWords = new Scanner(url.openStream());
        while (scannerWords.hasNextLine()) {
            words.add(scannerWords.nextLine());
        }
    }

    public void toMainMenu(ActionEvent ae) throws IOException {
        Main m = new Main();
        m.changeScene("trainer.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            dbController = DBController.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        playAgain.setVisible(false);
        playAgain.setDisable(true);
        seconds.setText(String.valueOf(timer));
        try {
            addToList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(words);
        programWord.setText(words.get(wordCounter));
        secondProgramWord.setText(words.get(wordCounter+1));
        wordCounter++;


    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            if (timer > -1) {
                seconds.setText(String.valueOf(timer));
                timer -= 1;
            }

            else {
                if (timer == -1) {
                    userWord.setDisable(true);
                    userWord.setText("Game over");
                    String currentUser = dbController.getCurrentUser();
                    int[] resArray = {countAll, counter, countAll-counter, 1};
                    try {
                        dbController.saveUserRes(currentUser, resArray);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (timer == -4) {
                    playAgain.setVisible(true);
                    playAgain.setDisable(false);
                    executor.shutdown();
                }

                timer -= 1;
            }
        }
    };

    Runnable fadeCorrect = new Runnable() {
        @Override
        public void run() {
            correct.setOpacity(0);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            correct.setOpacity(50);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            correct.setOpacity(100);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            correct.setOpacity(0);

        }
    };

    Runnable fadeWrong = new Runnable() {
        @Override
        public void run() {
            wrong.setOpacity(0);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wrong.setOpacity(50);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wrong.setOpacity(100);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wrong.setOpacity(0);
        }
    };


    private int countAll = 0;
    private int counter = 0;

    public void startGame(KeyEvent ke) {

        // only gets called once
        if (first == 1) {
            first = 0;
            executor.scheduleAtFixedRate(r, 0, 1, TimeUnit.SECONDS);
        }

        if (ke.getCode().equals(KeyCode.ENTER)) {

            String s = userWord.getText();
            String real = programWord.getText();
            countAll++;

            // if correct
            if (s.equals(real)) {
                counter++;
                wordsPerMin.setText(String.valueOf(counter));
                Thread t = new Thread(fadeCorrect);
                t.start();

            }
            else {
                Thread t = new Thread(fadeWrong);
                t.start();
            }
            userWord.setText("");
            accuracy.setText(String.valueOf(Math.round((counter*1.0/countAll)*100)));
            programWord.setText(words.get(wordCounter));
            secondProgramWord.setText(words.get(wordCounter+1));
            wordCounter++;
        }
    }
}
