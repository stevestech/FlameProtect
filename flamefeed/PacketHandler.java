/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import flamefeed.client.SQLResult;
import flamefeed.server.SQLHandler;
import flamefeed.server.log.EventLogger;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import net.minecraft.entity.player.EntityPlayer;
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
                handlePacketOnServer(packet, player);
            } else if (side == Side.CLIENT) {
                // We are on the client side.
                handlePacketOnClient(packet, player);
            } else {
                // We have an errornous state!
            }
        }
    }

    private void handlePacketOnServer(Packet250CustomPayload packet, Player player) {
//
//        try {
//            EventLogger.log(Level.INFO, "server packet accepted: "+player.toString()+":" + new String(packet.data, "UTF-8"));
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(PacketHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }

        String rawData[];
        HashMap<String, String> data = new HashMap();

        try {
            rawData = new String(packet.data, "UTF-8").split(";");
            for (String rawData1 : rawData) {
                String splitData[] = rawData1.split(",");
                data.put(splitData[0].trim(), splitData[1].trim());
            }
        } catch (UnsupportedEncodingException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }

        String sqlQuery = "SELECT * FROM logentries WHERE";

        sqlQuery += " x BETWEEN " + data.get("xMin") + " AND " + data.get("xMax");
        sqlQuery += " AND y BETWEEN " + data.get("yMin") + " AND " + data.get("yMax");
        sqlQuery += " AND z BETWEEN " + data.get("zMin") + " AND " + data.get("zMax");
        sqlQuery += " AND time BETWEEN '" + data.get("dateMin") + "' AND '" + data.get("dateMax")+"'";
        sqlQuery += " AND world = '" + ((EntityPlayer)player).worldObj.getWorldInfo().getWorldName()+"/"+data.get("dim")+"'";
        
        sqlQuery += " ORDER BY time DESC;";
        
        ResultSet resultSet = SQLHandler.executeQuery(sqlQuery);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(1);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            while(resultSet.next()){
                outputStream.writeUTF(resultSet.getString("time"));
                outputStream.writeUTF(",");
                outputStream.writeUTF(resultSet.getString("x"));
                outputStream.writeUTF(",");
                outputStream.writeUTF(resultSet.getString("y"));
                outputStream.writeUTF(",");
                outputStream.writeUTF(resultSet.getString("z"));
                outputStream.writeUTF(",");
                outputStream.writeUTF(resultSet.getString("source"));
                outputStream.writeUTF(",");
                outputStream.writeUTF(resultSet.getString("action"));
                outputStream.writeUTF(",");
                outputStream.writeUTF(resultSet.getString("target"));
                outputStream.writeUTF(",");
                outputStream.writeUTF(resultSet.getString("tool"));
                outputStream.writeUTF(";");
            }
            resultSet.close();
        } catch (IOException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        } catch (SQLException ex) {
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        } catch (NullPointerException ex){
            EventLogger.log(Level.SEVERE, ex.getLocalizedMessage());
        }

        Packet250CustomPayload resultPacket = new Packet250CustomPayload();
        resultPacket.channel = "FlameProtect";
        resultPacket.data = bos.toByteArray();
        resultPacket.length = bos.size();

        PacketDispatcher.sendPacketToPlayer(resultPacket, player);

    }

    private void handlePacketOnClient(Packet250CustomPayload packet, Player player) {
//        try {
//            EventLogger.log(Level.INFO, "client packet accepted: "+player.toString()+":" + new String(packet.data, "UTF-8"));
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(PacketHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
               
            SQLResult.parseResult(packet.data);
//        
        
        

    }
}
