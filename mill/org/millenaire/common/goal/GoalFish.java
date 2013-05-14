package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.core.MillCommonUtilities;


public class GoalFish extends Goal {

	private static ItemStack[] fishingRod=new ItemStack[]{new ItemStack(Item.fishingRod, 1)};

	public GoalFish() {
		buildingLimit.put(new InvItem(Item.fishRaw.itemID,0), 512);
		buildingLimit.put(new InvItem(Item.fishCooked.itemID,0), 512);
	}
	
	
	@Override
	public int actionDuration(MillVillager villager) {

		return 25000;
	}
	
	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {


		final Vector<Building> vb=villager.getTownHall().getBuildingsWithTag(Building.tagFishingSpot);

		Building closest=null;

		for (final Building b : vb) {
			if ((closest==null) || ((villager.getPos().horizontalDistanceToSquared(b.getSleepingPos()))<villager.getPos().horizontalDistanceToSquared(closest.getSleepingPos()))) {
				closest=b;
			}
		}

		if ((closest==null) || (closest.fishingspots.size()==0))
			return null;

		return packDest(closest.fishingspots.get(MillCommonUtilities.randomInt(closest.fishingspots.size())),closest);
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
		return fishingRod;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {

		for (final Building b : villager.getTownHall().getBuildings())
			if (b.fishingspots.size()>0)
				return true;
		return false;
	}


	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		villager.addToInv(Item.fishRaw.itemID, 1);
		
		villager.swingItem();

		return true;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		
		if (villager.getGoalBuildingDest()==null)
			return 20;
		
		return 100-(villager.getGoalBuildingDest().countGoods(Item.fishRaw.itemID));
	}

	@Override
	public boolean stuckAction(MillVillager villager)  throws Exception {
		return performAction(villager);
	}

}
