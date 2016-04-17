package org.millenaire.common.goal.leasure;

import java.util.List;

import net.minecraft.entity.Entity;

import org.millenaire.common.Culture.CultureLanguage.Dialogue;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;

public class GoalGoChat extends Goal {

	private final char[] chatColours = new char[] { MLN.WHITE, MLN.LIGHTBLUE, MLN.LIGHTGREEN, MLN.LIGHTGREY, MLN.LIGHTRED };

	public GoalGoChat() {
		leasure = true;
	}

	@Override
	public int actionDuration(final MillVillager villager) {
		return 1000;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		for (final MillVillager v : villager.getTownHall().villagers) {
			if (v != villager && Goal.gosocialise.key.equals(v.goalKey) && v.getPos().distanceToSquared(villager) < 25) {
				return packDest(null, null, v);
			}
		}

		return null;
	}

	@Override
	protected boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return getDestination(villager) != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public void onAccept(final MillVillager villager) throws Exception {

		final GoalInformation info = getDestination(villager);

		if (info != null) {

			final MillVillager target = (MillVillager) info.getTargetEnt();

			target.clearGoal();
			target.goalKey = key;
			target.setGoalDestEntity(villager);

			final Dialogue dialog = villager.getCulture().getDialog(villager, target);

			if (dialog != null) {

				final int role = dialog.validRoleFor(villager);

				villager.setGoalInformation(null);
				villager.setGoalDestEntity(target);

				char col = chatColours[MillCommonUtilities.randomInt(chatColours.length)];

				col = MLN.WHITE;

				if (dialog != null) {

					final List<Entity> entities = MillCommonUtilities.getEntitiesWithinAABB(villager.worldObj, MillVillager.class, villager.getPos(), 5, 5);

					boolean dialogueChat = true;

					// Only one dialogue can have subtitles at a time
					for (final Entity ent : entities) {
						if (ent != villager && ent != target) {
							final MillVillager v = (MillVillager) ent;

							if (key.equals(v.goalKey) && v.dialogueChat) {
								dialogueChat = false;
							}
						}
					}

					villager.dialogueKey = dialog.key;
					villager.dialogueRole = role;
					villager.dialogueStart = villager.worldObj.getWorldTime();
					villager.dialogueColour = col;
					villager.dialogueChat = dialogueChat;

					target.dialogueKey = dialog.key;
					target.dialogueRole = 3 - role;
					target.dialogueStart = villager.worldObj.getWorldTime();
					target.dialogueColour = col;
					target.dialogueChat = dialogueChat;
				}
			}
		}

		super.onAccept(villager);
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {
		return villager.dialogueKey == null;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 10;
	}

	@Override
	public int range(final MillVillager villager) {
		return 2;
	}

}
