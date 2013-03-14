package org.millenaire.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.millenaire.common.Building;
import org.millenaire.common.ContainerTrade;
import org.millenaire.common.ContainerTrade.MerchantSlot;
import org.millenaire.common.ContainerTrade.TradeSlot;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;

public class GuiTrade extends GuiContainer {

	private Building building;
	private MillVillager merchant;

	private int sellingRow=0,buyingRow=0;

	private final ContainerTrade container;

	private static RenderItem itemRenderer = new RenderItem();

	public GuiTrade(EntityPlayer player, Building building)
	{

		super(new ContainerTrade(player, building));

		container=(ContainerTrade)this.inventorySlots;

		this.building = building;
		ySize = 222;
		xSize = 248;
		
		updateRows(false,0,0);
		updateRows(true,0,0);
	}

	public GuiTrade(EntityPlayer player, MillVillager merchant)
	{
		super(new ContainerTrade(player, merchant));

		container=(ContainerTrade)this.inventorySlots;

		this.merchant = merchant;
		ySize = 222;
		xSize = 248;
		
		updateRows(false,0,0);
		updateRows(true,0,0);
	}

	/*
	 * Method extracted from code in GuiContainer, MC 1.2.5
	 * 
	 */
	public void displayItemOverlay(ItemStack stack, List<String> list,int posx,int posy) {
		if (list.size() > 0)
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

			final int j2 = (posx) + 12;
			int l2 = posy - 12;
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

				if (j4 == 0)
				{
					s = (new StringBuilder()).append("\247").append(Integer.toHexString(stack.getRarity().rarityColor)).append(s).toString();
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



	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		final int textId = mc.renderEngine.getTexture("/graphics/gui/ML_trade.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(textId);
		final int x = (width - xSize) / 2;
		final int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		if (sellingRow==0) {
			drawTexturedModalRect(x + 216, y+68, 5, 5, 11, 7);
		}
		if (buyingRow==0) {
			drawTexturedModalRect(x + 216, y+122, 5, 5, 11, 7);
		}
		
		if (sellingRow>=container.nbRowSelling-2) {
			drawTexturedModalRect(x + 230, y+68, 5, 5, 11, 7);				
		}
		
		if (buyingRow>=container.nbRowBuying-2) {
			drawTexturedModalRect(x + 230, y+122, 5, 5, 11, 7);				
		}
		
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		if (building!=null) {
			fontRenderer.drawString(building.getNativeBuildingName(), 8,6, 0x404040);
			fontRenderer.drawString(MLN.string("ui.wesell")+":", 8, 22, 0x404040);
			fontRenderer.drawString(MLN.string("ui.webuy")+":", 8, 76, 0x404040);
		} else {
			fontRenderer.drawString(merchant.getName()+": "+merchant.getNativeOccupationName(), 8,6, 0x404040);
			fontRenderer.drawString(MLN.string("ui.isell")+":", 8, 22, 0x404040);
		}
		fontRenderer.drawString(MLN.string("ui.inventory"), 8+36, (ySize - 96) + 2, 0x404040);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();
		final int k = (width - xSize) / 2;
		final int l = (height - ySize) / 2;
		drawGuiContainerBackgroundLayer(f,i,j);
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(k, l, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		Slot slot = null;

		String currentProblemString=null;

		for(int i1 = 0; i1 < inventorySlots.inventorySlots.size(); i1++)
		{
			final Slot slot1 = (Slot)inventorySlots.inventorySlots.get(i1);
			drawSlotInventory(slot1);

			String problem=null;

			if (slot1 instanceof TradeSlot) {
				final TradeSlot tslot=(TradeSlot) slot1;

				problem=tslot.isProblem();

				if (problem != null) {
					GL11.glDisable(2896 /*GL_LIGHTING*/);
					GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
					final int j1 = slot1.xDisplayPosition;
					final int l1 = slot1.yDisplayPosition;
					drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80000000, 0x80000000);
					GL11.glEnable(2896 /*GL_LIGHTING*/);
					GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
				}
			} else if (slot1 instanceof MerchantSlot) {
				final MerchantSlot tslot=(MerchantSlot) slot1;

				problem=tslot.isProblem();

				if (problem != null) {
					GL11.glDisable(2896 /*GL_LIGHTING*/);
					GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
					final int j1 = slot1.xDisplayPosition;
					final int l1 = slot1.yDisplayPosition;
					drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80000000, 0x80000000);
					GL11.glEnable(2896 /*GL_LIGHTING*/);
					GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
				}
			}

			if(getIsMouseOverSlot(slot1, i, j))
			{
				slot = slot1;

				currentProblemString=problem;

				GL11.glDisable(2896 /*GL_LIGHTING*/);
				GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
				final int j1 = slot1.xDisplayPosition;
				final int l1 = slot1.yDisplayPosition;
				if (problem==null) {
					drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80ffffff, 0x80ffffff);
				}
				GL11.glEnable(2896 /*GL_LIGHTING*/);
				GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
			}
		}

		final InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
		if(inventoryplayer.getItemStack() != null)
		{
			GL11.glTranslatef(0.0F, 0.0F, 32F);
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
		}
		GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
		drawGuiContainerForegroundLayer(i,j);
		if((inventoryplayer.getItemStack() == null) && (slot != null) && slot.getHasStack())
		{
			if (slot instanceof TradeSlot) {

				final TradeSlot tslot=(TradeSlot)slot;

				String price;
				int priceColour;

				if (tslot.sellingSlot) {
					price=MillCommonUtilities.getShortPrice(tslot.good.getSellingPrice(building.getTownHall()));
					priceColour=MillCommonUtilities.getPriceColourMC(tslot.good.getSellingPrice(building.getTownHall()));
				} else {
					price=MillCommonUtilities.getShortPrice(tslot.good.getBuyingPrice(building.getTownHall()));
					priceColour=MillCommonUtilities.getPriceColourMC(tslot.good.getBuyingPrice(building.getTownHall()));
				}

				final ItemStack itemstack = slot.getStack();

				try {
					final List<String> list=itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

					list.add("\247"+Integer.toHexString(priceColour)+price);
					if (currentProblemString != null) {
						list.add("\2474"+currentProblemString);
					}


					displayItemOverlay(itemstack,list,i - k, j-l);

				} catch (final Exception e) {
					MLN.printException("Exception when rendering tooltip for stack: "+itemstack, e);
				}

			} else if (slot instanceof MerchantSlot) {

				final MerchantSlot tslot=(MerchantSlot)slot;

				String price;
				int priceColour;

				price=MillCommonUtilities.getShortPrice(merchant.getForeignMerchantPrice(tslot.item));
				priceColour=MillCommonUtilities.getPriceColourMC(merchant.getForeignMerchantPrice(tslot.item));

				final ItemStack itemstack = slot.getStack();
				final List<String> list=itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

				list.add("\247"+Integer.toHexString(priceColour)+price);
				if (currentProblemString != null) {
					list.add("\2474"+currentProblemString);
				}

				displayItemOverlay(itemstack,list,i - k, j-l);

			} else {
				final ItemStack itemstack = slot.getStack();
				displayItemOverlay(itemstack,itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips),i - k, j-l);
			}
		}
		GL11.glPopMatrix();
		drawScreenGUIScreen(i, j, f);
		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
	}

	public void drawScreenGUIScreen(int i, int j, float f)
	{
		for(int k = 0; k < controlList.size(); k++)
		{
			final GuiButton guibutton = (GuiButton)controlList.get(k);
			guibutton.drawButton(mc, i, j);
		}

	}

	/**
	 * Draws an inventory slot
	 */
	@Override
	protected void drawSlotInventory(Slot par1Slot)
	{

		if (par1Slot==null) {
			MLN.printException("Tried drawing null slot", new Exception());
			return;
		}

		try {
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
				final int i1 = par1Slot.getBackgroundIconIndex();

				if (i1 >= 0)
				{
					GL11.glDisable(GL11.GL_LIGHTING);
					mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/items.png"));
					drawTexturedModalRect(k, l, (i1 % 16) * 16, (i1 / 16) * 16, 16, 16);
					GL11.glEnable(GL11.GL_LIGHTING);
					flag = true;
				}
			}

			if (!flag && (itemstack!=null))
			{
				itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, itemstack, k, l);
				itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, k, l);
			}

			itemRenderer.zLevel = 0.0F;
			zLevel = 0.0F;
		} catch (final Exception e) {
			MLN.printException("Error when trying to render slot in GuiTrade. Slot: "+par1Slot.slotNumber,e);


		}
	}

	private boolean getIsMouseOverSlot(Slot slot, int i, int j)
	{
		final int k = (width - xSize) / 2;
		final int l = (height - ySize) / 2;
		i -= k;
		j -= l;
		return (i >= (slot.xDisplayPosition - 1)) && (i < (slot.xDisplayPosition + 16 + 1)) && (j >= (slot.yDisplayPosition - 1)) && (j < (slot.yDisplayPosition + 16 + 1));
	}

	private void updateRows(boolean selling,int change,int row) {

		int pos=0;
		
		for (Object o : container.inventorySlots) {
			Slot slot=(Slot)o;
			if (slot instanceof TradeSlot) {
				TradeSlot tradeSlot=(TradeSlot)slot;
				if (tradeSlot.sellingSlot==selling) {
					tradeSlot.yDisplayPosition+=18*change;
					
					if (pos/13<row || pos/13>row+1) {//out of display
						if (tradeSlot.xDisplayPosition>0)
							tradeSlot.xDisplayPosition=tradeSlot.xDisplayPosition-1000;
					} else {
						if (tradeSlot.xDisplayPosition<0)
							tradeSlot.xDisplayPosition=tradeSlot.xDisplayPosition+1000;
					}
					
					pos++;
				}
			} else if (slot instanceof MerchantSlot && selling) {
				MerchantSlot merchantSlot=(MerchantSlot)slot;
				merchantSlot.yDisplayPosition+=18*change;
				
				if (pos/13<row || pos/13>row+1) {//out of display
					if (merchantSlot.xDisplayPosition>0)
						merchantSlot.xDisplayPosition=merchantSlot.xDisplayPosition-1000;
				} else {
					if (merchantSlot.xDisplayPosition<0)
						merchantSlot.xDisplayPosition=merchantSlot.xDisplayPosition+1000;
				}
				
				pos++;
			}
		}

	}

	@Override
	protected void mouseClicked(int x, int y, int clickType)
	{

		if (clickType==0) {
			final int startx = (width - xSize) / 2;
			final int starty = (height - ySize) / 2;

			int dx=x-startx;
			int dy=y-starty;

			if (dy>=68 && dy<=74) {
				if (dx>=216 && dx<=226) {
					if (sellingRow>0) {
						sellingRow--;
						updateRows(true,1,sellingRow);						
					}
				} else if (dx>=230 && dx<=240) {
					if (sellingRow<container.nbRowSelling-2) {
						sellingRow++;
						updateRows(true,-1,sellingRow);						
					}
				}
			} else if (dy>=122 && dy<=127) {
				if (dx>=216 && dx<=226) {
					if (buyingRow>0) {
						buyingRow--;
						updateRows(false,1,buyingRow);
						
					}
				} else if (dx>=230 && dx<=240) {
					if (buyingRow<container.nbRowBuying-2) {
						buyingRow++;
						updateRows(false,-1,buyingRow);
						
					}
				}
			}


		}

		super.mouseClicked(x, y, clickType);
	}
}
