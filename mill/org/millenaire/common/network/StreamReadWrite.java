package org.millenaire.common.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.millenaire.common.Culture;
import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.PujaSacrifice;
import org.millenaire.common.Quest;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.Quest.QuestInstanceVillager;
import org.millenaire.common.UserProfile;
import org.millenaire.common.VillagerRecord;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingLocation;
import org.millenaire.common.building.BuildingPlan;
import org.millenaire.common.building.BuildingProject;
import org.millenaire.common.building.BuildingProject.EnumProjects;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;

public class StreamReadWrite {

	static public Random random;

	public static BuildingPlan readBuildingPlanInfo(final DataInput ds, final Culture culture) throws IOException {

		final String key = ds.readUTF();
		final int level = ds.readInt();
		final int variation = ds.readInt();

		final BuildingPlan plan = new BuildingPlan(key, level, variation, culture);

		plan.planName = StreamReadWrite.readNullableString(ds);
		plan.nativeName = StreamReadWrite.readNullableString(ds);
		plan.requiredTag = StreamReadWrite.readNullableString(ds);
		plan.shop = StreamReadWrite.readNullableString(ds);
		plan.type = StreamReadWrite.readNullableString(ds);
		plan.price = ds.readInt();
		plan.reputation = ds.readInt();
		plan.maleResident = StreamReadWrite.readStringList(ds);
		plan.femaleResident = StreamReadWrite.readStringList(ds);
		plan.startingSubBuildings = StreamReadWrite.readStringList(ds);
		plan.subBuildings = StreamReadWrite.readStringList(ds);
		plan.tags = StreamReadWrite.readStringList(ds);

		return plan;
	}

	public static HashMap<InvItem, Integer> readInventory(final DataInput ds) throws IOException {

		final HashMap<InvItem, Integer> inv = new HashMap<InvItem, Integer>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			InvItem item;
			try {
				item = new InvItem(Item.getItemById(ds.readInt()), ds.readInt());
				inv.put(item, ds.readInt());
			} catch (final MillenaireException e) {
				MLN.printException(e);
			}
		}

		return inv;
	}

	/**
	 * Reads a ItemStack from the InputStream
	 * 
	 * Copied from Packet
	 */
	private static ItemStack readItemStack(final DataInput par1DataInput) throws IOException {
		ItemStack is = null;
		final int id = par1DataInput.readInt();

		if (id >= 0) {
			final byte nb = par1DataInput.readByte();
			final short meta = par1DataInput.readShort();
			is = new ItemStack(Item.getItemById(id), nb, meta);

			if (is.getItem().isDamageable()) {
				is.stackTagCompound = readNBTTagCompound(par1DataInput);
			}
		}

		return is;
	}

	/**
	 * Reads a compressed NBTTagCompound from the InputStream
	 * 
	 * Copied from Packet
	 */
	private static NBTTagCompound readNBTTagCompound(final DataInput par1DataInput) throws IOException {
		final short var2 = par1DataInput.readShort();

		if (var2 < 0) {
			return null;
		} else {
			final byte[] var3 = new byte[var2];
			par1DataInput.readFully(var3);
			return CompressedStreamTools.func_152457_a(var3, new NBTSizeTracker(2097152L));
		}
	}

	public static BuildingLocation readNullableBuildingLocation(final DataInput ds) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		final BuildingLocation bl = new BuildingLocation();
		bl.isCustomBuilding = ds.readBoolean();
		bl.planKey = readNullableString(ds);
		bl.shop = readNullableString(ds);

		bl.maleResident = StreamReadWrite.readStringList(ds);
		bl.femaleResident = StreamReadWrite.readStringList(ds);

		bl.minx = ds.readInt();
		bl.maxx = ds.readInt();
		bl.miny = ds.readInt();
		bl.maxy = ds.readInt();
		bl.minz = ds.readInt();
		bl.maxz = ds.readInt();

		bl.minxMargin = ds.readInt();
		bl.maxxMargin = ds.readInt();
		bl.minyMargin = ds.readInt();
		bl.maxyMargin = ds.readInt();
		bl.minzMargin = ds.readInt();
		bl.maxzMargin = ds.readInt();

		bl.orientation = ds.readInt();
		bl.length = ds.readInt();
		bl.width = ds.readInt();
		bl.level = ds.readInt();
		bl.setVariation(ds.readInt());
		bl.reputation = ds.readInt();
		bl.price = ds.readInt();

		bl.pos = StreamReadWrite.readNullablePoint(ds);
		bl.chestPos = StreamReadWrite.readNullablePoint(ds);
		bl.sleepingPos = StreamReadWrite.readNullablePoint(ds);
		bl.sellingPos = StreamReadWrite.readNullablePoint(ds);
		bl.craftingPos = StreamReadWrite.readNullablePoint(ds);
		bl.shelterPos = StreamReadWrite.readNullablePoint(ds);
		bl.defendingPos = StreamReadWrite.readNullablePoint(ds);

		final String cultureKey = readNullableString(ds);

		bl.culture = Culture.getCultureByName(cultureKey);

		bl.tags = StreamReadWrite.readStringList(ds);
		bl.subBuildings = StreamReadWrite.readStringList(ds);

		bl.showTownHallSigns = ds.readBoolean();
		bl.upgradesAllowed = ds.readBoolean();
		bl.bedrocklevel = ds.readBoolean();

		return bl;
	}

	public static BuildingProject readNullableBuildingProject(final DataInput ds, final Culture culture) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		final BuildingProject bp = new BuildingProject();
		bp.isCustomBuilding = ds.readBoolean();
		bp.key = readNullableString(ds);
		bp.location = readNullableBuildingLocation(ds);
		if (culture != null) {
			if (bp.isCustomBuilding) {
				bp.customBuildingPlan = culture.getBuildingCustom(bp.key);
			} else {
				bp.planSet = culture.getBuildingPlanSet(bp.key);
			}
		}

		return bp;
	}

	public static Goods readNullableGoods(final DataInput ds) throws IOException, MillenaireException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		InvItem iv;
		iv = new InvItem(MillCommonUtilities.getItemById(ds.readInt()), ds.readByte());
		final Goods g = new Goods(iv);
		g.requiredTag = readNullableString(ds);
		g.desc = readNullableString(ds);
		g.autoGenerate = ds.readBoolean();
		g.minReputation = ds.readInt();

		return g;
	}

	public static ItemStack readNullableItemStack(final DataInput ds) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		return readItemStack(ds);
	}

	public static Point readNullablePoint(final DataInput ds) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		final int x = ds.readInt();
		final int y = ds.readInt();
		final int z = ds.readInt();

		return new Point(x, y, z);
	}

	public static QuestInstance readNullableQuestInstance(final MillWorld mw, final DataInput ds) throws IOException {
		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		final long id = ds.readLong();

		final String questKey = ds.readUTF();

		if (!Quest.quests.containsKey(questKey)) {
			return null;
		}

		final Quest quest = Quest.quests.get(questKey);
		final UserProfile profile = mw.getProfile(ds.readUTF());

		final int currentStep = ds.readUnsignedByte();
		final long startTime = ds.readLong();
		final long currentStepStart = ds.readLong();

		final HashMap<String, QuestInstanceVillager> villagers = new HashMap<String, QuestInstanceVillager>();

		final int nb = ds.readUnsignedByte();

		for (int i = 0; i < nb; i++) {
			final String key = ds.readUTF();
			villagers.put(key, StreamReadWrite.readNullableQuestVillager(mw, ds));
		}

		final QuestInstance qi = new QuestInstance(mw, quest, profile, villagers, startTime, currentStep, currentStepStart);

		qi.uniqueid = id;

		return qi;
	}

	public static QuestInstanceVillager readNullableQuestVillager(final MillWorld mw, final DataInput ds) throws IOException {
		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		return new QuestInstanceVillager(mw, StreamReadWrite.readNullablePoint(ds), ds.readLong());
	}

	public static ResourceLocation readNullableResourceLocation(final DataInput ds) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		return new ResourceLocation(Mill.modId, ds.readUTF());
	}

	public static String readNullableString(final DataInput ds) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		return ds.readUTF();
	}

	public static VillagerRecord readNullableVillagerRecord(final MillWorld mw, final DataInput ds) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		final VillagerRecord vr = new VillagerRecord(mw);

		vr.id = ds.readLong();
		vr.type = readNullableString(ds);
		vr.firstName = readNullableString(ds);
		vr.familyName = readNullableString(ds);
		vr.nameKey = readNullableString(ds);
		vr.occupation = readNullableString(ds);
		vr.texture = readNullableResourceLocation(ds);

		vr.nb = ds.readInt();
		vr.gender = ds.readInt();
		vr.villagerSize = ds.readInt();

		vr.culture = Culture.getCultureByName(readNullableString(ds));

		vr.fathersName = readNullableString(ds);
		vr.mothersName = readNullableString(ds);
		vr.spousesName = readNullableString(ds);
		vr.maidenName = readNullableString(ds);

		vr.killed = ds.readBoolean();
		vr.raidingVillage = ds.readBoolean();
		vr.awayraiding = ds.readBoolean();
		vr.awayhired = ds.readBoolean();

		vr.housePos = readNullablePoint(ds);
		vr.townHallPos = readNullablePoint(ds);
		vr.originalVillagePos = readNullablePoint(ds);

		vr.raiderSpawn = ds.readLong();

		vr.inventory = readInventory(ds);
		vr.questTags = readStringList(ds);

		return vr;
	}

	public static PujaSacrifice readOrUpdateNullablePuja(final DataInput ds, final Building b, PujaSacrifice puja) throws IOException {

		final boolean isnull = ds.readBoolean();

		if (isnull) {
			return null;
		}

		final short type = ds.readShort();

		if (puja == null) {
			puja = new PujaSacrifice(b, type);
		}

		final int enchantmentId = ds.readShort();

		for (int i = 0; i < puja.getTargets().size(); i++) {
			if (puja.getTargets().get(i).enchantment.effectId == enchantmentId) {
				puja.currentTarget = puja.getTargets().get(i);
			}
		}

		puja.pujaProgress = ds.readShort();
		puja.offeringNeeded = ds.readShort();
		puja.offeringProgress = ds.readShort();

		return puja;
	}

	public static HashMap<Point, Integer> readPointIntegerMap(final DataInput ds) throws IOException {

		final HashMap<Point, Integer> map = new HashMap<Point, Integer>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			final Point p = readNullablePoint(ds);
			map.put(p, ds.readInt());
		}

		return map;
	}

	public static List<Point> readPointList(final DataInput ds) throws IOException {

		final List<Point> v = new ArrayList<Point>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			v.add(readNullablePoint(ds));
		}

		return v;
	}

	public static Map<BuildingProject.EnumProjects, List<BuildingProject>> readProjectListList(final DataInput ds, final Culture culture) throws IOException {

		final Map<BuildingProject.EnumProjects, List<BuildingProject>> v = new HashMap<BuildingProject.EnumProjects, List<BuildingProject>>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			final int nb2 = ds.readInt();
			final List<BuildingProject> v2 = new ArrayList<BuildingProject>();
			for (int j = 0; j < nb2; j++) {
				v2.add(readNullableBuildingProject(ds, culture));
			}
			v.put(BuildingProject.EnumProjects.getById(i), v2);
		}

		return v;
	}

	public static List<String> readStringList(final DataInput ds) throws IOException {

		final List<String> v = new ArrayList<String>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			v.add(readNullableString(ds));
		}

		return v;
	}

	public static String[][] readStringStringArray(final DataInput ds) throws IOException {

		final String[][] strings = new String[ds.readInt()][];

		for (int i = 0; i < strings.length; i++) {
			final String[] array = new String[ds.readInt()];
			for (int j = 0; j < array.length; j++) {
				array[j] = readNullableString(ds);
			}
			strings[i] = array;
		}

		return strings;
	}

	public static HashMap<String, List<String>> readStringStringListMap(final DataInput ds) throws IOException {

		final HashMap<String, List<String>> v = new HashMap<String, List<String>>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			final String key = ds.readUTF();
			v.put(key, readStringList(ds));
		}

		return v;
	}

	public static HashMap<String, String> readStringStringMap(final DataInput ds) throws IOException {

		final HashMap<String, String> v = new HashMap<String, String>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			final String key = ds.readUTF();
			v.put(key, readNullableString(ds));
		}

		return v;
	}

	public static List<VillagerRecord> readVillagerRecordList(final MillWorld mw, final DataInput ds) throws IOException {

		final List<VillagerRecord> v = new ArrayList<VillagerRecord>();

		final int nb = ds.readInt();

		for (int i = 0; i < nb; i++) {
			v.add(readNullableVillagerRecord(mw, ds));
		}

		return v;
	}

	public static void writeBuildingPlanInfo(final BuildingPlan plan, final DataOutput data) throws IOException {
		data.writeUTF(plan.buildingKey);

		data.writeInt(plan.level);
		data.writeInt(plan.variation);

		StreamReadWrite.writeNullableString(plan.planName, data);
		StreamReadWrite.writeNullableString(plan.nativeName, data);
		StreamReadWrite.writeNullableString(plan.requiredTag, data);
		StreamReadWrite.writeNullableString(plan.shop, data);
		StreamReadWrite.writeNullableString(plan.type, data);
		data.writeInt(plan.price);
		data.writeInt(plan.reputation);
		StreamReadWrite.writeStringList(plan.maleResident, data);
		StreamReadWrite.writeStringList(plan.femaleResident, data);
		StreamReadWrite.writeStringList(plan.startingSubBuildings, data);
		StreamReadWrite.writeStringList(plan.subBuildings, data);
		StreamReadWrite.writeStringList(plan.tags, data);
	}

	public static void writeInventory(final HashMap<InvItem, Integer> inventory, final DataOutput data) throws IOException {
		data.writeInt(inventory.size());

		for (final InvItem key : inventory.keySet()) {
			data.writeInt(Item.getIdFromItem(key.getItem()));
			data.writeInt(key.meta);
			data.writeInt(inventory.get(key));
		}
	}

	/**
	 * Writes the ItemStack's ID (short), then size (byte), then damage. (short)
	 * 
	 * Copied from Packet
	 */
	private static void writeItemStack(final ItemStack par1ItemStack, final DataOutput par2DataOutput) throws IOException {
		if (par1ItemStack == null) {
			par2DataOutput.writeShort(-1);
		} else {
			par2DataOutput.writeInt(Item.getIdFromItem(par1ItemStack.getItem()));
			par2DataOutput.writeByte(par1ItemStack.stackSize);
			par2DataOutput.writeShort(par1ItemStack.getItemDamage());

			if (par1ItemStack.getItem().isDamageable()) {
				writeNBTTagCompound(par1ItemStack.stackTagCompound, par2DataOutput);
			}
		}
	}

	/**
	 * Writes a compressed NBTTagCompound to the OutputStream
	 * 
	 * Copied from Packet
	 */
	private static void writeNBTTagCompound(final NBTTagCompound par1NBTTagCompound, final DataOutput par2DataOutput) throws IOException {
		if (par1NBTTagCompound == null) {
			par2DataOutput.writeShort(-1);
		} else {
			final byte[] var3 = CompressedStreamTools.compress(par1NBTTagCompound);
			par2DataOutput.writeShort((short) var3.length);
			par2DataOutput.write(var3);
		}
	}

	public static void writeNullableBuildingLocation(final BuildingLocation bl, final DataOutput data) throws IOException {

		data.writeBoolean(bl == null);

		if (bl != null) {
			data.writeBoolean(bl.isCustomBuilding);
			writeNullableString(bl.planKey, data);
			writeNullableString(bl.shop, data);
			writeStringList(bl.maleResident, data);
			writeStringList(bl.femaleResident, data);
			data.writeInt(bl.minx);
			data.writeInt(bl.maxx);
			data.writeInt(bl.miny);
			data.writeInt(bl.maxy);
			data.writeInt(bl.minz);
			data.writeInt(bl.maxz);

			data.writeInt(bl.minxMargin);
			data.writeInt(bl.maxxMargin);
			data.writeInt(bl.minyMargin);
			data.writeInt(bl.maxyMargin);
			data.writeInt(bl.minzMargin);
			data.writeInt(bl.maxzMargin);

			data.writeInt(bl.orientation);
			data.writeInt(bl.length);
			data.writeInt(bl.width);
			data.writeInt(bl.level);
			data.writeInt(bl.getVariation());
			data.writeInt(bl.reputation);
			data.writeInt(bl.price);

			StreamReadWrite.writeNullablePoint(bl.pos, data);
			StreamReadWrite.writeNullablePoint(bl.chestPos, data);
			StreamReadWrite.writeNullablePoint(bl.sleepingPos, data);
			StreamReadWrite.writeNullablePoint(bl.sellingPos, data);
			StreamReadWrite.writeNullablePoint(bl.craftingPos, data);
			StreamReadWrite.writeNullablePoint(bl.shelterPos, data);
			StreamReadWrite.writeNullablePoint(bl.defendingPos, data);
			writeNullableString(bl.culture.key, data);
			StreamReadWrite.writeStringList(bl.tags, data);
			StreamReadWrite.writeStringList(bl.subBuildings, data);

			data.writeBoolean(bl.showTownHallSigns);
			data.writeBoolean(bl.upgradesAllowed);
			data.writeBoolean(bl.bedrocklevel);
		}
	}

	public static void writeNullableBuildingProject(final BuildingProject bp, final DataOutput data) throws IOException {

		data.writeBoolean(bp == null);

		if (bp != null) {
			data.writeBoolean(bp.isCustomBuilding);
			writeNullableString(bp.key, data);
			writeNullableBuildingLocation(bp.location, data);
		}
	}

	public static void writeNullableGoods(final Goods g, final DataOutput data) throws IOException {
		data.writeBoolean(g == null);

		if (g != null) {
			data.writeInt(Item.getIdFromItem(g.item.getItem()));
			data.writeByte(g.item.meta);
			writeNullableString(g.requiredTag, data);
			writeNullableString(g.desc, data);
			data.writeBoolean(g.autoGenerate);
			data.writeInt(g.minReputation);
		}
	}

	public static void writeNullableItemStack(final ItemStack is, final DataOutput data) throws IOException {

		data.writeBoolean(is == null);

		if (is != null) {
			writeItemStack(is, data);
		}
	}

	public static void writeNullablePoint(final Point p, final DataOutput data) throws IOException {
		data.writeBoolean(p == null);

		if (p != null) {
			data.writeInt(p.getiX());
			data.writeInt(p.getiY());
			data.writeInt(p.getiZ());
		}
	}

	public static void writeNullablePuja(final PujaSacrifice puja, final DataOutput data) throws IOException {
		data.writeBoolean(puja == null);
		if (puja != null) {

			data.writeShort(puja.type);

			if (puja.currentTarget != null) {
				data.writeShort(puja.currentTarget.enchantment.effectId);
			} else {
				data.writeShort(0);
			}
			data.writeShort(puja.pujaProgress);
			data.writeShort(puja.offeringNeeded);
			data.writeShort(puja.offeringProgress);
		}
	}

	public static void writeNullableQuestInstance(final QuestInstance qi, final DataOutput ds) throws IOException {
		ds.writeBoolean(qi == null);

		if (qi != null) {
			ds.writeLong(qi.uniqueid);
			ds.writeUTF(qi.quest.key);
			ds.writeUTF(qi.profile.key);
			ds.writeByte(qi.currentStep);
			ds.writeLong(qi.startTime);
			ds.writeLong(qi.currentStepStart);

			ds.writeByte(qi.villagers.size());
			for (final String key : qi.villagers.keySet()) {
				ds.writeUTF(key);
				StreamReadWrite.writeNullableQuestVillager(qi.villagers.get(key), ds);
			}
		}
	}

	public static void writeNullableQuestVillager(final QuestInstanceVillager v, final DataOutput data) throws IOException {
		data.writeBoolean(v == null);

		if (v != null) {
			StreamReadWrite.writeNullablePoint(v.townHall, data);
			data.writeLong(v.id);
		}
	}

	public static void writeNullableResourceLocation(final ResourceLocation rs, final DataOutput data) throws IOException {

		data.writeBoolean(rs == null);

		if (rs != null) {
			data.writeUTF(rs.getResourcePath());
		}
	}

	public static void writeNullableString(final String s, final DataOutput data) throws IOException {

		data.writeBoolean(s == null);

		if (s != null) {
			data.writeUTF(s);
		}
	}

	public static void writeNullableVillagerRecord(final VillagerRecord vr, final DataOutput data) throws IOException {

		data.writeBoolean(vr == null);

		if (vr != null) {

			data.writeLong(vr.id);

			writeNullableString(vr.type, data);
			writeNullableString(vr.firstName, data);
			writeNullableString(vr.familyName, data);
			writeNullableString(vr.nameKey, data);
			writeNullableString(vr.occupation, data);
			writeNullableResourceLocation(vr.texture, data);

			data.writeInt(vr.nb);
			data.writeInt(vr.gender);
			data.writeInt(vr.villagerSize);

			writeNullableString(vr.culture.key, data);

			writeNullableString(vr.fathersName, data);
			writeNullableString(vr.mothersName, data);
			writeNullableString(vr.spousesName, data);
			writeNullableString(vr.maidenName, data);

			data.writeBoolean(vr.killed);
			data.writeBoolean(vr.raidingVillage);
			data.writeBoolean(vr.awayraiding);
			data.writeBoolean(vr.awayhired);

			writeNullablePoint(vr.housePos, data);
			writeNullablePoint(vr.townHallPos, data);
			writeNullablePoint(vr.originalVillagePos, data);

			data.writeLong(vr.raiderSpawn);
			writeInventory(vr.inventory, data);
			writeStringList(vr.questTags, data);
		}
	}

	public static void writePointIntegerMap(final HashMap<Point, Integer> map, final DataOutput data) throws IOException {
		data.writeInt(map.size());

		for (final Point p : map.keySet()) {
			writeNullablePoint(p, data);
			data.writeInt(map.get(p));
		}
	}

	public static void writePointList(final List<Point> points, final DataOutput data) throws IOException {
		data.writeInt(points.size());

		for (final Point p : points) {
			writeNullablePoint(p, data);
		}
	}

	public static void writeProjectListList(final Map<EnumProjects, List<BuildingProject>> projects, final DataOutput data) throws IOException {

		data.writeInt(EnumProjects.values().length);

		for (final EnumProjects ep : EnumProjects.values()) {
			if (projects.containsKey(ep)) {
				data.writeInt(projects.get(ep).size());
				for (final BuildingProject bp : projects.get(ep)) {
					writeNullableBuildingProject(bp, data);
				}
			} else {
				data.writeInt(0);
			}
		}

	}

	public static void writeStringList(final List<String> strings, final DataOutput data) throws IOException {
		data.writeInt(strings.size());

		for (final String s : strings) {
			writeNullableString(s, data);
		}
	}

	public static void writeStringStringArray(final String[][] strings, final DataOutput data) throws IOException {
		data.writeInt(strings.length);

		for (final String[] array : strings) {
			data.writeInt(array.length);
			for (final String s : array) {
				writeNullableString(s, data);
			}
		}
	}

	public static void writeStringStringListMap(final Map<String, List<String>> strings, final DataOutput data) throws IOException {

		if (strings == null) {
			data.writeInt(0);
			return;
		}

		data.writeInt(strings.size());

		for (final String key : strings.keySet()) {
			data.writeUTF(key);
			writeStringList(strings.get(key), data);
		}
	}

	public static void writeStringStringMap(final Map<String, String> strings, final DataOutput data) throws IOException {

		if (strings == null) {
			data.writeInt(0);
			return;
		}

		data.writeInt(strings.size());

		for (final String s : strings.keySet()) {
			data.writeUTF(s);
			writeNullableString(strings.get(s), data);
		}
	}

	public static void writeVillagerRecordList(final List<VillagerRecord> vrecords, final DataOutput data) throws IOException {
		data.writeInt(vrecords.size());

		for (final VillagerRecord vr : vrecords) {
			writeNullableVillagerRecord(vr, data);
		}
	}
}
