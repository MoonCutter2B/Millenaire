package org.millenaire.common.goal;

import java.util.List;
import java.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;


public class GoalBreedAnimals extends Goal {

	@SuppressWarnings("rawtypes")
	private Vector<Class> getValidAnimalClasses(MillVillager villager) {
		Vector<Class> validAnimals=new Vector<Class>();

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

	@SuppressWarnings("rawtypes")
	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {


		Vector<Class> validAnimals=getValidAnimalClasses(villager);

		for (Class animalClass : validAnimals) {

			int[] breedingItems=getBreedingItems(animalClass);

			boolean available=false;

			if (breedingItems==null) {
				available=true;
			} else {
				for (int breedingItem : breedingItems) {
					if (!available && villager.getHouse().countGoods(breedingItem)>0) {
						available=true;
					}
				}
			}



			if (available) {

				int targetAnimals=0;

				for (int i=0;i<villager.getHouse().spawns.size();i++) {
					if (villager.getHouse().spawnTypes.get(i).equals(EntityList.classToStringMapping.get(animalClass))) {
						targetAnimals=villager.getHouse().spawns.get(i).size();
					}
				}

				final List<Entity> animals=MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, animalClass, villager.getHouse().getPos(), 30, 10);

				int nbAdultAnimal=0,nbAnimal=0;

				for (final Entity ent : animals) {
					EntityAnimal animal=(EntityAnimal)ent;

					if (animal.getGrowingAge() == 0)
						nbAdultAnimal++;

					nbAnimal++;
				}

				if (nbAdultAnimal>=2 && nbAnimal<targetAnimals*2) {
					for (final Entity ent : animals) {
						EntityAnimal animal=(EntityAnimal)ent;

						if (animal.getGrowingAge() == 0 && animal.inLove==0)
							return packDest(null,villager.getHouse(),animal);
					}
				}
			}
		}

		return null;
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public boolean isFightingGoal() {
		return false;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return getDestination(villager)!=null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	static private final int[] CEREALS=new int[]{Item.wheat.itemID,Mill.rice.itemID,Mill.maize.itemID};
	static private final int[] SEEDS=new int[]{Item.seeds.itemID,Mill.rice.itemID,Mill.maize.itemID};
	static private final int[] CARROTS=new int[]{Item.carrot.itemID};


	@SuppressWarnings({ "rawtypes" })
	private int[] getBreedingItems(Class animalClass) {
		if (animalClass==EntityCow.class || animalClass==EntitySheep.class)
			return CEREALS;
		if (animalClass==EntityPig.class)
			return CARROTS;
		if (animalClass==EntityChicken.class)
			return SEEDS;

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		Vector<Class> validAnimals=getValidAnimalClasses(villager);

		for (Class animalClass : validAnimals) {

			final List<Entity> animals=MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, animalClass, villager.getPos(), 4, 2);

			for (final Entity ent : animals) {

				if (!ent.isDead) {

					final EntityAnimal animal=(EntityAnimal)ent;

					int[] breedingItems=getBreedingItems(animal.getClass());

					boolean available=false;
					int foundBreedingItem=-1;

					if (breedingItems==null) {
						available=true;
					} else {
						for (int breedingItem : breedingItems) {
							if (!available && villager.getHouse().countGoods(breedingItem)>0) {
								available=true;
								foundBreedingItem=breedingItem;
							}
						}
					}


					if (available) {

						if (!animal.isChild() && !animal.isInLove() && animal.getGrowingAge() == 0) {

							animal.inLove=600;
							animal.setTarget(null);

							if (foundBreedingItem>0)
								villager.getHouse().takeGoods(foundBreedingItem, 1);

							ServerSender.sendAnimalBreeding(animal);
						}
					}
				}
			}
		}

		return true;
	}



	@SuppressWarnings("rawtypes")
	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager)
	throws Exception {

		Vector<Class> validAnimals=getValidAnimalClasses(villager);

		for (Class animalClass : validAnimals) {

			final List<Entity> animals=MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, animalClass, villager.getGoalDestPoint(), 4, 2);


			for (final Entity ent : animals) {

				if (!ent.isDead) {

					final EntityAnimal animal=(EntityAnimal)ent;

					int[] breedingItems=getBreedingItems(animal.getClass());

					boolean available=false;
					int foundBreedingItem=-1;

					if (breedingItems==null) {
						available=true;
					} else {
						for (int breedingItem : breedingItems) {
							if (!available && villager.getHouse().countGoods(breedingItem)>0) {
								available=true;
								foundBreedingItem=breedingItem;
							}
						}
					}

					if (foundBreedingItem>0) {

						if (!animal.isChild() && !animal.isInLove()) {

							return new ItemStack[]{new ItemStack(Item.itemsList[foundBreedingItem],1)};
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 100;
	}
}
