package org.millenaire.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;

import org.millenaire.common.Culture.CultureLanguage.Dialogue;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.block.BlockMillCrops;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingLocation;
import org.millenaire.common.core.DevModUtilities;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.CommonGuiHandler;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.Goal.GoalInformation;
import org.millenaire.common.item.Goods;
import org.millenaire.common.item.Goods.ItemMillenaireAxe;
import org.millenaire.common.item.Goods.ItemMillenaireBow;
import org.millenaire.common.item.Goods.ItemMillenaireHoe;
import org.millenaire.common.item.Goods.ItemMillenairePickaxe;
import org.millenaire.common.item.Goods.ItemMillenaireShovel;
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

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class MillVillager extends EntityCreature implements IEntityAdditionalSpawnData, IAStarPathedEntity {

	public static class InvItem implements Comparable<InvItem> {

		public static final int ANYENCHANTED = 1;
		public static final int ENCHANTEDSWORD = 2;

		final public Item item;
		final public Block block;
		final public ItemStack staticStack;
		final public ItemStack[] staticStackArray;
		final public int meta;
		final public int special;

		public InvItem(final Block block) throws MillenaireException {
			this(block, 0);
		}

		public InvItem(final Block block, final int meta) throws MillenaireException {
			this.block = block;
			this.item = Item.getItemFromBlock(block);
			this.meta = meta;
			staticStack = new ItemStack(item, 1, meta);
			staticStackArray = new ItemStack[] { staticStack };
			special = 0;

			checkValidity();
		}

		public InvItem(final int special) throws MillenaireException {
			this.special = special;
			staticStack = null;
			staticStackArray = new ItemStack[] { staticStack };
			item = null;
			block = null;
			meta = 0;

			checkValidity();
		}

		public InvItem(final Item item) throws MillenaireException {
			this(item, 0);
		}

		public InvItem(final Item item, final int meta) throws MillenaireException {
			this.item = item;
			if (Block.getBlockFromItem(item) != Blocks.air) {
				block = Block.getBlockFromItem(item);
			} else {
				block = null;
			}
			this.meta = meta;
			staticStack = new ItemStack(item, 1, meta);
			staticStackArray = new ItemStack[] { staticStack };
			special = 0;

			checkValidity();
		}

		public InvItem(final ItemStack is) throws MillenaireException {
			item = is.getItem();
			if (Block.getBlockFromItem(item) != Blocks.air) {
				block = Block.getBlockFromItem(item);
			} else {
				block = null;
			}
			if (is.getItemDamage() > 0) {
				meta = is.getItemDamage();
			} else {
				meta = 0;
			}
			staticStack = new ItemStack(item, 1, meta);
			staticStackArray = new ItemStack[] { staticStack };
			special = 0;

			checkValidity();
		}

		public InvItem(final String s) throws MillenaireException {
			special = 0;
			if (s.split("/").length > 2) {
				final int id = Integer.parseInt(s.split("/")[0]);

				if (Item.getItemById(id) == null) {
					MLN.printException("Tried creating InvItem with null id from string: " + s, new Exception());
					item = null;
				} else {
					item = Item.getItemById(id);
				}

				if (Block.getBlockById(id) == null) {
					block = null;
				} else {
					block = (Block) Block.blockRegistry.getObjectById(id);
				}

				meta = Integer.parseInt(s.split("/")[1]);
				staticStack = new ItemStack(item, 1, meta);
			} else {
				staticStack = null;
				item = null;
				block = null;
				meta = 0;
			}

			staticStackArray = new ItemStack[] { staticStack };

			checkValidity();
		}

		private void checkValidity() throws MillenaireException {
			if (block == Blocks.air) {
				throw new MillenaireException("Attempted to create an InvItem for air blocks.");
			}
			if (item == null && block == null && special == 0) {
				throw new MillenaireException("Attempted to create an empty InvItem.");
			}
		}

		@Override
		public int compareTo(final InvItem ii) {
			return Item.getIdFromItem(item) * 20 + meta - Item.getIdFromItem(ii.getItem()) * 20 - ii.meta;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof InvItem)) {
				return false;
			}
			final InvItem other = (InvItem) obj;

			return other.item == item && other.meta == meta;
		}

		public Block getBlock() {
			return block;
		}

		public Item getItem() {
			return item;
		}

		public ItemStack getItemStack() {
			if (item == null) {
				return null;
			}
			return new ItemStack(item, 1, meta);
		}

		public String getName() {
			if (special == ANYENCHANTED) {
				return MLN.string("ui.anyenchanted");
			} else if (special == ENCHANTEDSWORD) {
				return MLN.string("ui.enchantedsword");
			} else if (meta == -1 && block == Blocks.log) {
				return MLN.string("ui.woodforplanks");
			} else if (meta == 0 && block == Blocks.log) {
				return MLN.string("ui.woodoak");
			} else if (meta == 1 && block == Blocks.log) {
				return MLN.string("ui.woodpine");
			} else if (meta == 2 && block == Blocks.log) {
				return MLN.string("ui.woodbirch");
			} else if (meta == 3 && block == Blocks.log) {
				return MLN.string("ui.woodjungle");
			} else if (meta == -1) {
				return Mill.proxy.getItemName(item, 0);
			} else {
				if (item != null) {
					return Mill.proxy.getItemName(item, meta);
				} else {
					MLN.printException(new MillenaireException("Trying to get the name of an invalid InvItem."));
					return "";
				}
			}
		}

		public String getTranslationKey() {
			return "_item:" + Item.getIdFromItem(item) + ":" + meta;
		}

		@Override
		public int hashCode() {
			return Item.getIdFromItem(item) + (meta << 8);
		}

		public boolean matches(final InvItem ii) {

			return ii.item == item && (ii.meta == meta || ii.meta == -1 || meta == -1);
		}

		@Override
		public String toString() {
			return getName() + "/" + meta;
		}
	}

	public static class InvItemAlphabeticalComparator implements Comparator<InvItem> {

		@Override
		public int compare(final InvItem arg0, final InvItem arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}

	}

	public static class MLEntityGenericAsymmFemale extends MillVillager {
		public MLEntityGenericAsymmFemale(final World world) {
			super(world);
		}
	}

	public static class MLEntityGenericMale extends MillVillager {
		public MLEntityGenericMale(final World world) {
			super(world);
		}
	}

	public static class MLEntityGenericSymmFemale extends MillVillager {
		public MLEntityGenericSymmFemale(final World world) {
			super(world);
		}
	}

	public static class MLEntityGenericZombie extends MillVillager {
		public MLEntityGenericZombie(final World world) {
			super(world);
		}

		@Override
		public void readSpawnData(final ByteBuf additionalData) {
			// TODO Auto-generated method stub

		}
	}

	private static final double MOVE_SPEED = 0.699D;
	private static final int ATTACK_RANGE_DEFENSIVE = 20;
	private static final String FREE_CLOTHES = "free";

	public static final int CONCEPTION_CHANCE = 6;

	public static final int FOREIGN_MERCHANT_NB_NIGHTS_BEFORE_LEAVING = 5;
	public static final int MALE = 1, FEMALE = 2;

	public static final String GENERIC_VILLAGER = "ml_GenericVillager";
	public static final String GENERIC_ASYMM_FEMALE = "ml_GenericAsimmFemale";
	public static final String GENERIC_SYMM_FEMALE = "ml_GenericSimmFemale";
	public static final String GENERIC_ZOMBIE = "ml_GenericZombie";

	public static ItemStack[] hoeWood = new ItemStack[] { new ItemStack(Items.wooden_hoe, 1) };
	public static ItemStack[] hoeStone = new ItemStack[] { new ItemStack(Items.stone_hoe, 1) };
	public static ItemStack[] hoeSteel = new ItemStack[] { new ItemStack(Items.iron_hoe, 1) };
	public static ItemStack[] hoeNorman = new ItemStack[] { new ItemStack(Mill.normanHoe, 1) };
	public static ItemStack[] hoeMayan = new ItemStack[] { new ItemStack(Mill.mayanHoe, 1) };

	public static ItemStack[] shovelWood = new ItemStack[] { new ItemStack(Items.wooden_shovel, 1) };
	public static ItemStack[] shovelStone = new ItemStack[] { new ItemStack(Items.stone_shovel, 1) };
	public static ItemStack[] shovelSteel = new ItemStack[] { new ItemStack(Items.iron_shovel, 1) };
	public static ItemStack[] shovelNorman = new ItemStack[] { new ItemStack(Mill.normanShovel, 1) };
	public static ItemStack[] shovelMayan = new ItemStack[] { new ItemStack(Mill.mayanShovel, 1) };

	public static ItemStack[] pickaxeWood = new ItemStack[] { new ItemStack(Items.wooden_pickaxe, 1) };
	public static ItemStack[] pickaxeStone = new ItemStack[] { new ItemStack(Items.stone_pickaxe, 1) };
	public static ItemStack[] pickaxeSteel = new ItemStack[] { new ItemStack(Items.iron_pickaxe, 1) };
	public static ItemStack[] pickaxeNorman = new ItemStack[] { new ItemStack(Mill.normanPickaxe, 1) };
	public static ItemStack[] pickaxeMayan = new ItemStack[] { new ItemStack(Mill.mayanPickaxe, 1) };

	public static ItemStack[] axeWood = new ItemStack[] { new ItemStack(Items.wooden_axe, 1) };
	public static ItemStack[] axeStone = new ItemStack[] { new ItemStack(Items.stone_axe, 1) };
	public static ItemStack[] axeSteel = new ItemStack[] { new ItemStack(Items.iron_axe, 1) };
	public static ItemStack[] axeNorman = new ItemStack[] { new ItemStack(Mill.normanAxe, 1) };
	public static ItemStack[] axeMayan = new ItemStack[] { new ItemStack(Mill.mayanAxe, 1) };

	public static ItemStack[] swordWood = new ItemStack[] { new ItemStack(Items.wooden_sword, 1) };
	public static ItemStack[] swordStone = new ItemStack[] { new ItemStack(Items.stone_sword, 1) };
	public static ItemStack[] swordSteel = new ItemStack[] { new ItemStack(Items.iron_sword, 1) };
	public static ItemStack[] swordNorman = new ItemStack[] { new ItemStack(Mill.normanBroadsword, 1) };
	public static ItemStack[] swordMayan = new ItemStack[] { new ItemStack(Mill.mayanMace, 1) };
	public static ItemStack[] swordByzantine = new ItemStack[] { new ItemStack(Mill.byzantineMace, 1) };

	public static final HashMap<String, String[]> oldVillagers = new HashMap<String, String[]>();
	// In descending order of priority:
	public static final Item[] weapons = new Item[] { Mill.normanBroadsword, Mill.tachiSword, Mill.byzantineMace, Items.diamond_sword, Mill.mayanMace, Items.iron_sword, Items.stone_sword,
			Mill.yumiBow, Items.bow, Mill.normanAxe, Mill.mayanAxe, Items.iron_axe, Items.stone_axe, Mill.normanPickaxe, Mill.mayanPickaxe, Items.iron_pickaxe, Items.stone_pickaxe, Mill.normanHoe,
			Mill.mayanHoe, Items.iron_hoe, Items.stone_hoe, Mill.normanShovel, Mill.mayanShovel, Items.stone_shovel, Items.wooden_shovel };

	public static final Item[] weaponsHandToHand = new Item[] { Mill.normanBroadsword, Mill.tachiSword, Mill.byzantineMace, Items.diamond_sword, Mill.mayanMace, Items.iron_sword, Items.stone_sword,
			Mill.normanAxe, Mill.mayanAxe, Items.iron_axe, Items.stone_axe, Mill.normanPickaxe, Mill.mayanPickaxe, Items.iron_pickaxe, Items.stone_pickaxe, Mill.normanHoe, Mill.mayanHoe,
			Items.iron_hoe, Items.stone_hoe, Mill.normanShovel, Mill.mayanShovel, Items.stone_shovel, Items.wooden_shovel };

	public static final Item[] weaponsSwords = new Item[] { Mill.normanBroadsword, Mill.tachiSword, Mill.byzantineMace, Items.diamond_sword, Mill.mayanMace, Items.iron_sword, Items.stone_sword,
			Items.wooden_sword };

	public static final Item[] weaponsRanged = new Item[] { Mill.yumiBow, Items.bow };

	private static final Item[] weaponsBow = new Item[] { Mill.yumiBow, Items.bow };

	public static final Item[] helmets = new Item[] {

	Mill.normanHelmet, Mill.byzantineHelmet, Mill.japaneseWarriorBlueHelmet, Mill.japaneseWarriorRedHelmet, Mill.japaneseGuardHelmet, Items.diamond_helmet, Items.iron_helmet, Items.chainmail_helmet,
			Items.golden_helmet,

			Items.leather_helmet };

	public static final Item[] chestplates = new Item[] { Mill.normanPlate, Mill.byzantinePlate, Mill.japaneseWarriorBluePlate, Mill.japaneseWarriorRedPlate, Mill.japaneseGuardPlate,
			Items.diamond_chestplate, Items.iron_chestplate, Items.chainmail_chestplate, Items.golden_chestplate, Items.leather_chestplate };

	public static final Item[] legs = new Item[] { Mill.normanLegs, Mill.byzantineLegs, Mill.japaneseWarriorBlueLegs, Mill.japaneseWarriorRedLegs, Mill.japaneseGuardLegs, Items.diamond_leggings,
			Items.iron_leggings, Items.chainmail_leggings, Items.golden_leggings, Items.leather_leggings };

	public static final Item[] boots = new Item[] { Mill.normanBoots, Mill.byzantineBoots, Mill.japaneseWarriorBlueBoots, Mill.japaneseWarriorRedBoots, Mill.japaneseGuardBoots, Items.diamond_boots,
			Items.iron_boots, Items.chainmail_boots, Items.golden_boots, Items.leather_boots };

	public static final Item[] pickaxes = new Item[] { Mill.normanPickaxe, Items.diamond_pickaxe, Items.iron_pickaxe, Items.stone_pickaxe, Items.wooden_pickaxe };

	public static final Item[] axes = new Item[] { Mill.normanAxe, Items.diamond_axe, Items.iron_axe, Items.stone_axe, Items.wooden_axe };

	public static final Item[] shovels = new Item[] { Mill.normanShovel, Items.diamond_shovel, Items.iron_shovel, Items.stone_shovel, Items.wooden_shovel };

	public static final Item[] hoes = new Item[] { Mill.normanHoe, Items.diamond_hoe, Items.iron_hoe, Items.stone_hoe, Items.wooden_hoe };

	private static final Item[] foodGrowth = new Item[] { Items.egg, Items.bread, Items.cooked_beef, Items.porkchop, Items.cooked_chicken, Items.cooked_fished, Items.carrot, Items.baked_potato,
			Mill.tripes, Mill.boudin, Mill.vegcurry, Mill.chickencurry, Mill.rice, Mill.masa, Mill.wah, Mill.udon, Mill.ikayaki, Mill.lambCooked, Mill.souvlaki };

	private static final int[] foodGrowthValues = new int[] { 1, 2, 4, 4, 3, 3, 1, 2, 6, 4, 3, 5, 1, 3, 5, 5, 5, 3, 6 };

	// Careful: unlike growth food must be in order from best to worse
	// As villagers will use only the first available
	private static final Item[] foodConception = new Item[] { Mill.wineFancy, Items.cake, Mill.calva, Mill.sake, Mill.cacauhaa, Mill.wineBasic, Mill.cider, Mill.rasgulla, Mill.feta, Items.cookie };

	private static final int[] foodConceptionChanceOn = new int[] { 2, 2, 2, 2, 2, 3, 3, 3, 3, 4 };

	static {
		oldVillagers.put("ml_carpenter", new String[] { "carpenter", "" });
		oldVillagers.put("ml_cattlefarmer", new String[] { "cattlefarmermale", "cattlefarmerfemale" });
		oldVillagers.put("ml_child", new String[] { "boy", "girl" });
		oldVillagers.put("ml_farmer", new String[] { "farmer", "" });
		oldVillagers.put("ml_guard", new String[] { "guard", "" });
		oldVillagers.put("ml_guildmaster", new String[] { "guildmaster", "" });
		oldVillagers.put("ml_knight", new String[] { "knight", "" });
		oldVillagers.put("ml_lady", new String[] { "", "lady" });
		oldVillagers.put("ml_lumberman", new String[] { "lumberman", "" });
		oldVillagers.put("ml_merchant", new String[] { "merchant", "" });
		oldVillagers.put("ml_miner", new String[] { "miner", "" });
		oldVillagers.put("ml_monk", new String[] { "monk", "" });
		oldVillagers.put("ml_priest", new String[] { "priest", "" });
		oldVillagers.put("ml_seneschal", new String[] { "seneschal", "" });
		oldVillagers.put("ml_smith", new String[] { "smith", "" });
		oldVillagers.put("ml_wife", new String[] { "", "wife" });
		oldVillagers.put("ml_foreignmerchant", new String[] { "merchant_weapons", "" });

		oldVillagers.put("ml_indianpeasant", new String[] { "indian_peasant", "" });
		oldVillagers.put("ml_indianchild", new String[] { "indian_boy", "indian_girl" });
		oldVillagers.put("ml_indianarmysmith", new String[] { "indian_armysmith", "" });
		oldVillagers.put("ml_indianlumberman", new String[] { "indian_lumberman", "" });
		oldVillagers.put("ml_indianmerchant", new String[] { "indian_merchant", "" });
		oldVillagers.put("ml_indianminer", new String[] { "indian_miner", "" });
		oldVillagers.put("ml_indianpandit", new String[] { "indian_pandit", "" });
		oldVillagers.put("ml_indianpeasantwife", new String[] { "", "indian_peasantwife" });
		oldVillagers.put("ml_indianraja", new String[] { "indian_raja", "" });
		oldVillagers.put("ml_indianrajputgeneral", new String[] { "indian_rajputgeneral", "" });
		oldVillagers.put("ml_indianvillagechief", new String[] { "indian_villagechief", "" });
		oldVillagers.put("ml_indianrichwoman", new String[] { "", "indian_richwoman" });
		oldVillagers.put("ml_indianscultor", new String[] { "indian_sculptor", "" });
		oldVillagers.put("ml_indiansmith", new String[] { "indian_smith", "" });
		oldVillagers.put("ml_indiansoldier", new String[] { "indian_soldier", "" });
	}
	static final int GATHER_RANGE = 20;// how far a villager will travel to
	// gather a good from getGoodsToGather()

	private static final int HOLD_DURATION = 20;

	public static final int ATTACK_RANGE = 80;
	static public boolean usingCustomPathing = true;
	static public boolean usingBinaryPathing = false;

	public static MillVillager createVillager(Culture c, String type, final int gender, final World world, final Point spawnPos, final Point housePos, final Point thPos, final boolean respawn,
			final String firstName, String familyName) {

		if (world.isRemote || !(world instanceof WorldServer)) {
			MLN.printException("Tried creating a villager in client world: " + world, new Exception());
			return null;
		}

		MillVillager villager;

		if (type == null || type.length() == 0) {
			MLN.error(null, "Tried creating child of null type: " + type);
		}

		// Conversion code for old buildings/villagers
		if (gender > 0 && oldVillagers.containsKey(type.toLowerCase())) {
			if (gender == MALE) {
				type = oldVillagers.get(type.toLowerCase())[0];
			} else {
				type = oldVillagers.get(type.toLowerCase())[1];
			}
		}

		if (c.getVillagerType(type.toLowerCase()) == null) {
			for (final Culture c2 : Culture.ListCultures) {
				if (c2.getVillagerType(type) != null) {
					MLN.error(null, "Could not find villager type " + type + " in culture " + c.key + " but could in " + c2.key + " so switching.");
					c = c2;
				}
			}
		}

		if (c.getVillagerType(type.toLowerCase()) != null) {

			final VillagerType vtype = c.getVillagerType(type.toLowerCase());

			villager = (MillVillager) EntityList.createEntityByName(vtype.getEntityName(), world);

			if (villager == null) {
				MLN.error(c, "Could not create villager of dynamic type: " + type + " entity: " + vtype.getEntityName());
				return null;
			}

			villager.housePoint = housePos;
			villager.townHallPoint = thPos;

			if (familyName == null) {
				familyName = vtype.getRandomFamilyName();
			}
			villager.initialise(vtype, familyName, respawn);

			if (firstName != null) {
				villager.firstName = firstName;
			}

			villager.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

		} else {
			MLN.error(null, "Unknown villager type: " + type + " for culture " + c);
			return null;
		}

		return villager;
	}

	public static void readVillagerPacket(final ByteBufInputStream data) {
		try {
			final long villager_id = data.readLong();

			if (Mill.clientWorld.villagers.containsKey(villager_id)) {
				Mill.clientWorld.villagers.get(villager_id).readVillagerStreamdata(data);
			} else {
				if (MLN.LogNetwork >= MLN.MINOR) {
					MLN.minor(null, "readVillagerPacket for unknown villager: " + villager_id);
				}
			}
		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	public VillagerType vtype;
	public int action = 0;
	public String goalKey = null;
	private GoalInformation goalInformation = null;
	private Point pathDestPoint;
	public PathPoint prevPathPoint;
	private Building house = null, townHall = null;
	public Point housePoint = null;
	public Point prevPoint = null;
	public Point townHallPoint = null;
	public boolean extraLog = false;
	public String firstName = "";
	public String familyName = "";
	public ItemStack heldItem;
	public long timer = 0, actionStart = 0;
	public boolean allowRandomMoves = false, stopMoving = false;
	public PathPoint lastJump = null;
	public float scale = 1;
	public int gender = 0;
	public boolean noHouseorTH = false;
	public boolean registered = false;
	public int longDistanceStuck;
	public boolean nightActionPerformed = false;
	public long speech_started = 0;
	public HashMap<InvItem, Integer> inventory;
	public Block previousBlock;
	public int previousBlockMeta;
	public int size = 0;
	public long pathingTime, timeSinceLastPathingTimeDisplay, villager_id = 0;
	public int nbPathsCalculated = 0, nbPathNoStart = 0, nbPathNoEnd = 0, nbPathAborted = 0, nbPathFailure = 0;
	public List<PathKey> abortedKeys = new ArrayList<PathKey>();
	public long goalStarted = 0;

	public boolean hasPrayedToday = false, hasDrunkToday = false;

	public int heldItemCount = 0, heldItemId = -1;

	public static final int MAX_CHILD_SIZE = 20;
	public String speech_key = null;
	public int speech_variant = 0;

	public String dialogueKey = null;
	public int dialogueRole = 0;
	public long dialogueStart = 0;
	public char dialogueColour = MLN.WHITE;
	public boolean dialogueChat = false;

	// for use client-side ONLY
	public String dialogueTargetFirstName = null;
	public String dialogueTargetLastName = null;

	private Point doorToClose = null;

	public int foreignMerchantNbNights = 0;
	public int foreignMerchantStallId = -1;

	public boolean lastAttackByPlayer = false;

	public HashMap<Goal, Long> lastGoalTime = new HashMap<Goal, Long>();

	public String hiredBy = null;

	public boolean aggressiveStance = false;

	public long hiredUntil = 0;

	public boolean isUsingBow, isUsingHandToHand;
	public boolean isRaider = false;
	private PathingWorker pathingWorker;
	public AStarPathPlanner jpsPathPlanner;
	public static final AStarConfig DEFAULT_JPS_CONFIG = new AStarConfig(true, false, false, true);

	public AS_PathEntity pathEntity;

	public int updateCounter = 0;
	public long client_lastupdated;
	private boolean registeredInGlobalList = false;
	public MillWorld mw;

	public int pathfailure = 0;

	private boolean pathFailedSincelastTick = false;

	private List<AStarNode> pathCalculatedSinceLastTick = null;

	private int localStuck = 0;

	private long pathCalculationStartTime = 0;

	private ResourceLocation clothTexture = null;
	private String clothName = null;

	public boolean shouldLieDown = false;

	public LinkedHashMap<Goods, Integer> merchantSells = new LinkedHashMap<Goods, Integer>();

	public ResourceLocation texture = null;

	public MillVillager(final World world) {

		super(world);
		this.worldObj = world;

		mw = Mill.getMillWorld(world);

		inventory = new HashMap<InvItem, Integer>();
		setHealth(getMaxHealth());

		isImmuneToFire = true;

		client_lastupdated = world.getWorldTime();

		jpsPathPlanner = new AStarPathPlanner(world, this);

		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MOVE_SPEED);

		if (MLN.LogVillagerSpawn >= MLN.DEBUG) {
			final Exception e = new Exception();

			MLN.printException("Creating villager " + this + " in world: " + world, e);
		}
	}

	public void addToInv(final Block block, final int nb) {
		addToInv(Item.getItemFromBlock(block), 0, nb);
	}

	public void addToInv(final Block block, final int meta, final int nb) {
		addToInv(Item.getItemFromBlock(block), meta, nb);
	}

	public void addToInv(final InvItem iv, final int nb) {
		addToInv(iv.getItem(), iv.meta, nb);
	}

	public void addToInv(final Item item, final int nb) {
		addToInv(item, 0, nb);
	}

	public void addToInv(final Item item, final int meta, final int nb) {
		InvItem key;
		try {
			key = new InvItem(item, meta);

			if (inventory.containsKey(key)) {
				inventory.put(key, inventory.get(key) + nb);
			} else {
				inventory.put(key, nb);
			}
			if (getTownHall() != null) {
				getTownHall().updateVillagerRecord(this);
			} else {
				MLN.error(this, "Wanted to update VR after an addToInv but TH is null.");
			}
			updateClothTexturePath();
		} catch (final MillenaireException e) {
			MLN.printException(e);
		}

	}

	public void adjustSize() {
		scale = 0.5f + (float) size / 100;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MOVE_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(computeMaxHealth());
	}

	private void applyPathCalculatedSinceLastTick() {
		// MLN.temp(this,
		// "Path found between "+getPos()+" and "+getPathDestPoint()+" in "+(System.currentTimeMillis()-this.pathCalculationStartTime));

		final AS_PathEntity path = AStarStatic.translateAStarPathtoPathEntity(worldObj, pathCalculatedSinceLastTick, getPathingConfig());

		try {
			registerNewPath(path);

			pathfailure = 0;

		} catch (final Exception e) {
			MLN.printException("Exception when finding JPS path:", e);
		}

		pathCalculatedSinceLastTick = null;
	}

	@Override
	public void attackEntity(final Entity entity, final float f) {
		if (vtype.isArcher && f > 5 && hasBow()) {
			attackEntityBow(entity, f);
			isUsingBow = true;
		} else {
			if (attackTime <= 0 && f < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY) {
				attackTime = 20;
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength());
				swingItem();
			}
			isUsingHandToHand = true;
		}
	}

	public void attackEntityBow(final Entity entity, final float f) {
		if (!(entity instanceof EntityLivingBase)) {
			return;
		}

		if (f < 10F) {
			final double d = entity.posX - posX;
			final double d1 = entity.posZ - posZ;
			if (attackTime == 0) {

				float speedFactor = 1;
				float damageBonus = 0;

				final ItemStack weapon = getWeapon();

				if (weapon != null) {
					final Item item = weapon.getItem();

					if (item instanceof ItemMillenaireBow) {
						final ItemMillenaireBow bow = (ItemMillenaireBow) item;

						if (bow.speedFactor > speedFactor) {
							speedFactor = bow.speedFactor;
						}
						if (bow.damageBonus > damageBonus) {
							damageBonus = bow.damageBonus;
						}
					}
				}

				final EntityArrow arrow = new EntityArrow(this.worldObj, this, (EntityLivingBase) entity, 1.6F, 12.0F);

				this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
				this.worldObj.spawnEntityInWorld(arrow);

				attackTime = 60;

				// faster MLN arrows
				arrow.motionX *= speedFactor;
				arrow.motionY *= speedFactor;
				arrow.motionZ *= speedFactor;

				// extra arrow damage
				arrow.setDamage(arrow.getDamage() + damageBonus);
			}
			rotationYaw = (float) (Math.atan2(d1, d) * 180D / 3.1415927410125732D) - 90F;
			hasAttacked = true;
		}
	}

	@Override
	public boolean attackEntityFrom(final DamageSource ds, final float i) {

		if (ds.getSourceOfDamage() == null && ds != DamageSource.outOfWorld) {
			return false;
		}

		final boolean hadFullHealth = getMaxHealth() == getHealth();

		final boolean b = super.attackEntityFrom(ds, i);

		final Entity entity = ds.getSourceOfDamage();

		lastAttackByPlayer = false;

		if (entity != null) {
			if (entity instanceof EntityLivingBase) {
				if (entity instanceof EntityPlayer) {
					lastAttackByPlayer = true;

					final EntityPlayer player = (EntityPlayer) entity;

					if (!isRaider) {
						if (!vtype.hostile) {
							MillCommonUtilities.getServerProfile(player.worldObj, player.getDisplayName()).adjustReputation(getTownHall(), (int) (-i * 10));
						}
						if (worldObj.difficultySetting != EnumDifficulty.PEACEFUL && this.getHealth() < getMaxHealth() - 10) {
							entityToAttack = entity;
							clearGoal();
							if (getTownHall() != null) {
								getTownHall().callForHelp(entity);
							}
						}

						if (hadFullHealth && (player.getHeldItem() == null || MillCommonUtilities.getItemWeaponDamage(player.getHeldItem().getItem()) <= 1) && !worldObj.isRemote) {
							ServerSender.sendTranslatedSentence(player, MLN.ORANGE, "ui.communicationexplanations");
						}
					}

					if (lastAttackByPlayer && getHealth() <= 0) {
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
		int nbChildren = 0;

		for (final MillVillager villager : getHouse().villagers) {
			if (villager.isChild()) {
				nbChildren++;
			}
		}

		if (nbChildren > 1) {
			if (MLN.LogChildren >= MLN.DEBUG) {
				MLN.debug(this, "Wife already has " + nbChildren + " children, no need for more.");
			}
			return true;
		}

		final int nbChildVillage = getTownHall().countChildren();

		if (nbChildVillage > MLN.maxChildrenNumber) {
			if (MLN.LogChildren >= MLN.DEBUG) {
				MLN.debug(this, "Village already has " + nbChildVillage + ", no need for more.");
			}
			return true;
		}

		boolean couldMoveIn = false;

		for (final Point housePoint : getTownHall().buildings) {

			final Building house = mw.getBuilding(housePoint);
			if (house != null && !house.equals(getHouse()) && house.isHouse()) {

				if (house.canChildMoveIn(MALE, familyName) || house.canChildMoveIn(FEMALE, familyName)) {
					couldMoveIn = true;
				}
			}
		}

		if (nbChildVillage > 5 && !couldMoveIn) {
			if (MLN.LogChildren >= MLN.DEBUG) {
				MLN.debug(this, "Village already has " + nbChildVillage + " and no slot is available for the new child.");
			}
			return true;
		}

		final List<Entity> entities = MillCommonUtilities.getEntitiesWithinAABB(worldObj, MillVillager.class, getPos(), 4, 2);

		boolean manFound = false;

		for (final Entity ent : entities) {
			final MillVillager villager = (MillVillager) ent;
			if (villager.gender == MALE && !villager.isChild()) {
				manFound = true;
			}
		}

		if (!manFound) {
			return false;
		}

		if (MLN.LogChildren >= MLN.DEBUG) {
			MLN.debug(this, "Less than two kids and man present, trying for new child.");
		}

		boolean createChild = false;

		boolean foundConceptionFood = false;

		for (int i = 0; i < foodConception.length && !foundConceptionFood; i++) {
			if (getHouse().countGoods(foodConception[i]) > 0) {
				getHouse().takeGoods(foodConception[i], 1);
				foundConceptionFood = true;
				if (MillCommonUtilities.randomInt(foodConceptionChanceOn[i]) == 0) {
					createChild = true;
					if (MLN.LogChildren >= MLN.MINOR) {
						MLN.minor(this, "Conceiving child with help from: " + foodConception[i].getUnlocalizedName());
					} else if (MLN.LogChildren >= MLN.MINOR) {
						MLN.minor(this, "Failed to conceive child even with help from: " + foodConception[i].getUnlocalizedName());
					}
				}
			}
		}

		if (!foundConceptionFood) {
			if (MillCommonUtilities.randomInt(CONCEPTION_CHANCE) == 0) {
				createChild = true;
				if (MLN.LogChildren >= MLN.MINOR) {
					MLN.minor(this, "Conceiving child without help.");
				}
			} else if (MLN.LogChildren >= MLN.MINOR) {
				MLN.minor(this, "Failed to conceive child without help.");
			}
		}

		if (MLN.DEV) {
			createChild = true;
		}

		if (createChild) {
			getHouse().createChild(this, getTownHall(), getRecord().spousesName);
		}

		return true;
	}

	public void calculateMerchantGoods() {
		for (final InvItem key : vtype.foreignMerchantStock.keySet()) {
			if (getCulture().goodsByItem.containsKey(key) && getBasicForeignMerchantPrice(key) > 0) {
				merchantSells.put(getCulture().goodsByItem.get(key), getBasicForeignMerchantPrice(key));
			}
		}
	}

	@Override
	public boolean canDespawn() {
		return false;
	}

	public boolean canMeditate() {
		return vtype.canMeditate;
	}

	public boolean canPerformSacrifices() {
		return vtype.canPerformSacrifices;
	}

	public void checkGoals() throws Exception {

		final Goal goal = Goal.goals.get(goalKey);

		if (goal == null) {
			MLN.error(this, "Invalid goal key: " + goalKey);
			goalKey = null;
			return;
		}

		if (getGoalDestEntity() != null) {
			if (getGoalDestEntity().isDead) {
				setGoalDestEntity(null);
				setPathDestPoint(null);
			} else {
				setPathDestPoint(new Point(getGoalDestEntity()));
			}
		}

		Point target = null;

		boolean continuingGoal = true;

		if (getPathDestPoint() != null) {
			target = getPathDestPoint();
			if (pathEntity != null && pathEntity.getCurrentPathLength() > 0) {
				target = new Point(pathEntity.getFinalPathPoint());
			}
		}
		speakSentence(goal.sentenceKey());

		if (getGoalDestPoint() == null && getGoalDestEntity() == null) {
			goal.setVillagerDest(this);
			if (MLN.LogGeneralAI >= MLN.MINOR && extraLog) {
				MLN.minor(this, "Goal destination: " + getGoalDestPoint() + "/" + getGoalDestEntity());
			}
		} else if (target != null && target.horizontalDistanceTo(this) < goal.range(this)) {
			if (actionStart == 0) {
				stopMoving = goal.stopMovingWhileWorking();
				actionStart = System.currentTimeMillis();
				shouldLieDown = goal.shouldVillagerLieDown();

				if (MLN.LogGeneralAI >= MLN.MINOR && extraLog) {
					MLN.minor(this, "Starting action: " + actionStart);
				}
			}

			if (System.currentTimeMillis() - actionStart >= goal.actionDuration(this)) {
				if (goal.performAction(this)) {
					clearGoal();
					goalKey = goal.nextGoal(this);
					stopMoving = false;
					shouldLieDown = false;
					heldItem = null;
					continuingGoal = false;
					if (MLN.LogGeneralAI >= MLN.MINOR && extraLog) {
						MLN.minor(this, "Goal performed. Now doing: " + goalKey);
					}
				} else {
					stopMoving = goal.stopMovingWhileWorking();
				}
				actionStart = 0;
				goalStarted = System.currentTimeMillis();
			}
		} else {
			stopMoving = false;
			shouldLieDown = false;
		}

		if (!continuingGoal) {
			return;
		}

		if (goal.isStillValid(this)) {

			if (System.currentTimeMillis() - goalStarted > goal.stuckDelay(this)) {

				final boolean actionDone = goal.stuckAction(this);

				if (actionDone) {
					goalStarted = System.currentTimeMillis();
				}

				if (goal.isStillValid(this)) {
					allowRandomMoves = goal.allowRandomMoves();
					if (stopMoving) {
						setPathToEntity(null);
						pathEntity = null;
					}
					if (heldItemCount > HOLD_DURATION) {
						ItemStack[] heldItems = null;
						if (target != null && target.horizontalDistanceTo(this) < goal.range(this)) {
							heldItems = goal.getHeldItemsDestination(this);
						} else {
							heldItems = goal.getHeldItemsTravelling(this);
						}
						if (heldItems != null && heldItems.length > 0) {
							heldItemId = (heldItemId + 1) % heldItems.length;
							heldItem = heldItems[heldItemId];
						}
						heldItemCount = 0;
					}

					if (heldItemCount == 0 && goal.swingArms(this)) {
						this.swingItem();
					}

					heldItemCount++;

				}
			} else {
				if (heldItemCount > HOLD_DURATION) {
					ItemStack[] heldItems = null;
					if (target != null && target.horizontalDistanceTo(this) < goal.range(this)) {
						heldItems = goal.getHeldItemsDestination(this);
					} else {
						heldItems = goal.getHeldItemsTravelling(this);
					}
					if (heldItems != null && heldItems.length > 0) {
						heldItemId = (heldItemId + 1) % heldItems.length;
						heldItem = heldItems[heldItemId];
					}
					heldItemCount = 0;
				}

				if (heldItemCount == 0 && goal.swingArms(this)) {
					this.swingItem();
				}

				heldItemCount++;
			}
		} else {
			stopMoving = false;
			shouldLieDown = false;
			goal.onComplete(this);
			clearGoal();
			goalKey = goal.nextGoal(this);
			heldItemCount = HOLD_DURATION + 1;
			heldItemId = -1;
		}

	}

	private void checkRegistration() throws MillenaireException {
		if (!registered || MillCommonUtilities.chanceOn(100)) {
			if (getHouse() != null) {
				if (!getHouse().villagers.contains(this)) {
					getHouse().registerVillager(this);
					if (MLN.LogOther >= MLN.DEBUG) {
						MLN.debug(this, "Registering in house List.");
					}
				}
			}
			if (getTownHall() != null) {
				if (!getTownHall().villagers.contains(this)) {
					getTownHall().registerVillager(this);
					if (MLN.LogOther >= MLN.DEBUG) {
						MLN.debug(this, "Registering in TH List.");
					}
				}
			}

			registered = true;
		}
	}

	public void clearGoal() {
		setGoalDestPoint(null);
		setGoalBuildingDestPoint(null);
		setGoalDestEntity(null);
		goalKey = null;
		shouldLieDown = false;
	}

	private boolean closeFenceGate(final int i, final int j, final int k) {
		final int l = worldObj.getBlockMetadata(i, j, k);
		if (BlockFenceGate.isFenceGateOpen(l)) {
			MillCommonUtilities.setBlockMetadata(worldObj, i, j, k, l & -5, true);

			return true;
		}
		return false;
	}

	public float computeMaxHealth() {

		if (vtype == null) {
			return 40;
		}

		if (isChild()) {
			return 10 + size;
		}

		return vtype.health;
	}

	private List<PathPoint> computeNewPath(final Point dest) {

		if (getPos().sameBlock(dest)) {
			return null;
		}

		if (usingCustomPathing) {

			if (MLN.jpsPathing) {

				if (jpsPathPlanner.isBusy()) {
					jpsPathPlanner.stopPathSearch(true);
				}

				AStarNode destNode = null;

				final AStarNode[] possibles = AStarStatic.getAccessNodesSorted(worldObj, doubleToInt(posX), doubleToInt(posY), doubleToInt(posZ), getPathDestPoint().getiX(), getPathDestPoint()
						.getiY(), getPathDestPoint().getiZ(), getPathingConfig());
				if (possibles.length != 0) {
					destNode = possibles[0];
				}

				if (destNode != null) {
					pathCalculationStartTime = System.currentTimeMillis();
					jpsPathPlanner.getPath(doubleToInt(this.posX), doubleToInt(this.posY) - 1, doubleToInt(this.posZ), destNode.x, destNode.y, destNode.z, getPathingConfig());
				} else {
					onNoPathAvailable();
				}

			} else {

				if (pathingWorker != null) {
					pathingWorker.interrupt();
				}

				pathingWorker = townHall.calculatePath(this, getPos(), dest, extraLog);
			}

			return null;
		} else {
			final List<PathPoint> pp = new ArrayList<PathPoint>();

			final PathEntity pe = worldObj.getEntityPathToXYZ(this, dest.getiX(), dest.getiY(), dest.getiZ(), (float) (getPos().distanceTo(dest) + 16), true, false, false, true);

			if (pe == null) {
				return null;
			}

			for (int i = 0; i < pe.getCurrentPathLength(); i++) {
				pp.add(pe.getPathPointFromIndex(i));
			}

			return pp;
		}
	}

	public int countBlocksAround(final int x, final int y, final int z, final int rx, final int ry, final int rz) {
		return MillCommonUtilities.countBlocksAround(worldObj, x, y, z, rx, ry, rz);
	}

	public int countInv(final Block block, final int meta) {
		try {
			return countInv(new InvItem(Item.getItemFromBlock(block), meta));
		} catch (final MillenaireException e) {
			MLN.printException(e);
			return 0;
		}
	}

	public int countInv(final InvItem key) {

		if (key.meta == -1) {// undefined, so has to try the 16 possible values
			int nb = 0;
			for (int i = 0; i < 16; i++) {
				InvItem tkey;
				try {
					tkey = new InvItem(key.item, i);
					if (inventory.containsKey(tkey)) {
						nb += inventory.get(tkey);
					}
				} catch (final MillenaireException e) {
					MLN.printException(e);
				}

			}
			return nb;
		}

		if (inventory.containsKey(key)) {
			return inventory.get(key);
		} else {
			return 0;
		}
	}

	public int countInv(final Item item) {
		return countInv(item, 0);
	}

	public int countInv(final Item item, final int meta) {
		try {
			return countInv(new InvItem(item, meta));
		} catch (final MillenaireException e) {
			MLN.printException(e);
			return 0;
		}
	}

	public int countItemsAround(final Item item, final int radius) {
		return countItemsAround(new Item[] { item }, radius);
	}

	public int countItemsAround(final Item[] items, final int radius) {
		final List<Entity> list = MillCommonUtilities.getEntitiesWithinAABB(worldObj, EntityItem.class, getPos(), radius, radius);

		int count = 0;

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getClass() == EntityItem.class) {
					final EntityItem entity = (EntityItem) list.get(i);

					if (!entity.isDead) {
						for (final Item id : items) {
							if (id == entity.getEntityItem().getItem()) {
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

		if (worldObj.isRemote) {
			return;// server-side only
		}

		if (hiredBy != null) {
			final EntityPlayer owner = worldObj.getPlayerEntityByName(hiredBy);

			if (owner != null) {
				ServerSender.sendTranslatedSentence(owner, MLN.DARKRED, "hire.hiredied", getName());
			}
		}

		super.setDead();
	}

	public void despawnVillagerSilent() {

		if (MLN.LogVillagerSpawn >= MLN.DEBUG) {
			final Exception e = new Exception();

			MLN.printException("Despawning villager: " + this, e);
		}

		super.setDead();
	}

	public void detrampleCrops() {
		if (getPos().sameBlock(prevPoint) && (previousBlock == Blocks.wheat || previousBlock instanceof BlockMillCrops) && getBlock(getPos()) != Blocks.air
				&& getBlock(getPos().getBelow()) == Blocks.dirt) {
			setBlock(getPos(), previousBlock);
			setBlockMetadata(getPos(), previousBlockMeta);
			setBlock(getPos().getBelow(), Blocks.farmland);
		}

		previousBlock = getBlock(getPos());
		previousBlockMeta = getBlockMeta(getPos());
	}

	public int doubleToInt(final double input) {
		return AStarStatic.getIntCoordFromDoubleCoord(input);
	}

	@Override
	public boolean equals(final Object obj) {

		if (obj == null || !(obj instanceof MillVillager)) {
			return false;
		}

		final MillVillager v = (MillVillager) obj;

		return villager_id == v.villager_id;

	}

	// emptied to prevent generic code from turning the villagers' heads toward
	// the player
	@Override
	public void faceEntity(final Entity par1Entity, final float par2, final float par3) {

	}

	public void faceEntityMill(final Entity par1Entity, final float par2, final float par3) {
		final double d0 = par1Entity.posX - this.posX;
		final double d1 = par1Entity.posZ - this.posZ;
		double d2;

		if (par1Entity instanceof EntityLivingBase) {
			final EntityLivingBase entityliving = (EntityLivingBase) par1Entity;
			d2 = entityliving.posY + entityliving.getEyeHeight() - (this.posY + this.getEyeHeight());
		} else {
			d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (this.posY + this.getEyeHeight());
		}

		final double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		final float f2 = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
		final float f3 = (float) -(Math.atan2(d2, d3) * 180.0D / Math.PI);
		this.rotationPitch = this.updateRotation(this.rotationPitch, f3, par3);
		this.rotationYaw = this.updateRotation(this.rotationYaw, f2, par2);
	}

	public void facePoint(final Point p, final float par2, final float par3) {
		final double x = p.x - this.posX;
		final double z = p.z - this.posZ;
		final double y = p.y - (this.posY + this.getEyeHeight());

		getLookHelper().setLookPosition(x, y, z, 10.0F, getVerticalFaceSpeed());
	}

	private boolean foreignMerchantNightAction() {
		foreignMerchantNbNights++;

		if (foreignMerchantNbNights > FOREIGN_MERCHANT_NB_NIGHTS_BEFORE_LEAVING) {
			leaveVillage();
		} else {
			boolean hasItems = false;
			for (final InvItem key : vtype.foreignMerchantStock.keySet()) {
				if (getHouse().countGoods(key) > 0) {
					hasItems = true;
				}
			}
			if (!hasItems) {
				leaveVillage();
			}
		}

		return true;
	}

	private void foreignMerchantUpdate() {
		if (foreignMerchantStallId < 0) {
			for (int i = 0; i < getHouse().getResManager().stalls.size() && foreignMerchantStallId < 0; i++) {
				boolean taken = false;
				for (final MillVillager v : getHouse().villagers) {
					if (v.foreignMerchantStallId == i) {
						taken = true;
					}
				}
				if (!taken) {
					foreignMerchantStallId = i;
				}
			}
		}
		if (foreignMerchantStallId < 0) {
			foreignMerchantStallId = 0;
		}
	}

	@Override
	public ItemStack func_130225_q(final int type) {

		if (type == 0) {
			for (final Item weapon : helmets) {
				if (countInv(weapon) > 0) {
					return new ItemStack(weapon, 1);
				}
			}
			return null;
		}
		if (type == 1) {
			for (final Item weapon : chestplates) {
				if (countInv(weapon) > 0) {
					return new ItemStack(weapon, 1);
				}
			}
			return null;
		}
		if (type == 2) {
			for (final Item weapon : legs) {
				if (countInv(weapon) > 0) {
					return new ItemStack(weapon, 1);
				}
			}
			return null;
		}
		if (type == 3) {
			for (final Item weapon : boots) {
				if (countInv(weapon) > 0) {
					return new ItemStack(weapon, 1);
				}
			}
			return null;
		}

		return null;
	}

	public boolean gathersApples() {
		return vtype.gathersApples;
	}

	public String getActionLabel(final int action) {
		return "none";
	}

	public int getAttackStrength() {
		int attackStrength = vtype.baseAttackStrength;

		final ItemStack weapon = getWeapon();
		if (weapon != null) {
			attackStrength += Math.ceil((float) MillCommonUtilities.getItemWeaponDamage(weapon.getItem()) / 2);
		}

		return attackStrength;
	}

	public int getBasicForeignMerchantPrice(final InvItem item) {

		if (getTownHall() == null) {
			return 0;
		}

		if (getCulture().goodsByItem.containsKey(item)) {

			if (getCulture() != getTownHall().culture) {
				return (int) (getCulture().goodsByItem.get(item).foreignMerchantPrice * 1.5);
			} else {
				return getCulture().goodsByItem.get(item).foreignMerchantPrice;
			}
		}

		return 0;
	}

	public float getBedOrientationInDegrees() {

		Point ref = getPos();

		if (getGoalDestPoint() != null) {
			ref = getGoalDestPoint();
		}

		final int x = (int) ref.x;
		final int y = (int) ref.y;
		final int z = (int) ref.z;
		final Block block = worldObj.getBlock(x, y, z);

		if (block == Blocks.bed) {
			final int var2 = block == null ? 0 : block.getBedDirection(worldObj, x, y, z);

			switch (var2) {
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

			if (worldObj.getBlock(x + 1, y, z) == Blocks.air) {
				return 0.0F;
			} else if (worldObj.getBlock(x, y, z + 1) == Blocks.air) {
				return 90.0F;
			} else if (worldObj.getBlock(x - 1, y, z) == Blocks.air) {
				return 180.0F;
			} else if (worldObj.getBlock(x, y, z - 1) == Blocks.air) {
				return 270.0F;
			}

		}

		return 0.0F;
	}

	public ItemTool getBestAxe() {
		ItemTool bestTool = (ItemTool) Items.wooden_axe;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemAxe || item.item instanceof ItemMillenaireAxe)) {
					final ItemTool tool = (ItemTool) item.item;
					if (tool.func_150893_a(item.staticStack, Blocks.log) > bestRating) {
						bestTool = tool;
						bestRating = tool.func_150893_a(item.staticStack, Blocks.stone);
					}
				}
			}
		}

		return bestTool;
	}

	public ItemStack[] getBestAxeStack() {

		ItemStack[] bestTool = axeWood;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemAxe || item.item instanceof ItemMillenaireAxe)) {
					final ItemTool tool = (ItemTool) item.item;
					if (tool.func_150893_a(item.staticStack, Blocks.log) > bestRating) {
						bestTool = item.staticStackArray;
						bestRating = tool.func_150893_a(item.staticStack, Blocks.stone);
					}
				}
			}
		}

		return bestTool;
	}

	public Item getBestHoe() {
		Item bestTool = Items.wooden_hoe;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemHoe || item.item instanceof ItemMillenaireHoe)) {

					if (item.item.getMaxDamage() > bestRating) {
						bestTool = item.item;
						bestRating = item.item.getMaxDamage();
					}
				}
			}
		}

		return bestTool;
	}

	public ItemStack[] getBestHoeStack() {
		ItemStack[] bestTool = hoeWood;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemHoe || item.item instanceof ItemMillenaireHoe)) {

					if (item.item.getMaxDamage() > bestRating) {
						bestTool = item.staticStackArray;
						bestRating = item.item.getMaxDamage();
					}
				}
			}
		}

		return bestTool;
	}

	public ItemTool getBestPickaxe() {
		ItemTool bestTool = (ItemTool) Items.wooden_pickaxe;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemPickaxe || item.item instanceof ItemMillenairePickaxe)) {
					final ItemTool tool = (ItemTool) item.item;
					if (tool.func_150893_a(item.staticStack, Blocks.stone) > bestRating) {
						bestTool = tool;
						bestRating = tool.func_150893_a(item.staticStack, Blocks.stone);
					}
				}
			}
		}

		return bestTool;
	}

	public ItemStack[] getBestPickaxeStack() {

		ItemStack[] bestTool = pickaxeWood;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemPickaxe || item.item instanceof ItemMillenairePickaxe)) {
					final ItemTool tool = (ItemTool) item.item;
					if (tool.func_150893_a(item.staticStack, Blocks.stone) > bestRating) {
						bestTool = item.staticStackArray;
						bestRating = tool.func_150893_a(item.staticStack, Blocks.stone);
					}
				}
			}
		}

		return bestTool;
	}

	public ItemTool getBestShovel() {
		ItemTool bestTool = (ItemTool) Items.wooden_shovel;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemSpade || item.item instanceof ItemMillenaireShovel)) {
					final ItemTool tool = (ItemTool) item.item;
					if (tool.func_150893_a(item.staticStack, Blocks.dirt) > bestRating) {
						bestTool = tool;
						bestRating = tool.func_150893_a(item.staticStack, Blocks.stone);
					}
				}
			}
		}

		return bestTool;
	}

	public ItemStack[] getBestShovelStack() {
		ItemStack[] bestTool = shovelWood;
		float bestRating = 0;

		for (final InvItem item : inventory.keySet()) {
			if (inventory.get(item) > 0) {
				if (item.staticStack != null && (item.item instanceof ItemSpade || item.item instanceof ItemMillenaireShovel)) {
					final ItemTool tool = (ItemTool) item.item;
					if (tool.func_150893_a(item.staticStack, Blocks.dirt) > bestRating) {
						bestTool = item.staticStackArray;
						bestRating = tool.func_150893_a(item.staticStack, Blocks.stone);
					}
				}
			}
		}

		return bestTool;
	}

	public Block getBlock(final Point p) {
		return MillCommonUtilities.getBlock(worldObj, p);
	}

	public int getBlockMeta(final Point p) {
		return MillCommonUtilities.getBlockMeta(worldObj, p);
	}

	@Override
	public float getBlockPathWeight(final int i, final int j, final int k) {

		if (!allowRandomMoves) {
			if (MLN.LogPathing >= MLN.DEBUG && extraLog) {
				MLN.debug(this, "Forbiding random moves. Current goal: " + Goal.goals.get(goalKey) + " Returning: " + -99999F);
			}
			return Float.NEGATIVE_INFINITY;
		}

		final Point rp = new Point(i, j, k);
		final double dist = rp.distanceTo(housePoint);
		if (worldObj.getBlock(i, j - 1, k) == Blocks.farmland) {
			return -50;
		} else if (dist > 10) {
			return -(float) dist;
		} else {
			return MillCommonUtilities.randomInt(10);
		}
	}

	public Point getClosest(final List<Point> points) {
		double bestdist = Double.MAX_VALUE;
		Point bp = null;
		;

		for (final Point p : points) {
			final double dist = p.distanceToSquared(this);
			if (dist < bestdist) {
				bestdist = dist;
				bp = p;
			}
		}
		return bp;
	}

	public Point getClosestBlock(final Block[] blockIds, final Point pos, final int rx, final int ry, final int rz) {
		return MillCommonUtilities.getClosestBlock(worldObj, blockIds, pos, rx, ry, rz);
	}

	public Point getClosestBlockMeta(final Block[] blockIds, final int meta, final Point pos, final int rx, final int ry, final int rz) {
		return MillCommonUtilities.getClosestBlockMeta(worldObj, blockIds, meta, pos, rx, ry, rz);
	}

	public Point getClosestHorizontal(final List<Point> points) {
		double bestdist = Double.MAX_VALUE;
		Point bp = null;

		for (final Point p : points) {
			final double dist = p.horizontalDistanceToSquared(this);
			if (dist < bestdist) {
				bestdist = dist;
				bp = p;
			}
		}
		return bp;
	}

	public Point getClosestHorizontalWithAltitudeCost(final List<Point> points, final float vCost) {
		double bestdist = Double.MAX_VALUE;
		Point bp = null;

		for (final Point p : points) {
			double dist = p.horizontalDistanceToSquared(this);
			dist += Math.abs(townHall.getAltitude((int) posX, (int) posZ) - townHall.getAltitude(p.getiX(), p.getiZ())) * vCost;

			if (dist < bestdist) {
				bestdist = dist;
				bp = p;
			}
		}
		return bp;
	}

	public EntityItem getClosestItemVertical(final InvItem item, final int radius, final int vertical) {
		return getClosestItemVertical(new InvItem[] { item }, radius, vertical);
	}

	public EntityItem getClosestItemVertical(final InvItem[] items, final int radius, final int vertical) {
		return MillCommonUtilities.getClosestItemVertical(worldObj, getPos(), items, radius, vertical);
	}

	public Point getClosestToHouse(final List<Point> points) {
		double bestdist = Double.MAX_VALUE;
		Point bp = null;
		;

		for (final Point p : points) {
			final double dist = p.distanceToSquared(house.getPos());
			if (dist < bestdist) {
				bestdist = dist;
				bp = p;
			}
		}
		return bp;
	}

	public ResourceLocation getClothTexturePath() {
		return clothTexture;
	}

	public Culture getCulture() {
		if (vtype == null) {
			return null;
		}
		return vtype.culture;
	}

	@Override
	public Entity getEntityToAttack() {
		return entityToAttack;
	}

	@Override
	public ItemStack getEquipmentInSlot(final int par1) {
		if (par1 > 0) {
			return func_130225_q(par1 - 1);
		}

		return this.heldItem;
	}

	@Override
	protected int getExperiencePoints(final EntityPlayer par1EntityPlayer) {
		return vtype.expgiven;
	}

	public String getFemaleChild() {
		return vtype.femaleChild;
	}

	public String getGameOccupationName(final String playername) {

		if (getCulture() == null || vtype == null) {
			return "";
		}

		if (!getCulture().canReadVillagerNames(playername)) {
			return "";
		}

		if (isChild() && size == MAX_CHILD_SIZE) {
			return getCulture().getCultureString("villager." + vtype.altkey);
		}
		return getCulture().getCultureString("villager." + vtype.key);
	}

	public String getGameSpeech(final String playername) {

		if (getCulture() == null) {
			return null;
		}

		final String speech = MillCommonUtilities.getVillagerSentence(this, playername, false);

		if (speech != null) {

			int duration = 10 + speech.length() / 5;
			duration = Math.min(duration, 30);

			if (speech_started + 20 * duration < worldObj.getWorldTime()) {
				return null;
			}
		}

		return speech;
	}

	public int getGatheringRange() {
		return GATHER_RANGE;
	}

	public String getGenderString() {

		if (gender == MALE) {
			return "male";
		} else {
			return "female";
		}
	}

	public Building getGoalBuildingDest() {
		return mw.getBuilding(getGoalBuildingDestPoint());
	}

	public Point getGoalBuildingDestPoint() {
		if (goalInformation == null) {
			return null;
		}
		return goalInformation.getDestBuildingPos();
	}

	public Entity getGoalDestEntity() {
		if (goalInformation == null) {
			return null;
		}
		return goalInformation.getTargetEnt();
	}

	public Point getGoalDestPoint() {
		if (goalInformation == null) {
			return null;
		}
		return goalInformation.getDest();
	}

	public String getGoalLabel(final String goal) {
		if (Goal.goals.containsKey(goal)) {
			return Goal.goals.get(goal).gameName(this);
		} else {
			return "none";
		}
	}

	public Goal[] getGoals() {
		if (vtype != null) {
			return vtype.goals;
		} else {
			return null;
		}
	}

	public InvItem[] getGoodsToBringBackHome() {
		return vtype.bringBackHomeGoods;
	}

	public InvItem[] getGoodsToCollect() {
		return vtype.collectGoods;
	}

	@Override
	public ItemStack getHeldItem() {
		return heldItem;
	}

	public int getHireCost(final EntityPlayer player) {

		int cost = vtype.hireCost;

		if (getTownHall().controlledBy(player.getDisplayName())) {
			cost = cost / 2;
		}

		return cost;
	}

	public Building getHouse() {
		if (house != null) {
			return house;
		}
		if (MLN.LogVillager >= MLN.DEBUG && extraLog) {
			MLN.debug(this, "Seeking uncached house");
		}
		if (mw != null) {
			house = mw.getBuilding(housePoint);

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

	public int getMerchantSellPrice(final Goods g) {
		return merchantSells.get(g);
	}

	public String getName() {
		return firstName + " " + familyName;
	}

	public String getNameKey() {
		if (vtype == null) {
			return "";
		}
		if (isChild() && size == MAX_CHILD_SIZE) {
			return vtype.altkey;
		}
		return vtype.key;
	}

	public String getNativeOccupationName() {
		if (vtype == null) {
			return null;
		}

		if (isChild() && size == MAX_CHILD_SIZE) {
			return vtype.altname;
		}
		return vtype.name;
	}

	public String getNativeSpeech(final String playername) {

		if (getCulture() == null) {
			return null;
		}

		final String speech = MillCommonUtilities.getVillagerSentence(this, playername, true);

		if (speech != null) {

			int duration = 10 + speech.length() / 5;
			duration = Math.min(duration, 30);

			if (speech_started + 20 * duration < worldObj.getWorldTime()) {
				return null;
			}
		}

		return speech;
	}

	public int getNewGender(final Building th) {
		return vtype.gender;
	}

	public String getNewName() {
		return getCulture().getRandomNameFromList(vtype.firstNameList);
	}

	public ResourceLocation getNewTexture() {

		if (vtype != null) {
			return new ResourceLocation(Mill.modId, vtype.getTexture());
		} else {
			return null;
		}
	}

	public Point getPathDestPoint() {
		return pathDestPoint;
	}

	private AStarConfig getPathingConfig() {

		if (goalKey != null) {
			return Goal.goals.get(goalKey).getPathingConfig();
		}

		return DEFAULT_JPS_CONFIG;
	}

	public PathPoint getPathPointPos() {
		return new PathPoint(MathHelper.floor_double(this.boundingBox.minX), MathHelper.floor_double(this.boundingBox.minY), MathHelper.floor_double(this.boundingBox.minZ));
	}

	public Point getPos() {
		return new Point(this.posX, this.posY, this.posZ);
	}

	public String getRandomFamilyName() {
		return getCulture().getRandomNameFromList(vtype.familyNameList);
	}

	public VillagerRecord getRecord() {

		if (getTownHall() != null && getTownHall().vrecords != null) {
			for (final VillagerRecord vr : getTownHall().vrecords) {
				if (vr.id == villager_id) {
					return vr;
				}
			}
		}

		if (getHouse() != null && getHouse().vrecords != null) {
			for (final VillagerRecord vr : getHouse().vrecords) {
				if (vr.id == villager_id) {
					return vr;
				}
			}
		}

		return null;
	}

	public MillVillager getSpouse() {

		if (getHouse() == null || isChild()) {
			return null;
		}

		for (final MillVillager v : getHouse().villagers) {
			if (!v.isChild() && v.gender != gender) {
				return v;
			}
		}

		return null;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public InvItem[] getToolsNeeded() {
		if (vtype != null) {
			return vtype.toolsNeeded;
		} else {
			return null;
		}
	}

	@Override
	public int getTotalArmorValue() {
		int total = 0;
		for (int i = 0; i < 4; i++) {
			final ItemStack armour = func_130225_q(i);

			if (armour != null && armour.getItem() instanceof ItemArmor) {
				total += ((ItemArmor) armour.getItem()).damageReduceAmount;
			}
		}
		return total;
	}

	public Building getTownHall() {
		if (townHall != null) {
			// Log.debug(Log.Villager, "Seeking cached townHall");
			return townHall;
		}

		if (MLN.LogVillager >= MLN.DEBUG && extraLog) {
			MLN.debug(this, "Seeking uncached townHall");
		}

		if (mw != null) {

			townHall = mw.getBuilding(townHallPoint);

			return townHall;
		}

		return null;
	}

	public ItemStack getWeapon() {

		if (isUsingBow) {
			for (final Item weapon : weaponsBow) {
				if (countInv(weapon) > 0) {
					return new ItemStack(weapon, 1);
				}
			}
		}

		if (isUsingHandToHand || !vtype.isArcher) {
			for (final Item weapon : weaponsHandToHand) {
				if (countInv(weapon) > 0) {
					return new ItemStack(weapon, 1);
				}
			}

			if (vtype != null && vtype.startingWeapon != null) {
				return new ItemStack(vtype.startingWeapon.getItem(), 1, vtype.startingWeapon.meta);
			}
		}

		for (final Item weapon : weapons) {
			if (countInv(weapon) > 0) {
				return new ItemStack(weapon, 1);
			}
		}

		if (vtype != null && vtype.startingWeapon != null) {
			return new ItemStack(vtype.startingWeapon.getItem(), 1, vtype.startingWeapon.meta);
		}

		return null;
	}

	public void growSize() {

		int growth = 2;

		int nb = 0;

		nb = getHouse().takeGoods(Items.egg, 1);
		if (nb == 1) {
			growth += 1 + MillCommonUtilities.randomInt(5);
		}

		for (int i = 0; i < foodGrowth.length; i++) {
			if (growth < 10 && size + growth < MAX_CHILD_SIZE) {
				nb = getHouse().takeGoods(foodGrowth[i], 1);
				if (nb == 1) {
					growth += foodGrowthValues[i] + MillCommonUtilities.randomInt(foodGrowthValues[i]);
				}
			}
		}

		size += growth;

		if (size > MAX_CHILD_SIZE) {
			size = MAX_CHILD_SIZE;
		}

		getRecord().villagerSize = size;

		adjustSize();
		if (MLN.LogChildren >= MLN.MINOR) {
			MLN.minor(this, "Child growing by " + growth + ", new size: " + size);
		}
	}

	private void handleDoorsAndFenceGates() {
		if (doorToClose != null) {// checking for door to close
			if (pathEntity == null || pathEntity.getCurrentPathLength() == 0// door
																			// must
																			// be
					// closed
					|| pathEntity.getPastTargetPathPoint(2) != null && doorToClose.sameBlock(pathEntity.getPastTargetPathPoint(2))) {
				if (getBlock(doorToClose) == Blocks.wooden_door) {
					final int meta = getBlockMeta(doorToClose);

					if ((meta & 4) == 4) {
						toggleDoor(doorToClose.getiX(), doorToClose.getiY(), doorToClose.getiZ());
					}
					doorToClose = null;
				} else if (getBlock(doorToClose) == Blocks.fence_gate) {
					if (closeFenceGate(doorToClose.getiX(), doorToClose.getiY(), doorToClose.getiZ())) {
						doorToClose = null;
					}
				} else {
					doorToClose = null;
				}
			}
		} else {// checking for door to open
			if (pathEntity != null && pathEntity.getCurrentPathLength() > 0) {
				PathPoint p = null;
				// check for wood door:
				if (pathEntity.getCurrentTargetPathPoint() != null
						&& worldObj.getBlock(pathEntity.getCurrentTargetPathPoint().xCoord, pathEntity.getCurrentTargetPathPoint().yCoord, pathEntity.getCurrentTargetPathPoint().zCoord) == Blocks.wooden_door) {
					p = pathEntity.getCurrentTargetPathPoint();
				} else if (pathEntity.getNextTargetPathPoint() != null
						&& worldObj.getBlock(pathEntity.getNextTargetPathPoint().xCoord, pathEntity.getNextTargetPathPoint().yCoord, pathEntity.getNextTargetPathPoint().zCoord) == Blocks.wooden_door) {
					p = pathEntity.getNextTargetPathPoint();
				}

				if (p != null) {
					final int meta = worldObj.getBlockMetadata(p.xCoord, p.yCoord, p.zCoord);
					if ((meta & 4) == 0) {
						toggleDoor(p.xCoord, p.yCoord, p.zCoord);
						doorToClose = new Point(p);
					}
				} else {// check for fence gate:
					if (pathEntity.getNextTargetPathPoint() != null
							&& worldObj.getBlock(pathEntity.getNextTargetPathPoint().xCoord, pathEntity.getNextTargetPathPoint().yCoord, pathEntity.getNextTargetPathPoint().zCoord) == Blocks.fence_gate) {
						p = pathEntity.getNextTargetPathPoint();
					} else if (pathEntity.getCurrentTargetPathPoint() != null
							&& worldObj.getBlock(pathEntity.getCurrentTargetPathPoint().xCoord, pathEntity.getCurrentTargetPathPoint().yCoord, pathEntity.getCurrentTargetPathPoint().zCoord) == Blocks.fence_gate) {
						p = pathEntity.getCurrentTargetPathPoint();
					}

					if (p != null) {
						openFenceGate(p.xCoord, p.yCoord, p.zCoord);
						doorToClose = new Point(p);
					}
				}
			}
		}
	}

	private boolean hasBow() {
		for (final Item weapon : weaponsBow) {
			if (countInv(weapon) > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean hasChildren() {
		return vtype.maleChild != null && vtype.femaleChild != null;
	}

	@Override
	public int hashCode() {
		return (int) villager_id;
	}

	public boolean helpsInAttacks() {
		return vtype.helpInAttacks;
	}

	public void initialise(final VillagerType v, final String familyName, final boolean respawn) {
		vtype = v;
		villager_id = Math.abs(MillCommonUtilities.randomLong());

		gender = v.gender;
		firstName = getNewName();
		this.familyName = familyName;

		texture = getNewTexture();
		setHealth(getMaxHealth());

		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(v.health);

		updateClothTexturePath();

		if (isChild()) {
			size = 0;
			scale = 0.5f;
		} else {
			scale = v.baseScale + (MillCommonUtilities.randomInt(10) - 5) / 100;
		}

		if (!respawn) {
			for (final InvItem item : v.startingInv.keySet()) {
				addToInv(item.getItem(), item.meta, v.startingInv.get(item));
			}
		}

		registerInGlobalList();
	}

	@Override
	public boolean interact(final EntityPlayer entityplayer) {

		if (worldObj.isRemote || this.isVillagerSleeping()) {
			return true;
		}

		final UserProfile profile = mw.getProfile(entityplayer.getDisplayName());

		entityplayer.addStat(MillAchievements.firstContact, 1);

		if (vtype != null && (vtype.key.equals("indian_sadhu") || vtype.key.equals("alchemist"))) {
			entityplayer.addStat(MillAchievements.maitreapenser, 1);
		}

		if (profile.villagersInQuests.containsKey(villager_id)) {
			final QuestInstance qi = profile.villagersInQuests.get(villager_id);
			if (qi.getCurrentVillager().id == villager_id) {
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

	public void interactDev(final EntityPlayer entityplayer) {
		DevModUtilities.villagerInteractDev(entityplayer, this);
	}

	public boolean interactSpecial(final EntityPlayer entityplayer) {

		if (getTownHall() == null) {
			MLN.error(this, "Trying to interact with a villager with no TH.");
		}

		if (isChief()) {
			ServerSender.displayVillageChiefGUI(entityplayer, this);
			return true;
		}

		final UserProfile profile = mw.getProfile(entityplayer.getDisplayName());

		if (canMeditate() && mw.isGlobalTagSet(MillWorld.PUJAS) || canPerformSacrifices() && mw.isGlobalTagSet(MillWorld.MAYANSACRIFICES)) {

			if (MLN.LogPujas >= MLN.DEBUG) {
				MLN.debug(this, "canMeditate");
			}

			if (getTownHall().getReputation(entityplayer.getDisplayName()) >= Building.MIN_REPUTATION_FOR_TRADE) {
				for (final BuildingLocation l : getTownHall().getLocations()) {
					if (l.level >= 0 && l.getSellingPos() != null && l.getSellingPos().distanceTo(this) < 8) {
						final Building b = l.getBuilding(worldObj);
						if (b.pujas != null) {

							if (MLN.LogPujas >= MLN.DEBUG) {
								MLN.debug(this, "Found shrine: " + b);
							}

							final Point p = b.getPos();

							entityplayer.openGui(Mill.instance, CommonGuiHandler.GUI_PUJAS, worldObj, p.getiX(), p.getiY(), p.getiZ());

							return true;
						}
					}
				}
			} else {
				ServerSender.sendTranslatedSentence(entityplayer, MLN.WHITE, "ui.sellerboycott", getName());
				return false;
			}
		}

		if (isSeller() && !getTownHall().controlledBy(entityplayer.getDisplayName())) {
			if (getTownHall().getReputation(entityplayer.getDisplayName()) >= Building.MIN_REPUTATION_FOR_TRADE && getTownHall().chestLocked) {
				for (final BuildingLocation l : getTownHall().getLocations()) {
					if (l.level >= 0 && l.shop != null && l.shop.length() > 0) {
						if (l.getSellingPos() != null && l.getSellingPos().distanceTo(this) < 5 || l.sleepingPos.distanceTo(this) < 5) {
							ServerSender.displayVillageTradeGUI(entityplayer, l.getBuilding(worldObj));
							return true;
						}
					}
				}
			} else if (!getTownHall().chestLocked) {
				ServerSender.sendTranslatedSentence(entityplayer, MLN.WHITE, "ui.sellernotcurrently possible", getName());
				return false;
			} else {
				ServerSender.sendTranslatedSentence(entityplayer, MLN.WHITE, "ui.sellerboycott", getName());
				return false;
			}
		}

		if (isForeignMerchant()) {
			ServerSender.displayMerchantTradeGUI(entityplayer, this);
			return true;
		}

		if (vtype.hireCost > 0) {
			if (hiredBy == null || hiredBy.equals(entityplayer.getDisplayName())) {
				ServerSender.displayHireGUI(entityplayer, this);
				return true;
			} else {
				ServerSender.sendTranslatedSentence(entityplayer, MLN.WHITE, "hire.hiredbyotherplayer", getName(), hiredBy);
				return false;
			}
		}

		if (this.isLocalMerchant() && !profile.villagersInQuests.containsKey(villager_id)) {
			ServerSender.sendTranslatedSentence(entityplayer, MLN.ORANGE, "other.localmerchantinteract", getName(), hiredBy);
			return false;
		}

		return false;
	}

	public boolean isChief() {
		return vtype.isChief;
	}

	@Override
	public boolean isChild() {
		if (vtype == null) {
			return false;
		}
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
	protected boolean isMovementBlocked() {
		return this.getHealth() <= 0 || this.isVillagerSleeping();
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

	public boolean isTextureValid(final String texture) {
		if (vtype != null) {
			return vtype.isTextureValid(texture);
		} else {
			return true;
		}
	}

	public boolean isVillagerSleeping() {
		return shouldLieDown;
	}

	public boolean isVisitor() {
		if (vtype == null) {
			return false;
		}
		return vtype.visitor;
	}

	private void jumpToDest() {

		final Point jumpTo = MillCommonUtilities.findVerticalStandingPos(worldObj, getPathDestPoint());

		if (jumpTo != null && jumpTo.distanceTo(getPathDestPoint()) < 4) {
			if (MLN.LogPathing >= MLN.MAJOR && extraLog) {
				MLN.major(this, "Jumping from " + getPos() + " to " + jumpTo);
			}

			setPosition(jumpTo.getiX() + 0.5, jumpTo.getiY() + 0.5, jumpTo.getiZ() + 0.5);

			longDistanceStuck = 0;
			localStuck = 0;
		} else {
			if (goalKey != null && Goal.goals.containsKey(goalKey)) {
				final Goal goal = Goal.goals.get(goalKey);
				try {
					goal.unreachableDestination(this);
				} catch (final Exception e) {
					MLN.printException(this + ": Exception in handling unreachable dest for goal " + goalKey, e);
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
			if (inventory.get(iv) > 0) {
				MillCommonUtilities.spawnItem(worldObj, getPos(), new ItemStack(iv.getItem(), inventory.get(iv), iv.meta), 0);
			}
		}

		if (hiredBy != null) {
			final EntityPlayer owner = worldObj.getPlayerEntityByName(hiredBy);

			if (owner != null) {
				ServerSender.sendTranslatedSentence(owner, MLN.WHITE, "hire.hiredied", getName());
			}
		}

		if (getTownHall() != null) {
			final VillagerRecord vr = getTownHall().getVillagerRecordById(villager_id);

			if (vr != null) {
				if (MLN.LogGeneralAI >= MLN.MAJOR) {
					MLN.major(this, getTownHall() + ": Villager has been killed!");
				}
				vr.killed = true;
			}
		}

		super.setDead();
	}

	private void leaveVillage() {
		// Foreign merchants leave with their stocks
		for (final InvItem iv : vtype.foreignMerchantStock.keySet()) {
			getHouse().takeGoods(iv.getItem(), iv.meta, vtype.foreignMerchantStock.get(iv));
		}

		getTownHall().deleteVillager(this);
		getTownHall().removeVillagerRecord(villager_id);
		getHouse().deleteVillager(this);
		getHouse().removeVillagerRecord(villager_id);

		despawnVillager();
	}

	public void localMerchantUpdate() throws Exception {
		if (getHouse() != null && getHouse() == getTownHall()) {

			final List<Building> buildings = getTownHall().getBuildingsWithTag(Building.tagInn);
			Building inn = null;

			for (final Building building : buildings) {
				if (building.merchantRecord == null) {
					inn = building;
				}
			}

			if (inn == null) {
				getHouse().removeVillagerRecord(villager_id);
				getHouse().deleteVillager(this);
				despawnVillager();
				MLN.error(this, "Merchant had Town Hall as house and inn is full. Killing him.");
			} else {
				setHousePoint(inn.getPos());
				getHouse().addOrReplaceVillager(this);
				getTownHall().removeVillagerRecord(villager_id);
				final VillagerRecord vr = new VillagerRecord(mw, this);
				getHouse().addOrReplaceRecord(vr);
				getTownHall().addOrReplaceRecord(vr);
				MLN.error(this, "Merchant had Town Hall as house. Moving him to the inn.");
			}
		}
	}

	@Override
	public void onFoundPath(final List<AStarNode> result) {
		pathCalculatedSinceLastTick = result;
	}

	@Override
	public void onLivingUpdate() {

		super.onLivingUpdate();

		this.updateArmSwingProgress();

		setFacingDirection();

		if (isVillagerSleeping()) {
			motionX = 0;
			motionY = 0;
			motionZ = 0;
		}
	}

	@Override
	public void onNoPathAvailable() {
		pathFailedSincelastTick = true;
		if (MLN.LogPathing >= MLN.MINOR) {
			MLN.minor(this, "No path found between " + getPos() + " and " + getPathDestPoint() + " in " + (System.currentTimeMillis() - this.pathCalculationStartTime));
		}
	}

	@Override
	public void onUpdate() {

		try {

			if (vtype == null) {
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

			if (pathCalculatedSinceLastTick != null) {
				applyPathCalculatedSinceLastTick();
			}

			if (MLN.DEV) {
				if (goalKey != null) {

					final Goal goal = Goal.goals.get(goalKey);

					if (getPathDestPoint() != null && !jpsPathPlanner.isBusy() && pathEntity == null && !stopMoving && goalKey != null && !goalKey.equals("gorest")
							&& getPathDestPoint().distanceTo(getPos()) > goal.range(this)) {
						// MLN.error(this,
						// "Null path and not calculating a new one at start? Goal: "+goalKey);
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

			if (hiredBy != null) {
				updateHired();
				super.onUpdate();
				return;
			}

			if (getTownHall() == null || getHouse() == null) {
				return;
			}

			if (getTownHall() != null && !getTownHall().isActive) {
				return;
			}

			try {

				timer++;

				if (getHealth() < getMaxHealth() & MillCommonUtilities.randomInt(1600) == 0) {
					setHealth(getHealth() + 1);
				}

				detrampleCrops();

				allowRandomMoves = true;// overriden inside goal if needed

				if (getTownHall() == null || getHouse() == null) {
					super.onUpdate();
					return;
				}

				checkRegistration();

				if (Goal.beSeller.key.equals(goalKey)) {
					townHall.seller = this;
				} else if (Goal.getResourcesForBuild.key.equals(goalKey) || Goal.construction.key.equals(goalKey)) {
					if (MLN.LogTileEntityBuilding >= MLN.DEBUG) {
						MLN.debug(this, "Registering as builder for: " + townHall);
					}
					townHall.builder = this;
				}

				if (getTownHall().underAttack) {

					if (!(goalKey != null && (goalKey.equals(Goal.raidVillage.key) || goalKey.equals(Goal.defendVillage.key) || goalKey.equals(Goal.hide.key)))) {
						// we clear any previous dest, it will be set by the
						// goals
						// instead
						clearGoal();
					}
					if (isRaider) {
						goalKey = Goal.raidVillage.key;
						targetDefender();
					} else if (helpsInAttacks()) {
						goalKey = Goal.defendVillage.key;
						targetRaider();
					} else {
						goalKey = Goal.hide.key;
					}
					checkGoals();
				}

				if (entityToAttack != null) {

					if (vtype.isDefensive && getPos().distanceTo(getHouse().getResManager().getDefendingPos()) > ATTACK_RANGE_DEFENSIVE) {
						entityToAttack = null;
					} else if (!entityToAttack.isEntityAlive() || getPos().distanceTo(entityToAttack) > ATTACK_RANGE || worldObj.difficultySetting == EnumDifficulty.PEACEFUL
							&& entityToAttack instanceof EntityPlayer) {
						entityToAttack = null;
					}

					if (entityToAttack != null) {
						shouldLieDown = false;
					}

				} else {
					if (isHostile() && worldObj.difficultySetting != EnumDifficulty.PEACEFUL && getTownHall().closestPlayer != null && getPos().distanceTo(getTownHall().closestPlayer) <= ATTACK_RANGE) {
						int range = ATTACK_RANGE;

						if (vtype.isDefensive) {
							range = ATTACK_RANGE_DEFENSIVE;
						}

						entityToAttack = worldObj.getClosestPlayer(posX, posY, posZ, range);
						clearGoal();
					}
				}

				if (entityToAttack != null) {
					setGoalDestPoint(new Point(entityToAttack));
					heldItem = getWeapon();

					if (goalKey != null) {
						if (!Goal.goals.get(goalKey).isFightingGoal()) {
							clearGoal();
						}
					}

				} else if (!getTownHall().underAttack) {

					if (worldObj.isDaytime()) {

						speakSentence("greeting", 10 * 60 * 20, 3, 10);

						nightActionPerformed = false;

						final InvItem[] goods = getGoodsToCollect();

						if (goods != null) {
							final EntityItem item = getClosestItemVertical(goods, 3, 30);
							if (item != null) {
								item.setDead();
								if (item.getEntityItem().getItem() == Item.getItemFromBlock(Blocks.sapling)) {
									addToInv(item.getEntityItem().getItem(), item.getEntityItem().getItemDamage() & 3, 1);
								} else {
									addToInv(item.getEntityItem().getItem(), item.getEntityItem().getItemDamage(), 1);
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
								shouldLieDown = false;
							}
						}
					} else {
						hasPrayedToday = false;
						hasDrunkToday = false;

						if (!isRaider) {
							if (goalKey == null) {
								setNextGoal();
							}
							if (goalKey != null) {
								checkGoals();
							} else {
								shouldLieDown = false;
							}
						}
					}
				}

				if (getPathDestPoint() != null && pathEntity != null && pathEntity.getCurrentPathLength() > 0 && !stopMoving) {

					// first test for "long distance stuck" - villager not
					// getting
					// closer to goal at all
					double olddistance = prevPoint.horizontalDistanceToSquared(getPathDestPoint());
					double newdistance = getPos().horizontalDistanceToSquared(getPathDestPoint());

					if (olddistance - newdistance < 0.0002) {
						longDistanceStuck++;
					} else {
						longDistanceStuck--;
					}

					if (longDistanceStuck < 0) {
						longDistanceStuck = 0;
					}
					if (pathEntity != null && pathEntity.getCurrentPathLength() > 1) {
						if (MLN.LogPathing >= MLN.MINOR && extraLog) {
							MLN.minor(this,
									"Stuck: " + longDistanceStuck + " pos " + getPos() + " node: " + pathEntity.getCurrentTargetPathPoint() + " next node: " + pathEntity.getNextTargetPathPoint()
											+ " dest: " + getPathDestPoint());
						}
					}

					if (longDistanceStuck > 3000 && (!vtype.noTeleport || getRecord() != null && getRecord().raidingVillage)) {// pathing
																																// has
						// failed,
						// using
						// long-range
						// teleport
						jumpToDest();
					}

					// then test for "local stuck" - villager not progressing in
					// path
					// typically stuck in doorframe or around a corner
					final PathPoint nextPoint = pathEntity.getNextTargetPathPoint();

					if (nextPoint != null) {

						olddistance = prevPoint.distanceToSquared(nextPoint);
						newdistance = getPos().distanceToSquared(nextPoint);

						if (olddistance - newdistance < 0.0002) {
							localStuck += 4;
						} else {
							localStuck--;
						}

						if (localStuck < 0) {
							localStuck = 0;
						}

						if (localStuck > 30) {
							// will force a path recalculation and maybe avoid
							// new
							// obstacle
							setPathToEntity(null);
							pathEntity = null;
						}

						if (localStuck > 100) {
							// short jump to next point, to clear a villager
							// stuck
							// in a door for example
							setPosition(nextPoint.xCoord + 0.5, nextPoint.yCoord + 0.5, nextPoint.zCoord + 0.5);
							localStuck = 0;
						}
					}

				} else {
					longDistanceStuck = 0;
					localStuck = 0;
				}

				if (getPathDestPoint() != null && !stopMoving) {
					updatePathIfNeeded(getPathDestPoint());
				}
				if (stopMoving || this.jpsPathPlanner.isBusy()) {
					setPathToEntity(null);
					pathEntity = null;
				}

				prevPoint = getPos();

				handleDoorsAndFenceGates();

				if (System.currentTimeMillis() - timeSinceLastPathingTimeDisplay > 10000) {
					if (pathingTime > 500) {
						if (getPathDestPoint() != null) {
							MLN.warning(this,
									"Pathing time in last 10 secs: " + pathingTime + " dest: " + getPathDestPoint() + " dest bid: " + MillCommonUtilities.getBlock(worldObj, getPathDestPoint())
											+ " above bid: " + MillCommonUtilities.getBlock(worldObj, getPathDestPoint().getAbove()));
						} else {
							MLN.warning(this, "Pathing time in last 10 secs: " + pathingTime + " null dest point.");
						}

						MLN.warning(this, "nbPathsCalculated: " + nbPathsCalculated + " nbPathNoStart: " + nbPathNoStart + " nbPathNoEnd: " + nbPathNoEnd + " nbPathAborted: " + nbPathAborted
								+ " nbPathFailure: " + nbPathFailure);
						String s = "";
						for (final PathKey p : abortedKeys) {
							s += p + "     ";
						}
						MLN.warning(this, "Aborted keys: " + s);

						if (goalKey != null) {
							MLN.warning(this, "Current goal: " + Goal.goals.get(goalKey));
						}

					}
					timeSinceLastPathingTimeDisplay = System.currentTimeMillis();
					pathingTime = 0;
					nbPathsCalculated = 0;
					nbPathNoStart = 0;
					nbPathNoEnd = 0;
					nbPathAborted = 0;
					nbPathFailure = 0;
					abortedKeys.clear();
				}

				sendVillagerPacket();

			} catch (final MillenaireException e) {
				Mill.proxy.sendChatAdmin(getName() + ": Error in onUpdate(). Check millenaire.log.");
				MLN.error(this, e.getMessage());
			} catch (final Exception e) {
				Mill.proxy.sendChatAdmin(getName() + ": Error in onUpdate(). Check millenaire.log.");
				MLN.error(this, "Exception in Villager.onUpdate(): ");
				MLN.printException(e);
			}

			triggerMobAttacks();

			updateDialogue();

			isUsingBow = false;// will be set to true if the NPC is attacking
								// with a
			// bow in attackEntity()
			isUsingHandToHand = false;

			for (int i = 0; i < 5; i++) {
				if (getEquipmentInSlot(i) != null && getEquipmentInSlot(i).getItem() == null) {
					MLN.printException("ItemStack with null item for villager " + this + ", goal: " + this.goalKey, new Exception());
				}
			}

			super.onUpdate();

			final double timeInMl = (double) (System.nanoTime() - startTime) / 1000000;

			if (MLN.DEV) {
				getTownHall().monitor.addToGoal(goalKey, timeInMl);

				if (getPathDestPoint() != null && !jpsPathPlanner.isBusy() && pathEntity == null) {
					// MLN.error(this,
					// "Null path and not calculating a new one? Goal: "+goalKey);
				}

				if (getPathDestPoint() != null && getGoalDestPoint() != null && getPathDestPoint().distanceTo(getGoalDestPoint()) > 20) {
					// MLN.error(this,
					// "pathDestPoint: "+pathDestPoint+", goalDestPoint: "+goalDestPoint+", goal: "+goalKey);
				}
			}

		} catch (final Exception e) {
			MLN.printException("Exception in onUpdate() of villager: " + this, e);
		}
	}

	private boolean openFenceGate(final int i, final int j, final int k) {
		int l = worldObj.getBlockMetadata(i, j, k);
		if (!BlockFenceGate.isFenceGateOpen(l)) {
			final int i1 = (MathHelper.floor_double(rotationYaw * 4F / 360F + 0.5D) & 3) % 4;
			final int j1 = BlockDirectional.getDirection(l);
			if (j1 == (i1 + 2) % 4) {
				l = i1;
			}
			MillCommonUtilities.setBlockMetadata(worldObj, i, j, k, l | 4, true);

		}

		return true;
	}

	private void pathFailedSinceLastTick() {
		if (pathfailure >= 20 && (!vtype.noTeleport || getRecord() != null && getRecord().raidingVillage)) {
			jumpToDest();
			pathfailure = 0;
		} else {
			pathfailure++;
			final Point p = MillCommonUtilities.findRandomStandingPosAround(worldObj, getPathDestPoint());
			jpsPathPlanner.stopPathSearch(true);
			if (p != null) {
				computeNewPath(p);
			} else {
				if (!vtype.noTeleport || getRecord() != null && getRecord().raidingVillage) {
					jumpToDest();
				}
				pathfailure = 0;
			}
		}
		pathFailedSincelastTick = false;
	}

	public boolean performNightAction() {

		if (isChild()) {
			if (size < MAX_CHILD_SIZE) {
				growSize();
			} else {
				teenagerNightAction();
			}
		}

		if (isForeignMerchant()) {
			foreignMerchantNightAction();
		}

		if (hasChildren()) {
			return attemptChildConception();
		}

		return true;
	}

	public int putInBuilding(final Building building, final Item item, final int nb) {
		return putInBuilding(building, item, 0, nb);
	}

	public int putInBuilding(final Building building, final Item item, final int meta, int nb) {
		nb = takeFromInv(item, meta, nb);
		building.storeGoods(item, meta, nb);

		return nb;
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		final String type = nbttagcompound.getString("vtype");
		final String culture = nbttagcompound.getString("culture");

		if (Culture.getCultureByName(culture) != null) {
			if (Culture.getCultureByName(culture).getVillagerType(type) != null) {
				vtype = Culture.getCultureByName(culture).getVillagerType(type);
			} else {
				MLN.error(this, "Could not load dynamic NPC: unknown type: " + type + " in culture: " + culture);
			}
		} else {
			MLN.error(this, "Could not load dynamic NPC: unknown culture: " + culture);
		}

		texture = new ResourceLocation(Mill.modId, nbttagcompound.getString("texture"));
		housePoint = Point.read(nbttagcompound, "housePos");
		if (housePoint == null) {
			MLN.error(this, "Error when loading villager: housePoint null");
			Mill.proxy.sendChatAdmin(getName() + ": Could not load house position. Check millenaire.log");
		}

		townHallPoint = Point.read(nbttagcompound, "townHallPos");

		if (townHallPoint == null) {
			MLN.error(this, "Error when loading villager: townHallPoint null");
			Mill.proxy.sendChatAdmin(getName() + ": Could not load town hall position. Check millenaire.log");
		}

		setGoalDestPoint(Point.read(nbttagcompound, "destPoint"));
		setPathDestPoint(Point.read(nbttagcompound, "pathDestPoint"));
		setGoalBuildingDestPoint(Point.read(nbttagcompound, "destBuildingPoint"));
		prevPoint = Point.read(nbttagcompound, "prevPoint");
		doorToClose = Point.read(nbttagcompound, "doorToClose");
		action = nbttagcompound.getInteger("action");
		goalKey = nbttagcompound.getString("goal");

		if (goalKey.trim().length() == 0) {
			goalKey = null;
		}

		if (goalKey != null && !Goal.goals.containsKey(goalKey)) {
			goalKey = null;
		}

		dialogueKey = nbttagcompound.getString("dialogueKey");
		dialogueStart = nbttagcompound.getLong("dialogueStart");
		dialogueRole = nbttagcompound.getInteger("dialogueRole");
		dialogueColour = (char) nbttagcompound.getInteger("dialogueColour");
		dialogueChat = nbttagcompound.getBoolean("dialogueChat");

		if (dialogueKey.trim().length() == 0) {
			dialogueKey = null;
		}

		familyName = nbttagcompound.getString("familyName");
		firstName = nbttagcompound.getString("firstName");
		scale = nbttagcompound.getFloat("scale");
		gender = nbttagcompound.getInteger("gender");
		// speech_started=nbttagcompound.getLong("lastSpeechLong");

		if (nbttagcompound.hasKey("villager_lid")) {
			villager_id = Math.abs(nbttagcompound.getLong("villager_lid"));
		}

		if (!isTextureValid(texture.getResourcePath())) {
			texture = getNewTexture();
		}

		final NBTTagList nbttaglist = nbttagcompound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);

			final int itemID = nbttagcompound1.getInteger("item");
			int itemMeta = nbttagcompound1.getInteger("meta");

			// Hack to fix badly saved inventories with sideway wood blocks
			if (itemID == Block.getIdFromBlock(Blocks.log)) {
				itemMeta = itemMeta & 3;
			}

			try {
				inventory.put(new InvItem(Item.getItemById(itemID), itemMeta), nbttagcompound1.getInteger("amount"));
			} catch (final MillenaireException e) {
				MLN.printException(e);
			}
		}

		previousBlock = Block.getBlockById(nbttagcompound.getInteger("previousBlock"));
		previousBlockMeta = nbttagcompound.getInteger("previousBlockMeta");
		size = nbttagcompound.getInteger("size");

		hasPrayedToday = nbttagcompound.getBoolean("hasPrayedToday");
		hasDrunkToday = nbttagcompound.getBoolean("hasDrunkToday");

		hiredBy = nbttagcompound.getString("hiredBy");
		hiredUntil = nbttagcompound.getLong("hiredUntil");
		aggressiveStance = nbttagcompound.getBoolean("aggressiveStance");
		isRaider = nbttagcompound.getBoolean("isRaider");

		if (hiredBy.equals("")) {
			hiredBy = null;
		}

		clothName = nbttagcompound.getString("clothName");
		clothTexture = new ResourceLocation(Mill.modId, nbttagcompound.getString("clothTexture"));

		if (clothName.equals("")) {
			clothName = null;
			clothTexture = null;
		}

		registerInGlobalList();
		updateClothTexturePath();

	}

	@Override
	public void readSpawnData(final ByteBuf ds) {

		final ByteBufInputStream data = new ByteBufInputStream(ds);
		try {
			villager_id = data.readLong();
			readVillagerStreamdata(data);
			registerInGlobalList();
		} catch (final IOException e) {
			MLN.printException("Error in readSpawnData for villager " + this, e);
		}

	}

	private void readVillagerStreamdata(final DataInput data) throws IOException {

		final Culture culture = Culture.getCultureByName(StreamReadWrite.readNullableString(data));

		final String vt = StreamReadWrite.readNullableString(data);
		if (culture != null) {
			vtype = culture.getVillagerType(vt);
		}

		texture = StreamReadWrite.readNullableResourceLocation(data);

		goalKey = StreamReadWrite.readNullableString(data);
		housePoint = StreamReadWrite.readNullablePoint(data);
		townHallPoint = StreamReadWrite.readNullablePoint(data);
		firstName = StreamReadWrite.readNullableString(data);
		familyName = StreamReadWrite.readNullableString(data);

		scale = data.readFloat();
		gender = data.readInt();
		size = data.readInt();

		hiredBy = StreamReadWrite.readNullableString(data);
		aggressiveStance = data.readBoolean();
		hiredUntil = data.readLong();
		isUsingBow = data.readBoolean();
		isUsingHandToHand = data.readBoolean();
		speech_key = StreamReadWrite.readNullableString(data);
		speech_variant = data.readInt();
		speech_started = data.readLong();
		heldItem = StreamReadWrite.readNullableItemStack(data);

		inventory = StreamReadWrite.readInventory(data);

		clothName = StreamReadWrite.readNullableString(data);
		clothTexture = StreamReadWrite.readNullableResourceLocation(data);

		setGoalDestPoint(StreamReadWrite.readNullablePoint(data));
		shouldLieDown = data.readBoolean();

		dialogueTargetFirstName = StreamReadWrite.readNullableString(data);
		dialogueTargetLastName = StreamReadWrite.readNullableString(data);
		dialogueColour = data.readChar();
		dialogueChat = data.readBoolean();
		setHealth(data.readFloat());

		final int nbMerchantSells = data.readInt();

		if (nbMerchantSells > -1) {
			merchantSells.clear();

			for (int i = 0; i < nbMerchantSells; i++) {
				Goods g;
				try {
					g = StreamReadWrite.readNullableGoods(data);
					merchantSells.put(g, data.readInt());
				} catch (final MillenaireException e) {
					MLN.printException(e);
				}
			}
		}

		final int goalDestEntityID = data.readInt();

		if (goalDestEntityID != -1) {
			final Entity ent = worldObj.getEntityByID(goalDestEntityID);

			if (ent != null) {
				setGoalDestEntity(ent);
			}
		}

		client_lastupdated = worldObj.getWorldTime();

	}

	public void registerInGlobalList() {
		if (registeredInGlobalList) {
			if (MillCommonUtilities.chanceOn(20)) {
				if (!mw.villagers.containsKey(villager_id)) {
					mw.villagers.put(villager_id, this);
				} else if (mw.villagers.get(villager_id) != null && mw.villagers.get(villager_id) != this && mw.villagers.get(villager_id).isDead == false) {// replaced
					// by
					// other
					// villager!
					despawnVillagerSilent();
				}
			}
			return;
		}

		if (mw == null) {
			MLN.error(this, "Could not register as mw is null");
			return;
		}

		if (mw.villagers.containsKey(villager_id)) {
			mw.villagers.get(villager_id).despawnVillagerSilent();
		}

		mw.villagers.put(villager_id, this);

		registeredInGlobalList = true;
	}

	public void registerNewPath(final AS_PathEntity path) throws Exception {

		if (path == null) {
			boolean handled = false;
			if (goalKey != null) {
				final Goal goal = Goal.goals.get(goalKey);
				handled = goal.unreachableDestination(this);
			}
			if (!handled) {
				clearGoal();
			}

		} else {
			setPathToEntity(path);
			pathEntity = path;
			moveStrafing = 0;
		}

		prevPathPoint = getPathPointPos();

		pathingWorker = null;

	}

	public void registerNewPath(final List<PathPoint> result) throws Exception {

		AS_PathEntity path = null;

		if (result != null) {
			final PathPoint[] pointsCopy = new PathPoint[result.size()];

			int i = 0;
			for (final PathPoint p : result) {
				if (p == null) {
					pointsCopy[i] = null;
				} else {
					final PathPoint p2 = new PathPoint(p.xCoord, p.yCoord, p.zCoord);
					pointsCopy[i] = p2;
				}
				i++;
			}
			path = new AS_PathEntity(pointsCopy);
		}

		registerNewPath(path);
	}

	public void registerNewPathException(final Exception e) {
		if (e instanceof PathingException && ((PathingException) e).errorCode == PathingException.UNREACHABLE_START) {
			if (MLN.LogPathing >= MLN.MAJOR && extraLog) {
				MLN.major(this, "Unreachable start. Jumping back home.");
			}
			setPosition(getHouse().getResManager().getSleepingPos().x + 0.5, getHouse().getResManager().getSleepingPos().y + 1, getHouse().getResManager().getSleepingPos().z + 0.5);
		}
		pathingWorker = null;
	}

	public void registerNewPathInterrupt(final PathingWorker worker) {
		if (pathingWorker == worker) {
			pathingWorker = null;
		}
	}

	public HashMap<InvItem, Integer> requiresGoods() {
		if (isChild() && size < MAX_CHILD_SIZE) {
			return vtype.requiredFoodAndGoods;
		}
		if (hasChildren() && getHouse().villagers.size() < 4) {
			return vtype.requiredFoodAndGoods;
		}

		return vtype.requiredGoods;
	}

	private void sendVillagerPacket() {
		final DataOutput data = ServerSender.getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_VILLAGER);
			writeVillagerStreamData(data, false);
		} catch (final IOException e) {
			MLN.printException(this + ": Error in sendVillagerPacket", e);
		}

		ServerSender.sendPacketToPlayersInRange(data, getPos(), 30);
	}

	public boolean setBlock(final Point p, final Block block) {
		return MillCommonUtilities.setBlock(worldObj, p, block, true, true);
	}

	public boolean setBlockAndMetadata(final Point p, final Block block, final int metadata) {
		return MillCommonUtilities.setBlockAndMetadata(worldObj, p, block, metadata, true, true);
	}

	public boolean setBlockMetadata(final Point p, final int metadata) {
		return MillCommonUtilities.setBlockMetadata(worldObj, p, metadata);
	}

	@Override
	public void setDead() {
		if (getHealth() <= 0) {
			killVillager();
		}
	}

	public void setEntityToAttack(final Entity ent) {
		entityToAttack = ent;
	}

	private void setFacingDirection() {

		if (entityToAttack != null) {
			faceEntityMill(entityToAttack, 30, 30);
			return;
		}

		if (goalKey != null && (getGoalDestPoint() != null || getGoalDestEntity() != null)) {
			final Goal goal = Goal.goals.get(goalKey);

			if (goal.lookAtGoal()) {
				if (getGoalDestEntity() != null && getPos().distanceTo(getGoalDestEntity()) < goal.range(this)) {
					faceEntityMill(getGoalDestEntity(), 10, 10);
				} else if (getGoalDestPoint() != null && getPos().distanceTo(getGoalDestPoint()) < goal.range(this)) {
					facePoint(getGoalDestPoint(), 10, 10);
				}
			}

			if (goal.lookAtPlayer()) {
				final EntityPlayer player = worldObj.getClosestPlayerToEntity(this, 10);
				if (player != null) {
					faceEntityMill(player, 10, 10);
					return;
				}
			}
		}
	}

	public void setGoalBuildingDestPoint(final Point newDest) {
		if (goalInformation == null) {
			goalInformation = new GoalInformation(null, null, null);
		}

		goalInformation.setDestBuildingPos(newDest);
	}

	public void setGoalDestEntity(final Entity ent) {
		if (goalInformation == null) {
			goalInformation = new GoalInformation(null, null, null);
		}

		goalInformation.setTargetEnt(ent);
		if (ent != null) {
			setPathDestPoint(new Point(ent));
		}

		if (ent instanceof MillVillager) {

			final MillVillager v = (MillVillager) ent;

			dialogueTargetFirstName = v.firstName;
			dialogueTargetLastName = v.familyName;
		}

	}

	public void setGoalDestPoint(final Point newDest) {

		if (goalInformation == null) {
			goalInformation = new GoalInformation(null, null, null);
		}

		goalInformation.setDest(newDest);
		setPathDestPoint(newDest);
	}

	public void setGoalInformation(final GoalInformation info) {
		goalInformation = info;
		if (info != null) {
			if (info.getTargetEnt() != null) {
				setPathDestPoint(new Point(info.getTargetEnt()));
			} else if (info.getDest() != null) {
				setPathDestPoint(info.getDest());
			} else {
				setPathDestPoint(null);
			}
		} else {
			setPathDestPoint(null);
		}
	}

	public void setHousePoint(final Point p) {
		housePoint = p;
		house = null;
	}

	public void setInv(final Item item, final int nb) {
		setInv(item, 0, nb);
	}

	public void setInv(final Item item, final int meta, final int nb) {
		try {
			inventory.put(new InvItem(item, meta), nb);
		} catch (final MillenaireException e) {
			MLN.printException(e);
		}
		if (getTownHall() != null) {
			getTownHall().updateVillagerRecord(this);
		}
	}

	public void setNextGoal() throws Exception {

		Goal nextGoal = null;
		clearGoal();

		for (final Goal goal : getGoals()) {
			if (goal.isPossible(this)) {
				if (MLN.LogGeneralAI >= MLN.MINOR && extraLog) {
					MLN.minor(this, "Priority for goal " + goal.gameName(this) + ": " + goal.priority(this));
				}
				if (nextGoal == null || nextGoal.priority(this) < goal.priority(this)) {
					nextGoal = goal;
				}
			}
		}

		if (MLN.LogGeneralAI >= MLN.MINOR && extraLog) {
			MLN.minor(this, "Selected this: " + nextGoal);
		}

		if (MLN.LogBuildingPlan >= MLN.MAJOR && nextGoal != null && nextGoal.key.equals(Goal.getResourcesForBuild.key)) {
			MLN.major(this, getName() + " is new builder, for: " + townHall.getCurrentBuildingPlan() + ". Blocks loaded: " + townHall.getBblocks().length);
		}

		if (nextGoal != null) {
			speakSentence(nextGoal.key + ".chosen");
			goalKey = nextGoal.key;
			heldItem = null;
			heldItemCount = Integer.MAX_VALUE;
			nextGoal.onAccept(this);
			goalStarted = System.currentTimeMillis();
			lastGoalTime.put(nextGoal, worldObj.getWorldTime());

		} else {
			goalKey = null;
		}

	}

	public void setPathDestPoint(final Point newDest) {
		if (newDest == null || !newDest.equals(pathDestPoint)) {
			setPathToEntity(null);
			pathEntity = null;
		}

		this.pathDestPoint = newDest;

	}

	public void setTexture(final ResourceLocation tx) {
		texture = tx;
	}

	public void setTownHallPoint(final Point p) {
		townHallPoint = p;
		townHall = null;
	}

	public void speakSentence(final String key) {
		speakSentence(key, 30 * 20, 3, 1);
	}

	public void speakSentence(final String key, final int chanceOn) {
		speakSentence(key, 30 * 20, 3, chanceOn);
	}

	public void speakSentence(String key, final int delay, final int distance, final int chanceOn) {

		if (delay > worldObj.getWorldTime() - speech_started) {
			return;
		}

		if (!MillCommonUtilities.chanceOn(chanceOn)) {
			return;
		}

		if (getTownHall() == null || getTownHall().closestPlayer == null || getPos().distanceTo(getTownHall().closestPlayer) > distance) {
			return;
		}

		key = key.toLowerCase();

		speech_key = null;

		if (getCulture().hasSentences(getNameKey() + "." + key)) {
			speech_key = getNameKey() + "." + key;
		} else if (getCulture().hasSentences(getGenderString() + "." + key)) {
			speech_key = getGenderString() + "." + key;
		} else if (getCulture().hasSentences("villager." + key)) {
			speech_key = "villager." + key;
		}

		if (speech_key != null) {
			speech_variant = MillCommonUtilities.randomInt(getCulture().getSentences(speech_key).size());
			speech_started = worldObj.getWorldTime();

			sendVillagerPacket();

			ServerSender.sendVillageSentenceInRange(worldObj, getPos(), 30, this);

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

	public int takeFromBuilding(final Building building, final InvItem ii, final int nb) {
		return takeFromBuilding(building, ii.getItem(), ii.meta, nb);
	}

	public int takeFromBuilding(final Building building, final Item item, final int nb) {
		return takeFromBuilding(building, item, 0, nb);
	}

	public int takeFromBuilding(final Building building, final Item item, final int meta, int nb) {
		if (item == Item.getItemFromBlock(Blocks.log) && meta == -1) {
			int nb2, total = 0;
			nb2 = building.takeGoods(item, 0, nb);
			addToInv(item, 0, nb2);
			total += nb2;
			nb2 = building.takeGoods(item, 0, nb - total);
			addToInv(item, 0, nb2);
			total += nb2;
			nb2 = building.takeGoods(item, 0, nb - total);
			addToInv(item, 0, nb2);
			total += nb2;
			return total;
		}
		nb = building.takeGoods(item, meta, nb);
		addToInv(item, meta, nb);
		return nb;
	}

	public int takeFromInv(final Block block, final int nb) {
		return takeFromInv(Item.getItemFromBlock(block), 0, nb);
	}

	public int takeFromInv(final Block block, final int meta, final int nb) {
		return takeFromInv(Item.getItemFromBlock(block), meta, nb);
	}

	public int takeFromInv(final InvItem item, final int nb) {
		return takeFromInv(item.getItem(), item.meta, nb);
	}

	public int takeFromInv(final Item item, final int nb) {
		return takeFromInv(item, 0, nb);
	}

	public int takeFromInv(final Item item, final int meta, int nb) {

		if (item == Item.getItemFromBlock(Blocks.log) && meta == -1) {
			int total = 0, nb2;
			InvItem key;
			try {
				for (int i = 0; i < 16; i++) {
					key = new InvItem(item, i);
					if (inventory.containsKey(key)) {
						nb2 = Math.min(nb, inventory.get(key));
						inventory.put(key, inventory.get(key) - nb2);
						total += nb2;
					}
				}
				if (getTownHall() != null) {
					getTownHall().updateVillagerRecord(this);
				}
			} catch (final MillenaireException e) {
				MLN.printException(e);
			}

			return total;
		} else {
			InvItem key;
			try {
				key = new InvItem(item, meta);
				if (inventory.containsKey(key)) {
					nb = Math.min(nb, inventory.get(key));
					inventory.put(key, inventory.get(key) - nb);
					if (getTownHall() != null) {
						getTownHall().updateVillagerRecord(this);
					}

					updateClothTexturePath();

					return nb;
				} else {
					return 0;
				}
			} catch (final MillenaireException e) {
				MLN.printException(e);
				return 0;
			}

		}

	}

	private void targetDefender() {

		int bestDist = Integer.MAX_VALUE;
		Entity target = null;

		for (final MillVillager v : getTownHall().villagers) {

			if (v.helpsInAttacks() && !v.isRaider) {

				if (getPos().distanceToSquared(v) < bestDist) {
					target = v;
					bestDist = (int) getPos().distanceToSquared(v);
				}
			}
		}

		if (target != null && getPos().distanceToSquared(target) <= 25) {
			entityToAttack = target;
		}
	}

	private void targetRaider() {

		int bestDist = Integer.MAX_VALUE;
		Entity target = null;

		for (final MillVillager v : getTownHall().villagers) {

			if (v.isRaider) {

				if (getPos().distanceToSquared(v) < bestDist) {
					target = v;
					bestDist = (int) getPos().distanceToSquared(v);
				}
			}
		}

		if (target != null && getPos().distanceToSquared(target) <= 25) {
			entityToAttack = target;
		}
	}

	private void teenagerNightAction() {

		// attempt a transfer to an other village to find work
		// we are assuming that in most cases he hasn't found room in his
		// village
		// as otherwise he'd have moved in already

		for (final Point p : getTownHall().getKnownVillages()) {

			if (getTownHall().getRelationWithVillage(p) > Building.RELATION_EXCELLENT) {
				final Building distantVillage = mw.getBuilding(p);

				if (distantVillage != null && distantVillage.culture == getCulture() && distantVillage != getTownHall()) {
					boolean canMoveIn = false;

					if (MLN.LogChildren >= MLN.MAJOR) {
						MLN.major(this, "Attempting to move to village: " + distantVillage.getVillageQualifiedName());
					}

					Building distantInn = null;
					for (final Building distantBuilding : distantVillage.getBuildings()) {
						if (!canMoveIn && distantBuilding != null && distantBuilding.isHouse()) {
							if (distantBuilding.canChildMoveIn(gender, familyName)) {
								canMoveIn = true;
							}
						} else if (distantInn == null && distantBuilding.isInn) {
							if (distantBuilding.vrecords.size() < 2) {
								distantInn = distantBuilding;
							}
						}
					}

					if (canMoveIn && distantInn != null) {

						if (MLN.LogChildren >= MLN.MAJOR) {
							MLN.major(this, "Moving to village: " + distantVillage.getVillageQualifiedName());
						}

						getHouse().transferVillager(getHouse().getVillagerRecordById(villager_id), distantInn, false);
						distantInn.visitorsList.add("panels.childarrived;" + getName() + ";" + getTownHall().getVillageQualifiedName());
					}
				}
			}

		}
	}

	public boolean teleportTo(final double d, final double d1, final double d2) {
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
		if (worldObj.blockExists(i, j, k)) {
			boolean flag1;
			for (flag1 = false; !flag1 && j > 0;) {
				final Block block = worldObj.getBlock(i, j - 1, k);
				if (block == Blocks.air || !block.getMaterial().blocksMovement()) {
					posY--;
					j--;
				} else {
					flag1 = true;
				}
			}

			if (flag1) {
				setPosition(posX, posY, posZ);
				if (worldObj.getCollidingBoundingBoxes(this, boundingBox).size() == 0 && !worldObj.isAnyLiquid(boundingBox)) {
					flag = true;
				}
			}
		}
		if (!flag) {
			setPosition(d3, d4, d5);
			return false;
		}

		return true;
	}

	public boolean teleportToEntity(final Entity entity) {
		Vec3 vec3d = Vec3.createVectorHelper(posX - entity.posX, boundingBox.minY + height / 2.0F - entity.posY + entity.getEyeHeight(), posZ - entity.posZ);
		vec3d = vec3d.normalize();
		final double d = 16D;
		final double d1 = posX + (rand.nextDouble() - 0.5D) * 8D - vec3d.xCoord * d;
		final double d2 = posY + (rand.nextInt(16) - 8) - vec3d.yCoord * d;
		final double d3 = posZ + (rand.nextDouble() - 0.5D) * 8D - vec3d.zCoord * d;
		return teleportTo(d1, d2, d3);
	}

	private void toggleDoor(final int i, final int j, final int k) {

		final int l = worldObj.getBlockMetadata(i, j, k);

		MillCommonUtilities.setBlockMetadata(worldObj, i, j, k, l ^ 4, true);
		worldObj.markBlockRangeForRenderUpdate(i, j - 1, k, i, j, k);

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@" + ": " + getName() + "/" + this.villager_id + "/" + worldObj;
	}

	private void triggerMobAttacks() {
		// Only spiders as other mobs can be handld by the MC AI system (see
		// MillEventController)
		final List<Entity> entities = MillCommonUtilities.getEntitiesWithinAABB(worldObj, EntityMob.class, getPos(), 16, 5);

		for (final Entity ent : entities) {

			final EntityMob mob = (EntityMob) ent;

			if (mob.getEntityToAttack() == null) {
				if (mob.canEntityBeSeen(this)) {
					mob.setTarget(this);
				}
			}
		}

	}

	private void updateClothTexturePath() {

		if (vtype == null) {
			return;
		}

		String bestClothName = null;
		int clothLevel = -1;

		if (vtype.getRandomClothTexture(FREE_CLOTHES) != null) {
			bestClothName = FREE_CLOTHES;
			clothLevel = 0;
		}

		for (final InvItem iv : inventory.keySet()) {
			if (iv.item == Mill.clothes && inventory.get(iv) > 0) {
				if (Mill.clothes.getClothPriority(iv.meta) > clothLevel) {
					// we need to check that the villager has the cloth, but
					// also that he can wear that type
					if (vtype.getRandomClothTexture(Mill.clothes.getClothName(iv.meta)) != null) {
						bestClothName = Mill.clothes.getClothName(iv.meta);
						clothLevel = Mill.clothes.getClothPriority(iv.meta);
					}
				}
			}
		}

		// best cloth to wear has changed
		if (bestClothName != null) {
			if (!bestClothName.equals(clothName) || !vtype.isClothValid(clothName, clothTexture.getResourcePath())) {
				clothName = bestClothName;
				clothTexture = new ResourceLocation(Mill.modId, vtype.getRandomClothTexture(bestClothName));
			}
		} else {
			clothName = null;
			clothTexture = null;
		}
	}

	private void updateDialogue() {

		if (dialogueKey == null) {
			return;
		}

		final Dialogue d = getCulture().getDialogue(dialogueKey);

		if (d == null) {
			dialogueKey = null;
			return;
		}

		final long timePassed = worldObj.getWorldTime() - dialogueStart;

		if (d.timeDelays.get(d.timeDelays.size() - 1) + 100 < timePassed) {
			dialogueKey = null;
			return;
		}

		String toSpeakKey = null;

		for (int i = 0; i < d.speechBy.size(); i++) {
			if (dialogueRole == d.speechBy.get(i) && timePassed >= d.timeDelays.get(i)) {
				toSpeakKey = "chat_" + d.key + "_" + i;
			}
		}

		if (toSpeakKey != null && (speech_key == null || !speech_key.contains(toSpeakKey))) {
			speakSentence(toSpeakKey, 0, 10, 1);
		}
	}

	private void updateHired() {

		try {
			if (getHealth() < getMaxHealth() & MillCommonUtilities.randomInt(1600) == 0) {
				setHealth(getHealth() + 1);
			}

			final EntityPlayer entityplayer = worldObj.getPlayerEntityByName(hiredBy);

			if (worldObj.getWorldTime() > hiredUntil) {

				if (entityplayer != null) {
					ServerSender.sendTranslatedSentence(entityplayer, MLN.WHITE, "hire.hireover", getName());
				}

				hiredBy = null;
				hiredUntil = 0;

				final VillagerRecord vr = getTownHall().getVillagerRecordById(villager_id);
				if (vr != null) {
					vr.awayhired = false;
				}

				return;
			}

			if (entityToAttack != null) {
				if (getPos().distanceTo(entityToAttack) > ATTACK_RANGE || worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
					entityToAttack = null;
				}
			} else {
				if (isHostile() && worldObj.difficultySetting != EnumDifficulty.PEACEFUL && getTownHall().closestPlayer != null && getPos().distanceTo(getTownHall().closestPlayer) <= ATTACK_RANGE) {
					entityToAttack = worldObj.getClosestPlayer(posX, posY, posZ, 100);

				}
			}

			if (entityToAttack == null) {
				List<?> list = worldObj.getEntitiesWithinAABB(EntityCreature.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(16D, 8D, 16D));

				// first possible target: entity attacking the player
				for (final Object o : list) {
					if (entityToAttack == null) {
						final EntityCreature creature = (EntityCreature) o;

						if (creature.getEntityToAttack() == entityplayer && !(creature instanceof EntityCreeper)) {
							entityToAttack = creature;
						}
					}
				}

				// otherwise, any mob or hostile villager
				if (entityToAttack == null && aggressiveStance) {
					list = worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(16D, 8D, 16D));

					if (!list.isEmpty()) {
						entityToAttack = (Entity) list.get(worldObj.rand.nextInt(list.size()));

						if (entityToAttack instanceof EntityCreeper) {
							entityToAttack = null;
						}
					}
					if (entityToAttack == null) {
						list = worldObj.getEntitiesWithinAABB(MillVillager.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(16D, 8D, 16D));

						for (final Object o : list) {
							if (entityToAttack == null) {
								final MillVillager villager = (MillVillager) o;

								if (villager.isHostile()) {
									entityToAttack = villager;
								}
							}
						}
					}

				}

			}

			Entity target = null;

			if (entityToAttack != null) {
				target = entityToAttack;
				heldItem = getWeapon();

				final PathEntity pathentity = worldObj.getPathEntityToEntity(this, target, 16F, true, false, false, true);
				if (pathentity != null) {
					setPathToEntity(pathentity);
				}

			} else {

				heldItem = null;
				final Entity player = (Entity) mw.world.playerEntities.get(0);
				target = player;

				final int dist = (int) getPos().distanceTo(target);

				if (dist > 16) {
					teleportToEntity(player);
				} else if (dist > 4) {
					final PathEntity pathentity = worldObj.getPathEntityToEntity(this, target, 16F, true, false, false, true);
					if (pathentity != null) {
						setPathToEntity(pathentity);
					}
				}
			}

			prevPoint = getPos();

			handleDoorsAndFenceGates();

		} catch (final Exception e) {
			MLN.printException("Error in hired onUpdate():", e);
		}

	}

	private void updatePathIfNeeded(final Point dest) throws Exception {
		if (dest == null) {
			return;
		}

		if (pathEntity != null && pathEntity.getCurrentPathLength() > 0 && !MillCommonUtilities.chanceOn(50) && pathEntity.getCurrentTargetPathPoint() != null) {// all
																																									// good
			if (MLN.DEV) {
				getTownHall().monitor.nbPathing++;
				getTownHall().monitor.nbReused++;
			}
			setPathToEntity(pathEntity);// because EntityCreature randomly
			// clears it
		} else {
			if (MLN.jpsPathing) {
				if (!jpsPathPlanner.isBusy()) {
					computeNewPath(dest);
				}
			} else {
				if (pathingWorker == null) {// only if no update already running
					computeNewPath(dest);
				}
			}
		}
	}

	public float updateRotation(final float f, final float f1, final float f2) {
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
	public void writeEntityToNBT(final NBTTagCompound nbttagcompound) {
		try {

			if (vtype == null) {
				MLN.error(this, "Not saving villager due to null vtype.");
				return;
			}

			super.writeEntityToNBT(nbttagcompound);

			nbttagcompound.setString("vtype", vtype.key);

			nbttagcompound.setString("culture", getCulture().key);

			nbttagcompound.setString("texture", texture.getResourcePath());
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
			if (doorToClose != null) {
				doorToClose.write(nbttagcompound, "doorToClose");
			}

			nbttagcompound.setInteger("action", action);
			if (goalKey != null) {
				nbttagcompound.setString("goal", goalKey);
			}
			nbttagcompound.setString("firstName", firstName);
			nbttagcompound.setString("familyName", familyName);
			nbttagcompound.setFloat("scale", scale);
			nbttagcompound.setInteger("gender", gender);
			nbttagcompound.setLong("lastSpeechLong", speech_started);
			nbttagcompound.setLong("villager_lid", villager_id);

			if (dialogueKey != null) {
				nbttagcompound.setString("dialogueKey", dialogueKey);
				nbttagcompound.setLong("dialogueStart", dialogueStart);
				nbttagcompound.setInteger("dialogueRole", dialogueRole);
				nbttagcompound.setInteger("dialogueColour", dialogueColour);
				nbttagcompound.setBoolean("dialogueChat", dialogueChat);
			}

			final NBTTagList nbttaglist = new NBTTagList();
			for (final InvItem key : inventory.keySet()) {

				final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setInteger("item", Item.getIdFromItem(key.getItem()));
				nbttagcompound1.setInteger("meta", key.meta);
				nbttagcompound1.setInteger("amount", inventory.get(key));
				nbttaglist.appendTag(nbttagcompound1);

			}
			nbttagcompound.setTag("inventory", nbttaglist);

			nbttagcompound.setInteger("previousBlock", Block.getIdFromBlock(previousBlock));
			nbttagcompound.setInteger("previousBlockMeta", previousBlockMeta);
			nbttagcompound.setInteger("size", size);
			nbttagcompound.setBoolean("hasPrayedToday", hasPrayedToday);
			nbttagcompound.setBoolean("hasDrunkToday", hasDrunkToday);

			if (hiredBy != null) {
				nbttagcompound.setString("hiredBy", hiredBy);
				nbttagcompound.setLong("hiredUntil", hiredUntil);
				nbttagcompound.setBoolean("aggressiveStance", aggressiveStance);
			}

			nbttagcompound.setBoolean("isRaider", isRaider);

			if (clothName != null) {
				nbttagcompound.setString("clothName", clothName);
				nbttagcompound.setString("clothTexture", clothTexture.getResourcePath());
			}
		} catch (final Exception e) {
			MLN.printException("Exception when attempting to save villager " + this, e);
		}
	}

	@Override
	public void writeSpawnData(final ByteBuf ds) {
		final ByteBufOutputStream data = new ByteBufOutputStream(ds);
		try {
			writeVillagerStreamData(data, true);
		} catch (final IOException e) {
			MLN.printException("Error in writeSpawnData for villager " + this, e);
		}
	}

	private void writeVillagerStreamData(final DataOutput data, final boolean isSpawn) throws IOException {

		if (vtype == null) {
			MLN.error(this, "Cannot write stream data due to null vtype.");
			return;
		}

		data.writeLong(villager_id);
		StreamReadWrite.writeNullableString(vtype.culture.key, data);
		StreamReadWrite.writeNullableString(vtype.key, data);

		StreamReadWrite.writeNullableResourceLocation(texture, data);

		StreamReadWrite.writeNullableString(goalKey, data);
		StreamReadWrite.writeNullablePoint(housePoint, data);
		StreamReadWrite.writeNullablePoint(townHallPoint, data);
		StreamReadWrite.writeNullableString(firstName, data);
		StreamReadWrite.writeNullableString(familyName, data);
		data.writeFloat(scale);
		data.writeInt(gender);
		data.writeInt(size);
		StreamReadWrite.writeNullableString(hiredBy, data);
		data.writeBoolean(aggressiveStance);
		data.writeLong(hiredUntil);
		data.writeBoolean(isUsingBow);
		data.writeBoolean(isUsingHandToHand);
		StreamReadWrite.writeNullableString(speech_key, data);
		data.writeInt(speech_variant);
		data.writeLong(speech_started);
		StreamReadWrite.writeNullableItemStack(heldItem, data);
		StreamReadWrite.writeInventory(inventory, data);
		StreamReadWrite.writeNullableString(clothName, data);
		StreamReadWrite.writeNullableResourceLocation(clothTexture, data);
		StreamReadWrite.writeNullablePoint(getGoalDestPoint(), data);
		data.writeBoolean(shouldLieDown);
		StreamReadWrite.writeNullableString(dialogueTargetFirstName, data);
		StreamReadWrite.writeNullableString(dialogueTargetLastName, data);
		data.writeChar(dialogueColour);
		data.writeBoolean(dialogueChat);
		data.writeFloat(getHealth());

		if (isSpawn) {
			calculateMerchantGoods();

			data.writeInt(merchantSells.size());

			for (final Goods g : merchantSells.keySet()) {
				StreamReadWrite.writeNullableGoods(g, data);
				data.writeInt(merchantSells.get(g));
			}
		} else {
			data.writeInt(-1);
		}

		if (getGoalDestEntity() != null) {
			data.writeInt(getGoalDestEntity().getEntityId());
		} else {
			data.writeInt(-1);
		}

	}

}
