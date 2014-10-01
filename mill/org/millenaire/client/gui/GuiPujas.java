package org.millenaire.client.gui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.ContainerPuja;
import org.millenaire.common.ContainerPuja.MoneySlot;
import org.millenaire.common.ContainerPuja.OfferingSlot;
import org.millenaire.common.ContainerPuja.ToolSlot;
import org.millenaire.common.MLN;
import org.millenaire.common.PujaSacrifice;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class GuiPujas extends GuiContainer {
	private final Building temple;
	private final EntityPlayer player;
	private final Method drawSlotInventory;

	private static final ResourceLocation texturePujas = new ResourceLocation(
			Mill.modId, "textures/gui/ML_pujas.png");
	private static final ResourceLocation textureSacrifices = new ResourceLocation(
			Mill.modId, "textures/gui/ML_mayansacrifices.png");

	public GuiPujas(final EntityPlayer player, final Building temple) {
		super(new ContainerPuja(player, temple));

		ySize = 188;

		this.temple = temple;
		this.player = player;

		if (MLN.LogPujas >= MLN.DEBUG) {
			MLN.debug(this, "Opening shrine GUI");
		}

		drawSlotInventory = MillCommonUtilities
				.getDrawSlotInventoryMethod(this);
	}

	/*
	 * Method extracted from code in GuiContainer, MC 1.2.5
	 */
	public void displayItemOverlay(final ItemStack stack,
			final List<String> list, final int posx, final int posy) {
		if (list != null && list.size() > 0) {
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
			itemRender.zLevel = 300F;
			final int k3 = 0xf0100010;
			drawGradientRect(j2 - 3, l2 - 4, j2 + i3 + 3, l2 - 3, k3, k3);
			drawGradientRect(j2 - 3, l2 + j3 + 3, j2 + i3 + 3, l2 + j3 + 4, k3,
					k3);
			drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 + j3 + 3, k3, k3);
			drawGradientRect(j2 - 4, l2 - 3, j2 - 3, l2 + j3 + 3, k3, k3);
			drawGradientRect(j2 + i3 + 3, l2 - 3, j2 + i3 + 4, l2 + j3 + 3, k3,
					k3);
			final int l3 = 0x505000ff;
			final int i4 = (l3 & 0xfefefe) >> 1 | l3 & 0xff000000;
			drawGradientRect(j2 - 3, l2 - 3 + 1, j2 - 3 + 1, l2 + j3 + 3 - 1,
					l3, i4);
			drawGradientRect(j2 + i3 + 2, l2 - 3 + 1, j2 + i3 + 3, l2 + j3 + 3
					- 1, l3, i4);
			drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 - 3 + 1, l3, l3);
			drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3, i4,
					i4);

			for (int j4 = 0; j4 < list.size(); j4++) {
				String s = list.get(j4);

				if (j4 == 0 && stack != null) {
					s = new StringBuilder()
							.append("\247")
							.append(Integer.toHexString(stack.getRarity().rarityColor
									.getFormattingCode())).append(s).toString();

				} else {
					s = new StringBuilder().append("\2477").append(s)
							.toString();
				}

				fontRendererObj.drawStringWithShadow(s, j2, l2, -1);

				if (j4 == 0) {
					l2 += 2;
				}

				l2 += 10;
			}

			zLevel = 0.0F;
			itemRender.zLevel = 0.0F;
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(final float par1,
			final int par2, final int par3) {

		try {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (temple.pujas != null
					&& temple.pujas.type == PujaSacrifice.MAYAN) {
				mc.renderEngine.bindTexture(textureSacrifices);
			} else {
				mc.renderEngine.bindTexture(texturePujas);
			}
			final int j = (width - xSize) / 2;
			final int k = (height - ySize) / 2;
			drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

			if (temple.pujas != null) {

				int linePos = 0;
				int colPos = 0;

				for (int cp = 0; cp < temple.pujas.getTargets().size(); cp++) {
					if (temple.pujas.currentTarget == temple.pujas.getTargets()
							.get(cp)) {
						drawTexturedModalRect(j + getTargetXStart() + colPos
								* getButtonWidth(), k + getTargetYStart()
								+ getButtonHeight() * linePos, temple.pujas
								.getTargets().get(cp).startXact, temple.pujas
								.getTargets().get(cp).startYact,
								getButtonWidth(), getButtonHeight());
					} else {
						drawTexturedModalRect(j + getTargetXStart() + colPos
								* getButtonWidth(), k + getTargetYStart()
								+ getButtonHeight() * linePos, temple.pujas
								.getTargets().get(cp).startX, temple.pujas
								.getTargets().get(cp).startY, getButtonWidth(),
								getButtonHeight());
					}

					colPos++;

					if (colPos >= getNbPerLines()) {
						colPos = 0;
						linePos++;
					}

				}
				int progress = temple.pujas.getPujaProgressScaled(13);
				drawTexturedModalRect(j + 27, k + 39 + 13 - progress, 176,
						13 - progress, 15, progress);

				progress = temple.pujas.getOfferingProgressScaled(16);
				drawTexturedModalRect(j + 84, k + 63 + 16 - progress, 176,
						31 + 16 - progress, 19, progress);
			}
		} catch (final Exception e) {
			MLN.printException("Exception in drawScreen: ", e);
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(final int par1,
			final int par2) {
		if (temple.pujas.type == PujaSacrifice.MAYAN) {
			fontRendererObj.drawString(MLN.string("sacrifices.offering"), 8, 6,
					0x404040);
			fontRendererObj.drawString(MLN.string("sacrifices.panditfee"), 8,
					75, 0x404040);
		} else {
			fontRendererObj.drawString(MLN.string("pujas.offering"), 8, 6,
					0x404040);
			fontRendererObj.drawString(MLN.string("pujas.panditfee"), 8, 75,
					0x404040);
		}

		fontRendererObj.drawString(
				StatCollector.translateToLocal("container.inventory"), 8,
				ySize - 104 + 2, 0x404040);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drawScreen(final int x, final int y, final float f) {
		drawDefaultBackground();
		final int k = (width - xSize) / 2;
		final int l = (height - ySize) / 2;
		drawGuiContainerBackgroundLayer(f, x, y);
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(k, l, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
		Slot slot = null;

		for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); i1++) {
			final Slot slot1 = (Slot) inventorySlots.inventorySlots.get(i1);
			drawSlotInventory(slot1);

			if (getIsMouseOverSlot(slot1, x, y)) {
				slot = slot1;

				GL11.glDisable(2896 /* GL_LIGHTING */);
				GL11.glDisable(2929 /* GL_DEPTH_TEST */);
				final int j1 = slot1.xDisplayPosition;
				final int l1 = slot1.yDisplayPosition;
				drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80ffffff,
						0x80ffffff);
				GL11.glEnable(2896 /* GL_LIGHTING */);
				GL11.glEnable(2929 /* GL_DEPTH_TEST */);
			}
		}

		final InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
		if (inventoryplayer.getItemStack() != null) {
			GL11.glTranslatef(0.0F, 0.0F, 32F);
			itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine,
					inventoryplayer.getItemStack(), x - k - 8, y - l - 8);
			itemRender.renderItemOverlayIntoGUI(fontRendererObj,
					mc.renderEngine, inventoryplayer.getItemStack(), x - k - 8,
					y - l - 8);
		}
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDisable(2929 /* GL_DEPTH_TEST */);
		drawGuiContainerForegroundLayer(x, y);
		if (inventoryplayer.getItemStack() == null && slot != null) {
			List<String> list = null;
			ItemStack itemstack = null;

			if (slot.getHasStack()) {
				itemstack = slot.getStack();
				list = itemstack.getTooltip(this.mc.thePlayer,
						this.mc.gameSettings.advancedItemTooltips);
			} else if (slot instanceof OfferingSlot) {
				list = new ArrayList<String>();
				list.add("\2476" + MLN.string("pujas.offeringslot"));
				list.add("\2477" + MLN.string("pujas.offeringslot2"));
			} else if (slot instanceof MoneySlot) {
				list = new ArrayList<String>();
				list.add("\2476" + MLN.string("pujas.moneyslot"));
			} else if (slot instanceof ToolSlot) {
				list = new ArrayList<String>();
				list.add("\2476" + MLN.string("pujas.toolslot"));
			}

			displayItemOverlay(itemstack, list, x - k, y - l);

		}

		final int startx = (width - xSize) / 2;
		final int starty = (height - ySize) / 2;

		if (temple.pujas != null) {

			int linePos = 0;
			int colPos = 0;

			for (int cp = 0; cp < temple.pujas.getTargets().size(); cp++) {

				if (x > startx + getTargetXStart() + colPos * getButtonWidth()
						&& x < startx + getTargetXStart() + (colPos + 1)
								* getButtonWidth()
						&& y > starty + getTargetYStart() + getButtonHeight()
								* linePos
						&& y < starty + getTargetYStart() + getButtonHeight()
								* (linePos + 1)) {
					final String s = MLN.string(temple.pujas.getTargets().get(
							cp).mouseOver);
					final int stringlength = fontRendererObj.getStringWidth(s);

					drawGradientRect(x - startx + 5, y - starty - 3, x - startx
							+ stringlength + 3, y - starty + 8 + 3, 0xc0000000,
							0xc0000000);
					fontRendererObj.drawString(s, x + 8 - startx, y - starty,
							0xA0A0A0);
				}

				colPos++;

				if (colPos >= getNbPerLines()) {
					colPos = 0;
					linePos++;
				}

			}
		}

		GL11.glPopMatrix();
		drawScreenGUIScreen(x, y, f);
		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glEnable(2929 /* GL_DEPTH_TEST */);
	}

	public void drawScreenGUIScreen(final int i, final int j, final float f) {
		try {
			for (int k = 0; k < buttonList.size(); k++) {
				final GuiButton guibutton = (GuiButton) buttonList.get(k);
				guibutton.drawButton(mc, i, j);
			}
		} catch (final Exception e) {
			MLN.printException("Exception in drawScreenGUIScreen: ", e);
		}

	}

	public void drawSlotInventory(final Slot slot) {

		try {
			drawSlotInventory.invoke(this, slot);
		} catch (final Exception e) {
			MLN.printException(
					"Exception when trying to access drawSlotInventory", e);
		}
	}

	private int getButtonHeight() {
		if (temple.pujas == null) {
			return 0;
		}

		if (temple.pujas.type == PujaSacrifice.PUJA) {
			return 17;
		}

		if (temple.pujas.type == PujaSacrifice.MAYAN) {
			return 20;
		}

		return 0;
	}

	private int getButtonWidth() {
		if (temple.pujas == null) {
			return 0;
		}

		if (temple.pujas.type == PujaSacrifice.PUJA) {
			return 46;
		}

		if (temple.pujas.type == PujaSacrifice.MAYAN) {
			return 20;
		}

		return 0;
	}

	private boolean getIsMouseOverSlot(final Slot slot, int i, int j) {
		final int k = (width - xSize) / 2;
		final int l = (height - ySize) / 2;
		i -= k;
		j -= l;
		return i >= slot.xDisplayPosition - 1
				&& i < slot.xDisplayPosition + 16 + 1
				&& j >= slot.yDisplayPosition - 1
				&& j < slot.yDisplayPosition + 16 + 1;
	}

	private int getNbPerLines() {
		if (temple.pujas == null) {
			return 1;
		}

		if (temple.pujas.type == PujaSacrifice.PUJA) {
			return 1;
		}

		if (temple.pujas.type == PujaSacrifice.MAYAN) {
			return 3;
		}

		return 1;
	}

	private int getTargetXStart() {
		if (temple.pujas == null) {
			return 0;
		}

		if (temple.pujas.type == PujaSacrifice.PUJA) {
			return 118;
		}

		if (temple.pujas.type == PujaSacrifice.MAYAN) {
			return 110;
		}

		return 0;
	}

	private int getTargetYStart() {
		if (temple.pujas == null) {
			return 0;
		}

		if (temple.pujas.type == PujaSacrifice.PUJA) {
			return 22;
		}

		if (temple.pujas.type == PujaSacrifice.MAYAN) {
			return 22;
		}

		return 0;
	}

	@Override
	protected void mouseClicked(final int x, final int y, final int par3) {

		super.mouseClicked(x, y, par3);

		final int startx = (width - xSize) / 2;
		final int starty = (height - ySize) / 2;

		if (temple.pujas != null) {

			int linePos = 0;
			int colPos = 0;

			for (int cp = 0; cp < temple.pujas.getTargets().size(); cp++) {

				if (x > startx + getTargetXStart() + colPos * getButtonWidth()
						&& x < startx + getTargetXStart() + (colPos + 1)
								* getButtonWidth()
						&& y > starty + getTargetYStart() + getButtonHeight()
								* linePos
						&& y < starty + getTargetYStart() + getButtonHeight()
								* (linePos + 1)) {
					ClientSender.pujasChangeEnchantment(player, temple, cp);
				}

				colPos++;

				if (colPos >= getNbPerLines()) {
					colPos = 0;
					linePos++;
				}

			}
		}

	}

}
