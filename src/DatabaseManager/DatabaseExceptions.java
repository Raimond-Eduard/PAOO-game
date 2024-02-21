package DatabaseManager;

import java.sql.SQLException;

public class DatabaseExceptions extends Exception {
    public DatabaseExceptions(String message, Exception e) {
        super(message + ": " + e.getMessage(), e);
    }
}
