package org.millenaire.common;

import io.netty.buffer.ByteBufInputStream;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.WeightedChoice;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.Goods;
import org.millenaire.common.network.StreamReadWrite;


public class VillagerType implements WeightedChoice {

	private static final String TAG_LOCALMERCHANT="localmerchant";
	private static final String TAG_FOREIGNMERCHANT="foreignmerchant";
	private static final String TAG_CHILD="child";
	private static final String TAG_RELIGIOUS="religious";
	private static final String TAG_CHIEF="chief";
	private static final String TAG_DRINKER="heavydrinker";
	private static final String TAG_SELLER="seller";
	private static final String TAG_MEDITATES="meditates";
	private static final String TAG_SACRIFICES="performssacrifices";
	private static final String TAG_VISITOR="visitor";
	private static final String TAG_HELPSINATTACKS="helpinattacks";
	private static final String TAG_GATHERSAPPLES="gathersapples";
	private static final String TAG_HOSTILE="hostile";
	private static final String TAG_NOLEAFCLEARING="noleafclearing";
	private static final String TAG_ARCHER="archer";
	private static final String TAG_RAIDER="raider";
	private static final String TAG_NOTELEPORT="noteleport";
	private static final String TAG_HIDENAME="hidename";
	private static final String TAG_SHOWHEALTH="showhealth";
	private static final String TAG_DEFENSIVE="defensive";
	private static final String TAG_NORESURRECT="noresurrect";
	

	public static VillagerType loadVillagerType(File file, Culture c) {

		final VillagerType v=new VillagerType(c,file.getName().split("\\.")[0]);

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			final Vector<Goal> goals=new Vector<Goal>();
			final Vector<String> textures=new Vector<String>();
			final Vector<InvItem> toolsNeeded=new Vector<InvItem>();
			final Vector<InvItem> bringBackHomeGoods=new Vector<InvItem>();
			final Vector<InvItem> collectGoods=new Vector<InvItem>();

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {
					final String[] temp=line.split("=");
					if (temp.length!=2) {
						MLN.error(null, "Invalid line when loading villager type "+file.getName()+": "+line);
					} else {

						final String key=temp[0].toLowerCase();
						final String value=temp[1].trim();
						if (key.equals("native_name")) {
							v.name=value;
						} else if (key.equals("alt_native_name")) {
							v.altname=value;
						} else if (key.equals("alt_key")) {
							v.altkey=value;
						} else if (key.equals("model")) {
							v.model=value.toLowerCase();
						} else if (key.equals("goal")) {
							if (Goal.goals.containsKey(value.toLowerCase())) {
								goals.add(Goal.goals.get(value.toLowerCase()));
							} else {
								MLN.error(null, "Unknown goal found when loading villager type "+file.getName()+": "+value+" amoung "+Goal.goals.size());
							}
						} else if (key.equals("texture")) {
							textures.add(value);
						} else if (key.equals("requiredgood")) {

							if (Goods.goodsName.containsKey(value.split(",")[0].toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.split(",")[0].toLowerCase());
								v.requiredGoods.put(iv, Integer.parseInt(value.split(",")[1]));
								v.requiredFoodAndGoods.put(iv, Integer.parseInt(value.split(",")[1]));
							} else {
								MLN.error(null, "Unknown required good found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("requiredfood")) {
							if (Goods.goodsName.containsKey(value.split(",")[0].toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.split(",")[0].toLowerCase());
								v.requiredFoodAndGoods.put(iv, Integer.parseInt(value.split(",")[1]));
							} else {
								MLN.error(null, "Unknown required good found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("startinginv")) {
							if (Goods.goodsName.containsKey(value.split(",")[0].toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.split(",")[0].toLowerCase());
								v.startingInv.put(iv, Integer.parseInt(value.split(",")[1]));
							} else {
								MLN.error(null, "Unknown starting inv found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("merchantstock")) {
							if (Goods.goodsName.containsKey(value.split(",")[0].toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.split(",")[0].toLowerCase());
								v.foreignMerchantStock.put(iv, Integer.parseInt(value.split(",")[1]));
							} else {
								MLN.error(null, "Unknown merchantstock found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("toolneeded")) {
							if (Goods.goodsName.containsKey(value.toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.toLowerCase());
								toolsNeeded.add(iv);
							} else {
								MLN.error(null, "Unknown tool needed found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("toolneededclass")) {
							if (value.equalsIgnoreCase("meleeweapons")) {
								for (Item item : MillVillager.weaponsSwords) {
									toolsNeeded.add(new InvItem(item,0));	
								}
							} else if (value.equalsIgnoreCase("rangedweapons")) {
								for (Item item : MillVillager.weaponsRanged) {
									toolsNeeded.add(new InvItem(item,0));	
								}
							} else if (value.equalsIgnoreCase("armour")) {
								for (Item item : MillVillager.helmets) {
									toolsNeeded.add(new InvItem(item,0));	
								}
								for (Item item : MillVillager.chestplates) {
									toolsNeeded.add(new InvItem(item,0));	
								}
								for (Item item : MillVillager.legs) {
									toolsNeeded.add(new InvItem(item,0));	
								}
								for (Item item : MillVillager.boots) {
									toolsNeeded.add(new InvItem(item,0));	
								}
							} else if (value.equalsIgnoreCase("pickaxes")) {
								for (Item item : MillVillager.pickaxes) {
									toolsNeeded.add(new InvItem(item,0));	
								}
							} else if (value.equalsIgnoreCase("axes")) {
								for (Item item : MillVillager.axes) {
									toolsNeeded.add(new InvItem(item,0));	
								}
							} else if (value.equalsIgnoreCase("shovels")) {
								for (Item item : MillVillager.shovels) {
									toolsNeeded.add(new InvItem(item,0));	
								}
							} else if (value.equalsIgnoreCase("hoes")) {
								for (Item item : MillVillager.hoes) {
									toolsNeeded.add(new InvItem(item,0));	
								}
							} else {
								MLN.error(null, "Unknown tool class found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("defaultweapon")) {
							if (Goods.goodsName.containsKey(value.toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.toLowerCase());
								v.startingWeapon=iv;
							} else {
								MLN.error(null, "Unknown default weapon found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("bringbackhomegood")) {
							if (Goods.goodsName.containsKey(value.toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.toLowerCase());
								bringBackHomeGoods.add(iv);
							} else {
								MLN.error(null, "Unknown bring back home good found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("collectgood")) {
							if (Goods.goodsName.containsKey(value.toLowerCase())) {
								final InvItem iv=Goods.goodsName.get(value.toLowerCase());
								collectGoods.add(iv);
							} else {
								MLN.error(null, "Unknown collect good found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("gender")) {
							if (value.equals("male")) {
								v.gender=MillVillager.MALE;
							} else if (value.equals("female")) {
								v.gender=MillVillager.FEMALE;
							} else {
								MLN.error(null, "Unknown gender found when loading villager type "+file.getName()+": "+value);
							}
						} else if (key.equals("baseattackstrength")) {
							v.baseAttackStrength=Integer.parseInt(value);
						} else if (key.equals("experiencegiven")) {
							v.expgiven=Integer.parseInt(value);
						} else if (key.equals("familynamelist")) {
							v.familyNameList=value;
						} else if (key.equals("firstnamelist")) {
							v.firstNameList=value;
						} else if (key.equals("malechild")) {
							v.maleChild=value;
						} else if (key.equals("femalechild")) {
							v.femaleChild=value;
						} else if (key.equals("tag")) {
							v.tags.add(value.toLowerCase());
						} else if (key.equals("baseheight")) {
							v.baseScale=Float.parseFloat(value);
						} else if (key.equals("health")) {
							v.health=Integer.parseInt(value);
						} else if (key.equals("hiringcost")) {
							v.hireCost=Integer.parseInt(value);
						} else if (key.equals("chanceweight")) {
							v.chanceWeight=Integer.parseInt(value);
						} else if (key.equals("clothes")) {
							if (value.split(",").length<2) {
								MLN.error(null, "Two values are required for all clothes tag (cloth name, then texture file).");
							} else {
								final String clothname=value.split(",")[0];
								final String textpath=value.split(",")[1];

								if (!v.clothes.containsKey(clothname)) {
									v.clothes.put(clothname, new Vector<String>());
								}

								v.clothes.get(clothname).add(textpath);
							}

						} else {
							MLN.error(null, "Could not understand parameter when loading villager type "+file.getName()+": "+line);
						}
					}
				}
			}
			reader.close();

			v.isChild=v.tags.contains(TAG_CHILD);
			v.isChief=v.tags.contains(TAG_CHIEF);
			v.isHeavyDrinker=v.tags.contains(TAG_DRINKER);
			v.isReligious=v.tags.contains(TAG_RELIGIOUS);
			v.canSell=v.tags.contains(TAG_SELLER);
			v.canMeditate=v.tags.contains(TAG_MEDITATES);
			v.canPerformSacrifices=v.tags.contains(TAG_SACRIFICES);
			v.visitor=v.tags.contains(TAG_VISITOR);
			v.helpInAttacks=v.tags.contains(TAG_HELPSINATTACKS);
			v.isLocalMerchant=v.tags.contains(TAG_LOCALMERCHANT);
			v.isForeignMerchant=v.tags.contains(TAG_FOREIGNMERCHANT);
			v.gathersApples=v.tags.contains(TAG_GATHERSAPPLES);
			v.hostile=v.tags.contains(TAG_HOSTILE);
			v.noleafclearing=v.tags.contains(TAG_NOLEAFCLEARING);
			v.isArcher=v.tags.contains(TAG_ARCHER);
			v.isRaider=v.tags.contains(TAG_RAIDER);
			v.noTeleport=v.tags.contains(TAG_NOTELEPORT);
			v.hideName=v.tags.contains(TAG_HIDENAME);
			v.showHealth=v.tags.contains(TAG_SHOWHEALTH);
			v.isDefensive=v.tags.contains(TAG_DEFENSIVE);
			v.noResurrect=v.tags.contains(TAG_NORESURRECT);


			v.textures=textures.toArray(new String[0]);
			v.toolsNeeded=toolsNeeded.toArray(new InvItem[0]);
			v.bringBackHomeGoods=bringBackHomeGoods.toArray(new InvItem[0]);
			v.collectGoods=collectGoods.toArray(new InvItem[0]);

			goals.add(Goal.sleep);

			if (v.toolsNeeded.length>0) {
				boolean foundToolFetchingGoal=false;

				for (final Goal g : goals) {
					if (g==Goal.gettool) {
						foundToolFetchingGoal=true;
					}
				}

				if (!foundToolFetchingGoal) {
					goals.add(Goal.gettool);
				}
			}

			v.goals=goals.toArray(new Goal[0]);


			if (v.health==-1) {
				if (v.helpInAttacks) {
					v.health=40;
				} else {
					v.health=30;
				}
			}

			if (v.baseAttackStrength==-1) {
				if (v.helpInAttacks) {
					v.baseAttackStrength=2;
				} else {
					v.baseAttackStrength=1;
				}
			}


			for (final InvItem item : v.foreignMerchantStock.keySet()) {

				if (!c.goodsByItem.containsKey(item)) {
					MLN.warning(v, "Starting inv of foreign merchant countains non-tradeable good: "+item);
				} else if (c.goodsByItem.get(item).foreignMerchantPrice<1) {
					MLN.warning(v, "Starting inv of foreign merchant countains good with null tradeable price: "+item);
				}

			}



			if (MLN.LogVillager>=MLN.MAJOR) {
				MLN.major(v, "Loaded villager type: "+v.key+" "+v.helpInAttacks);
			}

			return v;

		} catch (final Exception e) {
			MLN.printException(e);
		}
		return null;
	}

	public Culture culture;
	public String name;
	public String key;
	public String altname;
	public String altkey;
	public String model=null;
	public int baseAttackStrength = -1;
	public int health = -1;
	public boolean isChild = false;
	public boolean isReligious = false;
	public boolean isChief = false;
	public boolean isHeavyDrinker = false;
	public boolean canSell = false;
	public boolean canMeditate = false;
	public boolean canPerformSacrifices = false;
	public boolean visitor = false;
	public boolean helpInAttacks = false;
	public boolean isLocalMerchant = false;
	public boolean isForeignMerchant = false;
	public boolean gathersApples = false;
	public boolean hostile = false;
	public boolean noleafclearing = false;
	public boolean isArcher = false;
	public boolean isRaider = false;
	public boolean noTeleport = false;
	public boolean hideName = false;
	public boolean showHealth = false;
	public boolean isDefensive = false;
	public boolean noResurrect = false;
	public float baseScale = 1;
	public String familyNameList;
	public String firstNameList;
	public int chanceWeight = 0;

	public int expgiven = 0;
	public Goal[] goals;
	public String[] textures;
	public HashMap<InvItem, Integer> requiredGoods=new HashMap<InvItem, Integer>();
	public HashMap<InvItem, Integer> requiredFoodAndGoods=new HashMap<InvItem, Integer>();
	public HashMap<InvItem, Integer> startingInv=new HashMap<InvItem, Integer>();
	public HashMap<InvItem, Integer> foreignMerchantStock=new HashMap<InvItem, Integer>();
	public HashMap<String, Vector<String>> clothes=new HashMap<String, Vector<String>>();

	public InvItem[] bringBackHomeGoods;
	public InvItem[] collectGoods;
	public InvItem startingWeapon;

	private final Vector<String> tags=new Vector<String>();
	public InvItem[] toolsNeeded;

	public int gender;
	public String maleChild=null;
	public String femaleChild=null;

	public int hireCost;

	public VillagerType(Culture c,String key) {
		culture=c;
		this.key=key;
	}

	@Override
	public int getChoiceWeight(EntityPlayer player) {
		return chanceWeight;
	}

	public String getEntityName() {
		if ("femaleasymmetrical".equals(model))
			return MillVillager.GENERIC_ASYMM_FEMALE;
		if ("femalesymmetrical".equals(model))
			return MillVillager.GENERIC_SYMM_FEMALE;
		if ("zombie".equals(model))
			return MillVillager.GENERIC_ZOMBIE;

		return MillVillager.GENERIC_VILLAGER;
	}

	public String getRandomClothTexture(String clothType) {
		if (clothes.containsKey(clothType))
			return clothes.get(clothType).get(MillCommonUtilities.randomInt(clothes.get(clothType).size()));
		return null;
	}

	public String getRandomFamilyName() {
		return culture.getRandomNameFromList(familyNameList);
	}


	public String getTexture() {
		return textures[MillCommonUtilities.randomInt(textures.length)];
	}

	public boolean isClothValid(String clothType,String texture) {

		if (!clothes.containsKey(clothType))
			return false;

		for (final String s : clothes.get(clothType))
			if (s.equals(texture))
				return true;

		return false;
	}

	public boolean isTextureValid(String texture) {
		for (final String s : textures)
			if (s.equals(texture))
				return true;

		return false;
	}

	public void readVillagerTypeInfoPacket(ByteBufInputStream ds) throws IOException {
		name=StreamReadWrite.readNullableString(ds);
		altkey=StreamReadWrite.readNullableString(ds);
		altname=StreamReadWrite.readNullableString(ds);
		model=StreamReadWrite.readNullableString(ds);
		gender=ds.read();
	}


	@Override
	public String toString() {
		return "VT: "+culture.key+"/"+key;
	}

	public void writeVillagerTypeInfo(DataOutput data) throws IOException {
		data.writeUTF(key);
		StreamReadWrite.writeNullableString(name, data);
		StreamReadWrite.writeNullableString(altkey, data);
		StreamReadWrite.writeNullableString(altname, data);
		StreamReadWrite.writeNullableString(model, data);
		data.write(gender);
	}
}
