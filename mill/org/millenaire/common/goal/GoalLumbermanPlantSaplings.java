package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;



public class GoalLumbermanPlantSaplings extends Goal {

	@Override
	public int actionDuration(MillVillager villager) {
		return 1000;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {

		final Vector<Point> vp=new Vector<Point>();
		final Vector<Point> buildingp=new Vector<Point>();
		for (final Building grove : villager.getTownHall().getBuildingsWithTag(Building.tagGrove)) {
			final Point p=grove.getPlantingLocation();
			if (p!=null) {
				vp.add(p);
				buildingp.add(grove.getPos());
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

		int meta=0;
		if (villager.takeFromInv(Block.sapling.blockID,0, 1)==1) {
			meta=0;
		} else if (villager.takeFromInv(Block.sapling.blockID,1, 1)==1) {
			meta=1;
		} else if (villager.takeFromInv(Block.sapling.blockID,2, 1)==1) {
			meta=2;
		} else if (villager.takeFromInv(Block.sapling.blockID,3, 1)==1) {
			meta=3;
		}

		return new ItemStack[]{new ItemStack(Block.sapling.blockID,1,meta)};
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		for (final Building grove : villager.getTownHall().getBuildingsWithTag(Building.tagGrove)) {
			final Point p=grove.getPlantingLocation();
			if (p!=null)
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

		final int bid=MillCommonUtilities.getBlock(villager.worldObj, villager.getGoalDestPoint());
		if ((bid==0) || (bid==Block.snow.blockID)) {

			final int metaStart=MillCommonUtilities.randomInt(4);
			int chosenMeta=-1;

			for (int i=metaStart;(i<4) && (chosenMeta==-1);i++) {
				if (villager.takeFromInv(Block.sapling.blockID,i, 1)==1) {
					chosenMeta=i;
				}
			}

			for (int i=0;(i<metaStart) && (chosenMeta==-1);i++) {
				if (villager.takeFromInv(Block.sapling.blockID,i, 1)==1) {
					chosenMeta=i;
				}
			}

			if (chosenMeta==-1) {
				chosenMeta=0;
			}

			villager.setBlockAndMetadata(villager.getGoalDestPoint(),Block.sapling.blockID,chosenMeta);

			if ((MLN.LogLumberman>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Planted at: "+villager.getGoalDestPoint());
			}
		} else if ((MLN.LogLumberman>=MLN.DEBUG) && villager.extraLog) {
			MLN.debug(this, "Failed to plant at: "+villager.getGoalDestPoint());
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return 120;
	}

}
