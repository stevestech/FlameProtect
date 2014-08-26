package com.the_beast_unleashed.flameprotect.server.protect;

import java.util.EnumSet;
import java.util.logging.Level;

import com.the_beast_unleashed.flameprotect.FlameProtectLogger;
import com.the_beast_unleashed.flameprotect.server.PermissionManager;
import com.the_beast_unleashed.flameprotect.server.PermissionManagerException;
import com.the_beast_unleashed.flameprotect.server.ServerConfigHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class ProtectTickHandler implements IScheduledTickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		boolean canFeed;
		
		if (ServerConfigHandler.EnabledModules.protection
				&& type.contains(TickType.PLAYER)
				&& tickData.length > 0
				&& tickData[0] instanceof EntityPlayer) {
			
	    	try {
	    		canFeed = PermissionManager.hasPermission((EntityPlayer) tickData[0], ServerConfigHandler.Perms.FEED);
	    	} catch (PermissionManagerException ex) {
	    		canFeed = false;
	    		ServerConfigHandler.EnabledModules.protection = false;
	    		FlameProtectLogger.getLogger().log(Level.WARNING, ServerConfigHandler.Lang.noPermissionManager);
	    	}				
			
			if (canFeed
					&& ((EntityPlayer) tickData[0]).getFoodStats().needFood()) {
				
				((EntityPlayer) tickData[0]).getFoodStats().addStats(20, 5.0F);
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "FlameProtect player protection tick-handler";
	}

	@Override
	public int nextTickSpacing() {
		// Schedule for every 200 ticks ~ 10 seconds
		return 200;
	}

}
