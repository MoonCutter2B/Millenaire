package org.millenaire.common.construction;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.BuildingLocation;
import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.VillageType;
import org.millenaire.common.construction.BuildingPlan.LocationBuildingPair;
import org.millenaire.common.construction.BuildingPlan.PointType;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.network.StreamReadWrite;


public  class BuildingPlanSet {

	public File dir;
	public String key;
	public int max;
	public Vector<BuildingPlan[]> plans=new Vector<BuildingPlan[]>();
	public Culture culture;

	public BuildingPlanSet(Culture c,String key,File dir) {
		culture=c;
		this.key=key;
		this.dir=dir;
	};

	public Vector<LocationBuildingPair> buildLocation(MillWorld mw, VillageType villageType,BuildingLocation location,boolean worldGeneration,boolean townHall,Point townHallPos, boolean wandimport,EntityPlayer owner) {
		return plans.get(location.getVariation())[location.level].build(mw, villageType, location, worldGeneration, townHall, townHallPos, wandimport, owner, false);
	}

	public BuildingProject getBuildingProject() {
		return new BuildingProject(this);
	}

	public PointType[][][] getConsolidatedPlan(int variation,int level) {

		final int minLevel=getMinLevel(variation,level);
		final int maxLevel=getMaxLevel(variation,level);

		final int length=plans.get(variation)[0].plan[0].length;
		final int width=plans.get(variation)[0].plan[0][0].length;

		final PointType[][][] consolidatedPlan=new PointType[maxLevel-minLevel][length][width];


		for (int lid=0;lid<=level;lid++) {
			final BuildingPlan plan=plans.get(variation)[lid];

			if (MLN.LogBuildingPlan>=MLN.MAJOR) {
				MLN.major(this, "Consolidating plan: adding level "+lid);
			}

			final int ioffset=plan.firstLevel-minLevel;

			for (int i = 0;i<plan.plan.length;i++) {
				for (int j =  0;j<length;j++) {
					for (int k = 0;k<width;k++) {
						final PointType pt=plan.plan[i][j][k];

						if (!pt.isType(BuildingPlan.bempty) || (lid==0)) {//all except white pixels in upgrades
							consolidatedPlan[i+ioffset][j][k]=pt;
						}
					}
				}
			}
		}
		return consolidatedPlan;
	}

	public String getFullName(EntityPlayer player) {
		final BuildingPlan plan=getRandomStartingPlan();

		return plan.getNativeDisplayName(player);
	}

	public String getGameName() {
		final BuildingPlan plan=getRandomStartingPlan();

		return plan.getGameName();
	}

	public int getMaxLevel(int variation,int level) {

		int maxLevel=Integer.MIN_VALUE;

		for (int i=0;i<=level;i++) {
			final BuildingPlan plan=plans.get(variation)[i];
			if ((plan.plan.length+plan.firstLevel)>maxLevel) {
				maxLevel=plan.plan.length+plan.firstLevel;
			}
		}

		return maxLevel;
	}

	public int getMinLevel(int variation,int level) {

		int minLevel=Integer.MAX_VALUE;

		for (int i=0;i<=level;i++) {
			final BuildingPlan plan=plans.get(variation)[i];
			if (plan.firstLevel<minLevel) {
				minLevel=plan.firstLevel;
			}
		}

		return minLevel;
	}

	public String getNativeName() {
		if (plans.size()==0)
			return key;
		final BuildingPlan plan=getRandomStartingPlan();
		return plan.nativeName;
	}

	public BuildingPlan getRandomStartingPlan() {
		if (plans.size()==0)
			return null;
		return plans.get(MillCommonUtilities.randomInt(plans.size()))[0];
	}

	public void loadPictPlans(boolean importPlan) throws Exception {

		final Vector<Vector<BuildingPlan>> vplans=new Vector<Vector<BuildingPlan>>();

		BuildingPlan prevPlan=null;

		char varChar='A';
		int variation=0;

		while (new File(dir,key+"_"+(varChar)+".txt").exists()) {
			vplans.add(new Vector<BuildingPlan>());
			int level=0;
			prevPlan=null;

			final BufferedReader reader = MillCommonUtilities.getReader(new File(dir,key+"_"+(varChar)+".txt"));

			while (new File(dir,key+"_"+varChar+level+".png").exists()) {
				final String line=reader.readLine();
				prevPlan=new BuildingPlan(dir,key,level,variation,prevPlan,line,culture,importPlan);
				vplans.get(variation).add(prevPlan);
				level++;
			}
			if (vplans.get(variation).size()==0)
				throw new MillenaireException("No file found for building "+key+varChar);
			varChar++;
			variation++;
		}

		max=vplans.get(0).get(0).max;

		for (final Vector<BuildingPlan> varPlans : vplans) {
			final int length=varPlans.get(0).length;
			final int width=varPlans.get(0).width;

			for (final BuildingPlan plan : varPlans) {
				if (plan.width != width)
					throw new MillenaireException("Width of upgrade "+plan.level+" of building "+plan.buildingKey+" is "+plan.width+" instead of "+width);

				if (plan.length != length)
					throw new MillenaireException("Length of upgrade "+plan.level+" of building "+plan.buildingKey+" is "+plan.length+" instead of "+length);
			}

			final BuildingPlan[] varplansarray=new BuildingPlan[varPlans.size()];

			for (int i=0;i<varPlans.size();i++) {
				varplansarray[i]=varPlans.get(i);
			}

			plans.add(varplansarray);
		}
	}

	public void loadPlans(Culture culture,boolean importPlan) throws Exception {

		final Vector<Vector<BuildingPlan>> vplans=new Vector<Vector<BuildingPlan>>();

		BuildingPlan prevPlan=null;

		char varChar='A';
		int variation=0;

		while (new File(dir,key+"_"+(varChar)+"0.txt").exists()) {
			vplans.add(new Vector<BuildingPlan>());
			int level=0;
			prevPlan=null;
			while (new File(dir,key+"_"+varChar+level+".txt").exists()) {
				prevPlan=new BuildingPlan(dir,key,level,variation,prevPlan,culture,importPlan);
				vplans.get(variation).add(prevPlan);
				level++;

			}
			if (vplans.get(variation).size()==0)
				throw new MillenaireException("No file found for building "+key+varChar);
			varChar++;
			variation++;
		}

		max=vplans.get(0).get(0).max;

		for (final Vector<BuildingPlan> varPlans : vplans) {
			final int length=varPlans.get(0).length;
			final int width=varPlans.get(0).width;

			for (final BuildingPlan plan : varPlans) {
				if (plan.width != width)
					throw new MillenaireException("Width of upgrade "+plan.level+" of building "+plan.buildingKey+" is "+plan.width+" instead of "+width);

				if (plan.length != length)
					throw new MillenaireException("Length of upgrade "+plan.level+" of building "+plan.buildingKey+" is "+plan.length+" instead of "+length);
			}

			final BuildingPlan[] varplansarray=new BuildingPlan[varPlans.size()];

			for (int i=0;i<varPlans.size();i++) {
				varplansarray[i]=varPlans.get(i);
			}

			plans.add(varplansarray);
		}
	}

	public void readBuildingPlanSetInfoPacket(ByteBufInputStream ds) throws IOException {

		final int nb=ds.readInt();

		this.plans.clear();

		for (int i=0;i<nb;i++) {
			final int nb2=ds.readInt();

			final BuildingPlan[] plans=new BuildingPlan[nb2];

			for (int j=0;j<nb2;j++) {
				plans[j]=StreamReadWrite.readBuildingPlanInfo(ds, culture);
			}

			this.plans.add(plans);
		}
	}

	@Override
	public String toString() {
		return key+" ("+plans.size()+" / "+max+"/"+plans.get(0)[0].nativeName+")";
	}

	public void writeBuildingPlanSetInfo(ByteBufOutputStream data) throws IOException {
		data.writeUTF(key);
		data.writeInt(plans.size());

		for (final BuildingPlan[] plans : this.plans) {
			data.writeInt(plans.length);
			for (final BuildingPlan plan : plans) {
				StreamReadWrite.writeBuildingPlanInfo(plan,data);
			}
		}
	}

}