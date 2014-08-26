package com.the_beast_unleashed.flameprotect.server;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.sperion.forgeperms.api.IPermissionManager;
import com.the_beast_unleashed.flameprotect.FlameProtectLogger;
import com.the_beast_unleashed.flameprotect.server.protect.ProtectEventHandler;

public class PermissionManager {
	private static IPermissionManager permissionManager;
		
	private static void initPermissionManager() {
        try {
            Class<?> forgePerms = Class.forName("com.sperion.forgeperms.ForgePerms");
            permissionManager = (IPermissionManager) forgePerms.getMethod("getPermissionManager").invoke(null);
        } catch (Throwable ex) {
       	}       
	}
	
	
	public static boolean isOpped(EntityPlayer player) {
		// Includes checks for single player, including is player owner if opened to LAN
		return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).isPlayerOpped(player.username);
	}
	
	
    public static boolean hasPermission(EntityPlayer player, Permission perm) throws PermissionManagerException {
        if (permissionManager == null) {
            initPermissionManager();
        }
        
        if (permissionManager == null) {
        	throw new PermissionManagerException();
        }

        else {
            try {
                if (permissionManager.canAccess(player.username, String.valueOf(player.dimension), perm.getNode())) {
                	return true;
                }
            } catch (IllegalArgumentException ex) {
                FlameProtectLogger.getLogger().log(Level.WARNING, null, ex);
            }
        }
        
        if (perm.getDefaultForOps() && isOpped(player)) {
            return true;
        }
        
        return false;
    }	
}
