/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.server.log;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import flamefeed.server.Server;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 *
 * @author Anedaar
 */
public class SQLHandler {

    private static Statement sql;
    private static PreparedStatement sqlAddEntry;

    private static final java.text.SimpleDateFormat sdf
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SQLHandlerThread thread;

    //Thread to handle all SQL Queries
    //Main class is responsible to only instantiate once
    private static class SQLHandlerThread extends Thread {

        //Helper Class to store a Packet and a Player inside the queue
        private class PlayerQuery {

            public Packet250CustomPayload packet;
            public Player player;

            public PlayerQuery(Packet250CustomPayload pa, Player pl) {
                this.packet = pa;
                this.player = pl;
            }
        }

        private final ConcurrentLinkedQueue<Object> eventQueue = new ConcurrentLinkedQueue();

        @Override
        public void run() {
            Object obj = null;

            while (!this.isInterrupted()) {
                //get Object if possible, obj might be null if Queue is empty
                synchronized (eventQueue) {
                    obj = eventQueue.poll();
                }

                //pass object to the handling method
                if (obj instanceof LogEvent) {
                    log((LogEvent) obj);
                } else if (obj instanceof PlayerQuery) {
                    handleQuery((PlayerQuery) obj);
                } else if (obj == null) {
                    //queue empty, wait for new objects
                    try {
                        synchronized (eventQueue) {
                            eventQueue.wait();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SQLHandler.class.getName()).log(Level.SEVERE, null, ex);
                        this.interrupt();
                    }
                } //else: unknown object, discard and go on (should be impossible)
            }
        }

        public void addEvent(LogEvent event) {
            synchronized (eventQueue) {
                eventQueue.add(event);
                eventQueue.notify();
            }
        }

        public void addQuery(Packet250CustomPayload packet, Player player) {
            synchronized (eventQueue) {
                eventQueue.add(new PlayerQuery(packet, player));
                eventQueue.notify();
            }
        }

        //logs events to the database
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

        //handles queries from players to get an extract of the database
        private void handleQuery(PlayerQuery pQuery) {

            Packet250CustomPayload packet = pQuery.packet;
            Player player = pQuery.player;

            String rawData[];
            HashMap<String, String> data = new HashMap();

            try {
                rawData = new String(packet.data, "UTF-8").split(";");
                for (String rawData1 : rawData) {
                    String splitData[] = rawData1.split(",");
                    data.put(splitData[0].trim(), splitData[1].trim());
                }
            } catch (UnsupportedEncodingException ex) {
                EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            }

            String sqlQuery = "SELECT * FROM logEntries WHERE";

            sqlQuery += " x BETWEEN " + data.get("xMin") + " AND " + data.get("xMax");
            sqlQuery += " AND y BETWEEN " + data.get("yMin") + " AND " + data.get("yMax");
            sqlQuery += " AND z BETWEEN " + data.get("zMin") + " AND " + data.get("zMax");
            sqlQuery += " AND time BETWEEN '" + data.get("dateMin") + "' AND '" + data.get("dateMax") + "'";
            sqlQuery += " AND world = '" + ((EntityPlayer) player).worldObj.getWorldInfo().getWorldName() + "/" + data.get("dim") + "'";

            sqlQuery += " ORDER BY time DESC;";

            EventLogger.log(Level.INFO, "executing " + sqlQuery);

            ResultSet resultSet = null;

            try {
                resultSet = sql.executeQuery(sqlQuery);
            } catch (SQLException ex) {
                EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            }

            if (resultSet == null) {
                return;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream(1);
            DataOutputStream outputStream = new DataOutputStream(bos);

            try {
                while (resultSet.next()) {
                    outputStream.writeUTF(resultSet.getString("time"));
                    outputStream.writeUTF(",");
                    outputStream.writeUTF(resultSet.getString("x"));
                    outputStream.writeUTF(",");
                    outputStream.writeUTF(resultSet.getString("y"));
                    outputStream.writeUTF(",");
                    outputStream.writeUTF(resultSet.getString("z"));
                    outputStream.writeUTF(",");
                    outputStream.writeUTF(resultSet.getString("source"));
                    outputStream.writeUTF(",");
                    outputStream.writeUTF(resultSet.getString("action"));
                    outputStream.writeUTF(",");
                    outputStream.writeUTF(resultSet.getString("target"));
                    outputStream.writeUTF(",");
                    outputStream.writeUTF(resultSet.getString("tool"));
                    outputStream.writeUTF(";");
                }
                resultSet.close();
            } catch (IOException ex) {
                EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            } catch (SQLException ex) {
                EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            } catch (NullPointerException ex) {
                EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            }

            Packet250CustomPayload resultPacket = new Packet250CustomPayload();
            resultPacket.channel = "FlameProtect";
            resultPacket.data = bos.toByteArray();
            resultPacket.length = bos.size();

            PacketDispatcher.sendPacketToPlayer(resultPacket, player);

        }
    } //End of SQLHandlerThread

    /*    
     *   SQLHandler main Class
     *
     */
    /**
     * Initializes the SQL Connection and the SQLHandlerThread
     */
    public static synchronized void init() {
        if (thread != null) {
            return;
        }

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

            sqlAddEntry = con.prepareStatement("INSERT INTO logEntries "
                    + "(time,x,y,z,source,target,action,tool,world)"
                    + " VALUES(?,?,?,?,?,?,?,?,?);");

            thread = new SQLHandlerThread();
            thread.setName("SQLHandlerThread");
            thread.setDaemon(true);
            thread.start();

        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
            EventLogger.log(Level.SEVERE, "SQL logging has been disabled");
            Server.SQL.enabled = false;
        }
    }

    /**
     * Stops the SQLHandlerThread and closes the SQL connection
     */
    public static void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        try {
            sql.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //helper method for init()
    private static void addSQLColumn(String name, String type) {
        try {
            sql.executeUpdate("ALTER TABLE logEntries ADD " + name + " " + type);
        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }

    //helper method to cut strings, can be public used
    /**
     * Cuts a String after n Chars
     *
     * @param s String to be cut
     * @param n Length of return String
     * @return
     */
    public static String cut(String s, int n) {
        return s.substring(0, Math.min(n, s.length()));
    }

    /**
     * adds a query with its player to the thread-queue SQLHandlerThread is
     * responsible for the answer
     *
     * @param packet must be a packet containing the Query Variables
     * @param player the player who gets the resultSet as a packet
     */
    public static void addQuery(Packet250CustomPayload packet, Player player) {
        if (thread == null || !Server.SQL.enabled) {
            return;
        }

        thread.addQuery(packet, player);
    }

    /**
     * adds a Event to the thread-queue SQLHandlerThread is responsible to log
     * the event
     *
     * @param event the event to log
     */
    public static void log(LogEvent event) {
        if (thread == null || !Server.SQL.enabled) {
            return;
        }

        thread.addEvent(event);
    }

}
