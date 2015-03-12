/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server.log;

import java.util.Date;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Anedaar
 */
public class LogEvent {

    public int x = 0;
    public int y = 0;
    public int z = 0;
    public String world = "NYI";
    public Date time = new Date();
    public String source = "NYI";
    public UUID sourceUUID;
    public String targetName = "none";
    public int targetDamage = 0;
    public int targetN = 0;
    public String action = "NYI";
    public String toolName = "Hands";
    public int toolDamage = 0;
    public int toolN = 0;
    public boolean isSneaking = false;

    public LogEvent() {

    }
    
    /**
     * Sets the given player as the events source.
     * This includes source, toolID, toolDamage, toolN, isSneaking
     * @param player
     */
    public void setSource(EntityPlayer player){
        this.source = player.getDisplayName();
        this.sourceUUID = player.getUniqueID();

        ItemStack toolStack = player.getCurrentEquippedItem();
        if (toolStack != null) {
        	this.toolName = toolStack.getDisplayName();
            this.toolDamage = toolStack.getItemDamage();
            this.toolN = toolStack.stackSize;
        }
        
        this.isSneaking = player.isSneaking();
    }
    
    /**
     * Sets the given coordinates as the events position.
     * This includes x,y,z
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(int x, int y, int z){
         this.x = x;
         this.y = y;
         this.z = z;
    }
    
    /**
     * Sets the given player as the events position.
     * This includes x,y,z
     * @param player
     */
    public void setPosition(EntityPlayer player){
         this.x = (int)player.posX;
         this.y = (int)player.posY;
         this.z = (int)player.posZ;
    }

}
