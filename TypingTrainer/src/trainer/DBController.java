package trainer;

import javafx.scene.chart.XYChart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DBController {
    private static DBController instance;
    private Connection connection;
    private final String url = "jdbc:postgresql://localhost:5432/TypingTrainer";
    private final String username = "postgres";
    private final String password = "Dmpost";
    private PreparedStatement ps;

    // Create tables queries
    private final String users = "CREATE TABLE IF NOT EXISTS users (username varchar(45) NOT NULL primary key, joined_at TIMESTAMP NOT NULL)";
    private final String usersStatistics = "CREATE TABLE IF NOT EXISTS statistics" +
            " (username varchar(45) NOT NULL references users(username), total_words int DEFAULT 0," +
            " correct_words int DEFAULT 0, invalid_words int DEFAULT 0, game_counter int DEFAULT 0, wpm int DEFAULT 0," +
            " last_train TIMESTAMP DEFAULT NULL)";
    private final String usersTrains = "CREATE TABLE IF NOT EXISTS users_trains (username varchar(45) NOT NULL references users(username), correct_words int DEFAULT 0, trained TIMESTAMP NOT NULL)";

    // Holding current user
    private String currentUser;

    private DBController() throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
        this.createUsers();
        this.createUsersStatistics();
        this.createUsersTrains();
    }

    public Connection getConnection() {
        return connection;
    }

    public static DBController getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBController();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBController();
        }
        return instance;
    }

    public void createUsers() {
        try {
            ps = this.connection.prepareStatement(users);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createUsersStatistics() {
        try {
            ps = this.connection.prepareStatement(usersStatistics);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createUsersTrains() {
        try {
            ps = this.connection.prepareStatement(usersTrains);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getUser(String username) throws SQLException {
        ps = this.connection.prepareStatement("SELECT 1 FROM users WHERE username=?");
        ps.setObject(1, username);
        ResultSet user = ps.executeQuery();

        //If no results create user and his stat
        if (!user.next())
        {
            //Insert new user
            ps = this.connection.prepareStatement("INSERT INTO users VALUES (?,NOW())");
            ps.setObject(1, username);
            ps.executeUpdate();

            //Insert new user statistics
            ps = this.connection.prepareStatement("INSERT INTO statistics(username) VALUES (?)");
            ps.setObject(1, username);
            ps.executeUpdate();
        }
        this.currentUser = username;
        return username;
    }

    public String getCurrentUser(){
        return this.currentUser;
    }

    public List<String> getUsers() throws SQLException {
        List<String> usersRes = new ArrayList<>();
        ps = this.connection.prepareStatement("SELECT username FROM users");
        ResultSet users = ps.executeQuery();
        while (users.next()) {
            usersRes.add(users.getString("username"));
        }
        return usersRes;
    }

    public int[] getUserStatistics(String username) throws SQLException {
        ps = this.connection.prepareStatement("SELECT * FROM statistics WHERE username=?");
        ps.setObject(1, username);
        ResultSet userStatistics = ps.executeQuery();
        userStatistics.next();
        return new int[] {userStatistics.getInt("total_words"),
                userStatistics.getInt("correct_words"),
                userStatistics.getInt("invalid_words"),
                userStatistics.getInt("game_counter"),
                userStatistics.getInt("wpm")
        };
    }

    public XYChart.Series<String, Integer> getUserProgress(String username) throws SQLException {
        XYChart.Series<String, Integer> userDataSeries = new XYChart.Series<String, Integer>();
        ps = this.connection.prepareStatement("SELECT * FROM users_trains WHERE username=? ORDER BY trained");
        ps.setObject(1, username);
        ResultSet userTrains = ps.executeQuery();
        while (userTrains.next()) {
            userDataSeries.getData().add(new XYChart.Data<String, Integer>(userTrains.getString("trained"), userTrains.getInt("correct_words")));
        }
        return userDataSeries;
    }

    public int saveUserRes(String username, int[] res) throws SQLException {
        int[] currentRes = this.getUserStatistics(username);
        int[] newRes = IntStream.range(0, res.length)
                .map(i -> currentRes[i] + res[i])
                .toArray();
        ps = this.connection.prepareStatement("UPDATE statistics SET" +
                " total_words=?, correct_words=?, invalid_words=?, game_counter=?, wpm=?, last_train=NOW()  WHERE username=?");
        ps.setObject(1, newRes[0]);
        ps.setObject(2, newRes[1]);
        ps.setObject(3, newRes[2]);
        ps.setObject(4, newRes[3]);
        ps.setObject(5, Math.round(newRes[1]*1.0/newRes[3]));
        ps.setObject(6, username);
        return ps.executeUpdate();
    }

    public void saveUserTrain(String username, int correct_words) throws SQLException {
        ps = this.connection.prepareStatement("INSERT INTO users_trains VALUES (?,?,NOW())");
        ps.setObject(1, username);
        ps.setObject(2, correct_words);
        ps.executeUpdate();
    }

    public String bestScore() throws SQLException {
        int highestScore = 0;
        int currentTotal = 0;
        String bestUser = null;
        ps = this.connection.prepareStatement("SELECT username,total_words,wpm FROM statistics ORDER BY last_train");
        ResultSet wpmResults = ps.executeQuery();
        while (wpmResults.next()) {
            if (wpmResults.getInt("wpm") > 0) {
                if (wpmResults.getInt("wpm") > highestScore) {
                    highestScore = wpmResults.getInt("wpm");
                    currentTotal = wpmResults.getInt("total_words");
                    bestUser = wpmResults.getString("username");
                }
                else if (wpmResults.getInt("wpm") == highestScore &&
                        wpmResults.getInt("total_words") < currentTotal) {
                    highestScore = wpmResults.getInt("wpm");
                    currentTotal = wpmResults.getInt("total_words");
                    bestUser = wpmResults.getString("username");
                }

            }
        }
        return bestUser;
    }
}

