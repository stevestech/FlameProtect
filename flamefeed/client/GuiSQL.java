/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flamefeed.client;

import cpw.mods.fml.common.network.PacketDispatcher;
import flamefeed.client.SQLResult.SQLResultRow;
import flamefeed.server.SQLHandler;
import flamefeed.server.log.EventLogger;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Anedaar
 */
class GuiSQL extends GuiScreen {

    //Gui-Elements
    private final GuiButton resetButton;
    private final GuiButton sqlButton;
    private final HashMap<String, GuiTextField> textFields = new HashMap();
    private SQLQueryValues values = null;

    //resize
    private int lastwidth;
    private int lastheight;

    //player
    private final EntityClientPlayerMP player;

    //size-variables
    private int xAnchor = 0;
    private int yAnchor = 0;
    private int curY = 0;
    private final int titleRowHeight = 10;
    private final int buttonRowHeight = 25;
    private final int buttonHeight = 20;
    private final int sqlRowHeight = 20;

    public GuiSQL(EntityClientPlayerMP player) {
        this.player = player;
        //GuiButton(id,x,y[,width=200,height=20],string)
        resetButton = new GuiButton(0, 0, 1, 100, 20, "Reset Fields");
        sqlButton = new GuiButton(1, 0, 1, 100, 20, "SQL Query");

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void resize() {

        //Buttons
        //Textfields
        if (values == null) {
            values = new SQLQueryValues(player);
        } else {
            values.getValuesFromTextFields(textFields);
        }

        //GuiTextField(FontRenderer,x,y,width,height)
        textFields.clear();

        //first row under title
        resetButton.xPosition = xAnchor;
        resetButton.yPosition = curY;
        textFields.put("dateMin", new GuiTextField(fontRenderer, xAnchor + 110, curY, 100, buttonHeight));
        textFields.get("dateMin").setMaxStringLength(19);
        textFields.put("xMin", new GuiTextField(fontRenderer, xAnchor + 220, curY, 50, buttonHeight));
        textFields.get("xMin").setMaxStringLength(6);
        textFields.put("yMin", new GuiTextField(fontRenderer, xAnchor + 275, curY, 50, buttonHeight));
        textFields.get("yMin").setMaxStringLength(6);
        textFields.put("zMin", new GuiTextField(fontRenderer, xAnchor + 330, curY, 50, buttonHeight));
        textFields.get("zMin").setMaxStringLength(6);
        curY += buttonRowHeight;

        //second row
        sqlButton.xPosition = xAnchor;
        sqlButton.yPosition = curY;
        textFields.get("dateMin").setFocused(true);
        textFields.put("dateMax", new GuiTextField(fontRenderer, xAnchor + 110, curY, 100, buttonHeight));
        textFields.get("dateMax").setMaxStringLength(19);
        textFields.put("xMax", new GuiTextField(fontRenderer, xAnchor + 220, curY, 50, buttonHeight));
        textFields.get("xMax").setMaxStringLength(6);
        textFields.put("yMax", new GuiTextField(fontRenderer, xAnchor + 275, curY, 50, buttonHeight));
        textFields.get("yMax").setMaxStringLength(6);
        textFields.put("zMax", new GuiTextField(fontRenderer, xAnchor + 330, curY, 50, buttonHeight));
        textFields.get("zMax").setMaxStringLength(6);
        curY += buttonRowHeight;
//        dateMaxText = new GuiTextField(mc.fontRenderer,0,0,1,1);

        values.putValuesToTextFields(textFields);

    }

    private void drawSQLResult() {
        if (SQLResult.rows == null) {
            return;
        }

        float scale = 1.0F;

        GL11.glScalef(scale, scale, 1.0F);

        //Titles
        fontRenderer.drawString("Date", xAnchor, curY, 0x000000, false);
        fontRenderer.drawString("x", xAnchor + 135, curY, 0x000000, false);
        fontRenderer.drawString("y", xAnchor + 165, curY, 0x000000, false);
        fontRenderer.drawString("z", xAnchor + 195, curY, 0x000000, false);
        fontRenderer.drawString("source", xAnchor + 225, curY, 0x000000, false);
        fontRenderer.drawString("action", xAnchor + 300, curY, 0x000000, false);
        fontRenderer.drawString("target", xAnchor + 340, curY, 0x000000, false);
        fontRenderer.drawString("tool", xAnchor + 400, curY, 0x000000, false);
        curY += sqlRowHeight;

        for (SQLResultRow row : SQLResult.rows) {
            fontRenderer.drawString(row.time, xAnchor, curY, 0x000000, false);
            fontRenderer.drawString(row.x, xAnchor + 135, curY, 0x000000, false);
            fontRenderer.drawString(row.y, xAnchor + 165, curY, 0x000000, false);
            fontRenderer.drawString(row.z, xAnchor + 195, curY, 0x000000, false);
            fontRenderer.drawString(row.source, xAnchor + 225, curY, 0x000000, false);
            fontRenderer.drawString(row.action, xAnchor + 300, curY, 0x000000, false);
            fontRenderer.drawString(row.target, xAnchor + 340, curY, 0x000000, false);
            fontRenderer.drawString(row.tool, xAnchor + 400, curY, 0x000000, false);
            curY += sqlRowHeight;
            if (curY > 0.8F * height) {
                break;
            }
        }

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F);

    }

    @Override
    public void drawScreen(int x, int y, float f) {
        drawDefaultBackground();

        //Anchors
        xAnchor = (int) (width * 0.1F);
        yAnchor = (int) (height * 0.1F);
        curY = yAnchor;

        //Background
        this.mc.renderEngine.bindTexture(new ResourceLocation("textures/map/map_background.png"));
        GL11.glScalef(width / 276.0F, height / 276.0F, 1);
        drawTexturedModalRect(10, 10, 0, 0, 256, 256);
        GL11.glScalef(276.0F / width, 276.0F / height, 1);

        //Texts
        fontRenderer.drawString("Date", xAnchor + 110, curY, 0x000000, false);
        fontRenderer.drawString("x", xAnchor + 220, curY, 0x000000, false);
        fontRenderer.drawString("y", xAnchor + 275, curY, 0x000000, false);
        fontRenderer.drawString("z", xAnchor + 330, curY, 0x000000, false);
        curY += titleRowHeight;

        //resize
        if (width != lastwidth || height != lastheight || values == null) {
            lastwidth = width;
            lastheight = height;
            resize();
        } else {
            curY += (2 * buttonRowHeight);
        }

        //Buttons
        this.buttonList.clear();
        this.buttonList.add(resetButton);
        this.buttonList.add(sqlButton);

        //Textfields
        for (Map.Entry<String, GuiTextField> field : textFields.entrySet()) {
            field.getValue().drawTextBox();
        }

        //SQL Data
        drawSQLResult();

        super.drawScreen(x, y, f);
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {

        for (Map.Entry<String, GuiTextField> field : textFields.entrySet()) {
            field.getValue().mouseClicked(par1, par2, par3);
        }

        super.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {

        for (Map.Entry<String, GuiTextField> field : textFields.entrySet()) {
            field.getValue().textboxKeyTyped(par1, par2);
        }

        super.keyTyped(par1, par2);
    }

    private void sendQueryToServer() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(1);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            for (Map.Entry<String, String> field : values.entrySet()) {
                outputStream.writeUTF(field.getKey());
                outputStream.writeUTF(",");
                outputStream.writeUTF(field.getValue());
                outputStream.writeUTF(";");
            }
        } catch (IOException ex) {
            Logger.getLogger(GuiSQL.class.getName()).log(Level.SEVERE, null, ex);
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "FlameProtect";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                //Reset button
                values = null;
                break;
            case 1:
                //SQL button
                values.getValuesFromTextFields(textFields);
                sendQueryToServer();
                break;
            default:
        }
    }

}
