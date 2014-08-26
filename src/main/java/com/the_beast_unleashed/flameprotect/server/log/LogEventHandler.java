/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.log;

import java.util.HashMap;
import java.util.logging.Level;

import com.the_beast_unleashed.flameprotect.server.ServerConfigHandler;

import net.minecraft.block.Block;
import net.minecraftforge.event.Event;
import static net.minecraftforge.event.EventPriority.HIGHEST;
import static net.minecraftforge.event.EventPriority.LOWEST;
import net.minecraftforge.event.ForgeSubscribe;
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

    @ForgeSubscribe(priority = LOWEST)
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
            log.targetID = event.block.blockID;
        
        log.targetDamage = event.blockMetadata;
        log.targetN = 1;

        log.action = "break";

        EventLogger.log(log, ServerConfigHandler.Log.blockBreak);

    }

    @ForgeSubscribe(priority = LOWEST)
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
                log.targetID = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);
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
            } else if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == 0) {
                EventLogger.log(Level.SEVERE, "BlockID is 0");
            } else if (Block.blocksList[event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z)] == null) {
                EventLogger.log(Level.SEVERE, "Block is 0, ID is " + event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z));
            }

            try {
                log.targetID = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);
                log.targetDamage = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
                log.targetN = 1;
            } catch (NullPointerException e) {
                EventLogger.log(Level.SEVERE, e.getStackTrace().toString());
            }

            log.action = "hit";

            EventLogger.log(log, ServerConfigHandler.Log.leftBlock);
        }

    }

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (!ServerConfigHandler.EnabledModules.loggingSQL
        		&& !ServerConfigHandler.EnabledModules.loggingConsole)
        	return;

        LogEvent log = new LogEvent();

        log.setPosition(event.entityPlayer);
        log.world = event.entityPlayer.worldObj.getWorldInfo().getWorldName() + "/"
                + event.entityPlayer.dimension;

        log.setSource(event.entityPlayer);

        log.targetID = event.item.getEntityItem().itemID;
        log.targetDamage = event.item.getEntityItem().getItemDamage();
        log.targetN = event.item.getEntityItem().stackSize;

        log.action = "pickup";

        eventMap.put(event, log);
    }

    @ForgeSubscribe(priority = LOWEST, receiveCanceled = true)
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
