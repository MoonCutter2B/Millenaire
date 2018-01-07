package org.millenaire.gui;

import org.millenaire.Millenaire;
import org.millenaire.networking.MillPacket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiOptions extends GuiScreen
{
	private final static ResourceLocation OPTIONGUI = new ResourceLocation(Millenaire.MODID + ":textures/gui/ML_village_chief.png");
	private String string;
	private int eventID;

	private GuiButton yes;
	private GuiButton no;

	GuiOptions(int IDin, String stringIn)
	{
		string = I18n.format(stringIn);
		eventID = IDin;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
	    this.drawDefaultBackground();
	    mc.getTextureManager().bindTexture(OPTIONGUI);
	    this.drawTexturedModalRect((this.width - 255) / 2, 2, 0, 0, 255, 199);
	    this.fontRendererObj.drawSplitString(string, (this.width / 2) - 94, 20, 190, 0);
	    
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	public void initGui() 
	{
	    this.buttonList.add(this.yes = new GuiButton(0, (this.width / 2) - 50, (this.height / 2) + 40, 40, 20, "Yes"));
	    this.buttonList.add(this.no = new GuiButton(1, (this.width / 2) + 10, (this.height / 2) + 40, 40, 20, "No"));
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button == this.yes)
		{
			if(eventID == 2)
			{
				Millenaire.simpleNetworkWrapper.sendToServer(new MillPacket(2));
			}
			if(eventID == 3)
			{
				Millenaire.simpleNetworkWrapper.sendToServer(new MillPacket(3));
			}
			if(eventID == 4)
			{
				Millenaire.simpleNetworkWrapper.sendToServer(new MillPacket(4));
			}
			this.mc.displayGuiScreen(null);
	        if (this.mc.currentScreen == null)
	            this.mc.setIngameFocus();
		}
		if(button == this.no)
		{
			this.mc.displayGuiScreen(null);
	        if (this.mc.currentScreen == null)
	            this.mc.setIngameFocus();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() { return false; }
}
