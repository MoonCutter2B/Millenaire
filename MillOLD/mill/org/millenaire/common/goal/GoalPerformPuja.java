package org.millenaire.common.goal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.building.Building;

public class GoalPerformPuja extends Goal {

	@Override
	public int actionDuration(final MillVillager villager) throws Exception {
		return 100;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		Building temple = null;

		if (villager.canMeditate()) {
			temple = villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);
		} else if (villager.canPerformSacrifices()) {
			temple = villager.getTownHall().getFirstBuildingWithTag(Building.tagSacrifices);
		}

		if (temple != null && temple.pujas != null && (temple.pujas.priest == null || temple.pujas.priest == villager) && temple.pujas.canPray()) {
			return packDest(temple.getResManager().getCraftingPos(), temple);
		}

		return null;
	}

	@Override
	public ItemStack[] getHeldItemsDestination(final MillVillager villager) {

		Building temple = null;

		if (villager.canMeditate()) {
			temple = villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);
		} else if (villager.canPerformSacrifices()) {
			temple = villager.getTownHall().getFirstBuildingWithTag(Building.tagSacrifices);
		}

		if (temple.pujas.getStackInSlot(0) != null) {
			return new ItemStack[] { temple.pujas.getStackInSlot(0) };
		}

		return null;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {

		if (villager.canMeditate()) {
			if (!villager.mw.isGlobalTagSet(MillWorld.PUJAS)) {
				return false;
			}
		} else if (villager.canPerformSacrifices()) {
			if (!villager.mw.isGlobalTagSet(MillWorld.MAYANSACRIFICES)) {
				return false;
			}
		}

		return getDestination(villager) != null;
	}

	@Override
	public String labelKey(final MillVillager villager) {
		if (villager != null && villager.canPerformSacrifices()) {
			return "performsacrifices";
		}

		return key;
	}

	@Override
	public String labelKeyWhileTravelling(final MillVillager villager) {
		if (villager != null && villager.canPerformSacrifices()) {
			return "performsacrifices";
		}

		return key;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		Building temple = null;

		if (villager.canMeditate()) {
			temple = villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);
		} else if (villager.canPerformSacrifices()) {
			temple = villager.getTownHall().getFirstBuildingWithTag(Building.tagSacrifices);
		}

		final boolean canContinue = temple.pujas.performPuja(villager);

		final EntityPlayer player = villager.worldObj.getClosestPlayerToEntity(villager, 16);

		if (player != null) {
			temple.sendBuildingPacket(player, false);
		}

		if (!canContinue) {
			return true;
		}

		return false;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 500;
	}

	@Override
	public boolean swingArms() {
		return true;
	}

}
