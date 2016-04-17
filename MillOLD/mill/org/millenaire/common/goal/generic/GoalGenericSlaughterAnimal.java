package org.millenaire.common.goal.generic;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalGenericSlaughterAnimal extends GoalGeneric {

	public static GoalGenericSlaughterAnimal loadGenericSlaughterAnimalGoal(final File file) {

		final GoalGenericSlaughterAnimal g = new GoalGenericSlaughterAnimal();

		g.key = file.getName().split("\\.")[0].toLowerCase();

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0 && !line.startsWith("//")) {
					final String[] temp = line.split("=");
					if (temp.length != 2) {
						MLN.error(null, "Invalid line when loading generic slaughter goal " + file.getName() + ": " + line);
					} else {
						final String key = temp[0].trim().toLowerCase();
						final String value = temp[1].trim();

						if (!GoalGeneric.readGenericGoalConfigLine(g, key, value, file, line)) {
							if (key.equals("animalkey")) {
								if (EntityList.stringToClassMapping.containsKey(value)) {
									g.animalKey = value;
								} else {
									MLN.error(null, "Unknown animalkey in generic slaughter goal " + file.getName() + ": " + line + ". Careful, it is case-sensitive.");
								}
							} else if (key.equals("bonusitem")) {
								final String[] temp2 = value.split(",");

								if (temp2.length != 3 && temp2.length != 2) {
									MLN.error(null,
											"bonusitem must take the form of bonusitem=goodname,chanceon100 or bonusitem=goodname,chanceon100,requiredtag (ex: leather,50 or tripes,10,oven) in generic slaughter goal "
													+ file.getName() + ": " + line);
								} else {
									if (Goods.goodsName.containsKey(temp2[0])) {
										g.extraItems.add(Goods.goodsName.get(temp2[0]));
										g.extraItemsChance.add(Integer.parseInt(temp2[1]));
										if (temp2.length == 3) {
											g.extraItemsTag.add(temp2[2].trim());
										} else {
											g.extraItemsTag.add(null);
										}
									} else {
										MLN.error(null, "Unknown bonusitem item in generic slaughter goal " + file.getName() + ": " + line);
									}
								}

							} else {
								MLN.error(null, "Unknown line in generic slaughter goal " + file.getName() + ": " + line);
							}
						}
					}
				}
			}

			if (g.animalKey == null) {
				MLN.error(null, "The animalKey is mandatory in custom slaughter goals " + file.getName());
				return null;
			}

			reader.close();
		} catch (final Exception e) {
			MLN.printException(e);

			return null;
		}

		return g;

	}

	public String animalKey = null;
	public List<InvItem> extraItems = new ArrayList<InvItem>();
	public List<Integer> extraItemsChance = new ArrayList<Integer>();

	public List<String> extraItemsTag = new ArrayList<String>();

	public GoalGenericSlaughterAnimal() {
		super();
		duration = 100;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		final Point pos = villager.getPos();
		Entity closest = null;
		Building destBuilding = null;
		double bestDist = Double.MAX_VALUE;

		final List<Building> buildings = getBuildings(villager);

		for (final Building dest : buildings) {

			if (isDestPossible(villager, dest)) {

				final List<Entity> animals = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class) EntityList.stringToClassMapping.get(animalKey), dest.getPos(), 15, 10);

				for (final Entity ent : animals) {
					if (!ent.isDead && !isEntityChild(ent)) {
						if (closest == null || pos.distanceTo(ent) < bestDist) {
							closest = ent;
							destBuilding = dest;
							bestDist = pos.distanceTo(ent);
						}
					}
				}
			}
		}

		if (closest == null) {
			return null;
		}

		return packDest(null, destBuilding, closest);
	}

	@Override
	public AStarConfig getPathingConfig() {
		if (animalKey.equals(Mill.ENTITY_SQUID)) {
			return JPS_CONFIG_SLAUGHTERSQUIDS;
		}

		return JPS_CONFIG_TIGHT;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isDestPossibleSpecific(final MillVillager villager, final Building b) {
		final List<Entity> animals = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class) EntityList.stringToClassMapping.get(animalKey), b.getPos(), 15, 10);

		if (animals == null) {
			return false;
		}

		int nbanimals = 0;

		for (final Entity ent : animals) {

			if (!ent.isDead && !isEntityChild(ent)) {
				nbanimals++;
			}

		}

		int targetAnimals = 0;

		for (int i = 0; i < b.getResManager().spawns.size(); i++) {
			if (b.getResManager().spawnTypes.get(i).equals(animalKey)) {
				targetAnimals = b.getResManager().spawns.get(i).size();
			}
		}
		return nbanimals > targetAnimals;
	}

	private boolean isEntityChild(final Entity ent) {
		if (!(ent instanceof EntityAgeable)) {
			return false;
		}

		final EntityAgeable animal = (EntityAgeable) ent;

		return animal.isChild();
	}

	@Override
	public boolean isFightingGoal() {
		return true;
	}

	@Override
	public boolean isPossibleGenericGoal(final MillVillager villager) throws Exception {
		return getDestination(villager) != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		final Building dest = villager.getGoalBuildingDest();

		if (dest == null) {
			return true;
		}

		List<Entity> animals = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class) EntityList.stringToClassMapping.get(animalKey), villager.getPos(), 2, 5);

		for (final Entity ent : animals) {
			if (!ent.isDead) {
				if (!isEntityChild(ent)) {
					if (villager.canEntityBeSeen(ent)) {
						villager.setEntityToAttack(ent);

						for (int i = 0; i < extraItems.size(); i++) {
							if (extraItemsTag.get(i) == null || dest.location.tags.contains(extraItemsTag.get(i))) {
								if (MillCommonUtilities.randomInt(100) < extraItemsChance.get(i)) {
									villager.addToInv(extraItems.get(i), 1);
								}
							}
						}

						villager.swingItem();

						return true;
					}
				}
			}
		}

		animals = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, (Class) EntityList.stringToClassMapping.get(animalKey), villager.getPos(), 2, 5);

		for (final Entity ent : animals) {
			if (!ent.isDead) {
				if (!isEntityChild(ent)) {
					if (villager.canEntityBeSeen(ent)) {
						villager.setEntityToAttack(ent);

						for (int i = 0; i < extraItems.size(); i++) {
							if (extraItemsTag.get(i) == null || dest.location.tags.contains(extraItemsTag.get(i))) {
								if (MillCommonUtilities.randomInt(100) < extraItemsChance.get(i)) {
									villager.addToInv(extraItems.get(i), 1);
								}
							}
						}

						villager.swingItem();

						return true;
					}
				}
			}
		}

		return true;
	}

	@Override
	public int range(final MillVillager villager) {
		return 2;
	}
}
