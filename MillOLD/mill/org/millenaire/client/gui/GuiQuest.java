package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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

	private boolean showOk = false;
	private int type;
	private boolean firstStep;

	ResourceLocation background = new ResourceLocation(Mill.modId, "textures/gui/ML_quest.png");

	public GuiQuest(final EntityPlayer player, final MillVillager villager) {
		this.villager = villager;
		this.player = player;
	}

	@Override
	protected void actionPerformed(final GuiButton guibutton) {
		if (!guibutton.enabled) {
			return;
		}

		final UserProfile profile = Mill.proxy.getClientProfile();

		if (guibutton.id == 0) {
			final QuestInstance qi = profile.villagersInQuests.get(villager.villager_id);
			final boolean firstStep = qi.currentStep == 0;
			final String res = qi.completeStep(player, villager);
			ClientSender.questCompleteStep(player, villager);
			initStatus(1, res, firstStep);
		} else if (guibutton.id == 1) {
			final QuestInstance qi = profile.villagersInQuests.get(villager.villager_id);
			final boolean firstStep = qi.currentStep == 0;
			final String res = qi.refuseQuest(player, villager);
			ClientSender.questRefuse(player, villager);
			initStatus(2, res, firstStep);
		} else {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
			ClientSender.villagerInteractSpecial(player, villager);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buttonPagination() {

		super.buttonPagination();

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		if (type == 0) {
			if (firstStep) {
				if (showOk) {
					buttonList.add(new GuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, MLN.string("quest.refuse")));
					buttonList.add(new GuiButton(0, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, MLN.string("quest.accept")));
				} else {
					buttonList.add(new GuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, MLN.string("quest.refuse")));
					buttonList.add(new GuiButton(2, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, MLN.string("quest.close")));
				}
			} else {
				if (showOk) {
					buttonList.add(new GuiButton(0, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, MLN.string("quest.continue")));
				} else {
					buttonList.add(new GuiButton(2, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, MLN.string("quest.close")));
				}
			}
		} else {
			buttonList.add(new GuiButton(2, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, MLN.string("quest.close")));
		}

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

	private List<List<Line>> getData(final int type, final String baseText) {

		final List<Line> text = new ArrayList<Line>();

		String game = "";
		if (villager.getGameOccupationName(player.getDisplayName()).length() > 0) {
			game = " (" + villager.getGameOccupationName(player.getDisplayName()) + ")";
		}

		text.add(new Line(villager.getName() + ", " + villager.getNativeOccupationName() + game));
		text.add(new Line());
		text.add(new Line(baseText.replaceAll("\\$name", player.getDisplayName())));

		final UserProfile profile = Mill.proxy.getClientProfile();

		if (type == 0) {
			final QuestStep step = profile.villagersInQuests.get(villager.villager_id).getCurrentStep();

			final String error = step.lackingConditions(player);

			if (error != null) {
				text.add(new Line());
				text.add(new Line(error));
				showOk = false;
			} else {
				showOk = true;
			}
		}

		final List<List<Line>> ftext = new ArrayList<List<Line>>();
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
		final UserProfile profile = Mill.proxy.getClientProfile();

		final String baseText = profile.villagersInQuests.get(villager.villager_id).getDescription(profile);
		final boolean firstStep = profile.villagersInQuests.get(villager.villager_id).currentStep == 0;

		initStatus(0, baseText, firstStep);
	}

	private void initStatus(final int type, final String baseText, final boolean firstStep) {

		pageNum = 0;
		this.type = type;
		this.firstStep = firstStep;
		descText = getData(type, baseText);
		buttonPagination();
	}

	@Override
	protected void keyTyped(final char c, final int i) {
		if (i == 1) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();

			ClientSender.villagerInteractSpecial(player, villager);
		}
	}
}
