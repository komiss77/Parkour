package me.A5H73Y.Parkour;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.event.Listener;

public class MySQL implements Listener {

    //private static String host;
    //private static String user;
    //private static String password;
    //private static String db;
    private static String table;
    private static Connection connection;
    private static String url;

    
    
    public MySQL() {
            //host = Parkour.GetConfig().getString("Database.Host");
            //user = Parkour.GetConfig().getString("Database.User");
            //password = Parkour.GetConfig().getString("Database.Password");
            //db = Parkour.GetConfig().getString("Database.Db");
            table = Parkour.GetConfig().getString("Database.Table");
            
            url = Parkour.GetConfig().getString("Database.Host") + "?useUnicode=true&characterEncoding=utf-8&user=" + 
                    Parkour.GetConfig().getString("Database.User") + "&password=" + Parkour.GetConfig().getString("Database.Password");
            connection = CreateConn();
            SetupTable();
    }

    
    
  /*  static void connect() {

        try {
                if (connection == null) {
                    String ex = "jdbc:mysql://" + MySQL.host + "/" + MySQL.db;

                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    MySQL.connection = (Connection) DriverManager.getConnection(ex, MySQL.user, MySQL.password);
                    System.out.println("[Parkour] [SQL] Successfully Connected to database!");
                    MySQL.connection.setEncoding("UTF-8");
                }
            } catch (InstantiationException instantiationexception) {
                System.err.println("[Parkour] [SQL] Connection to database failed!");
            }

    }
*/
    
    
    
    static void createCourse(String course) {
        try {
            String e = "INSERT INTO " + table + " (`id` ,`course` ,`time` ,`deaths` ,`player` ,`created`) VALUES (NULL ,  \'" + course + "\',  \'23:59:59\',  \'999\',  \'N/A\', CURRENT_TIMESTAMP);";

           Statement statement = GetConnection().createStatement(); 
            statement.executeUpdate(e);
        } catch (SQLException sqlexception) {
            System.out.println(sqlexception.getMessage());
        }

    }

    
    
    static void updateRecord(String course, String time, int deaths, String player) {
        try {
            Statement statement = GetConnection().createStatement();
            String e = "UPDATE " + table + " SET time = \'" + time + "\', deaths = " + deaths + ", player = \'" + player + "\' WHERE (course = \'" + course + "\');";

            statement.executeUpdate(e);
            Parkour.logger.log(Level.INFO, "[Parkour] [SQL] Updated {0}", course);
        } catch (SQLException sqlexception) {
            System.out.println(sqlexception.getMessage());
        }

    }

    
    
    static void deleteRecord(String course) {
        try {
            Statement statement = GetConnection().createStatement();
            String e = "DELETE FROM " + table + " WHERE (course = \'" + course + "\');";

            statement.execute(e);
            Parkour.logger.log(Level.INFO, "[Parkour] [SQL] Deleted {0}", course);
        } catch (SQLException sqlexception) {
            System.out.println(sqlexception.getMessage());
        }

    }
    
    
    
    
    
private static void SetupTable() {
        try {
            
            GetConnection().createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `" 
                            + table + "` (" 
                            + "`id` int(11) NOT NULL AUTO_INCREMENT," 
                            + "`course` varchar(15) NOT NULL," 
                            + "`time` varchar(8) NOT NULL," 
                            + "`deaths` int(11) NOT NULL," 
                            + "`player` varchar(30) NOT NULL," 
                            + "`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," 
                            + "PRIMARY KEY (`id`), UNIQUE KEY `course` (`course`)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");
            
        } catch (SQLException sqlexception) {}

    }

 
private static Connection CreateConn() {
        try {
            Disconnect();
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.print("§4MySql: соединение с базой денег не удалось!");
            e.printStackTrace();
            return null;
        }
    }
    
    
private static Connection GetConnection() {
        try {
            if ( connection != null && !connection.isClosed() && connection.isValid(6)) return connection;
            else {
            System.out.print("§6MySQL - таймаут соединения, реконнект..");
                Long olong = System.currentTimeMillis();
                CreateConn();
                System.out.print("§2MySQL соединение востановлено за "+ (System.currentTimeMillis() - olong));
            }
        } catch (SQLException sqlexception) {
            sqlexception.printStackTrace();
        }
        
        return connection = CreateConn();
    }


private static void Disconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException sqlexception) {
            sqlexception.printStackTrace();
        }

    }
    
    
}
