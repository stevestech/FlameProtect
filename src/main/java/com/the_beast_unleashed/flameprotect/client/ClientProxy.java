/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect.client;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

import com.the_beast_unleashed.flameprotect.server.ServerProxy;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

/**
 *
 * @author Anedaar
 */
public class ClientProxy extends ServerProxy {
    
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        KeyBinding[] key = {new KeyBinding("Show SQL Window", Keyboard.KEY_G)};
        boolean[] repeat = {false};
        KeyBindingRegistry.registerKeyBinding(new ClientKeyHandler(key, repeat));

    }
}
