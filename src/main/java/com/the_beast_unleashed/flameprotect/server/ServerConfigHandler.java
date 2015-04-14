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
	    public static String protCmd;
	    public static String protCmd_default="prot";
	    public static String noPickup;
	    public static String noPickup_default="You are not allowed to pick up items here.";
	    public static String noInteract;
	    public static String noInteract_default="You are not allowed to interact here.";
	    public static String noAttack;
	    public static String noAttack_default="You are not allowed to attack entities here.";
	    
	    public static String noPermissionManager="Unable to initialise permission manager. "
	    		+ "Please ensure that Legobear154's ForgePerms mod is installed.";
	}
	
    public static class SQL {
        public static String host;
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
    	public static boolean protection;
    	public static boolean loggingSQL;
    	public static boolean loggingConsole;
    }
    
	public static void init(File configFile) {
		Configuration FConfig = new Configuration(configFile);


		FConfig.load();
		
		//what Command
		
		Lang.whatCmd = FConfig.get(Configuration.CATEGORY_GENERAL, "whatCommand", Lang.whatCmd_default,
		        "Displays informations about the current held item.").getString();
		
		
		//Protection
		String protCategory = "Protection";
		FConfig.addCustomCategoryComment(protCategory, "World Protection Settings");
		
		Lang.protCmd = FConfig.get(protCategory, "protCommand", Lang.protCmd_default,
		        "This command is used to toggle the protection on/off").getString();
		EnabledModules.protection = FConfig.get(protCategory, "enabled", true).getBoolean(true);
		Lang.noPickup = FConfig.get(protCategory, "noPickup", Lang.noPickup_default,
		        "This message is displayed, if someone is not allowed to pick up items.").getString();
		Lang.noInteract = FConfig.get(protCategory, "noInteract", Lang.noInteract_default,
		        "This message is displayed, if someone is not allowed to interact with the world.").getString();
		Lang.noAttack = FConfig.get(protCategory, "noAttack", Lang.noAttack_default,
				"This message is displayed, if someone is prevented from attacking an entity.").getString();
		
		
		//SQL
		String sqlCategory = "SQL";
		FConfig.addCustomCategoryComment(sqlCategory, "General SQL Settings");
		
		EnabledModules.loggingSQL = FConfig.get(sqlCategory, "enabled", true).getBoolean(true);
		SQL.host = FConfig.get(sqlCategory, "hostname", "localhost").getString();
		SQL.user = FConfig.get(sqlCategory, "username", "admin").getString();
		SQL.pw = FConfig.get(sqlCategory, "password", "").getString();
		SQL.database = FConfig.get(sqlCategory, "database", "exampleDatabase").getString();
		Lang.logCmd = FConfig.get(protCategory, "logCommand", Lang.logCmd_default,
		        "This command is used to toggle the protection on/off").getString();
		
		
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
