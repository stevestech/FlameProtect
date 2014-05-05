/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.server.log;

import java.util.Date;
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
    public int targetID = 0;
    public int targetDamage = 0;
    public int targetN = 0;
    public String action = "NYI";
    public int toolID = 0;
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
         this.source = player.getEntityName();

        ItemStack toolStack = player.getCurrentEquippedItem();
        if (toolStack != null) {
            this.toolID = toolStack.itemID;
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

    public String getTargetName() {
        if (targetID==0) return "Air";
        try {
            return new ItemStack(targetID, 1, targetDamage).getDisplayName();
        } catch (NullPointerException e) {
            return "NPE";
        }
    }
    
    public String getTargetID(){
        if (targetDamage!=0){
            return targetID+":"+targetDamage;
        }
        return targetID+"";
    }

    public String getToolName() {
        if (toolID==0) return "Hands";
        try {
            return new ItemStack(toolID, 1, toolDamage).getDisplayName();
        } catch (NullPointerException e) {
            return "NPE";
        }
    }
    
    public String getToolID(){
        if (toolDamage!=0){
            return toolID+":"+toolDamage;
        }
        return toolID+"";
    }

}
