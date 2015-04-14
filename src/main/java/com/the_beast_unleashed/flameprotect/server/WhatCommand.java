/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

/**
 *
 * @author Anedaar
 */
public class WhatCommand implements ICommand {

    @Override
    public String getCommandName() {
        return ServerConfigHandler.Lang.whatCmd;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/" + ServerConfigHandler.Lang.whatCmd;
    }

    @Override
    public List getCommandAliases() {
        ArrayList list = new ArrayList();
        list.add(ServerConfigHandler.Lang.whatCmd);

        return list;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (!(icommandsender instanceof EntityPlayer)) return;
        
        EntityPlayer player = (EntityPlayer)icommandsender;
        
        ItemStack item = player.getCurrentEquippedItem();
        
        if (item != null) {
        	
        	icommandsender.addChatMessage(new ChatComponentText("DisplayName: "+item.getDisplayName()));         
            icommandsender.addChatMessage(new ChatComponentText("UnlocalizedName: "+item.getUnlocalizedName()));
            icommandsender.addChatMessage(new ChatComponentText("NewStackDisplayName: "+
                    new ItemStack(item.getItem(),1,item.getItemDamage()).getDisplayName() ));
            
            icommandsender.addChatMessage(new ChatComponentText("Damage: "+item.getItemDamage()));
            icommandsender.addChatMessage(new ChatComponentText("StackSize: "+item.stackSize));
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
