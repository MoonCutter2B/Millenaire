package org.millenaire.common.goal;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.network.ServerSender;



public class GoalBeSeller extends Goal {

	public static final int sellingRadius = 7;



	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return packDest(villager.getTownHall().sellingPlace);
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		return false; //only triggered by TownHall
	}

	@Override
	public boolean isStillValidSpecific(MillVillager villager) throws Exception {

		if (villager.getTownHall().sellingPlace == null)
			return false;

		final EntityPlayer player=villager.worldObj.getClosestPlayer(villager.getTownHall().sellingPlace.getiX(),villager.getTownHall().sellingPlace.getiY(),villager.getTownHall().sellingPlace.getiZ(),sellingRadius);

		final boolean valid=((player != null) && (villager.getTownHall().sellingPlace.distanceTo(player) < sellingRadius));

		if (!valid && (MLN.LogWifeAI>=MLN.MAJOR)) {
			MLN.major(this, "Selling goal no longer valid.");
		}

		return valid;
	}


	@Override
	public boolean lookAtPlayer() {
		return true;
	}

	@Override
	public void onAccept(MillVillager villager) {
		final EntityPlayer player=villager.worldObj.getClosestPlayer(villager.getTownHall().sellingPlace.getiX(),villager.getTownHall().sellingPlace.getiY(),villager.getTownHall().sellingPlace.getiZ(),sellingRadius);
		ServerSender.sendTranslatedSentence(player,MLN.WHITE, "ui.sellercoming",villager.getName());
	}

	@Override
	public void onComplete(MillVillager villager) {

		final EntityPlayer player=villager.worldObj.getClosestPlayer(villager.getTownHall().getSellingPos().getiX(),
				villager.getTownHall().getSellingPos().getiY(),
				villager.getTownHall().getSellingPos().getiZ(),sellingRadius+10);

		ServerSender.sendTranslatedSentence(player,MLN.WHITE, "ui.tradecomplete",villager.getName());
		villager.getTownHall().seller=null;
		villager.getTownHall().sellingPlace=null;
	}

	@Override
	public boolean performAction(MillVillager villager) {
		if (villager.getTownHall().sellingPlace == null) {
			MLN.error(this, "villager.townHall.sellingPlace is null.");
			return true;
		}
		return false;
	}

	@Override
	public int priority(MillVillager villager) {
		return 0;
	}

	@Override
	public int range(MillVillager villager) {
		return 2;
	}
}
