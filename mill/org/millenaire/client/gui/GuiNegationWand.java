package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.forge.Mill;

public class GuiNegationWand extends GuiText {


	private final Building th;
	private final EntityPlayer player;

	public GuiNegationWand(EntityPlayer player, Building th) {
		this.th=th;
		this.player=player;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
			return;

		if(guibutton.id == 0)
		{
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

		buttonList.add(new GuiButton(1, (xStart+(getXSize() / 2)) - 100, (yStart+getYSize())-40, 95,20, MLN.string("negationwand.cancel")));
		buttonList.add(new GuiButton(0, xStart+(getXSize() / 2) + 5, (yStart+getYSize())-40, 95,20, MLN.string("negationwand.confirm")));

	}

	@Override
	protected void customDrawBackground(int i, int j, float f) {

	}

	@Override
	protected void customDrawScreen(int i, int j, float f) {

	}


	@Override
	public boolean doesGuiPauseGame()
	{
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

	ResourceLocation background=new ResourceLocation(Mill.modId,"/graphics/gui/ML_quest.png");

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

		descText=new Vector<Vector<Line>>();

		final Vector<Line> page = new Vector<Line>();
		page.add(new Line(MLN.string("negationwand.confirmmessage",th.villageType.name)));
		descText.add(page);

		descText=adjustText(descText);


		pageNum=0;

		Keyboard.enableRepeatEvents(true);
	}

}
