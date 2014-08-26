/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.protect;

import com.the_beast_unleashed.flameprotect.server.PermissionManager;
import com.the_beast_unleashed.flameprotect.server.PermissionManagerException;
import com.the_beast_unleashed.flameprotect.server.ServerConfigHandler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

/**
 *
 * @author Anedaar
 */
public class ProtectCommand implements ICommand {

    public ProtectCommand() {
    }

    @Override
    public String getCommandName() {
        return ServerConfigHandler.Lang.protCmd;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/" + ServerConfigHandler.Lang.protCmd;
    }

    @Override
    public List getCommandAliases() {
        ArrayList list = new ArrayList();
        list.add(ServerConfigHandler.Lang.protCmd);

        return list;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        ServerConfigHandler.EnabledModules.protection = !ServerConfigHandler.EnabledModules.protection;
        
        icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText(
        		"Protection: " + Boolean.toString(ServerConfigHandler.EnabledModules.protection))
        );
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        if (icommandsender instanceof EntityPlayer) {
        	try {
        		return PermissionManager.hasPermission((EntityPlayer) icommandsender, ServerConfigHandler.Perms.ADMIN);
        	} catch (PermissionManagerException ex) {
        		icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText(ServerConfigHandler.Lang.noPermissionManager));
        	}
        }
        	
        return false;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isUsernameIndex(String[] astring, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int compareTo(ICommand par1ICommand) {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    @Override
    public int compareTo(Object par1Obj) {
        return this.compareTo((ICommand) par1Obj);
    }

}
