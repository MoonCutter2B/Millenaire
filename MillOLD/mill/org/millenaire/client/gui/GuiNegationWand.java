package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
import org.millenaire.common.building.Building;
import org.millenaire.common.forge.Mill;

public class GuiNegationWand extends GuiText {

	private final Building th;
	private final EntityPlayer player;

	ResourceLocation background = new ResourceLocation(Mill.modId, "textures/gui/ML_quest.png");

	public GuiNegationWand(final EntityPlayer player, final Building th) {
		this.th = th;
		this.player = player;
	}

	@Override
	protected void actionPerformed(final GuiButton guibutton) {
		if (!guibutton.enabled) {
			return;
		}

		if (guibutton.id == 0) {
			ClientSender.negationWand(player, th);
		}

		mc.displayGuiScreen(null);
		mc.setIngameFocus();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buttonPagination() {

		super.buttonPagination();

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		buttonList.add(new GuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, MLN.string("negationwand.cancel")));
		buttonList.add(new GuiButton(0, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, MLN.string("negationwand.confirm")));

	}

	@Override
	protected void customDrawBackground(final int i, final int j, final float f) {

	}

	@Override
	protected void customDrawScreen(final int i, final int j, final float f) {

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public int getLineSizeInPx() {
		return 240;
	}

	@Override
	public int getPageSize() {
		return 16;
	}

	@Override
	public ResourceLocation getPNGPath() {
		return background;
	}

	@Override
	public int getXSize() {
		return 256;
	}

	@Override
	public int getYSize() {
		return 220;
	}

	@Override
	public void initData() {

		descText = new ArrayList<List<Line>>();

		final List<Line> page = new ArrayList<Line>();
		page.add(new Line(MLN.string("negationwand.confirmmessage", th.villageType.name)));
		descText.add(page);

		descText = adjustText(descText);

		pageNum = 0;

		Keyboard.enableRepeatEvents(true);
	}

}
