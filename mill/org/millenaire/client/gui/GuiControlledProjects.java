package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
import org.millenaire.common.VillagerRecord;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingProject;
import org.millenaire.common.forge.Mill;

public class GuiControlledProjects extends GuiText {

	public static class GuiButtonProject extends MillGuiButton {

		public static final int ALLOW_UPGRADES = 1;
		public static final int FORBID_UPGRADES = 2;
		public static final int CANCEL_BUILDING = 3;

		public BuildingProject project;

		public GuiButtonProject(final BuildingProject project, final int i, final String s) {
			super(i, 0, 0, 0, 0, s);
			this.project = project;
		}
	}

	private final Building townHall;
	private List<BuildingProject> projects;
	private final EntityPlayer player;

	ResourceLocation background = new ResourceLocation(Mill.modId, "textures/gui/ML_panel.png");

	public GuiControlledProjects(final net.minecraft.entity.player.EntityPlayer player, final Building th) {

		townHall = th;
		projects = townHall.getFlatProjectList();
		this.player = player;
	}

	@Override
	protected void actionPerformed(final GuiButton guibutton) {
		if (!guibutton.enabled) {
			return;
		}

		final GuiButtonProject gbp = (GuiButtonProject) guibutton;

		if (gbp.id == GuiButtonProject.ALLOW_UPGRADES) {
			ClientSender.controlledBuildingsToggleUpgrades(player, townHall, gbp.project, true);
		} else if (gbp.id == GuiButtonProject.FORBID_UPGRADES) {
			ClientSender.controlledBuildingsToggleUpgrades(player, townHall, gbp.project, false);
		} else if (gbp.id == GuiButtonProject.CANCEL_BUILDING) {
			ClientSender.controlledBuildingsForgetBuilding(player, townHall, gbp.project);

			projects = townHall.getFlatProjectList();
		}

		fillData();
	}

	@Override
	protected void customDrawBackground(final int i, final int j, final float f) {

	}

	@Override
	protected void customDrawScreen(final int i, final int j, final float f) {

	}

	private void fillData() {
		final List<Line> text = new ArrayList<Line>();

		text.add(new Line(townHall.getVillageQualifiedName(), false));
		text.add(new Line(false));
		text.add(new Line(MLN.string("ui.controlbuildingprojects")));
		text.add(new Line());

		for (int i = 0; i < projects.size(); i++) {
			final BuildingProject project = projects.get(i);

			if (project.planSet != null) {

				String status;

				if (project.location.level < 0) {
					status = MLN.string("ui.notyetbuilt");
				} else {
					status = MLN.string("ui.level") + ": " + (project.location.level + 1) + "/" + project.planSet.plans.get(project.location.getVariation()).length;
				}

				text.add(new Line(project.getFullName(player) + " (" + (char) ('A' + project.location.getVariation()) + "):", false));
				text.add(new Line(status + ", " + townHall.getPos().distanceDirectionShort(project.location.pos), false));

				int nbInhabitants = 0;

				if (project.location != null && project.location.chestPos != null) {
					for (final VillagerRecord vr : townHall.vrecords) {
						if (project.location.chestPos.equals(vr.housePos)) {
							nbInhabitants++;
						}
					}
				}

				text.add(new Line(MLN.string("ui.nbinhabitants", "" + nbInhabitants)));

				MillGuiButton firstButton = null;
				MillGuiButton secondButton = null;

				if (project.location.level < project.planSet.plans.get(project.location.getVariation()).length - 1 && project.planSet.plans.get(project.location.getVariation()).length > 1) {
					if (project.location.upgradesAllowed) {
						firstButton = new GuiButtonProject(project, GuiButtonProject.FORBID_UPGRADES, MLN.string("ui.forbidupgrades"));
					} else {
						firstButton = new GuiButtonProject(project, GuiButtonProject.ALLOW_UPGRADES, MLN.string("ui.allowupgrades"));
					}
				}

				boolean canForget;

				if (project.location == null || project.location.getBuilding(townHall.worldObj) != null && project.location.getBuilding(townHall.worldObj).isTownhall) {
					canForget = false;
				} else {
					canForget = nbInhabitants == 0;
				}

				if (canForget) {
					secondButton = new GuiButtonProject(project, GuiButtonProject.CANCEL_BUILDING, MLN.string("ui.cancelbuilding"));
				}

				text.add(new Line(firstButton, secondButton));
				text.add(new Line(false));
				text.add(new Line());
				text.add(new Line());
			}
		}

		final List<List<Line>> pages = new ArrayList<List<Line>>();
		pages.add(text);
		descText = adjustText(pages);

		buttonPagination();
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
		fillData();
	}

}
