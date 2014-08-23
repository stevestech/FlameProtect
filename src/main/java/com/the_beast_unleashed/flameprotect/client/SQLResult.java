/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.client;

import com.the_beast_unleashed.flameprotect.PacketHandler;
import com.the_beast_unleashed.flameprotect.server.log.EventLogger;
import com.the_beast_unleashed.flameprotect.server.log.SQLHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anedaar
 */
public class SQLResult {

    public static class SQLResultRow extends HashMap<String,String>{

        public SQLResultRow(String[] str) {
            //str has format "key1=value1,key2=value2,key3=value3,"
            for (String kvPair : str){
                decodeValue(kvPair);
            }
        }
        
        private void decodeValue(String str){
            if (str==null || str.equals("")) return;
            
            String[] kv = str.split("=");
            if(kv.length<2) return;
            
            this.put(kv[0], kv[1]);
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
