package org.millenaire.common.goal.leasure;

import org.millenaire.common.Culture.CultureLanguage.Dialog;
import org.millenaire.common.MillVillager;
import org.millenaire.common.goal.Goal;

public class GoalGoChat extends Goal {
	
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
		return (villager.dialogKey==null);
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
			
			target.goalKey=key;
			target.setGoalInformation(packDest(null,null,villager));
			
			int role=1;
			Dialog dialog=villager.getCulture().getDialog(villager, target);
			
			if (dialog==null) {
				dialog=villager.getCulture().getDialog(villager, target);
				role=2;
			}
			
			villager.setGoalInformation(packDest(null,null,target));
			
			if (dialog!=null) {
				villager.dialogKey=dialog.key;
				villager.dialogRole=role;
				villager.dialogStart=villager.worldObj.getWorldTime();
				
				target.dialogKey=dialog.key;
				target.dialogRole=3-role;
				target.dialogStart=villager.worldObj.getWorldTime();
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
		return 1;
	}
	
	@Override
	public boolean lookAtGoal() {
		return true;
	}
	
	
}
