/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.server.protect;

import flamefeed.server.Server;
import forgeperms.api.IPermissionManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.entity.player.EntityPlayer;
import static net.minecraftforge.event.EventPriority.HIGHEST;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

/**
 *
 * @author Anedaar
 */
public class ProtectEventHandler {

    private static String PICKUP = "pickup";
    private static String INTERACT = "interact";
    
    public IPermissionManager permManager;

    public ProtectEventHandler() {
        try {
            Class<?> forgePerms = Class.forName("forgeperms.ForgePerms");
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
        if (Server.enabled == false || player.worldObj.isRemote) {
            return true;
        }
        
        if (permManager==null) {
        try {
            Class<?> forgePerms = Class.forName("forgeperms.ForgePerms");
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
        
        if(permManager!=null){
        try {
            return permManager.canAccess(player.username, "world", "modifyworld."+event);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        else{
             Logger.getLogger(ProtectEventHandler.class.getName()).log(Level.SEVERE, null, "could not initialize permManager");
        }

        return false;
    }

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (!hasPermission(event.entityPlayer, PICKUP)) {
            event.setCanceled(true);
            event.entityPlayer.addChatMessage(Server.noPickup);
        }
    }

    @ForgeSubscribe(priority = HIGHEST)
    public void onEntityInteract(PlayerInteractEvent event) {
        if (!hasPermission(event.entityPlayer, INTERACT)) {
            event.setCanceled(true);
            event.entityPlayer.addChatMessage(Server.noInteract);
        }

    }
}
