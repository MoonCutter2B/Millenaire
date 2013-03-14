package org.millenaire.common;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;

import org.millenaire.client.MillClientUtilities;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.construction.BuildingProject;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.network.StreamReadWrite;

public class TileEntityPanel extends TileEntitySign {

	public static class PanelPacketInfo {

		Point pos,buildingPos;
		String[][] lines;
		long villager_id;
		int panelType;

		public PanelPacketInfo(Point pos, String[][] lines, Point buildingPos, int panelType, long village_id) {
			this.pos=pos;
			this.lines=lines;
			this.buildingPos=buildingPos;
			this.panelType=panelType;
			this.villager_id=village_id;
		}
	}

	public static final int VILLAGE_MAP = 1;
	public static final int etatCivil=1;
	public static final int constructions=2;
	public static final int projects=3;
	public static final int controlledProjects=4;
	public static final int house=5;
	public static final int resources=6;
	public static final int archives=7;
	public static final int villageMap=8;

	public static final int military=9;
	public static final int tradeGoods=10;

	public static final int innVisitors=11;

	public static final int marketMerchants=12;

	private static void addProjectToList(EntityPlayer player,BuildingProject project,Building townHall,Vector<String> page) {

		if ((project.location==null) || (project.location.level<0)) {
			final BuildingPlan plan=project.planSet.getRandomStartingPlan();

			page.add(plan.getNativeDisplayName(player)+": "+MLN.string("panels.notyetbuilt")+".");
		} else {
			if ((project.location.level+1) < project.getLevelsNumber(project.location.getVariation())) {

				final BuildingPlan plan=project.getPlan(project.location.getVariation(), project.location.level+1);

				final BuildingLocation l=project.location;
				page.add(plan.getNativeDisplayName(player)+" ("+MathHelper.floor_double(l.pos.distanceTo(townHall.getPos()))+"m "+townHall.getPos().directionToShort(l.pos)+"): "+
						MLN.string("panels.nbupgradesleft",""+(project.getLevelsNumber(project.location.getVariation())-project.location.level-1)));
			} else {
				final BuildingPlan plan=project.getPlan(project.location.getVariation(), project.location.level);

				final BuildingLocation l=project.location;
				page.add(plan.getNativeDisplayName(player)+" ("+MathHelper.floor_double(l.pos.distanceTo(townHall.getPos()))+"m "+townHall.getPos().directionToShort(l.pos)+"): "+MLN.string("panels.finished")+".");
			}
		}
	}
	public static Vector<Vector<String>> generateArchives(EntityPlayer player,Building townHall, long villager_id) {

		if ((townHall==null))
			return null;

		final VillagerRecord vr=townHall.getVillagerRecordById(villager_id);

		if ((vr==null))
			return null;

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		final Vector<String> page=new Vector<String>();

		page.add(vr.getName());
		page.add(vr.getGameOccupation(player.username));
		page.add("");

		if ((vr.mothersName!=null) && (vr.mothersName.length()>0)) {
			page.add(MLN.string("panels.mother")+": "+vr.mothersName);
		}
		if ((vr.fathersName!=null) && (vr.fathersName.length()>0)) {
			page.add(MLN.string("panels.father")+": "+vr.fathersName);
		}
		if ((vr.spousesName!=null) && (vr.spousesName.length()>0)) {
			page.add(MLN.string("panels.spouse")+": "+vr.spousesName);
		}

		page.add("");

		MillVillager villager=null;

		for (final MillVillager v : townHall.villagers) {
			if (v.villager_id==vr.id) {
				villager=v;
			}
		}

		page.add("");

		if (villager==null) {
			if (vr.killed) {
				page.add(MLN.string("panels.dead"));
			} else if (vr.awayraiding) {
				page.add(MLN.string("panels.awayraiding"));
			} else if (vr.awayraiding) {
				page.add(MLN.string("panels.awayhired"));
			} else if (vr.raidingVillage && (townHall.worldObj.getWorldTime()<(vr.raiderSpawn+Building.INVADER_SPAWNING_DELAY))) {
				page.add(MLN.string("panels.invaderincoming"));
			} else {
				page.add(MLN.string("panels.missing"));
			}
		} else {
			String occupation="";

			if ((villager.goalKey!=null) && Goal.goals.containsKey(villager.goalKey)) {
				occupation=Goal.goals.get(villager.goalKey).gameName(villager);
			}
			page.add(MLN.string("panels.currentoccupation")+": "+occupation);
		}

		text.add(page);
		return text;
	}
	public static Vector<Vector<String>> generateConstructions(EntityPlayer player,Building townHall) {

		final Vector<String> page=new Vector<String>();


		page.add(MLN.string("panels.constructions")+" : "+townHall.getVillageQualifiedName());

		page.add("");

		for (final Vector<BuildingProject> projectsLevel : townHall.buildingProjects) {
			for (final BuildingProject project : projectsLevel) {
				if (project.location!=null) {
					String level=null;
					if (project.location.level<0) {
						level=MLN.string("ui.notyetbuilt");
					}
					if (project.location.level>0) {
						level=MLN.string("panels.upgrade")+" "+project.location.level;
					}

					final Vector<String> effects=project.location.getBuildingEffects(townHall.worldObj);

					String effect=null;
					if (effects.size()>0) {
						effect="";
						for (final String s : effects) {
							if (effect.length()>0) {
								effect+=", ";
							}
							effect+=s;
						}
					}

					page.add(project.location.getPlan().getNativeDisplayName(player)+": "+MathHelper.floor_double(project.location.pos.distanceTo(townHall.getPos()))+"m "+townHall.getPos().directionToShort(project.location.pos));
					if (level!=null) {
						page.add(level);
					}
					if (effect!=null) {
						page.add(effect);
					}
					page.add("");
				}
			}
		}

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		text.add(page);

		return text;
	}

	public static Vector<Vector<String>> generateEtatCivil(EntityPlayer player,Building townHall) {

		if (townHall==null)
			return null;

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		final Vector<String> page=new Vector<String>();
		final Vector<String> visitorsPage=new Vector<String>();

		page.add(MLN.string("ui.population")+" "+townHall.getVillageQualifiedName());
		page.add("");

		visitorsPage.add(MLN.string("panels.visitors")+":");
		visitorsPage.add("");

		for (final VillagerRecord vr : townHall.vrecords) {



			int nbFound=0;

			boolean belongsToVillage=true;

			for (final MillVillager villager : townHall.villagers) {
				if (villager.villager_id==vr.id) {
					nbFound++;
					belongsToVillage=!villager.isVisitor();
				}
			}
			String error="";

			if (nbFound==0) {
				if (vr.killed) {
					error=" ("+MLN.string("panels.dead").toLowerCase()+")";
				} else if (vr.awayraiding) {
					error=" ("+MLN.string("panels.awayraiding").toLowerCase()+")";
				} else if (vr.awayhired) {
					error=" ("+MLN.string("panels.awayhired").toLowerCase()+")";
				} else if (vr.raidingVillage && (townHall.worldObj.getWorldTime()<(vr.raiderSpawn+Building.INVADER_SPAWNING_DELAY))) {
					error=" ("+MLN.string("panels.invaderincoming").toLowerCase()+")";
				} else if (vr.raidingVillage) {
					error=" ("+MLN.string("panels.raider").toLowerCase()+")";
				} else {
					error=" ("+MLN.string("panels.missing").toLowerCase()+")";
				}
			} else if (nbFound>1) {
				error=" ("+MLN.string("panels.multiple",""+nbFound).toLowerCase()+")";
			}

			if (belongsToVillage) {
				page.add(vr.getName()+", "+vr.getGameOccupation(player.username).toLowerCase()+error);
			} else {
				visitorsPage.add(vr.getName()+", "+vr.getGameOccupation(player.username).toLowerCase()+error);
			}

		}

		text.add(page);
		text.add(visitorsPage);
		return text;
	}

	public static Vector<Vector<String>> generateHouse(EntityPlayer player,Building house) {

		final Vector<String> page=new Vector<String>();


		page.add("House : "+house.getNativeBuildingName());

		page.add("");


		VillagerRecord wife=null,husband=null;

		for (final VillagerRecord vr : house.vrecords) {
			if ((vr.gender==MillVillager.FEMALE) && !vr.getType().isChild) {
				wife=vr;
			}
			if ((vr.gender==MillVillager.MALE) && !vr.getType().isChild) {
				husband=vr;
			}
		}

		if ((wife==null) && (husband==null)) {
			page.add(MLN.string("panels.houseunoccupied"));
		} else if (wife==null) {

			page.add(MLN.string("panels.man")+": "+husband.getName()+", "+husband.getGameOccupation(player.username));
			page.add("");
			if (house.location.femaleResident.size()==0) {
				page.add(MLN.string("panels.nofemaleresident"));
			} else {
				page.add(MLN.string("panels.bachelor"));
			}
		} else if (husband==null) {

			page.add(MLN.string("panels.woman")+": "+wife.getName()+", "+wife.getGameOccupation(player.username));
			page.add("");
			if ((house.location.maleResident==null) || (house.location.maleResident.size()==0)) {
				page.add(MLN.string("panels.nomaleresident"));
			} else {
				page.add(MLN.string("panels.spinster"));
			}
		} else {

			page.add(MLN.string("panels.woman")+": "+wife.getName()+", "+wife.getGameOccupation(player.username).toLowerCase());

			page.add(MLN.string("panels.man")+": "+husband.getName()+", "+husband.getGameOccupation(player.username).toLowerCase());

			if (house.vrecords.size()>2) {
				page.add("");
				page.add(MLN.string("panels.children")+":");
				page.add("");
				for (final VillagerRecord vr : house.vrecords) {
					if (vr.getType().isChild) {

						page.add(vr.getName()+", "+vr.getGameOccupation(player.username).toLowerCase());
					}
				}
			}

		}

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		text.add(page);

		return text;
	}

	public static Vector<Vector<String>> generateInnVisitors(Building house) {

		final Vector<String> page=new Vector<String>();

		page.add(MLN.string("panels.innvisitors",house.getNativeBuildingName())+":");

		page.add("");
		for (int i=house.visitorsList.size()-1;i>-1;i--) {
			final String s=house.visitorsList.get(i);

			if (s.split(";").length>1) {
				if (s.startsWith("storedexports;")) {
					final String[] v=s.split(";");

					String taken="";

					for (int j=2;j<v.length;j++) {
						final InvItem iv=new InvItem(v[j]);

						if (taken.length()>0) {
							taken+=", ";
						}

						taken+=iv.getName()+": "+v[j].split("/")[2];
					}

					page.add(MLN.string("panels.storedexports",v[1],taken));

				} else if (s.startsWith("broughtimport;")) {
					final String[] v=s.split(";");

					String taken="";

					for (int j=2;j<v.length;j++) {
						final InvItem iv=new InvItem(v[j]);

						if (taken.length()>0) {
							taken+=", ";
						}

						taken+=iv.getName()+": "+v[j].split("/")[2];
					}

					page.add(MLN.string("panels.broughtimport",v[1],taken));
				} else {
					page.add(MLN.string(s.split(";")));
				}
			} else {//old system, already prepared sentence
				page.add(s);
			}

			page.add("");
		}


		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		text.add(page);

		return text;
	}


	public static Vector<Vector<String>> generateMarketGoods(Building house) {

		final Vector<String> page=new Vector<String>();

		page.add(MLN.string("panels.goodstraded")+":");

		page.add("");
		page.add(MLN.string("panels.goodsimported")+":");
		page.add("");
		for (final InvItem good : house.imported.keySet()) {
			page.add(good.getName()+": "+house.imported.get(good));
		}

		page.add("");
		page.add(MLN.string("panels.goodsexported")+":");
		page.add("");
		for (final InvItem good : house.exported.keySet()) {
			page.add(good.getName()+": "+house.exported.get(good));
		}


		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		text.add(page);

		return text;
	}

	public static Vector<Vector<String>> generateMarketMerchants(Building market) {

		if (market==null)
			return null;

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		final Vector<String> page=new Vector<String>();

		page.add(MLN.string("panels.merchantlist")+": ");
		page.add("("+MLN.string("panels.capacity")+": "+market.stalls.size()+")");
		page.add("");

		for (final VillagerRecord vr : market.vrecords) {
			MillVillager v=null;
			for (final MillVillager av : market.villagers) {
				if (vr.matches(av)) {
					v=av;
				}
			}
			page.add(vr.getName());
			if (v==null) {
				if (vr.killed) {
					page.add(MLN.string("panels.dead"));
				} else {
					page.add(MLN.string("panels.missing"));
				}
			} else {
				page.add(v.getNativeOccupationName());
				page.add(MLN.string("panels.nbnightsin",""+v.foreignMerchantNbNights));
				page.add("");
			}
		}

		text.add(page);
		return text;
	}

	public static Vector<Vector<String>> generateMilitary(EntityPlayer player,Building townHall) {

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		Vector<String> page=new Vector<String>();

		page.add(MLN.string("panels.military")+" : "+townHall.getVillageQualifiedName());
		page.add("");

		int nbAttackers=0;
		Point attackingVillagePos=null;

		if (townHall.raidTarget!=null) {
			final Building target=Mill.clientWorld.getBuilding(townHall.raidTarget);

			if (target!=null) {
				if (townHall.raidStart>0) {
					page.add(MLN.string("panels.raidinprogresslong",target.getVillageQualifiedName(),
							""+Math.round((townHall.worldObj.getWorldTime()-townHall.raidStart)/1000)));
				} else {
					page.add(MLN.string("panels.planningraidlong",target.getVillageQualifiedName(),
							""+Math.round((townHall.worldObj.getWorldTime()-townHall.raidPlanningStart)/1000)));
				}

				page.add("");
			}
		} else {

			for (final VillagerRecord vr : townHall.vrecords) {
				if (vr.raidingVillage) {
					nbAttackers++;
					attackingVillagePos=vr.originalVillagePos;
				}
			}
			if (nbAttackers>0) {
				final Building attackingVillage=Mill.clientWorld.getBuilding(attackingVillagePos);

				String attackedBy;
				if (attackingVillage!=null) {
					attackedBy=attackingVillage.getVillageQualifiedName();
				} else {
					attackedBy=MLN.string("panels.unknownattacker");
				}

				page.add(MLN.string("panels.underattacklong",""+nbAttackers,""+townHall.getVillageAttackerStrength(),attackedBy));
				page.add("");
			}
		}

		page.add(MLN.string("panels.offenselong",""+townHall.getVillageRaidingStrength()));
		page.add(MLN.string("panels.defenselong",""+townHall.getVillageDefendingStrength()));

		text.add(page);

		page=new Vector<String>();

		page.add(MLN.string("panels.villagefighters"));
		page.add("");

		for (final VillagerRecord vr : townHall.vrecords) {
			if ((vr.getType().isRaider || vr.getType().helpInAttacks) && !vr.raidingVillage) {
				String status="";

				if (vr.getType().helpInAttacks) {
					status+=MLN.string("panels.defender");
				}

				if (vr.getType().isRaider) {
					if (status.length()>0) {
						status+=", ";
					}
					status+=MLN.string("panels.raider");
				}

				if (vr.awayraiding) {
					status+=", "+MLN.string("panels.awayraiding");
				} else if (vr.awayhired) {
					status+=", "+MLN.string("panels.awayhired");
				} else if (vr.raidingVillage && (townHall.worldObj.getWorldTime()<(vr.raiderSpawn+Building.INVADER_SPAWNING_DELAY))) {
					status+=", "+MLN.string("panels.invaderincoming");
				} else if (vr.killed) {
					status+=", "+MLN.string("panels.dead");
				}

				String weapon="";

				final Item bestMelee=vr.getBestMeleeWeapon();
				if (bestMelee!=null) {
					weapon=Mill.proxy.getItemName(bestMelee.itemID,0);
				}

				if (vr.getType().isArcher && (vr.countInv(Item.bow.itemID)>0)) {
					if (weapon.length()>0) {
						weapon+=", ";
					}
					weapon+=Mill.proxy.getItemName(Item.bow.itemID,0);
				}

				page.add(vr.getName()+", "+vr.getGameOccupation(player.username));
				page.add(status);
				page.add(MLN.string("panels.health")+": "+vr.getMaxHealth()+
						", "+MLN.string("panels.armour")+": "+vr.getTotalArmorValue()+
						", "+MLN.string("panels.weapons")+": "+weapon+
						", "+MLN.string("panels.militarystrength")+": "+vr.getMilitaryStrength());
				page.add("");
			}



		}

		text.add(page);

		if (nbAttackers>0) {

			page=new Vector<String>();
			page.add(MLN.string("panels.attackers"));
			page.add("");

			for (final VillagerRecord vr : townHall.vrecords) {
				if (vr.raidingVillage) {
					String status="";
					if (vr.killed) {
						status=MLN.string("panels.dead");
					}

					String weapon="";

					final Item bestMelee=vr.getBestMeleeWeapon();
					if (bestMelee!=null) {
						weapon=Mill.proxy.getItemName(bestMelee.itemID,0);
					}

					if (vr.getType().isArcher && (vr.countInv(Item.bow.itemID)>0)) {
						if (weapon.length()>0) {
							weapon+=", ";
						}
						weapon+=Mill.proxy.getItemName(Item.bow.itemID,0);
					}

					page.add(vr.getName()+", "+vr.getGameOccupation(player.username));
					page.add(status);
					page.add(MLN.string("panels.health")+": "+vr.getMaxHealth()+
							", "+MLN.string("panels.armour")+": "+vr.getTotalArmorValue()+
							", "+MLN.string("panels.weapons")+": "+weapon+
							", "+MLN.string("panels.militarystrength")+": "+vr.getMilitaryStrength());
					page.add("");
				}
			}
			text.add(page);
		}

		if (townHall.raidsPerformed.size()>0) {

			page=new Vector<String>();

			page.add(MLN.string("panels.raidsperformed"));
			page.add("");

			for (int i=townHall.raidsPerformed.size()-1;i>=0;i--) {
				final String s=townHall.raidsPerformed.get(i);

				if (s.split(";").length>1) {
					if (s.split(";")[0].equals("failure")) {
						page.add(MLN.string("raid.historyfailure",s.split(";")[1]));
					} else {

						final String[] v=s.split(";");
						String taken="";

						for (int j=2;j<v.length;j++) {
							final InvItem iv=new InvItem(v[j]);

							if (taken.length()>0) {
								taken+=", ";
							}

							taken+=iv.getName()+": "+v[j].split("/")[2];
						}

						if (taken.length()==0) {
							taken=MLN.string("raid.nothing");
						}

						page.add(MLN.string("raid.historysuccess",s.split(";")[1],taken));
					}

				} else {//old system, already prepared sentence
					page.add(townHall.raidsPerformed.get(i));
				}
				page.add("");
			}

			text.add(page);
		}

		if (townHall.raidsSuffered.size()>0) {

			page=new Vector<String>();

			page.add(MLN.string("panels.raidssuffered"));
			page.add("");

			for (int i=townHall.raidsSuffered.size()-1;i>=0;i--) {

				final String s=townHall.raidsSuffered.get(i);

				if (s.split(";").length>1) {
					if (s.split(";")[0].equals("failure")) {
						page.add(MLN.string("raid.historydefended",s.split(";")[1]));
					} else {

						final String[] v=s.split(";");
						String taken="";

						for (int j=2;j<v.length;j++) {
							final InvItem iv=new InvItem(v[j]);

							if (taken.length()>0) {
								taken+=", ";
							}

							taken+=iv.getName()+": "+v[j].split("/")[2];
						}

						if (taken.length()==0) {
							taken=MLN.string("raid.nothing");
						}

						page.add(MLN.string("raid.historyraided",s.split(";")[1],taken));
					}

				} else {//old system, already prepared sentence
					page.add(townHall.raidsSuffered.get(i));
				}
				page.add("");
			}

			text.add(page);

		}

		return text;
	}

	public static Vector<Vector<String>> generateProjects(EntityPlayer player,Building townHall) {

		if (townHall.villageType==null)
			return null;

		final Vector<String> page=new Vector<String>();

		page.add(MLN.string("panels.buildingprojects")+" : "+townHall.getVillageQualifiedName());

		page.add("");

		for (int i=0;i<townHall.buildingProjects.size();i++) {
			if (!townHall.villageType.playerControlled || (i==0) || (i==1) || (i==3)) {//for controlled villages, only centre, start and core
				final Vector<BuildingProject> projectsLevel=townHall.buildingProjects.get(i);
				page.add(VillageType.levelNames[i]+":");
				page.add("");

				for (final BuildingProject project : projectsLevel) {
					if (townHall.isDisplayableProject(project)) {
						addProjectToList(player,project,townHall,page);
					}
				}
				page.add("");
			}
		}

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();
		text.add(page);

		return text;
	}

	public static Vector<Vector<String>> generateResources(EntityPlayer player,Building house) {

		Vector<String> page=new Vector<String>();

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();

		page.add(MLN.string("panels.resources")+": "+house.getNativeBuildingName());

		page.add("");

		final BuildingPlan goalPlan=house.getCurrentGoalBuildingPlan();

		final Vector<InvItem> res=new Vector<InvItem>();
		final Vector<Integer> resCost=new Vector<Integer>();
		final Vector<Integer> resHas=new Vector<Integer>();

		if (goalPlan != null) {
			for (final InvItem key : goalPlan.resCost.keySet()) {
				res.add(key);
				resCost.add(goalPlan.resCost.get(key));
				int has=house.countGoods(key.id(),key.meta);
				if ((house.builder != null) && (house.buildingLocationIP != null) && house.buildingLocationIP.key.equals(house.buildingGoal)) {
					has+=house.builder.countInv(key.id(),key.meta);
				}
				if (has > goalPlan.resCost.get(key)) {
					has=goalPlan.resCost.get(key);
				}

				resHas.add(has);
			}

			page.add(MLN.string("panels.resourcesneeded")+":");

			String name,gameName=goalPlan.getGameName();
			if ((goalPlan.nativeName!=null) && (goalPlan.nativeName.length()>0)) {
				name=goalPlan.nativeName;
			} else if ((goalPlan.getGameName()!=null) && (goalPlan.getGameName().length()>0)) {
				name=goalPlan.getGameName();
				gameName="";
			} else {
				name="";
			}

			if ((gameName!=null) && (gameName.length()>0)) {
				name+=" ("+gameName+")";
			}

			String status="";
			if ((house.buildingLocationIP != null) && house.buildingLocationIP.key.equals(house.buildingGoal)) {
				if (house.buildingLocationIP.level==0) {
					status=MLN.string("ui.inconstruction");
				} else {
					status=MLN.string("ui.upgrading")+" ("+house.buildingLocationIP.level+")";
				}
			} else {
				status=MLN.string(house.buildingGoalIssue);
			}

			page.add(name+" - "+status);

			page.add("");

			for (int i=0;i<resHas.size();i++) {
				page.add(res.get(i).getName()+": "+resHas.get(i)+"/"+resCost.get(i));
			}

			text.add(page);

			page=new Vector<String>();

		}

		page.add(MLN.string("panels.resourcesavailable")+":");

		page.add("");

		final HashMap<InvItem,Integer> contents=house.getChestsContent();

		for (final InvItem key : contents.keySet()) {
			page.add(key.getName()+": "+contents.get(key));
		}

		text.add(page);

		return text;
	}

	public static Vector<Vector<String>> generateVillageMap(Building house) {

		final Vector<Vector<String>> text=new  Vector<Vector<String>>();

		Vector<String> page=new Vector<String>();

		page.add(MLN.string("ui.villagemap")+": "+house.getNativeBuildingName());

		text.add(page);
		page=new Vector<String>();

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

	public static void readPacket(DataInputStream ds) {

		try {

			final Point pos = StreamReadWrite.readNullablePoint(ds);
			final int panelType=ds.read();
			final Point buildingPos=StreamReadWrite.readNullablePoint(ds);
			final long villager_id=ds.readLong();
			final String[][]lines=StreamReadWrite.readStringStringArray(ds);

			MillClientUtilities.updatePanel(pos, lines, panelType, buildingPos, villager_id);

			if (MLN.Network>=MLN.DEBUG) {
				MLN.debug(null, "Receiving panel packet.");
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	public Point buildingPos=null;

	public long villager_id=0;

	public int panelType=0;

	public Vector<Vector<String>> getFullText(EntityPlayer player) {

		if ((panelType==0) || (buildingPos==null))
			return null;

		final Building building=Mill.clientWorld.getBuilding(buildingPos);

		if (panelType==etatCivil)
			return generateEtatCivil(player,building);
		else if (panelType==constructions)
			return generateConstructions(player,building);
		else if (panelType==projects)
			return generateProjects(player,building);
		else if (panelType==house)
			return generateHouse(player,building);
		else if (panelType==archives)
			return generateArchives(player,building, villager_id);
		else if (panelType==resources)
			return generateResources(player,building);
		else if (panelType==villageMap)
			return generateVillageMap(building);
		else if (panelType==military)
			return generateMilitary(player,building);
		else if (panelType==tradeGoods)
			return generateMarketGoods(building);
		else if (panelType==innVisitors)
			return generateInnVisitors(building);
		else if (panelType==marketMerchants)
			return generateMarketMerchants(building);
		else
			return null;
	}

	public int getMapType() {

		if (panelType==villageMap)
			return VILLAGE_MAP;

		return 0;
	}

	@Override
	public boolean isEditable() {
		return false;
	}
}
