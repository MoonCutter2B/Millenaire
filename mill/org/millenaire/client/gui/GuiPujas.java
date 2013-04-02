package org.millenaire.client.gui;

import java.util.List;
import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.ContainerPuja;
import org.millenaire.common.ContainerPuja.MoneySlot;
import org.millenaire.common.ContainerPuja.OfferingSlot;
import org.millenaire.common.ContainerPuja.ToolSlot;
import org.millenaire.common.MLN;
import org.millenaire.common.Puja;

public class GuiPujas extends GuiContainer
{
	private static final int PUJATARGET_START = 22;
	private final Building temple;
	private final EntityPlayer player;

	public GuiPujas(EntityPlayer player, Building temple)
	{
		super(new ContainerPuja(player, temple));

		ySize = 195;

		this.temple = temple;
		this.player=player;

		if (MLN.LogPujas>=MLN.DEBUG) {
			MLN.debug(this, "Opening shrine GUI");
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture("/graphics/gui/ML_pujas.png");
		final int j = (width - xSize) / 2;
		final int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

		if (temple.pujas!=null) {
			for (int cp=0;cp<4;cp++) {
				if (temple.pujas.enchantmentTarget==Puja.enchantments[cp]) {
					drawTexturedModalRect(j + 118, k + PUJATARGET_START + (15*cp), 0, 194 + (15*cp), 44, 15);
				}
			}
			int progress = temple.pujas.getPujaProgressScaled(13);
			drawTexturedModalRect(j + 27, (k + 39 + 13) - progress, 176, 13 - progress, 15, progress);

			progress = temple.pujas.getOfferingProgressScaled(16);
			drawTexturedModalRect(j + 84, (k + 63 + 16) - progress, 176, (31 + 16) - progress, 19, progress);
		}
	}


	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRenderer.drawString(MLN.string("pujas.offering"), 8, 6, 0x404040);
		fontRenderer.drawString(MLN.string("pujas.panditfee"), 8, 75, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
	}

	//Taken from GuiContainer, MC 1.2.5
	@SuppressWarnings("unchecked")
	@Override
	public void drawScreen(int x, int y, float par3)
	{
		drawDefaultBackground();
		final int i = guiLeft;
		final int j = guiTop;
		drawGuiContainerBackgroundLayer(par3, x, y);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef(i, j, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		Slot slot = null;
		final int k = 240;
		final int i1 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, i1 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int l = 0; l < inventorySlots.inventorySlots.size(); l++)
		{
			final Slot slot1 = (Slot)inventorySlots.inventorySlots.get(l);
			drawSlotInventory(slot1);

			if (isMouseOverSlot(slot1, x, y))
			{
				slot = slot1;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				final int j1 = slot1.xDisplayPosition;
				final int k1 = slot1.yDisplayPosition;
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, 0x80ffffff, 0x80ffffff);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		drawGuiContainerForegroundLayer(x,y);
		final InventoryPlayer inventoryplayer = mc.thePlayer.inventory;

		if (inventoryplayer.getItemStack() != null)
		{
			GL11.glTranslatef(0.0F, 0.0F, 32F);
			zLevel = 200F;
			itemRenderer.zLevel = 200F;
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), x - i - 8, y - j - 8);
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), x - i - 8, y - j - 8);
			zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);


		final int startx = (width - xSize) / 2;
		final int starty = (height - ySize) / 2;

		if ((x>(startx+118)) && (x<(startx+118+44))) {
			for (int cp=0;cp<4;cp++) {
				if ((y>(starty+PUJATARGET_START+(15*cp))) && (y<(starty+PUJATARGET_START+(15*(cp+1))))) {

					final String s=MLN.string("pujas.god"+cp);
					final int stringlength=fontRenderer.getStringWidth(s);

					drawGradientRect((x-startx) + 5, y-starty - 3, (x-startx) + stringlength + 3, (y-starty) + 8 + 3, 0xc0000000, 0xc0000000);
					fontRenderer.drawString(s, (x+8)-startx, y-starty, 0xA0A0A0);
				}

			}
		}


		if ((inventoryplayer.getItemStack() == null) && (slot != null))
		{

			List<String> list=null;
			ItemStack itemstack=null;

			if (slot.getHasStack()) {
				itemstack = slot.getStack();
				list = itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
			} else if (slot instanceof OfferingSlot) {
				list = new Vector<String>();
				list.add("\2476"+MLN.string("pujas.offeringslot"));
				list.add("\2477"+MLN.string("pujas.offeringslot2"));
			} else if (slot instanceof MoneySlot) {
				list = new Vector<String>();
				list.add("\2476"+MLN.string("pujas.moneyslot"));
			} else if (slot instanceof ToolSlot) {
				list = new Vector<String>();
				list.add("\2476"+MLN.string("pujas.toolslot"));
			}

			if ((list!=null) && (list.size() > 0))
			{
				int l1 = 0;

				for (int i2 = 0; i2 < list.size(); i2++)
				{
					final int k2 = fontRenderer.getStringWidth(list.get(i2));

					if (k2 > l1)
					{
						l1 = k2;
					}
				}

				final int j2 = (x - i) + 12;
				int l2 = y - j - 12;
				final int i3 = l1;
				int j3 = 8;

				if (list.size() > 1)
				{
					j3 += 2 + ((list.size() - 1) * 10);
				}

				zLevel = 300F;
				itemRenderer.zLevel = 300F;
				final int k3 = 0xf0100010;
				drawGradientRect(j2 - 3, l2 - 4, j2 + i3 + 3, l2 - 3, k3, k3);
				drawGradientRect(j2 - 3, l2 + j3 + 3, j2 + i3 + 3, l2 + j3 + 4, k3, k3);
				drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 + j3 + 3, k3, k3);
				drawGradientRect(j2 - 4, l2 - 3, j2 - 3, l2 + j3 + 3, k3, k3);
				drawGradientRect(j2 + i3 + 3, l2 - 3, j2 + i3 + 4, l2 + j3 + 3, k3, k3);
				final int l3 = 0x505000ff;
				final int i4 = ((l3 & 0xfefefe) >> 1) | (l3 & 0xff000000);
				drawGradientRect(j2 - 3, (l2 - 3) + 1, (j2 - 3) + 1, (l2 + j3 + 3) - 1, l3, i4);
				drawGradientRect(j2 + i3 + 2, (l2 - 3) + 1, j2 + i3 + 3, (l2 + j3 + 3) - 1, l3, i4);
				drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, (l2 - 3) + 1, l3, l3);
				drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3, i4, i4);

				for (int j4 = 0; j4 < list.size(); j4++)
				{
					String s = list.get(j4);

					if ((j4 == 0) && (itemstack!=null))
					{
						s = (new StringBuilder()).append("\247").append(Integer.toHexString(itemstack.getRarity().rarityColor)).append(s).toString();
					}
					else
					{
						s = (new StringBuilder()).append("\2477").append(s).toString();
					}


					fontRenderer.drawStringWithShadow(s, j2, l2, -1);

					if (j4 == 0)
					{
						l2 += 2;
					}

					l2 += 10;
				}

				zLevel = 0.0F;
				itemRenderer.zLevel = 0.0F;
			}
		}

		GL11.glPopMatrix();
		drawScreenGUIScreen(x, y, par3);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public void drawScreenGUIScreen(int i, int j, float f)
	{
		for(int k = 0; k < buttonList.size(); k++)
		{
			final GuiButton guibutton = (GuiButton)buttonList.get(k);
			guibutton.drawButton(mc, i, j);
		}

	}

	@Override
	protected void drawSlotInventory(Slot par1Slot)
	{
		final int i = par1Slot.xDisplayPosition;
		final int j = par1Slot.yDisplayPosition;
		final ItemStack itemstack = par1Slot.getStack();
		boolean flag = false;
		final int k = i;
		final int l = j;
		zLevel = 100F;
		itemRenderer.zLevel = 100F;

		if (itemstack == null)
		{
			final Icon icon = par1Slot.getBackgroundIconIndex();

			if (icon != null)
			{
				GL11.glDisable(GL11.GL_LIGHTING);
				mc.renderEngine.bindTexture("/gui/items.png");
				this.drawTexturedModelRectFromIcon(i, j, icon, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
				flag = true;
			}
		}

		if (!flag)
		{
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, itemstack, k, l);
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, k, l);
		}

		itemRenderer.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	/**
	 * Returns if the passed mouse position is over the specified slot.
	 */
	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3)
	{
		final int i = guiLeft;
		final int j = guiTop;
		par2 -= i;
		par3 -= j;
		return (par2 >= (par1Slot.xDisplayPosition - 1)) && (par2 < (par1Slot.xDisplayPosition + 16 + 1)) && (par3 >= (par1Slot.yDisplayPosition - 1)) && (par3 < (par1Slot.yDisplayPosition + 16 + 1));
	}

	@Override
	protected void mouseClicked(int x, int y, int par3) {

		super.mouseClicked(x, y, par3);

		final int startx = (width - xSize) / 2;
		final int starty = (height - ySize) / 2;

		if (temple.pujas!=null) {
			if ((x>(startx+118)) && (x<(startx+118+44))) {
				for (int i=0;i<4;i++) {
					if ((y>(starty+PUJATARGET_START+(15*i))) && (y<(starty+PUJATARGET_START+(15*(i+1))))) {
						ClientSender.pujasChangeEnchantment(player, temple, i);
					}
				}
			}
		}

	}
}
