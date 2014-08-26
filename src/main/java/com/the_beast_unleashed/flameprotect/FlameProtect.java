/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;

import com.the_beast_unleashed.flameprotect.server.ServerProxy;

/**
 *
 * @author Anedaar
 */
@Mod(modid = "FlameProtect", name = "World protection and player logging utility", version = "0.3")
@NetworkMod(channels = {"FlameProtect"}, clientSideRequired = false, serverSideRequired = false, packetHandler = PacketHandler.class)

public class FlameProtect {
	
    @SidedProxy(serverSide = "com.the_beast_unleashed.flameprotect.server.ServerProxy", clientSide = "com.the_beast_unleashed.flameprotect.client.ClientProxy")
    public static ServerProxy proxy;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        proxy.serverLoad(event);
    }

}
