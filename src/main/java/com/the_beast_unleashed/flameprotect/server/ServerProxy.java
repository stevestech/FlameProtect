/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.server;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

import com.the_beast_unleashed.flameprotect.FlameProtectLogger;
import com.the_beast_unleashed.flameprotect.server.ServerConfigHandler;
import com.the_beast_unleashed.flameprotect.server.log.EventLogger;
import com.the_beast_unleashed.flameprotect.server.log.LogEventHandler;

import java.util.logging.Level;

import net.minecraftforge.common.MinecraftForge;

/**
 *
 * @author Anedaar
 */

public class ServerProxy {
    
    
    public void preInit(FMLPreInitializationEvent event) {
        ServerConfigHandler.init(event.getSuggestedConfigurationFile());
        EventLogger.init();
    }

    public void postInit(FMLPostInitializationEvent event) {    
        EventLogger.log(Level.INFO, "registering EventHandlers");
        MinecraftForge.EVENT_BUS.register(new LogEventHandler());
    }

    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandFlameProtect());
    }

}
