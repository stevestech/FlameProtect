/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.FlameProtect.src.server;

import java.io.File;
import net.minecraftforge.common.Configuration;

/**
 *
 * @author Anedaar
 */

public class ServerConfigHandler {
public static void init(File configFile) {
Configuration FConfig = new Configuration(configFile);


FConfig.load();

//what Command

Server.whatCmd = FConfig.get(Configuration.CATEGORY_GENERAL, "whatCommand", Server.whatCmd_default,
        "Displays informations about the current held item.").getString();


//Protection
String protCategory = "Protection";
FConfig.addCustomCategoryComment(protCategory, "World Protection Settings");

Server.protCmd = FConfig.get(protCategory, "protCommand", Server.protCmd_default,
        "This command is used to toggle the protection on/off").getString();
Server.enabled = FConfig.get(protCategory, "enabled", true).getBoolean(true);
Server.noPickup = FConfig.get(protCategory, "noPickup", Server.noPickup_default,
        "This message is displayed, if someone is not allowed to pick up items.").getString();
Server.noInteract = FConfig.get(protCategory, "noInteract", Server.noInteract_default,
        "This message is displayed, if someone is not allowed to interact with the world.").getString();


//SQL
String sqlCategory = "SQL";
FConfig.addCustomCategoryComment(sqlCategory, "General SQL Settings");

Server.SQL.enabled = FConfig.get(sqlCategory, "enabled", true).getBoolean(true);
Server.SQL.host = FConfig.get(sqlCategory, "hostname", "localhost").getString();
Server.SQL.user = FConfig.get(sqlCategory, "username", "admin").getString();
Server.SQL.pw = FConfig.get(sqlCategory, "password", "").getString();
Server.SQL.database = FConfig.get(sqlCategory, "database", "exampleDatabase").getString();
Server.logCmd = FConfig.get(protCategory, "logCommand", Server.logCmd_default,
        "This command is used to toggle the protection on/off").getString();


//Logging
String logCategory = "Logging";
FConfig.addCustomCategoryComment(logCategory, "Enables/Disables logging of certain Events. 0=OFF, 1=Console, 2=SQL, 3=Both");

Server.Log.console = FConfig.get(logCategory, "consoleLog", true).getBoolean(true);
Server.Log.blockBreak = (byte)FConfig.get(logCategory, "BlockBreak", 3).getInt();
Server.Log.leftBlock = (byte)FConfig.get(logCategory, "LeftClickBlock", 3).getInt();
Server.Log.rightBlock = (byte)FConfig.get(logCategory, "RightClickBlock", 3).getInt();
Server.Log.rightAir = (byte)FConfig.get(logCategory, "RightClickAir", 3).getInt();
Server.Log.pickup = (byte)FConfig.get(logCategory, "PickupItem", 3).getInt();

FConfig.save();
}
}
