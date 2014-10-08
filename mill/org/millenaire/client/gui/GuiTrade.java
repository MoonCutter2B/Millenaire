package org.millenaire.client.gui;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.millenaire.client.MillClientUtilities;
import org.millenaire.client.gui.GuiText.MillGuiButton;
import org.millenaire.common.ContainerTrade;
import org.millenaire.common.ContainerTrade.MerchantSlot;
import org.millenaire.common.ContainerTrade.TradeSlot;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class GuiTrade extends GuiContainer {

	private Building building;
	private MillVillager merchant;
	private final EntityPlayer player;

	private int sellingRow = 0, buyingRow = 0;

	private final ContainerTrade container;
	private final Method drawSlotInventory;

	private static RenderItem itemRenderer = new RenderItem();

	ResourceLocation background = new ResourceLocation(Mill.modId, "textures/gui/ML_trade.png");

	public GuiTrade(final EntityPlayer player, final Building building) {

		super(new ContainerTrade(player, building));

		drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);

		container = (ContainerTrade) this.inventorySlots;

		this.building = building;
		this.player = player;
		ySize = 222;
		xSize = 248;

		updateRows(false, 0, 0);
		updateRows(true, 0, 0);
	}

	public GuiTrade(final EntityPlayer player, final MillVillager merchant) {
		super(new ContainerTrade(player, merchant));

		drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);

		container = (ContainerTrade) this.inventorySlots;

		this.merchant = merchant;
		this.player = player;
		ySize = 222;
		xSize = 248;

		updateRows(false, 0, 0);
		updateRows(true, 0, 0);
	}

	@Override
	protected void actionPerformed(final GuiButton button) {

		if (button instanceof MillGuiButton) {
			MillClientUtilities.displayTradeHelp(building, player);
			return;
		}

		super.actionPerformed(button);
	}

	/*
	 * Method extracted from code in GuiContainer, MC 1.2.5
	 */
	public void displayItemOverlay(final ItemStack stack, final List<String> list, final int posx, final int posy) {
		if (list.size() > 0) {
			int l1 = 0;

			for (int i2 = 0; i2 < list.size(); i2++) {
				final int k2 = fontRendererObj.getStringWidth(list.get(i2));

				if (k2 > l1) {
					l1 = k2;
				}
			}

			final int j2 = posx + 12;
			int l2 = posy - 12;
			final int i3 = l1;
			int j3 = 8;

			if (list.size() > 1) {
				j3 += 2 + (list.size() - 1) * 10;
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
			final int i4 = (l3 & 0xfefefe) >> 1 | l3 & 0xff000000;
			drawGradientRect(j2 - 3, l2 - 3 + 1, j2 - 3 + 1, l2 + j3 + 3 - 1, l3, i4);
			drawGradientRect(j2 + i3 + 2, l2 - 3 + 1, j2 + i3 + 3, l2 + j3 + 3 - 1, l3, i4);
			drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 - 3 + 1, l3, l3);
			drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3, i4, i4);

			for (int j4 = 0; j4 < list.size(); j4++) {
				String s = list.get(j4);

				if (j4 == 0) {
					s = new StringBuilder().append(stack.getRarity().rarityColor).append(s).toString();
				} else {
					s = new StringBuilder().append("\2477").append(s).toString();
				}

				fontRendererObj.drawStringWithShadow(s, j2, l2, -1);

				if (j4 == 0) {
					l2 += 2;
				}

				l2 += 10;
			}

			zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int i, final int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(background);
		final int x = (width - xSize) / 2;
		final int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		if (sellingRow == 0) {
			drawTexturedModalRect(x + 216, y + 68, 5, 5, 11, 7);
		}
		if (buyingRow == 0) {
			drawTexturedModalRect(x + 216, y + 122, 5, 5, 11, 7);
		}

		if (sellingRow >= container.nbRowSelling - 2) {
			drawTexturedModalRect(x + 230, y + 68, 5, 5, 11, 7);
		}

		if (buyingRow >= container.nbRowBuying - 2) {
			drawTexturedModalRect(x + 230, y + 122, 5, 5, 11, 7);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int x, final int y) {
		if (building != null) {
			fontRendererObj.drawString(building.getNativeBuildingName(), 8, 6, 0x404040);
			fontRendererObj.drawString(MLN.string("ui.wesell") + ":", 8, 22, 0x404040);
			fontRendererObj.drawString(MLN.string("ui.webuy") + ":", 8, 76, 0x404040);
		} else {
			fontRendererObj.drawString(merchant.getName() + ": " + merchant.getNativeOccupationName(), 8, 6, 0x404040);
			fontRendererObj.drawString(MLN.string("ui.isell") + ":", 8, 22, 0x404040);
		}

		fontRendererObj.drawString(MLN.string("ui.inventory"), 8 + 36, ySize - 96 + 2, 0x404040);

		@SuppressWarnings("rawtypes")
		final Iterator iterator = this.buttonList.iterator();

		while (iterator.hasNext()) {
			final GuiButton guibutton = (GuiButton) iterator.next();

			if (guibutton.func_146115_a()) {
				guibutton.func_146111_b(x - this.guiLeft, y - this.guiTop);
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drawScreen(final int i, final int j, final float f) {
		drawDefaultBackground();
		final int k = (width - xSize) / 2;
		final int l = (height - ySize) / 2;
		drawGuiContainerBackgroundLayer(f, i, j);
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(k, l, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		Slot slot = null;

		String currentProblemString = null;

		for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); i1++) {
			final Slot slot1 = (Slot) inventorySlots.inventorySlots.get(i1);
			try {
				drawSlotInventory.invoke(this, slot1);
			} catch (final Exception e) {
				MLN.printException("Exception when trying to access drawSlotInventory", e);
			}

			String problem = null;

			if (slot1 instanceof TradeSlot) {
				final TradeSlot tslot = (TradeSlot) slot1;

				problem = tslot.isProblem();

				if (problem != null) {
					GL11.glDisable(2896 /* GL_LIGHTING */);
					GL11.glDisable(2929 /* GL_DEPTH_TEST */);
					final int j1 = slot1.xDisplayPosition;
					final int l1 = slot1.yDisplayPosition;
					drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80000000, 0x80000000);
					GL11.glEnable(2896 /* GL_LIGHTING */);
					GL11.glEnable(2929 /* GL_DEPTH_TEST */);
				}
			} else if (slot1 instanceof MerchantSlot) {
				final MerchantSlot tslot = (MerchantSlot) slot1;

				problem = tslot.isProblem();

				if (problem != null) {
					GL11.glDisable(2896 /* GL_LIGHTING */);
					GL11.glDisable(2929 /* GL_DEPTH_TEST */);
					final int j1 = slot1.xDisplayPosition;
					final int l1 = slot1.yDisplayPosition;
					drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80000000, 0x80000000);
					GL11.glEnable(2896 /* GL_LIGHTING */);
					GL11.glEnable(2929 /* GL_DEPTH_TEST */);
				}
			}

			if (getIsMouseOverSlot(slot1, i, j)) {
				slot = slot1;

				currentProblemString = problem;

				GL11.glDisable(2896 /* GL_LIGHTING */);
				GL11.glDisable(2929 /* GL_DEPTH_TEST */);
				final int j1 = slot1.xDisplayPosition;
				final int l1 = slot1.yDisplayPosition;
				if (problem == null) {
					drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80ffffff, 0x80ffffff);
				}
				GL11.glEnable(2896 /* GL_LIGHTING */);
				GL11.glEnable(2929 /* GL_DEPTH_TEST */);
			}
		}

		final InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
		if (inventoryplayer.getItemStack() != null) {
			GL11.glTranslatef(0.0F, 0.0F, 32F);
			itemRenderer.renderItemIntoGUI(fontRendererObj, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
			itemRenderer.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
		}
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDisable(2929 /* GL_DEPTH_TEST */);
		drawGuiContainerForegroundLayer(i, j);
		if (inventoryplayer.getItemStack() == null && slot != null && slot.getHasStack()) {
			if (slot instanceof TradeSlot) {

				final TradeSlot tslot = (TradeSlot) slot;

				String price;
				int priceColour;

				if (tslot.sellingSlot) {
					price = MillCommonUtilities.getShortPrice(tslot.good.getCalculatedSellingPrice(building, player));
					priceColour = MillCommonUtilities.getPriceColourMC(tslot.good.getCalculatedSellingPrice(building, player));
				} else {
					price = MillCommonUtilities.getShortPrice(tslot.good.getCalculatedBuyingPrice(building, player));
					priceColour = MillCommonUtilities.getPriceColourMC(tslot.good.getCalculatedBuyingPrice(building, player));
				}

				final ItemStack itemstack = slot.getStack();

				try {
					final List<String> list = itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

					list.add("\247" + Integer.toHexString(priceColour) + price);
					if (currentProblemString != null) {
						list.add("\2474" + currentProblemString);
					}

					displayItemOverlay(itemstack, list, i - k, j - l);

				} catch (final Exception e) {
					MLN.printException("Exception when rendering tooltip for stack: " + itemstack, e);
				}

			} else if (slot instanceof MerchantSlot) {

				final MerchantSlot tslot = (MerchantSlot) slot;

				String price;
				int priceColour;

				price = MillCommonUtilities.getShortPrice(tslot.good.getCalculatedSellingPrice(merchant));
				priceColour = MillCommonUtilities.getPriceColourMC(tslot.good.getCalculatedSellingPrice(merchant));

				final ItemStack itemstack = slot.getStack();
				final List<String> list = itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

				list.add("\247" + Integer.toHexString(priceColour) + price);
				if (currentProblemString != null) {
					list.add("\2474" + currentProblemString);
				}

				displayItemOverlay(itemstack, list, i - k, j - l);

			} else {
				final ItemStack itemstack = slot.getStack();
				displayItemOverlay(itemstack, itemstack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips), i - k, j - l);
			}
		}
		GL11.glPopMatrix();
		drawScreenGUIScreen(i, j, f);
		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glEnable(2929 /* GL_DEPTH_TEST */);
	}

	public void drawScreenGUIScreen(final int i, final int j, final float f) {
		for (int k = 0; k < buttonList.size(); k++) {
			final GuiButton guibutton = (GuiButton) buttonList.get(k);
			guibutton.drawButton(mc, i, j);
		}

	}

	private boolean getIsMouseOverSlot(final Slot slot, int i, int j) {
		final int k = (width - xSize) / 2;
		final int l = (height - ySize) / 2;
		i -= k;
		j -= l;
		return i >= slot.xDisplayPosition - 1 && i < slot.xDisplayPosition + 16 + 1 && j >= slot.yDisplayPosition - 1 && j < slot.yDisplayPosition + 16 + 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();

		if (building != null) {
			final int xStart = (width - xSize) / 2;
			final int yStart = (height - ySize) / 2;

			this.buttonList.add(new MillGuiButton(0, xStart + xSize - 21, yStart + 5, 15, 20, "?"));
		}
	}

	@Override
	protected void mouseClicked(final int x, final int y, final int clickType) {

		if (clickType == 0) {
			final int startx = (width - xSize) / 2;
			final int starty = (height - ySize) / 2;

			final int dx = x - startx;
			final int dy = y - starty;

			if (dy >= 68 && dy <= 74) {
				if (dx >= 216 && dx <= 226) {
					if (sellingRow > 0) {
						sellingRow--;
						updateRows(true, 1, sellingRow);
					}
				} else if (dx >= 230 && dx <= 240) {
					if (sellingRow < container.nbRowSelling - 2) {
						sellingRow++;
						updateRows(true, -1, sellingRow);
					}
				}
			} else if (dy >= 122 && dy <= 127) {
				if (dx >= 216 && dx <= 226) {
					if (buyingRow > 0) {
						buyingRow--;
						updateRows(false, 1, buyingRow);

					}
				} else if (dx >= 230 && dx <= 240) {
					if (buyingRow < container.nbRowBuying - 2) {
						buyingRow++;
						updateRows(false, -1, buyingRow);

					}
				}
			}

		}

		super.mouseClicked(x, y, clickType);
	}

	private void updateRows(final boolean selling, final int change, final int row) {

		int pos = 0;

		for (final Object o : container.inventorySlots) {
			final Slot slot = (Slot) o;
			if (slot instanceof TradeSlot) {
				final TradeSlot tradeSlot = (TradeSlot) slot;
				if (tradeSlot.sellingSlot == selling) {
					tradeSlot.yDisplayPosition += 18 * change;

					if (pos / 13 < row || pos / 13 > row + 1) {// out of display
						if (tradeSlot.xDisplayPosition > 0) {
							tradeSlot.xDisplayPosition = tradeSlot.xDisplayPosition - 1000;
						}
					} else {
						if (tradeSlot.xDisplayPosition < 0) {
							tradeSlot.xDisplayPosition = tradeSlot.xDisplayPosition + 1000;
						}
					}

					pos++;
				}
			} else if (slot instanceof MerchantSlot && selling) {
				final MerchantSlot merchantSlot = (MerchantSlot) slot;
				merchantSlot.yDisplayPosition += 18 * change;

				if (pos / 13 < row || pos / 13 > row + 1) {// out of display
					if (merchantSlot.xDisplayPosition > 0) {
						merchantSlot.xDisplayPosition = merchantSlot.xDisplayPosition - 1000;
					}
				} else {
					if (merchantSlot.xDisplayPosition < 0) {
						merchantSlot.xDisplayPosition = merchantSlot.xDisplayPosition + 1000;
					}
				}

				pos++;
			}
		}

	}

}
