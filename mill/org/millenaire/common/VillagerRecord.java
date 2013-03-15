package org.millenaire.common;

import java.util.HashMap;
import java.util.Vector;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.forge.Mill;

public class VillagerRecord implements Cloneable {

	public static VillagerRecord read(MillWorld mw,Culture thCulture, Point th,
			NBTTagCompound nbttagcompound, String label) {

		if (!nbttagcompound.hasKey(label + "_id")
				&& !nbttagcompound.hasKey(label + "_lid"))
			return null;

		VillagerRecord vr;

		if (nbttagcompound.hasKey(label + "_culture")) {
			vr = new VillagerRecord(mw,Culture.getCultureByName(nbttagcompound.getString(label + "_culture")));
		} else {
			vr = new VillagerRecord(mw,thCulture);//for pre-2.3, when culture was assumed to match the village's
		}

		if (nbttagcompound.hasKey(label + "_lid")) {
			vr.id = Math.abs(nbttagcompound.getLong(label + "_lid"));
		}

		vr.nb = nbttagcompound.getInteger(label + "_nb");
		vr.gender = nbttagcompound.getInteger(label + "_gender");
		vr.type = nbttagcompound.getString(label + "_type").toLowerCase();

		// Conversion code for old buildings/villagers
		if ((vr.gender > 0) && MillVillager.oldVillagers.containsKey(vr.type)) {
			if (vr.gender == MillVillager.MALE) {
				vr.type = MillVillager.oldVillagers.get(vr.type)[0];
			} else {
				vr.type = MillVillager.oldVillagers.get(vr.type)[1];
			}
		}

		vr.raiderSpawn = nbttagcompound.getLong(label + "_raiderSpawn");
		vr.firstName = nbttagcompound.getString(label + "_firstName");
		vr.familyName = nbttagcompound.getString(label + "_familyName");
		vr.nameKey = nbttagcompound.getString(label + "_propertype");
		vr.occupation = nbttagcompound.getString(label + "_occupation");
		vr.texture = nbttagcompound.getString(label + "_texture");
		vr.housePos = Point.read(nbttagcompound, label + "_housePos");
		vr.townHallPos = Point.read(nbttagcompound, label + "_townHallPos");
		vr.originalVillagePos = Point.read(nbttagcompound, label + "_originalVillagePos");

		if (vr.townHallPos == null) {
			vr.townHallPos = th;
		}

		vr.villagerSize = nbttagcompound.getInteger(label + "_size");

		vr.fathersName = nbttagcompound.getString(label + "_fathersName");
		vr.mothersName = nbttagcompound.getString(label + "_mothersName");
		vr.maidenName = nbttagcompound.getString(label + "_maidenName");
		vr.spousesName = nbttagcompound.getString(label + "_spousesName");
		vr.killed = nbttagcompound.getBoolean(label + "_killed");
		vr.raidingVillage = nbttagcompound.getBoolean(label + "_raidingVillage");
		vr.awayraiding = nbttagcompound.getBoolean(label + "_awayraiding");
		vr.awayhired = nbttagcompound.getBoolean(label + "_awayhired");

		NBTTagList nbttaglist = nbttagcompound
				.getTagList(label + "questTags");
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist
					.tagAt(i);
			vr.questTags.add(nbttagcompound1.getString("tag"));
		}

		nbttaglist = nbttagcompound.getTagList(label + "_inventory");
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			vr.inventory.put(new InvItem(nbttagcompound1.getInteger("item"),nbttagcompound1.getInteger("meta")), nbttagcompound1.getInteger("amount"));
		}
		
		if (vr.getType()==null) {
			MLN.error(vr, "Could not find type "+vr.type+" for VR. Skipping.");
			return null;
		}

		return vr;
	}

	public Culture culture;

	public String fathersName = "", mothersName = "", spousesName = "",
			maidenName = "";

	public boolean flawedRecord = false;
	public boolean killed = false;
	public boolean raidingVillage = false;
	public boolean awayraiding = false;
	public boolean awayhired = false;
	public Point housePos, townHallPos, originalVillagePos;
	public long id,raiderSpawn=0;
	public int nb, gender, villagerSize;
	public HashMap<InvItem,Integer> inventory=new HashMap<InvItem,Integer>();

	public Vector<String> questTags = new Vector<String>();

	public String type, firstName, familyName, nameKey, occupation, texture;

	private Building house;

	private Building townHall;
	private Building originalVillage;

	public MillWorld mw;

	public VillagerRecord(MillWorld mw) {
		this.mw=mw;
	}

	private VillagerRecord(MillWorld mw,Culture c) {
		culture=c;
		this.mw=mw;
	}

	public VillagerRecord(MillWorld mw,MillVillager v) {
		this.mw=mw;
		culture = v.getCulture();
		id = v.villager_id;
		if (v.vtype!=null) {
			type = v.vtype.key;
		}
		firstName = v.firstName;
		familyName = v.familyName;
		nameKey = v.getNameKey();
		occupation = v.getNativeOccupationName();
		gender = v.gender;
		nb = 1;
		texture = v.getTexture();
		housePos = v.housePoint;
		townHallPos = v.townHallPoint;
		villagerSize = v.size;
		raidingVillage = v.isRaider;

		for (final InvItem iv : v.getInventoryKeys()) {
			inventory.put(iv, v.countInv(iv));
		}

		if (housePos == null) {
			MLN.error(this,
					"Creation constructor: House position in record is null.");
			flawedRecord = true;
		}
	}

	@Override
	public VillagerRecord clone() {
		try {
			return (VillagerRecord) super.clone();
		} catch (final CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int countInv(int id) {
		return countInv(id,0);
	}

	public int countInv(int id,int meta) {
		final InvItem key=new InvItem(id,meta);
		if (inventory.containsKey(key))
			return inventory.get(key);
		else
			return 0;
	}

	public int countInv(InvItem key) {
		if (inventory.containsKey(key))
			return inventory.get(key);
		else
			return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VillagerRecord))
			return false;
		final VillagerRecord other = (VillagerRecord) obj;

		return other.id == id;
	}

	public ItemStack getArmourPiece(int type) {

		if (type==0) {
			for (final Item weapon : MillVillager.helmets) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}
		if (type==1) {
			for (final Item weapon : MillVillager.chestplates) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}
		if (type==2) {
			for (final Item weapon : MillVillager.legs) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}
		if (type==3) {
			for (final Item weapon : MillVillager.boots) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}

		return null;
	}

	public Item getBestMeleeWeapon() {

		int max=1;
		Item best=null;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item)>0) {
				if (Item.itemsList[item.id()]==null) {
					MLN.error(this, "Attempting to check null melee weapon with id: "+inventory.get(item));
				} else {
					if (Item.itemsList[item.id()].getDamageVsEntity(null)>max) {
						max=Item.itemsList[item.id()].getDamageVsEntity(null);
						best=Item.itemsList[item.id()];
					}
				}
			}
		}

		if ((getType()!=null) && (getType().startingWeapon!=null)) {
			if (Item.itemsList[getType().startingWeapon.id()].getDamageVsEntity(null)>max) {
				max=Item.itemsList[getType().startingWeapon.id()].getDamageVsEntity(null);
				best=Item.itemsList[getType().startingWeapon.id()];
			}
		}

		return best;
	}

	public String getGameOccupation(String username) {

		if ((culture==null) || (culture.getVillagerType(nameKey)==null))
			return "";

		String s = culture.getVillagerType(nameKey).name;

		if (culture.canReadVillagerNames(username)) {

			final String game = culture.getCultureString("villager." + nameKey);

			if (!game.equals("")) {
				s = s + " (" + game + ")";
			}
		}

		return s;
	}

	public Building getHouse() {
		if (house != null)
			return house;
		if ((MLN.LogVillager>=MLN.DEBUG)) {
			MLN.debug(this, "Seeking uncached house");
		}
		house=mw.getBuilding(housePos);

		return house;
	}

	public int getMaxHealth() {

		if (getType()==null)
			return 20;

		if (getType().isChild)
			return 10+(villagerSize/2);

		return getType().health;
	}

	public int getMilitaryStrength() {

		int strength=getMaxHealth()/2;


		int attack=getType().baseAttackStrength;

		final Item bestMelee=getBestMeleeWeapon();

		if (bestMelee!=null) {
			attack+=bestMelee.getDamageVsEntity(null);
		}

		strength+=attack*2;

		if ((getType().isArcher && (countInv(Item.bow.itemID)>0)) || (countInv(Mill.yumiBow.itemID)>0)) {
			strength+=10;
		}

		strength+=getTotalArmorValue()*2;

		return strength;
	}

	public String getName() {
		return firstName + " " + familyName;
	}

	public String getNativeOccupationName() {
		if (getType().isChild && (villagerSize == MillVillager.max_child_size))
			return getType().altname;
		return getType().name;
	}

	public Building getOriginalVillage() {
		if (originalVillage != null)
			//Log.debug(Log.Villager, "Seeking cached townHall");
			return originalVillage;

		if ((MLN.LogVillager>=MLN.DEBUG)) {
			MLN.debug(this, "Seeking uncached originalVillage");
		}
		originalVillage=mw.getBuilding(originalVillagePos);

		return originalVillage;
	}


	public int getTotalArmorValue()
	{
		int total = 0;
		for (int i = 0; i < 4; i++)
		{
			final ItemStack armour=getArmourPiece(i);

			if ((armour != null) && (armour.getItem() instanceof ItemArmor))
			{
				total += ((ItemArmor)armour.getItem()).damageReduceAmount;
			}
		}
		return total;
	}

	public Building getTownHall() {
		if (townHall != null)
			//Log.debug(Log.Villager, "Seeking cached townHall");
			return townHall;

		if ((MLN.LogVillager>=MLN.DEBUG)) {
			MLN.debug(this, "Seeking uncached townHall");
		}
		townHall=mw.getBuilding(townHallPos);

		return townHall;
	}

	public VillagerType getType() {
		if (culture.getVillagerType(type)==null) {
			for (final Culture c : Culture.vectorCultures) {
				if (c.getVillagerType(type)!=null) {
					MLN.error(this, "Could not find villager type " + type
							+ " in culture " + culture.key+" but could in "+c.key+" so switching.");
					culture=c;
				}
			}
		}

		return culture.getVillagerType(type);
	}

	public boolean matches(MillVillager v) {
		return (id == v.villager_id);
	}

	@Override
	public String toString() {
		return firstName + " " + familyName + "/" + type + "/" + nameKey
				+ "/" + texture + "/" + id;
	}

	public void updateRecord(MillVillager v) {
		id = v.villager_id;
		if (v.vtype!=null) {
			type = v.vtype.key;
		}
		firstName = v.firstName;
		familyName = v.familyName;
		nameKey = v.getNameKey();
		occupation = v.getNativeOccupationName();
		gender = v.gender;
		nb = 1;
		texture = v.getTexture();
		housePos = v.housePoint;
		townHallPos = v.townHallPoint;
		villagerSize = v.size;
		raidingVillage=v.isRaider;
		killed=v.isDead;

		if (housePos == null) {
			MLN.error(this,
					"updateRecord(): House position in record is null.");
			flawedRecord = true;
		}

		inventory.clear();
		for (final InvItem iv : v.getInventoryKeys()) {
			inventory.put(iv, v.countInv(iv));
		}
	}

	public void write(NBTTagCompound nbttagcompound, String label) {
		nbttagcompound.setLong(label + "_lid", id);
		nbttagcompound.setInteger(label + "_nb", nb);
		nbttagcompound.setString(label + "_type", type);
		nbttagcompound.setString(label + "_firstName", firstName);
		nbttagcompound.setString(label + "_familyName", familyName);
		nbttagcompound.setString(label + "_propertype", nameKey);
		nbttagcompound.setString(label + "_occupation", occupation);
		if ((fathersName != null) && (fathersName.length() > 0)) {
			nbttagcompound.setString(label + "_fathersName", fathersName);
		}
		if ((mothersName != null) && (mothersName.length() > 0)) {
			nbttagcompound.setString(label + "_mothersName", mothersName);
		}
		if ((maidenName != null) && (maidenName.length() > 0)) {
			nbttagcompound.setString(label + "_maidenName", maidenName);
		}
		if ((spousesName != null) && (spousesName.length() > 0)) {
			nbttagcompound.setString(label + "_spousesName", spousesName);
		}
		nbttagcompound.setInteger(label + "_gender", gender);
		nbttagcompound.setString(label + "_texture", texture);

		nbttagcompound.setBoolean(label + "_killed", killed);
		nbttagcompound.setBoolean(label + "_raidingVillage", raidingVillage);
		nbttagcompound.setBoolean(label + "_awayraiding", awayraiding);
		nbttagcompound.setBoolean(label + "_awayhired", awayhired);
		nbttagcompound.setLong(label + "_raiderSpawn", raiderSpawn);

		if (housePos != null) {
			housePos.write(nbttagcompound, label + "_housePos");
		}
		if (townHallPos != null) {
			townHallPos.write(nbttagcompound, label + "_townHallPos");
		}
		if (originalVillagePos != null) {
			originalVillagePos.write(nbttagcompound, label + "_originalVillagePos");
		}
		nbttagcompound.setInteger(label + "_size", villagerSize);



		NBTTagList nbttaglist = new NBTTagList();
		for (final String tag : questTags) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("tag", tag);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag("questTags", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final InvItem key : inventory.keySet()) {

			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setInteger("item", key.id());
			nbttagcompound1.setInteger("meta", key.meta);
			nbttagcompound1.setInteger("amount", inventory.get(key));
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag(label + "_inventory", nbttaglist);

		nbttagcompound.setString(label + "_culture", culture.key);
	}
}