/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.client;

import flamefeed.PacketHandler;
import flamefeed.server.log.SQLHandler;
import flamefeed.server.log.EventLogger;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anedaar
 */
public class SQLResult {

    public static class SQLResultRow {

        public String time;
        public String x;
        public String y;
        public String z;
        public String source;
        public String action;
        public String target;
        public String tool;

        public SQLResultRow(String[] str) {
            try {
                time = SQLHandler.cut(str[0].trim(),19);
                x = str[1].trim();
                y = str[2].trim();
                z = str[3].trim();
                source = SQLHandler.cut(str[4].trim(),10);
                action = str[5].trim();
                target = SQLHandler.cut(str[6].trim(),10);
                tool = SQLHandler.cut(str[7].trim(),10);
        EventLogger.log(Level.INFO, "decoded Event at time: " + time);
            } catch (Exception e) {
            }
        }
    }

    public static ArrayList<SQLResultRow> rows = null;

    public static void parseResult(byte[] stream) {

        rows = new ArrayList();
        String rawData[];

        try {
            rawData = new String(stream, "UTF-8").split(";");
        EventLogger.log(Level.INFO, rawData[0]);
            for (String rawData1 : rawData) {
                rows.add(new SQLResultRow(rawData1.split(",")));
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PacketHandler.class.getName()).log(Level.SEVERE, null, ex);
//        

        }

    }
}
