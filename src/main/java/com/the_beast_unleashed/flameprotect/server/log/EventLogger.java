/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.log;

import com.the_beast_unleashed.flameprotect.server.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

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

        if ((output & 1) != 0 && Server.Log.console) {
            logger.log(Level.INFO, 
                "Logged Event: [{0},{1},{2},{3}] {4}: {5} {6}x{7} ({8}) with {9}x{10} ({11})",
                    new Object[]{e.world, e.x, e.y, e.z, e.source, e.action,
                        e.targetN, e.getTargetID(), e.getTargetName(),
                        e.toolN, e.getToolID(), e.getToolName()});
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
