package trainer;

import java.sql.*;
import java.lang.Class;

public class DBController {
    private static DBController instance;
    private Connection connection;
    private final String url = "jdbc:postgresql://localhost:5432/TrypingTrainer";
    private final String username = "postgres";
    private final String password = "";
    private PreparedStatement ps;

    // tables queries
    private String Users = "CREATE TABLE IF NOT EXISTS users (username varchar(45) NOT NULL primary key)";
    private String Statistics = "CREATE TABLE IF NOT EXISTS statistics" +
            " (username varchar(45) NOT NULL references users(username), total_words int DEFAULT 0," +
            " avg int DEFAULT 0, invalid int DEFAULT 0, game_counter int DEFAULT 0)";

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
            ps = this.connection.prepareStatement("INSERT INTO users(username) VALUES (?)");
            ps.setObject(1, username);
            ps.executeUpdate();
        }
        return username;
    }

    public int[] getUserStatistics(String username) throws SQLException {
        this.getUser(username);
        ps = this.connection.prepareStatement("SELECT * FROM statistics WHERE username=?");
        ps.setObject(1, username);
        ResultSet userStatistics = ps.executeQuery();
        if (!userStatistics.next())
        {
            ps = this.connection.prepareStatement("INSERT INTO statistics(username) VALUES (?)");
            ps.setObject(1, username);
            ps.executeUpdate();
            ps = this.connection.prepareStatement("SELECT * FROM statistics WHERE username=?");
            ps.setObject(1, username);
            userStatistics = ps.executeQuery();
            userStatistics.next();
        }
        return new int[] {userStatistics.getInt("total_words"),
                userStatistics.getInt("avg"),
                userStatistics.getInt("invalid"),
                userStatistics.getInt("game_counter")};

    }
}

