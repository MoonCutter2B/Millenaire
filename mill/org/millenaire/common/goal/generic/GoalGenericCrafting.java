package org.millenaire.common.goal.generic;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.item.Goods;

public class GoalGenericCrafting extends GoalGeneric {

	@Override
	public boolean swingArms() {
		return true;
	}

	public HashMap<InvItem,Integer> inputs=new HashMap<InvItem,Integer>();
	public HashMap<InvItem,Integer> outputs=new HashMap<InvItem,Integer>();


	@Override
	public int actionDuration(MillVillager villager) throws Exception {
		return duration;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {
		final Vector<Building> buildings=getBuildings(villager);

		for (Building dest : buildings) {

			if (isDestPossible(villager,dest)) {
				return packDest(dest.getCraftingPos(),dest);
			}
		}

		return null;
	}


	@Override
	public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		final Building dest=villager.getGoalBuildingDest();
		
		if (dest==null)
			return true;

		//check again that all the inputs are there
		for (final InvItem item : inputs.keySet()) {
			if ((villager.countInv(item)+dest.countGoods(item))<inputs.get(item))
				return true;
		}

		for (final InvItem item : inputs.keySet()) {
			final int nbTaken=villager.takeFromInv(item, 1024);
			dest.storeGoods(item, nbTaken);

			dest.takeGoods(item, inputs.get(item));
		}

		for (final InvItem item : outputs.keySet()) {
			dest.storeGoods(item, outputs.get(item));
		}

		if (sound!=null) {
			MillCommonUtilities.playSoundByMillName(villager.worldObj,villager.getPos(),sound,10f);
		}

		return true;
	}

	public static GoalGenericCrafting loadGenericCraftingGoal(File file) {

		final GoalGenericCrafting g=new GoalGenericCrafting();

		g.key=file.getName().split("\\.")[0].toLowerCase();

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {
					final String[] temp=line.split("=");
					if (temp.length!=2) {
						MLN.error(null, "Invalid line when loading generic crafting goal "+file.getName()+": "+line);
					} else {
						final String key=temp[0].toLowerCase();
						final String value=temp[1];

						if (!GoalGeneric.readGenericGoalConfigLine(g, key, value, file, line)) {
							if (key.equals("input")) {
								final String[] temp2=value.split(",");

								if (temp2.length!=2) {
									MLN.error(null, "Inputs must take the form of input=goodname,goodquatity in generic crafting goal "+file.getName()+": "+line);
								} else {
									if (Goods.goodsName.containsKey(temp2[0])) {
										g.inputs.put(Goods.goodsName.get(temp2[0]), Integer.parseInt(temp2[1]));
									} else {
										MLN.error(null, "Unknown input item in generic crafting goal "+file.getName()+": "+line);
									}
								}
							}  else if (key.equals("output")) {
								final String[] temp2=value.split(",");

								if (temp2.length!=2) {
									MLN.error(null, "Outputs must take the form of input=goodname,goodquatity in generic crafting goal "+file.getName()+": "+line);
								} else {
									if (Goods.goodsName.containsKey(temp2[0])) {
										g.outputs.put(Goods.goodsName.get(temp2[0]), Integer.parseInt(temp2[1]));
									} else {
										MLN.error(null, "Unknown output item in generic crafting goal "+file.getName()+": "+line);
									}
								}
							} else {
								MLN.error(null, "Unknown line in generic crafting goal "+file.getName()+": "+line);
							}
						}
					}
				}
			}

			reader.close();
		} catch (final Exception e) {
			MLN.printException(e);

			return null;
		}

		return g;

	}

	@Override
	public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
		for (final InvItem item : inputs.keySet()) {
			if ((villager.countInv(item)+b.countGoods(item))<inputs.get(item))
				return false;
		}
		return true;
	}

}
