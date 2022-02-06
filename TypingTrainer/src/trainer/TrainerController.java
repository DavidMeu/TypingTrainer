package trainer;
import java.sql.SQLException;
import java.util.*;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TrainerController extends Controller {


    private int wordCounter = 0;
    private int first = 1;
    private int timer = 60;
    private DBController dbController;

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

    private List<String> words = new ArrayList<String>();

    // add words to array list
    private List<String> readWordsFromURL(String url) throws IOException {
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            return br
                    .lines()
                    .collect(Collectors.toCollection(ArrayList<String>::new));
        }
    }

    public void toMainMenu(ActionEvent ae) throws IOException {
        Main m = new Main();
        m.changeScene("main.fxml");
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
            words = readWordsFromURL("http://web.stanford.edu/class/archive/cs/cs106l/cs106l.1102/assignments/dictionary.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(words);
        programWord.setText(words.get(wordCounter));
        secondProgramWord.setText(words.get(wordCounter+1));
        wordCounter++;


    }

    Runnable timerRunner = new Runnable() {
        @Override
        public void run() {
            if (timer > -1) {
                seconds.setText(String.valueOf(timer));
            }

            else {
                if (timer == -1) {
                    userWord.setDisable(true);
                    userWord.setText("Game over");
                    String currentUser = dbController.getCurrentUser();
                    int[] resArray = {countAll, counter, countAll-counter, 1};
                    //Insert user game progress DATA
                    try {
                        int updated = dbController.saveUserRes(currentUser, resArray);
                        if (updated == 1){
                            //TODO: Check if user broke his own record and display it else don't.
                            String updatedRes = String.format("Updated %s result!", dbController.getCurrentUser());
                            secondProgramWord.setVisible(false);
                            programWord.setText(updatedRes);
                        }
                        dbController.saveUserTrain(currentUser, counter);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (timer == -4) {
                    playAgain.setVisible(true);
                    playAgain.setDisable(false);
                    executor.shutdown();
                }

            }
            timer -= 1;
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
            executor.scheduleAtFixedRate(timerRunner, 0, 1, TimeUnit.SECONDS);
        }

        if (ke.getCode().equals(KeyCode.ENTER)) {

            String insertedWord = userWord.getText();
            String real = programWord.getText();
            countAll++;

            // if correct
            if (insertedWord.equals(real)) {
                counter++;
                wordsPerMin.setText(String.valueOf(counter));
                Thread correct = new Thread(fadeCorrect);
                correct.start();

            }
            else {
                Thread wrong = new Thread(fadeWrong);
                wrong.start();
            }
            userWord.setText("");
            accuracy.setText(String.valueOf(Math.round((counter*1.0/countAll)*100)));
            programWord.setText(words.get(wordCounter));
            secondProgramWord.setText(words.get(wordCounter+1));
            wordCounter++;
        }
    }
}
