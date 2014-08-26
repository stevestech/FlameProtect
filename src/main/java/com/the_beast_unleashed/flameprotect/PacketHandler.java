/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.the_beast_unleashed.flameprotect;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

import com.the_beast_unleashed.flameprotect.client.SQLResult;
import com.the_beast_unleashed.flameprotect.server.log.SQLHandler;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 *
 * @author Anedaar
 */
public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
//
//        try {
//            EventLogger.log(Level.INFO, "packet accepted: "+player.toString()+":" + new String(packet.data, "UTF-8"));
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(PacketHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }

        if (packet.channel.equals("FlameProtect")) {
            Side side = FMLCommonHandler.instance().getEffectiveSide();
            if (side == Side.SERVER) {
                // We are on the server side.
                SQLHandler.addQuery(packet, player);
            } else if (side == Side.CLIENT) {
                // We are on the client side.
                handlePacketOnClient(packet, player);
            } else {
                // We have an errornous state!
            }
        }
    }

    private void handlePacketOnClient(Packet250CustomPayload packet, Player player) {
//        try {
//            FlameProtectLogger.getLogger().log(Level.INFO, "client packet accepted: "+player.toString()+":" + new String(packet.data, "UTF-8"));
//        } catch (UnsupportedEncodingException ex) {
//            FlameProtectLogger.getLogger().log(Level.SEVERE, null, ex);
//        }
               
          SQLResult.parseResult(packet.data);
    }
}
