/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.server.log;

import flamefeed.server.Server;
import java.util.logging.Level;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
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

    @ForgeSubscribe(priority = LOWEST)
    public void onBlockBreak(BreakEvent event) {
        if (event.world.isRemote || (!Server.Log.console && !Server.SQL.enabled)) {
            return;
        }

        LogEvent log = new LogEvent();

        log.x = event.x;
        log.y = event.y;
        log.z = event.z;
        log.world = event.world.getWorldInfo().getWorldName() + "/"
                + event.getPlayer().dimension;

        log.source = event.getPlayer().getEntityName();
        log.target = event.block.getLocalizedName();
        log.action = "break";

        EventLogger.log(log, Server.Log.blockBreak);

    }

    @ForgeSubscribe(priority = LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.entityPlayer.worldObj.isRemote || (!Server.Log.console && !Server.SQL.enabled)) {
            return;
        }

        LogEvent log = new LogEvent();

        log.source = event.entityPlayer.getEntityName();
        log.x = event.x;
        log.y = event.y;
        log.z = event.z;
        log.world = event.entityPlayer.worldObj.getWorldInfo().getWorldName() + "/"
                + event.entityPlayer.dimension;

        ItemStack toolStack = event.entityPlayer.getCurrentEquippedItem();
        if (toolStack == null) {
            log.tool = "hands";
        } else {
            log.tool = event.entityPlayer.getCurrentEquippedItem().getUnlocalizedName();
        }

        if (event.action == Action.RIGHT_CLICK_AIR) { //Use Item

            log.target = "air";
            log.action = "use";

            log.x = (int) event.entityPlayer.posX;
            log.y = (int) event.entityPlayer.posY;
            log.z = (int) event.entityPlayer.posZ;

            EventLogger.log(log, Server.Log.rightAir);

        } else if (event.action == Action.RIGHT_CLICK_BLOCK) { //Use Item on Block

            try {
                Block targetBlock = Block.blocksList[event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z)];
                log.target = targetBlock.getLocalizedName();
            } catch (NullPointerException e) {
                EventLogger.log(Level.SEVERE, e.getStackTrace().toString());
                log.target = "NPE";
            }

            if (false) { //Try to determine if it gets placed/used/accessed
                log.action = "place";
            } else {
                log.action = "use";
            }

            EventLogger.log(log, Server.Log.rightBlock);

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
                log.target = Block.blocksList[event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z)].getLocalizedName();
            } catch (NullPointerException e) {
                EventLogger.log(Level.SEVERE, e.getStackTrace().toString());
                log.target = "NPE";
            }

            log.action = "hit";

            EventLogger.log(log, Server.Log.leftBlock);
        }

    }

    @ForgeSubscribe(priority = LOWEST)
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (event.entityPlayer.worldObj.isRemote || (!Server.Log.console && !Server.SQL.enabled)) {
            return;
        }

        LogEvent log = new LogEvent();

        log.x = (int) event.entityPlayer.posX;
        log.y = (int) event.entityPlayer.posY;
        log.z = (int) event.entityPlayer.posZ;
        log.world = event.entityPlayer.worldObj.getWorldInfo().getWorldName() + "/"
                + event.entityPlayer.dimension;

        log.source = event.entityPlayer.getEntityName();
        log.target = event.item.getEntityName();
        log.action = "pickup";

        EventLogger.log(log, Server.Log.pickup);
    }

}
