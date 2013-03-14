package org.millenaire.common.goal.generic;

import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.item.Goods;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;


public class GoalGenericSlaughterAnimal extends GoalGeneric {


	public String animalKey=null;

	public Vector<InvItem> extraItems=new Vector<InvItem>();
	public Vector<Integer> extraItemsChance=new Vector<Integer>();
	public Vector<String> extraItemsTag=new Vector<String>();

	public GoalGenericSlaughterAnimal() {
		super();
		duration=100;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {

		final Point pos=villager.getPos();
		Entity closest=null;
		Building destBuilding=null;
		double bestDist=Double.MAX_VALUE;

		final Vector<Building> buildings=getBuildings(villager);

		for (Building dest : buildings) {

			if (isDestPossible(villager,dest)) {

				final List<Entity> animals=MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class)EntityList.stringToClassMapping.get(animalKey), dest.getPos(), 15, 10);

				for (final Entity ent : animals) {
					EntityAnimal animal=(EntityAnimal)ent;
					if (!animal.isChild() && !animal.isDead) {
						if ((closest==null) || (pos.distanceTo(ent) < bestDist)) {
							closest=ent;
							destBuilding=dest;
							bestDist=pos.distanceTo(ent);
						}
					}
				}
			}
		}

		if (closest==null)
			return null;

		return packDest(null,destBuilding,closest);
	}


	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_TIGHT;
	}

	@Override
	public int range(MillVillager villager) {
		return 2;
	}

	@Override
	public boolean isFightingGoal() {
		return true;
	}


	@Override
	public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
		return getDestination(villager)!=null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		final Building dest=villager.getGoalBuildingDest();

		if (dest==null)
			return true;

		List<Entity> animals=MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class)EntityList.stringToClassMapping.get(animalKey), villager.getPos(), 2, 5);

		for (final Entity ent : animals) {
			if (!ent.isDead) {
				final EntityAnimal animal=(EntityAnimal)ent;

				if (!animal.isChild()) {
					if(villager.canEntityBeSeen(ent)) {
						villager.setEntityToAttack(ent);

						for (int i=0;i<extraItems.size();i++) {
							if (extraItemsTag.get(i)==null || dest.location.tags.contains(extraItemsTag.get(i))) {
								if (MillCommonUtilities.randomInt(100)<extraItemsChance.get(i)) {
									villager.addToInv(extraItems.get(i),1);
								}
							}
						}

						//if ((MLN.CattleFarmer>=MLN.MAJOR) && villager.extraLog) {
							MLN.major(this, "Attacking: "+ent);
						//}
						return true;
					}
				}
			}
		}

		
		MLN.major(this, "No valid target among: "+animals.size());
		
		animals=MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class)EntityList.stringToClassMapping.get(animalKey), villager.getPos(), 2, 5);

		for (final Entity ent : animals) {
			if (!ent.isDead) {
				final EntityAnimal animal=(EntityAnimal)ent;

				if (!animal.isChild()) {
					if(villager.canEntityBeSeen(ent)) {
						villager.setEntityToAttack(ent);

						for (int i=0;i<extraItems.size();i++) {
							if (extraItemsTag.get(i)==null || dest.location.tags.contains(extraItemsTag.get(i))) {
								if (MillCommonUtilities.randomInt(100)<extraItemsChance.get(i)) {
									villager.addToInv(extraItems.get(i),1);
								}
							}
						}

						//if ((MLN.CattleFarmer>=MLN.MAJOR) && villager.extraLog) {
							MLN.major(this, "Attacking: "+ent);
						//}
						return true;
					}
				}
			}
		}
		
		
		return true;
	}

	public static GoalGenericSlaughterAnimal loadGenericSlaughterAnimalGoal(File file) {

		final GoalGenericSlaughterAnimal g=new GoalGenericSlaughterAnimal();

		g.key=file.getName().split("\\.")[0].toLowerCase();

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {
					final String[] temp=line.split("=");
					if (temp.length!=2) {
						MLN.error(null, "Invalid line when loading generic slaughter goal "+file.getName()+": "+line);
					} else {
						final String key=temp[0].trim().toLowerCase();
						final String value=temp[1].trim();

						if (!GoalGeneric.readGenericGoalConfigLine(g, key, value, file, line)) {
							if (key.equals("animalkey")) {
								if (EntityList.stringToClassMapping.containsKey(value)) {
									g.animalKey=value;
								} else {
									MLN.error(null, "Unknown animalkey in generic slaughter goal "+file.getName()+": "+line+". Careful, it is case-sensitive.");
								}
							}  else if (key.equals("bonusitem")) {
								final String[] temp2=value.split(",");

								if (temp2.length!=3 && temp2.length!=2) {
									MLN.error(null, "bonusitem must take the form of bonusitem=goodname,chanceon100 or bonusitem=goodname,chanceon100,requiredtag (ex: leather,50 or tripes,10,oven) in generic slaughter goal "+file.getName()+": "+line);
								} else {
									if (Goods.goodsName.containsKey(temp2[0])) {
										g.extraItems.add(Goods.goodsName.get(temp2[0]));
										g.extraItemsChance.add(Integer.parseInt(temp2[1]));
										if (temp2.length==3) {
											g.extraItemsTag.add(temp2[2].trim());
										} else {
											g.extraItemsTag.add(null);
										}
									} else {
										MLN.error(null, "Unknown bonusitem item in generic slaughter goal "+file.getName()+": "+line);
									}
								}

							} else {
								MLN.error(null, "Unknown line in generic slaughter goal "+file.getName()+": "+line);
							}
						}
					}
				}
			}

			if (g.animalKey==null) {
				MLN.error(null, "The animalKey is mandatory in custom slaughter goals "+file.getName());
				return null;
			}

			reader.close();
		} catch (final Exception e) {
			MLN.printException(e);

			return null;
		}

		return g;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
		final List<Entity> animals=MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class)EntityList.stringToClassMapping.get(animalKey), b.getPos(), 15, 10);

		if (animals==null)
			return false;

		int nbanimals=0;

		for (final Entity ent : animals) {

			final EntityAnimal animal=(EntityAnimal)ent;

			if (!animal.isChild()) {
				nbanimals++;
			}
		}

		int targetAnimals=0;

		for (int i=0;i<b.spawns.size();i++) {
			if (b.spawnTypes.get(i).equals(animalKey)) {
				targetAnimals=b.spawns.get(i).size();
			}
		}
		return (nbanimals > targetAnimals);
	}
}
