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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 *
 * @author Anedaar
 */
public class SQLHandler {
    
    private static Statement sql;
    private static PreparedStatement sqlAddEntry;
    
    private static final java.text.SimpleDateFormat sdf
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void init() {
        EventLogger.log(Level.INFO, "initializing SQL");
        try {
            //establish connection
            Connection con = DriverManager.getConnection("jdbc:mysql://" + Server.SQL.host + "/" + Server.SQL.database, Server.SQL.user, Server.SQL.pw);
            sql = con.createStatement();

            //create table
            EventLogger.log(Level.INFO, "creating Table logEntries");
            sql.executeUpdate("CREATE TABLE IF NOT EXISTS logEntries ("
                    + "x INT,"
                    + "y INT,"
                    + "z INT"
                    + ")");
            addSQLColumn("world", "VARCHAR(20)");
            addSQLColumn("time", "DATETIME");
            addSQLColumn("source", "VARCHAR(20)");
            addSQLColumn("target", "VARCHAR(20)");
            addSQLColumn("action", "VARCHAR(5)");
            addSQLColumn("tool", "VARCHAR(20)");
            
            sqlAddEntry = con.prepareStatement("INSERT INTO logEntries "+
                    "(time,x,y,z,source,target,action,tool,world)"+
                    " VALUES(?,?,?,?,?,?,?,?,?);");
        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            EventLogger.log(Level.SEVERE, "SQL logging has been disabled");
            Server.SQL.enabled = false;
        }
    }
    
    private static void addSQLColumn(String name, String type) {
        try {
            sql.executeUpdate("ALTER TABLE logEntries ADD " + name + " " + type);
        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }
    
    public static void log(LogEvent e) {
        try {
        //time
        sqlAddEntry.setString(1, sdf.format(e.time));
        //x
        sqlAddEntry.setInt(2, e.x);
        //y
        sqlAddEntry.setInt(3, e.y);
        //z
        sqlAddEntry.setInt(4, e.z);
        //source
        sqlAddEntry.setString(5, cut(e.source, 20));
        //target
        sqlAddEntry.setString(6, cut(e.target, 20));
        //action
        sqlAddEntry.setString(7, cut(e.action, 5));
        //tool
        sqlAddEntry.setString(8, cut(e.tool, 20));
        //world
        sqlAddEntry.setString(9, cut(e.world, 20));
        
        sqlAddEntry.executeUpdate();

        sqlAddEntry.clearParameters();
        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }
    
    public static String cut(String s, int n) {
        return s.substring(0, Math.min(n, s.length()));
    }
    
    public static ResultSet executeQuery(String query){
        if(sql==null) return null;
    
        EventLogger.log(Level.INFO, "executing " + query);
        try {
            ResultSet result = sql.executeQuery(query);
//            result.next();
//        EventLogger.log(Level.INFO, "first result time:" + result.getString("time"));
            return result;

        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
        return null;
    }
 
}
