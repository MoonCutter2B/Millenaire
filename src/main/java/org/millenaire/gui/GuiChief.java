package org.millenaire.gui;

import org.millenaire.Millenaire;
import org.millenaire.gui.GuiParchment.NextPageButton;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiChief extends GuiScreen
{
	private final static ResourceLocation CHIEFGUI = new ResourceLocation(Millenaire.MODID + ":textures/gui/ML_village_chief.png");
	private String string;
	private int page = 0;
	private int maxPage = 4;

	private GuiButton forward;
	private GuiButton backward;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
	    this.drawDefaultBackground();
	    mc.getTextureManager().bindTexture(CHIEFGUI);
	    this.drawTexturedModalRect((this.width - 255) / 2, 2, 0, 0, 255, 199);
	    this.fontRendererObj.drawSplitString(string, (this.width / 2) - 94, 20, 190, 0);
	    
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() 
	{
	    this.buttonList.add(this.backward = new NextPageButton(0, (this.width / 2) - 95, 208, false));
	    this.buttonList.add(this.forward = new NextPageButton(1, (this.width / 2) + 77, 208, true));
	    updateButtons();
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
	    if (button == this.forward) 
	    {
	        page++;
	        updateButtons();
	    }
	    if (button == this.backward)
	    {
	    	page--;
	    	updateButtons();
	    }
	}
	
	private void updateButtons()
	{
		this.backward.visible = page != 0;

		this.forward.visible = page != maxPage - 1;
	}
	
	@Override
	public boolean doesGuiPauseGame() { return false; }
}
