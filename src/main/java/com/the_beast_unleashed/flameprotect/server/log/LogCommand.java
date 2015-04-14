/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.log;

import com.the_beast_unleashed.flameprotect.FlameProtectLogger;
import com.the_beast_unleashed.flameprotect.server.ServerConfigHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentText;

/**
 *
 * @author Anedaar
 */
public class LogCommand implements ICommand {

    @Override
    public String getCommandName() {
        return ServerConfigHandler.Lang.logCmd;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/" +  ServerConfigHandler.Lang.logCmd + " help";
    }

    @Override
    public List getCommandAliases() {
        ArrayList list = new ArrayList();
        list.add( ServerConfigHandler.Lang.logCmd);

        return list;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 0) {
            //general help
            icommandsender.addChatMessage(new ChatComponentText("Logging commands:\n"
                    + "/" + ServerConfigHandler.Lang.logCmd + " sql [true/false]\n"
                    + "/" + ServerConfigHandler.Lang.logCmd + " subscribe <n>"));
        } else if ("sql".compareTo(astring[0]) == 0) {
            //sql command
            if (astring.length < 2
                    || ("true".compareTo(astring[1]) != 0 && "false".compareTo(astring[1]) != 0)) {
            	
                icommandsender.addChatMessage(new ChatComponentText(
                		"SQL-Logging: " + ServerConfigHandler.EnabledModules.loggingSQL + "\n"
                        + "/" + ServerConfigHandler.Lang.logCmd + " sql [true/false]"));
            } else {
            	ServerConfigHandler.EnabledModules.loggingSQL = Boolean.parseBoolean(astring[1]);
                icommandsender.addChatMessage(new ChatComponentText("SQL-Logging: " + ServerConfigHandler.EnabledModules.loggingSQL));
            }
        } else if ("console".compareTo(astring[0]) == 0) {
            //console log command
            if (astring.length < 2
                    || ("true".compareTo(astring[1]) != 0 && "false".compareTo(astring[1]) != 0)) {
            	
                icommandsender.addChatMessage(new ChatComponentText("Console-Logging: " + ServerConfigHandler.EnabledModules.loggingConsole + "\n"
                        + "/" + ServerConfigHandler.Lang.logCmd + " console [true/false]"));
            } else {
            	ServerConfigHandler.EnabledModules.loggingConsole = Boolean.parseBoolean(astring[1]);
                icommandsender.addChatMessage(new ChatComponentText("Console-Logging: " + ServerConfigHandler.EnabledModules.loggingConsole));
            }
        } else if ("subscribe".compareTo(astring[0]) == 0) {
            //subscribe command
            if (astring.length > 2) {
                try {
                    Byte n = Byte.parseByte(astring[1]);
                    EventLogger.subscribe(icommandsender, n);
                    return;
                } catch (NumberFormatException ex) {
                	FlameProtectLogger.getLogger().log(Level.WARNING, null, ex);
                }
            }
            
            icommandsender.addChatMessage(new ChatComponentText("/" + ServerConfigHandler.Lang.logCmd + " subscribe <n>"));

        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        if (icommandsender instanceof EntityPlayer) {
        	// Is player opped?
        	if(MinecraftServer.getServer().getConfigurationManager().func_152596_g(((EntityPlayer) icommandsender).getGameProfile())) {
        		return true;
        	}
        }
        	
        return false;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isUsernameIndex(String[] astring, int i) {
        return false;
    }

    public int compareTo(ICommand par1ICommand) {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    @Override
    public int compareTo(Object par1Obj) {
        return this.compareTo((ICommand) par1Obj);
    }

}
