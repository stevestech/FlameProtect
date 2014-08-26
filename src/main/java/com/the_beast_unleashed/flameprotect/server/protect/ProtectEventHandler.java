/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.protect;

import com.the_beast_unleashed.flameprotect.FlameProtectLogger;
import com.the_beast_unleashed.flameprotect.server.PermissionManager;
import com.the_beast_unleashed.flameprotect.server.PermissionManagerException;
import com.the_beast_unleashed.flameprotect.server.ServerConfigHandler;
import com.sperion.forgeperms.api.IPermissionManager;

import cpw.mods.fml.common.FMLLog;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import static net.minecraftforge.event.EventPriority.HIGHEST;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 *
 * @author Anedaar
 */
public class ProtectEventHandler {

    private static final String PICKUP = "pickup";
    private static final String INTERACT = "interact";
    private static final String ATTACK = "attack";
    
    private Map<String, Map<String, Date>> chatWarningCooldown;
    // Map<String username, Map<String blockedAction, Date lastChatWarning>>    

    public IPermissionManager permManager;
    

    public ProtectEventHandler() {
    	chatWarningCooldown = new HashMap<String, Map<String, Date>>();
    }
    
       
    private boolean readyForNextWarning(EntityPlayer entityPlayer, String blockedAction, int cooldownSeconds) {
    	// Has the cooldown had time to complete since the last chat warning for the given player and action?
    	
    	Calendar currentTime = Calendar.getInstance();
    	Calendar lastChatWarning = Calendar.getInstance();
    	
    	// Perform any necessary initialisations
    	if (!chatWarningCooldown.containsKey(entityPlayer.username)) {
    		chatWarningCooldown.put(entityPlayer.username, new HashMap<String, Date>());
    	}
    	
    	if (!chatWarningCooldown.get(entityPlayer.username).containsKey(blockedAction)) {
    		chatWarningCooldown.get(entityPlayer.username).put(blockedAction, new Date(0));
    	}
    		
		currentTime.setTime(new Date());
		lastChatWarning.setTime(chatWarningCooldown.get(entityPlayer.username).get(blockedAction));
		lastChatWarning.add(Calendar.SECOND, cooldownSeconds);
		
		if (currentTime.after(lastChatWarning)) {   			
			chatWarningCooldown.get(entityPlayer.username).put(blockedAction, new Date());
			return true;    				
		} else {
			return false;
		}	
		
    }
    

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityItemPickup(EntityItemPickupEvent event) {
    	boolean canPickup;    	
    	
    	if (ServerConfigHandler.EnabledModules.protection) {
	    	try {
	    		canPickup = PermissionManager.hasPermission(event.entityPlayer, ServerConfigHandler.Perms.PICKUP);
	    	} catch (PermissionManagerException ex) {
	    		canPickup = true;
	    		ServerConfigHandler.EnabledModules.protection = false;
	    		FlameProtectLogger.getLogger().log(Level.WARNING, ServerConfigHandler.Lang.noPermissionManager);
	    	}
	
	    	if (event.isCancelable() && !canPickup) {	    		
	        	event.setCanceled(true);
	            
	            if (readyForNextWarning(event.entityPlayer, PICKUP, 10)) {
	                event.entityPlayer.addChatMessage(ServerConfigHandler.Lang.noPickup);
	            }	            
	        }
    	}    	
    }
    

    @ForgeSubscribe(priority = HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	boolean canInteract;
 	
        if (ServerConfigHandler.EnabledModules.protection) {
	    	try {
	    		canInteract = PermissionManager.hasPermission(event.entityPlayer, ServerConfigHandler.Perms.INTERACT);
	    	} catch (PermissionManagerException ex) {
	    		canInteract = true;
	    		ServerConfigHandler.EnabledModules.protection = false;
	    		FlameProtectLogger.getLogger().log(Level.WARNING, ServerConfigHandler.Lang.noPermissionManager);
	    	}
        	        	
	    	if (event.isCancelable() && !canInteract) {	    		
	        	event.setCanceled(true);
	            
	            if (readyForNextWarning(event.entityPlayer, INTERACT, 10)) {
	                event.entityPlayer.addChatMessage(ServerConfigHandler.Lang.noInteract);
	            }	            
	        }           
        }        
    }
    
    
	// Covers all damage sources
    @ForgeSubscribe(priority = HIGHEST)
    public void onLivingAttack(LivingAttackEvent event) {
    	boolean isInvulnerable;
    	
    	if (ServerConfigHandler.EnabledModules.protection && event.entityLiving instanceof EntityPlayer) {
	    	try {
	    		isInvulnerable = PermissionManager.hasPermission((EntityPlayer) event.entityLiving, ServerConfigHandler.Perms.INVULNERABLE);
	    	} catch (PermissionManagerException ex) {
	    		isInvulnerable = false;
	    		ServerConfigHandler.EnabledModules.protection = false;
	    		FlameProtectLogger.getLogger().log(Level.WARNING, ServerConfigHandler.Lang.noPermissionManager);
	    	}
	    	
	    	if (isInvulnerable && event.isCancelable()) {
	    		event.setCanceled(true);
	    	}
	    	
	    	if (isInvulnerable && event.source.getEntity() instanceof EntityLiving) {
	    		event.source.getEntity().setDead();
	    	}
    	}    	
    }
    
    
    @ForgeSubscribe(priority = HIGHEST)
    public void onPlayerAttackEntity(AttackEntityEvent event) {
    	boolean canAttack;
    	
    	if (ServerConfigHandler.EnabledModules.protection) {
	    	try {
	    		canAttack = PermissionManager.hasPermission(event.entityPlayer, ServerConfigHandler.Perms.ATTACK);
	    	} catch (PermissionManagerException ex) {
	    		canAttack = true;
	    		ServerConfigHandler.EnabledModules.protection = false;
	    		FlameProtectLogger.getLogger().log(Level.WARNING, ServerConfigHandler.Lang.noPermissionManager);
	    	}
	    	
	    	if (!canAttack && event.isCancelable()) {
	    		event.setCanceled(true);
	    		
	            if (readyForNextWarning(event.entityPlayer, ATTACK, 10)) {
	                event.entityPlayer.addChatMessage(ServerConfigHandler.Lang.noAttack);
	            }  
	    	}
    	}
    }
    
    
    @ForgeSubscribe(priority = HIGHEST)
    public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
    	boolean isInvulnerable;
    	
    	if (ServerConfigHandler.EnabledModules.protection && event.target instanceof EntityPlayer) {
	    	try {
	    		isInvulnerable = PermissionManager.hasPermission((EntityPlayer) event.target, ServerConfigHandler.Perms.INVULNERABLE);
	    	} catch (PermissionManagerException ex) {
	    		isInvulnerable = false;
	    		ServerConfigHandler.EnabledModules.protection = false;
	    		FlameProtectLogger.getLogger().log(Level.WARNING, ServerConfigHandler.Lang.noPermissionManager);
	    	}
	    	
	    	if (isInvulnerable) {
	    		event.entityLiving.setDead();
	    	}
    	}
    }    
}
