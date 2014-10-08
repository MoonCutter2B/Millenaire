package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.GuiActions;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.UserProfile;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingPlan;
import org.millenaire.common.building.BuildingProject;
import org.millenaire.common.building.BuildingProject.EnumProjects;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class GuiVillageHead extends GuiText {

	public static class GuiButtonChief extends MillGuiButton {

		public static final String PRAISE = "PRAISE";
		public static final String SLANDER = "SLANDER";
		public static final String BUILDING = "BUILDING";
		public static final String VILLAGE_SCROLL = "VILLAGE_SCROLL";
		public static final String CULTURE_CONTROL = "CULTURE_CONTROL";
		public static final String CROP = "CROP";

		public Point village;
		public String value, key;

		public GuiButtonChief(final String key, final String label) {
			super(0, 0, 0, 0, 0, label);
			this.key = key;
		}

		public GuiButtonChief(final String key, final String label, final Point v) {
			super(0, 0, 0, 0, 0, label);
			village = v;
			this.key = key;
		}

		public GuiButtonChief(final String key, final String label, final String plan) {
			super(0, 0, 0, 0, 0, label);
			this.key = key;
			this.value = plan;
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

	public static final int ACTION_ICON_LENGTH = 15;
	public static final int ACTION_ICON_HEIGHT = 13;

	private final MillVillager chief;

	private final EntityPlayer player;

	ResourceLocation background = new ResourceLocation(Mill.modId, "textures/gui/ML_village_chief.png");

	public GuiVillageHead(final EntityPlayer player, final MillVillager chief) {
		this.chief = chief;
		this.player = player;

	}

	@Override
	protected void actionPerformed(final GuiButton guibutton) {

		if (guibutton instanceof GuiButtonChief) {

			final GuiButtonChief gb = (GuiButtonChief) guibutton;

			boolean close = false;

			if (gb.key == GuiButtonChief.PRAISE) {
				ClientSender.villageChiefPerformDiplomacy(player, chief, gb.village, true);
			} else if (gb.key == GuiButtonChief.SLANDER) {
				ClientSender.villageChiefPerformDiplomacy(player, chief, gb.village, false);
			} else if (gb.key == GuiButtonChief.VILLAGE_SCROLL) {
				ClientSender.villageChiefPerformVillageScroll(player, chief);
				close = true;
			} else if (gb.key == GuiButtonChief.CULTURE_CONTROL) {
				ClientSender.villageChiefPerformCultureControl(player, chief);
				close = true;
			} else if (gb.key == GuiButtonChief.BUILDING) {
				ClientSender.villageChiefPerformBuilding(player, chief, gb.value);
				close = true;
			} else if (gb.key == GuiButtonChief.CROP) {
				ClientSender.villageChiefPerformCrop(player, chief, gb.value);
				close = true;
			}

			if (close) {
				closeWindow();
			} else {
				descText = getData();
				buttonPagination();
			}
		}

		super.actionPerformed(guibutton);
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

	private List<List<Line>> getData() {

		List<Line> text = new ArrayList<Line>();

		String game = "";
		if (chief.getGameOccupationName(player.getDisplayName()).length() > 0) {
			game = " (" + chief.getGameOccupationName(player.getDisplayName()) + ")";
		}

		text.add(new Line(chief.getName() + ", " + chief.getNativeOccupationName() + game, false));
		text.add(new Line(MLN.string("ui.villagechief").replace("<0>", chief.getTownHall().getVillageQualifiedName())));
		text.add(new Line());

		String col = "";

		if (chief.getTownHall().getReputation(player.getDisplayName()) >= 8 * 64 * 64) {
			col = DARKGREEN;
		} else if (chief.getTownHall().getReputation(player.getDisplayName()) >= 64 * 64) {
			col = DARKBLUE;
		} else if (chief.getTownHall().getReputation(player.getDisplayName()) < -4 * 64) {
			col = DARKRED;
		} else if (chief.getTownHall().getReputation(player.getDisplayName()) < 0) {
			col = LIGHTRED;
		}

		text.add(new Line(col + MLN.string("ui.yourstatus") + ": " + chief.getTownHall().getReputationLevelLabel(player.getDisplayName()), false));
		text.add(new Line(col + chief.getTownHall().getReputationLevelDesc(player.getDisplayName()).replaceAll("\\$name", player.getDisplayName())));
		text.add(new Line());
		text.add(new Line(MLN.string("ui.possiblehousing") + ":"));
		text.add(new Line());
		final Map<EnumProjects, List<BuildingProject>> projects = chief.getTownHall().buildingProjects;

		final UserProfile profile = Mill.proxy.getClientProfile();

		final int reputation = chief.getTownHall().getReputation(player.getDisplayName());

		for (final EnumProjects ep : EnumProjects.values()) {
			if (chief.getTownHall().buildingProjects.containsKey(ep)) {
				final List<BuildingProject> projectsLevel = chief.getTownHall().buildingProjects.get(ep);
				for (final BuildingProject project : projectsLevel) {
					if (project.planSet != null) {
						final BuildingPlan plan = project.planSet.getRandomStartingPlan();
						if (plan != null && plan.price > 0 && !plan.isgift) {
							String status = "";

							boolean buyButton = false;

							if (project.location != null) {
								status = MLN.string("ui.alreadybuilt") + ".";
							} else if (chief.getTownHall().buildingsBought.contains(project.key)) {
								status = MLN.string("ui.alreadyrequested") + ".";
							} else if (plan.reputation > reputation) {
								status = MLN.string("ui.notavailableyet") + ".";
							} else if (plan.price > MillCommonUtilities.countMoney(player.inventory)) {
								status = MLN.string("ui.youaremissing", "" + MillCommonUtilities.getShortPrice(plan.price - MillCommonUtilities.countMoney(player.inventory)));
							} else {
								status = MLN.string("ui.available") + ".";
								buyButton = true;
							}
							text.add(new Line(plan.nativeName + ": " + status, false));

							if (buyButton) {
								text.add(new Line(new GuiButtonChief(GuiButtonChief.BUILDING, MLN.string("ui.buybuilding", plan.nativeName, MillCommonUtilities.getShortPrice(plan.price)),
										plan.buildingKey)));
								text.add(new Line(false));
								text.add(new Line());
							}
						} else if (plan.isgift && MLN.bonusEnabled && !Mill.proxy.isTrueClient()) {
							String status = "";

							boolean buyButton = false;

							if (project.location != null) {
								status = MLN.string("ui.alreadybuilt") + ".";
							} else if (chief.getTownHall().buildingsBought.contains(project.key)) {
								status = MLN.string("ui.alreadyrequested") + ".";
							} else {
								status = MLN.string("ui.bonusavailable") + ".";
								buyButton = true;
							}
							text.add(new Line(plan.nativeName + ": " + status, false));

							if (buyButton) {
								text.add(new Line(new GuiButtonChief(GuiButtonChief.BUILDING, MLN.string("ui.buybonusbuilding", plan.nativeName), plan.buildingKey)));
								text.add(new Line(false));
								text.add(new Line());
							}
						}
					}
				}
			}
		}

		if (GuiActions.VILLAGE_SCROLL_REPUTATION > reputation) {
			text.add(new Line(MLN.string("ui.scrollsnoreputation")));
		} else if (GuiActions.VILLAGE_SCROLL_PRICE > MillCommonUtilities.countMoney(player.inventory)) {
			text.add(new Line(MLN.string("ui.scrollsnotenoughmoney", "" + MillCommonUtilities.getShortPrice(GuiActions.VILLAGE_SCROLL_PRICE - MillCommonUtilities.countMoney(player.inventory)))));
		} else {
			text.add(new Line(MLN.string("ui.scrollsok"), false));
			text.add(new Line(new GuiButtonChief(GuiButtonChief.VILLAGE_SCROLL, MLN.string("ui.buyscroll"), MillCommonUtilities.getShortPrice(GuiActions.VILLAGE_SCROLL_PRICE))));
			text.add(new Line());
			text.add(new Line());
		}

		if (chief.getCulture().knownCrops.size() > 0) {
			text.add(new Line(MLN.string("ui.cropsknown")));
			text.add(new Line());

			for (final String crop : chief.getCulture().knownCrops) {
				if (profile.isTagSet(MillWorld.CROP_PLANTING + crop)) {
					text.add(new Line(MLN.string("ui.cropknown", MLN.string("item." + crop))));
				} else if (GuiActions.CROP_REPUTATION > reputation) {
					text.add(new Line(MLN.string("ui.cropinsufficientreputation", MLN.string("item." + crop))));
				} else if (GuiActions.CROP_PRICE > MillCommonUtilities.countMoney(player.inventory)) {
					text.add(new Line(MLN.string("ui.cropnotenoughmoney", MLN.string("item." + crop),
							"" + MillCommonUtilities.getShortPrice(GuiActions.CROP_PRICE - MillCommonUtilities.countMoney(player.inventory)))));
				} else {
					text.add(new Line(MLN.string("ui.cropoktolearn", MLN.string("item." + crop)), false));
					text.add(new Line(new GuiButtonChief(GuiButtonChief.CROP, MLN.string("ui.croplearn", "" + MillCommonUtilities.getShortPrice(GuiActions.CROP_PRICE)), crop)));
					text.add(new Line(false));
					text.add(new Line());
				}

			}
			text.add(new Line());
		}

		if (profile.isTagSet(MillWorld.CULTURE_CONTROL + chief.getCulture().key)) {
			text.add(new Line(MLN.string("ui.control_alreadydone", chief.getCulture().getCultureGameName())));
		} else if (GuiActions.CULTURE_CONTROL_REPUTATION > reputation) {
			text.add(new Line(MLN.string("ui.control_noreputation", chief.getCulture().getCultureGameName())));
		} else {
			text.add(new Line(MLN.string("ui.control_ok", chief.getCulture().getCultureGameName()), false));
			text.add(new Line(new GuiButtonChief(GuiButtonChief.CULTURE_CONTROL, MLN.string("ui.control_get"))));
			text.add(new Line(false));
			text.add(new Line());
		}

		final List<List<Line>> pages = new ArrayList<List<Line>>();
		pages.add(text);

		text = new ArrayList<Line>();

		text.add(new Line(MLN.string("ui.relationlist")));
		text.add(new Line());
		text.add(new Line(MLN.string("ui.relationpoints", "" + profile.getDiplomacyPoints(chief.getTownHall()))));
		text.add(new Line());

		final List<VillageRelation> relations = new ArrayList<VillageRelation>();

		for (final Point p : chief.getTownHall().getKnownVillages()) {
			final Building b = chief.getTownHall().mw.getBuilding(p);
			if (b != null) {
				relations.add(new VillageRelation(p, chief.getTownHall().getRelationWithVillage(p), b.getVillageQualifiedName()));
			}
		}

		Collections.sort(relations);

		for (final VillageRelation vr : relations) {
			final Building b = chief.getTownHall().mw.getBuilding(vr.pos);
			if (b != null) {
				col = "";

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
						+ MLN.string("ui.villagerelations", b.getVillageQualifiedName(), b.villageType.name, b.culture.getCultureGameName(),
								MLN.string(MillCommonUtilities.getRelationName(vr.relation)) + " (" + vr.relation + ")"), false));

				GuiButtonChief praise = null;
				GuiButtonChief slander = null;

				if (profile.getDiplomacyPoints(chief.getTownHall()) > 0 && reputation > 0) {
					if (vr.relation < Building.RELATION_MAX) {
						praise = new GuiButtonChief(GuiButtonChief.PRAISE, MLN.string("ui.relationpraise"), vr.pos);
					}
					if (vr.relation > Building.RELATION_MIN) {
						slander = new GuiButtonChief(GuiButtonChief.SLANDER, MLN.string("ui.relationslander"), vr.pos);
					}
					text.add(new Line(praise, slander));
					text.add(new Line(false));
					text.add(new Line());
					text.add(new Line());
				} else {
					text.add(new Line(DARKRED + MLN.string("ui.villagerelationsnobutton")));
					text.add(new Line());
				}
			}
		}

		pages.add(text);

		text = new ArrayList<Line>();
		text.add(new Line(MLN.string("ui.relationhelp")));
		pages.add(text);

		return adjustText(pages);
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
		return 200;
	}

	@Override
	public void initData() {
		descText = getData();
	}

}
