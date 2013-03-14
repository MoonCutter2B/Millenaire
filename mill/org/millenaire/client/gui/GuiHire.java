package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;

public class GuiHire extends GuiText {

	private static final int REPUTATION_NEEDED = 64*64;
	public static final int BUTTON_CLOSE = 0;
	public static final int BUTTON_HIRE = 1;
	public static final int BUTTON_EXTEND = 2;
	public static final int BUTTON_RELEASE = 3;

	private final MillVillager villager;
	private final EntityPlayer player;

	public GuiHire(EntityPlayer player, MillVillager villager) {
		this.villager=villager;
		this.player=player;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
			return;

		if(guibutton.id == BUTTON_CLOSE) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		} else if (guibutton.id == BUTTON_HIRE) {
			ClientSender.hireHire(player, villager);
			refreshContent();
		} else if (guibutton.id == BUTTON_EXTEND) {
			ClientSender.hireExtend(player, villager);
			refreshContent();
		} else if (guibutton.id == BUTTON_RELEASE) {
			ClientSender.hireRelease(player, villager);
			refreshContent();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buttonPagination() {

		super.buttonPagination();

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		if (villager.hiredBy!=null) {
			if (MillCommonUtilities.countMoney(player.inventory)>=villager.getHireCost(player)) {
				buttonList.add(new GuiButton(BUTTON_EXTEND, (xStart+(getXSize() / 2)) -100, (yStart+getYSize())-40, 63,20, MLN.string("hire.extend")));
			}
			buttonList.add(new GuiButton(BUTTON_RELEASE, (xStart+(getXSize() / 2)) - 32, (yStart+getYSize())-40, 64,20, MLN.string("hire.release")));
			buttonList.add(new GuiButton(BUTTON_CLOSE, xStart+(getXSize() / 2) + 37, (yStart+getYSize())-40, 63,20, MLN.string("hire.close")));

		} else {
			if ((villager.getTownHall().getReputation(player.username)>=REPUTATION_NEEDED) && (MillCommonUtilities.countMoney(player.inventory)>=villager.getHireCost(player))) {
				buttonList.add(new GuiButton(BUTTON_HIRE, (xStart+(getXSize() / 2)) - 100, (yStart+getYSize())-40, 95, 20, MLN.string("hire.hire")));
			}
			buttonList.add(new GuiButton(BUTTON_CLOSE, (xStart+(getXSize() / 2)) + 5, (yStart+getYSize())-40, 95, 20, MLN.string("hire.close")));
		}

	}

	@Override
	protected void customDrawBackground(int i, int j, float f) {
	}

	@Override
	protected void customDrawScreen(int i, int j, float f) {
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}


	private Vector<Vector<Line>> getData() {

		final Vector<Line> text=new Vector<Line>();

		text.add(new Line(villager.getName()+", "+villager.getNativeOccupationName()));
		text.add(new Line());

		if (villager.hiredBy!=null) {
			text.add(new Line(MLN.string("hire.hiredvillager",""+Math.round((villager.hiredUntil-villager.worldObj.getWorldTime())/1000),Keyboard.getKeyName(MLN.keyAggressiveEscorts))));
		} else if (villager.getTownHall().getReputation(player.username)>=REPUTATION_NEEDED) {
			text.add(new Line(MLN.string("hire.hireablevillager")));
		} else {
			text.add(new Line(MLN.string("hire.hireablevillagernoreputation")));
		}
		text.add(new Line());
		text.add(new Line(MLN.string("hire.health")+": "+(villager.getHealth()*0.5)+"/"+(villager.getMaxHealth()*0.5)));
		text.add(new Line(MLN.string("hire.strength")+": "+villager.getAttackStrength()));
		text.add(new Line(MLN.string("hire.cost")+": "+MillCommonUtilities.getShortPrice(villager.getHireCost(player))));

		final Vector<Vector<Line>> ftext=new Vector<Vector<Line>>();
		ftext.add(text);

		return adjustText(ftext);
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
	public String getPNGPath() {
		return "/graphics/gui/ML_quest.png";
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
		refreshContent();
	}

	private void refreshContent() {
		descText=getData();
		buttonPagination();
	}
}
