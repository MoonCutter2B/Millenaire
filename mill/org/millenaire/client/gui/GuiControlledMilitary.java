package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.core.MillCommonUtilities;

public class GuiControlledMilitary extends GuiText {

	private class VillageRelation implements Comparable<VillageRelation> {

		int relation;
		Point pos;
		String name;

		VillageRelation(Point p, int r,String name) {
			relation=r;
			pos=p;
			this.name=name;
		}

		@Override
		public int compareTo(VillageRelation arg0) {
			return name.compareTo(arg0.name);
		}
	}

	public static class GuiButtonDiplomacy extends MillGuiButton {

		public static final int REL_GOOD=100;
		public static final int REL_NEUTRAL=0;
		public static final int REL_BAD=-100;
		public static final int REL=0;
		public static final int RAID=1;
		public static final int RAIDCANCEL=2;

		public Point targetVillage;
		public int data=0;

		public GuiButtonDiplomacy(Point targetVillage, int id,int data, String s) {
			super(id, 0,0,0,0, s);
			this.targetVillage=targetVillage;
			this.data=data;
		}
	}

	private final Building townhall;
	private final EntityPlayer player;

	public GuiControlledMilitary(net.minecraft.entity.player.EntityPlayer player,Building th) {

		townhall=th;
		this.player=player;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
			return;

		final GuiButtonDiplomacy gbp=(GuiButtonDiplomacy)guibutton;

		if (gbp.id==GuiButtonDiplomacy.REL) {
			ClientSender.controlledMilitaryDiplomacy(player, townhall, gbp.targetVillage, gbp.data);
		} else if (gbp.id==GuiButtonDiplomacy.RAID) {
			ClientSender.controlledMilitaryPlanRaid(player, townhall, gbp.targetVillage);
		} else if (gbp.id==GuiButtonDiplomacy.RAIDCANCEL) {
			ClientSender.controlledMilitaryCancelRaid(player, townhall);
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
		text.add(new Line(MLN.string("ui.controldiplomacy")));
		text.add(new Line());

		final ArrayList<VillageRelation> relations=new ArrayList<VillageRelation>();

		for (final Point p : townhall.getKnownVillages()) {
			final Building b = townhall.mw.getBuilding(p);
			if (b!=null)
				relations.add(new VillageRelation(p,townhall.getRelationWithVillage(p),b.getVillageQualifiedName()));
		}

		Collections.sort(relations);

		for (final VillageRelation vr : relations) {
			final Building b = townhall.mw.getBuilding(vr.pos);
			if (b!=null) {
				String col="";

				if (vr.relation>Building.RELATION_VERYGOOD) {
					col=DARKGREEN;
				} else if (vr.relation>Building.RELATION_DECENT) {
					col=DARKBLUE;
				} else if (vr.relation<=Building.RELATION_OPENCONFLICT) {
					col=DARKRED;
				} else if (vr.relation<=Building.RELATION_BAD) {
					col=LIGHTRED;
				}

				text.add(new Line(col+MLN.string("ui.villagerelations",b.getVillageQualifiedName(),b.villageType.name,b.culture.getCultureGameName(),MLN.string(MillCommonUtilities.getRelationName(vr.relation))+" ("+vr.relation+")"),false));

				GuiButtonDiplomacy relGood=new GuiButtonDiplomacy(vr.pos,GuiButtonDiplomacy.REL,GuiButtonDiplomacy.REL_GOOD,MLN.string("ui.relgood"));
				GuiButtonDiplomacy relNeutral=new GuiButtonDiplomacy(vr.pos,GuiButtonDiplomacy.REL,GuiButtonDiplomacy.REL_NEUTRAL,MLN.string("ui.relneutral"));
				GuiButtonDiplomacy relBad=new GuiButtonDiplomacy(vr.pos,GuiButtonDiplomacy.REL,GuiButtonDiplomacy.REL_BAD,MLN.string("ui.relbad"));
				
				text.add(new Line(relGood,relNeutral,relBad));
				text.add(new Line(false));
				
				
				
				if (townhall.raidTarget==null) {
					GuiButtonDiplomacy raid=new GuiButtonDiplomacy(vr.pos,GuiButtonDiplomacy.RAID,GuiButtonDiplomacy.REL_BAD,MLN.string("ui.raid"));
					text.add(new Line(raid));
					text.add(new Line(false));
				} else {
					if (townhall.raidStart>0) {
						if (townhall.raidTarget.equals(vr.pos)) {
							text.add(new Line(DARKRED+MLN.string("ui.raidinprogress")));
						} else {
							text.add(new Line(DARKRED+MLN.string("ui.otherraidinprogress")));
						}
					} else {
						if (townhall.raidTarget.equals(vr.pos)) {
							GuiButtonDiplomacy raid=new GuiButtonDiplomacy(vr.pos,GuiButtonDiplomacy.RAIDCANCEL,0,MLN.string("ui.raidcancel"));
							text.add(new Line(raid));
							text.add(new Line(false));
							text.add(new Line(LIGHTRED+MLN.string("ui.raidplanned")));
							
						} else {
							GuiButtonDiplomacy raid=new GuiButtonDiplomacy(vr.pos,GuiButtonDiplomacy.RAID,GuiButtonDiplomacy.REL_BAD,MLN.string("ui.raid"));
							text.add(new Line(raid));
							text.add(new Line(false));
							text.add(new Line(LIGHTRED+MLN.string("ui.otherraidplanned")));
						}
					}
				}
				text.add(new Line());
			}
		}

		final Vector<Vector<Line>> pages = new Vector<Vector<Line>>();
		pages.add(text);
		
		Vector<Vector<String>> milpages = TileEntityPanel.generateMilitary(player, townhall);
		
		for (Vector<String> textPage : milpages) {
			Vector<Line> page=new Vector<Line>();
			
			for (String s : textPage) {
				page.add(new Line(s));
			}
			pages.add(page);
		}
		
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
