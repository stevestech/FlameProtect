/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.log;

import java.util.HashMap;
import java.util.logging.Level;

import com.the_beast_unleashed.flameprotect.server.ServerConfigHandler;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

/**
 *
 * @author Anedaar
 */
public class LogEventHandler {

    private HashMap<Event, LogEvent> eventMap = new HashMap();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BreakEvent event) {
        if (!ServerConfigHandler.EnabledModules.loggingSQL
        		&& !ServerConfigHandler.EnabledModules.loggingConsole)
        	return;
        
        LogEvent log = new LogEvent();
        log.setPosition(event.x, event.y, event.z);

        if (event.getPlayer() != null) {
            log.world = event.world.getWorldInfo().getWorldName() + "/"
                    + event.getPlayer().dimension;

            log.setSource(event.getPlayer());
        }

        if (event.block != null)
            log.targetName = event.block.getLocalizedName();
        
        log.targetDamage = event.blockMetadata;
        log.targetN = 1;

        log.action = "break";

        EventLogger.log(log, ServerConfigHandler.Log.blockBreak);

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!ServerConfigHandler.EnabledModules.loggingSQL
        		&& !ServerConfigHandler.EnabledModules.loggingConsole)
        	return;

        LogEvent log = new LogEvent();

        log.setSource(event.entityPlayer);
        log.setPosition(event.x, event.y, event.z);
        log.world = event.entityPlayer.worldObj.getWorldInfo().getWorldName() + "/"
                + event.entityPlayer.dimension;

        if (event.action == Action.RIGHT_CLICK_AIR) { //Use Item

            log.action = "use";

            log.setPosition(event.entityPlayer);

            EventLogger.log(log, ServerConfigHandler.Log.rightAir);

        } else if (event.action == Action.RIGHT_CLICK_BLOCK) { //Use Item on Block

            try {
                log.targetName = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z).getLocalizedName();
                log.targetDamage = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
                log.targetN = 1;
            } catch (NullPointerException e) {
                EventLogger.log(Level.SEVERE, e.getStackTrace().toString());
            }

//            if (false) { //Try to determine if it gets placed/used/accessed
//                log.action = "place";
//            } else {
//                log.action = "use";
//            }
            log.action = "use";

            EventLogger.log(log, ServerConfigHandler.Log.rightBlock);

        } else if (event.action == Action.LEFT_CLICK_BLOCK) { //Hit Block

            if (event.entityPlayer == null) {
                EventLogger.log(Level.SEVERE, "entityPlayer is NULL");
            } else if (event.entityPlayer.worldObj == null) {
                EventLogger.log(Level.SEVERE, "worldObj is NULL");
            } else if (event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) == null) {
                EventLogger.log(Level.SEVERE, "Block object is NULL");
            }

            try {
                log.targetName = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z).getLocalizedName();
                log.targetDamage = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
                log.targetN = 1;
            } catch (NullPointerException e) {
                EventLogger.log(Level.SEVERE, e.getStackTrace().toString());
            }

            log.action = "hit";

            EventLogger.log(log, ServerConfigHandler.Log.leftBlock);
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (!ServerConfigHandler.EnabledModules.loggingSQL
        		&& !ServerConfigHandler.EnabledModules.loggingConsole)
        	return;

        LogEvent log = new LogEvent();

        log.setPosition(event.entityPlayer);
        log.world = event.entityPlayer.worldObj.getWorldInfo().getWorldName() + "/"
                + event.entityPlayer.dimension;

        log.setSource(event.entityPlayer);

        log.targetName = event.item.getEntityItem().getDisplayName();
        log.targetDamage = event.item.getEntityItem().getItemDamage();
        log.targetN = event.item.getEntityItem().stackSize;

        log.action = "pickup";

        eventMap.put(event, log);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onEarlyEntityItemPickup(EntityItemPickupEvent event) {
        LogEvent log = eventMap.get(event);

        if (log != null && !event.isCanceled()) {
            EventLogger.log(log, ServerConfigHandler.Log.pickup);
        }
        
        eventMap.remove(event);
    }

    //TODO:
    //Implement:
    //ItemTossEvent
    //LivingAttackEvent/LivingHurtEvent
    //LivingDeathEvent
}
