package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;


public class GoalMinerMineResource extends Goal {

	@Override
	public int actionDuration(MillVillager villager) {

		final int blockId=villager.getBlock(villager.getGoalDestPoint());

		if ((blockId == Block.stone.blockID) || (blockId == Block.sandStone.blockID)) {
			final int toolEfficiency=(int)villager.getBestPickaxe().efficiencyOnProperMaterial;

			return 7000-(200*toolEfficiency);

		} else if ((blockId == Block.sand.blockID) || (blockId == Block.blockClay.blockID) || (blockId == Block.gravel.blockID)) {
			final int toolEfficiency=(int)villager.getBestShovel().efficiencyOnProperMaterial;

			return 7000-(200*toolEfficiency);
		}
		return 0;
	}
	
	@Override
	public boolean swingArms() {
		return true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {

		final Vector<Vector<Point>> sources=villager.getHouse().sources;

		final Vector<Point> validSources=new Vector<Point>();

		for (int i=0;i<sources.size();i++) {
			for (int j=0;j<sources.get(i).size();j++) {
				final int blockId=villager.getBlock(sources.get(i).get(j));

				if ((blockId == Block.stone.blockID) || (blockId == Block.sandStone.blockID) || (blockId == Block.sand.blockID)
						|| (blockId == Block.blockClay.blockID) || (blockId == Block.gravel.blockID)) {
					validSources.add(sources.get(i).get(j));
				}

			}
		}

		if (validSources.isEmpty())
			return null;

		return packDest(validSources.get(MillCommonUtilities.randomInt(validSources.size())),villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {

		if ((villager.getBlock(villager.getGoalDestPoint())==Block.sand.blockID) || (villager.getBlock(villager.getGoalDestPoint())==Block.blockClay.blockID) || (villager.getBlock(villager.getGoalDestPoint())==Block.gravel.blockID))
			return villager.getBestShovelStack();

		return villager.getBestPickaxeStack();
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public int range(MillVillager villager) {
		return 5;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return getDestination(villager)!=null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		final int blockId=villager.getBlock(villager.getGoalDestPoint());

		if (blockId == Block.sand.blockID) {
			villager.addToInv(Block.sand.blockID, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Block.sand,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gathered sand at: "+villager.getGoalDestPoint());
			}
		} else if (blockId==Block.stone.blockID) {
			villager.addToInv(Block.cobblestone.blockID, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Block.stone,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather cobblestone at: "+villager.getGoalDestPoint());
			}
		} else if (blockId==Block.sandStone.blockID) {
			villager.addToInv(Block.sandStone.blockID, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Block.sandStone,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather sand stone at: "+villager.getGoalDestPoint());
			}
		} else if (blockId==Block.blockClay.blockID) {
			villager.addToInv(Item.clay.itemID, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Block.blockClay,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather clay at: "+villager.getGoalDestPoint());
			}
		} else if (blockId==Block.gravel.blockID) {
			villager.addToInv(Block.gravel.blockID, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Block.gravel,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather gravel at: "+villager.getGoalDestPoint());
			}
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 30;
	}

	@Override
	public boolean stuckAction(MillVillager villager)  throws Exception {
		return performAction(villager);
	}
}
