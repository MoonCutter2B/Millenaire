package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.VillagerRecord;
import org.millenaire.common.construction.BuildingProject;

public class GuiControlledProjects extends GuiText {

	public static class GuiButtonProject extends MillGuiButton {

		public static final int ALLOW_UPGRADES=1;
		public static final int FORBID_UPGRADES=2;
		public static final int CANCEL_BUILDING=3;

		public BuildingProject project;

		public GuiButtonProject(BuildingProject project, int i, String s) {
			super(i, 0,0,0,0, s);
			this.project=project;
		}
	}

	private final Building townhall;
	private Vector<BuildingProject> projects;
	private final EntityPlayer player;



	public GuiControlledProjects(net.minecraft.entity.player.EntityPlayer player,Building th) {

		townhall=th;
		projects=townhall.getFlatProjectList();
		this.player=player;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
			return;

		final GuiButtonProject gbp=(GuiButtonProject)guibutton;

		if (gbp.id==GuiButtonProject.ALLOW_UPGRADES) {
			ClientSender.controlledBuildingsToggleUpgrades(player, townhall, gbp.project, true);
		} else if (gbp.id==GuiButtonProject.FORBID_UPGRADES) {
			ClientSender.controlledBuildingsToggleUpgrades(player, townhall, gbp.project, false);
		} else if (gbp.id==GuiButtonProject.CANCEL_BUILDING) {
			ClientSender.controlledBuildingsForgetBuilding(player, townhall, gbp.project);

			projects=townhall.getFlatProjectList();
		}

		fillData();
	}

	@Override
	protected void customDrawBackground(int i, int j, float f) {


	}

	@Override
	protected void customDrawScreen(int i, int j, float f) {

	}


	private void fillData() {
		final Vector<Line> text=new Vector<Line>();

		text.add(new Line(townhall.getVillageQualifiedName(),false));
		text.add(new Line(false));
		text.add(new Line(MLN.string("ui.controlbuildingprojects")));
		text.add(new Line());

		for (int i=0;(i<projects.size());i++) {
			final BuildingProject project=projects.get(i);

			String status;

			if (project.location.level<0) {
				status=MLN.string("ui.notyetbuilt");
			} else {
				status=MLN.string("ui.level")+": "+(project.location.level+1)+"/"+(project.planSet.plans.get(project.location.getVariation()).length);
			}

			text.add(new Line(project.getFullName(player)+" ("+(char)('A'+project.location.getVariation())+"):",false));
			text.add(new Line(status+", "+townhall.getPos().distanceDirectionShort(project.location.pos),false));

			int nbInhabitants=0;

			if ((project.location!=null) && (project.location.chestPos!=null)) {
				for (final VillagerRecord vr : townhall.vrecords) {
					if (project.location.chestPos.equals(vr.housePos)) {
						nbInhabitants++;
					}
				}
			}


			text.add(new Line(MLN.string("ui.nbinhabitants",""+nbInhabitants)));

			MillGuiButton firstButton=null;
			MillGuiButton secondButton=null;

			if ((project.location.level<(project.planSet.plans.get(project.location.getVariation()).length-1)) && (project.planSet.plans.get(project.location.getVariation()).length>1)) {
				if (project.location.upgradesAllowed) {
					firstButton=new GuiButtonProject(project,GuiButtonProject.FORBID_UPGRADES, MLN.string("ui.forbidupgrades"));
				} else {
					firstButton=new GuiButtonProject(project,GuiButtonProject.ALLOW_UPGRADES,MLN.string("ui.allowupgrades"));
				}
			}

			boolean canForget;

			if ((project.location==null) || ((project.location.getBuilding(townhall.worldObj)!=null) && project.location.getBuilding(townhall.worldObj).isTownhall)) {
				canForget=false;
			} else {
				canForget=(nbInhabitants==0);
			}

			if (canForget) {
				secondButton=new GuiButtonProject(project,GuiButtonProject.CANCEL_BUILDING, MLN.string("ui.cancelbuilding"));
			}

			text.add(new Line(firstButton,secondButton));
			text.add(new Line(false));
			text.add(new Line());
			text.add(new Line());
		}

		final Vector<Vector<Line>> pages = new Vector<Vector<Line>>();
		pages.add(text);
		descText=adjustText(pages);

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
	public String getPNGPath() {
		return "/graphics/gui/ML_panel.png";
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
