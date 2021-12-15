package trainer;

import java.sql.*;
import java.lang.Class;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class DBController {
    private static DBController instance;
    private Connection connection;
    private final String url = "jdbc:postgresql://localhost:5432/TrypingTrainer";
    private final String username = "postgres";
    private final String password = "Dmpost";
    private PreparedStatement ps;

    // tables queries
    private String Users = "CREATE TABLE IF NOT EXISTS users (username varchar(45) NOT NULL primary key)";
    private String Statistics = "CREATE TABLE IF NOT EXISTS statistics" +
            " (username varchar(45) NOT NULL references users(username), total_words int DEFAULT 0," +
            " correct_words int DEFAULT 0, invalid_words int DEFAULT 0, game_counter int DEFAULT 0)";

    //Current user
    private String currentUser;

    private DBController() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            ps = this.connection.prepareStatement(Users);
            ps.executeUpdate();
            ps = this.connection.prepareStatement(Statistics);
            ps.executeUpdate();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public String getUser(String username) throws SQLException {
        ps = this.connection.prepareStatement("SELECT 1 FROM users WHERE username=?");
        ps.setObject(1, username);
        ResultSet user = ps.executeQuery();
        if (!user.next())
        {
            //Init user
            ps = this.connection.prepareStatement("INSERT INTO users(username) VALUES (?)");
            ps.setObject(1, username);
            ps.executeUpdate();
            //Init user statistics
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
                userStatistics.getInt("game_counter")};

    }

    public void saveUserRes(String username, int[] res) throws SQLException {
        int[] currentRes = this.getUserStatistics(username);
        int[] newRes = IntStream.range(0, currentRes.length)
                .map(i -> currentRes[i] + res[i])
                .toArray();
        ps = this.connection.prepareStatement("UPDATE statistics SET" +
                " total_words=?, correct_words=?, invalid_words=?, game_counter=? WHERE username=?");
        ps.setObject(1, newRes[0]);
        ps.setObject(2, newRes[1]);
        ps.setObject(3, newRes[2]);
        ps.setObject(4, newRes[3]);
        ps.setObject(5, username);
        ps.executeUpdate();
    }
}

