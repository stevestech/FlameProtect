/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.the_beast_unleashed.flameprotect.FlameProtectLogger;
import com.the_beast_unleashed.flameprotect.server.log.EventLogger;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 *
 * @author Anedaar
 */
public class CommandFlameProtect implements ICommand {

    @Override
    public String getCommandName() {
        return ServerConfigHandler.Lang.flameProtectCmd;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/" + ServerConfigHandler.Lang.flameProtectCmd + " [log | what | users]";
    }

    @Override
    public List getCommandAliases() {
        ArrayList list = new ArrayList();
        list.add(ServerConfigHandler.Lang.flameProtectCmd);

        return list;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (!(icommandsender instanceof EntityPlayer) || astring == null) return;
        
        if(astring.length == 0) {
        	icommandsender.addChatMessage(new ChatComponentText(this.getCommandUsage(icommandsender)));
        }
        
        // "/fp log"
        else if("log".compareTo(astring[0].toLowerCase()) == 0) {
            
            if (astring.length == 1) {
                //log command usage
                icommandsender.addChatMessage(new ChatComponentText("Logging commands:\n"
                        + "/" + ServerConfigHandler.Lang.flameProtectCmd + " log sql [true | false]\n"
                        + "/" + ServerConfigHandler.Lang.flameProtectCmd + " log console [true | false]"));
            }
            
            // "/fp log sql"
            else if ("sql".compareTo(astring[1].toLowerCase()) == 0) {
                //sql command
                if (astring.length < 3
                        || ("true".compareTo(astring[2].toLowerCase()) != 0 && "false".compareTo(astring[2].toLowerCase()) != 0)) {
                	
                    icommandsender.addChatMessage(new ChatComponentText(
                    		"SQL-Logging: " + ServerConfigHandler.EnabledModules.loggingSQL + "\n"
                            + "/" + ServerConfigHandler.Lang.flameProtectCmd + " sql [true | false]"));
                }
                
                else {
                	ServerConfigHandler.EnabledModules.loggingSQL = Boolean.parseBoolean(astring[2]);
                    icommandsender.addChatMessage(new ChatComponentText("SQL-Logging: " + ServerConfigHandler.EnabledModules.loggingSQL));
                }                   
            }
            
            // "/fp log console"
            else if ("console".compareTo(astring[1].toLowerCase()) == 0) {
                //console command
                if (astring.length < 3
                        || ("true".compareTo(astring[1].toLowerCase()) != 0 && "false".compareTo(astring[1].toLowerCase()) != 0)) {
                    	
                    icommandsender.addChatMessage(new ChatComponentText("Console-Logging: " + ServerConfigHandler.EnabledModules.loggingConsole + "\n"
                            + "/" + ServerConfigHandler.Lang.flameProtectCmd + " console [true | false]"));                    
                }
                
                else {
                    ServerConfigHandler.EnabledModules.loggingConsole = Boolean.parseBoolean(astring[2]);
                    icommandsender.addChatMessage(new ChatComponentText("Console-Logging: " + ServerConfigHandler.EnabledModules.loggingConsole));
                }
            }
        }
        
        // "/fp what"
        else if("what".compareTo(astring[0].toLowerCase()) == 0) {
            EntityPlayer player = (EntityPlayer)icommandsender;
            
            ItemStack item = player.getCurrentEquippedItem();
            
            if (item == null) {
                icommandsender.addChatMessage(new ChatComponentText("Please use this command while holding an item."));
            }
            
            else {                                
                icommandsender.addChatMessage(new ChatComponentText("DisplayName: "+item.getDisplayName()));         
                icommandsender.addChatMessage(new ChatComponentText("UnlocalizedName: "+item.getUnlocalizedName()));
                icommandsender.addChatMessage(new ChatComponentText("NewStackDisplayName: "+
                        new ItemStack(item.getItem(),1,item.getItemDamage()).getDisplayName() ));
                
                icommandsender.addChatMessage(new ChatComponentText("Damage: "+item.getItemDamage()));
                icommandsender.addChatMessage(new ChatComponentText("StackSize: "+item.stackSize));
            }             
        }
        
        // "/fp users"
        else if("users".compareTo(astring[0].toLowerCase()) == 0) {
            List playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList; 
            String concatPlayerList = "";
            
            for (Object player : playerEntityList) {                
                if(player instanceof EntityPlayerMP) {
                    
                    concatPlayerList = concatPlayerList
                            + ((EntityPlayerMP) player).getUniqueID().toString()
                            + " "
                            + ((EntityPlayerMP) player).getDisplayName()
                            + "\n";
                    
                }                
            }
            
            icommandsender.addChatMessage(new ChatComponentText(concatPlayerList));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        return (icommandsender instanceof EntityPlayer);
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
