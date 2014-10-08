package org.millenaire.common.building;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.forge.Mill;

public class BuildingLocation implements Cloneable {

	public static BuildingLocation read(final NBTTagCompound nbttagcompound, final String label, final String debug) {

		if (!nbttagcompound.hasKey(label + "_key")) {
			return null;
		}

		final BuildingLocation bl = new BuildingLocation();

		bl.pos = Point.read(nbttagcompound, label + "_pos");

		// pre-6.0 this tag did not exist
		if (nbttagcompound.hasKey(label + "_isCustomBuilding")) {
			bl.isCustomBuilding = nbttagcompound.getBoolean(label + "_isCustomBuilding");
		}

		final Culture culture = Culture.getCultureByName(nbttagcompound.getString(label + "_culture"));
		bl.culture = culture;

		bl.orientation = nbttagcompound.getInteger(label + "_orientation");
		bl.length = nbttagcompound.getInteger(label + "_length");
		bl.width = nbttagcompound.getInteger(label + "_width");
		bl.areaToClear = nbttagcompound.getInteger(label + "_areaToClear");
		bl.level = nbttagcompound.getInteger(label + "_level");
		bl.planKey = nbttagcompound.getString(label + "_key");

		// MLN.temp(bl, "Reading key "+debug+": "+bl.key);

		bl.shop = nbttagcompound.getString(label + "_shop");
		if (Culture.oldShopConversion.containsKey(bl.shop)) {
			bl.shop = Culture.oldShopConversion.get(bl.shop);
		}

		bl.setVariation(nbttagcompound.getInteger(label + "_variation"));

		// MLN.temp(bl, "Reading variation "+debug+": "+bl.getVariation());

		bl.reputation = nbttagcompound.getInteger(label + "_reputation");
		bl.priorityMoveIn = nbttagcompound.getInteger(label + "_priorityMoveIn");
		bl.price = nbttagcompound.getInteger(label + "_price");

		if (bl.pos == null) {
			MLN.error(null, "Null point loaded for: " + label + "_pos");
		}

		bl.sleepingPos = Point.read(nbttagcompound, label + "_standingPos");
		bl.sellingPos = Point.read(nbttagcompound, label + "_sellingPos");
		bl.craftingPos = Point.read(nbttagcompound, label + "_craftingPos");
		bl.shelterPos = Point.read(nbttagcompound, label + "_shelterPos");
		bl.defendingPos = Point.read(nbttagcompound, label + "_defendingPos");
		bl.chestPos = Point.read(nbttagcompound, label + "_chestPos");

		final List<String> maleResident = new ArrayList<String>();

		// pre 4.3 bugged tag
		NBTTagList nbttaglist = nbttagcompound.getTagList("maleResidentList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			maleResident.add(nbttagcompound1.getString("value"));
		}

		nbttaglist = nbttagcompound.getTagList(label + "_maleResidentList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			maleResident.add(nbttagcompound1.getString("value"));
		}

		// for compatibility with versions with only one male resident:
		String s = nbttagcompound.getString(label + "_maleResident");
		if (s != null && s.length() > 0) {
			maleResident.add(s);
		}

		bl.maleResident = maleResident;

		final List<String> femaleResident = new ArrayList<String>();

		// pre 4.3 bugged tag
		nbttaglist = nbttagcompound.getTagList("femaleResidentList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			femaleResident.add(nbttagcompound1.getString("value"));
		}

		nbttaglist = nbttagcompound.getTagList(label + "_femaleResidentList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			femaleResident.add(nbttagcompound1.getString("value"));
		}

		// for compatibility with versions with only one female resident:
		s = nbttagcompound.getString(label + "_femaleResident");
		if (s != null && s.length() > 0) {
			femaleResident.add(s);
		}

		bl.femaleResident = femaleResident;

		final List<String> tags = new ArrayList<String>();

		// pre 4.3 bugged tag
		nbttaglist = nbttagcompound.getTagList("tags", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final String value = nbttagcompound1.getString("value").toLowerCase();

			// Conversion for backward compatibility
			if (value.equals("market1") || value.equals("market2") || value.equals("market3")) {
				tags.add("market");
			} else {
				tags.add(value);
			}

		}

		nbttaglist = nbttagcompound.getTagList(label + "_tags", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final String value = nbttagcompound1.getString("value");

			tags.add(value);
		}

		bl.tags = tags;

		final List<String> subb = new ArrayList<String>();

		// pre 4.3 bugged tag
		nbttaglist = nbttagcompound.getTagList("subBuildings", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			subb.add(nbttagcompound1.getString("value"));

		}

		nbttaglist = nbttagcompound.getTagList(label + "_subBuildings", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			subb.add(nbttagcompound1.getString("value"));

		}

		bl.subBuildings = subb;

		bl.showTownHallSigns = nbttagcompound.getBoolean(label + "_showTownHallSigns");

		if (nbttagcompound.hasKey(label + "_upgradesAllowed")) {
			bl.upgradesAllowed = nbttagcompound.getBoolean(label + "_upgradesAllowed");
		}

		if (bl.getPlan() == null && bl.getCustomPlan() == null) {
			MLN.error(bl, "Unknown building type: " + bl.planKey + " Cancelling load.");
			return null;
		}

		bl.initialise();

		return bl;
	}

	public String planKey, shop;
	public List<String> maleResident;
	public List<String> femaleResident;
	public int priorityMoveIn = 10;
	public int minx, maxx, minz, maxz;
	public int minxMargin, maxxMargin, minzMargin, maxzMargin;
	public int orientation, length, width, areaToClear, level, reputation, price;
	private int variation;

	public boolean isCustomBuilding = false;

	public Point pos, chestPos = null, sleepingPos = null;
	public Point sellingPos = null, craftingPos = null, shelterPos = null, defendingPos = null;
	public Culture culture;
	public List<String> tags;
	public List<String> subBuildings;
	public boolean showTownHallSigns;
	public boolean upgradesAllowed = true;

	public boolean bedrocklevel = false;

	public BuildingLocation() {

	}

	/**
	 * Creates a location based on a custom plan and a position
	 * 
	 * @param customBuilding
	 * @param pos
	 */
	public BuildingLocation(final BuildingCustomPlan customBuilding, final Point pos, final boolean isTownHall) {
		this.pos = pos;
		this.chestPos = pos;
		orientation = 0;

		planKey = customBuilding.buildingKey;
		isCustomBuilding = true;
		level = 0;
		tags = customBuilding.tags;
		subBuildings = new ArrayList<String>();
		setVariation(0);
		maleResident = customBuilding.maleResident;
		femaleResident = customBuilding.femaleResident;
		shop = customBuilding.shop;
		reputation = 0;
		price = 0;
		showTownHallSigns = isTownHall;
		culture = customBuilding.culture;
		priorityMoveIn = customBuilding.priorityMoveIn;
	}

	/**
	 * Creates a location based on a plan, a position and an orientation
	 * 
	 * @param plan
	 * @param ppos
	 * @param porientation
	 */
	public BuildingLocation(final BuildingPlan plan, final Point ppos, final int porientation) {
		pos = ppos;

		if (pos == null) {
			MLN.error(this, "Attempting to create a location with a null position.");
		}

		orientation = porientation;
		length = plan.length;
		width = plan.width;
		planKey = plan.buildingKey;
		level = plan.level;
		tags = plan.tags;
		subBuildings = plan.subBuildings;
		setVariation(plan.variation);
		maleResident = plan.maleResident;
		femaleResident = plan.femaleResident;
		shop = plan.shop;
		reputation = plan.reputation;
		price = plan.price;
		showTownHallSigns = plan.showTownHallSigns;
		culture = plan.culture;
		priorityMoveIn = plan.priorityMoveIn;

		initialise();

	}

	@Override
	public BuildingLocation clone() {
		try {
			final BuildingLocation bl = (BuildingLocation) super.clone();

			return bl;
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Computes margins around building
	 */
	public void computeMargins() {
		minxMargin = minx - (areaToClear + MLN.minDistanceBetweenBuildings);
		minzMargin = minz - (areaToClear + MLN.minDistanceBetweenBuildings);
		maxxMargin = maxx + areaToClear + MLN.minDistanceBetweenBuildings;
		maxzMargin = maxz + areaToClear + MLN.minDistanceBetweenBuildings;
	}

	public BuildingLocation createLocationForLevel(final int plevel) {
		final BuildingPlan plan = culture.getBuildingPlanSet(planKey).plans.get(getVariation())[plevel];

		final BuildingLocation bl = clone();
		bl.level = plevel;
		bl.tags = plan.tags;
		bl.subBuildings = plan.subBuildings;

		return bl;
	}

	public BuildingLocation createLocationForStartingSubBuilding(final String subkey) {
		final BuildingLocation bl = createLocationForSubBuilding(subkey);
		bl.level = 0;

		return bl;
	}

	public BuildingLocation createLocationForSubBuilding(final String subkey) {
		final BuildingPlan plan = culture.getBuildingPlanSet(subkey).getRandomStartingPlan();

		final BuildingLocation bl = clone();
		bl.planKey = subkey;
		bl.level = -1;
		bl.tags = plan.tags;
		bl.subBuildings = plan.subBuildings;
		bl.maleResident = plan.maleResident;
		bl.femaleResident = plan.femaleResident;
		bl.shop = plan.shop;
		bl.reputation = plan.reputation;
		bl.price = plan.price;
		bl.showTownHallSigns = plan.showTownHallSigns;
		return bl;
	}

	@Override
	public boolean equals(final Object obj) {

		if (obj == null || !(obj instanceof BuildingLocation)) {
			return false;
		}

		final BuildingLocation bl = (BuildingLocation) obj;

		return planKey.equals(bl.planKey) && level == bl.level && pos.equals(bl.pos) && orientation == bl.orientation && getVariation() == bl.getVariation();

	}

	public Building getBuilding(final World world) {
		return Mill.getMillWorld(world).getBuilding(chestPos);
	}

	public List<String> getBuildingEffects(final World world) {
		final List<String> effects = new ArrayList<String>();

		final Building building = getBuilding(world);

		if (building != null) {
			if (building.isTownhall) {
				effects.add(MLN.string("effect.towncentre"));
			}
		}

		if (shop != null && shop.length() > 0) {
			effects.add(MLN.string("effect.shop", culture.getCultureString("shop." + shop)));
		}

		if (tags.contains(Building.tagPujas)) {
			effects.add(MLN.string("effect.pujalocation"));
		}

		if (tags.contains(Building.tagSacrifices)) {
			effects.add(MLN.string("effect.sacrificeslocation"));
		}

		final BuildingPlan plan = getPlan();

		if (plan != null) {
			if (plan.irrigation > 0) {
				effects.add(MLN.string("effect.irrigation", "" + plan.irrigation));
			}
		}

		if (building != null) {
			if (building.getResManager().healingspots.size() > 0) {
				effects.add(MLN.string("effect.healing"));
			}
		}

		return effects;
	}

	public Point[] getCorners() {

		final Point[] corners = new Point[4];

		corners[0] = new Point(minxMargin, pos.getiY(), minzMargin);
		corners[1] = new Point(maxxMargin, pos.getiY(), minzMargin);
		corners[2] = new Point(minxMargin, pos.getiY(), maxzMargin);
		corners[3] = new Point(maxxMargin, pos.getiY(), maxzMargin);
		return corners;
	}

	public BuildingCustomPlan getCustomPlan() {
		if (culture == null) {
			MLN.error(this, "null culture");
			return null;
		}

		if (culture.getBuildingCustom(planKey) != null) {
			return culture.getBuildingCustom(planKey);
		} else {
			return null;
		}
	}

	/**
	 * Native name plus name in player's language, if readable.
	 * 
	 * Ex: "puit (well)"
	 * 
	 * @return
	 */
	public String getFullDisplayName() {
		if (isCustomBuilding) {
			return getCustomPlan().getFullDisplayName();
		} else {
			return getPlan().getFullDisplayName();
		}
	}

	/**
	 * Name in player's language, if readable by player
	 * 
	 * Ex: "well"
	 * 
	 * @return
	 */
	public String getGameName() {
		if (isCustomBuilding) {
			return getCustomPlan().getGameName();
		} else {
			return getPlan().getGameName();
		}
	}

	/**
	 * Native name
	 * 
	 * Ex: puit
	 * 
	 * @return
	 */
	public String getNativeName() {
		if (isCustomBuilding) {
			return getCustomPlan().nativeName;
		} else {
			return getPlan().nativeName;
		}
	}

	public BuildingPlan getPlan() {
		if (culture == null) {
			MLN.error(this, "null culture");
			return null;
		}

		if (culture.getBuildingPlanSet(planKey) != null && culture.getBuildingPlanSet(planKey).plans.size() > getVariation()) {
			if (level < 0) {
				return culture.getBuildingPlanSet(planKey).plans.get(getVariation())[0];
			}
			if (culture.getBuildingPlanSet(planKey).plans.get(getVariation()).length > level) {
				return culture.getBuildingPlanSet(planKey).plans.get(getVariation())[level];
			}
			return null;
		} else {
			return null;
		}
	}

	public Point getSellingPos() {
		if (sellingPos != null) {
			return sellingPos;
		}

		return sleepingPos;
	}

	public int getVariation() {
		return variation;
	}

	@Override
	public int hashCode() {
		return (planKey + "_" + level + " at " + pos + "/" + orientation + "/" + getVariation()).hashCode();
	}

	/**
	 * Computes X & Z min & max values for plan-based locations from length and
	 * width and orientation
	 */
	private void initialise() {
		final Point op1 = BuildingPlan.adjustForOrientation(pos.getiX(), pos.getiY(), pos.getiZ(), length / 2, width / 2, orientation);
		final Point op2 = BuildingPlan.adjustForOrientation(pos.getiX(), pos.getiY(), pos.getiZ(), -length / 2, -width / 2, orientation);

		if (op1.getiX() > op2.getiX()) {
			minx = op2.getiX();
			maxx = op1.getiX();
		} else {
			minx = op1.getiX();
			maxx = op2.getiX();
		}

		if (op1.getiZ() > op2.getiZ()) {
			minz = op2.getiZ();
			maxz = op1.getiZ();
		} else {
			minz = op1.getiZ();
			maxz = op2.getiZ();
		}

		computeMargins();
	}

	public boolean intersectWith(final BuildingLocation b) {

		if (minxMargin > b.maxxMargin || maxxMargin < b.minxMargin || minzMargin > b.maxzMargin || maxzMargin < b.minzMargin) {
			return false;
		}

		return true;
	}

	public boolean isInside(final Point p) {
		if (minx < p.getiX() && p.getiX() <= maxx && minz < p.getiZ() && p.getiZ() <= maxz) {
			return true;
		}
		// Log.debug(this, Log.WorldGeneration, "Outside!");
		return false;
	}

	public boolean isInsideZone(final Point p) {

		if (minxMargin <= p.getiX() && p.getiX() <= maxxMargin && minzMargin <= p.getiZ() && p.getiZ() <= maxzMargin) {
			return true;
		}
		// Log.debug(this, Log.WorldGeneration, "Outside!");
		return false;
	}

	public boolean isLocationSamePlace(final BuildingLocation l) {
		if (l == null) {
			return false;
		}

		return pos.equals(l.pos) && orientation == l.orientation && getVariation() == l.getVariation();
	}

	public boolean isSameLocation(final BuildingLocation l) {
		if (l == null) {
			return false;
		}

		return pos.equals(l.pos) && planKey.equals(l.planKey) && orientation == l.orientation && getVariation() == l.getVariation();
	}

	public int oldHashCode() {
		return super.hashCode();
	}

	public void setVariation(final int var) {
		variation = var;
	}

	@Override
	public String toString() {
		return planKey + "_" + level + " at " + pos + "/" + orientation + "/" + getVariation();
	}

	public void write(final NBTTagCompound nbttagcompound, final String label, final String debug) {

		pos.write(nbttagcompound, label + "_pos");

		nbttagcompound.setBoolean(label + "_isCustomBuilding", isCustomBuilding);
		nbttagcompound.setString(label + "_culture", culture.key);
		nbttagcompound.setInteger(label + "_orientation", orientation);
		nbttagcompound.setInteger(label + "_length", length);
		nbttagcompound.setInteger(label + "_width", width);
		nbttagcompound.setInteger(label + "_areaToClear", areaToClear);
		nbttagcompound.setInteger(label + "_level", level);
		nbttagcompound.setString(label + "_key", planKey);

		// MLN.temp(this,
		// "Writing key "+oldHashCode()+"-"+debug+": "+key+"_"+level);

		nbttagcompound.setInteger(label + "_variation", getVariation());

		// MLN.temp(this,
		// "Writing variation "+oldHashCode()+": "+getVariation());

		nbttagcompound.setInteger(label + "_reputation", reputation);
		nbttagcompound.setInteger(label + "_price", price);
		nbttagcompound.setInteger(label + "_priorityMoveIn", priorityMoveIn);
		if (shop != null && shop.length() > 0) {
			nbttagcompound.setString(label + "_shop", shop);
		}

		NBTTagList nbttaglist = new NBTTagList();
		for (final String tag : maleResident) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", tag);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag(label + "_maleResidentList", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final String tag : femaleResident) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", tag);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag(label + "_femaleResidentList", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final String tag : tags) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", tag);
			nbttaglist.appendTag(nbttagcompound1);
			// MLN.temp(this, "Writing tag: "+tag);
		}
		nbttagcompound.setTag(label + "_tags", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final String subb : subBuildings) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("value", subb);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag(label + "_subBuildings", nbttaglist);

		if (sleepingPos != null) {
			sleepingPos.write(nbttagcompound, label + "_standingPos");
		}
		if (sellingPos != null) {
			sellingPos.write(nbttagcompound, label + "_sellingPos");
		}
		if (craftingPos != null) {
			craftingPos.write(nbttagcompound, label + "_craftingPos");
		}
		if (defendingPos != null) {
			defendingPos.write(nbttagcompound, label + "_defendingPos");
		}
		if (shelterPos != null) {
			shelterPos.write(nbttagcompound, label + "_shelterPos");
		}
		if (chestPos != null) {
			chestPos.write(nbttagcompound, label + "_chestPos");
		}

		nbttagcompound.setBoolean(label + "_showTownHallSigns", showTownHallSigns);
		nbttagcompound.setBoolean(label + "_upgradesAllowed", upgradesAllowed);

	}

}
