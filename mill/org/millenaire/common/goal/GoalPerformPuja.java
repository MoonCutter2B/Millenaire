package org.millenaire.common.goal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;


public class GoalPerformPuja extends Goal {

	@Override
	public String labelKey(MillVillager villager) {
		if (villager!=null && villager.canPerformSacrifices())
			return "performsacrifices";
		
		return key;
	}
	
	@Override
	public String labelKeyWhileTravelling(MillVillager villager) {
		if (villager!=null && villager.canPerformSacrifices())
			return "performsacrifices";
		
		return key;
	}
	
	@Override
	public int actionDuration(MillVillager villager) throws Exception {
		return 100;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {

		Building temple=null;

		if (villager.canMeditate())
			temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);
		else if (villager.canPerformSacrifices())
			temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagSacrifices);

		if ((temple!=null) && ((temple.pujas!=null) && ((temple.pujas.priest==null) || (temple.pujas.priest==villager)) && temple.pujas.canPray()))
			return packDest(temple.getCraftingPos(),temple);

		return null;
	}

	@Override
	public ItemStack[] getHeldItemsDestination(MillVillager villager) {

		Building temple=null;

		if (villager.canMeditate())
			temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);
		else if (villager.canPerformSacrifices())
			temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagSacrifices);

		if (temple.pujas.getStackInSlot(0)!=null)
			return new ItemStack[]{temple.pujas.getStackInSlot(0)};

		return null;
	}
	
	@Override
	public boolean swingArms() {
		return true;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {

		if (villager.canMeditate()) {
			if (!villager.mw.isGlobalTagSet(MillWorld.PUJAS))
				return false;
		} else if (villager.canPerformSacrifices()) {
			if (!villager.mw.isGlobalTagSet(MillWorld.MAYANSACRIFICES))
				return false;
		}

		return (getDestination(villager)!=null);
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		Building temple=null;

		if (villager.canMeditate())
			temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);
		else if (villager.canPerformSacrifices())
			temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagSacrifices);

		final boolean canContinue=temple.pujas.performPuja(villager);
		
		EntityPlayer player=villager.worldObj.getClosestPlayerToEntity(villager, 16);
		
		if (player!=null) {
			temple.sendBuildingPacket(player, false);
		}
			

		if (!canContinue)
			return true;

		return false;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 500;
	}

}
