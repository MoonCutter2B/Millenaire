package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.Quest.QuestStep;
import org.millenaire.common.UserProfile;
import org.millenaire.common.forge.Mill;


public class GuiQuest extends GuiText {


	private final MillVillager villager;
	private final EntityPlayer player;

	private boolean showOk=false;
	private int type;
	private boolean firstStep;

	public GuiQuest(EntityPlayer player, MillVillager villager) {
		this.villager=villager;
		this.player=player;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
			return;

		final UserProfile profile=Mill.proxy.getClientProfile();

		if(guibutton.id == 0)
		{
			final QuestInstance qi=profile.villagersInQuests.get(villager.villager_id);
			final boolean firstStep=(qi.currentStep==0);
			final String res=qi.completeStep(player,villager);
			ClientSender.questCompleteStep(player, villager);
			initStatus(1,res,firstStep);
		} else if (guibutton.id == 1) {
			final QuestInstance qi=profile.villagersInQuests.get(villager.villager_id);
			final boolean firstStep=(qi.currentStep==0);
			final String res=qi.refuseQuest(player,villager);
			ClientSender.questRefuse(player, villager);
			initStatus(2,res,firstStep);
		} else {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buttonPagination() {

		super.buttonPagination();

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		if (type==0) {
			if (firstStep) {
				if (showOk) {
					controlList.add(new GuiButton(1, (xStart+(getXSize() / 2)) - 100, (yStart+getYSize())-40, 95,20, MLN.string("quest.refuse")));
					controlList.add(new GuiButton(0, xStart+(getXSize() / 2) + 5, (yStart+getYSize())-40, 95,20, MLN.string("quest.accept")));
				} else {
					controlList.add(new GuiButton(1, (xStart+(getXSize() / 2)) - 100, (yStart+getYSize())-40, 95,20, MLN.string("quest.refuse")));
					controlList.add(new GuiButton(2, xStart+(getXSize() / 2) + 5, (yStart+getYSize())-40, 95,20, MLN.string("quest.close")));
				}
			} else {
				if (showOk) {
					controlList.add(new GuiButton(0, (xStart+(getXSize() / 2)) - 100, (yStart+getYSize())-40, MLN.string("quest.continue")));
				} else {
					controlList.add(new GuiButton(2, (xStart+(getXSize() / 2)) - 100, (yStart+getYSize())-40, MLN.string("quest.close")));
				}
			}
		} else {
			controlList.add(new GuiButton(2, (xStart+(getXSize() / 2)) - 100, (yStart+getYSize())-40, MLN.string("quest.close")));
		}


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


	private Vector<Vector<Line>> getData(int type,String baseText) {

		final Vector<Line> text=new Vector<Line>();

		String game="";
		if (villager.getGameOccupationName(player.username).length()>0) {
			game=" ("+villager.getGameOccupationName(player.username)+")";
		}

		text.add(new Line(villager.getName()+", "+villager.getNativeOccupationName()+game));
		text.add(new Line());
		text.add(new Line(baseText.replaceAll("\\$name", player.username)));

		final UserProfile profile=Mill.proxy.getClientProfile();

		if (type==0) {
			final QuestStep step=profile.villagersInQuests.get(villager.villager_id).getCurrentStep();

			final String error=step.lackingConditions(player);

			if (error!=null) {
				text.add(new Line());
				text.add(new Line(error));
				showOk=false;
			} else {
				showOk=true;
			}
		}

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
		final UserProfile profile=Mill.proxy.getClientProfile();

		final String baseText=profile.villagersInQuests.get(villager.villager_id).getDescription(profile);
		final boolean firstStep=(profile.villagersInQuests.get(villager.villager_id).currentStep==0);

		initStatus(0,baseText,firstStep);
	}

	private void initStatus(int type,String baseText,boolean firstStep) {

		pageNum=0;
		this.type=type;
		this.firstStep=firstStep;
		descText=getData(type,baseText);
		buttonPagination();
	}

	@Override
	protected void keyTyped(char c, int i)
	{
		if(i == 1) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();

			ClientSender.villagerInteractSpecial(player, villager);
		}
	}
}

