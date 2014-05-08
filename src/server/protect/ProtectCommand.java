/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.FlameProtect.src.server.protect;

import flamefeed.FlameProtect.src.server.Server;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatMessageComponent;

/**
 *
 * @author Anedaar
 */
public class ProtectCommand implements ICommand {

    public ProtectCommand() {
    }

    @Override
    public String getCommandName() {
        return Server.protCmd;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/"+Server.protCmd;
    }

    @Override
    public List getCommandAliases() {
        ArrayList list = new ArrayList();
        list.add(Server.protCmd);

        return list;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        Server.enabled = !Server.enabled;
        icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Protection: " + Boolean.toString(Server.enabled)));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        if (!icommandsender.getEntityWorld().isRemote) return true;
        if (icommandsender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) icommandsender;
            return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getOps().contains(player.username.toLowerCase().trim());
        } else {
            return !(icommandsender instanceof TileEntityCommandBlock);
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isUsernameIndex(String[] astring, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int compareTo(ICommand par1ICommand) {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    @Override
    public int compareTo(Object par1Obj) {
        return this.compareTo((ICommand) par1Obj);
    }

}
