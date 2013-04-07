package org.millenaire.common;

import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.forge.Mill;


public class BuildingLocation implements Cloneable {

	public static BuildingLocation read(NBTTagCompound nbttagcompound,String label, String debug) {

		if (!nbttagcompound.hasKey(label+"_key"))
			return null;

		final BuildingLocation bl=new BuildingLocation();

		bl.pos=Point.read(nbttagcompound, label+"_pos");

		final Culture culture=Culture.getCultureByName(nbttagcompound.getString(label+"_culture"));
		bl.culture=culture;

		bl.orientation=nbttagcompound.getInteger(label+"_orientation");
		bl.length=nbttagcompound.getInteger(label+"_length");
		bl.width=nbttagcompound.getInteger(label+"_width");
		bl.areaToClear=nbttagcompound.getInteger(label+"_areaToClear");
		bl.level=nbttagcompound.getInteger(label+"_level");
		bl.key=nbttagcompound.getString(label+"_key");

		//MLN.temp(bl, "Reading key "+debug+": "+bl.key);

		bl.shop=nbttagcompound.getString(label+"_shop");
		if (Culture.oldShopConversion.containsKey(bl.shop)) {
			bl.shop=Culture.oldShopConversion.get(bl.shop);
		}

		bl.setVariation(nbttagcompound.getInteger(label+"_variation"));

		//MLN.temp(bl, "Reading variation "+debug+": "+bl.getVariation());

		bl.reputation=nbttagcompound.getInteger(label+"_reputation");
		bl.priorityMoveIn=nbttagcompound.getInteger(label+"_priorityMoveIn");
		bl.price=nbttagcompound.getInteger(label+"_price");

		if (bl.pos==null) {
			MLN.error(null, "Null point loaded for: "+label+"_pos");
		}

		bl.sleepingPos=Point.read(nbttagcompound, label+"_standingPos");
		bl.sellingPos=Point.read(nbttagcompound, label+"_sellingPos");
		bl.craftingPos=Point.read(nbttagcompound, label+"_craftingPos");
		bl.shelterPos=Point.read(nbttagcompound, label+"_shelterPos");
		bl.defendingPos=Point.read(nbttagcompound, label+"_defendingPos");
		bl.chestPos=Point.read(nbttagcompound, label+"_chestPos");

		final Vector<String> maleResident=new Vector<String>();

		//pre 4.3 bugged tag
		NBTTagList nbttaglist = nbttagcompound.getTagList("maleResidentList");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			maleResident.add(nbttagcompound1.getString("value"));
		}

		nbttaglist = nbttagcompound.getTagList(label+"_maleResidentList");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			maleResident.add(nbttagcompound1.getString("value"));
		}

		//for compatibility with versions with only one male resident:
		String s=nbttagcompound.getString(label+"_maleResident");
		if ((s!=null) && (s.length()>0)) {
			maleResident.add(s);
		}

		bl.maleResident=maleResident;

		final Vector<String> femaleResident=new Vector<String>();

		//pre 4.3 bugged tag
		nbttaglist = nbttagcompound.getTagList("femaleResidentList");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			femaleResident.add(nbttagcompound1.getString("value"));
		}

		nbttaglist = nbttagcompound.getTagList(label+"_femaleResidentList");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			femaleResident.add(nbttagcompound1.getString("value"));
		}


		//for compatibility with versions with only one female resident:
		s=nbttagcompound.getString(label+"_femaleResident");
		if ((s!=null) && (s.length()>0)) {
			femaleResident.add(s);
		}

		bl.femaleResident=femaleResident;


		final Vector<String> tags=new Vector<String>();

		//pre 4.3 bugged tag
		nbttaglist = nbttagcompound.getTagList("tags");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			final String value=nbttagcompound1.getString("value");

			//Conversion for backward compatibility
			if (value.equals("market1") || value.equals("market2") || value.equals("market3")) {
				tags.add("market");
			} else {
				tags.add(value);
			}

		}


		nbttaglist = nbttagcompound.getTagList(label+"_tags");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			final String value=nbttagcompound1.getString("value");

			tags.add(value);
		}

		bl.tags=tags;

		final Vector<String> subb=new Vector<String>();

		//pre 4.3 bugged tag
		nbttaglist = nbttagcompound.getTagList("subBuildings");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			subb.add(nbttagcompound1.getString("value"));

		}

		nbttaglist = nbttagcompound.getTagList(label+"_subBuildings");
		for(int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			subb.add(nbttagcompound1.getString("value"));

		}

		bl.subBuildings=subb;

		bl.showTownHallSigns=nbttagcompound.getBoolean(label+"_showTownHallSigns");

		if (nbttagcompound.hasKey(label+"_upgradesAllowed")) {
			bl.upgradesAllowed=nbttagcompound.getBoolean(label+"_upgradesAllowed");
		}

		if (bl.getPlan()==null) {
			MLN.error(bl, "Unknown building type. Cancelling load.");
			return null;
		}

		bl.initialise();

		return bl;
	}
	public String key,shop;
	public Vector<String> maleResident;
	public Vector<String> femaleResident;
	public int priorityMoveIn=10;
	public int minx,maxx,minz,maxz;
	public int minxMargin,maxxMargin,minzMargin,maxzMargin;
	public int orientation,length,width,areaToClear,level,reputation,price;
	private int variation;

	public Point pos,chestPos=null,sleepingPos=null;
	public Point sellingPos=null,craftingPos=null,shelterPos=null,defendingPos=null;
	public Culture culture;
	public Vector<String> tags;
	public Vector<String> subBuildings;
	public boolean showTownHallSigns;
	public boolean upgradesAllowed=true;

	public boolean bedrocklevel=false;


	public BuildingLocation() {

	}

	public BuildingLocation(BuildingPlan plan,Point ppos,int porientation) {
		pos=ppos;

		if (pos==null) {
			MLN.error(this, "Attempting to create a location with a null position.");
		}

		orientation=porientation;
		length=plan.length;
		width=plan.width;
		key=plan.buildingKey;
		level=plan.level;
		tags=plan.tags;
		subBuildings=plan.subBuildings;
		setVariation(plan.variation);
		maleResident=plan.maleResident;
		femaleResident=plan.femaleResident;
		shop=plan.shop;
		reputation=plan.reputation;
		price=plan.price;
		showTownHallSigns=plan.showTownHallSigns;
		culture=plan.culture;
		priorityMoveIn=plan.priorityMoveIn;

		initialise();

	}

	@Override
	public BuildingLocation clone() {
		try {
			final BuildingLocation bl= (BuildingLocation) super.clone();


			return bl;
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	public BuildingLocation createLocationForLevel(int plevel) {
		final BuildingPlan plan=culture.getBuildingPlanSet(key).plans.get(getVariation())[plevel];

		final BuildingLocation bl=clone();
		bl.level=plevel;
		bl.tags=plan.tags;
		bl.subBuildings=plan.subBuildings;

		return bl;
	}

	public BuildingLocation createLocationForStartingSubBuilding(String subkey) {
		final BuildingLocation bl=createLocationForSubBuilding(subkey);
		bl.level=0;

		return bl;
	}

	public BuildingLocation createLocationForSubBuilding(String subkey) {
		final BuildingPlan plan=culture.getBuildingPlanSet(subkey).getRandomStartingPlan();

		final BuildingLocation bl=clone();
		bl.key=subkey;
		bl.level=-1;
		bl.tags=plan.tags;
		bl.subBuildings=plan.subBuildings;
		bl.maleResident=plan.maleResident;
		bl.femaleResident=plan.femaleResident;
		bl.shop=plan.shop;
		bl.reputation=plan.reputation;
		bl.price=plan.price;
		bl.showTownHallSigns=plan.showTownHallSigns;
		return bl;
	}

	public Building getBuilding(World world) {
		return Mill.getMillWorld(world).getBuilding(chestPos);
	}

	public Vector<String> getBuildingEffects(World world) {
		final Vector<String> effects=new Vector<String>();

		final Building building=getBuilding(world);

		if (building != null) {
			if (building.isTownhall) {
				effects.add(MLN.string("effect.towncentre"));
			}
		}

		if ((shop!=null) && (shop.length()>0)) {
			effects.add(MLN.string("effect.shop",culture.getCultureString("shop."+shop)));
		}

		if (tags.contains(Building.tagPujas)) {
			effects.add(MLN.string("effect.pujalocation"));
		}

		final BuildingPlan plan=getPlan();

		if (plan!=null) {
			if (plan.irrigation>0) {
				effects.add(MLN.string("effect.irrigation", ""+plan.irrigation));
			}
		}

		if (building != null) {
			if (building.healingspots.size()>0) {
				effects.add(MLN.string("effect.healing"));
			}
		}

		return effects;
	}

	public Point[] getCorners() {

		final Point[] corners=new Point[4];

		corners[0]=new Point(minxMargin,pos.getiY(),minzMargin);
		corners[1]=new Point(maxxMargin,pos.getiY(),minzMargin);
		corners[2]=new Point(minxMargin,pos.getiY(),maxzMargin);
		corners[3]=new Point(maxxMargin,pos.getiY(),maxzMargin);
		return corners;
	}

	public BuildingPlan getPlan() {
		if (culture==null) {
			MLN.error(this, "null culture");
			return null;
		}


		if ((culture.getBuildingPlanSet(key)!=null) && (culture.getBuildingPlanSet(key).plans.size()>getVariation())) {
			if (level<0)
				return culture.getBuildingPlanSet(key).plans.get(getVariation())[0];
			if (culture.getBuildingPlanSet(key).plans.get(getVariation()).length>level)
				return culture.getBuildingPlanSet(key).plans.get(getVariation())[level];
			return null;
		} else
			return null;
	}

	public Point getSellingPos() {
		if (sellingPos!=null)
			return sellingPos;

		return sleepingPos;
	}

	public int getVariation() {
		return variation;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	private void initialise() {
		final Point op1=BuildingPlan.adjustForOrientation(pos.getiX(),pos.getiY(),pos.getiZ(),(length)/2,(width)/2,orientation);
		final Point op2=BuildingPlan.adjustForOrientation(pos.getiX(),pos.getiY(),pos.getiZ(),-(length)/2,-(width)/2,orientation);

		if (op1.getiX() > op2.getiX()) {
			minx=op2.getiX();
			maxx=op1.getiX();
		} else {
			minx=op1.getiX();
			maxx=op2.getiX();
		}

		if (op1.getiZ() > op2.getiZ()) {
			minz=op2.getiZ();
			maxz=op1.getiZ();
		} else {
			minz=op1.getiZ();
			maxz=op2.getiZ();
		}

		minxMargin=minx-(areaToClear+MLN.minDistanceBetweenBuildings);
		minzMargin=minz-(areaToClear+MLN.minDistanceBetweenBuildings);
		maxxMargin=maxx+areaToClear+MLN.minDistanceBetweenBuildings;
		maxzMargin=maxz+areaToClear+MLN.minDistanceBetweenBuildings;
	}

	public boolean intersectWith(BuildingLocation b) {

		if ((minxMargin > b.maxxMargin) || (maxxMargin < b.minxMargin) || (minzMargin > b.maxzMargin) || (maxzMargin < b.minzMargin) )
			return false;

		return true;
	}

	public boolean isInside(Point p) {
		if ((minx < p.getiX()) && (p.getiX() <= maxx) && (minz < p.getiZ()) && (p.getiZ() <= maxz))
			return true;
		//Log.debug(this, Log.WorldGeneration, "Outside!");
		return false;
	}

	public boolean isInsideZone(Point p) {


		if ((minxMargin <= p.getiX()) && (p.getiX() <= maxxMargin) && (minzMargin <= p.getiZ()) && (p.getiZ() <= maxzMargin))
			return true;
		//Log.debug(this, Log.WorldGeneration, "Outside!");
		return false;
	}


	public boolean isLocationSamePlace(BuildingLocation l) {
		if (l==null)
			return false;

		return (pos.equals(l.pos) && (orientation==l.orientation) && (getVariation()==l.getVariation()));
	}

	public boolean isSameLocation(BuildingLocation l) {
		if (l==null)
			return false;

		return (pos.equals(l.pos) && key.equals(l.key) && (orientation==l.orientation) && (getVariation()==l.getVariation()));
	}

	public int oldHashCode() {
		return super.hashCode();
	}

	public void setVariation(int var) {
		variation=var;
	}

	@Override
	public String toString() {
		return key+"_"+level+" at "+pos+"/"+orientation+"/"+getVariation();
	}

	public void write(NBTTagCompound nbttagcompound,String label,String debug) {

		pos.write(nbttagcompound, label+"_pos");

		nbttagcompound.setString(label+"_culture", culture.key);
		nbttagcompound.setInteger(label+"_orientation", orientation);
		nbttagcompound.setInteger(label+"_length", length);
		nbttagcompound.setInteger(label+"_width", width);
		nbttagcompound.setInteger(label+"_areaToClear", areaToClear);
		nbttagcompound.setInteger(label+"_level", level);
		nbttagcompound.setString(label+"_key", key);

		//MLN.temp(this, "Writing key "+oldHashCode()+"-"+debug+": "+key+"_"+level);

		nbttagcompound.setInteger(label+"_variation", getVariation());

		//MLN.temp(this, "Writing variation "+oldHashCode()+": "+getVariation());

		nbttagcompound.setInteger(label+"_reputation", reputation);
		nbttagcompound.setInteger(label+"_price", price);
		nbttagcompound.setInteger(label+"_priorityMoveIn", priorityMoveIn);
		if ((shop != null) && (shop.length()>0)) {
			nbttagcompound.setString(label+"_shop", shop);
		}

		NBTTagList nbttaglist = new NBTTagList();
		for (final String tag : maleResident) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", tag);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag(label+"_maleResidentList", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final String tag : femaleResident) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", tag);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag(label+"_femaleResidentList", nbttaglist);


		nbttaglist = new NBTTagList();
		for (final String tag : tags) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", tag);
			nbttaglist.appendTag(nbttagcompound1);
			//MLN.temp(this, "Writing tag: "+tag);
		}
		nbttagcompound.setTag(label+"_tags", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final String subb : subBuildings) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", subb);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag(label+"_subBuildings", nbttaglist);


		if (sleepingPos!=null) {
			sleepingPos.write(nbttagcompound, label+"_standingPos");
		}
		if (sellingPos!=null) {
			sellingPos.write(nbttagcompound, label+"_sellingPos");
		}
		if (craftingPos!=null) {
			craftingPos.write(nbttagcompound, label+"_craftingPos");
		}
		if (defendingPos!=null) {
			defendingPos.write(nbttagcompound, label+"_defendingPos");
		}
		if (shelterPos!=null) {
			shelterPos.write(nbttagcompound, label+"_shelterPos");
		}
		if (chestPos!=null) {
			chestPos.write(nbttagcompound, label+"_chestPos");
		}

		nbttagcompound.setBoolean(label+"_showTownHallSigns", showTownHallSigns);
		nbttagcompound.setBoolean(label+"_upgradesAllowed", upgradesAllowed);


	}

}
