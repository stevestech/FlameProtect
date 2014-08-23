/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.protect;

import com.the_beast_unleashed.flameprotect.server.Server;
import com.sperion.forgeperms.api.IPermissionManager;

import cpw.mods.fml.common.FMLLog;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import static net.minecraftforge.event.EventPriority.HIGHEST;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 *
 * @author Anedaar
 */
public class ProtectEventHandler {

    private static final String PICKUP = "pickup";
    private static final String INTERACT = "interact";
    
    private static Date lastChatWarningDate;

    public IPermissionManager permManager;

    public ProtectEventHandler() {
    	lastChatWarningDate = new Date(0);    	
    	
        try {
            Class<?> forgePerms = Class.forName("com.sperion.forgeperms.ForgePerms");
            permManager = (IPermissionManager) forgePerms.getMethod("getPermissionManager").invoke(null);
        } catch (ClassNotFoundException ex) {
        	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
       	} catch (NoSuchMethodException ex) {
        	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
       	} catch (SecurityException ex) {
        	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
       	} catch (IllegalAccessException ex) {
        	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
       	} catch (IllegalArgumentException ex) {
        	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
       	} catch (InvocationTargetException ex) {
        	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
       	}
    }
    

    private boolean hasPermission(EntityPlayer player, String event) {
        if (Server.enabled == false || MinecraftServer.getServer().isSinglePlayer()) {
            return true;
        }

        if (permManager == null) {
            try {
                Class<?> forgePerms = Class.forName("com.sperion.forgeperms.ForgePerms");
                permManager = (IPermissionManager) forgePerms.getMethod("getPermissionManager").invoke(null);
            } catch (ClassNotFoundException ex) {
            	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
           	} catch (NoSuchMethodException ex) {
            	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
           	} catch (SecurityException ex) {
            	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
           	} catch (IllegalAccessException ex) {
            	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
           	} catch (IllegalArgumentException ex) {
            	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
           	} catch (InvocationTargetException ex) {
            	Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
           	}
        }

        if (permManager != null) {
            try {
                return permManager.canAccess(player.username, "world", "modifyworld." + event);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, "could not initialize permManager");
        }

        return false;
    }
    
    
    private boolean readyForNextWarning() {
    	// Have more than 10 seconds passed since the last chat warning?
    	
    	Calendar currentTime = Calendar.getInstance();
    	Calendar lastChatWarningCalendar = Calendar.getInstance();
        currentTime.setTime(new Date());
        lastChatWarningCalendar.setTime(lastChatWarningDate);
        lastChatWarningCalendar.add(Calendar.SECOND, 10);
        return currentTime.after(lastChatWarningCalendar);
    }
    

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityItemPickup(EntityItemPickupEvent event) {

    	if (!hasPermission(event.entityPlayer, PICKUP)) {
            event.setCanceled(true);           
            if (readyForNextWarning()) {
                event.entityPlayer.addChatMessage(Server.noPickup);
            	lastChatWarningDate = new Date();
            }
            
        }
    	
    }
    

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityInteract(PlayerInteractEvent event) {
 	
        if (!hasPermission(event.entityPlayer, INTERACT)) {        	        	
            event.setCanceled(true);
            if (readyForNextWarning()) {
            	event.entityPlayer.addChatMessage(Server.noInteract);
            	lastChatWarningDate = new Date();
            }
            
        }
        
    }
    
}
