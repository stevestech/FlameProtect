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
import java.util.HashMap;
import java.util.Map;
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
    
    private Map<String, Map<String, Date>> chatWarningCooldown;
    // Map<String username, Map<String blockedAction, Date lastChatWarning>>    

    public IPermissionManager permManager;
    

    public ProtectEventHandler() {
    	chatWarningCooldown = new HashMap<String, Map<String, Date>>();
    	
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
    
    
    private boolean readyForNextWarning(EntityPlayer entityPlayer, String blockedAction, int cooldownSeconds) {
    	// Has the cooldown had time to complete since the last chat warning for the given player and action?
    	
    	String username = entityPlayer.username.toLowerCase().trim();
    	Calendar currentTime = Calendar.getInstance();
    	Calendar lastChatWarning = Calendar.getInstance();
    	
    	// Perform any necessary initialisations
    	if (!chatWarningCooldown.containsKey(username)) {
    		chatWarningCooldown.put(username, new HashMap<String, Date>());
    	}
    	
    	if (!chatWarningCooldown.get(username).containsKey(blockedAction)) {
    		chatWarningCooldown.get(username).put(blockedAction, new Date(0));
    	}
    		
		currentTime.setTime(new Date());
		lastChatWarning.setTime(chatWarningCooldown.get(username).get(blockedAction));
		lastChatWarning.add(Calendar.SECOND, cooldownSeconds);
		
		if (currentTime.after(lastChatWarning)) {   			
			chatWarningCooldown.get(username).put(blockedAction, new Date());
			return true;    				
		} else {
			return false;
		}	
		
    }
    

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityItemPickup(EntityItemPickupEvent event) {

    	if (!hasPermission(event.entityPlayer, PICKUP)) {
            event.setCanceled(true);           
            
            if (readyForNextWarning(event.entityPlayer, PICKUP, 10)) {
                event.entityPlayer.addChatMessage(Server.noPickup);
            }
            
        }
    	
    }
    

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityInteract(PlayerInteractEvent event) {
 	
        if (!hasPermission(event.entityPlayer, INTERACT)) {        	        	
            event.setCanceled(true);
            
            if (readyForNextWarning(event.entityPlayer, INTERACT, 10)) {
            	event.entityPlayer.addChatMessage(Server.noInteract);
            }
            
        }
        
    }
    
}
