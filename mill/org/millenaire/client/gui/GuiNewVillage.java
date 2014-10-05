package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.UserProfile;
import org.millenaire.common.VillageType;
import org.millenaire.common.forge.Mill;

public class GuiNewVillage extends GuiText {

	private List<VillageType> possibleVillages = new ArrayList<VillageType>();
	private final Point pos;
	private final EntityPlayer player;

	ResourceLocation background = new ResourceLocation(Mill.modId,
			"textures/gui/ML_panel.png");

	public GuiNewVillage(final EntityPlayer player, final Point p) {
		pos = p;
		this.player = player;
	}

	@Override
	protected void actionPerformed(final GuiButton guibutton) {
		if (!guibutton.enabled) {
			return;
		}

		final VillageType village = possibleVillages.get(guibutton.id);

		closeWindow();
		
		if (village.customCentre==null) {
			ClientSender.newVillageCreation(player, pos, village.culture.key,
					village.key);			
		} else {
			DisplayActions.displayNewCustomBuildingGUI(player, pos, village);
		}	
	}

	@Override
	protected void customDrawBackground(final int i, final int j, final float f) {
	}

	@Override
	protected void customDrawScreen(final int i, final int j, final float f) {

	}

	@Override
	public int getLineSizeInPx() {
		return 195;
	}

	@Override
	public int getPageSize() {
		return 19;
	}

	@Override
	public ResourceLocation getPNGPath() {
		return background;
	}

	@Override
	public int getXSize() {
		return 204;
	}

	@Override
	public int getYSize() {
		return 220;
	}

	@Override
	public void initData() {
		final List<Line> text = new ArrayList<Line>();

		text.add(new Line(MLN.string("ui.selectavillage"), false));
		text.add(new Line(false));
		text.add(new Line(MLN.string("ui.leadershipstatus") + ":"));
		text.add(new Line());

		boolean notleader = false;

		final UserProfile profile = Mill.proxy.getClientProfile();

		for (final Culture culture : Culture.ListCultures) {
			if (profile.isTagSet(MillWorld.CULTURE_CONTROL + culture.key)) {
				text.add(new Line(MLN.string("ui.leaderin",
						culture.getCultureGameName())));
			} else {
				text.add(new Line(MLN.string("ui.notleaderin",
						culture.getCultureGameName())));
				notleader = true;
			}
		}

		if (notleader) {
			text.add(new Line());
			text.add(new Line(MLN.string("ui.leaderinstruction")));
		}
		text.add(new Line());

		possibleVillages = VillageType.spawnableVillages(player);

		for (int i = 0; i < possibleVillages.size(); i++) {
			String controlled = "";
			if (possibleVillages.get(i).playerControlled) {
				if (possibleVillages.get(i).customCentre!=null)
					controlled = ", " + MLN.string("ui.controlledcustom");
				else
					controlled = ", " + MLN.string("ui.controlled");
			}

			text.add(new Line(new MillGuiButton(i, 0, 0, 0, 0, possibleVillages
					.get(i).name
					+ " ("
					+ possibleVillages.get(i).culture.getCultureGameName()
					+ controlled + ")")));
			text.add(new Line(false));
			text.add(new Line());
		}

		final List<List<Line>> pages = new ArrayList<List<Line>>();
		pages.add(text);
		descText = adjustText(pages);
	}
}
