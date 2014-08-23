/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.log;

import com.the_beast_unleashed.flameprotect.server.Server;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatMessageComponent;

/**
 *
 * @author Anedaar
 */
public class LogCommand implements ICommand {

    @Override
    public String getCommandName() {
        return Server.logCmd;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/" + Server.logCmd + " help";
    }

    @Override
    public List getCommandAliases() {
        ArrayList list = new ArrayList();
        list.add(Server.logCmd);

        return list;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 0) {
            //general help
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Logging commands:\n"
                    + "/" + Server.logCmd + " sql [true/false]\n"
                    + "/" + Server.logCmd + " subscribe <n>"));
        } else if ("sql".compareTo(astring[0]) == 0) {
            //sql command
            if (astring.length < 2
                    || ("true".compareTo(astring[1]) != 0 && "false".compareTo(astring[1]) != 0)) {
                icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("SQL-Logging: " + Server.SQL.enabled + "\n"
                        + "/" + Server.logCmd + " sql [true/false]"));
            } else {
                Server.SQL.enabled = Boolean.parseBoolean(astring[1]);
                icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("SQL-Logging: " + Server.SQL.enabled + ""));
            }
        } else if ("console".compareTo(astring[0]) == 0) {
            //console log command
            if (astring.length < 2
                    || ("true".compareTo(astring[1]) != 0 && "false".compareTo(astring[1]) != 0)) {
                icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Console-Logging: " + Server.Log.console + "\n"
                        + "/" + Server.logCmd + " console [true/false]"));
            } else {
                Server.Log.console = Boolean.parseBoolean(astring[1]);
                icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Console-Logging: " + Server.Log.console + ""));
            }
        } else if ("subscribe".compareTo(astring[0]) == 0) {
            //subscribe command
            if (astring.length > 2) {
                try {
                    Byte n = Byte.parseByte(astring[1]);
                    EventLogger.subscribe(icommandsender, n);
                    return;
                } catch (NumberFormatException numberFormatException) {
                }
            }
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("/" + Server.logCmd + " subscribe <n>"));

        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        if (MinecraftServer.getServer().isSinglePlayer())
        	return true;
        if (icommandsender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) icommandsender;
            return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getOps().contains(player.username.toLowerCase().trim());
        } else {
            return !(icommandsender instanceof TileEntityCommandBlock);
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
