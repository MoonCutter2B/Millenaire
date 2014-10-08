package org.millenaire.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingCustomPlan;
import org.millenaire.common.building.BuildingPlan;
import org.millenaire.common.building.BuildingPlan.LocationReturn;
import org.millenaire.common.building.BuildingPlanSet;
import org.millenaire.common.building.BuildingProject;
import org.millenaire.common.building.BuildingProject.EnumProjects;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.network.ServerSender;

public class GuiActions {

	public static final int VILLAGE_SCROLL_PRICE = 2 * 64;
	public static final int VILLAGE_SCROLL_REPUTATION = 2 * 64 * 64;

	public static final int CROP_REPUTATION = 2 * 64 * 64;
	public static final int CROP_PRICE = 8 * 64;
	public static final int CULTURE_CONTROL_REPUTATION = 32 * 64 * 64;

	public static void activateMillChest(final EntityPlayer player, final Point p) {

		final World world = player.worldObj;

		if (MLN.DEV) {

			final MillWorld mw = Mill.getMillWorld(world);

			if (mw.buildingExists(p)) {
				final Building ent = mw.getBuilding(p);

				if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Blocks.sand)) {
					ent.testModeGoods();
					return;
				}

				if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Mill.path)) {
					ent.clearOldPaths();
					ent.constructCalculatedPaths();
					return;
				}

				if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Mill.pathSlab)) {
					ent.recalculatePaths(true);
					return;
				}

				if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == Mill.denier_or) {
					ent.displayInfos(player);
					return;
				}

				if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == Items.glass_bottle) {
					mw.setGlobalTag("alchemy");
					MLN.major(mw, "Set alchemy tag.");
					return;
				}

				if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == Mill.summoningWand) {

					ent.displayInfos(player);

					try {
						if (ent.isTownhall) {
							ent.rushBuilding();
						}
						if (ent.isInn) {
							ent.attemptMerchantMove(true);
						}
						if (ent.isMarket) {
							ent.updateMarket(true);
						}
					} catch (final MillenaireException e) {
						MLN.printException(e);
					}
					return;
				}
			}
		}

		if (MillCommonUtilities.isBlockOpaqueCube(world, p.getiX(), p.getiY() + 1, p.getiZ())) {
			return;
		}
		if (world.getBlock(p.getiX() - 1, p.getiY(), p.getiZ()) == Mill.lockedChest && MillCommonUtilities.isBlockOpaqueCube(world, p.getiX() - 1, p.getiY() + 1, p.getiZ())) {
			return;
		}
		if (world.getBlock(p.getiX() + 1, p.getiY(), p.getiZ()) == Mill.lockedChest && MillCommonUtilities.isBlockOpaqueCube(world, p.getiX() + 1, p.getiY() + 1, p.getiZ())) {
			return;
		}
		if (world.getBlock(p.getiX(), p.getiY(), p.getiZ() - 1) == Mill.lockedChest && MillCommonUtilities.isBlockOpaqueCube(world, p.getiX(), p.getiY() + 1, p.getiZ() - 1)) {
			return;
		}
		if (world.getBlock(p.getiX(), p.getiY(), p.getiZ() + 1) == Mill.lockedChest && MillCommonUtilities.isBlockOpaqueCube(world, p.getiX(), p.getiY() + 1, p.getiZ() + 1)) {
			return;
		}

		ServerSender.displayMillChest(player, p);

	}

	public static void controlledBuildingsForgetBuilding(final EntityPlayer player, final Building townHall, final BuildingProject project) {
		townHall.cancelBuilding(project.location);
	}

	public static void controlledBuildingsToggleUpgrades(final EntityPlayer player, final Building townHall, final BuildingProject project, final boolean allow) {
		project.location.upgradesAllowed = allow;
		if (allow) {
			townHall.noProjectsLeft = false;
		}
	}

	public static void controlledMilitaryCancelRaid(final EntityPlayer player, final Building townHall) {
		if (townHall.raidStart == 0) {
			townHall.cancelRaid();
			if (!townHall.worldObj.isRemote) {
				townHall.sendBuildingPacket(player, false);
			}
		}
	}

	public static void controlledMilitaryDiplomacy(final EntityPlayer player, final Building townHall, final Point target, final int level) {
		townHall.adjustRelation(target, level, true);
		if (!townHall.worldObj.isRemote) {
			townHall.sendBuildingPacket(player, false);
		}
	}

	public static void controlledMilitaryPlanRaid(final EntityPlayer player, final Building townHall, final Building target) {
		if (townHall.raidStart == 0) {
			townHall.adjustRelation(target.getPos(), -100, true);
			townHall.planRaid(target);
			if (!townHall.worldObj.isRemote) {
				townHall.sendBuildingPacket(player, false);
			}
		}
	}

	public static void hireExtend(final EntityPlayer player, final MillVillager villager) {
		villager.hiredBy = player.getDisplayName();
		villager.hiredUntil += 24000;
		MillCommonUtilities.changeMoney(player.inventory, -villager.getHireCost(player), player);
	}

	public static void hireHire(final EntityPlayer player, final MillVillager villager) {
		villager.hiredBy = player.getDisplayName();
		villager.hiredUntil = villager.worldObj.getWorldTime() + 24000;
		final VillagerRecord vr = villager.getTownHall().getVillagerRecordById(villager.villager_id);
		if (vr != null) {
			vr.awayhired = true;
		}

		player.addStat(MillAchievements.hired, 1);

		MillCommonUtilities.changeMoney(player.inventory, -villager.getHireCost(player), player);
	}

	public static void hireRelease(final EntityPlayer player, final MillVillager villager) {
		villager.hiredBy = null;
		villager.hiredUntil = 0;
		final VillagerRecord vr = villager.getTownHall().getVillagerRecordById(villager.villager_id);
		if (vr != null) {
			vr.awayhired = false;
		}
	}

	public static void newBuilding(final EntityPlayer player, final Building townHall, final Point pos, final String planKey) {

		final BuildingPlanSet set = townHall.culture.getBuildingPlanSet(planKey);

		if (set == null) {
			return;
		}

		final BuildingPlan plan = set.getRandomStartingPlan();

		final LocationReturn lr = plan.testSpot(townHall.winfo, townHall.pathing, townHall.getPos(), pos.getiX() - townHall.winfo.mapStartX, pos.getiZ() - townHall.winfo.mapStartZ,
				MillCommonUtilities.getRandom(), -1);

		if (lr.location == null) {
			String error = null;
			if (lr.errorCode == LocationReturn.CONSTRUCTION_FORBIDEN) {
				error = "ui.constructionforbidden";
			} else if (lr.errorCode == LocationReturn.LOCATION_CLASH) {
				error = "ui.locationclash";
			} else if (lr.errorCode == LocationReturn.OUTSIDE_RADIUS) {
				error = "ui.outsideradius";
			} else if (lr.errorCode == LocationReturn.WRONG_ALTITUDE) {
				error = "ui.wrongelevation";
			} else if (lr.errorCode == LocationReturn.DANGER) {
				error = "ui.danger";
			} else if (lr.errorCode == LocationReturn.NOT_REACHABLE) {
				error = "ui.notreachable";
			} else {
				error = "ui.unknownerror";
			}

			if (MLN.DEV) {
				MillCommonUtilities.setBlock(townHall.mw.world, lr.errorPos.getRelative(0, 30, 0), Blocks.gravel);
			}

			ServerSender.sendTranslatedSentence(player, MLN.ORANGE, "ui.problemat", pos.distanceDirectionShort(lr.errorPos), error);
		} else {
			lr.location.level = -1;
			final BuildingProject project = new BuildingProject(set);
			project.location = lr.location;

			setSign(townHall, lr.location.minx, lr.location.minz, project);
			setSign(townHall, lr.location.maxx, lr.location.minz, project);
			setSign(townHall, lr.location.minx, lr.location.maxz, project);
			setSign(townHall, lr.location.maxx, lr.location.maxz, project);

			townHall.buildingProjects.get(EnumProjects.CORE).add(project);
			townHall.noProjectsLeft = false;
			ServerSender.sendTranslatedSentence(player, MLN.DARKGREEN, "ui.projectadded");
		}
	}

	/**
	 * Creates a new custom building server-side
	 */
	public static void newCustomBuilding(final EntityPlayer player, final Building townHall, final Point pos, final String planKey) {

		final BuildingCustomPlan customBuilding = townHall.culture.getBuildingCustom(planKey);

		if (customBuilding != null) {

			try {
				townHall.addCustomBuilding(customBuilding, pos);
			} catch (final Exception e) {
				MLN.printException("Exception when creation custom building: " + planKey, e);
			}
		}

	}

	public static void newVillageCreation(final EntityPlayer player, final Point pos, final String cultureKey, final String villageTypeKey) {

		final Culture culture = Culture.getCultureByName(cultureKey);

		if (culture == null) {
			return;
		}

		final VillageType villageType = culture.getVillageType(villageTypeKey);

		if (villageType == null) {
			return;
		}

		final WorldGenVillage genVillage = new WorldGenVillage();
		final boolean result = genVillage.generateVillageAtPoint(player.worldObj, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player, false, true, 0, villageType, null, null);

		if (result) {
			player.addStat(MillAchievements.summoningwand, 1);
			if (villageType.playerControlled) {
				player.addStat(MillAchievements.villageleader, 1);
			}
		}

	}

	public static void pujasChangeEnchantment(final EntityPlayer player, final Building temple, final int enchantmentId) {
		if (temple != null && temple.pujas != null) {
			temple.pujas.changeEnchantment(enchantmentId);
			player.addStat(MillAchievements.puja, 1);
			temple.sendBuildingPacket(player, false);
		}
	}

	public static void questCompleteStep(final EntityPlayer player, final MillVillager villager) {
		final UserProfile profile = Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		final QuestInstance qi = profile.villagersInQuests.get(villager.villager_id);

		if (qi == null) {
			MLN.error(villager, "Could not find quest instance for this villager.");
		} else {
			qi.completeStep(player, villager);
		}
	}

	public static void questRefuse(final EntityPlayer player, final MillVillager villager) {
		final UserProfile profile = Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		final QuestInstance qi = profile.villagersInQuests.get(villager.villager_id);
		if (qi == null) {
			MLN.error(villager, "Could not find quest instance for this villager.");
		} else {
			qi.refuseQuest(player, villager);
		}
	}

	private static void setSign(final Building townHall, final int i, final int j, final BuildingProject project) {
		MillCommonUtilities.setBlockAndMetadata(townHall.worldObj, i, MillCommonUtilities.findTopSoilBlock(townHall.worldObj, i, j), j, Blocks.standing_sign, 0, true, false);
		final TileEntitySign sign = (TileEntitySign) townHall.worldObj.getTileEntity(i, MillCommonUtilities.findTopSoilBlock(townHall.worldObj, i, j), j);
		if (sign != null) {
			sign.signText = new String[] { project.getNativeName(), "", project.getGameName(), "" };
		}
	}

	public static void updateCustomBuilding(final EntityPlayer player, final Building building) {
		if (building.location.getCustomPlan() != null) {
			building.location.getCustomPlan().registerResources(building, building.location);
		}
	}

	public static void useNegationWand(final EntityPlayer player, final Building townHall) {
		ServerSender.sendTranslatedSentence(player, MLN.DARKRED, "negationwand.destroyed", townHall.villageType.name);

		if (!townHall.villageType.lonebuilding) {
			player.addStat(MillAchievements.scipio, 1);
		}

		townHall.destroyVillage();
	}

	public static void useSummoningWand(final EntityPlayer player, final Point pos) {
		final MillWorld mw = Mill.getMillWorld(player.worldObj);

		final Building closestVillage = mw.getClosestVillage(pos);

		if (closestVillage != null && pos.squareRadiusDistance(closestVillage.getPos()) < closestVillage.villageType.radius + 10) {
			if (closestVillage.controlledBy(player.getDisplayName())) {
				final Building b = closestVillage.getBuildingAtCoord(pos);

				if (b != null) {
					if (b.location.isCustomBuilding) {
						ServerSender.displayNewBuildingProjectGUI(player, closestVillage, pos);
					} else {
						ServerSender.sendTranslatedSentence(player, MLN.YELLOW, "ui.wand_locationinuse");
					}
				} else {
					ServerSender.displayNewBuildingProjectGUI(player, closestVillage, pos);
				}

				return;
			} else {
				ServerSender.sendTranslatedSentence(player, MLN.YELLOW, "ui.wand_invillagerange", closestVillage.getVillageQualifiedName());
				return;
			}
		}

		final Block block = MillCommonUtilities.getBlock(player.worldObj, pos);

		if (block == Blocks.obsidian) {
			final WorldGenVillage genVillage = new WorldGenVillage();
			genVillage.generateVillageAtPoint(player.worldObj, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player, false, true, 0, null, null, null);
		}

		if (block == Blocks.gold_block) {
			ServerSender.displayNewVillageGUI(player, pos);

		}

		ServerSender.sendTranslatedSentence(player, MLN.WHITE, "ui.wandinstruction");
	}

	public static void villageChiefPerformBuilding(final EntityPlayer player, final MillVillager chief, final String planKey) {
		final BuildingPlan plan = chief.getTownHall().culture.getBuildingPlanSet(planKey).getRandomStartingPlan();
		chief.getTownHall().buildingsBought.add(planKey);
		MillCommonUtilities.changeMoney(player.inventory, -plan.price, player);
		ServerSender.sendTranslatedSentence(player, MLN.WHITE, "ui.housebought", chief.getName(), plan.nativeName);
	}

	public static void villageChiefPerformCrop(final EntityPlayer player, final MillVillager chief, final String value) {
		final UserProfile profile = Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		profile.setTag(MillWorld.CROP_PLANTING + value);
		MillCommonUtilities.changeMoney(player.inventory, -CROP_PRICE, player);
		ServerSender.sendTranslatedSentence(player, MLN.WHITE, "ui.croplearned", chief.getName(), "item." + value);
	}

	public static void villageChiefPerformCultureControl(final EntityPlayer player, final MillVillager chief) {
		final UserProfile profile = Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		profile.setTag(MillWorld.CULTURE_CONTROL + chief.getCulture().key);
		ServerSender.sendTranslatedSentence(player, MLN.WHITE, "ui.control_gotten", chief.getName(), "culture." + chief.getCulture().key);
	}

	public static void villageChiefPerformDiplomacy(final EntityPlayer player, final MillVillager chief, final Point village, final boolean praise) {
		float effect = 0;

		if (praise) {
			effect = 10;
		} else {
			effect = -10;
		}

		final int reputation = Math.min(chief.getTownHall().getReputation(player.getDisplayName()), Building.MAX_REPUTATION);

		// coeff is weighted average of log ration and regular ratio (to make it
		// progressive but not too much)
		final float coeff = (float) ((Math.log(reputation) / Math.log(Building.MAX_REPUTATION) * 2 + reputation / Building.MAX_REPUTATION) / 3);

		effect *= coeff;

		effect *= (MillCommonUtilities.randomInt(40) + 80) / 100.0;

		chief.getTownHall().adjustRelation(village, (int) effect, false);

		final UserProfile profile = Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		profile.adjustDiplomacyPoint(chief.getTownHall(), -1);

		if (MLN.LogVillage >= MLN.MAJOR) {
			MLN.major(chief.getTownHall(), "Adjusted relation by " + effect + " (coef: " + coeff + ")");
		}
	}

	public static void villageChiefPerformVillageScroll(final EntityPlayer player, final MillVillager chief) {

		for (int i = 0; i < Mill.getMillWorld(player.worldObj).villagesList.pos.size(); i++) {
			final Point p = Mill.getMillWorld(player.worldObj).villagesList.pos.get(i);
			if (chief.getTownHall().getPos().sameBlock(p)) {
				MillCommonUtilities.changeMoney(player.inventory, -VILLAGE_SCROLL_PRICE, player);
				MillCommonUtilities.putItemsInChest(player.inventory, Mill.parchmentVillageScroll, i, 1);
				ServerSender.sendTranslatedSentence(player, MLN.WHITE, "ui.scrollbought", chief.getName());
			}
		}
	}

}
