/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.client;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiTextField;

/**
 *
 * @author Anedaar
 */
public class SQLQueryValues extends HashMap<String, String> {

    public SQLQueryValues(EntityClientPlayerMP player) {
        
        this.put("dateMax",new Timestamp(Calendar.getInstance().getTimeInMillis()).toString());
        this.put("dateMin",new Timestamp(Calendar.getInstance().getTimeInMillis()-(1000*60*60*24)).toString());
        
        this.put("xMin", (int)(player.posX - 5)+"");
        this.put("xMax", (int)(player.posX + 5)+"");
        this.put("yMin", (int)(player.posY - 5)+"");
        this.put("yMax", (int)(player.posY + 5)+"");
        this.put("zMin", (int)(player.posZ - 5)+"");
        this.put("zMax", (int)(player.posZ + 5)+"");
        this.put("dim", ""+player.dimension);
                
        //init standard values
    }

    public void getValuesFromTextFields(HashMap<String, GuiTextField> map) {
        for (Entry<String, GuiTextField> field : map.entrySet()) {
            if (field.getValue() != null) {
                this.put(field.getKey(), field.getValue().getText());
            }
        }
    }

    public void putValuesToTextFields(HashMap<String, GuiTextField> map) {
        for (Entry<String, GuiTextField> field : map.entrySet()) {
            if (field.getValue() != null && this.get(field.getKey()) != null) {
                field.getValue().setText(this.get(field.getKey()));
            }
        }
    }

}
