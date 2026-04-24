package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class cnx {

    public static Connection getConnexion() {
        Connection con = null;
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // DB URL
            String url = "jdbc:mysql://localhost:3306/school_db?useUnicode=true&characterEncoding=UTF-8";
            String user = "root";
            String password = ""; // put your password if exists

            // Connect
            con = DriverManager.getConnection(url, user, password);

            System.out.println("Connected successfully!");

        } catch (Exception e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return con;
    }
}