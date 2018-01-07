package org.millenaire.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.millenaire.Millenaire;
import org.millenaire.items.ItemMillParchment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiParchment extends GuiScreen
{
	private final static ResourceLocation PARCHMENTGUI = new ResourceLocation(Millenaire.MODID + ":textures/gui/ML_parchment.png");
	private final static ResourceLocation BOOKGUI = new ResourceLocation(Millenaire.MODID + ":textures/gui/ML_book.png");

	private ItemMillParchment item;
	private List<String> stringPages = new ArrayList<String>();
	private int page = 0;

	private GuiButton forward;
	private GuiButton backward;

	GuiParchment(ItemStack stack)
	{
		if(stack.getItem() instanceof ItemMillParchment)
			item = (ItemMillParchment)stack.getItem();
		else
			System.err.println("Parchment Gui called from wrong Item.  Something failed.");
		
		for(int i = 0; i < item.contents.length; i++)
		{
			String current = I18n.format(item.contents[i]);
			int marker = 0;
			while(marker < current.length())
			{
				int j = 650;
				marker = 0;
			
				while(marker < current.length() && (j > 0 || current.charAt(marker) != ' '))
				{
					if(current.substring(marker).startsWith("\n\n"))
					{
						j= j - 37;
					}
				
					j--;
					marker++;
				}

				if(marker == current.length())
				{
					stringPages.add(current);
					break;
				}
				else
				{
					String sub = current.substring(0, marker);
					stringPages.add(sub);
					current = current.substring(marker + 1, current.length());
					marker = 0;
				}
			}
		}
	}
	
	@Override
	public void initGui() 
	{
	    this.buttonList.add(this.backward = new NextPageButton(0, (this.width / 2) - 95, 208, false));
	    this.buttonList.add(this.forward = new NextPageButton(1, (this.width / 2) + 77, 208, true));
	    updateButtons();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
	    this.drawDefaultBackground();
	    mc.getTextureManager().bindTexture(PARCHMENTGUI);
	    this.drawTexturedModalRect((this.width - 203) / 2, 2, 0, 0, 203, 219);
	    
	    String drawTitle = I18n.format(item.title);
	    this.fontRendererObj.drawString(drawTitle, (this.width - this.fontRendererObj.getStringWidth(drawTitle)) / 2, 6, 0);
	    
	    String drawContents = stringPages.get(page);
	    this.fontRendererObj.drawSplitString(drawContents, (this.width / 2) - 94, 20, 190, 0);
	    
	    super.drawScreen(mouseX, mouseY, partialTicks);
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

		this.forward.visible = page != stringPages.size() - 1;
	}
	
	@Override
	public boolean doesGuiPauseGame() 
	{
	    return false;
	}
	
	static class NextPageButton extends GuiButton
    {
        private final boolean nextPage;

		NextPageButton(int id, int xIn, int yIn, boolean nextPageIn)
        {
            super(id, xIn, yIn, 18, 10, "");
            this.nextPage = nextPageIn;
        }

        /**
         * Draws this button to the screen.
         */
        public void drawButton(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(BOOKGUI);
                int i = 0;
                int j = 180;

                if (flag)
                {
                    i += 18;
                }

                if (!this.nextPage)
                {
                    j += 10;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, 18, 10);
            }
        }
    }
}
