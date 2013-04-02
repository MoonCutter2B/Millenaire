package org.millenaire.common.goal;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.network.ServerSender;



public class GoalBePujaPerformer extends Goal {

	public static final int sellingRadius = 7;

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		final Building temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);

		if ((temple!=null) && ((temple.pujas!=null) && ((temple.pujas.priest==null) || (temple.pujas.priest==villager)))) {

			if (MLN.LogPujas>=MLN.DEBUG) {
				MLN.debug(villager, "Destination for bepujaperformer: "+temple);
			}

			return packDest(temple.getCraftingPos(),temple);
		}

		return null;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		if (!villager.mw.isGlobalTagSet(MillWorld.PUJAS))
			return false;

		final Building temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);

		if (temple==null)
			return false;

		final EntityPlayer player=villager.worldObj.getClosestPlayer(temple.getCraftingPos().getiX(),temple.getCraftingPos().getiY(),
				temple.getCraftingPos().getiZ(),sellingRadius);

		final boolean valid=((player != null) && (temple.getCraftingPos().distanceTo(player) < sellingRadius));

		if (!valid)
			return false;

		return getDestination(villager)!=null;
	}

	@Override
	public boolean isStillValidSpecific(MillVillager villager) throws Exception {

		final Building temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);

		if (temple==null)
			return false;

		final EntityPlayer player=villager.worldObj.getClosestPlayer(temple.getCraftingPos().getiX(),temple.getCraftingPos().getiY(),
				temple.getCraftingPos().getiZ(),sellingRadius);

		final boolean valid=((player != null) && (temple.getCraftingPos().distanceTo(player) < sellingRadius));

		if (!valid && (MLN.LogPujas>=MLN.MAJOR)) {
			MLN.major(this, "Be Puja Performer no longer valid.");
		}

		//if he can pray, time to work instead of waiting
		return valid && !temple.pujas.canPray();
	}


	@Override
	public boolean lookAtPlayer() {
		return true;
	}

	@Override
	public void onAccept(MillVillager villager) {

		final Building temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);

		if (temple==null)
			return;

		final EntityPlayer player=villager.worldObj.getClosestPlayer(temple.getCraftingPos().getiX(),temple.getCraftingPos().getiY(),
				temple.getCraftingPos().getiZ(),sellingRadius);

		ServerSender.sendTranslatedSentence(player,MLN.WHITE,"pujas.priestcoming",villager.getName());
	}

	@Override
	public boolean performAction(MillVillager villager) {

		final Building temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);

		temple.pujas.priest=villager;

		//if he can pray, time to work instead of waiting
		return temple.pujas.canPray();
	}

	@Override
	public int priority(MillVillager villager) {
		return 300;
	}

	@Override
	public int range(MillVillager villager) {
		return 2;
	}
}
