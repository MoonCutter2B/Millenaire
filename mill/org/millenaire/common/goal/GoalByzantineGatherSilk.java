package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;



public class GoalByzantineGatherSilk extends Goal {

	private static ItemStack[] shears={new ItemStack(Items.shears,1)};
	
	public GoalByzantineGatherSilk() {
		maxSimultaneousInBuilding=2;
		buildingLimit.put(new InvItem(Mill.silk), 128);
		townhallLimit.put(new InvItem(Mill.silk), 128);
	}

	@Override
	public int actionDuration(MillVillager villager) {
		return 1000;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {

		final Vector<Point> vp=new Vector<Point>();
		final Vector<Point> buildingp=new Vector<Point>();
		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagSilkwormFarm)) {
			final Point p=kiln.getSilkwormHarvestLocation();
			if (p!=null) {
				vp.add(p);
				buildingp.add(kiln.getPos());
			}
		}
		
		

		if (vp.isEmpty())
			return null;

		Point p=vp.firstElement();
		Point buildingP=buildingp.firstElement();

		for (int i=1;i<vp.size();i++) {
			if (vp.get(i).horizontalDistanceToSquared(villager) < p.horizontalDistanceToSquared(villager)) {
				p=vp.get(i);
				buildingP=buildingp.get(i);
			}
		}
		return packDest(p,buildingP);
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
		return shears;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		

		boolean delayOver;
		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver=true;
		} else {
			delayOver=(villager.worldObj.getWorldTime()>(villager.lastGoalTime.get(this)+STANDARD_DELAY));
		}

		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagSilkwormFarm)) {
			final int nb=kiln.getNbSilkWormHarvestLocation();

			if ((nb>0) && delayOver)
				return true;
			if (nb>4)
				return true;
		}
		return false;
	}



	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) {
		if ((MillCommonUtilities.getBlock(villager.worldObj, villager.getGoalDestPoint())==Mill.wood_decoration) && (MillCommonUtilities.getBlockMeta(villager.worldObj, villager.getGoalDestPoint())==4)) {
			villager.addToInv(Mill.silk,0, 1);
			villager.setBlockAndMetadata(villager.getGoalDestPoint(),Mill.wood_decoration,3);
			
			villager.swingItem();
			
			return false;
		} else
			return true;

	}

	@Override
	public int priority(MillVillager villager) {

		int p=100-(villager.getTownHall().nbGoodAvailable(new InvItem(Mill.stone_decoration, 1), false, false)*2);

		for (final MillVillager v : villager.getTownHall().villagers) {
			if (this.key.equals(v.goalKey)) {
				p=p/2;
			}
		}

		return p;
	}

	@Override
	public boolean unreachableDestination(MillVillager villager) {
		return false;
	}

}
