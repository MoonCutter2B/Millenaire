package org.millenaire.common.goal.generic;

import java.io.BufferedReader;
import java.io.File;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;

public class GoalGenericPlantCrop extends GoalGeneric {

	public String soilName=null,cropType=null;

	public InvItem seedItem=null;

	@Override
	public boolean isPossibleGenericGoal(MillVillager villager)
			throws Exception {
		return getDestination(villager)!=null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public int priority(MillVillager villager) {

		GoalInformation info=getDestination(villager);


		if (info==null || info.getDest()==null)
			return -1;

		return (int) (30-villager.getPos().distanceTo(info.getDest()));
	}

	private boolean isValidPlantingLocation(World world,Point p) {
		if ((p.getAbove().getId(world) == 0 || p.getAbove().getId(world) == Block.snow.blockID || p.getAbove().getId(world) == Block.leaves.blockID)
				&& (p.getId(world)==Block.grass.blockID || p.getId(world)==Block.dirt.blockID || p.getId(world)==Block.tilledField.blockID)) {
			return true;
		}
		return false;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {

		Point dest=null;
		Building destBuilding=null;

		final Vector<Building> buildings=getBuildings(villager);

		for (Building buildingDest : buildings) {

			if (isDestPossible(villager,buildingDest)) {


				Vector<Point> soils=buildingDest.getSoilPoints(soilName);

				if (soils!=null) {
					for (final Point p : soils) {
						if (isValidPlantingLocation(villager.worldObj,p)) {
							if ((dest == null) || (p.distanceTo(villager)<dest.distanceTo(villager))) {
								dest=p;
								destBuilding=buildingDest;
							}
						}
					}
				}
			}
		}
		if (dest==null)
			return null;

		return packDest(dest,destBuilding);
	}

	@Override
	public boolean performAction(MillVillager villager) {

		final Building dest=villager.getGoalBuildingDest();

		if (dest==null)
			return true;

		if (!isValidPlantingLocation(villager.worldObj,villager.getGoalDestPoint()))
			return true;

		if (seedItem!=null) {
			int taken=villager.takeFromInv(seedItem, 1);
			if (taken==0)
				dest.takeGoods(seedItem, 1);
		}

		if (villager.getGoalDestPoint().getId(villager.worldObj)!=Block.tilledField.blockID) {
			villager.setBlockAndMetadata(villager.getGoalDestPoint(), Block.tilledField.blockID, 0);
		}

		villager.setBlockAndMetadata(villager.getGoalDestPoint().getAbove(),getCropBlockId(cropType),getCropBlockMeta(cropType));

		return true;
	}

	public static int getCropBlockId(String cropType) {
		if (cropType.equals(Mill.CROP_WHEAT)) {
			return Block.crops.blockID;
		}
		if (cropType.equals(Mill.CROP_CARROT)) {
			return Block.carrot.blockID;
		}
		if (cropType.equals(Mill.CROP_POTATO)) {
			return Block.potato.blockID;
		}
		if (cropType.equals(Mill.CROP_RICE) || cropType.equals(Mill.CROP_TURMERIC)
				|| cropType.equals(Mill.CROP_MAIZE) || cropType.equals(Mill.CROP_VINE)) {
			return Mill.crops.blockID;
		}
		return 0;
	}

	public static int getCropBlockMeta(String cropType) {
		if (cropType.equals(Mill.CROP_TURMERIC)) {
			return 2;
		}
		if (cropType.equals(Mill.CROP_MAIZE)) {
			return 4;
		}
		if (cropType.equals(Mill.CROP_VINE)) {
			return 6;
		}
		return 0;
	}


	public GoalGenericPlantCrop() {
		super();
		duration=100;
	}

	public static GoalGenericPlantCrop loadGenericPlantCropGoal(File file) {

		final GoalGenericPlantCrop g=new GoalGenericPlantCrop();

		g.key=file.getName().split("\\.")[0].toLowerCase();

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {
					final String[] temp=line.split("=");
					if (temp.length!=2) {
						MLN.error(null, "Invalid line when loading generic plating goal "+file.getName()+": "+line);
					} else {
						final String key=temp[0].trim().toLowerCase();
						String value=temp[1].trim();

						if (!GoalGeneric.readGenericGoalConfigLine(g, key, value, file, line)) {
							if (key.equals("soilname")) {
								g.soilName=value.trim().toLowerCase();
							}  else if (key.equals("croptype")) {
								g.cropType=value.trim().toLowerCase();
							}  else if (key.equals("seed")) {
								value=value.trim().toLowerCase();
								if (Goods.goodsName.containsKey(value)) {
									g.seedItem=Goods.goodsName.get(value);
								} else {
									MLN.error(null, "Unknown seed in generic planting goal "+file.getName()+": "+line);
								}
							} else {
								MLN.error(null, "Unknown line in generic planting goal "+file.getName()+": "+line);
							}
						}
					}
				}
			}

			if (g.soilName==null) {
				MLN.error(null, "The soilname is mandatory in custom planting goals "+file.getName());
				return null;
			}
			if (g.cropType==null) {
				MLN.error(null, "The croptype is mandatory in custom planting goals "+file.getName());
				return null;
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
		if (seedItem!=null && b.countGoods(seedItem)+villager.countInv(seedItem)==0)		
			return false;
		return true;
	}
}
