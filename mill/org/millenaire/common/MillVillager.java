package org.millenaire.common;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.millenaire.common.Culture.CultureLanguage.Dialog;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.core.DevModUtilities;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.CommonGuiHandler;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.Goal.GoalInformation;
import org.millenaire.common.item.Goods.ItemMillenaireBow;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.pathing.AStarPathing.PathKey;
import org.millenaire.common.pathing.AStarPathing.PathingException;
import org.millenaire.common.pathing.AStarPathing.PathingWorker;
import org.millenaire.common.pathing.atomicstryker.AS_PathEntity;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlanner;
import org.millenaire.common.pathing.atomicstryker.AStarStatic;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class MillVillager extends EntityCreature  implements IEntityAdditionalSpawnData, IAStarPathedEntity {

	public static class InvItem {

		public static final int ANYENCHANTED=1;
		public static final int ENCHANTEDSWORD=2;

		public Item item=null;
		public int meta=0;
		public int special=0;

		public InvItem(Block block,int meta) {
			this.item=Item.itemsList[block.blockID];
			this.meta=meta;
		}

		public InvItem(int special) {
			this.special=special;
		}

		public InvItem(int id,int meta) {
			if (Item.itemsList[id]==null) {
				MLN.printException("Tried creating InvItem with null id: "+id, new Exception());
			} else {
				item=Item.itemsList[id];
			}
			this.meta=meta;
		}

		public InvItem(Item item,int meta) {
			this.item=item;
			this.meta=meta;
		}

		public InvItem(ItemStack is) {
			item=is.getItem();
			if (is.getItemDamage()>0) {
				meta=is.getItemDamage();
			}
		}

		public InvItem(String s) {
			if (s.split("/").length>2) {
				final int id=Integer.parseInt(s.split("/")[0]);

				if (Item.itemsList[id]==null) {
					MLN.printException("Tried creating InvItem with null id from string: "+s, new Exception());
				} else {
					item=Item.itemsList[id];
				}

				meta=Integer.parseInt(s.split("/")[1]);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof InvItem))
				return false;
			final InvItem other = (InvItem) obj;

			return ((other.item==item) && (other.meta==meta));
		}

		public Item getItem() {
			return item;
		}

		public ItemStack getItemStack() {
			if (item==null)
				return null;
			return new ItemStack(item,1,meta);
		}

		public String getName() {
			if (special==ANYENCHANTED)
				return MLN.string("ui.anyenchanted");
			else if (special==ENCHANTEDSWORD)
				return MLN.string("ui.enchantedsword");
			else if ((meta==-1) && (id()==Block.wood.blockID))
				return MLN.string("ui.woodforplanks");
			else if ((meta==0) && (id()==Block.wood.blockID))
				return MLN.string("ui.woodoak");
			else if ((meta==1) && (id()==Block.wood.blockID))
				return MLN.string("ui.woodpine");
			else if ((meta==2) && (id()==Block.wood.blockID))
				return MLN.string("ui.woodbirch");
			else if ((meta==3) && (id()==Block.wood.blockID))
				return MLN.string("ui.woodjungle");
			else if (meta==-1)
				return Mill.proxy.getItemName(id(), 0);
			else
				return Mill.proxy.getItemName(id(), meta);
		}

		public String getTranslationKey() {
			return "_item:"+id()+":"+meta;
		}

		@Override
		public int hashCode() {
			return id()+(meta << 8);
		}

		public int id() {
			if (item!=null)
				return item.itemID;

			if (special>0)
				return -special;

			return 0;
		}

		public boolean matches(InvItem ii) {

			return ((ii.item==item) && ((ii.meta==meta) || (ii.meta==-1) || (meta==-1)));
		}

		@Override
		public String toString() {
			return getName()+"/"+meta;
		}
	}

	public static class MLEntityGenericAsymmFemale extends MillVillager {
		public MLEntityGenericAsymmFemale(World world) {
			super(world);
		}
	}
	public static class MLEntityGenericMale extends MillVillager {
		public MLEntityGenericMale(World world) {

			super(world);

		}
	}
	public static class MLEntityGenericSymmFemale extends MillVillager {
		public MLEntityGenericSymmFemale(World world) {
			super(world);
		}
	}
	public static class MLEntityGenericZombie extends MillVillager {
		public MLEntityGenericZombie(World world) {
			super(world);
		}
	}
	private static final String FREE_CLOTHES = "free";

	public static final int CONCEPTION_CHANCE = 6;

	public static final int FOREIGN_MERCHANT_NB_NIGHTS_BEFORE_LEAVING=3;
	public static final int MALE=1,FEMALE=2;

	public static final String GENERIC_VILLAGER="ml_GenericVillager";
	public static final String GENERIC_ASYMM_FEMALE="ml_GenericAsimmFemale";
	public static final String GENERIC_SYMM_FEMALE="ml_GenericSimmFemale";
	public static final String GENERIC_ZOMBIE="ml_GenericZombie";


	public static ItemStack[] hoeWood=new ItemStack[]{new ItemStack(Item.hoeWood, 1)};
	public static ItemStack[] hoeStone=new ItemStack[]{new ItemStack(Item.hoeStone, 1)};
	public static ItemStack[] hoeSteel=new ItemStack[]{new ItemStack(Item.hoeSteel, 1)};
	public static ItemStack[] hoeNorman=new ItemStack[]{new ItemStack(Mill.normanHoe, 1)};
	public static ItemStack[] hoeMayan=new ItemStack[]{new ItemStack(Mill.mayanHoe, 1)};

	public static ItemStack[] shovelWood=new ItemStack[]{new ItemStack(Item.shovelWood, 1)};
	public static ItemStack[] shovelStone=new ItemStack[]{new ItemStack(Item.shovelStone, 1)};
	public static ItemStack[] shovelSteel=new ItemStack[]{new ItemStack(Item.shovelSteel, 1)};
	public static ItemStack[] shovelNorman=new ItemStack[]{new ItemStack(Mill.normanShovel, 1)};
	public static ItemStack[] shovelMayan=new ItemStack[]{new ItemStack(Mill.mayanShovel, 1)};

	public static ItemStack[] pickaxeWood=new ItemStack[]{new ItemStack(Item.pickaxeWood, 1)};
	public static ItemStack[] pickaxeStone=new ItemStack[]{new ItemStack(Item.pickaxeStone, 1)};
	public static ItemStack[] pickaxeSteel=new ItemStack[]{new ItemStack(Item.pickaxeSteel, 1)};
	public static ItemStack[] pickaxeNorman=new ItemStack[]{new ItemStack(Mill.normanPickaxe, 1)};
	public static ItemStack[] pickaxeMayan=new ItemStack[]{new ItemStack(Mill.mayanPickaxe, 1)};

	public static ItemStack[] axeWood=new ItemStack[]{new ItemStack(Item.axeWood, 1)};
	public static ItemStack[] axeStone=new ItemStack[]{new ItemStack(Item.axeStone, 1)};
	public static ItemStack[] axeSteel=new ItemStack[]{new ItemStack(Item.axeSteel, 1)};
	public static ItemStack[] axeNorman=new ItemStack[]{new ItemStack(Mill.normanAxe, 1)};
	public static ItemStack[] axeMayan=new ItemStack[]{new ItemStack(Mill.mayanAxe, 1)};

	public static ItemStack[] swordWood=new ItemStack[]{new ItemStack(Item.swordWood, 1)};
	public static ItemStack[] swordStone=new ItemStack[]{new ItemStack(Item.swordStone, 1)};
	public static ItemStack[] swordSteel=new ItemStack[]{new ItemStack(Item.swordSteel, 1)};
	public static ItemStack[] swordNorman=new ItemStack[]{new ItemStack(Mill.normanBroadsword, 1)};
	public static ItemStack[] swordMayan=new ItemStack[]{new ItemStack(Mill.mayanMace, 1)};
	public static ItemStack[] swordByzantine=new ItemStack[]{new ItemStack(Mill.byzantineMace, 1)};


	public static final HashMap<String,String[]> oldVillagers=new HashMap<String,String[]>();
	//In descending order of priority:
	private static final Item[] weapons=new Item[]{
		Mill.normanBroadsword,Mill.tachiSword,Mill.byzantineMace,Mill.mayanMace,Item.swordSteel,Item.swordStone,
		Mill.yumiBow,Item.bow,
		Mill.normanAxe,Mill.mayanAxe,Item.axeSteel,Item.axeStone,
		Mill.normanPickaxe,Mill.mayanPickaxe,Item.pickaxeSteel,Item.pickaxeStone,
		Mill.normanHoe,Mill.mayanHoe,Item.hoeSteel,Item.hoeStone,
		Mill.normanShovel,Mill.mayanShovel,Item.shovelWood,Item.shovelStone
	};

	private static final Item[] weaponsHandToHand=new Item[]{
		Mill.normanBroadsword,Mill.tachiSword,Mill.byzantineMace,Mill.mayanMace,Item.swordSteel,Item.swordStone,
		Mill.normanAxe,Mill.mayanAxe,Item.axeSteel,Item.axeStone,
		Mill.normanPickaxe,Mill.mayanPickaxe,Item.pickaxeSteel,Item.pickaxeStone,
		Mill.normanHoe,Mill.mayanHoe,Item.hoeSteel,Item.hoeStone,
		Mill.normanShovel,Mill.mayanShovel,Item.shovelWood,Item.shovelStone
	};

	private static final Item[] weaponsBow=new Item[]{
		Mill.yumiBow,Item.bow
	};

	public static final Item[] helmets=new Item[]{
		Mill.normanHelmet,Mill.byzantineHelmet,Mill.japaneseWarriorBlueHelmet,Mill.japaneseWarriorRedHelmet,Mill.japaneseGuardHelmet,Item.helmetDiamond,Item.helmetSteel,Item.helmetGold,
		Item.helmetLeather
	};

	public static final Item[] chestplates=new Item[]{
		Mill.normanPlate,Mill.byzantinePlate,Mill.japaneseWarriorBluePlate,Mill.japaneseWarriorRedPlate,Mill.japaneseGuardPlate
		,Item.plateDiamond,Item.plateSteel,Item.plateGold,
		Item.plateLeather
	};

	public static final Item[] legs=new Item[]{
		Mill.normanLegs,Mill.byzantineLegs,Mill.japaneseWarriorBlueLegs,Mill.japaneseWarriorRedLegs,Mill.japaneseGuardLegs,
		Item.legsDiamond,Item.legsSteel,Item.legsGold,
		Item.legsLeather
	};

	public static final Item[] boots=new Item[]{
		Mill.normanBoots,Mill.byzantineBoots,Mill.japaneseWarriorBlueBoots,Mill.japaneseWarriorRedBoots,Mill.japaneseGuardBoots,Item.bootsDiamond,Item.bootsSteel,Item.bootsGold,
		Item.bootsLeather
	};

	private static final Item[] foodGrowth=new Item[]{
		Item.egg,Item.bread,Item.beefCooked,Item.porkCooked,Item.chickenCooked,Item.fishCooked,Item.carrot,Item.bakedPotato,
		Mill.tripes,Mill.boudin,
		Mill.vegcurry,Mill.chickencurry,Mill.rice,
		Mill.masa,Mill.wah,
		Mill.udon,
		Mill.lambCooked,Mill.souvlaki
	};

	private static final int[] foodGrowthValues=new int[]{
		1,2,4,4,3,3,1,2,
		6,4,
		3,5,1,
		3,5,
		5,
		3,6
	};

	//Careful: unlike growth food must be in order from best to worse
	//As villagers will use only the first available
	private static final Item[] foodConception=new Item[]{
		Mill.wineFancy,Mill.calva,Mill.wineBasic,Mill.cider,Mill.rasgulla,Mill.feta
	};

	private static final int[] foodConceptionChanceOn=new int[]{
		2,2,3,3,3,3
	};

	static {
		oldVillagers.put("ml_carpenter", new String[]{"carpenter",""});
		oldVillagers.put("ml_cattlefarmer", new String[]{"cattlefarmermale","cattlefarmerfemale"});
		oldVillagers.put("ml_child", new String[]{"boy","girl"});
		oldVillagers.put("ml_farmer", new String[]{"farmer",""});
		oldVillagers.put("ml_guard", new String[]{"guard",""});
		oldVillagers.put("ml_guildmaster", new String[]{"guildmaster",""});
		oldVillagers.put("ml_knight", new String[]{"knight",""});
		oldVillagers.put("ml_lady", new String[]{"","lady"});
		oldVillagers.put("ml_lumberman", new String[]{"lumberman",""});
		oldVillagers.put("ml_merchant", new String[]{"merchant",""});
		oldVillagers.put("ml_miner", new String[]{"miner",""});
		oldVillagers.put("ml_monk", new String[]{"monk",""});
		oldVillagers.put("ml_priest", new String[]{"priest",""});
		oldVillagers.put("ml_seneschal", new String[]{"seneschal",""});
		oldVillagers.put("ml_smith", new String[]{"smith",""});
		oldVillagers.put("ml_wife", new String[]{"","wife"});
		oldVillagers.put("ml_foreignmerchant", new String[]{"merchant_weapons",""});

		oldVillagers.put("ml_indianpeasant", new String[]{"indian_peasant",""});
		oldVillagers.put("ml_indianchild", new String[]{"indian_boy","indian_girl"});
		oldVillagers.put("ml_indianarmysmith", new String[]{"indian_armysmith",""});
		oldVillagers.put("ml_indianlumberman", new String[]{"indian_lumberman",""});
		oldVillagers.put("ml_indianmerchant", new String[]{"indian_merchant",""});
		oldVillagers.put("ml_indianminer", new String[]{"indian_miner",""});
		oldVillagers.put("ml_indianpandit", new String[]{"indian_pandit",""});
		oldVillagers.put("ml_indianpeasantwife", new String[]{"","indian_peasantwife"});
		oldVillagers.put("ml_indianraja", new String[]{"indian_raja",""});
		oldVillagers.put("ml_indianrajputgeneral", new String[]{"indian_rajputgeneral",""});
		oldVillagers.put("ml_indianvillagechief", new String[]{"indian_villagechief",""});
		oldVillagers.put("ml_indianrichwoman", new String[]{"","indian_richwoman"});
		oldVillagers.put("ml_indianscultor", new String[]{"indian_sculptor",""});
		oldVillagers.put("ml_indiansmith", new String[]{"indian_smith",""});
		oldVillagers.put("ml_indiansoldier", new String[]{"indian_soldier",""});
	}
	static final int GATHER_RANGE = 20;//how far a villager will travel to gather a good from getGoodsToGather()

	private static final int HOLD_DURATION = 20;

	public static final int ATTACK_RANGE = 80;
	static public boolean usingCustomPathing = true;
	static public boolean usingBinaryPathing = false;


	public static MillVillager createVillager(Culture c,String type, int gender, World world, Point spawnPos, Point housePos, Point thPos, boolean respawn, String firstName, String familyName) {

		if (world.isRemote || !(world instanceof WorldServer)) {
			MLN.printException("Tried creating a villager in client world: "+world, new Exception());
			return null;
		}


		MillVillager villager;

		if ((type==null) || (type.length()==0)) {
			MLN.error(null, "Tried creating child of null type: "+type);
		}

		//Conversion code for old buildings/villagers
		if ((gender>0) && oldVillagers.containsKey(type.toLowerCase())) {
			if (gender==MALE) {
				type=oldVillagers.get(type.toLowerCase())[0];
			} else {
				type=oldVillagers.get(type.toLowerCase())[1];
			}
		}

		if (c.getVillagerType(type.toLowerCase())==null) {
			for (final Culture c2 : Culture.vectorCultures) {
				if (c2.getVillagerType(type)!=null) {
					MLN.error(null, "Could not find villager type " + type
							+ " in culture " + c.key+" but could in "+c2.key+" so switching.");
					c=c2;
				}
			}
		}

		if (c.getVillagerType(type.toLowerCase())!=null) {

			final VillagerType vtype=c.getVillagerType(type.toLowerCase());

			villager=(MillVillager)EntityList.createEntityByName(vtype.getEntityName(), world);

			if (villager==null) {
				MLN.error(c, "Could not create villager of dynamic type: "+type+" entity: "+vtype.getEntityName());
				return null;
			}

			villager.housePoint=housePos;
			villager.townHallPoint=thPos;


			if (familyName==null) {
				familyName=vtype.getRandomFamilyName();
			}
			villager.initialise(vtype, familyName,  respawn);

			if (firstName!=null) {
				villager.firstName=firstName;
			}

			villager.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

		} else {
			MLN.error(null, "Unknown villager type: "+type+" for culture "+c);
			return null;
		}

		return villager;
	}
	public static void readVillagerPacket(DataInputStream data) {
		try {
			final long villager_id=data.readLong();

			if (Mill.clientWorld.villagers.containsKey(villager_id)) {
				Mill.clientWorld.villagers.get(villager_id).readVillagerStreamdata(data);
			} else {
				if (MLN.Network>=MLN.MINOR) {
					MLN.minor(null, "readVillagerPacket for unknown villager: "+villager_id);
				}
			}
		} catch (final IOException e) {
			MLN.printException(e);
		}
	}
	public VillagerType vtype;
	public int action = 0;
	public String goalKey=null;
	private GoalInformation goalInformation=null;
	private Point pathDestPoint;
	public PathPoint prevPathPoint;
	private Building house=null,townHall=null;
	public Point housePoint=null;
	public Point prevPoint=null;
	public Point townHallPoint=null;
	public boolean extraLog = false;
	public String firstName="";
	public String familyName="";
	public ItemStack heldItem;
	public long timer=0,actionStart=0;
	public boolean allowRandomMoves=false,stopMoving=false;
	public PathPoint lastJump=null;
	public float scale=1;
	public int gender=0;
	public boolean noHouseorTH=false;
	public boolean registered=false;
	public int longDistanceStuck;
	public boolean nightActionPerformed=false;
	public long speech_started=0;
	public HashMap<InvItem,Integer> inventory;
	public int previousBlock;
	public int previousBlockMeta;
	public int size = 0;
	public long pathingTime,timeSinceLastPathingTimeDisplay,villager_id=0;
	public int nbPathsCalculated=0,nbPathNoStart=0,nbPathNoEnd=0,nbPathAborted=0,nbPathFailure=0;
	public Vector<PathKey> abortedKeys=new Vector<PathKey>();
	public long goalStarted=0;

	public boolean hasPrayedToday=false,hasDrunkToday=false;

	public int heldItemCount=0,heldItemId=-1;

	public static final int max_child_size = 20;
	public String speech_key=null;
	public int speech_variant=0;
	
	public String dialogKey=null;
	public int dialogRole=0;
	public long dialogStart=0;
	
	private Point doorToClose=null;

	public int foreignMerchantNbNights=0;
	public int foreignMerchantStallId=-1;

	public boolean lastAttackByPlayer=false;

	public HashMap<Goal,Long> lastGoalTime=new HashMap<Goal,Long>();

	public String hiredBy = null;

	public boolean aggressiveStance = false;

	public long hiredUntil = 0;

	public boolean isUsingBow,isUsingHandToHand;
	public boolean isRaider = false;
	private PathingWorker pathingWorker;
	public AStarPathPlanner jpsPathPlanner;
	public static final AStarConfig DEFAULT_JPS_CONFIG=new AStarConfig(true,false,false,true);

	public AS_PathEntity pathEntity;


	public int updateCounter=0;
	public long client_lastupdated;
	private boolean registeredInGlobalList=false;
	public MillWorld mw;

	public int pathfailure=0;

	private boolean pathFailedSincelastTick=false;

	private ArrayList<AStarNode> pathCalculatedSinceLastTick=null;

	private int localStuck=0;

	private long pathCalculationStartTime=0;

	private String clothTexture=null,clothName=null;

	public boolean shouldLieDown=false;

	public MillVillager(World world) {

		super(world);
		this.worldObj=world;

		mw=Mill.getMillWorld(world);

		inventory=new HashMap<InvItem,Integer>();
		health = getMaxHealth();

		isImmuneToFire=true;

		client_lastupdated=world.getWorldTime();

		jpsPathPlanner=new AStarPathPlanner(world, this);

		if (MLN.VillagerSpawn>=MLN.DEBUG) {
			final Exception e = new Exception();

			MLN.printException("Creating villager "+this+" in world: "+world, e);
		}
	}

	public int a(EntityPlayer entityplayer) {
		return vtype.expgiven;
	}

	public void addToInv(int id,int nb) {
		addToInv(id,0,nb);
	}

	public void addToInv(int id,int meta,int nb) {
		final InvItem key=new InvItem(id,meta);
		if (inventory.containsKey(key)) {
			inventory.put(key,inventory.get(key)+nb);
		} else {
			inventory.put(key, nb);
		}
		if (getTownHall()!=null) {
			getTownHall().updateVillagerRecord(this);
		} else {
			MLN.error(this, "Wanted to update VR after an addToInv but TH is null.");
		}
		updateClothTexturePath();
	}

	public void addToInv(InvItem iv,int nb) {
		addToInv(iv.id(),iv.meta,nb);
	}

	public void adjustSize() {
		scale=0.5f+(((float)size)/100);
	}

	private void applyPathCalculatedSinceLastTick() {
		//MLN.temp(this, "Path found between "+getPos()+" and "+getPathDestPoint()+" in "+(System.currentTimeMillis()-this.pathCalculationStartTime));

		final AS_PathEntity path=AStarStatic.translateAStarPathtoPathEntity(worldObj,pathCalculatedSinceLastTick,getPathingConfig());

		try {
			registerNewPath(path);

			pathfailure=0;

		} catch (final Exception e) {
			MLN.printException("Exception when finding JPS path:", e);
		}

		pathCalculatedSinceLastTick=null;
	}

	@Override
	public void attackEntity(Entity entity, float f) {
		if (vtype.isArcher && (f > 5) && hasBow()) {
			attackEntityBow(entity, f);
			isUsingBow = true;
		} else {
			if((attackTime <= 0) && (f < 2.0F) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
			{
				attackTime = 20;
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength());
			}
			isUsingHandToHand=true;
		}
	}

	public void attackEntityBow(Entity entity, float f)
	{
		if (!(entity instanceof EntityLiving))
			return;


		if (f < 10F)
		{
			final double d = entity.posX - posX;
			final double d1 = entity.posZ - posZ;
			if (attackTime == 0)
			{

				float speedFactor=1;
				float damageBonus=0;

				final ItemStack weapon=getWeapon();

				if (weapon!=null) {
					final Item item=Item.itemsList[weapon.itemID];

					if (item instanceof ItemMillenaireBow) {
						final ItemMillenaireBow bow=(ItemMillenaireBow)item;

						if (bow.speedFactor>speedFactor) {
							speedFactor=bow.speedFactor;
						}
						if (bow.damageBonus>damageBonus) {
							damageBonus=bow.damageBonus;
						}
					}
				}

				final EntityArrow arrow = new EntityArrow(this.worldObj, this, (EntityLiving)entity, 1.6F, 12.0F);

				this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / ((this.getRNG().nextFloat() * 0.4F) + 0.8F));
				this.worldObj.spawnEntityInWorld(arrow);





				attackTime = 60;

				//faster MLN arrows
				arrow.motionX*=speedFactor;
				arrow.motionY*=speedFactor;
				arrow.motionZ*=speedFactor;

				//extra arrow damage
				arrow.setDamage(arrow.getDamage()+damageBonus);
			}
			rotationYaw = (float)((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F;
			hasAttacked = true;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource ds, int i){

		if ((ds.getSourceOfDamage()==null) && (ds!=DamageSource.outOfWorld))
			return false;

		final boolean hadFullHealth=(getMaxHealth()==health);

		final boolean b=super.attackEntityFrom(ds, i);

		final Entity entity = ds.getSourceOfDamage();

		lastAttackByPlayer=false;

		if (entity!=null) {
			if (entity instanceof EntityLiving) {
				if (entity instanceof EntityPlayer) {
					lastAttackByPlayer=true;

					final EntityPlayer player=(EntityPlayer)entity;

					if (!isRaider) {
						if (!vtype.hostile) {
							MillCommonUtilities.getServerProfile(player.worldObj,player.username).adjustReputation(getTownHall(), -i*10);
						}
						if ((worldObj.difficultySetting != 0) && (this.health<(getMaxHealth()-10))) {
							entityToAttack = entity;
							clearGoal();
							if (getTownHall() != null) {
								getTownHall().callForHelp(entity);
							}
						}

						if (hadFullHealth && ((player.getHeldItem()==null) || (player.getHeldItem().getDamageVsEntity(this)<=1)) &&!worldObj.isRemote) {
							ServerSender.sendTranslatedSentence(player,MLN.ORANGE, "ui.communicationexplanations");
						}
					}

					if (lastAttackByPlayer && (health<=0)) {
						if (vtype.hostile) {
							player.addStat(MillAchievements.selfdefense, 1);
						} else {
							player.addStat(MillAchievements.darkside, 1);
						}
					}


				} else {
					entityToAttack = entity;
					clearGoal();

					if (getTownHall() != null) {
						getTownHall().callForHelp(entity);
					}

				}
			}
		}

		return b;
	}

	public boolean attemptChildConception() {
		int nbChildren=0;

		for (final MillVillager villager : getHouse().villagers) {
			if (villager.isChild()) {
				nbChildren++;
			}
		}

		if (nbChildren > 1) {
			if (MLN.Children>=MLN.DEBUG) {
				MLN.debug(this,  "Wife already has "+nbChildren+" children, no need for more.");
			}
			return true;
		}

		final int nbChildVillage=getTownHall().countChildren();

		if (nbChildVillage>MLN.maxChildrenNumber) {
			if (MLN.Children>=MLN.DEBUG) {
				MLN.debug(this, "Village already has "+nbChildVillage+", no need for more.");
			}
			return true;
		}

		boolean couldMoveIn=false;

		for (final Point housePoint : getTownHall().buildings) {

			final Building house = mw.getBuilding(housePoint);
			if ((house != null) && !house.equals(getHouse()) && house.isHouse()) {

				if (house.canChildMoveIn(MALE,familyName) || house.canChildMoveIn(FEMALE,familyName)) {
					couldMoveIn=true;
				}
			}
		}

		if ((nbChildVillage>5) && !couldMoveIn) {
			if (MLN.Children>=MLN.DEBUG) {
				MLN.debug(this, "Village already has "+nbChildVillage+" and no slot is available for the new child.");
			}
			return true;
		}

		final List<Entity> entities=MillCommonUtilities.getEntitiesWithinAABB(worldObj, MillVillager.class, getPos(), 4, 2);


		boolean manFound=false;

		for (final Entity ent : entities) {
			final MillVillager villager = (MillVillager) ent;
			if ((villager.gender==MALE) && !villager.isChild()) {
				manFound=true;
			}
		}

		if (!manFound)
			return false;

		if (MLN.Children>=MLN.DEBUG) {
			MLN.debug(this, "Less than two kids and man present, trying for new child.");
		}

		boolean createChild=false;

		boolean foundConceptionFood=false;

		for (int i=0;(i<foodConception.length) && !foundConceptionFood;i++) {
			if (getHouse().countGoods(foodConception[i].itemID)>0) {
				getHouse().takeGoods(foodConception[i].itemID, 1);
				foundConceptionFood=true;
				if (MillCommonUtilities.randomInt(foodConceptionChanceOn[i])==0) {
					createChild=true;
					if (MLN.Children>=MLN.MINOR) {
						MLN.minor(this, "Conceiving child with help from: "+foodConception[i].getUnlocalizedName());
					} else 	if (MLN.Children>=MLN.MINOR) {
						MLN.minor(this, "Failed to conceive child even with help from: "+foodConception[i].getUnlocalizedName());
					}
				}
			}
		}

		if (!foundConceptionFood) {
			if (MillCommonUtilities.randomInt(CONCEPTION_CHANCE)==0) {
				createChild=true;
				if (MLN.Children>=MLN.MINOR) {
					MLN.minor(this, "Conceiving child without help.");
				}
			} else	if (MLN.Children>=MLN.MINOR) {
				MLN.minor(this, "Failed to conceive child without help.");
			}
		}

		if (MLN.DEV) {
			createChild=true;
		}

		if (createChild) {
			getHouse().createChild(this, getTownHall(), getRecord().spousesName);
		}

		return true;
	}

	@Override
	public boolean canDespawn() {
		return false;
	}

	public boolean canMeditate() {
		return vtype.canMeditate;
	}

	public void checkGoals() throws Exception {

		final Goal goal=Goal.goals.get(goalKey);

		if (goal==null) {
			MLN.error(this, "Invalid goal key: "+goalKey);
			goalKey=null;
			return;
		}

		if (getGoalDestEntity()!=null) {
			if (getGoalDestEntity().isDead) {
				setGoalDestEntity(null);
				setPathDestPoint(null);
			} else {
				if ((worldObj.getWorldTime()%100)==25) {
					setPathDestPoint(new Point(getGoalDestEntity()));
				}
			}
		}

		Point target=null;

		boolean continuingGoal=true;

		if (getPathDestPoint() != null) {
			target=getPathDestPoint();
			if ((pathEntity!=null) && (pathEntity.getCurrentPathLength()>0)) {
				target=new Point(pathEntity.getFinalPathPoint());
			}
		}
		speakSentence(goal.sentenceKey());

		if ((getGoalDestPoint()==null) && (getGoalDestEntity()==null)) {
			goal.setVillagerDest(this);
			if ((MLN.GeneralAI>=MLN.MINOR) && extraLog) {
				MLN.minor(this,"Goal destination: "+getGoalDestPoint()+"/"+getGoalDestEntity());
			}
		} else if ((target!=null) && (target.horizontalDistanceTo(this) < goal.range(this))) {
			if (actionStart==0) {
				stopMoving=goal.stopMovingWhileWorking();
				actionStart=System.currentTimeMillis();
				shouldLieDown=goal.shouldVillagerLieDown();

				if ((MLN.GeneralAI>=MLN.MINOR) && extraLog) {
					MLN.minor(this,"Starting action: "+actionStart);
				}
			}

			if ((System.currentTimeMillis()-actionStart) >= goal.actionDuration(this)) {
				if (goal.performAction(this)) {
					clearGoal();
					goalKey=goal.nextGoal(this);
					stopMoving=false;
					shouldLieDown=false;
					heldItem=null;
					continuingGoal=false;
					if ((MLN.GeneralAI>=MLN.MINOR) && extraLog) {
						MLN.minor(this,"Goal performed. Now doing: "+goalKey);
					}
				} else {
					stopMoving=goal.stopMovingWhileWorking();
				}
				actionStart=0;
				goalStarted=System.currentTimeMillis();
			}
		} else {
			stopMoving=false;
			shouldLieDown=false;
		}

		if (!continuingGoal)
			return;

		if (goal.isStillValid(this)) {

			if ((System.currentTimeMillis()-goalStarted) > goal.stuckDelay(this)) {

				final boolean actionDone=goal.stuckAction(this);

				if (actionDone) {
					goalStarted=System.currentTimeMillis();
				}

				if (goal.isStillValid(this)) {
					allowRandomMoves=goal.allowRandomMoves();
					if (stopMoving) {
						setPathToEntity(null);
						pathEntity=null;
					}
					if (heldItemCount>HOLD_DURATION) {
						ItemStack[] heldItems=null;
						if ((target != null) && (target.horizontalDistanceTo(this) < goal.range(this))) {
							heldItems=goal.getHeldItemsDestination(this);
						} else {
							heldItems=goal.getHeldItemsTravelling(this);
						}
						if ((heldItems != null) && (heldItems.length>0)) {
							heldItemId=(heldItemId+1) % heldItems.length;
							heldItem=heldItems[heldItemId];
						}
						heldItemCount=0;
					}
					heldItemCount++;
				}
			} else {
				if (heldItemCount>HOLD_DURATION) {
					ItemStack[] heldItems=null;
					if ((target != null) && (target.horizontalDistanceTo(this) < goal.range(this))) {
						heldItems=goal.getHeldItemsDestination(this);
					} else {
						heldItems=goal.getHeldItemsTravelling(this);
					}
					if ((heldItems != null) && (heldItems.length>0)) {
						heldItemId=(heldItemId+1) % heldItems.length;
						heldItem=heldItems[heldItemId];
					}
					heldItemCount=0;
				}
				heldItemCount++;
			}
		} else {
			stopMoving=false;
			shouldLieDown=false;
			goal.onComplete(this);
			clearGoal();
			goalKey=goal.nextGoal(this);
			heldItemCount=HOLD_DURATION+1;
			heldItemId=-1;
		}

	}

	private void checkRegistration() throws MillenaireException {
		if (!registered || MillCommonUtilities.chanceOn(100)) {
			if (getHouse() != null) {
				if (!getHouse().villagers.contains(this)) {
					getHouse().registerVillager(this);
					if (MLN.Other>=MLN.DEBUG) {
						MLN.debug(this,"Registering in house vector.");
					}
				}
			}
			if (getTownHall() != null) {
				if (!getTownHall().villagers.contains(this)) {
					getTownHall().registerVillager(this);
					if (MLN.Other>=MLN.DEBUG) {
						MLN.debug(this,"Registering in TH vector.");
					}
				}
			}

			registered=true;
		}
	}

	public void clearGoal() {
		setGoalDestPoint(null);
		setGoalBuildingDestPoint(null);
		setGoalDestEntity(null);
		goalKey=null;
		shouldLieDown=false;
	}

	private boolean closeFenceGate(int i, int j, int k)
	{
		final int l = worldObj.getBlockMetadata(i, j, k);
		if(BlockFenceGate.isFenceGateOpen(l))
		{
			MillCommonUtilities.setBlockMetadata(worldObj, i,j,k, l & -5,true);

			return true;
		}
		return false;
	}

	private List<PathPoint> computeNewPath(Point dest) {

		if (getPos().sameBlock(dest))
			return null;

		if (usingCustomPathing) {

			if (MLN.jpsPathing) {

				if (jpsPathPlanner.isBusy())
				{
					jpsPathPlanner.stopPathSearch(true);
				}


				AStarNode destNode=null;

				final AStarNode[] possibles = AStarStatic.getAccessNodesSorted(worldObj, doubleToInt(posX), doubleToInt(posY), doubleToInt(posZ), getPathDestPoint().getiX(), getPathDestPoint().getiY(), getPathDestPoint().getiZ(), getPathingConfig());
				if (possibles.length != 0)
				{
					destNode=possibles[0];
				}

				if (destNode!=null) {
					pathCalculationStartTime=System.currentTimeMillis();
					jpsPathPlanner.getPath(doubleToInt(this.posX), doubleToInt(this.posY)-1, doubleToInt(this.posZ), destNode.x,destNode.y,destNode.z, getPathingConfig());
				} else {
					onNoPathAvailable();
				}

			} else {

				if (pathingWorker!=null) {
					pathingWorker.interrupt();
				}

				pathingWorker=townHall.calculatePath(this,getPos(), dest,extraLog);
			}

			return null;
		} else {
			final List<PathPoint> pp=new ArrayList<PathPoint>();

			final PathEntity pe=worldObj.getEntityPathToXYZ(this, dest.getiX(), dest.getiY(), dest.getiZ(), (float) (getPos().distanceTo(dest)+16), true, false, false, true);

			if (pe==null)
				return null;

			for (int i=0;i<pe.getCurrentPathLength();i++) {
				pp.add(pe.getPathPointFromIndex(i));
			}


			return pp;
		}
	}

	public int countBlocksAround(int x, int y, int z, int rx, int ry, int rz) {
		return MillCommonUtilities.countBlocksAround(worldObj, x, y, z, rx, ry, rz);
	}

	public int countInv(int id) {
		return countInv(id,0);
	}

	public int countInv(int id,int meta) {

		return countInv(new InvItem(id,meta));
	}

	public int countInv(InvItem key) {

		if (key.meta==-1) {//undefined, so has to try the 16 possible values
			int nb=0;
			for (int i=0;i<16;i++) {
				final InvItem tkey=new InvItem(key.id(),i);
				if (inventory.containsKey(tkey)) {
					nb+=inventory.get(tkey);
				}
			}
			return nb;
		}

		if (inventory.containsKey(key))
			return inventory.get(key);
		else
			return 0;
	}

	public int countItemsAround(int itemID,int radius) {
		return countItemsAround(new int[]{itemID},radius);
	}

	public int countItemsAround(int[] itemIDs,int radius) {
		final List<Entity> list = MillCommonUtilities.getEntitiesWithinAABB(worldObj, EntityItem.class, getPos(), radius, radius);

		int count=0;

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getClass() == EntityItem.class) {
					final EntityItem entity = (EntityItem)list.get(i);

					if (!entity.isDead) {
						for (final int id : itemIDs) {
							if (id==entity.getEntityItem().itemID) {
								count++;
							}
						}
					}
				}
			}
		}
		return count;
	}

	public void despawnVillager() {

		if (worldObj.isRemote)
			return;//server-side only

		if (hiredBy!=null) {
			final EntityPlayer owner=worldObj.getPlayerEntityByName(hiredBy);

			if (owner!=null) {
				ServerSender.sendTranslatedSentence(owner,MLN.DARKRED, "hire.hiredied", getName());
			}
		}

		super.setDead();
	}

	public void despawnVillagerSilent() {

		if (MLN.VillagerSpawn>=MLN.DEBUG) {
			final Exception e=new Exception();

			MLN.printException("Despawning villager: "+this, e);
		}

		super.setDead();
	}

	public void detrampleCrops() {
		if (getPos().sameBlock(prevPoint) &&
				((previousBlock == Block.crops.blockID) || (previousBlock == Mill.crops.blockID)) && (getBlock(getPos()) == 0) && (getBlock(getPos().getBelow()) == Block.dirt.blockID)) {
			setBlock(getPos(), previousBlock);
			setBlockMetadata(getPos(), previousBlockMeta);
			setBlock(getPos().getBelow(),Block.tilledField.blockID);
		}

		previousBlock = getBlock(getPos());
		previousBlockMeta = getBlockMeta(getPos());
	}

	public int doubleToInt(double input)
	{
		return AStarStatic.getIntCoordFromDoubleCoord(input);
	}

	public void facePoint(Point p, float par2, float par3)
	{
		final double var4 = p.x - this.posX;
		final double var8 = p.z - this.posZ;
		final double var6 = p.y - (this.posY + this.getEyeHeight());


		final double var14 = MathHelper.sqrt_double((var4 * var4) + (var8 * var8));
		final float var12 = (float)((Math.atan2(var8, var4) * 180.0D) / Math.PI) - 90.0F;
		final float var13 = (float)(-((Math.atan2(var6, var14) * 180.0D) / Math.PI));
		this.rotationPitch = -this.updateRotation(this.rotationPitch, var13, par3);
		this.rotationYaw = this.updateRotation(this.rotationYaw, var12, par2);
	}

	private boolean foreignMerchantNightAction() {
		foreignMerchantNbNights++;

		if (foreignMerchantNbNights>FOREIGN_MERCHANT_NB_NIGHTS_BEFORE_LEAVING) {
			leaveVillage();
		} else {
			boolean hasItems=false;
			for (final InvItem key : vtype.foreignMerchantStock.keySet()) {
				if (getHouse().countGoods(key)>0) {
					hasItems=true;
				}
			}
			if (!hasItems) {
				leaveVillage();
			}
		}

		return true;
	}

	private void foreignMerchantUpdate() {
		if (foreignMerchantStallId<0) {
			for (int i=0;(i<getHouse().stalls.size()) && (foreignMerchantStallId<0);i++) {
				boolean taken=false;
				for (final MillVillager v : getHouse().villagers) {
					if (v.foreignMerchantStallId==i) {
						taken=true;
					}
				}
				if (!taken) {
					foreignMerchantStallId=i;
				}
			}
		}
		if (foreignMerchantStallId<0) {
			foreignMerchantStallId=0;
		}
	}

	public boolean gathersApples() {
		return vtype.gathersApples;
	}

	public String getActionLabel(int action) {
		return "none";
	}

	public ItemStack getArmourPiece(int type) {

		if (type==0) {
			for (final Item weapon : helmets) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}
		if (type==1) {
			for (final Item weapon : chestplates) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}
		if (type==2) {
			for (final Item weapon : legs) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}
		if (type==3) {
			for (final Item weapon : boots) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
			return null;
		}

		return null;
	}

	public ItemStack getArmourPiece2(int type) {

		if (type==0)
			return new ItemStack(Mill.japaneseWarriorRedHelmet,1);
		if (type==1)
			return new ItemStack(Mill.japaneseWarriorRedPlate,1);
		if (type==2)
			return new ItemStack(Mill.japaneseWarriorRedLegs,1);
		if (type==3)
			return new ItemStack(Mill.japaneseWarriorRedBoots,1);

		return null;
	}

	public int getAttackStrength() {
		int attackStrength=vtype.baseAttackStrength;

		final ItemStack weapon=getWeapon();
		if (weapon!=null) {
			attackStrength+=(Math.ceil(((float)weapon.getDamageVsEntity(null))/2));
		}

		return attackStrength;
	}



	public float getBedOrientationInDegrees()
	{

		Point ref=getPos();

		if (getGoalDestPoint()!=null) {
			ref=getGoalDestPoint();
		}

		final int x = (int)ref.x;
		final int y = (int)ref.y;
		final int z = (int)ref.z;
		final Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];

		if (block==Block.bed) {
			final int var2 = (block == null ? 0 : block.getBedDirection(worldObj, x, y, z));

			switch (var2)
			{
			case 0:
				return 270.0F;
			case 1:
				return 0.0F;
			case 2:
				return 90.0F;
			case 3:
				return 180.0F;
			}
		} else {

			if (worldObj.getBlockId(x+1, y, z)==0)
				return 0.0F;
			else if (worldObj.getBlockId(x, y, z+1)==0)
				return 90.0F;
			else if (worldObj.getBlockId(x-1, y, z)==0)
				return 180.0F;
			else if (worldObj.getBlockId(x, y, z-1)==0)
				return 270.0F;

		}



		return 0.0F;
	}

	public ItemTool getBestAxe() {
		if (countInv(Mill.normanAxe.itemID)>0)
			return (ItemTool) Mill.normanAxe;
		if (countInv(Mill.mayanAxe.itemID)>0)
			return (ItemTool) Mill.mayanAxe;
		if (countInv(Item.axeSteel.itemID)>0)
			return (ItemTool) Item.axeSteel;
		if (countInv(Item.axeStone.itemID)>0)
			return (ItemTool) Item.axeStone;
		return (ItemTool) Item.axeWood;
	}

	public ItemStack[] getBestAxeStack() {
		if (countInv(Mill.normanAxe.itemID)>0)
			return axeNorman;
		if (countInv(Mill.mayanAxe.itemID)>0)
			return axeMayan;
		if (countInv(Item.axeSteel.itemID)>0)
			return axeSteel;
		if (countInv(Item.axeStone.itemID)>0)
			return axeStone;
		return axeWood;
	}

	public ItemTool getBestHoe() {
		if (countInv(Mill.normanHoe.itemID)>0)
			return (ItemTool) Mill.normanHoe;
		if (countInv(Mill.mayanHoe.itemID)>0)
			return (ItemTool) Mill.mayanHoe;
		if (countInv(Item.hoeSteel.itemID)>0)
			return (ItemTool) Item.hoeSteel;
		if (countInv(Item.hoeStone.itemID)>0)
			return (ItemTool) Item.hoeStone;
		return (ItemTool) Item.hoeWood;
	}

	public ItemStack[] getBestHoeStack() {
		if (countInv(Mill.normanHoe.itemID)>0)
			return hoeNorman;
		if (countInv(Mill.mayanHoe.itemID)>0)
			return hoeMayan;
		if (countInv(Item.hoeSteel.itemID)>0)
			return hoeSteel;
		if (countInv(Item.hoeStone.itemID)>0)
			return hoeStone;
		return hoeWood;
	}

	public ItemTool getBestPickaxe() {
		if (countInv(Mill.normanPickaxe.itemID)>0)
			return (ItemTool) Mill.normanPickaxe;
		if (countInv(Mill.mayanPickaxe.itemID)>0)
			return (ItemTool) Mill.mayanPickaxe;
		if (countInv(Item.pickaxeSteel.itemID)>0)
			return (ItemTool) Item.pickaxeSteel;
		if (countInv(Item.pickaxeStone.itemID)>0)
			return (ItemTool) Item.pickaxeStone;
		return (ItemTool) Item.pickaxeWood;
	}




	public ItemStack[] getBestPickaxeStack() {
		if (countInv(Mill.normanPickaxe.itemID)>0)
			return pickaxeNorman;
		if (countInv(Mill.mayanPickaxe.itemID)>0)
			return pickaxeMayan;
		if (countInv(Item.pickaxeSteel.itemID)>0)
			return pickaxeSteel;
		if (countInv(Item.pickaxeStone.itemID)>0)
			return pickaxeStone;
		return pickaxeWood;
	}

	public ItemTool getBestShovel() {
		if (countInv(Mill.normanShovel.itemID)>0)
			return (ItemTool) Mill.normanShovel;
		if (countInv(Mill.mayanShovel.itemID)>0)
			return (ItemTool) Mill.mayanShovel;
		if (countInv(Item.shovelSteel.itemID)>0)
			return (ItemTool) Item.shovelSteel;
		if (countInv(Item.shovelStone.itemID)>0)
			return (ItemTool) Item.shovelStone;
		return (ItemTool) Item.shovelWood;
	}

	public ItemStack[] getBestShovelStack() {
		if (countInv(Mill.normanShovel.itemID)>0)
			return shovelNorman;
		if (countInv(Mill.mayanShovel.itemID)>0)
			return shovelMayan;
		if (countInv(Item.shovelSteel.itemID)>0)
			return shovelSteel;
		if (countInv(Item.shovelStone.itemID)>0)
			return shovelStone;
		return shovelWood;
	}

	public int getBlock(Point p)
	{
		return MillCommonUtilities.getBlock(worldObj, p);
	}

	public int getBlockMeta(Point p)
	{
		return MillCommonUtilities.getBlockMeta(worldObj, p);
	}

	@Override
	public float getBlockPathWeight(int i, int j, int k) {

		if (!allowRandomMoves) {
			if ((MLN.Pathing>=MLN.DEBUG) && extraLog) {
				MLN.debug(this,  "Forbiding random moves. Current goal: "+Goal.goals.get(goalKey)+" Returning: "+(-99999F));
			}
			return Float.NEGATIVE_INFINITY;
		}

		final Point rp=new Point(i,j,k);
		final double dist=rp.distanceTo(housePoint);
		if(worldObj.getBlockId(i, j - 1, k) == Block.tilledField.blockID)
			return -50;
		else if (dist > 10)
			return -(float)dist;
		else
			return MillCommonUtilities.randomInt(10);
	}

	public Point getClosest(List<Point> points) {
		double bestdist=Double.MAX_VALUE;
		Point bp=null;;

		for (final Point p : points) {
			final double dist=p.distanceToSquared(this);
			if (dist < bestdist) {
				bestdist=dist;
				bp=p;
			}
		}
		return bp;
	}

	public Point getClosestBlock(int[] blockIds, Point pos,
			int rx, int ry, int rz) {
		return MillCommonUtilities.getClosestBlock(worldObj, blockIds, pos, rx, ry, rz);
	}

	public Point getClosestBlockMeta(int[] blockIds,int meta, Point pos,
			int rx, int ry, int rz) {
		return MillCommonUtilities.getClosestBlockMeta(worldObj, blockIds, meta, pos, rx, ry, rz);
	}

	public Point getClosestHorizontal(List<Point> points) {
		double bestdist=Double.MAX_VALUE;
		Point bp=null;

		for (final Point p : points) {
			final double dist=p.horizontalDistanceToSquared(this);
			if (dist < bestdist) {
				bestdist=dist;
				bp=p;
			}
		}
		return bp;
	}


	public Point getClosestHorizontalWithAltitudeCost(List<Point> points,float vCost) {
		double bestdist=Double.MAX_VALUE;
		Point bp=null;

		for (final Point p : points) {
			double dist=p.horizontalDistanceToSquared(this);
			dist+=Math.abs(townHall.getAltitude((int)posX, (int)posZ)-townHall.getAltitude(p.getiX(), p.getiZ()))*vCost;

			if (dist < bestdist) {
				bestdist=dist;
				bp=p;
			}
		}
		return bp;
	}

	public EntityItem getClosestItemVertical(InvItem item,int radius,int vertical) {
		return getClosestItemVertical(new InvItem[]{item},radius,vertical);
	}

	public EntityItem getClosestItemVertical(InvItem[] items,int radius,int vertical) {
		return MillCommonUtilities.getClosestItemVertical(worldObj, getPos(), items, radius, vertical);
	}

	public Point getClosestToHouse(List<Point> points) {
		double bestdist=Double.MAX_VALUE;
		Point bp=null;;

		for (final Point p : points) {
			final double dist=p.distanceToSquared(house.getPos());
			if (dist < bestdist) {
				bestdist=dist;
				bp=p;
			}
		}
		return bp;
	}

	public String getClothTexturePath() {
		return clothTexture;
	}

	public Culture getCulture() {
		if (vtype==null)
			return null;
		return vtype.culture;
	}

	@Override
	public Entity getEntityToAttack() {
		return entityToAttack;
	}

	public String getFemaleChild() {
		return vtype.femaleChild;
	}

	public int getForeignMerchantPrice(InvItem item) {

		if (getCulture().goodsByItem.containsKey(item)) {

			if (getCulture()!=getTownHall().culture)
				return (int)(getCulture().goodsByItem.get(item).foreignMerchantPrice*1.5);
			else
				return getCulture().goodsByItem.get(item).foreignMerchantPrice;
		}

		return 0;
	}

	public String getGameOccupationName(String playername) {

		if ((getCulture()==null) || (vtype==null))
			return "";

		if (!getCulture().canReadVillagerNames(playername))
			return "";

		if (isChild() && (size == max_child_size))
			return getCulture().getCultureString("villager."+vtype.altkey);
		return getCulture().getCultureString("villager."+vtype.key);
	}

	public String getGameSpeech(String playername) {

		if (getCulture()==null)
			return null;

		if (!getCulture().canReadDialogs(playername))
			return null;

		final Vector<String> variants=getCulture().getSentences(speech_key);

		if ((variants!=null) && (variants.size()>speech_variant)) {
			String s=variants.get(speech_variant).replaceAll("\\$name", playername);

			if (s.split("/").length>1) {
				s=s.split("/")[1].trim();

				int duration=10+(s.length()/5);
				duration=Math.min(duration, 30);

				if ((speech_started + (20*duration)) < worldObj.getWorldTime())
					return null;

				return s;
			}
		} else
			return speech_key;

		return null;
	}

	public int getGatheringRange() {
		return GATHER_RANGE;
	}

	public String getGenderString() {

		if (gender==MALE)
			return "male";
		else
			return "female";
	}

	public Building getGoalBuildingDest() {
		return mw.getBuilding(getGoalBuildingDestPoint());
	}

	public Point getGoalBuildingDestPoint() {
		if (goalInformation==null)
			return null;
		return goalInformation.getDestBuildingPos();
	}

	public Entity getGoalDestEntity() {
		if (goalInformation==null)
			return null;
		return goalInformation.getTargetEnt();
	}

	public Point getGoalDestPoint() {
		if (goalInformation==null)
			return null;
		return goalInformation.getDest();
	}

	public String getGoalLabel(String goal) {
		if (Goal.goals.containsKey(goal))
			return Goal.goals.get(goal).gameName(this);
		else
			return "none";
	}

	public Goal[] getGoals() {
		if (vtype!=null)
			return vtype.goals;
		else
			return null;
	}

	public InvItem[] getGoodsToBringBackHome() {
		return vtype.bringBackHomeGoods;
	}

	public InvItem[] getGoodsToCollect() {
		return vtype.collectGoods;
	}

	@Override
	public int getHealth() {
		return health;
	}

	@Override
	public ItemStack getHeldItem() {
		return heldItem;
	}

	public int getHireCost(EntityPlayer player) {

		int cost=vtype.hireCost;

		if (getTownHall().controlledBy(player.username)) {
			cost=cost/2;
		}

		return cost;
	}

	public Building getHouse() {
		if (house != null)
			return house;
		if ((MLN.LogVillager>=MLN.DEBUG) && extraLog) {
			MLN.debug(this, "Seeking uncached house");
		}
		if (mw!=null) {
			house=mw.getBuilding(housePoint);

			return house;
		}

		return null;
	}

	public Set<InvItem> getInventoryKeys() {
		return inventory.keySet();
	}


	public String getMaleChild() {
		return vtype.maleChild;
	}

	@Override
	public int getMaxHealth() {

		if (vtype==null)//happens when called by Minecraft before the villager is fully created
			return 40;

		if (isChild())
			return 10+size;

		return vtype.health;
	}

	public String getName() {
		return firstName+" "+familyName;
	}

	public String getNameKey() {
		if (vtype==null)
			return "";
		if (isChild() && (size == max_child_size))
			return vtype.altkey;
		return vtype.key;
	}


	public String getNativeOccupationName() {
		if (vtype==null)
			return null;

		if (isChild() && (size == max_child_size))
			return vtype.altname;
		return vtype.name;
	}


	public String getNativeSpeech(String playername) {

		if (getCulture()==null)
			return null;

		final Vector<String> variants=getCulture().getSentences(speech_key);

		if ((variants!=null) && (variants.size()>speech_variant)) {
			String s=variants.get(speech_variant).replaceAll("\\$name", playername);

			if (s.split("/").length>1) {
				s=s.split("/")[0].trim();
			}

			int duration=10+(s.length()/5);
			duration=Math.min(duration, 30);

			if ((speech_started + (20*duration)) < worldObj.getWorldTime())
				return null;

			return s;
		}

		return null;
	}

	public int getNewGender(Building th) {
		return vtype.gender;
	}

	public String getNewName() {
		return getCulture().getRandomNameFromList(vtype.firstNameList);
	}

	public String getNewTexture() {

		if (vtype!=null)
			return vtype.getTexture();
		else
			return null;
	}

	public Point getPathDestPoint() {
		return pathDestPoint;
	}

	private AStarConfig getPathingConfig() {

		if (goalKey!=null)
			return Goal.goals.get(goalKey).getPathingConfig();

		return DEFAULT_JPS_CONFIG;
	}

	public PathPoint getPathPointPos() {
		return new PathPoint(MathHelper.floor_double(this.boundingBox.minX), MathHelper.floor_double(this.boundingBox.minY), MathHelper.floor_double(this.boundingBox.minZ));
	}

	public Point getPos() {
		return new Point(this.posX,this.posY,this.posZ);
	}

	public String getRandomFamilyName() {
		return getCulture().getRandomNameFromList(vtype.familyNameList);
	}

	public VillagerRecord getRecord() {

		if ((getHouse()!=null) && (getHouse().vrecords!=null)) {
			for (final VillagerRecord vr : getHouse().vrecords) {
				if (vr.id==villager_id)
					return vr;
			}
		}

		if ((getTownHall()!=null) && (getTownHall().vrecords!=null)) {
			for (final VillagerRecord vr : getTownHall().vrecords) {
				if (vr.id==villager_id)
					return vr;
			}
		}

		return null;
	}

	public MillVillager getSpouse() {

		if ((getHouse()==null) || isChild())
			return null;

		for (final MillVillager v : getHouse().villagers) {
			if (!v.isChild() && (v.gender!=gender))
				return v;
		}

		return null;
	}

	@Override
	public String getTexture() {
		return texture;
	}

	public InvItem[] getToolsNeeded() {
		if (vtype!=null)
			return vtype.toolsNeeded;
		else
			return null;
	}

	@Override
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

		if ((MLN.LogVillager>=MLN.DEBUG) && extraLog) {
			MLN.debug(this, "Seeking uncached townHall");
		}

		if (mw!=null) {

			townHall=mw.getBuilding(townHallPoint);

			return townHall;
		}

		return null;
	}


	public ItemStack getWeapon() {

		if (isUsingBow) {
			for (final Item weapon : weaponsBow) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}
		}

		if (isUsingHandToHand || !vtype.isArcher) {
			for (final Item weapon : weaponsHandToHand) {
				if (countInv(weapon.itemID)>0)
					return new ItemStack(weapon,1);
			}

			if ((vtype!=null) && (vtype.startingWeapon!=null))
				return new ItemStack(vtype.startingWeapon.id(), 1, vtype.startingWeapon.meta);
		}

		for (final Item weapon : weapons) {
			if (countInv(weapon.itemID)>0)
				return new ItemStack(weapon,1);
		}

		if ((vtype!=null) && (vtype.startingWeapon!=null))
			return new ItemStack(vtype.startingWeapon.id(), 1, vtype.startingWeapon.meta);

		return null;
	}

	public void growSize() {

		int growth=2;

		int nb=0;

		nb=getHouse().takeGoods(Item.egg.itemID, 1);
		if (nb==1) {
			growth+=1+MillCommonUtilities.randomInt(5);
		}

		for (int i=0;i<foodGrowth.length;i++) {
			if ((growth<10) && ((size+growth)<max_child_size)) {
				nb=getHouse().takeGoods(foodGrowth[i].itemID, 1);
				if (nb==1) {
					growth+=foodGrowthValues[i]+MillCommonUtilities.randomInt(foodGrowthValues[i]);
				}
			}
		}

		size+=growth;

		if (size>max_child_size) {
			size=max_child_size;
		}

		getRecord().villagerSize=size;

		adjustSize();
		if (MLN.Children>=MLN.MINOR) {
			MLN.minor(this, "Child growing by "+growth+", new size: "+size);
		}
	}

	private void handleDoorsAndFenceGates() {
		if (doorToClose!=null) {//checking for door to close
			if ((pathEntity==null) || (pathEntity.getCurrentPathLength()==0)//door must be closed
					|| ((pathEntity.getPastTargetPathPoint(2)!=null) && doorToClose.sameBlock(pathEntity.getPastTargetPathPoint(2)))
					) {
				if (getBlock(doorToClose)==Block.doorWood.blockID) {
					final int meta=getBlockMeta(doorToClose);

					if ((meta & 4) == 4) {
						toggleDoor(doorToClose.getiX(),doorToClose.getiY(),doorToClose.getiZ());
					}
					doorToClose=null;
				} else if (getBlock(doorToClose)==Block.fenceGate.blockID) {
					if (closeFenceGate(doorToClose.getiX(),doorToClose.getiY(),doorToClose.getiZ())) {
						doorToClose=null;
					}
				} else {
					doorToClose=null;
				}
			}
		} else {//checking for door to open
			if ((pathEntity!=null) && (pathEntity.getCurrentPathLength()>0)) {
				PathPoint p=null;
				//check for wood door:
				if ((pathEntity.getCurrentTargetPathPoint()!=null) && (worldObj.getBlockId(pathEntity.getCurrentTargetPathPoint().xCoord, pathEntity.getCurrentTargetPathPoint().yCoord, pathEntity.getCurrentTargetPathPoint().zCoord)==Block.doorWood.blockID)) {
					p=pathEntity.getCurrentTargetPathPoint();
				} else if ((pathEntity.getNextTargetPathPoint()!=null) &&
						(worldObj.getBlockId(pathEntity.getNextTargetPathPoint().xCoord, pathEntity.getNextTargetPathPoint().yCoord, pathEntity.getNextTargetPathPoint().zCoord)==Block.doorWood.blockID)) {
					p=pathEntity.getNextTargetPathPoint();
				}

				if (p!=null) {
					final int meta=worldObj.getBlockMetadata(p.xCoord, p.yCoord, p.zCoord);
					if ((meta & 4)==0) {
						toggleDoor(p.xCoord, p.yCoord, p.zCoord);
						doorToClose=new Point(p);
					}
				} else {//check for fence gate:
					if ((pathEntity.getNextTargetPathPoint()!=null) && (worldObj.getBlockId(pathEntity.getNextTargetPathPoint().xCoord,pathEntity.getNextTargetPathPoint().yCoord, pathEntity.getNextTargetPathPoint().zCoord)==Block.fenceGate.blockID)) {
						p=pathEntity.getNextTargetPathPoint();
					} else if ((pathEntity.getCurrentTargetPathPoint()!=null) && (worldObj.getBlockId(pathEntity.getCurrentTargetPathPoint().xCoord, pathEntity.getCurrentTargetPathPoint().yCoord, pathEntity.getCurrentTargetPathPoint().zCoord)==Block.fenceGate.blockID)) {
						p=pathEntity.getCurrentTargetPathPoint();
					}

					if (p!=null) {
						openFenceGate(p.xCoord, p.yCoord, p.zCoord);
						doorToClose=new Point(p);
					}
				}
			}
		}
	}



	private boolean hasBow() {
		for (final Item weapon : weaponsBow) {
			if (countInv(weapon.itemID)>0)
				return true;
		}
		return false;
	}

	public boolean hasChildren() {
		return ((vtype.maleChild!=null) && (vtype.femaleChild!=null));
	}

	@Override
	public int hashCode() {
		return (int)villager_id;
	}

	public boolean helpsInAttacks() {
		return vtype.helpInAttacks;
	}


	public void initialise(VillagerType v, String familyName, boolean respawn) {
		vtype=v;
		villager_id=Math.abs(MillCommonUtilities.randomLong());

		gender=v.gender;
		firstName=getNewName();
		this.familyName=familyName;

		texture = getNewTexture();
		health = getMaxHealth();

		updateClothTexturePath();

		if (isChild()) {
			size=0;
			scale=0.5f;
		} else {
			scale=v.baseScale+((MillCommonUtilities.randomInt(10)-5)/100);
		}

		if (!respawn) {
			for (final InvItem item : v.startingInv.keySet()) {
				addToInv(item.id(),item.meta,v.startingInv.get(item));
			}
		}

		registerInGlobalList();
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {

		if (worldObj.isRemote || this.isVillagerSleeping())
			return true;

		final UserProfile profile=mw.getProfile(entityplayer.username);

		entityplayer.addStat(MillAchievements.firstContact, 1);

		if ((vtype!=null) && (vtype.key.equals("indian_sadhu") || vtype.key.equals("alchemist"))) {
			entityplayer.addStat(MillAchievements.maitreapenser, 1);
		}

		if (profile.villagersInQuests.containsKey(villager_id)) {
			final QuestInstance qi=profile.villagersInQuests.get(villager_id);
			if (qi.getCurrentVillager().id==villager_id) {
				ServerSender.displayQuestGUI(entityplayer, this);
			} else {
				interactSpecial(entityplayer);
			}
		} else {
			interactSpecial(entityplayer);
		}

		if (MLN.DEV) {
			interactDev(entityplayer);
		}

		return true;
	}

	public void interactDev(EntityPlayer entityplayer) {
		DevModUtilities.villagerInteractDev(entityplayer,this);
	}


	public boolean interactSpecial(EntityPlayer entityplayer) {
		if (isChief()) {
			ServerSender.displayVillageChiefGUI(entityplayer, this);
			return true;
		}


		if (canMeditate() && mw.isGlobalTagSet(MillWorld.PUJAS)) {

			if (MLN.Pujas>=MLN.DEBUG) {
				MLN.debug(this, "canMeditate");
			}

			if ((getTownHall().getReputation(entityplayer.username)>=Building.MIN_REPUTATION_FOR_TRADE)) {
				for (final BuildingLocation l : getTownHall().getLocations()) {
					if ((l.level>=0) && (l.getSellingPos() != null) && (l.getSellingPos().distanceTo(this) < 8)) {
						final Building b=l.getBuilding(worldObj);
						if (b.pujas!=null) {

							if (MLN.Pujas>=MLN.DEBUG) {
								MLN.debug(this, "Found shrine: "+b);
							}

							final Point p=b.getPos();

							entityplayer.openGui(Mill.instance, CommonGuiHandler.GUI_PUJAS, worldObj,p.getiX(),p.getiY(),p.getiZ());

							return true;
						}
					}
				}
			} else {
				ServerSender.sendTranslatedSentence(entityplayer,MLN.WHITE, "ui.sellerboycott", getName());
				return false;
			}
		}

		if (isSeller() && !getTownHall().controlledBy(entityplayer.username)) {
			if ((getTownHall().getReputation(entityplayer.username)>=Building.MIN_REPUTATION_FOR_TRADE) && getTownHall().chestLocked) {
				for (final BuildingLocation l : getTownHall().getLocations()) {
					if ((l.level>=0) && (l.shop != null) && (l.shop.length()>0)) {
						if (((l.getSellingPos() != null) && (l.getSellingPos().distanceTo(this) < 5)) || (l.sleepingPos.distanceTo(this) < 5)) {
							ServerSender.displayVillageTradeGUI(entityplayer, l.getBuilding(worldObj));
							return true;
						}
					}
				}
			} else if (!getTownHall().chestLocked) {
				ServerSender.sendTranslatedSentence(entityplayer,MLN.WHITE, "ui.sellernotcurrently possible", getName());
				return false;
			} else {
				ServerSender.sendTranslatedSentence(entityplayer,MLN.WHITE, "ui.sellerboycott", getName());
				return false;
			}
		}

		if (isForeignMerchant()) {
			ServerSender.displayMerchantTradeGUI(entityplayer, this);
			return true;
		}



		if (vtype.hireCost>0) {
			if ((hiredBy==null) || hiredBy.equals(entityplayer.username)) {
				ServerSender.displayHireGUI(entityplayer, this);
				return true;
			} else {
				ServerSender.sendTranslatedSentence(entityplayer,MLN.WHITE, "hire.hiredbyotherplayer",getName(),hiredBy);
				return false;
			}
		}

		if (this.isLocalMerchant()) {
			ServerSender.sendTranslatedSentence(entityplayer,MLN.ORANGE, "other.localmerchantinteract",getName(),hiredBy);
			return false;
		}

		return false;
	}

	public boolean isChief() {
		return vtype.isChief;
	}

	@Override
	public boolean isChild() {
		if (vtype==null)
			return false;
		return vtype.isChild;
	}

	public boolean isForeignMerchant() {
		return vtype.isForeignMerchant;
	}

	public boolean isHostile() {
		return vtype.hostile;
	}

	public boolean isLocalMerchant() {
		return vtype.isLocalMerchant;
	}

	/**
	 * Dead and sleeping entities cannot move
	 */
	@Override
	protected boolean isMovementBlocked()
	{
		return (this.getHealth() <= 0) || this.isVillagerSleeping();
	}

	public boolean isPriest() {
		return vtype.isReligious;
	}

	public boolean isReligious() {
		return vtype.isReligious;
	}

	public boolean isSeller() {
		return vtype.canSell;
	}

	public boolean isTextureValid(String texture) {
		if (vtype!=null)
			return vtype.isTextureValid(texture);
		else
			return true;
	}

	public boolean isVillagerSleeping() {
		return shouldLieDown;
	}

	public boolean isVisitor() {
		if (vtype==null)
			return false;
		return vtype.visitor;
	}

	private void jumpToDest() {

		final Point jumpTo=MillCommonUtilities.findVerticalStandingPos(worldObj, getPathDestPoint());

		if ((jumpTo != null) && (jumpTo.distanceTo(getPathDestPoint())<4)) {
			if ((MLN.Pathing>=MLN.MAJOR) && extraLog) {
				MLN.major(this, "Jumping from "+getPos()+" to "+jumpTo);
			}

			setPosition(jumpTo.getiX()+0.5,jumpTo.getiY()+0.5,jumpTo.getiZ()+0.5);

			longDistanceStuck=0;
			localStuck=0;
		} else {
			if ((goalKey!=null) && Goal.goals.containsKey(goalKey)) {
				final Goal goal=Goal.goals.get(goalKey);
				try {
					goal.unreachableDestination(this);
				} catch (final Exception e) {
					MLN.printException(this+": Exception in handling unreachable dest for goal "+goalKey, e);
				}
			}
		}
	}

	public void killVillager() {
		if (worldObj.isRemote || !(worldObj instanceof WorldServer)) {
			super.setDead();
			return;
		}

		for (final InvItem iv : inventory.keySet()) {
			if (inventory.get(iv)>0) {
				MillCommonUtilities.spawnItem(worldObj, getPos(), new ItemStack(iv.id(),inventory.get(iv),iv.meta), 0);
			}
		}

		if (hiredBy!=null) {
			final EntityPlayer owner=worldObj.getPlayerEntityByName(hiredBy);

			if (owner!=null) {
				ServerSender.sendTranslatedSentence(owner,MLN.WHITE, "hire.hiredied", getName());
			}
		}

		if (getTownHall()!=null) {
			final VillagerRecord vr=getTownHall().getVillagerRecordById(villager_id);

			if (vr!=null) {
				if (MLN.GeneralAI>=MLN.MAJOR) {
					MLN.major(this, getTownHall()+": Villager has been killed!");
				}
				vr.killed=true;
			}
		}

		super.setDead();
	}

	private void leaveVillage() {
		//Foreign merchants leave with their stocks
		for (final InvItem iv : vtype.foreignMerchantStock.keySet()) {
			getHouse().takeGoods(iv.id(),iv.meta, vtype.foreignMerchantStock.get(iv));
		}

		getTownHall().deleteVillager(this);
		getTownHall().removeVillagerRecord(villager_id);
		getHouse().deleteVillager(this);
		getHouse().removeVillagerRecord(villager_id);

		despawnVillager();
	}

	public void localMerchantUpdate() throws Exception {
		if ((getHouse() != null) && (getHouse()==getTownHall())) {

			final Vector<Building> buildings=getTownHall().getBuildingsWithTag(Building.tagInn);
			Building inn=null;

			for (final Building building : buildings) {
				if (building.merchantRecord==null) {
					inn=building;
				}
			}

			if (inn==null) {
				getHouse().removeVillagerRecord(villager_id);
				getHouse().deleteVillager(this);
				despawnVillager();
				MLN.error(this, "Merchant had Town Hall as house and inn is full. Killing him.");
			} else {
				setHousePoint(inn.getPos());
				getHouse().addOrReplaceVillager(this);
				getTownHall().removeVillagerRecord(villager_id);
				final VillagerRecord vr=new VillagerRecord(mw,this);
				getHouse().addOrReplaceRecord(vr);
				getTownHall().addOrReplaceRecord(vr);
				MLN.error(this, "Merchant had Town Hall as house. Moving him to the inn.");
			}
		}
	}

	@Override
	public void onFoundPath(ArrayList<AStarNode> result) {
		pathCalculatedSinceLastTick=result;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		setFacingDirection();

		if (isVillagerSleeping()) {
			motionX=0;
			motionY=0;
			motionZ=0;
		}
	}

	@Override
	public void onNoPathAvailable() {
		pathFailedSincelastTick=true;
		if (MLN.Pathing>=MLN.MINOR) {
			MLN.minor(this, "No path found between "+getPos()+" and "+getPathDestPoint()+" in "+(System.currentTimeMillis()-this.pathCalculationStartTime));
		}
	}
	
	private void updateDialog() {
		
		if (dialogKey==null)
			return;
		
		Dialog d=getCulture().getDialog(dialogKey);
		
		if (d==null) {
			dialogKey=null;
			return;
		}
		
		long timePassed=worldObj.getWorldTime()-dialogStart;
		
		if (d.timeDelays.get(d.timeDelays.size()-1)+2000<timePassed) {
			dialogKey=null;
			return;
		}
		
		String toSpeakKey=null;
		
		
		for (int i=0;i<d.speechBy.size();i++) {
			if (dialogRole==d.speechBy.get(i) && timePassed>=d.timeDelays.get(i)) {
				toSpeakKey="chat_"+d.key+"_"+i;
			}
		}
		
		if (toSpeakKey!=null && (speech_key==null || !speech_key.contains(toSpeakKey))) {
			speakSentence(toSpeakKey,0,10,1);
		}
	}

	@Override
	public void onUpdate() {

		if (vtype==null) {
			if (!isDead) {
				MLN.error(this, "Unknown villager type. Killing him.");
				this.despawnVillagerSilent();
			}
			return;
		}

		registerInGlobalList();

		if (pathFailedSincelastTick) {
			pathFailedSinceLastTick();
		}

		if (pathCalculatedSinceLastTick!=null) {
			applyPathCalculatedSinceLastTick();
		}

		if (MLN.DEV) {
			if (goalKey!=null) {

				final Goal goal=Goal.goals.get(goalKey);

				if ((getPathDestPoint()!=null) && !jpsPathPlanner.isBusy() && (pathEntity==null) && !stopMoving && (goalKey!=null) && !goalKey.equals("gorest") && (getPathDestPoint().distanceTo(getPos())>goal.range(this))) {
					//MLN.error(this, "Null path and not calculating a new one at start? Goal: "+goalKey);
				}
			}
		}

		if (worldObj.isRemote) {
			super.onUpdate();

			try {
				checkRegistration();
			} catch (final MillenaireException e) {
				MLN.printException(this.getName(), e);
			}
			return;
		}

		final long startTime = System.nanoTime();


		if (this.isDead) {
			super.onUpdate();
			return;
		}

		if (hiredBy!=null) {
			updateHired();
			super.onUpdate();
			return;
		}

		if ((getTownHall() != null) && !getTownHall().isActive)
			return;

		try {

			timer++;

			if ((health < getMaxHealth()) & (MillCommonUtilities.randomInt(1600)==0)) {
				health++;
			}

			detrampleCrops();

			allowRandomMoves=true;//overriden inside goal if needed


			if ((getTownHall() == null) || (getHouse() == null)) {
				super.onUpdate();
				return;
			}

			checkRegistration();


			if (Goal.beSeller.key.equals(goalKey)) {
				townHall.seller=this;
			} else if (Goal.getResourcesForBuild.key.equals(goalKey) || Goal.construction.key.equals(goalKey)) {
				if (MLN.TileEntityBuilding>=MLN.DEBUG) {
					MLN.debug(this,"Registering as builder for: "+townHall);
				}
				townHall.builder=this;
			}


			if (getTownHall().underAttack) {


				if (!((goalKey!=null) && (goalKey.equals(Goal.raidVillage.key)
						|| goalKey.equals(Goal.defendVillage.key)
						|| goalKey.equals(Goal.hide.key)))) {
					//we clear any previous dest, it will be set by the goals instead
					clearGoal();
				}
				if (isRaider) {
					goalKey=Goal.raidVillage.key;
					targetDefender();
				} else if (helpsInAttacks()) {
					goalKey=Goal.defendVillage.key;
					targetRaider();
				} else {
					goalKey=Goal.hide.key;
				}
				checkGoals();
			}

			if (entityToAttack != null) {
				if (!entityToAttack.isEntityAlive() || (getPos().distanceTo(entityToAttack) > ATTACK_RANGE) || ((worldObj.difficultySetting == 0) && (entityToAttack instanceof EntityPlayer))) {
					entityToAttack=null;
				} else {
					shouldLieDown=false;
				}
			} else {
				if (isHostile() && (worldObj.difficultySetting>0) && (getTownHall().closestPlayer!=null) && (getPos().distanceTo(getTownHall().closestPlayer) <= ATTACK_RANGE)) {
					entityToAttack=worldObj.getClosestPlayer(posX, posY, posZ, 100);
					clearGoal();
				}
			}

			if (entityToAttack != null) {
				setGoalDestPoint(new Point(entityToAttack));
				heldItem=getWeapon();

				if (goalKey!=null) {
					if (!Goal.goals.get(goalKey).isFightingGoal()) {
						clearGoal();
					}
				}

			} else if (!getTownHall().underAttack) {

				if (worldObj.isDaytime()) {

					speakSentence("greeting",10*60*20,3,10);

					nightActionPerformed=false;

					final InvItem[] goods=getGoodsToCollect();

					if (goods != null) {
						final EntityItem item=getClosestItemVertical(goods,3,30);
						if (item != null) {
							item.setDead();
							if (item.getEntityItem().itemID==Block.sapling.blockID) {
								addToInv(item.getEntityItem().itemID,item.getEntityItem().getItemDamage() & 3,1);
							} else {
								addToInv(item.getEntityItem().itemID,item.getEntityItem().getItemDamage(),1);
							}
						}
					}

					specificUpdate();

					if (!isRaider) {
						if (goalKey == null) {
							setNextGoal();
						}
						if (goalKey != null) {
							checkGoals();
						} else {
							shouldLieDown=false;
						}
					}
				} else {
					hasPrayedToday=false;
					hasDrunkToday=false;

					if (!isRaider) {
						if (goalKey == null) {
							setNextGoal();
						}
						if (goalKey != null) {
							checkGoals();
						} else {
							shouldLieDown=false;
						}
					}
				}
			}

			if ((getPathDestPoint() != null) && (pathEntity != null) && (pathEntity.getCurrentPathLength() > 0) && !stopMoving) {

				//first test for "long distance stuck" - villager not getting closer to goal at all
				double olddistance = prevPoint.horizontalDistanceToSquared(getPathDestPoint());
				double newdistance = getPos().horizontalDistanceToSquared(getPathDestPoint());

				if ((olddistance - newdistance) < 0.0002) {
					longDistanceStuck++;
				} else {
					longDistanceStuck--;
				}

				if (longDistanceStuck < 0) {
					longDistanceStuck=0;
				}
				if ((pathEntity != null) && (pathEntity.getCurrentPathLength() > 1)) {
					if ((MLN.Pathing>=MLN.MINOR) && extraLog) {
						MLN.minor(this, "Stuck: "+longDistanceStuck+" pos "+getPos()+" node: "+pathEntity.getCurrentTargetPathPoint()+" next node: "+pathEntity.getNextTargetPathPoint()+" dest: "+ getPathDestPoint());
					}
				}

				if (longDistanceStuck > 3000) {//pathing has failed, using long-range teleport
					jumpToDest();
				}

				//then test for "local stuck" - villager not progressing in path
				//typically stuck in doorframe or around a corner
				final PathPoint nextPoint=pathEntity.getNextTargetPathPoint();

				if (nextPoint!=null) {

					olddistance = prevPoint.distanceToSquared(nextPoint);
					newdistance = getPos().distanceToSquared(nextPoint);

					if ((olddistance - newdistance) < 0.0002) {
						localStuck+=4;
					} else {
						localStuck--;
					}

					if (localStuck < 0) {
						localStuck=0;
					}

					if (localStuck>30) {
						//will force a path recalculation and maybe avoid new obstacle
						setPathToEntity(null);
						pathEntity=null;
					}

					if (localStuck > 100) {
						//short jump to next point, to clear a villager stuck in a door for example
						setPosition(nextPoint.xCoord+0.5,nextPoint.yCoord+0.5,nextPoint.zCoord+0.5);
						localStuck=0;
					}
				}

			} else {
				longDistanceStuck=0;
				localStuck=0;
			}

			if ((getPathDestPoint() != null) && !stopMoving) {
				updatePathIfNeeded(getPathDestPoint());
			}
			if (stopMoving || this.jpsPathPlanner.isBusy()) {
				setPathToEntity(null);
				pathEntity=null;
			}

			prevPoint=getPos();

			handleDoorsAndFenceGates();

			if ((System.currentTimeMillis() - timeSinceLastPathingTimeDisplay) > 10000) {
				if (pathingTime > 500) {
					if (getPathDestPoint()!=null) {
						MLN.warning(this, "Pathing time in last 10 secs: "+pathingTime
								+" dest: "+getPathDestPoint()+" dest bid: "+MillCommonUtilities.getBlock(worldObj, getPathDestPoint())
								+" above bid: "+MillCommonUtilities.getBlock(worldObj, getPathDestPoint().getAbove()));
					} else {
						MLN.warning(this, "Pathing time in last 10 secs: "+pathingTime
								+" null dest point.");
					}


					MLN.warning(this, "nbPathsCalculated: "+nbPathsCalculated+" nbPathNoStart: "+nbPathNoStart
							+" nbPathNoEnd: "+nbPathNoEnd+" nbPathAborted: "+nbPathAborted
							+" nbPathFailure: "+nbPathFailure);
					String s="";
					for (final PathKey p : abortedKeys) {
						s+=p+"     ";
					}
					MLN.warning(this,  "Aborted keys: "+s);


					if (goalKey != null) {
						MLN.warning(this, "Current goal: "+Goal.goals.get(goalKey));
					}

				}
				timeSinceLastPathingTimeDisplay=System.currentTimeMillis();
				pathingTime=0;
				nbPathsCalculated=0;
				nbPathNoStart=0;
				nbPathNoEnd=0;
				nbPathAborted=0;
				nbPathFailure=0;
				abortedKeys.clear();
			}

			sendVillagerPacket();

		} catch (final MillenaireException e) {
			Mill.proxy.sendChatAdmin(getName()+": Error in onUpdate(). Check millenaire.log.");
			MLN.error(this, e.getMessage());
		} catch (final Exception e) {
			Mill.proxy.sendChatAdmin(getName()+": Error in onUpdate(). Check millenaire.log.");
			MLN.error(this, "Exception in Villager.onUpdate(): ");
			MLN.printException(e);
		}

		triggerMobAttacks();
		
		updateDialog();

		isUsingBow=false;//will be set to true if the NPC is attacking with a bow in attackEntity()
		isUsingHandToHand=false;

		super.onUpdate();

		final double timeInMl=((double)(System.nanoTime()-startTime))/1000000;

		if (MLN.DEV) {
			getTownHall().monitor.addToGoal(goalKey, timeInMl);

			if ((getPathDestPoint()!=null) && !jpsPathPlanner.isBusy() && (pathEntity==null)) {
				//MLN.error(this, "Null path and not calculating a new one? Goal: "+goalKey);
			}

			if ((getPathDestPoint()!=null) && (getGoalDestPoint()!=null) && (getPathDestPoint().distanceTo(getGoalDestPoint())>20)) {
				//MLN.error(this, "pathDestPoint: "+pathDestPoint+", goalDestPoint: "+goalDestPoint+", goal: "+goalKey);
			}

		}
	}

	private boolean openFenceGate(int i, int j, int k)
	{
		int l = worldObj.getBlockMetadata(i, j, k);
		if(!BlockFenceGate.isFenceGateOpen(l)) {
			final int i1 = (MathHelper.floor_double(((rotationYaw * 4F) / 360F) + 0.5D) & 3) % 4;
			final int j1 = BlockDirectional.getDirection(l);
			if(j1 == ((i1 + 2) % 4))
			{
				l = i1;
			}
			MillCommonUtilities.setBlockMetadata(worldObj, i,j,k, l | 4,true);

		}

		return true;
	}

	private void pathFailedSinceLastTick() {
		if (pathfailure>=20) {
			jumpToDest();
			pathfailure=0;
		} else {
			pathfailure++;
			final Point p=MillCommonUtilities.findRandomStandingPosAround(worldObj,getPathDestPoint());
			jpsPathPlanner.stopPathSearch(true);
			if (p!=null) {
				computeNewPath(p);
			} else {
				jumpToDest();
				pathfailure=0;
			}
		}
		pathFailedSincelastTick=false;
	}

	public boolean performNightAction() {

		if (isChild()) {
			if (size < max_child_size) {
				growSize();
			} else {
				teenagerNightAction();
			}
		}

		if (isForeignMerchant()) {
			foreignMerchantNightAction();
		}

		if (hasChildren())
			return attemptChildConception();

		return true;
	}
	public int putInBuilding(Building building,int id,int nb) {
		return putInBuilding(building,id,0,nb);
	}

	public int putInBuilding(Building building,int id,int meta,int nb) {
		nb=takeFromInv(id,meta,nb);
		building.storeGoods(id,meta, nb);

		return nb;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);

		final String type=nbttagcompound.getString("vtype");
		final String culture=nbttagcompound.getString("culture");

		if (Culture.getCultureByName(culture)!=null) {
			if (Culture.getCultureByName(culture).getVillagerType(type)!=null) {
				vtype=Culture.getCultureByName(culture).getVillagerType(type);
			} else {
				MLN.error(this, "Could not load dynamic NPC: unknown type: "+type+" in culture: "+culture);
			}
		} else {
			MLN.error(this, "Could not load dynamic NPC: unknown culture: "+culture);
		}

		texture = nbttagcompound.getString("texture");
		housePoint=Point.read(nbttagcompound, "housePos");
		if (housePoint == null) {
			MLN.error(this, "Error when loading villager: housePoint null");
			Mill.proxy.sendChatAdmin(getName()+": Could not load house position. Check millenaire.log");
		}

		townHallPoint=Point.read(nbttagcompound, "townHallPos");

		if (townHallPoint == null) {
			MLN.error(this, "Error when loading villager: townHallPoint null");
			Mill.proxy.sendChatAdmin(getName()+": Could not load town hall position. Check millenaire.log");
		}

		setGoalDestPoint(Point.read(nbttagcompound, "destPoint"));
		setPathDestPoint(Point.read(nbttagcompound, "pathDestPoint"));
		setGoalBuildingDestPoint(Point.read(nbttagcompound, "destBuildingPoint"));
		prevPoint=Point.read(nbttagcompound, "prevPoint");
		doorToClose=Point.read(nbttagcompound, "doorToClose");
		action=nbttagcompound.getInteger("action");
		goalKey=nbttagcompound.getString("goal");

		if (goalKey.trim().length()==0) {
			goalKey=null;
		}

		if ((goalKey!=null) && !Goal.goals.containsKey(goalKey)) {
			goalKey=null;
		}
		
		
		dialogKey=nbttagcompound.getString("dialogKey");
		dialogStart=nbttagcompound.getLong("dialogStart");
		dialogRole=nbttagcompound.getInteger("dialogRole");
		
		if (dialogKey.trim().length()==0) {
			dialogKey=null;
		}

		familyName=nbttagcompound.getString("familyName");
		firstName=nbttagcompound.getString("firstName");
		scale=nbttagcompound.getFloat("scale");
		gender=nbttagcompound.getInteger("gender");
		//speech_started=nbttagcompound.getLong("lastSpeechLong");

		if (nbttagcompound.hasKey("villager_lid")) {
			villager_id=Math.abs(nbttagcompound.getLong("villager_lid"));
		}

		if (!isTextureValid(texture)) {
			texture=getNewTexture();
		}

		final NBTTagList nbttaglist = nbttagcompound.getTagList("inventory");
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			inventory.put(new InvItem(nbttagcompound1.getInteger("item"),nbttagcompound1.getInteger("meta")), nbttagcompound1.getInteger("amount"));
		}

		previousBlock=nbttagcompound.getInteger("previousBlock");
		previousBlockMeta=nbttagcompound.getInteger("previousBlockMeta");
		size=nbttagcompound.getInteger("size");

		hasPrayedToday=nbttagcompound.getBoolean("hasPrayedToday");
		hasDrunkToday=nbttagcompound.getBoolean("hasDrunkToday");

		hiredBy=nbttagcompound.getString("hiredBy");
		hiredUntil=nbttagcompound.getLong("hiredUntil");
		aggressiveStance=nbttagcompound.getBoolean("aggressiveStance");
		isRaider=nbttagcompound.getBoolean("isRaider");

		if (hiredBy.equals("")) {
			hiredBy=null;
		}

		clothName=nbttagcompound.getString("clothName");
		clothTexture=nbttagcompound.getString("clothTexture");

		if (clothName.equals("")) {
			clothName=null;
			clothTexture=null;
		}

		registerInGlobalList();
		updateClothTexturePath();

	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {


		try {
			villager_id=data.readLong();
			readVillagerStreamdata(data);
			registerInGlobalList();
		} catch (final IOException e) {
			MLN.printException("Error in readSpawnData for villager "+this, e);
		}

	}

	private void readVillagerStreamdata(DataInput data) throws IOException {

		final Culture culture=Culture.getCultureByName(StreamReadWrite.readNullableString(data));

		final String vt=StreamReadWrite.readNullableString(data);
		if (culture!=null) {
			vtype=culture.getVillagerType(vt);
		}

		texture=StreamReadWrite.readNullableString(data);

		goalKey=StreamReadWrite.readNullableString(data);
		housePoint=StreamReadWrite.readNullablePoint(data);
		townHallPoint=StreamReadWrite.readNullablePoint(data);
		firstName=StreamReadWrite.readNullableString(data);
		familyName=StreamReadWrite.readNullableString(data);

		scale=data.readFloat();
		gender=data.readInt();
		size=data.readInt();

		hiredBy=StreamReadWrite.readNullableString(data);
		aggressiveStance=data.readBoolean();
		hiredUntil=data.readLong();
		isUsingBow=data.readBoolean();
		isUsingHandToHand=data.readBoolean();
		speech_key=StreamReadWrite.readNullableString(data);
		speech_variant=data.readInt();
		speech_started=data.readLong();
		heldItem=StreamReadWrite.readNullableItemStack(data);

		inventory=StreamReadWrite.readInventory(data);

		clothName=StreamReadWrite.readNullableString(data);
		clothTexture=StreamReadWrite.readNullableString(data);

		setGoalDestPoint(StreamReadWrite.readNullablePoint(data));
		shouldLieDown=data.readBoolean();

		client_lastupdated=worldObj.getWorldTime();


	}

	public void registerInGlobalList() {
		if (registeredInGlobalList) {
			if (MillCommonUtilities.chanceOn(20)) {
				if (!mw.villagers.containsKey(villager_id)) {
					mw.villagers.put(villager_id, this);
				} else if ((mw.villagers.get(villager_id)!=null) &&
						(mw.villagers.get(villager_id)!=this)
						&& (mw.villagers.get(villager_id).isDead==false)) {//replaced by other villager!
					despawnVillagerSilent();
				}
			}
			return;
		}

		if (mw==null) {
			MLN.error(this, "Could not register as mw is null");
			return;
		}

		if (mw.villagers.containsKey(villager_id)) {
			mw.villagers.get(villager_id).despawnVillagerSilent();
		}

		mw.villagers.put(villager_id, this);

		registeredInGlobalList=true;
	}

	public void registerNewPath(AS_PathEntity path) throws Exception {

		if (path==null) {
			boolean handled=false;
			if (goalKey != null) {
				final Goal goal=Goal.goals.get(goalKey);
				handled=goal.unreachableDestination(this);
			}
			if (!handled) {
				clearGoal();
			}

		} else {
			setPathToEntity(path);
			pathEntity=path;
			moveStrafing=0;
		}

		prevPathPoint=getPathPointPos();

		pathingWorker=null;

	}

	public void registerNewPath(List<PathPoint> result) throws Exception {

		AS_PathEntity path=null;

		if ((result!=null)) {
			final PathPoint[] pointsCopy=new PathPoint[result.size()];

			int i=0;
			for (final PathPoint p : result) {
				if (p == null) {
					pointsCopy[i]=null;
				} else {
					final PathPoint p2=new PathPoint(p.xCoord,p.yCoord,p.zCoord);
					pointsCopy[i]=p2;
				}
				i++;
			}
			path=new AS_PathEntity(pointsCopy);
		}

		registerNewPath(path);
	}

	public void registerNewPathException(Exception e) {
		if ((e instanceof PathingException) && (((PathingException)e).errorCode==PathingException.UNREACHABLE_START)) {
			if ((MLN.Pathing>=MLN.MAJOR) && extraLog) {
				MLN.major(this,"Unreachable start. Jumping back home.");
			}
			setPosition(getHouse().getSleepingPos().x+0.5,getHouse().getSleepingPos().y+1,getHouse().getSleepingPos().z+0.5);
		}
		pathingWorker=null;
	}

	public void registerNewPathInterrupt(PathingWorker worker) {
		if (pathingWorker==worker) {
			pathingWorker=null;
		}
	}

	public HashMap<InvItem, Integer> requiresGoods() {
		if (isChild() && (size<max_child_size))
			return vtype.requiredFoodAndGoods;
		if (hasChildren() && (getHouse().villagers.size()<4))
			return vtype.requiredFoodAndGoods;

		return vtype.requiredGoods;
	}

	private void sendVillagerPacket() {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_VILLAGER);
			writeVillagerStreamData(data);
		} catch (final IOException e) {
			MLN.printException(this+": Error in sendVillagerPacket", e);
		}

		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;

		ServerSender.sendPacketToPlayersInRange(packet,getPos(),30);
	}

	public boolean setBlock(Point p, int blockId)
	{
		return MillCommonUtilities.setBlock(worldObj,p,blockId,true,true);
	}

	public boolean setBlockAndMetadata(Point p, int bid,int metadata)
	{
		return MillCommonUtilities.setBlockAndMetadata(worldObj,p,bid,metadata,true,true);
	}

	public boolean setBlockMetadata(Point p, int metadata)
	{
		return MillCommonUtilities.setBlockMetadata(worldObj,p,metadata);
	}

	@Override
	public void setDead() {
		if (health<=0) {
			killVillager();
		}
	}

	public void setEntityToAttack(Entity ent) {
		entityToAttack=ent;
	}

	private void setFacingDirection() {

		if (entityToAttack!=null) {
			faceEntity(entityToAttack,30,30);
			return;
		}

		if ((goalKey!=null) && ((getGoalDestPoint()!=null) || (getGoalDestEntity()!=null))) {
			final Goal goal=Goal.goals.get(goalKey);

			Point facingPoint;

			if (getGoalDestEntity()!=null) {
				facingPoint=new Point(getGoalDestEntity());
			} else {
				facingPoint=getGoalDestPoint();
			}

			if (goal.lookAtGoal() && (getPos().distanceTo(facingPoint)<goal.range(this))) {
				facePoint(facingPoint,10.0F, 10.0F);
				return;
			}

			if (goal.lookAtPlayer()) {
				final EntityPlayer player=worldObj.getClosestPlayerToEntity(this, 10);
				if (player!=null) {
					faceEntity(player,10,10);
					return;
				}
			}
		}
	}

	public void setGoalBuildingDestPoint(Point newDest) {
		if (goalInformation==null) {
			goalInformation=new GoalInformation(null,null,null);
		}

		goalInformation.setDestBuildingPos(newDest);
	}

	public void setGoalDestEntity(Entity ent) {
		if (goalInformation==null) {
			goalInformation=new GoalInformation(null,null,null);
		}

		goalInformation.setTargetEnt(ent);
		if (ent!=null) {
			setPathDestPoint(new Point(ent));
		}
	}

	public void setGoalDestPoint(Point newDest) {

		if (goalInformation==null) {
			goalInformation=new GoalInformation(null,null,null);
		}

		goalInformation.setDest(newDest);
		setPathDestPoint(newDest);
	}

	public void setGoalInformation(GoalInformation info) {
		goalInformation=info;
		if (info!=null) {
			if (info.getTargetEnt()!=null) {
				setPathDestPoint(new Point(info.getTargetEnt()));
			} else if (info.getDest()!=null) {
				setPathDestPoint(info.getDest());
			} else {
				setPathDestPoint(null);
			}
		} else {
			setPathDestPoint(null);
		}
	}

	public void setHealth(int h) {
		health=h;
	}

	public void setHousePoint(Point p) {
		housePoint=p;
		house=null;
	}

	public void setInv(int id,int nb) {
		setInv(id,0,nb);
	}


	public void setInv(int id,int meta,int nb) {
		inventory.put(new InvItem(id,meta), nb);
		if (getTownHall()!=null) {
			getTownHall().updateVillagerRecord(this);
		}
	}

	public void setNextGoal() throws Exception {

		Goal nextGoal=null;
		clearGoal();

		for (final Goal goal : getGoals()) {
			if (goal.isPossible(this)) {
				if ((MLN.GeneralAI>=MLN.MINOR) && extraLog) {
					MLN.minor(this,"Priority for goal "+goal.gameName(this)+": "+goal.priority(this));
				}
				if ((nextGoal == null) || (nextGoal.priority(this) < goal.priority(this))) {
					nextGoal=goal;
				}
			}
		}

		if ((MLN.GeneralAI>=MLN.MINOR) && extraLog) {
			MLN.minor(this,"Selected this: "+nextGoal);
		}

		if ((MLN.LogBuildingPlan>=MLN.MAJOR) && (nextGoal != null) && nextGoal.key.equals(Goal.getResourcesForBuild.key)) {
			MLN.major(this, getName()+" is new builder, for: "+townHall.getCurrentBuildingPlan()+". Blocks loaded: "+townHall.getBblocks().length);
		}

		if (nextGoal != null) {
			speakSentence(nextGoal.key+".chosen");
			goalKey=nextGoal.key;
			heldItem=null;
			heldItemCount=Integer.MAX_VALUE;
			nextGoal.onAccept(this);
			goalStarted=System.currentTimeMillis();
			lastGoalTime.put(nextGoal, worldObj.getWorldTime());

		} else {
			goalKey=null;
		}

	}

	public void setPathDestPoint(Point newDest) {
		if ((newDest==null) || !newDest.equals(pathDestPoint)) {
			setPathToEntity(null);
			pathEntity=null;
		}

		this.pathDestPoint = newDest;

	}

	public void setTexture(String tx) {
		texture=tx;
	}

	public void setTownHallPoint(Point p) {
		townHallPoint=p;
		townHall=null;
	}

	public void speakSentence(String key) {
		speakSentence(key,30*20,3,1);
	}

	public void speakSentence(String key,int chanceOn) {
		speakSentence(key,30*20,3,chanceOn);
	}

	public void speakSentence(String key,int delay,int distance,int chanceOn) {

		if (delay > (worldObj.getWorldTime()-speech_started))
			return;

		if (!MillCommonUtilities.chanceOn(chanceOn))
			return;

		if ((getTownHall()==null) || (getTownHall().closestPlayer==null) || (getPos().distanceTo(getTownHall().closestPlayer) > distance))
			return;

		key=key.toLowerCase();

		speech_key=null;

		if (getCulture().hasSentences(getNameKey()+"."+key)) {
			speech_key=getNameKey()+"."+key;
		} else if (getCulture().hasSentences(getGenderString()+"."+key)) {
			speech_key=getGenderString()+"."+key;
		} else if (getCulture().hasSentences("villager."+key)) {
			speech_key="villager."+key;
		}

		if (speech_key!=null) {
			speech_variant=MillCommonUtilities.randomInt(getCulture().getSentences(speech_key).size());
			speech_started=worldObj.getWorldTime();
			
			String destName="";
			
			if (dialogKey!=null && goalInformation!=null && goalInformation.getTargetEnt()!=null 
					&& goalInformation.getTargetEnt() instanceof MillVillager) {
				
				MillVillager v=(MillVillager)goalInformation.getTargetEnt();
				
				destName=v.getName();
			}
			
			ServerSender.sendVillageSentenceInRange(worldObj, getPos(), 30,getCulture().key,getName(),destName, speech_key, speech_variant);
			
		}
	}

	public void specificUpdate() throws Exception {
		if (isLocalMerchant()) {
			localMerchantUpdate();
		}
		if (isForeignMerchant()) {
			foreignMerchantUpdate();
		}
	}

	public int takeFromBuilding(Building building,int id,int nb) {
		return takeFromBuilding(building,id,0,nb);
	}

	public int takeFromBuilding(Building building,int id,int meta,int nb) {
		if ((id==Block.wood.blockID) && (meta==-1)) {
			int nb2,total=0;
			nb2=building.takeGoods(id,0, nb);
			addToInv(id,0,nb2);
			total+=nb2;
			nb2=building.takeGoods(id,0, nb-total);
			addToInv(id,0,nb2);
			total+=nb2;
			nb2=building.takeGoods(id,0, nb-total);
			addToInv(id,0,nb2);
			total+=nb2;
			return total;
		}
		nb=building.takeGoods(id,meta, nb);
		addToInv(id,meta,nb);
		return nb;
	}

	public int takeFromInv(int id,int nb) {
		return takeFromInv(id,0,nb);
	}

	public int takeFromInv(int id,int meta,int nb) {

		if ((id==Block.wood.blockID) && (meta==-1)) {
			int total=0,nb2;
			InvItem key=new InvItem(id,0);
			if (inventory.containsKey(key)) {
				nb2=Math.min(nb, inventory.get(key));
				inventory.put(key,inventory.get(key)-nb2);
				total+=nb2;
			}
			key=new InvItem(id,1);
			if (inventory.containsKey(key)) {
				nb2=Math.min(nb-total, inventory.get(key));
				inventory.put(key,inventory.get(key)-nb2);
				total+=nb2;
			}
			key=new InvItem(id,2);
			if (inventory.containsKey(key)) {
				nb2=Math.min(nb-total, inventory.get(key));
				inventory.put(key,inventory.get(key)-nb2);
				total+=nb2;
			}
			if (getTownHall()!=null) {
				getTownHall().updateVillagerRecord(this);
			}
			return total;
		} else {
			final InvItem key=new InvItem(id,meta);
			if (inventory.containsKey(key)) {
				nb=Math.min(nb, inventory.get(key));
				inventory.put(key,inventory.get(key)-nb);
				if (getTownHall()!=null) {
					getTownHall().updateVillagerRecord(this);
				}

				updateClothTexturePath();

				return nb;
			} else
				return 0;
		}


	}

	public int takeFromInv(InvItem item,int nb) {
		return takeFromInv(item.id(),item.meta,nb);
	}

	private void targetDefender() {


		int bestDist=Integer.MAX_VALUE;
		Entity target=null;

		for (final MillVillager v : getTownHall().villagers) {

			if (v.helpsInAttacks() && !v.isRaider) {

				if ((getPos().distanceToSquared(v)<bestDist)) {
					target=v;
					bestDist=(int)getPos().distanceToSquared(v);
				}
			}
		}


		if ((target!=null) && (getPos().distanceToSquared(target)<=25)) {
			entityToAttack=target;
		}
	}

	private void targetRaider() {



		int bestDist=Integer.MAX_VALUE;
		Entity target=null;

		for (final MillVillager v : getTownHall().villagers) {

			if (v.isRaider) {

				if ((getPos().distanceToSquared(v)<bestDist)) {
					target=v;
					bestDist=(int)getPos().distanceToSquared(v);
				}
			}
		}

		if ((target!=null) && (getPos().distanceToSquared(target)<=25)) {
			entityToAttack=target;
		}
	}

	private void teenagerNightAction() {

		//attempt a transfer to an other village to find work
		//we are assuming that in most cases he hasn't found room in his village
		//as otherwise he'd have moved in already

		for (final Point p : getTownHall().getKnownVillages()) {

			if (getTownHall().getRelationWithVillage(p)>Building.RELATION_EXCELLENT) {
				final Building distantVillage=mw.getBuilding(p);

				if ((distantVillage!=null) && (distantVillage.culture==getCulture()) && (distantVillage != getTownHall())) {
					boolean canMoveIn=false;

					if (MLN.Children>=MLN.MAJOR) {
						MLN.major(this, "Attempting to move to village: "+distantVillage.getVillageQualifiedName());
					}


					Building distantInn=null;
					for (final Building distantBuilding : distantVillage.getBuildings()) {
						if (!canMoveIn && (distantBuilding != null) && distantBuilding.isHouse()) {
							if (distantBuilding.canChildMoveIn(gender,familyName)) {
								canMoveIn=true;
							}
						} else if ((distantInn==null) && distantBuilding.isInn) {
							if (distantBuilding.vrecords.size()<2) {
								distantInn=distantBuilding;
							}
						}
					}

					if (canMoveIn && (distantInn!=null)) {

						if (MLN.Children>=MLN.MAJOR) {
							MLN.major(this, "Moving to village: "+distantVillage.getVillageQualifiedName());
						}

						getHouse().transferVillager(getHouse().getVillagerRecordById(villager_id), distantInn,false);
						distantInn.visitorsList.add("panels.childarrived;"+getName()+";"+getTownHall().getVillageQualifiedName());
					}
				}
			}

		}
	}

	public boolean teleportTo(double d, double d1, double d2)
	{
		final double d3 = posX;
		final double d4 = posY;
		final double d5 = posZ;
		posX = d;
		posY = d1;
		posZ = d2;
		boolean flag = false;
		final int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		final int k = MathHelper.floor_double(posZ);
		if(worldObj.blockExists(i, j, k))
		{
			boolean flag1;
			for(flag1 = false; !flag1 && (j > 0);)
			{
				final int i1 = worldObj.getBlockId(i, j - 1, k);
				if((i1 == 0) || !Block.blocksList[i1].blockMaterial.isSolid())
				{
					posY--;
					j--;
				} else
				{
					flag1 = true;
				}
			}

			if(flag1)
			{
				setPosition(posX, posY, posZ);
				if((worldObj.getCollidingBoundingBoxes(this, boundingBox).size() == 0) && !worldObj.isAnyLiquid(boundingBox))
				{
					flag = true;
				}
			}
		}
		if(!flag)
		{
			setPosition(d3, d4, d5);
			return false;
		}

		return true;
	}

	public boolean teleportToEntity(Entity entity)
	{
		Vec3 vec3d = Vec3.createVectorHelper(posX - entity.posX, ((boundingBox.minY + (height / 2.0F)) - entity.posY) + entity.getEyeHeight(), posZ - entity.posZ);
		vec3d = vec3d.normalize();
		final double d = 16D;
		final double d1 = (posX + ((rand.nextDouble() - 0.5D) * 8D)) - (vec3d.xCoord * d);
		final double d2 = (posY + (rand.nextInt(16) - 8)) - (vec3d.yCoord * d);
		final double d3 = (posZ + ((rand.nextDouble() - 0.5D) * 8D)) - (vec3d.zCoord * d);
		return teleportTo(d1, d2, d3);
	}

	private void toggleDoor(int i, int j, int k) {

		final int l = worldObj.getBlockMetadata(i, j, k);


		MillCommonUtilities.setBlockMetadata(worldObj, i,j,k, l ^ 4,true);
		worldObj.markBlockRangeForRenderUpdate(i, j - 1, k, i, j, k);

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName()+"@"+System.identityHashCode(this)+": "+getName()+"/"+this.villager_id+"/"+worldObj;
	}

	private void triggerMobAttacks() {
		//Only spiders as other mobs can be handld by the MC AI system (see MillEventController)
		final List<Entity> entities=MillCommonUtilities.getEntitiesWithinAABB(worldObj, EntityMob.class, getPos(), 16, 5);

		for (final Entity ent : entities) {

			final EntityMob mob=(EntityMob)ent;

			if (mob.getEntityToAttack()==null) {
				if (mob.canEntityBeSeen(this)) {
					mob.setTarget(this);
				}
			}
		}

	}

	private void updateClothTexturePath() {

		if (vtype==null)
			return;

		String bestClothName=null;
		int clothLevel=-1;

		if (vtype.getRandomClothTexture(FREE_CLOTHES)!=null) {
			bestClothName=FREE_CLOTHES;
			clothLevel=0;
		}

		for (final InvItem iv : inventory.keySet()) {
			if ((iv.item == Mill.clothes) && (inventory.get(iv)>0)) {
				if (Mill.clothes.getClothPriority(iv.meta)>clothLevel) {
					//we need to check that the villager has the cloth, but also that he can wear that type
					if (vtype.getRandomClothTexture(Mill.clothes.getClothName(iv.meta))!=null) {
						bestClothName=Mill.clothes.getClothName(iv.meta);
						clothLevel=Mill.clothes.getClothPriority(iv.meta);
					}
				}
			}
		}

		//best cloth to wear has changed
		if (bestClothName!=null) {
			if (!bestClothName.equals(clothName) || !vtype.isClothValid(clothName, clothTexture)) {
				clothName=bestClothName;
				clothTexture=vtype.getRandomClothTexture(bestClothName);
			}
		} else {
			clothName=null;
			clothTexture=null;
		}
	}

	private void updateHired() {

		try {
			if ((health < getMaxHealth()) & (MillCommonUtilities.randomInt(1600)==0)) {
				health++;
			}

			final EntityPlayer entityplayer = worldObj.getPlayerEntityByName(hiredBy);

			if (worldObj.getWorldTime()>hiredUntil) {

				if (entityplayer!=null) {
					ServerSender.sendTranslatedSentence(entityplayer,MLN.WHITE, "hire.hireover", getName());
				}

				hiredBy=null;
				hiredUntil=0;

				final VillagerRecord vr=getTownHall().getVillagerRecordById(villager_id);
				if (vr!=null) {
					vr.awayhired=false;
				}

				return;
			}



			if (entityToAttack != null) {
				if ((getPos().distanceTo(entityToAttack) > ATTACK_RANGE) || (worldObj.difficultySetting == 0)) {
					entityToAttack=null;
				}
			} else {
				if (isHostile() && (worldObj.difficultySetting>0) && (getTownHall().closestPlayer!=null) && (getPos().distanceTo(getTownHall().closestPlayer) <= ATTACK_RANGE)) {
					entityToAttack=worldObj.getClosestPlayer(posX, posY, posZ, 100);

				}
			}

			if (entityToAttack==null) {
				List<?> list = worldObj.getEntitiesWithinAABB(EntityCreature.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(16D, 8D, 16D));

				//first possible target: entity attacking the player
				for (final Object o : list) {
					if (entityToAttack==null) {
						final EntityCreature creature=(EntityCreature)o;

						if ((creature.getEntityToAttack()==entityplayer) && !(creature instanceof EntityCreeper)) {
							entityToAttack=creature;
						}
					}
				}

				//otherwise, any mob or hostile villager
				if ((entityToAttack==null) && aggressiveStance) {
					list = worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(16D, 8D, 16D));

					if(!list.isEmpty())
					{
						entityToAttack=(Entity)list.get(worldObj.rand.nextInt(list.size()));

						if (entityToAttack instanceof EntityCreeper) {
							entityToAttack=null;
						}
					}
					if (entityToAttack==null) {
						list = worldObj.getEntitiesWithinAABB(MillVillager.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(16D, 8D, 16D));

						for (final Object o : list) {
							if (entityToAttack==null) {
								final MillVillager villager=(MillVillager)o;

								if (villager.isHostile()) {
									entityToAttack=villager;
								}
							}
						}
					}

				}

			}


			Entity target=null;

			if (entityToAttack != null) {
				target=entityToAttack;
				heldItem=getWeapon();

				final PathEntity pathentity = worldObj.getPathEntityToEntity(this, target, 16F, true, false, false, true);
				if (pathentity!=null) {
					setPathToEntity(pathentity);
				}

			} else {

				heldItem=null;
				final Entity player=(Entity)mw.world.playerEntities.get(0);
				target=player;

				final int dist=(int) getPos().distanceTo(target);

				if (dist>16) {
					teleportToEntity(player);
				} else if (dist>4) {
					final PathEntity pathentity = worldObj.getPathEntityToEntity(this, target, 16F, true, false, false, true);
					if (pathentity!=null) {
						setPathToEntity(pathentity);
					}
				}
			}


			prevPoint=getPos();

			handleDoorsAndFenceGates();

		} catch (final Exception e) {
			MLN.printException("Error in hired onUpdate():",e);
		}



	}

	private void updatePathIfNeeded(Point dest) throws Exception {
		if (dest==null)
			return;

		if ((pathEntity != null) && (pathEntity.getCurrentPathLength() > 0) && !MillCommonUtilities.chanceOn(50)
				&& (pathEntity.getCurrentTargetPathPoint()!=null)) {//all good
			if (MLN.DEV) {
				getTownHall().monitor.nbPathing++;
				getTownHall().monitor.nbReused++;
			}
			setPathToEntity(pathEntity);//because EntityCreature randomly clears it
		} else {
			if (MLN.jpsPathing) {
				if (!jpsPathPlanner.isBusy()) {
					computeNewPath(dest);				}
			} else {
				if (pathingWorker==null) {//only if no update already running
					computeNewPath(dest);
				}
			}
		}
	}

	public float updateRotation(float f, float f1, float f2) {
		float f3;
		for (f3 = f1 - f; f3 < -180F; f3 += 360F) {
		}
		for (; f3 >= 180F; f3 -= 360F) {
		}
		if (f3 > f2) {
			f3 = f2;
		}
		if (f3 < -f2) {
			f3 = -f2;
		}
		return f + f3;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		try {

			if (vtype==null) {
				MLN.error(this, "Not saving villager due to null vtype.");
				return;
			}


			super.writeEntityToNBT(nbttagcompound);

			nbttagcompound.setString("vtype", vtype.key);


			nbttagcompound.setString("culture", getCulture().key);

			nbttagcompound.setString("texture", texture);
			if (housePoint != null) {
				housePoint.write(nbttagcompound, "housePos");
			}
			if (townHallPoint != null) {
				townHallPoint.write(nbttagcompound, "townHallPos");
			}
			if (getGoalDestPoint() != null) {
				getGoalDestPoint().write(nbttagcompound, "destPoint");
			}
			if (getGoalBuildingDestPoint() != null) {
				getGoalBuildingDestPoint().write(nbttagcompound, "destBuildingPoint");
			}
			if (getPathDestPoint() != null) {
				getPathDestPoint().write(nbttagcompound, "pathDestPoint");
			}
			if (prevPoint != null) {
				prevPoint.write(nbttagcompound, "prevPoint");
			}
			if (doorToClose!=null) {
				doorToClose.write(nbttagcompound, "doorToClose");
			}

			nbttagcompound.setInteger("action", action);
			if (goalKey!=null) {
				nbttagcompound.setString("goal", goalKey);
			}
			nbttagcompound.setString("firstName", firstName);
			nbttagcompound.setString("familyName", familyName);
			nbttagcompound.setFloat("scale", scale);
			nbttagcompound.setInteger("gender", gender);
			nbttagcompound.setLong("lastSpeechLong", speech_started);
			nbttagcompound.setLong("villager_lid", villager_id);

			if (dialogKey!=null) {
				nbttagcompound.setString("dialogKey", dialogKey);
				nbttagcompound.setLong("dialogStart", dialogStart);
				nbttagcompound.setInteger("dialogRole", dialogRole);
			}
			
			final NBTTagList nbttaglist = new NBTTagList();
			for (final InvItem key : inventory.keySet()) {

				final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setInteger("item", key.id());
				nbttagcompound1.setInteger("meta", key.meta);
				nbttagcompound1.setInteger("amount", inventory.get(key));
				nbttaglist.appendTag(nbttagcompound1);

			}
			nbttagcompound.setTag("inventory", nbttaglist);

			nbttagcompound.setInteger("previousBlock", previousBlock);
			nbttagcompound.setInteger("previousBlockMeta", previousBlockMeta);
			nbttagcompound.setInteger("size", size);
			nbttagcompound.setBoolean("hasPrayedToday", hasPrayedToday);
			nbttagcompound.setBoolean("hasDrunkToday", hasDrunkToday);

			if (hiredBy!=null) {
				nbttagcompound.setString("hiredBy", hiredBy);
				nbttagcompound.setLong("hiredUntil", hiredUntil);
				nbttagcompound.setBoolean("aggressiveStance", aggressiveStance);
			}

			nbttagcompound.setBoolean("isRaider", isRaider);

			if (clothName!=null) {
				nbttagcompound.setString("clothName", clothName);
				nbttagcompound.setString("clothTexture", clothTexture);
			}
		} catch (final Exception e) {
			MLN.printException("Exception when attempting to save villager "+this, e);
		}
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {

		try {
			writeVillagerStreamData(data);
		} catch (final IOException e) {
			MLN.printException("Error in writeSpawnData for villager "+this, e);
		}
	}

	private void writeVillagerStreamData(DataOutput data) throws IOException {

		if (vtype==null) {
			MLN.error(this, "Cannot write stream data due to null vtype.");
			return;
		}

		data.writeLong(villager_id);
		StreamReadWrite.writeNullableString(vtype.culture.key,data);
		StreamReadWrite.writeNullableString(vtype.key,data);
		StreamReadWrite.writeNullableString(texture,data);
		StreamReadWrite.writeNullableString(goalKey,data);
		StreamReadWrite.writeNullablePoint(housePoint,data);
		StreamReadWrite.writeNullablePoint(townHallPoint,data);
		StreamReadWrite.writeNullableString(firstName,data);
		StreamReadWrite.writeNullableString(familyName,data);
		data.writeFloat(scale);
		data.writeInt(gender);
		data.writeInt(size);
		StreamReadWrite.writeNullableString(hiredBy,data);
		data.writeBoolean(aggressiveStance);
		data.writeLong(hiredUntil);
		data.writeBoolean(isUsingBow);
		data.writeBoolean(isUsingHandToHand);
		StreamReadWrite.writeNullableString(speech_key,data);
		data.writeInt(speech_variant);
		data.writeLong(speech_started);
		StreamReadWrite.writeNullableItemStack(heldItem, data);
		StreamReadWrite.writeInventory(inventory, data);
		StreamReadWrite.writeNullableString(clothName,data);
		StreamReadWrite.writeNullableString(clothTexture,data);
		StreamReadWrite.writeNullablePoint(getGoalDestPoint(), data);
		data.writeBoolean(shouldLieDown);

	}

}
