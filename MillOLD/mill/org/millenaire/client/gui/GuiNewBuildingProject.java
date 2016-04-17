package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingCustomPlan;
import org.millenaire.common.building.BuildingPlanSet;
import org.millenaire.common.building.IBuildingPlan;
import org.millenaire.common.forge.Mill;

public class GuiNewBuildingProject extends GuiText {

	public static class GuiButtonNewBuilding extends MillGuiButton {

		private final String key;
		private final boolean custom;

		public GuiButtonNewBuilding(final String key, final String label, final boolean custom) {
			super(0, 0, 0, 0, 0, label);
			this.key = key;
			this.custom = custom;
		}
	}

	private final Building townHall;
	private final Point pos;
	private final EntityPlayer player;

	ResourceLocation background = new ResourceLocation(Mill.modId, "textures/gui/ML_panel.png");

	public GuiNewBuildingProject(final EntityPlayer player, final Building th, final Point p) {

		townHall = th;
		pos = p;
		this.player = player;
	}

	@Override
	protected void actionPerformed(final GuiButton guibutton) {
		if (!guibutton.enabled) {
			return;
		}

		final GuiButtonNewBuilding button = (GuiButtonNewBuilding) guibutton;

		if (!button.custom) {
			ClientSender.newBuilding(player, townHall, pos, button.key);
		} else {

			closeWindow();

			final BuildingCustomPlan customBuilding = townHall.culture.getBuildingCustom(button.key);

			if (customBuilding != null) {
				DisplayActions.displayNewCustomBuildingGUI(player, townHall, pos, customBuilding);
			}
		}

	}

	@Override
	protected void customDrawBackground(final int i, final int j, final float f) {

	}

	@Override
	protected void customDrawScreen(final int i, final int j, final float f) {

	}

	@Override
	public void decrementPage() {
		super.decrementPage();
		buttonPagination();
	}

	private String getCustomPlanDesc(final IBuildingPlan customPlan) {
		String desc = "";

		if (customPlan.getGameName() != null && customPlan.getGameName().length() > 0) {
			desc += customPlan.getGameName() + ". ";
		}

		if (customPlan.getMaleResident().size() > 0 || customPlan.getFemaleResident().size() > 0) {
			desc += MLN.string("ui.inhabitants") + ": ";
			boolean first = true;
			for (final String inhabitant : customPlan.getMaleResident()) {
				if (first) {
					first = false;
				} else {
					desc += ", ";
				}
				desc += customPlan.getCulture().getVillagerType(inhabitant).name;
			}
			for (final String inhabitant : customPlan.getFemaleResident()) {
				if (first) {
					first = false;
				} else {
					desc += ", ";
				}
				desc += customPlan.getCulture().getVillagerType(inhabitant).name;
			}
			desc += ". ";
		}

		return desc;
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
	public void incrementPage() {
		super.incrementPage();
		buttonPagination();
	}

	@Override
	public void initData() {

		final List<List<Line>> pages = new ArrayList<List<Line>>();
		List<Line> text = new ArrayList<Line>();

		text.add(new Line(townHall.getVillageQualifiedName()));
		text.add(new Line());
		text.add(new Line(MLN.string("ui.selectabuildingproject")));

		text.add(new Line());
		text.add(new Line(MLN.string("ui.selectabuildingproject_custom")));

		String desc;

		for (final BuildingCustomPlan customBuilding : townHall.villageType.customBuildings) {
			text.add(new Line(new GuiButtonNewBuilding(customBuilding.buildingKey, customBuilding.getNativeName(), true)));
			text.add(new Line(false));
			desc = getCustomPlanDesc(customBuilding).trim();
			if (desc.length() > 0) {
				text.add(new Line(desc));
			}
			text.add(new Line());
		}

		pages.add(text);
		text = new ArrayList<Line>();

		text.add(new Line());
		text.add(new Line(MLN.string("ui.selectabuildingproject_standard")));

		for (final BuildingPlanSet planSet : townHall.villageType.coreBuildings) {
			if (townHall.isValidProject(planSet.getBuildingProject())) {
				text.add(new Line(new GuiButtonNewBuilding(planSet.key, planSet.getNativeName(), false)));
				text.add(new Line(false));
				desc = getCustomPlanDesc(planSet.getFirstStartingPlan()).trim();
				if (desc.length() > 0) {
					text.add(new Line(desc));
				}
				text.add(new Line());
			}
		}

		pages.add(text);

		descText = adjustText(pages);
	}

}
