import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection connect(String dbName) throws Exception {
        String url = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String username = "root";
        String password = "Winter@2025";
        return DriverManager.getConnection(url, username, password);
    }
}

