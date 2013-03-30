package org.millenaire.common.goal.leasure;

import org.millenaire.common.Culture.CultureLanguage.Dialogue;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;

public class GoalGoChat extends Goal {
	
	private final char[] chatColours=new char[]{MLN.WHITE,MLN.LIGHTBLUE,MLN.LIGHTGREEN,MLN.LIGHTGREY,MLN.LIGHTRED};

	public GoalGoChat() {
		leasure=true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {

		for (MillVillager v : villager.getTownHall().villagers) {
			if (v!=villager && Goal.gosocialise.key.equals(v.goalKey) && v.getPos().distanceToSquared(villager)<25) {
				return packDest(null,null,v);
			}
		}

		return null;
	}

	@Override
	public int actionDuration(MillVillager villager) {
		return 1000;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {		
		return (villager.dialogueKey==null);
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 10;
	}

	@Override
	public void onAccept(MillVillager villager) throws Exception {

		GoalInformation info=getDestination(villager);

		if (info!=null) {

			MillVillager target=(MillVillager)info.getTargetEnt();

			target.clearGoal();
			target.goalKey=key;
			target.setGoalDestEntity(villager);



			Dialogue dialog=villager.getCulture().getDialog(villager, target);

			if (dialog!=null) {

				int role=dialog.validRoleFor(villager);

				villager.setGoalInformation(null);
				villager.setGoalDestEntity(target);
				
				char col=chatColours[MillCommonUtilities.randomInt(chatColours.length)];

				if (dialog!=null) {
					villager.dialogueKey=dialog.key;
					villager.dialogueRole=role;
					villager.dialogueStart=villager.worldObj.getWorldTime();
					villager.dialogueColour=col;

					target.dialogueKey=dialog.key;
					target.dialogueRole=3-role;
					target.dialogueStart=villager.worldObj.getWorldTime();
					target.dialogueColour=col;
				}
			}
		}

		super.onAccept(villager);
	}

	@Override
	protected boolean isPossibleSpecific(MillVillager villager)
			throws Exception {
		return (getDestination(villager)!=null);
	}

	@Override
	public int range(MillVillager villager) {
		return 2;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}


}
