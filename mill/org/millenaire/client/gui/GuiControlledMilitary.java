package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class GuiControlledMilitary extends GuiText {

	public static class GuiButtonDiplomacy extends MillGuiButton {

		public static final int REL_GOOD = 100;
		public static final int REL_NEUTRAL = 0;
		public static final int REL_BAD = -100;
		public static final int REL = 0;
		public static final int RAID = 1;
		public static final int RAIDCANCEL = 2;

		public Point targetVillage;
		public int data = 0;

		public GuiButtonDiplomacy(final Point targetVillage, final int id,
				final int data, final String s) {
			super(id, 0, 0, 0, 0, s);
			this.targetVillage = targetVillage;
			this.data = data;
		}
	}

	private class VillageRelation implements Comparable<VillageRelation> {

		int relation;
		Point pos;
		String name;

		VillageRelation(final Point p, final int r, final String name) {
			relation = r;
			pos = p;
			this.name = name;
		}

		@Override
		public int compareTo(final VillageRelation arg0) {
			return name.compareTo(arg0.name);
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null || !(o instanceof VillageRelation)) {
				return false;
			}

			return this.pos.equals(((VillageRelation) o).pos);
		}

		@Override
		public int hashCode() {
			return pos.hashCode();
		}
	}

	private final Building townhall;
	private final EntityPlayer player;

	ResourceLocation background = new ResourceLocation(Mill.modId,
			"textures/gui/ML_panel.png");

	public GuiControlledMilitary(
			final net.minecraft.entity.player.EntityPlayer player,
			final Building th) {

		townhall = th;
		this.player = player;
	}

	@Override
	protected void actionPerformed(final GuiButton guibutton) {
		if (!guibutton.enabled) {
			return;
		}

		final GuiButtonDiplomacy gbp = (GuiButtonDiplomacy) guibutton;

		if (gbp.id == GuiButtonDiplomacy.REL) {
			ClientSender.controlledMilitaryDiplomacy(player, townhall,
					gbp.targetVillage, gbp.data);
		} else if (gbp.id == GuiButtonDiplomacy.RAID) {
			ClientSender.controlledMilitaryPlanRaid(player, townhall,
					gbp.targetVillage);
		} else if (gbp.id == GuiButtonDiplomacy.RAIDCANCEL) {
			ClientSender.controlledMilitaryCancelRaid(player, townhall);
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

		text.add(new Line(townhall.getVillageQualifiedName(), false));
		text.add(new Line(false));
		text.add(new Line(MLN.string("ui.controldiplomacy")));
		text.add(new Line());

		final ArrayList<VillageRelation> relations = new ArrayList<VillageRelation>();

		for (final Point p : townhall.getKnownVillages()) {
			final Building b = townhall.mw.getBuilding(p);
			if (b != null) {
				relations
						.add(new VillageRelation(p, townhall
								.getRelationWithVillage(p), b
								.getVillageQualifiedName()));
			}
		}

		Collections.sort(relations);

		for (final VillageRelation vr : relations) {
			final Building b = townhall.mw.getBuilding(vr.pos);
			if (b != null) {
				String col = "";

				if (vr.relation > Building.RELATION_VERYGOOD) {
					col = DARKGREEN;
				} else if (vr.relation > Building.RELATION_DECENT) {
					col = DARKBLUE;
				} else if (vr.relation <= Building.RELATION_OPENCONFLICT) {
					col = DARKRED;
				} else if (vr.relation <= Building.RELATION_BAD) {
					col = LIGHTRED;
				}

				text.add(new Line(col
						+ MLN.string(
								"ui.villagerelations",
								b.getVillageQualifiedName(),
								b.villageType.name,
								b.culture.getCultureGameName(),
								MLN.string(MillCommonUtilities
										.getRelationName(vr.relation))
										+ " ("
										+ vr.relation + ")"), false));

				final GuiButtonDiplomacy relGood = new GuiButtonDiplomacy(
						vr.pos, GuiButtonDiplomacy.REL,
						GuiButtonDiplomacy.REL_GOOD, MLN.string("ui.relgood"));
				final GuiButtonDiplomacy relNeutral = new GuiButtonDiplomacy(
						vr.pos, GuiButtonDiplomacy.REL,
						GuiButtonDiplomacy.REL_NEUTRAL,
						MLN.string("ui.relneutral"));
				final GuiButtonDiplomacy relBad = new GuiButtonDiplomacy(
						vr.pos, GuiButtonDiplomacy.REL,
						GuiButtonDiplomacy.REL_BAD, MLN.string("ui.relbad"));

				text.add(new Line(relGood, relNeutral, relBad));
				text.add(new Line(false));

				if (townhall.raidTarget == null) {
					final GuiButtonDiplomacy raid = new GuiButtonDiplomacy(
							vr.pos, GuiButtonDiplomacy.RAID,
							GuiButtonDiplomacy.REL_BAD, MLN.string("ui.raid"));
					text.add(new Line(raid));
					text.add(new Line(false));
				} else {
					if (townhall.raidStart > 0) {
						if (townhall.raidTarget.equals(vr.pos)) {
							text.add(new Line(DARKRED
									+ MLN.string("ui.raidinprogress")));
						} else {
							text.add(new Line(DARKRED
									+ MLN.string("ui.otherraidinprogress")));
						}
					} else {
						if (townhall.raidTarget.equals(vr.pos)) {
							final GuiButtonDiplomacy raid = new GuiButtonDiplomacy(
									vr.pos, GuiButtonDiplomacy.RAIDCANCEL, 0,
									MLN.string("ui.raidcancel"));
							text.add(new Line(raid));
							text.add(new Line(false));
							text.add(new Line(LIGHTRED
									+ MLN.string("ui.raidplanned")));

						} else {
							final GuiButtonDiplomacy raid = new GuiButtonDiplomacy(
									vr.pos, GuiButtonDiplomacy.RAID,
									GuiButtonDiplomacy.REL_BAD,
									MLN.string("ui.raid"));
							text.add(new Line(raid));
							text.add(new Line(false));
							text.add(new Line(LIGHTRED
									+ MLN.string("ui.otherraidplanned")));
						}
					}
				}
				text.add(new Line());
			}
		}

		final List<List<Line>> pages = new ArrayList<List<Line>>();
		pages.add(text);

		final List<List<String>> milpages = TileEntityPanel.generateMilitary(
				player, townhall);

		for (final List<String> textPage : milpages) {
			final List<Line> page = new ArrayList<Line>();

			for (final String s : textPage) {
				page.add(new Line(s));
			}
			pages.add(page);
		}

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
