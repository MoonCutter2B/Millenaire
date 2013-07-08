package org.millenaire.common.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import org.millenaire.common.Building;
import org.millenaire.common.BuildingLocation;
import org.millenaire.common.Culture;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.PujaSacrifice;
import org.millenaire.common.Quest;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.Quest.QuestInstanceVillager;
import org.millenaire.common.UserProfile;
import org.millenaire.common.VillagerRecord;
import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.construction.BuildingProject;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;


public class StreamReadWrite  {


	static public Random random;
	public static BuildingPlan readBuildingPlanInfo(DataInput ds,Culture culture) throws IOException {

		final String key=ds.readUTF();
		final int level=ds.readInt();
		final int variation=ds.readInt();

		final BuildingPlan plan=new BuildingPlan(key,level,variation,culture);

		plan.planName=StreamReadWrite.readNullableString(ds);
		plan.nativeName=StreamReadWrite.readNullableString(ds);
		plan.requiredTag=StreamReadWrite.readNullableString(ds);
		plan.shop=StreamReadWrite.readNullableString(ds);
		plan.type=StreamReadWrite.readNullableString(ds);
		plan.price=ds.readInt();
		plan.reputation=ds.readInt();
		plan.maleResident=StreamReadWrite.readStringVector(ds);
		plan.femaleResident=StreamReadWrite.readStringVector(ds);
		plan.startingSubBuildings=StreamReadWrite.readStringVector(ds);
		plan.subBuildings=StreamReadWrite.readStringVector(ds);
		plan.tags=StreamReadWrite.readStringVector(ds);

		return plan;
	}

	public static HashMap<InvItem,Integer> readInventory(DataInput ds) throws IOException {

		final HashMap<InvItem,Integer> inv=new HashMap<InvItem,Integer>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			final InvItem item=new InvItem(ds.readInt(),ds.readInt());

			inv.put(item, ds.readInt());
		}

		return inv;
	}

	/**
	 * Reads a ItemStack from the InputStream
	 * 
	 * Copied from Packet
	 */
	private static ItemStack readItemStack(DataInput par1DataInput) throws IOException
	{
		ItemStack is = null;
		final short id = par1DataInput.readShort();

		if (id >= 0)
		{
			final byte nb = par1DataInput.readByte();
			final short meta = par1DataInput.readShort();
			is = new ItemStack(id, nb, meta);

			if (Item.itemsList[id].isDamageable())
			{
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
	private static NBTTagCompound readNBTTagCompound(DataInput par1DataInput) throws IOException
	{
		final short var2 = par1DataInput.readShort();

		if (var2 < 0)
			return null;
		else
		{
			final byte[] var3 = new byte[var2];
			par1DataInput.readFully(var3);
			return CompressedStreamTools.decompress(var3);
		}
	}

	public static BuildingLocation readNullableBuildingLocation(DataInput ds) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		final BuildingLocation bl=new BuildingLocation();

		bl.key=readNullableString(ds);
		bl.shop=readNullableString(ds);

		bl.maleResident=StreamReadWrite.readStringVector(ds);
		bl.femaleResident=StreamReadWrite.readStringVector(ds);

		bl.minx=ds.readInt();
		bl.maxx=ds.readInt();
		bl.minz=ds.readInt();
		bl.maxz=ds.readInt();

		bl.minxMargin=ds.readInt();
		bl.maxxMargin=ds.readInt();
		bl.minzMargin=ds.readInt();
		bl.maxzMargin=ds.readInt();

		bl.orientation=ds.readInt();
		bl.length=ds.readInt();
		bl.width=ds.readInt();
		bl.areaToClear=ds.readInt();
		bl.level=ds.readInt();
		bl.setVariation(ds.readInt());
		bl.reputation=ds.readInt();
		bl.price=ds.readInt();

		bl.pos=StreamReadWrite.readNullablePoint(ds);
		bl.chestPos=StreamReadWrite.readNullablePoint(ds);
		bl.sleepingPos=StreamReadWrite.readNullablePoint(ds);
		bl.sellingPos=StreamReadWrite.readNullablePoint(ds);
		bl.craftingPos=StreamReadWrite.readNullablePoint(ds);
		bl.shelterPos=StreamReadWrite.readNullablePoint(ds);
		bl.defendingPos=StreamReadWrite.readNullablePoint(ds);

		final String cultureKey=readNullableString(ds);

		bl.culture=Culture.getCultureByName(cultureKey);

		bl.tags=StreamReadWrite.readStringVector(ds);
		bl.subBuildings=StreamReadWrite.readStringVector(ds);

		bl.showTownHallSigns=ds.readBoolean();
		bl.upgradesAllowed=ds.readBoolean();
		bl.bedrocklevel=ds.readBoolean();

		return bl;
	}

	public static BuildingProject readNullableBuildingProject(DataInput ds,Culture culture) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		final BuildingProject bp=new BuildingProject();

		bp.key=readNullableString(ds);
		bp.location=readNullableBuildingLocation(ds);
		if (culture!=null) {
			bp.planSet=culture.getBuildingPlanSet(bp.key);
		}

		return bp;
	}

	public static ItemStack readNullableItemStack(DataInput ds) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		return readItemStack(ds);
	}

	public static Point readNullablePoint(DataInput ds) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		final int x=ds.readInt();
		final int y=ds.readInt();
		final int z=ds.readInt();

		return new Point(x,y,z);
	}

	public static QuestInstance readNullableQuestInstance(MillWorld mw,DataInput ds) throws IOException {
		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		final long id=ds.readLong();

		final String questKey=ds.readUTF();

		if (!Quest.quests.containsKey(questKey))
			return null;

		final Quest quest=Quest.quests.get(questKey);
		final UserProfile profile=mw.getProfile(ds.readUTF());

		final int currentStep=ds.readUnsignedByte();
		final long startTime=ds.readLong();
		final long currentStepStart=ds.readLong();

		final HashMap<String,QuestInstanceVillager> villagers=new HashMap<String,QuestInstanceVillager>();

		final int nb=ds.readUnsignedByte();

		for (int i=0;i<nb;i++) {
			final String key=ds.readUTF();
			villagers.put(key, StreamReadWrite.readNullableQuestVillager(mw,ds));
		}

		final QuestInstance qi=new QuestInstance(mw, quest, profile, villagers, startTime, currentStep, currentStepStart);

		qi.uniqueid=id;

		return qi;
	}

	public static QuestInstanceVillager readNullableQuestVillager(MillWorld mw,DataInput ds) throws IOException {
		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		return new QuestInstanceVillager(mw,StreamReadWrite.readNullablePoint(ds),ds.readLong());
	}

	public static String readNullableString(DataInput ds) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		return ds.readUTF();
	}
	
	public static ResourceLocation readNullableResourceLocation(DataInput ds) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		return new ResourceLocation(Mill.modId,ds.readUTF());
	}

	public static VillagerRecord readNullableVillagerRecord(MillWorld mw,DataInput ds) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		final VillagerRecord vr=new VillagerRecord(mw);

		vr.id=ds.readLong();
		vr.type=readNullableString(ds);
		vr.firstName=readNullableString(ds);
		vr.familyName=readNullableString(ds);
		vr.nameKey=readNullableString(ds);
		vr.occupation=readNullableString(ds);
		vr.texture=readNullableResourceLocation(ds);

		vr.nb=ds.readInt();
		vr.gender=ds.readInt();
		vr.villagerSize=ds.readInt();

		vr.culture=Culture.getCultureByName(readNullableString(ds));

		vr.fathersName=readNullableString(ds);
		vr.mothersName=readNullableString(ds);
		vr.spousesName=readNullableString(ds);
		vr.maidenName=readNullableString(ds);

		vr.killed=ds.readBoolean();
		vr.raidingVillage=ds.readBoolean();
		vr.awayraiding=ds.readBoolean();
		vr.awayhired=ds.readBoolean();

		vr.housePos=readNullablePoint(ds);
		vr.townHallPos=readNullablePoint(ds);
		vr.originalVillagePos=readNullablePoint(ds);

		vr.raiderSpawn=ds.readLong();

		vr.inventory=readInventory(ds);
		vr.questTags=readStringVector(ds);

		return vr;
	}

	public static PujaSacrifice readOrUpdateNullablePuja(DataInput ds,Building b,PujaSacrifice puja) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		short type=ds.readShort();

		if (puja==null) {
			puja=new PujaSacrifice(b,type);
		}

		final int enchantmentId=ds.readShort();

		for (int i=0;i<puja.getTargets().size();i++) {
			if (puja.getTargets().get(i).enchantment.effectId==enchantmentId) {
				puja.currentTarget=puja.getTargets().get(i);
			}
		}

		puja.pujaProgress=ds.readShort();
		puja.offeringNeeded=ds.readShort();
		puja.offeringProgress=ds.readShort();

		return puja;
	}

	public static HashMap<Point,Integer> readPointIntegerMap(DataInput ds) throws IOException {

		final HashMap<Point,Integer> map=new HashMap<Point,Integer>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			final Point p=readNullablePoint(ds);
			map.put(p, ds.readInt());
		}

		return map;
	}

	public static Vector<Point> readPointVector(DataInput ds) throws IOException {

		final Vector<Point> v=new Vector<Point>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			v.add(readNullablePoint(ds));
		}

		return v;
	}

	public static Vector<Vector<BuildingProject>> readProjectVectorVector(DataInput ds,Culture culture) throws IOException {

		final Vector<Vector<BuildingProject>> v=new Vector<Vector<BuildingProject>>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			final int nb2=ds.readInt();
			final Vector<BuildingProject> v2= new Vector<BuildingProject>();
			for (int j=0;j<nb2;j++) {
				v2.add(readNullableBuildingProject(ds,culture));
			}
			v.add(v2);
		}

		return v;
	}

	public static String[][] readStringStringArray(DataInput ds) throws IOException {

		final String[][] strings=new String[ds.readInt()][];

		for (int i=0;i<strings.length;i++) {
			final String[] array=new String[ds.readInt()];
			for (int j=0;j<array.length;j++) {
				array[j]=readNullableString(ds);
			}
			strings[i]=array;
		}

		return strings;
	}

	public static HashMap<String,String> readStringStringMap(DataInput ds) throws IOException {

		final HashMap<String,String> v=new HashMap<String,String>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			final String key=ds.readUTF();
			v.put(key, readNullableString(ds));
		}

		return v;
	}

	public static HashMap<String,Vector<String>> readStringStringVectorMap(DataInput ds) throws IOException {

		final HashMap<String,Vector<String>> v=new HashMap<String,Vector<String>>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			final String key=ds.readUTF();
			v.put(key, readStringVector(ds));
		}

		return v;
	}

	public static Vector<String> readStringVector(DataInput ds) throws IOException {

		final Vector<String> v=new Vector<String>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			v.add(readNullableString(ds));
		}

		return v;
	}

	public static Vector<VillagerRecord> readVillagerRecordVector(MillWorld mw,DataInput ds) throws IOException {

		final Vector<VillagerRecord> v=new Vector<VillagerRecord>();

		final int nb=ds.readInt();

		for (int i=0;i<nb;i++) {
			v.add(readNullableVillagerRecord(mw,ds));
		}

		return v;
	}

	public static void writeBuildingPlanInfo(BuildingPlan plan,DataOutput data) throws IOException {
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
		StreamReadWrite.writeStringVector(plan.maleResident, data);
		StreamReadWrite.writeStringVector(plan.femaleResident, data);
		StreamReadWrite.writeStringVector(plan.startingSubBuildings, data);
		StreamReadWrite.writeStringVector(plan.subBuildings, data);
		StreamReadWrite.writeStringVector(plan.tags, data);
	}

	public static void writeInventory(HashMap<InvItem,Integer> inventory,DataOutput data) throws IOException {
		data.writeInt(inventory.size());

		for (final InvItem key : inventory.keySet()) {
			data.writeInt(key.id());
			data.writeInt(key.meta);
			data.writeInt(inventory.get(key));
		}
	}


	/**
	 * Writes the ItemStack's ID (short), then size (byte), then damage. (short)
	 * 
	 * Copied from Packet
	 */
	private static void writeItemStack(ItemStack par1ItemStack, DataOutput par2DataOutput) throws IOException
	{
		if (par1ItemStack == null)
		{
			par2DataOutput.writeShort(-1);
		}
		else
		{
			par2DataOutput.writeShort(par1ItemStack.itemID);
			par2DataOutput.writeByte(par1ItemStack.stackSize);
			par2DataOutput.writeShort(par1ItemStack.getItemDamage());

			if (par1ItemStack.getItem().isDamageable())
			{
				writeNBTTagCompound(par1ItemStack.stackTagCompound, par2DataOutput);
			}
		}
	}

	/**
	 * Writes a compressed NBTTagCompound to the OutputStream
	 * 
	 * Copied from Packet
	 */
	private static void writeNBTTagCompound(NBTTagCompound par1NBTTagCompound, DataOutput par2DataOutput) throws IOException
	{
		if (par1NBTTagCompound == null)
		{
			par2DataOutput.writeShort(-1);
		}
		else
		{
			final byte[] var3 = CompressedStreamTools.compress(par1NBTTagCompound);
			par2DataOutput.writeShort((short)var3.length);
			par2DataOutput.write(var3);
		}
	}

	public static void writeNullableBuildingLocation(BuildingLocation bl, DataOutput data) throws IOException {

		data.writeBoolean(bl==null);

		if (bl!=null) {
			writeNullableString(bl.key,data);
			writeNullableString(bl.shop,data);
			writeStringVector(bl.maleResident, data);
			writeStringVector(bl.femaleResident, data);
			data.writeInt(bl.minx);
			data.writeInt(bl.maxx);
			data.writeInt(bl.minz);
			data.writeInt(bl.maxz);

			data.writeInt(bl.minxMargin);
			data.writeInt(bl.maxxMargin);
			data.writeInt(bl.minzMargin);
			data.writeInt(bl.maxzMargin);

			data.writeInt(bl.orientation);
			data.writeInt(bl.length);
			data.writeInt(bl.width);
			data.writeInt(bl.areaToClear);
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
			writeNullableString(bl.culture.key,data);
			StreamReadWrite.writeStringVector(bl.tags, data);
			StreamReadWrite.writeStringVector(bl.subBuildings, data);

			data.writeBoolean(bl.showTownHallSigns);
			data.writeBoolean(bl.upgradesAllowed);
			data.writeBoolean(bl.bedrocklevel);
		}
	}

	public static void writeNullableBuildingProject(BuildingProject bp,DataOutput data) throws IOException {

		data.writeBoolean(bp==null);

		if (bp!=null) {
			writeNullableString(bp.key,data);
			writeNullableBuildingLocation(bp.location,data);
		}
	}

	public static void writeNullableItemStack(ItemStack is,DataOutput data) throws IOException {

		data.writeBoolean(is==null);

		if (is!=null) {
			writeItemStack(is,data);
		}
	}

	public static void writeNullablePoint(Point p,DataOutput data) throws IOException {
		data.writeBoolean(p==null);

		if (p!=null) {
			data.writeInt(p.getiX());
			data.writeInt(p.getiY());
			data.writeInt(p.getiZ());
		}
	}

	public static void writeNullableGoods(Goods g,DataOutput data) throws IOException {
		data.writeBoolean(g==null);

		if (g!=null) {
			data.writeInt(g.item.id());
			data.writeByte(g.item.meta);
			writeNullableString(g.requiredTag, data);
			writeNullableString(g.desc, data);
			data.writeBoolean(g.autoGenerate);
			data.writeInt(g.minReputation);
		}
	}

	public static Goods readNullableGoods(DataInput ds) throws IOException {

		final boolean isnull=ds.readBoolean();

		if (isnull)
			return null;

		InvItem iv=new InvItem(ds.readInt(),ds.readByte());
		Goods g=new Goods(iv);

		g.requiredTag=readNullableString(ds);
		g.desc=readNullableString(ds);
		g.autoGenerate=ds.readBoolean();
		g.minReputation=ds.readInt();

		return g;
	}

	public static void writeNullablePuja(PujaSacrifice puja,DataOutput data) throws IOException {
		data.writeBoolean(puja==null);
		if (puja!=null) {

			data.writeShort(puja.type);

			if (puja.currentTarget!=null) {
				data.writeShort(puja.currentTarget.enchantment.effectId);
			} else {
				data.writeShort(0);
			}
			data.writeShort(puja.pujaProgress);
			data.writeShort(puja.offeringNeeded);
			data.writeShort(puja.offeringProgress);
		}
	}

	public static void writeNullableQuestInstance(QuestInstance qi,DataOutput ds) throws IOException {
		ds.writeBoolean(qi==null);

		if (qi!=null) {
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

	public static void writeNullableQuestVillager(QuestInstanceVillager v,DataOutput data) throws IOException {
		data.writeBoolean(v==null);

		if (v!=null) {
			StreamReadWrite.writeNullablePoint(v.townHall, data);
			data.writeLong(v.id);
		}
	}

	public static void writeNullableString(String s,DataOutput data) throws IOException {

		data.writeBoolean(s==null);

		if (s!=null) {
			data.writeUTF(s);
		}
	}
	

	public static void writeNullableResourceLocation(ResourceLocation rs,DataOutput data) throws IOException {

		data.writeBoolean(rs==null);

		if (rs!=null) {
			data.writeUTF(rs.func_110623_a());
		}
	}

	public static void writeNullableVillagerRecord(VillagerRecord vr,DataOutput data) throws IOException {

		data.writeBoolean(vr==null);

		if (vr!=null) {

			data.writeLong(vr.id);

			writeNullableString(vr.type,data);
			writeNullableString(vr.firstName,data);
			writeNullableString(vr.familyName,data);
			writeNullableString(vr.nameKey,data);
			writeNullableString(vr.occupation,data);
			writeNullableResourceLocation(vr.texture,data);

			data.writeInt(vr.nb);
			data.writeInt(vr.gender);
			data.writeInt(vr.villagerSize);

			writeNullableString(vr.culture.key,data);

			writeNullableString(vr.fathersName,data);
			writeNullableString(vr.mothersName,data);
			writeNullableString(vr.spousesName,data);
			writeNullableString(vr.maidenName,data);

			data.writeBoolean(vr.killed);
			data.writeBoolean(vr.raidingVillage);
			data.writeBoolean(vr.awayraiding);
			data.writeBoolean(vr.awayhired);

			writeNullablePoint(vr.housePos,data);
			writeNullablePoint(vr.townHallPos,data);
			writeNullablePoint(vr.originalVillagePos,data);

			data.writeLong(vr.raiderSpawn);
			writeInventory(vr.inventory,data);
			writeStringVector(vr.questTags,data);
		}
	}

	public static void writePointIntegerMap(HashMap<Point,Integer> map,DataOutput data) throws IOException {
		data.writeInt(map.size());

		for (final Point p : map.keySet()) {
			writeNullablePoint(p,data);
			data.writeInt(map.get(p));
		}
	}

	public static void writePointVector(Vector<Point> points,DataOutput data) throws IOException {
		data.writeInt(points.size());

		for (final Point p : points) {
			writeNullablePoint(p,data);
		}
	}

	public static void writeProjectVectorVector(Vector<Vector<BuildingProject>> projects,DataOutput data) throws IOException {
		data.writeInt(projects.size());

		for (final Vector<BuildingProject> vp : projects) {
			data.writeInt(vp.size());
			for (final BuildingProject bp : vp) {
				writeNullableBuildingProject(bp,data);
			}
		}
	}

	public static void writeStringStringArray(String[][] strings,DataOutput data) throws IOException {
		data.writeInt(strings.length);

		for (final String[] array : strings) {
			data.writeInt(array.length);
			for (final String s : array) {
				writeNullableString(s,data);
			}
		}
	}

	public static void writeStringStringMap(HashMap<String,String> strings,DataOutput data) throws IOException {

		if (strings==null) {
			data.writeInt(0);
			return;
		}


		data.writeInt(strings.size());

		for (final String s : strings.keySet()) {
			data.writeUTF(s);
			writeNullableString(strings.get(s),data);
		}
	}

	public static void writeStringStringVectorMap(HashMap<String,Vector<String>> strings,DataOutput data) throws IOException {

		if (strings==null) {
			data.writeInt(0);
			return;
		}


		data.writeInt(strings.size());

		for (final String key : strings.keySet()) {
			data.writeUTF(key);
			writeStringVector(strings.get(key),data);
		}
	}

	public static void writeStringVector(Vector<String> strings,DataOutput data) throws IOException {
		data.writeInt(strings.size());

		for (final String s : strings) {
			writeNullableString(s,data);
		}
	}

	public static void writeVillagerRecordVector(Vector<VillagerRecord> vrecords,DataOutput data) throws IOException {
		data.writeInt(vrecords.size());

		for (final VillagerRecord vr : vrecords) {
			writeNullableVillagerRecord(vr,data);
		}
	}
}
