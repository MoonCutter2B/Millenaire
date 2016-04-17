package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class GoalBreedAnimals extends Goal {

	static private final Item[] CEREALS = new Item[] { Items.wheat, Mill.rice, Mill.maize };

	static private final Item[] SEEDS = new Item[] { Items.wheat_seeds, Mill.rice, Mill.maize };

	static private final Item[] CARROTS = new Item[] { Items.carrot };

	@SuppressWarnings({ "rawtypes" })
	private Item[] getBreedingItems(final Class animalClass) {
		if (animalClass == EntityCow.class || animalClass == EntitySheep.class) {
			return CEREALS;
		}
		if (animalClass == EntityPig.class) {
			return CARROTS;
		}
		if (animalClass == EntityChicken.class) {
			return SEEDS;
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		final List<Class> validAnimals = getValidAnimalClasses(villager);

		for (final Class animalClass : validAnimals) {

			final Item[] breedingItems = getBreedingItems(animalClass);

			boolean available = false;

			if (breedingItems == null) {
				available = true;
			} else {
				for (final Item breedingItem : breedingItems) {
					if (!available && villager.getHouse().countGoods(breedingItem) > 0) {
						available = true;
					}
				}
			}

			if (available) {

				int targetAnimals = 0;

				for (int i = 0; i < villager.getHouse().getResManager().spawns.size(); i++) {
					if (villager.getHouse().getResManager().spawnTypes.get(i).equals(EntityList.classToStringMapping.get(animalClass))) {
						targetAnimals = villager.getHouse().getResManager().spawns.get(i).size();
					}
				}

				final List<Entity> animals = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, animalClass, villager.getHouse().getPos(), 30, 10);

				int nbAdultAnimal = 0, nbAnimal = 0;

				for (final Entity ent : animals) {
					final EntityAnimal animal = (EntityAnimal) ent;

					if (animal.getGrowingAge() == 0) {
						nbAdultAnimal++;
					}

					nbAnimal++;
				}

				if (nbAdultAnimal >= 2 && nbAnimal < targetAnimals * 2) {
					for (final Entity ent : animals) {
						final EntityAnimal animal = (EntityAnimal) ent;

						if (animal.getGrowingAge() == 0 && !animal.isInLove()) {
							return packDest(null, villager.getHouse(), animal);
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) throws Exception {

		if (villager.getGoalDestEntity() == null || !(villager.getGoalDestEntity() instanceof EntityAnimal)) {
			return null;
		}

		final EntityAnimal animal = (EntityAnimal) villager.getGoalDestEntity();

		final Item[] breedingItems = getBreedingItems(animal.getClass());

		if (breedingItems != null) {
			for (final Item breedingItem : breedingItems) {
				if (villager.getHouse().countGoods(breedingItem) > 0) {
					return new ItemStack[] { new ItemStack(breedingItem, 1) };
				}
			}
		}

		return null;
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@SuppressWarnings("rawtypes")
	private List<Class> getValidAnimalClasses(final MillVillager villager) {
		final List<Class> validAnimals = new ArrayList<Class>();

		if (villager.getHouse().location.tags.contains(Building.tagSheeps)) {
			validAnimals.add(EntitySheep.class);
			validAnimals.add(EntityChicken.class);
		}
		if (villager.getHouse().location.tags.contains(Building.tagCattle)) {
			validAnimals.add(EntityCow.class);
		}
		if (villager.getHouse().location.tags.contains(Building.tagPigs)) {
			validAnimals.add(EntityPig.class);
		}
		if (villager.getHouse().location.tags.contains(Building.tagChicken)) {
			validAnimals.add(EntityChicken.class);
		}

		return validAnimals;
	}

	@Override
	public boolean isFightingGoal() {
		return false;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return getDestination(villager) != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		final List<Class> validAnimals = getValidAnimalClasses(villager);

		for (final Class animalClass : validAnimals) {

			final List<Entity> animals = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, animalClass, villager.getPos(), 4, 2);

			for (final Entity ent : animals) {

				if (!ent.isDead) {

					final EntityAnimal animal = (EntityAnimal) ent;

					final Item[] breedingItems = getBreedingItems(animal.getClass());

					boolean available = false;
					Item foundBreedingItem = null;

					if (breedingItems == null) {
						available = true;
					} else {
						for (final Item breedingItem : breedingItems) {
							if (!available && villager.getHouse().countGoods(breedingItem) > 0) {
								available = true;
								foundBreedingItem = breedingItem;
							}
						}
					}

					if (available) {

						if (!animal.isChild() && !animal.isInLove() && animal.getGrowingAge() == 0) {
							ReflectionHelper.setPrivateValue(EntityAnimal.class, animal, 600, 0);
							animal.setTarget(null);

							if (foundBreedingItem != null) {
								villager.getHouse().takeGoods(foundBreedingItem, 1);
							}

							villager.swingItem();

							ServerSender.sendAnimalBreeding(animal);
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 100;
	}

	@Override
	public int range(final MillVillager villager) {
		return 5;
	}
}
