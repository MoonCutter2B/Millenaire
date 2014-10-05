package org.millenaire.common;

import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;

import org.millenaire.client.MillClientUtilities;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillVillager.InvItemAlphabeticalComparator;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingLocation;
import org.millenaire.common.building.BuildingPlan;
import org.millenaire.common.building.BuildingProject;
import org.millenaire.common.building.BuildingProject.EnumProjects;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.network.StreamReadWrite;

public class TileEntityPanel extends TileEntitySign {

	public static class PanelPacketInfo {

		Point pos, buildingPos;
		String[][] lines;
		long villager_id;
		int panelType;

		public PanelPacketInfo(final Point pos, final String[][] lines,
				final Point buildingPos, final int panelType,
				final long village_id) {
			this.pos = pos;
			this.lines = lines;
			this.buildingPos = buildingPos;
			this.panelType = panelType;
			this.villager_id = village_id;
		}
	}

	public static final int VILLAGE_MAP = 1;
	public static final int etatCivil = 1;
	public static final int constructions = 2;
	public static final int projects = 3;
	public static final int controlledProjects = 4;
	public static final int house = 5;
	public static final int resources = 6;
	public static final int archives = 7;
	public static final int villageMap = 8;

	public static final int military = 9;
	public static final int tradeGoods = 10;

	public static final int innVisitors = 11;

	public static final int marketMerchants = 12;

	public static final int controlledMilitary = 13;

	private static void addProjectToList(final EntityPlayer player,
			final BuildingProject project, final Building townHall,
			final List<String> page) {

		if (project.planSet != null) {
			if (project.location == null || project.location.level < 0) {
				final BuildingPlan plan = project.planSet
						.getRandomStartingPlan();

				page.add(plan.getFullDisplayName() + ": "
						+ MLN.string("panels.notyetbuilt") + ".");
			} else {
				if (project.location.level + 1 < project
						.getLevelsNumber(project.location.getVariation())) {

					final BuildingPlan plan = project.getPlan(
							project.location.getVariation(),
							project.location.level + 1);

					final BuildingLocation l = project.location;
					page.add(plan.getFullDisplayName()
							+ " ("
							+ MathHelper.floor_double(l.pos.distanceTo(townHall
									.getPos()))
							+ "m "
							+ townHall.getPos().directionToShort(l.pos)
							+ "): "
							+ MLN.string(
									"panels.nbupgradesleft",
									""
											+ (project
													.getLevelsNumber(project.location
															.getVariation())
													- project.location.level - 1)));
				} else {
					final BuildingPlan plan = project.getPlan(
							project.location.getVariation(),
							project.location.level);

					final BuildingLocation l = project.location;
					page.add(plan.getFullDisplayName()
							+ " ("
							+ MathHelper.floor_double(l.pos.distanceTo(townHall
									.getPos())) + "m "
							+ townHall.getPos().directionToShort(l.pos) + "): "
							+ MLN.string("panels.finished") + ".");
				}
			}
		}
	}

	public static List<List<String>> generateArchives(
			final EntityPlayer player, final Building townHall,
			final long villager_id) {

		if (townHall == null) {
			return null;
		}

		final VillagerRecord vr = townHall.getVillagerRecordById(villager_id);

		if (vr == null) {
			return null;
		}

		final List<List<String>> text = new ArrayList<List<String>>();
		final List<String> page = new ArrayList<String>();

		page.add(vr.getName());
		page.add(vr.getGameOccupation(player.getDisplayName()));
		page.add("");

		if (vr.mothersName != null && vr.mothersName.length() > 0) {
			page.add(MLN.string("panels.mother") + ": " + vr.mothersName);
		}
		if (vr.fathersName != null && vr.fathersName.length() > 0) {
			page.add(MLN.string("panels.father") + ": " + vr.fathersName);
		}
		if (vr.spousesName != null && vr.spousesName.length() > 0) {
			page.add(MLN.string("panels.spouse") + ": " + vr.spousesName);
		}

		page.add("");

		MillVillager villager = null;

		for (final MillVillager v : townHall.villagers) {
			if (v.villager_id == vr.id) {
				villager = v;
			}
		}

		page.add("");

		if (villager == null) {
			if (vr.killed) {
				page.add(MLN.string("panels.dead"));
			} else if (vr.awayraiding) {
				page.add(MLN.string("panels.awayraiding"));
			} else if (vr.awayraiding) {
				page.add(MLN.string("panels.awayhired"));
			} else if (vr.raidingVillage
					&& townHall.worldObj.getWorldTime() < vr.raiderSpawn
							+ Building.INVADER_SPAWNING_DELAY) {
				page.add(MLN.string("panels.invaderincoming"));
			} else {
				page.add(MLN.string("panels.missing"));
			}
		} else {
			String occupation = "";

			if (villager.goalKey != null
					&& Goal.goals.containsKey(villager.goalKey)) {
				occupation = Goal.goals.get(villager.goalKey)
						.gameName(villager);
			}
			page.add(MLN.string("panels.currentoccupation") + ": " + occupation);
		}

		text.add(page);
		return text;
	}

	public static List<List<String>> generateConstructions(
			final EntityPlayer player, final Building townHall) {

		final List<String> page = new ArrayList<String>();

		page.add(MLN.string("panels.constructions") + " : "
				+ townHall.getVillageQualifiedName());

		page.add("");

		for (final EnumProjects ep : EnumProjects.values()) {
			if (townHall.buildingProjects.containsKey(ep)) {
				final List<BuildingProject> projectsLevel = townHall.buildingProjects
						.get(ep);
				for (final BuildingProject project : projectsLevel) {
					if (project.location != null) {
						String level = null;
						if (project.location.level < 0) {
							level = MLN.string("ui.notyetbuilt");
						}
						if (project.location.level > 0) {
							level = MLN.string("panels.upgrade") + " "
									+ project.location.level;
						}

						final List<String> effects = project.location
								.getBuildingEffects(townHall.worldObj);

						String effect = null;
						if (effects.size() > 0) {
							effect = "";
							for (final String s : effects) {
								if (effect.length() > 0) {
									effect += ", ";
								}
								effect += s;
							}
						}

						page.add(project.location.getFullDisplayName()
								+ ": "
								+ MathHelper.floor_double(project.location.pos
										.distanceTo(townHall.getPos()))
								+ "m "
								+ townHall.getPos().directionToShort(
										project.location.pos));
						if (level != null) {
							page.add(level);
						}
						if (effect != null) {
							page.add(effect);
						}
						page.add("");
					}
				}
			}
		}

		final List<List<String>> text = new ArrayList<List<String>>();
		text.add(page);

		return text;
	}

	public static List<List<String>> generateEtatCivil(
			final EntityPlayer player, final Building townHall) {

		if (townHall == null) {
			return null;
		}

		final List<List<String>> text = new ArrayList<List<String>>();
		final List<String> page = new ArrayList<String>();
		final List<String> visitorsPage = new ArrayList<String>();

		page.add(MLN.string("ui.population") + " "
				+ townHall.getVillageQualifiedName());
		page.add("");

		visitorsPage.add(MLN.string("panels.visitors") + ":");
		visitorsPage.add("");

		for (final VillagerRecord vr : townHall.vrecords) {
			int nbFound = 0;

			boolean belongsToVillage = true;

			for (final MillVillager villager : townHall.villagers) {
				if (villager.villager_id == vr.id) {
					nbFound++;
					belongsToVillage = !villager.isVisitor();
				}
			}
			String error = "";

			if (nbFound == 0) {
				if (vr.killed) {
					error = " (" + MLN.string("panels.dead").toLowerCase()
							+ ")";
				} else if (vr.awayraiding) {
					error = " ("
							+ MLN.string("panels.awayraiding").toLowerCase()
							+ ")";
				} else if (vr.awayhired) {
					error = " (" + MLN.string("panels.awayhired").toLowerCase()
							+ ")";
				} else if (vr.raidingVillage
						&& townHall.worldObj.getWorldTime() < vr.raiderSpawn
								+ Building.INVADER_SPAWNING_DELAY) {
					error = " ("
							+ MLN.string("panels.invaderincoming")
									.toLowerCase() + ")";
				} else if (vr.raidingVillage) {
					error = " (" + MLN.string("panels.raider").toLowerCase()
							+ ")";
				} else {
					error = " (" + MLN.string("panels.missing").toLowerCase()
							+ ")";
				}

				if (MLN.DEV && Mill.serverWorlds.size() > 0) {

					final Building thServer = Mill.serverWorlds.get(0)
							.getBuilding(townHall.getPos());

					if (thServer != null) {
						int nbOnServer = 0;
						for (final MillVillager villager : thServer.villagers) {
							if (villager.villager_id == vr.id) {
								nbOnServer++;
							}
						}

						error += " nbOnServer:" + nbOnServer;
					}
				}

			} else if (nbFound > 1) {
				error = " ("
						+ MLN.string("panels.multiple", "" + nbFound)
								.toLowerCase() + ")";
			}

			if (belongsToVillage) {
				page.add(vr.getName()
						+ ", "
						+ vr.getGameOccupation(player.getDisplayName())
								.toLowerCase() + error);
			} else {
				visitorsPage.add(vr.getName()
						+ ", "
						+ vr.getGameOccupation(player.getDisplayName())
								.toLowerCase() + error);
			}

		}

		if (MLN.DEV && Mill.serverWorlds.size() > 0) {
			final int nbClient = MillCommonUtilities.getEntitiesWithinAABB(
					townHall.worldObj, MillVillager.class, townHall.getPos(),
					64, 16).size();
			final Building thServer = Mill.serverWorlds.get(0).getBuilding(
					townHall.getPos());
			final int nbServer = MillCommonUtilities.getEntitiesWithinAABB(
					thServer.worldObj, MillVillager.class, townHall.getPos(),
					64, 16).size();

			page.add("Client: " + nbClient + ", server: " + nbServer);

		}

		text.add(page);
		text.add(visitorsPage);
		return text;
	}

	public static List<List<String>> generateHouse(final EntityPlayer player,
			final Building house) {

		final List<String> page = new ArrayList<String>();

		page.add("House : " + house.getNativeBuildingName());

		page.add("");

		VillagerRecord wife = null, husband = null;

		for (final VillagerRecord vr : house.vrecords) {
			if (vr.gender == MillVillager.FEMALE && !vr.getType().isChild) {
				wife = vr;
			}
			if (vr.gender == MillVillager.MALE && !vr.getType().isChild) {
				husband = vr;
			}
		}

		if (wife == null && husband == null) {
			page.add(MLN.string("panels.houseunoccupied"));
		} else if (wife == null) {

			page.add(MLN.string("panels.man") + ": " + husband.getName() + ", "
					+ husband.getGameOccupation(player.getDisplayName()));
			page.add("");
			if (house.location.femaleResident.size() == 0) {
				page.add(MLN.string("panels.nofemaleresident"));
			} else {
				page.add(MLN.string("panels.bachelor"));
			}
		} else if (husband == null) {

			page.add(MLN.string("panels.woman") + ": " + wife.getName() + ", "
					+ wife.getGameOccupation(player.getDisplayName()));
			page.add("");
			if (house.location.maleResident == null
					|| house.location.maleResident.size() == 0) {
				page.add(MLN.string("panels.nomaleresident"));
			} else {
				page.add(MLN.string("panels.spinster"));
			}
		} else {

			page.add(MLN.string("panels.woman")
					+ ": "
					+ wife.getName()
					+ ", "
					+ wife.getGameOccupation(player.getDisplayName())
							.toLowerCase());

			page.add(MLN.string("panels.man")
					+ ": "
					+ husband.getName()
					+ ", "
					+ husband.getGameOccupation(player.getDisplayName())
							.toLowerCase());

			if (house.vrecords.size() > 2) {
				page.add("");
				page.add(MLN.string("panels.children") + ":");
				page.add("");
				for (final VillagerRecord vr : house.vrecords) {
					if (vr.getType().isChild) {

						page.add(vr.getName()
								+ ", "
								+ vr.getGameOccupation(player.getDisplayName())
										.toLowerCase());
					}
				}
			}

		}

		final List<List<String>> text = new ArrayList<List<String>>();
		text.add(page);

		return text;
	}

	public static List<List<String>> generateInnVisitors(final Building house) {

		final List<String> page = new ArrayList<String>();

		page.add(MLN.string("panels.innvisitors", house.getNativeBuildingName())
				+ ":");

		page.add("");
		for (int i = house.visitorsList.size() - 1; i > -1; i--) {
			final String s = house.visitorsList.get(i);

			if (s.split(";").length > 1) {
				if (s.startsWith("storedexports;")) {
					final String[] v = s.split(";");

					String taken = "";

					for (int j = 2; j < v.length; j++) {
						final InvItem iv = new InvItem(v[j]);

						if (taken.length() > 0) {
							taken += ", ";
						}

						taken += iv.getName() + ": " + v[j].split("/")[2];
					}

					page.add(MLN.string("panels.storedexports", v[1], taken));

				} else if (s.startsWith("broughtimport;")) {
					final String[] v = s.split(";");

					String taken = "";

					for (int j = 2; j < v.length; j++) {
						final InvItem iv = new InvItem(v[j]);

						if (taken.length() > 0) {
							taken += ", ";
						}

						taken += iv.getName() + ": " + v[j].split("/")[2];
					}

					page.add(MLN.string("panels.broughtimport", v[1], taken));
				} else {
					page.add(MLN.string(s.split(";")));
				}
			} else {// old system, already prepared sentence
				page.add(s);
			}

			page.add("");
		}

		final List<List<String>> text = new ArrayList<List<String>>();
		text.add(page);

		return text;
	}

	public static List<List<String>> generateMarketGoods(final Building house) {

		final List<String> page = new ArrayList<String>();

		page.add(MLN.string("panels.goodstraded") + ":");

		page.add("");
		page.add(MLN.string("panels.goodsimported") + ":");
		page.add("");
		for (final InvItem good : house.imported.keySet()) {
			page.add(good.getName() + ": " + house.imported.get(good));
		}

		page.add("");
		page.add(MLN.string("panels.goodsexported") + ":");
		page.add("");
		for (final InvItem good : house.exported.keySet()) {
			page.add(good.getName() + ": " + house.exported.get(good));
		}

		final List<List<String>> text = new ArrayList<List<String>>();
		text.add(page);

		return text;
	}

	public static List<List<String>> generateMarketMerchants(
			final Building market) {

		if (market == null) {
			return null;
		}

		final List<List<String>> text = new ArrayList<List<String>>();
		final List<String> page = new ArrayList<String>();

		page.add(MLN.string("panels.merchantlist") + ": ");
		page.add("(" + MLN.string("panels.capacity") + ": "
				+ market.getResManager().stalls.size() + ")");
		page.add("");

		for (final VillagerRecord vr : market.vrecords) {
			MillVillager v = null;
			for (final MillVillager av : market.villagers) {
				if (vr.matches(av)) {
					v = av;
				}
			}
			page.add(vr.getName());
			if (v == null) {
				if (vr.killed) {
					page.add(MLN.string("panels.dead"));
				} else {
					page.add(MLN.string("panels.missing"));
				}
			} else {
				page.add(v.getNativeOccupationName());
				page.add(MLN.string("panels.nbnightsin", ""
						+ v.foreignMerchantNbNights));
				page.add("");
			}
		}

		text.add(page);
		return text;
	}

	public static List<List<String>> generateMilitary(
			final EntityPlayer player, final Building townHall) {

		final List<List<String>> text = new ArrayList<List<String>>();
		List<String> page = new ArrayList<String>();

		page.add(MLN.string("panels.military") + " : "
				+ townHall.getVillageQualifiedName());
		page.add("");

		int nbAttackers = 0;
		Point attackingVillagePos = null;

		if (townHall.raidTarget != null) {
			final Building target = Mill.clientWorld
					.getBuilding(townHall.raidTarget);

			if (target != null) {
				if (townHall.raidStart > 0) {
					page.add(MLN.string(
							"panels.raidinprogresslong",
							target.getVillageQualifiedName(),
							""
									+ Math.round((townHall.worldObj
											.getWorldTime() - townHall.raidStart) / 1000)));
				} else {
					page.add(MLN.string(
							"panels.planningraidlong",
							target.getVillageQualifiedName(),
							""
									+ Math.round((townHall.worldObj
											.getWorldTime() - townHall.raidPlanningStart) / 1000)));
				}

				page.add("");
			}
		} else {

			for (final VillagerRecord vr : townHall.vrecords) {
				if (vr.raidingVillage) {
					nbAttackers++;
					attackingVillagePos = vr.originalVillagePos;
				}
			}
			if (nbAttackers > 0) {
				final Building attackingVillage = Mill.clientWorld
						.getBuilding(attackingVillagePos);

				String attackedBy;
				if (attackingVillage != null) {
					attackedBy = attackingVillage.getVillageQualifiedName();
				} else {
					attackedBy = MLN.string("panels.unknownattacker");
				}

				page.add(MLN.string("panels.underattacklong", "" + nbAttackers,
						"" + townHall.getVillageAttackerStrength(), attackedBy));
				page.add("");
			}
		}

		page.add(MLN.string("panels.offenselong",
				"" + townHall.getVillageRaidingStrength()));
		page.add(MLN.string("panels.defenselong",
				"" + townHall.getVillageDefendingStrength()));

		text.add(page);

		page = new ArrayList<String>();

		page.add(MLN.string("panels.villagefighters"));
		page.add("");

		for (final VillagerRecord vr : townHall.vrecords) {
			if ((vr.getType().isRaider || vr.getType().helpInAttacks)
					&& !vr.raidingVillage) {
				String status = "";

				if (vr.getType().helpInAttacks) {
					status += MLN.string("panels.defender");
				}

				if (vr.getType().isRaider) {
					if (status.length() > 0) {
						status += ", ";
					}
					status += MLN.string("panels.raider");
				}

				if (vr.awayraiding) {
					status += ", " + MLN.string("panels.awayraiding");
				} else if (vr.awayhired) {
					status += ", " + MLN.string("panels.awayhired");
				} else if (vr.raidingVillage
						&& townHall.worldObj.getWorldTime() < vr.raiderSpawn
								+ Building.INVADER_SPAWNING_DELAY) {
					status += ", " + MLN.string("panels.invaderincoming");
				} else if (vr.killed) {
					status += ", " + MLN.string("panels.dead");
				}

				String weapon = "";

				final Item bestMelee = vr.getBestMeleeWeapon();
				if (bestMelee != null) {
					weapon = Mill.proxy.getItemName(bestMelee, 0);
				}

				if (vr.getType().isArcher && vr.countInv(Items.bow) > 0) {
					if (weapon.length() > 0) {
						weapon += ", ";
					}
					weapon += Mill.proxy.getItemName(Items.bow, 0);
				}

				page.add(vr.getName() + ", "
						+ vr.getGameOccupation(player.getDisplayName()));
				page.add(status);
				page.add(MLN.string("panels.health") + ": " + vr.getMaxHealth()
						+ ", " + MLN.string("panels.armour") + ": "
						+ vr.getTotalArmorValue() + ", "
						+ MLN.string("panels.weapons") + ": " + weapon + ", "
						+ MLN.string("panels.militarystrength") + ": "
						+ vr.getMilitaryStrength());
				page.add("");
			}

		}

		text.add(page);

		if (nbAttackers > 0) {

			page = new ArrayList<String>();
			page.add(MLN.string("panels.attackers"));
			page.add("");

			for (final VillagerRecord vr : townHall.vrecords) {
				if (vr.raidingVillage) {
					String status = "";
					if (vr.killed) {
						status = MLN.string("panels.dead");
					}

					String weapon = "";

					final Item bestMelee = vr.getBestMeleeWeapon();
					if (bestMelee != null) {
						weapon = Mill.proxy.getItemName(bestMelee, 0);
					}

					if (vr.getType().isArcher && vr.countInv(Items.bow) > 0) {
						if (weapon.length() > 0) {
							weapon += ", ";
						}
						weapon += Mill.proxy.getItemName(Items.bow, 0);
					}

					page.add(vr.getName() + ", "
							+ vr.getGameOccupation(player.getDisplayName()));
					page.add(status);
					page.add(MLN.string("panels.health") + ": "
							+ vr.getMaxHealth() + ", "
							+ MLN.string("panels.armour") + ": "
							+ vr.getTotalArmorValue() + ", "
							+ MLN.string("panels.weapons") + ": " + weapon
							+ ", " + MLN.string("panels.militarystrength")
							+ ": " + vr.getMilitaryStrength());
					page.add("");
				}
			}
			text.add(page);
		}

		if (townHall.raidsPerformed.size() > 0) {

			page = new ArrayList<String>();

			page.add(MLN.string("panels.raidsperformed"));
			page.add("");

			for (int i = townHall.raidsPerformed.size() - 1; i >= 0; i--) {
				final String s = townHall.raidsPerformed.get(i);

				if (s.split(";").length > 1) {
					if (s.split(";")[0].equals("failure")) {
						page.add(MLN.string("raid.historyfailure",
								s.split(";")[1]));
					} else {

						final String[] v = s.split(";");
						String taken = "";

						for (int j = 2; j < v.length; j++) {
							final InvItem iv = new InvItem(v[j]);

							if (taken.length() > 0) {
								taken += ", ";
							}

							taken += iv.getName() + ": " + v[j].split("/")[2];
						}

						if (taken.length() == 0) {
							taken = MLN.string("raid.nothing");
						}

						page.add(MLN.string("raid.historysuccess",
								s.split(";")[1], taken));
					}

				} else {// old system, already prepared sentence
					page.add(townHall.raidsPerformed.get(i));
				}
				page.add("");
			}

			text.add(page);
		}

		if (townHall.raidsSuffered.size() > 0) {

			page = new ArrayList<String>();

			page.add(MLN.string("panels.raidssuffered"));
			page.add("");

			for (int i = townHall.raidsSuffered.size() - 1; i >= 0; i--) {

				final String s = townHall.raidsSuffered.get(i);

				if (s.split(";").length > 1) {
					if (s.split(";")[0].equals("failure")) {
						page.add(MLN.string("raid.historydefended",
								s.split(";")[1]));
					} else {

						final String[] v = s.split(";");
						String taken = "";

						for (int j = 2; j < v.length; j++) {
							final InvItem iv = new InvItem(v[j]);

							if (taken.length() > 0) {
								taken += ", ";
							}

							taken += iv.getName() + ": " + v[j].split("/")[2];
						}

						if (taken.length() == 0) {
							taken = MLN.string("raid.nothing");
						}

						page.add(MLN.string("raid.historyraided",
								s.split(";")[1], taken));
					}

				} else {// old system, already prepared sentence
					page.add(townHall.raidsSuffered.get(i));
				}
				page.add("");
			}

			text.add(page);

		}

		return text;
	}

	public static List<List<String>> generateProjects(
			final EntityPlayer player, final Building townHall) {

		if (townHall.villageType == null) {
			return null;
		}

		final List<String> page = new ArrayList<String>();

		page.add(MLN.string("panels.buildingprojects") + " : "
				+ townHall.getVillageQualifiedName());

		page.add("");

		for (final EnumProjects ep : EnumProjects.values()) {
			if (townHall.buildingProjects.containsKey(ep)) {
				if (!townHall.villageType.playerControlled
						|| ep == EnumProjects.CENTRE
						|| ep == EnumProjects.START || ep == EnumProjects.CORE) {// for
																					// controlled
																					// villages,
																					// only
																					// centre,
																					// start
					// and core
					final List<BuildingProject> projectsLevel = townHall.buildingProjects
							.get(ep);
					page.add(MLN.string(ep.labelKey) + ":");
					page.add("");

					for (final BuildingProject project : projectsLevel) {
						if (townHall.isDisplayableProject(project)) {
							addProjectToList(player, project, townHall, page);
						}
					}
					page.add("");
				}
			}
		}

		final List<List<String>> text = new ArrayList<List<String>>();
		text.add(page);

		return text;
	}

	public static List<List<String>> generateResources(
			final EntityPlayer player, final Building house) {

		List<String> page = new ArrayList<String>();

		final List<List<String>> text = new ArrayList<List<String>>();

		page.add(MLN.string("panels.resources") + ": "
				+ house.getNativeBuildingName());

		page.add("");

		final BuildingPlan goalPlan = house.getCurrentGoalBuildingPlan();

		final List<InvItem> res = new ArrayList<InvItem>();
		final HashMap<InvItem, Integer> resCost = new HashMap<InvItem, Integer>();
		final HashMap<InvItem, Integer> resHas = new HashMap<InvItem, Integer>();

		if (goalPlan != null) {
			for (final InvItem key : goalPlan.resCost.keySet()) {
				res.add(key);
				resCost.put(key, goalPlan.resCost.get(key));
				int has = house.countGoods(key.getItem(), key.meta);
				if (house.builder != null
						&& house.buildingLocationIP != null
						&& house.buildingLocationIP.planKey
								.equals(house.buildingGoal)) {
					has += house.builder.countInv(key.getItem(), key.meta);
				}
				if (has > goalPlan.resCost.get(key)) {
					has = goalPlan.resCost.get(key);
				}

				resHas.put(key, has);
			}

			page.add(MLN.string("panels.resourcesneeded") + ":");

			String name, gameName = goalPlan.getGameName();
			if (goalPlan.nativeName != null && goalPlan.nativeName.length() > 0) {
				name = goalPlan.nativeName;
			} else if (goalPlan.getGameName() != null
					&& goalPlan.getGameName().length() > 0) {
				name = goalPlan.getGameName();
				gameName = "";
			} else {
				name = "";
			}

			if (gameName != null && gameName.length() > 0) {
				name += " (" + gameName + ")";
			}

			String status = "";
			if (house.buildingLocationIP != null
					&& house.buildingLocationIP.planKey
							.equals(house.buildingGoal)) {
				if (house.buildingLocationIP.level == 0) {
					status = MLN.string("ui.inconstruction");
				} else {
					status = MLN.string("ui.upgrading") + " ("
							+ house.buildingLocationIP.level + ")";
				}
			} else {
				status = MLN.string(house.buildingGoalIssue);
			}

			page.add(name + " - " + status);

			page.add("");

			Collections.sort(res, new InvItemAlphabeticalComparator());

			for (int i = 0; i < res.size(); i++) {
				page.add(res.get(i).getName() + ": " + resHas.get(res.get(i))
						+ "/" + resCost.get(res.get(i)));
			}

			text.add(page);

			page = new ArrayList<String>();

		}

		page.add(MLN.string("panels.resourcesavailable") + ":");

		page.add("");

		final HashMap<InvItem, Integer> contents = house.getResManager()
				.getChestsContent();

		final List<InvItem> keys = new ArrayList<InvItem>(contents.keySet());

		Collections.sort(keys, new InvItemAlphabeticalComparator());

		for (final InvItem key : keys) {
			page.add(key.getName() + ": " + contents.get(key));
		}

		text.add(page);

		return text;
	}

	public static List<List<String>> generateSummary(final EntityPlayer player,
			final Building townHall) {
		final List<String> page = new ArrayList<String>();

		final List<List<String>> text = new ArrayList<List<String>>();

		page.add(MLN.string("panels.villagesummary") + ": "
				+ townHall.getVillageQualifiedName());
		page.add("");

		int nbMen = 0, nbFemale = 0, nbGrownBoy = 0, nbGrownGirl = 0, nbBoy = 0, nbGirl = 0;

		for (final VillagerRecord vr : townHall.vrecords) {
			final boolean belongsToVillage = vr.getType() != null
					&& !vr.getType().visitor && !vr.raidingVillage;

			if (belongsToVillage) {

				if (!vr.getType().isChild) {
					if (vr.gender == MillVillager.MALE) {
						nbMen++;
					} else {
						nbFemale++;
					}
				} else {
					if (vr.villagerSize == MillVillager.MAX_CHILD_SIZE) {
						if (vr.gender == MillVillager.MALE) {
							nbGrownBoy++;
						} else {
							nbGrownGirl++;
						}
					} else {
						if (vr.gender == MillVillager.MALE) {
							nbBoy++;
						} else {
							nbGirl++;
						}
					}
				}

			}
		}

		page.add(MLN
				.string("ui.populationnumber",
						""
								+ (nbMen + nbFemale + nbGrownBoy + nbGrownGirl
										+ nbBoy + nbGirl)));
		page.add(MLN.string("ui.adults", "" + (nbMen + nbFemale), "" + nbMen,
				"" + nbFemale));
		page.add(MLN.string("ui.teens", "" + (nbGrownBoy + nbGrownGirl), ""
				+ nbGrownBoy, "" + nbGrownGirl));
		page.add(MLN.string("ui.children", "" + (nbBoy + nbGirl), "" + nbBoy,
				"" + nbGirl));

		page.add("");

		if (townHall.buildingGoal == null) {
			page.add(MLN.string("ui.goalscompleted1") + " "
					+ MLN.string("ui.goalscompleted2"));
		} else {
			final BuildingPlan goal = townHall.getCurrentGoalBuildingPlan();

			String status;
			if (townHall.buildingLocationIP != null
					&& townHall.buildingLocationIP.planKey
							.equals(townHall.buildingGoal)) {
				if (townHall.buildingLocationIP.level == 0) {
					status = MLN.string("ui.inconstruction");
				} else {
					status = MLN.string("ui.upgrading", ""
							+ townHall.buildingLocationIP.level);
				}
			} else {
				status = MLN.string(townHall.buildingGoalIssue);
			}
			page.add(MLN.string("panels.buildingproject") + " "
					+ goal.nativeName + " " + goal.getGameName() + ": "
					+ status);

			final List<InvItem> res = new ArrayList<InvItem>();
			final HashMap<InvItem, Integer> resCost = new HashMap<InvItem, Integer>();
			final HashMap<InvItem, Integer> resHas = new HashMap<InvItem, Integer>();

			for (final InvItem key : goal.resCost.keySet()) {
				res.add(key);
				resCost.put(key, goal.resCost.get(key));
				int has = townHall.countGoods(key.getItem(), key.meta);
				if (townHall.builder != null
						&& townHall.buildingLocationIP != null
						&& townHall.buildingLocationIP.planKey
								.equals(townHall.buildingGoal)) {
					has += townHall.builder.countInv(key.getItem(), key.meta);
				}
				if (has > goal.resCost.get(key)) {
					has = goal.resCost.get(key);
				}

				resHas.put(key, has);
			}
			page.add("");
			page.add(MLN.string("panels.resourcesneeded") + ":");
			page.add("");

			Collections.sort(res, new InvItemAlphabeticalComparator());

			for (int i = 0; i < res.size(); i++) {
				page.add(res.get(i).getName() + ": " + resHas.get(res.get(i))
						+ "/" + resCost.get(res.get(i)));
			}

		}
		page.add("");
		page.add(MLN.string("panels.currentconstruction"));

		if (townHall.buildingLocationIP == null) {
			page.add(MLN.string("ui.noconstruction1") + " "
					+ MLN.string("ui.noconstruction2"));
		} else {
			final String planName = townHall.culture.getBuildingPlanSet(
					townHall.buildingLocationIP.planKey).getNativeName();

			String status;
			if (townHall.buildingLocationIP.level == 0) {
				status = MLN.string("ui.inconstruction");
			} else {
				status = MLN.string("ui.upgrading", ""
						+ townHall.buildingLocationIP.level);
			}

			String loc;

			if (townHall.buildingLocationIP != null) {

				final int distance = MathHelper.floor_double(townHall.getPos()
						.distanceTo(townHall.buildingLocationIP.pos));

				final String direction = MLN.string(townHall.getPos()
						.directionTo(townHall.buildingLocationIP.pos));

				loc = MLN.string("other.shortdistancedirection", "" + distance,
						"" + direction);
			} else {
				loc = "";
			}

			page.add(planName + ": " + status + " - " + loc);
		}

		text.add(page);

		return text;
	}

	public static List<List<String>> generateVillageMap(final Building house) {

		final List<List<String>> text = new ArrayList<List<String>>();

		List<String> page = new ArrayList<String>();

		page.add(MLN.string("ui.villagemap") + ": "
				+ house.getNativeBuildingName());

		text.add(page);
		page = new ArrayList<String>();

		page.add(MLN.string("panels.mappurple"));
		page.add(MLN.string("panels.mapblue"));
		page.add(MLN.string("panels.mapgreen"));
		page.add(MLN.string("panels.maplightgreen"));
		page.add(MLN.string("panels.mapred"));
		page.add(MLN.string("panels.mapyellow"));
		page.add(MLN.string("panels.maporange"));
		page.add(MLN.string("panels.maplightblue"));
		page.add(MLN.string("panels.mapbrown"));
		text.add(page);

		return text;
	}

	public static void readPacket(final ByteBufInputStream ds) {

		try {

			final Point pos = StreamReadWrite.readNullablePoint(ds);
			final int panelType = ds.read();
			final Point buildingPos = StreamReadWrite.readNullablePoint(ds);
			final long villager_id = ds.readLong();
			final String[][] lines = StreamReadWrite.readStringStringArray(ds);

			MillClientUtilities.updatePanel(pos, lines, panelType, buildingPos,
					villager_id);

			if (MLN.LogNetwork >= MLN.DEBUG) {
				MLN.debug(null, "Receiving panel packet.");
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	public Point buildingPos = null;

	public long villager_id = 0;

	public int panelType = 0;

	@Override
	public boolean func_145914_a() {
		return false;
	}

	public List<List<String>> getFullText(final EntityPlayer player) {

		if (panelType == 0 || buildingPos == null) {
			return null;
		}

		final Building building = Mill.clientWorld.getBuilding(buildingPos);

		if (panelType == etatCivil) {
			return generateEtatCivil(player, building);
		} else if (panelType == constructions) {
			return generateConstructions(player, building);
		} else if (panelType == projects) {
			return generateProjects(player, building);
		} else if (panelType == house) {
			return generateHouse(player, building);
		} else if (panelType == archives) {
			return generateArchives(player, building, villager_id);
		} else if (panelType == resources) {
			return generateResources(player, building);
		} else if (panelType == villageMap) {
			return generateVillageMap(building);
		} else if (panelType == military) {
			return generateMilitary(player, building);
		} else if (panelType == tradeGoods) {
			return generateMarketGoods(building);
		} else if (panelType == innVisitors) {
			return generateInnVisitors(building);
		} else if (panelType == marketMerchants) {
			return generateMarketMerchants(building);
		} else {
			return null;
		}
	}

	public int getMapType() {

		if (panelType == villageMap) {
			return VILLAGE_MAP;
		}

		return 0;
	}
}
