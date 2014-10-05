package org.millenaire.common.building;

import io.netty.buffer.ByteBufInputStream;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.VillageType;
import org.millenaire.common.building.BuildingPlan.LocationBuildingPair;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.network.StreamReadWrite;

public class BuildingPlanSet {

	public File dir;
	public String key;
	public int max;
	public List<BuildingPlan[]> plans = new ArrayList<BuildingPlan[]>();
	public Culture culture;

	public BuildingPlanSet(final Culture c, final String key, final File dir) {
		culture = c;
		this.key = key;
		this.dir = dir;
	};

	public List<LocationBuildingPair> buildLocation(final MillWorld mw,
			final VillageType villageType, final BuildingLocation location,
			final boolean worldGeneration, final boolean townHall,
			final Point townHallPos, final boolean wandimport,
			final EntityPlayer owner) {
		return plans.get(location.getVariation())[location.level].build(mw,
				villageType, location, worldGeneration, townHall, townHallPos,
				wandimport, owner, false);
	}

	public BuildingProject getBuildingProject() {
		return new BuildingProject(this);
	}

	public PointType[][][] getConsolidatedPlan(final int variation,
			final int level) {

		final int minLevel = getMinLevel(variation, level);
		final int maxLevel = getMaxLevel(variation, level);

		final int length = plans.get(variation)[0].plan[0].length;
		final int width = plans.get(variation)[0].plan[0][0].length;

		final PointType[][][] consolidatedPlan = new PointType[maxLevel
				- minLevel][length][width];

		for (int lid = 0; lid <= level; lid++) {
			final BuildingPlan plan = plans.get(variation)[lid];

			if (MLN.LogBuildingPlan >= MLN.MAJOR) {
				MLN.major(this, "Consolidating plan: adding level " + lid);
			}

			final int ioffset = plan.firstLevel - minLevel;

			for (int i = 0; i < plan.plan.length; i++) {
				for (int j = 0; j < length; j++) {
					for (int k = 0; k < width; k++) {
						final PointType pt = plan.plan[i][j][k];

						if (!pt.isType(BuildingPlan.bempty) || lid == 0) {// all
																			// except
																			// white
																			// pixels
																			// in
																			// upgrades
							consolidatedPlan[i + ioffset][j][k] = pt;
						}
					}
				}
			}
		}
		return consolidatedPlan;
	}

	public String getFullName(final EntityPlayer player) {
		final BuildingPlan plan = getRandomStartingPlan();

		return plan.getFullDisplayName();
	}

	public String getGameName() {
		final BuildingPlan plan = getRandomStartingPlan();

		return plan.getGameName();
	}

	public int getMaxLevel(final int variation, final int level) {

		int maxLevel = Integer.MIN_VALUE;

		for (int i = 0; i <= level; i++) {
			final BuildingPlan plan = plans.get(variation)[i];
			if (plan.plan.length + plan.firstLevel > maxLevel) {
				maxLevel = plan.plan.length + plan.firstLevel;
			}
		}

		return maxLevel;
	}

	public int getMinLevel(final int variation, final int level) {

		int minLevel = Integer.MAX_VALUE;

		for (int i = 0; i <= level; i++) {
			final BuildingPlan plan = plans.get(variation)[i];
			if (plan.firstLevel < minLevel) {
				minLevel = plan.firstLevel;
			}
		}

		return minLevel;
	}

	public String getNativeName() {
		if (plans.size() == 0) {
			return key;
		}
		final BuildingPlan plan = getRandomStartingPlan();
		return plan.nativeName;
	}

	public BuildingPlan getRandomStartingPlan() {
		if (plans.size() == 0) {
			return null;
		}
		return plans.get(MillCommonUtilities.randomInt(plans.size()))[0];
	}

	public void loadPictPlans(final boolean importPlan) throws Exception {

		final List<List<BuildingPlan>> vplans = new ArrayList<List<BuildingPlan>>();

		BuildingPlan prevPlan = null;

		char varChar = 'A';
		int variation = 0;

		while (new File(dir, key + "_" + varChar + ".txt").exists()) {
			vplans.add(new ArrayList<BuildingPlan>());
			int level = 0;
			prevPlan = null;

			final BufferedReader reader = MillCommonUtilities
					.getReader(new File(dir, key + "_" + varChar + ".txt"));

			while (new File(dir, key + "_" + varChar + level + ".png").exists()) {
				final String line = reader.readLine();
				prevPlan = new BuildingPlan(dir, key, level, variation,
						prevPlan, line, culture, importPlan);
				vplans.get(variation).add(prevPlan);
				level++;
			}
			if (vplans.get(variation).size() == 0) {
				throw new MillenaireException("No file found for building "
						+ key + varChar);
			}
			varChar++;
			variation++;
		}

		max = vplans.get(0).get(0).max;

		for (final List<BuildingPlan> varPlans : vplans) {
			final int length = varPlans.get(0).length;
			final int width = varPlans.get(0).width;

			for (final BuildingPlan plan : varPlans) {
				if (plan.width != width) {
					throw new MillenaireException("Width of upgrade "
							+ plan.level + " of building " + plan.buildingKey
							+ " is " + plan.width + " instead of " + width);
				}

				if (plan.length != length) {
					throw new MillenaireException("Length of upgrade "
							+ plan.level + " of building " + plan.buildingKey
							+ " is " + plan.length + " instead of " + length);
				}
			}

			final BuildingPlan[] varplansarray = new BuildingPlan[varPlans
					.size()];

			for (int i = 0; i < varPlans.size(); i++) {
				varplansarray[i] = varPlans.get(i);
			}

			plans.add(varplansarray);
		}
	}

	public void loadPlans(final Culture culture, final boolean importPlan)
			throws Exception {

		final List<List<BuildingPlan>> vplans = new ArrayList<List<BuildingPlan>>();

		BuildingPlan prevPlan = null;

		char varChar = 'A';
		int variation = 0;

		while (new File(dir, key + "_" + varChar + "0.txt").exists()) {
			vplans.add(new ArrayList<BuildingPlan>());
			int level = 0;
			prevPlan = null;
			while (new File(dir, key + "_" + varChar + level + ".txt").exists()) {
				prevPlan = new BuildingPlan(dir, key, level, variation,
						prevPlan, culture, importPlan);
				vplans.get(variation).add(prevPlan);
				level++;

			}
			if (vplans.get(variation).size() == 0) {
				throw new MillenaireException("No file found for building "
						+ key + varChar);
			}
			varChar++;
			variation++;
		}

		max = vplans.get(0).get(0).max;

		for (final List<BuildingPlan> varPlans : vplans) {
			final int length = varPlans.get(0).length;
			final int width = varPlans.get(0).width;

			for (final BuildingPlan plan : varPlans) {
				if (plan.width != width) {
					throw new MillenaireException("Width of upgrade "
							+ plan.level + " of building " + plan.buildingKey
							+ " is " + plan.width + " instead of " + width);
				}

				if (plan.length != length) {
					throw new MillenaireException("Length of upgrade "
							+ plan.level + " of building " + plan.buildingKey
							+ " is " + plan.length + " instead of " + length);
				}
			}

			final BuildingPlan[] varplansarray = new BuildingPlan[varPlans
					.size()];

			for (int i = 0; i < varPlans.size(); i++) {
				varplansarray[i] = varPlans.get(i);
			}

			plans.add(varplansarray);
		}
	}

	public void readBuildingPlanSetInfoPacket(final ByteBufInputStream ds)
			throws IOException {

		final int nb = ds.readInt();

		this.plans.clear();

		for (int i = 0; i < nb; i++) {
			final int nb2 = ds.readInt();

			final BuildingPlan[] plans = new BuildingPlan[nb2];

			for (int j = 0; j < nb2; j++) {
				plans[j] = StreamReadWrite.readBuildingPlanInfo(ds, culture);
			}

			this.plans.add(plans);
		}
	}

	@Override
	public String toString() {
		return key + " (" + plans.size() + " / " + max + "/"
				+ plans.get(0)[0].nativeName + ")";
	}

	public void writeBuildingPlanSetInfo(final DataOutput data)
			throws IOException {
		data.writeUTF(key);
		data.writeInt(plans.size());

		for (final BuildingPlan[] plans : this.plans) {
			data.writeInt(plans.length);
			for (final BuildingPlan plan : plans) {
				StreamReadWrite.writeBuildingPlanInfo(plan, data);
			}
		}
	}

}