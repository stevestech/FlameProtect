/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.server.log;

import flamefeed.server.Server;
import java.util.logging.Level;
import java.util.logging.Logger;
import cpw.mods.fml.common.FMLLog;
import flamefeed.server.SQLHandler;
import java.util.HashMap;
import net.minecraft.command.ICommandSender;

/**
 *
 * @author Anedaar
 */
public class EventLogger {

    private static final Logger logger = Logger.getLogger("FlameProtect");

    private static final HashMap<ICommandSender, Byte> subscribers = new HashMap();

    public static void init() {
        logger.setParent(FMLLog.getLogger());
        if (Server.SQL.enabled) {
            SQLHandler.init();
        }
    }


    public static void log(Level logLevel, String message) {
        logger.log(logLevel, message);
    }

    public static void log(LogEvent e, byte output) {

        if ((output & 1) != 0) {
            logger.log(Level.INFO, "Logged Event: {0}, Source: {1}, Target: {2}, Tool: {3},World: {4}, Coordinates: {5}, {6}, {7}",
                    new Object[]{e.action, e.source, e.target, e.tool, e.world, e.x, e.y, e.z});
        }

        if ((output & 2) != 0 && Server.SQL.enabled) {
            SQLHandler.log(e);
        }
//        
//        for (Map.Entry<ICommandSender, Byte> subscriber : subscribers.entrySet()) {
//            if ((output & subscriber.getValue()) != 0) {
//                subscriber.getKey().sendChatToPlayer(ChatMessageComponent.createFromText("LOG! "
//                        + "Time: " + sdf.format(e.time)
//                        + "X: " + e.x
//                        + "Y: " + e.y
//                        + "Z: " + e.z
//                        + "Source: " + e.source
//                        + "Target: " + e.target
//                        + "Action: " + e.action
//                        + "Tool: " + e.tool));
//            }
//        }
    }


    public static void subscribe(ICommandSender sender, Byte n) {
        subscribers.put(sender, n);
    }
}
