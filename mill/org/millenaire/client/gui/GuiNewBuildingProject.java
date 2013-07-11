package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.construction.BuildingPlanSet;
import org.millenaire.common.forge.Mill;


public class GuiNewBuildingProject extends GuiText {

	private static final int NBBUTTONLINE=6;
	private final Building townhall;
	private Vector<BuildingPlanSet> possibleBuildings=new Vector<BuildingPlanSet>();
	private final Point pos;
	private int nbbuttonpages;
	private final EntityPlayer player;


	public GuiNewBuildingProject(EntityPlayer player,Building th,Point p) {

		townhall=th;
		pos=p;
		this.player=player;


	}


	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
			return;

		ClientSender.newBuilding(player, townhall, pos, possibleBuildings.get(guibutton.id).key);
		closeWindow();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void buttonPagination() {

		super.buttonPagination();

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		int line=0;
		for (int i=(pageNum*(NBBUTTONLINE));(i<((pageNum+1)*(NBBUTTONLINE))) && (i<possibleBuildings.size());i++) {

			buttonList.add(new GuiButton(i, (xStart+(getXSize() / 2)) - 100, yStart+40+(25*line), 200,20, possibleBuildings.get(i).getFullName(player)));

			line++;
		}
	}

	@Override
	protected void customDrawBackground(int i, int j, float f) {


	}

	@Override
	protected void customDrawScreen(int i, int j, float f) {

	}

	@Override
	public void decrementPage() {
		super.decrementPage();
		buttonPagination();
	}

	@Override
	public int getLineSizeInPx() {
		return 195;
	}

	@Override
	protected int getNbPage() {
		return Math.max(descText.size(),nbbuttonpages);
	}

	@Override
	public int getPageSize() {
		return 19;
	}

	ResourceLocation background=new ResourceLocation(Mill.modId,"/textures/gui/ML_panel.png");

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

	@SuppressWarnings("unchecked")
	@Override
	public void initData() {


		final Vector<Line> text=new Vector<Line>();

		text.add(new Line(townhall.getVillageQualifiedName()));
		text.add(new Line());
		text.add(new Line(MLN.string("ui.selectabuildingproject")));

		final Vector<Vector<Line>> pages = new Vector<Vector<Line>>();
		pages.add(text);
		descText=pages;

		possibleBuildings = (Vector<BuildingPlanSet>) townhall.villageType.coreBuildings.clone();

		for (int i=possibleBuildings.size()-1;i>=0;i--) {
			if (!townhall.isValidProject(possibleBuildings.get(i).getBuildingProject())) {
				possibleBuildings.remove(i);
			}
		}

		nbbuttonpages=(int)Math.ceil((possibleBuildings.size()*1.0)/NBBUTTONLINE);
	}



}
