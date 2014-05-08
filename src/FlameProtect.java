/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.FlameProtect.src;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import flamefeed.FlameProtect.src.server.ServerProxy;

/**
 *
 * @author Anedaar
 */
@Mod(modid = "FlameProtect", name = "Flamefeed World Protection", version = "0.2")
@NetworkMod(channels = {"FlameProtect"}, clientSideRequired = false, serverSideRequired = false, packetHandler = PacketHandler.class)

public class FlameProtect {

    @SidedProxy(serverSide = "flamefeed.server.ServerProxy", clientSide = "flamefeed.client.ClientProxy")
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
