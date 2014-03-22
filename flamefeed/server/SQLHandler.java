/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.server;

import flamefeed.server.log.EventLogger;
import flamefeed.server.log.LogEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anedaar
 */
public class SQLHandler {
    
    private static Connection con;
    
    private static final java.text.SimpleDateFormat sdf
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void init() {
        EventLogger.log(Level.INFO, "initializing SQL");
        try {
            //establish connection
            con = DriverManager.getConnection("jdbc:mysql://" + Server.SQL.host + "/" + Server.SQL.database, Server.SQL.user, Server.SQL.pw);
            Statement sql = con.createStatement();

            //create table
            EventLogger.log(Level.INFO, "creating Table logEntries");
            sql.executeUpdate("CREATE TABLE IF NOT EXISTS logEntries ("
                    + "x INT,"
                    + "y INT,"
                    + "z INT"
                    + ")");
            addSQLColumn(sql, "world", "VARCHAR(20)");
            addSQLColumn(sql, "time", "DATETIME");
            addSQLColumn(sql, "source", "VARCHAR(20)");
            addSQLColumn(sql, "target", "VARCHAR(20)");
            addSQLColumn(sql, "action", "VARCHAR(20)");
            addSQLColumn(sql, "tool", "VARCHAR(20)");
            sql.close();
        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            EventLogger.log(Level.SEVERE, "SQL logging has been disabled");
            Server.SQL.enabled = false;
        }
    }
    
    private static void addSQLColumn(Statement sql, String name, String type) {
        try {
            sql.executeUpdate("ALTER TABLE logEntries ADD " + name + " " + type);
        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }
    
    public static void log(LogEvent e) {
        String source = cut(e.source, 20);
        String target = cut(e.target, 20);
        String action = cut(e.action, 20);
        String tool = cut(e.tool, 20);
        String world = cut(e.world, 20);
        String time = sdf.format(e.time);
        try {
            Statement sql = con.createStatement();
            String query = "INSERT INTO logEntries (time,x,y,z,source,target,action,tool,world)"
                    + " VALUES('"
                    + time + "',"
                    + e.x + ","
                    + e.y + ","
                    + e.z + ",'"
                    + source + "','"
                    + target + "','"
                    + action + "','"
                    + tool + "','"
                    + world + "');";
            sql.executeUpdate(query);
            sql.close();

        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }
    
    public static String cut(String s, int n) {
        return s.substring(0, Math.min(n, s.length()));
    }
    
    public static ResultSet executeQuery(String query){
        if(con==null) return null;
    
        EventLogger.log(Level.INFO, "executing " + query);
        try {
            Statement sql = con.createStatement();
            ResultSet result = sql.executeQuery(query);
            sql.closeOnCompletion();
//            result.next();
//        EventLogger.log(Level.INFO, "first result time:" + result.getString("time"));
            return result;

        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
        return null;
    }
 
}
