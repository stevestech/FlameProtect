/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.the_beast_unleashed.flameprotect.server;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 *
 * @author Anedaar
 */

public class ServerConfigHandler {
	public static class Lang {
	    public static String whatCmd;
	    public static String whatCmd_default="what";	
	    public static String logCmd;
	    public static String logCmd_default="log";
	}
	
    public static class SQL {
        public static String host;
        public static int port;
        public static String user;
        public static String pw;
        public static String database;
    }
    
    public static class Log {
        public static byte blockBreak;
        public static byte leftBlock;
        public static byte rightBlock;
        public static byte rightAir;
        public static byte pickup;
    }
    
    public static class EnabledModules {
    	public static boolean loggingSQL;
    	public static boolean loggingConsole;
    }
    
	public static void init(File configFile) {
		Configuration FConfig = new Configuration(configFile);


		FConfig.load();
		
		//what Command
		
		Lang.whatCmd = FConfig.get(Configuration.CATEGORY_GENERAL, "whatCommand", Lang.whatCmd_default,
		        "Displays informations about the current held item.").getString();
		Lang.logCmd = FConfig.get(Configuration.CATEGORY_GENERAL, "logCommand", Lang.logCmd_default,
		        "This command is used to enable/disable logging to console or SQL.").getString();
		
		
		
		//SQL
		String sqlCategory = "SQL";
		FConfig.addCustomCategoryComment(sqlCategory, "General SQL Settings");
		
		EnabledModules.loggingSQL = FConfig.get(sqlCategory, "enabled", true).getBoolean(true);
		SQL.host = FConfig.get(sqlCategory, "hostname", "localhost").getString();
		SQL.port = FConfig.get(sqlCategory, "port", 3306).getInt();
		SQL.user = FConfig.get(sqlCategory, "username", "admin").getString();
		SQL.pw = FConfig.get(sqlCategory, "password", "").getString();
		SQL.database = FConfig.get(sqlCategory, "database", "exampleDatabase").getString();
		
		
		//Logging
		String logCategory = "Logging";
		FConfig.addCustomCategoryComment(logCategory, "Enables/Disables logging of certain Events. 0=OFF, 1=Console, 2=SQL, 3=Both");
		
		EnabledModules.loggingConsole = FConfig.get(logCategory, "consoleLog", true).getBoolean(true);
		Log.blockBreak = (byte)FConfig.get(logCategory, "BlockBreak", 3).getInt();
		Log.leftBlock = (byte)FConfig.get(logCategory, "LeftClickBlock", 3).getInt();
		Log.rightBlock = (byte)FConfig.get(logCategory, "RightClickBlock", 3).getInt();
		Log.rightAir = (byte)FConfig.get(logCategory, "RightClickAir", 3).getInt();
		Log.pickup = (byte)FConfig.get(logCategory, "PickupItem", 3).getInt();
		
		FConfig.save();
	}
}
