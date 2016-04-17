package org.millenaire.common.goal;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;

public class GoalHuntMonster extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {
		final List<Entity> mobs = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, EntityMob.class, villager.getTownHall().getPos(), 50, 10);

		if (mobs == null) {
			return null;
		}

		int bestDist = Integer.MAX_VALUE;
		Entity target = null;

		for (final Entity ent : mobs) {

			if (ent instanceof EntityMob) {
				if (villager.getPos().distanceToSquared(ent) < bestDist && villager.getTownHall().getAltitude((int) ent.posX, (int) ent.posZ) < ent.posY) {
					target = ent;
					bestDist = (int) villager.getPos().distanceToSquared(ent);
				}
			}
		}

		if (target == null) {
			return null;
		}

		return packDest(null, null, target);
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {
		return new ItemStack[] { villager.getWeapon() };
	}

	@Override
	public boolean isFightingGoal() {
		return true;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return getDestination(villager) != null;
	}

	@Override
	public boolean isStillValidSpecific(final MillVillager villager) throws Exception {

		if (villager.worldObj.getWorldTime() % 10 == 0) {
			setVillagerDest(villager);
		}

		return villager.getGoalDestPoint() != null;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {
		final List<Entity> mobs = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, EntityMob.class, villager.getPos(), 4, 4);

		for (final Entity ent : mobs) {
			if (!ent.isDead && ent instanceof EntityMob && villager.canEntityBeSeen(ent)) {
				villager.setEntityToAttack(ent);
				if (MLN.LogGeneralAI >= MLN.MAJOR) {
					MLN.major(this, "Attacking entity: " + ent);
				}
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 50;
	}

}
