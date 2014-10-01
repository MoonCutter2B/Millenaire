package org.millenaire.common.goal;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalShearSheep extends Goal {

	public GoalShearSheep() {
		buildingLimit.put(new InvItem(Blocks.wool, 0), 1024);
		townhallLimit.put(new InvItem(Blocks.wool, 0), 1024);
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager)
			throws Exception {

		final Point pos = villager.getPos();
		Entity closestSheep = null;
		double sheepBestDist = Double.MAX_VALUE;

		final List<Entity> sheep = MillCommonUtilities.getEntitiesWithinAABB(
				villager.worldObj, EntitySheep.class, villager.getHouse()
						.getPos(), 30, 10);

		for (final Entity ent : sheep) {
			if (!((EntitySheep) ent).getSheared()
					&& !((EntitySheep) ent).isChild()) {
				if (closestSheep == null || pos.distanceTo(ent) < sheepBestDist) {
					closestSheep = ent;
					sheepBestDist = pos.distanceTo(ent);
				}
			}
		}

		if (closestSheep != null) {
			return packDest(null, villager.getHouse(), closestSheep);
		}

		return null;
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public boolean isFightingGoal() {
		return true;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager)
			throws Exception {

		if (!villager.getHouse().location.tags.contains(Building.tagSheeps)) {
			return false;
		}

		final List<Entity> sheep = MillCommonUtilities.getEntitiesWithinAABB(
				villager.worldObj, EntitySheep.class, villager.getHouse()
						.getPos(), 30, 10);

		if (sheep == null) {
			return false;
		}

		for (final Entity ent : sheep) {

			final EntitySheep asheep = (EntitySheep) ent;

			if (!asheep.isChild() && !asheep.isDead) {

				if (((EntitySheep) ent).getSheared() == false) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		final List<Entity> sheep = MillCommonUtilities.getEntitiesWithinAABB(
				villager.worldObj, EntitySheep.class, villager.getPos(), 4, 4);

		for (final Entity ent : sheep) {
			if (!ent.isDead) {

				final EntitySheep animal = (EntitySheep) ent;

				if (!animal.isChild()) {
					if (!animal.getSheared()) {
						villager.addToInv(Blocks.wool,
								((EntitySheep) ent).getFleeceColor(), 3);
						((EntitySheep) ent).setSheared(true);
						if (MLN.LogCattleFarmer >= MLN.MAJOR
								&& villager.extraLog) {
							MLN.major(this, "Shearing: " + ent);
						}

						villager.swingItem();
					}
				}
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 50;
	}

	@Override
	public int range(final MillVillager villager) {
		return 5;
	}
}
