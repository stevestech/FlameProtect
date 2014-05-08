/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.FlameProtect.src.server;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;

/**
 *
 * @author Anedaar
 */
public class WhatCommand implements ICommand {

    @Override
    public String getCommandName() {
        return Server.whatCmd;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/" + Server.whatCmd;
    }

    @Override
    public List getCommandAliases() {
        ArrayList list = new ArrayList();
        list.add(Server.whatCmd);

        return list;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (!(icommandsender instanceof EntityPlayer)) return;
        
        EntityPlayer player = (EntityPlayer)icommandsender;
        
        ItemStack item = player.getCurrentEquippedItem();
        
        if (item != null) {
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("DisplayName: "+item.getDisplayName()));
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("UnlocalizedName: "+item.getUnlocalizedName()));
            
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("NewStackDisplayName: "+
                    new ItemStack(item.getItem(),1,item.getItemDamage()).getDisplayName() ));
            
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("ID: "+item.itemID));
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Damage: "+item.getItemDamage()));
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("StackSize: "+item.stackSize));
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
