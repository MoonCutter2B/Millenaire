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
import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.construction.BuildingPlan.LocationReturn;
import org.millenaire.common.construction.BuildingPlanSet;
import org.millenaire.common.construction.BuildingProject;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.network.ServerSender;

public class GuiActions {

	public static final int VILLAGE_SCROLL_PRICE=2*64;
	public static final int VILLAGE_SCROLL_REPUTATION=2*64*64;

	public static final int CROP_REPUTATION=2*64*64;
	public static final int CROP_PRICE=8*64;
	public static final int CULTURE_CONTROL_REPUTATION=32*64*64;




	public static void activateMillChest(EntityPlayer player, Point p) {

		final World world=player.worldObj;

		if (MLN.DEV) {

			final MillWorld mw=Mill.getMillWorld(world);

			if (mw.buildingExists(p)) {
				final Building ent=mw.getBuilding(p);

				if ((player.inventory.getCurrentItem() != null) && (player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Blocks.sand))) {
					ent.testModeGoods();
					return;
				}

				if ((player.inventory.getCurrentItem() != null) && (player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Mill.path))) {
					ent.clearOldPaths();
					ent.constructCalculatedPaths();
					return;
				}

				if ((player.inventory.getCurrentItem() != null) && (player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Mill.pathSlab))) {
					ent.recalculatePaths(true);
					return;
				}

				if ((player.inventory.getCurrentItem() != null) && (player.inventory.getCurrentItem().getItem() == Mill.denier_or)) {
					ent.displayInfos(player);
					return;
				}

				if ((player.inventory.getCurrentItem() != null) && (player.inventory.getCurrentItem().getItem() == Items.glass_bottle)) {
					mw.setGlobalTag("alchemy");
					MLN.major(mw, "Set alchemy tag.");
					return;
				}

				if ((player.inventory.getCurrentItem() != null) && (player.inventory.getCurrentItem().getItem() == Mill.summoningWand)) {

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


		if(MillCommonUtilities.isBlockOpaqueCube(world,p.getiX(), p.getiY() + 1, p.getiZ()))
			return;
		if((world.getBlock(p.getiX() - 1, p.getiY(), p.getiZ()) == Mill.lockedChest) && MillCommonUtilities.isBlockOpaqueCube(world,p.getiX() - 1, p.getiY() + 1, p.getiZ()))
			return;
		if((world.getBlock(p.getiX() + 1, p.getiY(), p.getiZ()) == Mill.lockedChest) && MillCommonUtilities.isBlockOpaqueCube(world,p.getiX() + 1, p.getiY() + 1, p.getiZ()))
			return;
		if((world.getBlock(p.getiX(), p.getiY(), p.getiZ() - 1) == Mill.lockedChest) && MillCommonUtilities.isBlockOpaqueCube(world,p.getiX(), p.getiY() + 1, p.getiZ() - 1))
			return;
		if((world.getBlock(p.getiX(), p.getiY(), p.getiZ() + 1) == Mill.lockedChest) && MillCommonUtilities.isBlockOpaqueCube(world,p.getiX(), p.getiY() + 1, p.getiZ() + 1))
			return;

		ServerSender.displayMillChest(player, p);

	}

	public static void controlledBuildingsForgetBuilding(EntityPlayer player,Building townHall,BuildingProject project) {
		townHall.cancelBuilding(project.location);
	}

	public static void controlledBuildingsToggleUpgrades(EntityPlayer player,Building townHall,BuildingProject project,boolean allow) {
		project.location.upgradesAllowed=allow;
		if (allow) {
			townHall.noProjectsLeft=false;
		}
	}

	public static void hireExtend(EntityPlayer player, MillVillager villager) {
		villager.hiredBy=player.getDisplayName();
		villager.hiredUntil+=24000;
		MillCommonUtilities.changeMoney(player.inventory, -villager.getHireCost(player),player);
	}

	public static void hireHire(EntityPlayer player, MillVillager villager) {
		villager.hiredBy=player.getDisplayName();
		villager.hiredUntil=villager.worldObj.getWorldTime()+24000;
		final VillagerRecord vr=villager.getTownHall().getVillagerRecordById(villager.villager_id);
		if (vr!=null) {
			vr.awayhired=true;
		}

		player.addStat(MillAchievements.hired, 1);

		MillCommonUtilities.changeMoney(player.inventory, -villager.getHireCost(player),player);
	}

	public static void hireRelease(EntityPlayer player, MillVillager villager) {
		villager.hiredBy=null;
		villager.hiredUntil=0;
		final VillagerRecord vr=villager.getTownHall().getVillagerRecordById(villager.villager_id);
		if (vr!=null) {
			vr.awayhired=false;
		}
	}

	public static void newBuilding(EntityPlayer player,Building townhall,Point pos,String planKey) {

		final BuildingPlanSet set=townhall.culture.getBuildingPlanSet(planKey);

		if (set==null)
			return;

		final BuildingPlan plan=set.getRandomStartingPlan();

		final LocationReturn lr=plan.testSpot(townhall.winfo, townhall.pathing, townhall.getPos(),
				pos.getiX()-townhall.winfo.mapStartX, pos.getiZ()-townhall.winfo.mapStartZ,  MillCommonUtilities.getRandom(),-1);


		if (lr.location==null) {
			String error=null;
			if (lr.errorCode==LocationReturn.CONSTRUCTION_FORBIDEN) {
				error="ui.constructionforbidden";
			} else if (lr.errorCode==LocationReturn.LOCATION_CLASH) {
				error="ui.locationclash";
			} else if (lr.errorCode==LocationReturn.OUTSIDE_RADIUS) {
				error="ui.outsideradius";
			} else if (lr.errorCode==LocationReturn.WRONG_ALTITUDE) {
				error="ui.wrongelevation";
			} else if (lr.errorCode==LocationReturn.DANGER) {
				error="ui.danger";
			} else if (lr.errorCode==LocationReturn.NOT_REACHABLE) {
				error="ui.notreachable";
			} else {
				error="ui.unknownerror";
			}

			if (MLN.DEV) {
				MillCommonUtilities.setBlock(townhall.mw.world, lr.errorPos.getRelative(0, 30, 0), Blocks.gravel);
			}

			ServerSender.sendTranslatedSentence(player,MLN.ORANGE, "ui.problemat",pos.distanceDirectionShort(lr.errorPos),error);
		} else {
			lr.location.level=-1;
			final BuildingProject project=new BuildingProject(set);
			project.location=lr.location;

			setSign(townhall,lr.location.minx, lr.location.minz,project);
			setSign(townhall,lr.location.maxx, lr.location.minz,project);
			setSign(townhall,lr.location.minx, lr.location.maxz,project);
			setSign(townhall,lr.location.maxx, lr.location.maxz,project);

			townhall.buildingProjects.get(3).add(project);
			townhall.noProjectsLeft=false;
			ServerSender.sendTranslatedSentence(player,MLN.DARKGREEN, "ui.projectadded");
		}
	}

	public static void newVillageCreation(EntityPlayer player, Point pos,
			String cultureKey,String villageTypeKey) {

		final Culture culture=Culture.getCultureByName(cultureKey);

		if (culture==null)
			return;

		final VillageType villageType=culture.getVillageType(villageTypeKey);

		if (villageType==null)
			return;

		final WorldGenVillage genVillage=new WorldGenVillage();
		final boolean result=genVillage.generateVillageAtPoint(player.worldObj, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player,false,true, 0,villageType,null,null);

		if (result) {
			player.addStat(MillAchievements.summoningwand, 1);
			if (villageType.playerControlled) {
				player.addStat(MillAchievements.villageleader, 1);
			}

		}

	}

	public static void pujasChangeEnchantment(EntityPlayer player, Building temple,int enchantmentId) {
		if ((temple!=null) && (temple.pujas!=null)) {
			temple.pujas.changeEnchantment(enchantmentId);
			player.addStat(MillAchievements.puja, 1);
			temple.sendBuildingPacket(player, false);
		}
	}

	public static void questCompleteStep(EntityPlayer player, MillVillager villager) {
		final UserProfile profile=Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		final QuestInstance qi=profile.villagersInQuests.get(villager.villager_id);

		if (qi==null) {
			MLN.error(villager, "Could not find quest instance for this villager.");
		} else {
			qi.completeStep(player,villager);
		}
	}

	public static void questRefuse(EntityPlayer player, MillVillager villager) {
		final UserProfile profile=Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		final QuestInstance qi=profile.villagersInQuests.get(villager.villager_id);
		if (qi==null) {
			MLN.error(villager, "Could not find quest instance for this villager.");
		} else {
			qi.refuseQuest(player,villager);
		}
	}

	private static void setSign(Building townhall,int i,int j,BuildingProject project) {
		MillCommonUtilities.setBlockAndMetadata(townhall.worldObj, i, MillCommonUtilities.findTopSoilBlock(townhall.worldObj, i, j), j, Blocks.standing_sign,0,true,false);
		final TileEntitySign sign=(TileEntitySign) townhall.worldObj.getTileEntity(i, MillCommonUtilities.findTopSoilBlock(townhall.worldObj,i, j), j);
		if (sign!=null) {
			sign.signText=new String[]{project.getNativeName(),"",project.getGameName(),""};
		}
	}

	public static void useNegationWand(EntityPlayer player, Building townHall) {
		ServerSender.sendTranslatedSentence(player, MLN.DARKRED,"negationwand.destroyed",townHall.villageType.name);

		if (!townHall.villageType.lonebuilding) {
			player.addStat(MillAchievements.scipio, 1);
		}

		townHall.destroyVillage();
	}

	public static void useSummoningWand(EntityPlayer player, Point pos) {
		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		final Building closestVillage=mw.getClosestVillage(pos);

		if ((closestVillage != null)
				&& (pos.squareRadiusDistance(closestVillage.getPos())<closestVillage.villageType.radius+10)) {
			if (closestVillage.controlledBy(player.getDisplayName())) {
				ServerSender.displayNewBuildingProjectGUI(player,closestVillage, pos);
				return;
			} else
				return;
		}

		final Block block=MillCommonUtilities.getBlock(player.worldObj, pos);

		if (block == Blocks.obsidian) {
			final WorldGenVillage genVillage=new WorldGenVillage();
			genVillage.generateVillageAtPoint(player.worldObj, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player, false, true, 0,null,null,null);
		}

		if (block == Blocks.gold_block) {
			ServerSender.displayNewVillageGUI(player,pos);

		}

		ServerSender.sendTranslatedSentence(player,MLN.WHITE, "ui.wandinstruction");
	}

	public static void villageChiefPerformBuilding(EntityPlayer player,MillVillager chief,String planKey) {
		final BuildingPlan plan=chief.getTownHall().culture.getBuildingPlanSet(planKey).getRandomStartingPlan();
		chief.getTownHall().buildingsBought.add(planKey);
		MillCommonUtilities.changeMoney(player.inventory, -plan.price,player);
		ServerSender.sendTranslatedSentence(player,MLN.WHITE, "ui.housebought",chief.getName(),plan.nativeName);
	}


	public static void villageChiefPerformCrop(EntityPlayer player,MillVillager chief,String value) {
		final UserProfile profile=Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		profile.setTag(MillWorld.CROP_PLANTING+value);
		MillCommonUtilities.changeMoney(player.inventory, -CROP_PRICE,player);
		ServerSender.sendTranslatedSentence(player,MLN.WHITE, "ui.croplearned",chief.getName(),"item."+value);
	}

	public static void villageChiefPerformCultureControl(EntityPlayer player,MillVillager chief) {
		final UserProfile profile=Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		profile.setTag(MillWorld.CULTURE_CONTROL+chief.getCulture().key);
		ServerSender.sendTranslatedSentence(player,MLN.WHITE, "ui.control_gotten",chief.getName(),"culture."+chief.getCulture().key);
	}

	public static void villageChiefPerformDiplomacy(EntityPlayer player,MillVillager chief,Point village,boolean praise) {
		float effect=0;

		if (praise) {
			effect=10;
		} else {
			effect=-10;
		}

		final int reputation=Math.min(chief.getTownHall().getReputation(player.getDisplayName()),Building.MAX_REPUTATION);

		//coeff is weighted average of log ration and regular ratio (to make it progressive but not too much)
		final float coeff=(float) ((((Math.log(reputation)/Math.log(Building.MAX_REPUTATION))*2)+(reputation/(Building.MAX_REPUTATION)))/3);

		effect*=coeff;

		effect*=(MillCommonUtilities.randomInt(40)+80)/100.0;

		chief.getTownHall().adjustRelation(village,(int) effect,false);

		final UserProfile profile=Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName());
		profile.adjustDiplomacyPoint(chief.getTownHall(), -1);

		if (MLN.LogVillage>=MLN.MAJOR) {
			MLN.major(chief.getTownHall(),"Adjusted relation by "+effect+" (coef: "+coeff+")");
		}
	}

	public static void villageChiefPerformVillageScroll(EntityPlayer player,MillVillager chief) {

		for (int i=0;i<Mill.getMillWorld(player.worldObj).villagesList.pos.size();i++) {
			final Point p=Mill.getMillWorld(player.worldObj).villagesList.pos.get(i);
			if (chief.getTownHall().getPos().sameBlock(p)) {
				MillCommonUtilities.changeMoney(player.inventory, -VILLAGE_SCROLL_PRICE,player);
				MillCommonUtilities.putItemsInChest(player.inventory, Mill.parchmentVillageScroll, i, 1);
				ServerSender.sendTranslatedSentence(player,MLN.WHITE, "ui.scrollbought",chief.getName());
			}
		}
	}


	public static void controlledMilitaryDiplomacy(EntityPlayer player,Building townHall,Point target,int level) {
		townHall.adjustRelation(target,level,true);
		if (!townHall.worldObj.isRemote)
			townHall.sendBuildingPacket(player, false);
	}

	public static void controlledMilitaryPlanRaid(EntityPlayer player,Building townHall,Building target) {
		if (townHall.raidStart==0) {
			townHall.adjustRelation(target.getPos(),-100,true);
			townHall.planRaid(target);
			if (!townHall.worldObj.isRemote)
				townHall.sendBuildingPacket(player, false);
		}
	}

	public static void controlledMilitaryCancelRaid(EntityPlayer player,Building townHall) {
		if (townHall.raidStart==0) {
			townHall.cancelRaid();
			if (!townHall.worldObj.isRemote)
				townHall.sendBuildingPacket(player, false);
		}
	}



}
