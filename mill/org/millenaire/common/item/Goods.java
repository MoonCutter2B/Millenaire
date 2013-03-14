package org.millenaire.common.item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.EntityWallDecoration;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.VillageList;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class Goods {

	public static class ItemAmuletSkollHati extends ItemText {

		public ItemAmuletSkollHati(int i,String iconName) {
			super(i,iconName);
		}

		@Override
		public ItemStack onItemRightClick(ItemStack itemstack, World world,
				EntityPlayer entityplayer) {

			if (MLN.Other>=MLN.DEBUG) {
				MLN.debug(this, "Using skoll amulet.");
			}

			if (world.isRemote)
				return itemstack;

			final long time = world.getWorldTime() + 24000L;

			if (((time % 24000L) > 11000L) && ((time % 24000L) < 23500L)) {
				world.setWorldTime(time - (time % 24000L) - 500L);
			} else {
				world.setWorldTime((time - (time % 24000L)) + 13000L);
			}

			if (!MLN.infiniteAmulet) {
				itemstack.damageItem(1, entityplayer);
			}

			return itemstack;
		}
	}

	public static class ItemBrickMould extends ItemText
	{

		public ItemBrickMould(int i,String iconName) {
			super(i,iconName);
			this.setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUseFirst(ItemStack itemstack,
				EntityPlayer entityplayer, World world, int i, int j, int k,
				int l, float hitX, float hitY, float hitZ) {
			if(world.getBlockId(i, j, k) == Block.snow.blockID)
			{
				l = 0;
			} else
			{
				if(l == 0)
				{
					j--;
				}
				if(l == 1)
				{
					j++;
				}
				if(l == 2)
				{
					k--;
				}
				if(l == 3)
				{
					k++;
				}
				if(l == 4)
				{
					i--;
				}
				if(l == 5)
				{
					i++;
				}
			}

			if (world.getBlockId(i, j, k)!=0)
				return false;

			if ((MillCommonUtilities.countChestItems(entityplayer.inventory, Block.dirt.blockID, 0)==0) || (MillCommonUtilities.countChestItems(entityplayer.inventory, Block.sand.blockID, 0)==0)) {

				if (!world.isRemote) {
					ServerSender.sendTranslatedSentence(entityplayer,MLN.WHITE, "ui.brickinstructions");
				}
				return false;
			}

			MillCommonUtilities.getItemsFromChest(entityplayer.inventory, Block.dirt.blockID, 0, 1);
			MillCommonUtilities.getItemsFromChest(entityplayer.inventory, Block.sand.blockID, 0, 1);

			world.setBlockAndMetadataWithNotify(i, j, k, Mill.earth_decoration.blockID, 0,2);

			itemstack.damageItem(1, entityplayer);

			return false;

		}
	}

	public static class ItemClothes extends Item {

		public final String[] iconNames;
		public Icon[] icons=null;

		public ItemClothes(int i,String... iconNames) {
			super(i);
			this.setHasSubtypes(true);
			this.setMaxDamage(0);
			this.iconNames=iconNames;
		}

		public String getClothName(int meta) {
			if (meta==0)
				return "clothes_byz_wool";
			else
				return "clothes_byz_silk";
		}

		public int getClothPriority(int meta) {
			if (meta==0)
				return 1;
			else
				return 2;
		}

		@Override
		public Icon getIconFromDamage(int meta) {

			if (meta<iconNames.length)
				return icons[meta];

			return icons[0];
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			icons=new Icon[iconNames.length];

			for (int i=0;i<iconNames.length;i++) {
				icons[i]=MillCommonUtilities.getIcon(iconRegister, iconNames[i]);
			}
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconNames[0]);
		}


		@Override
		public String getUnlocalizedName(ItemStack par1ItemStack) {
			final int meta = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 15);

			return "item."+getClothName(meta);

		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@SideOnly(Side.CLIENT)
		@Override
		public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
		{
			for (int var4 = 0; var4 < 2; ++var4)
			{
				par3List.add(new ItemStack(par1, 1, var4));
			}
		}
	}

	public static class ItemMillenaireArmour extends ItemArmor {

		int  enchantmentValue;

		public final String iconName;

		public ItemMillenaireArmour(int id,String iconName, EnumArmorMaterial material, int armourId, double durationMultiplier, int enchantmentValue, int type) {
			super(id, material, armourId, type);
			setMaxDamage((int) (getMaxDamage()*durationMultiplier));
			this.enchantmentValue=enchantmentValue;
			this.iconName=iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public int getItemEnchantability() {
			return enchantmentValue;
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireAxe extends ItemTool {

		int enchantability;

		public final String iconName;

		public ItemMillenaireAxe(int i,String iconName,EnumToolMaterial material,int strength) {
			super(i, 3, material, new Block[] {
					Block.planks, Block.bookShelf, Block.wood, Block.chest, Block.stoneDoubleSlab, Block.stoneSingleSlab, Block.pumpkin, Block.pumpkinLantern, Mill.wood_decoration
			});

			efficiencyOnProperMaterial=strength;
			enchantability=-1;//use default value
			this.iconName=iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		public ItemMillenaireAxe(int i,String iconName,EnumToolMaterial material,int strength,int durability,int enchantability) {
			super(i, 3, material, new Block[] {
					Block.planks, Block.bookShelf, Block.wood, Block.chest, Block.stoneDoubleSlab, Block.stoneSingleSlab, Block.pumpkin, Block.pumpkinLantern, Mill.wood_decoration
			});

			efficiencyOnProperMaterial=strength;
			setMaxDamage(durability);
			this.enchantability=enchantability;
			this.iconName=iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public int getItemEnchantability() {
			if (enchantability==-1)
				return super.getItemEnchantability();

			return enchantability;
		}

		@Override
		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
		{
			return (par2Block != null) && ((par2Block.blockMaterial == Material.wood) || (par2Block.blockMaterial == Material.plants) || (par2Block.blockMaterial == Material.vine)) ? this.efficiencyOnProperMaterial : super.getStrVsBlock(par1ItemStack, par2Block);
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireBow extends ItemBow {

		public float speedFactor=1;
		public float damageBonus=0;
		public int iconPos;
		public final String[] iconNames;
		public Icon[] icons;

		public ItemMillenaireBow(int itemId,float speedFactor,float damageBonus,String... iconNames) {
			super(itemId);
			this.speedFactor=speedFactor;
			this.damageBonus=damageBonus;
			this.iconNames=iconNames;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconNames[0]);

			icons=new Icon[iconNames.length];

			for (int i=0;i<iconNames.length;i++) {
				icons[i]=MillCommonUtilities.getIcon(iconRegister, iconNames[i]);
			}
		}

		/**
		 * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
		 *
		 * Taken from ItemBow, MC 1.4.2
		 */

		@Override
		public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
		{
			int var6 = this.getMaxItemUseDuration(par1ItemStack) - par4;

			final ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, var6);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled())
				return;
			var6 = event.charge;

			final boolean var5 = par3EntityPlayer.capabilities.isCreativeMode || (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0);

			if (var5 || par3EntityPlayer.inventory.hasItem(Item.arrow.itemID))
			{
				float var7 = var6 / 20.0F;
				var7 = ((var7 * var7) + (var7 * 2.0F)) / 3.0F;

				if (var7 < 0.1D)
					return;

				if (var7 > 1.0F)
				{
					var7 = 1.0F;
				}

				final EntityArrow var8 = new EntityArrow(par2World, par3EntityPlayer, var7 * 2.0F);

				if (var7 == 1.0F)
				{
					var8.setIsCritical(true);
				}

				final int var9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, par1ItemStack);

				if (var9 > 0)
				{
					var8.setDamage(var8.getDamage() + (var9 * 0.5D) + 0.5D);
				}

				final int var10 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, par1ItemStack);

				if (var10 > 0)
				{
					var8.setKnockbackStrength(var10);
				}

				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, par1ItemStack) > 0)
				{
					var8.setFire(100);
				}

				par1ItemStack.damageItem(1, par3EntityPlayer);
				par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, (1.0F / ((itemRand.nextFloat() * 0.4F) + 1.2F)) + (var7 * 0.5F));

				if (var5)
				{
					var8.canBePickedUp = 2;
				}
				else
				{
					par3EntityPlayer.inventory.consumeInventoryItem(Item.arrow.itemID);
				}

				//faster MLN arrows
				var8.motionX*=speedFactor;
				var8.motionY*=speedFactor;
				var8.motionZ*=speedFactor;

				//extra arrow damage
				var8.setDamage(var8.getDamage()+damageBonus);

				if (!par2World.isRemote)
				{
					par2World.spawnEntityInWorld(var8);
				}
			}
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
		{
			final EntityPlayer entityplayer = (EntityPlayer)entity;
			Mill.proxy.updateBowIcon(this, entityplayer);
		}

		public void setBowIcon(int pos) {
			iconPos = pos;
		}

		@Override
		public Icon getIconFromDamage(int par1)
	    {
	        return icons[iconPos];
	    }
	}


	public static class ItemMillenaireHoe extends ItemHoe {

		public final String iconName;

		public ItemMillenaireHoe(int i,String iconName,int durability) {
			super(i, EnumToolMaterial.IRON);//material has no effect except durability that is overridden
			setMaxDamage(durability);
			setCreativeTab(Mill.tabMillenaire);
			this.iconName=iconName;
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenairePickaxe extends ItemPickaxe {

		int enchantability;
		public final String iconName;

		public ItemMillenairePickaxe(int i,String iconName,EnumToolMaterial material,int strength) {
			super(i, material);

			efficiencyOnProperMaterial=strength;
			this.enchantability=-1;
			this.iconName=iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		public ItemMillenairePickaxe(int i,String iconName,EnumToolMaterial material,int strength,int durability,int enchantability) {
			super(i, material);

			efficiencyOnProperMaterial=strength;
			setMaxDamage(durability);
			this.enchantability=enchantability;
			this.iconName=iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public int getItemEnchantability() {
			if (enchantability==-1)
				return super.getItemEnchantability();

			return enchantability;
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireShovel extends ItemSpade {

		int enchantability;
		public final String iconName;

		public ItemMillenaireShovel(int i,String iconName,EnumToolMaterial material,int strength) {
			super(i, material);

			efficiencyOnProperMaterial=strength;
			this.enchantability=-1;
			this.iconName=iconName;

			setCreativeTab(Mill.tabMillenaire);
		}

		public ItemMillenaireShovel(int i,String iconName,EnumToolMaterial material,int strength,int durability,int enchantability) {
			super(i, material);
			efficiencyOnProperMaterial=strength;
			setMaxDamage(durability);
			this.enchantability=enchantability;
			setCreativeTab(Mill.tabMillenaire);
			this.iconName=iconName;
		}

		@Override
		public int getItemEnchantability() {
			if (enchantability==-1)
				return super.getItemEnchantability();

			return enchantability;
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireSword extends ItemSword {

		int damage;
		float criticalChance;
		int criticalMultiple;
		int enchantability;

		public final String iconName;

		boolean knockback;
		public ItemMillenaireSword(int i,String iconName,int maxUse,int damage, int enchantability,float criticalChance,int criticalMultiple,boolean knockback) {
			super(i, EnumToolMaterial.IRON);//material isn't really used (all uses are overridden)
			setMaxDamage(maxUse);
			this.damage=damage;
			this.criticalChance=criticalChance;
			this.criticalMultiple=criticalMultiple;
			this.enchantability=enchantability;
			this.knockback=knockback;
			this.iconName=iconName;
			setCreativeTab(Mill.tabMillenaire);
		}
		@Override
		public int getDamageVsEntity(Entity entity) {

			if ((entity!=null) && MillCommonUtilities.probability(criticalChance) && !entity.worldObj.isRemote) {
				ServerSender.sendTranslatedSentenceInRange(entity.worldObj, new Point(entity), 10,MLN.DARKRED, "weapon.criticalstrike",""+criticalMultiple);
				return damage*criticalMultiple;
			}

			return damage;
		}
		@Override
		public int getItemEnchantability() {
			return enchantability;
		}
		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
		}

		@Override
		public boolean hitEntity(ItemStack itemstack,
				EntityLiving entityliving, EntityLiving player) {

			return super.hitEntity(itemstack, entityliving, player);
		}

		@Override
		public boolean onBlockStartBreak(ItemStack itemstack, int i, int j,
				int k, EntityPlayer player) {

			if (MLN.DEV && (i==Block.grass.blockID)) {
				MillCommonUtilities.spawnItem(player.worldObj, new Point(i,j,k), new ItemStack(Item.appleGold.itemID,1,0), 0.3f);
			}

			return super.onBlockStartBreak(itemstack, i, j, k, player);
		}

		@Override
		public void onCreated(ItemStack par1ItemStack, World par2World,
				EntityPlayer par3EntityPlayer) {

			if (knockback) {
				par1ItemStack.addEnchantment(Enchantment.knockback, 2);
			}

		}


		@Override
		public boolean onItemUse(ItemStack par1ItemStack,
				EntityPlayer par2EntityPlayer, World par3World, int par4,
				int par5, int par6, int par7, float par8, float par9,
				float par10) {

			if (knockback) {
				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, par1ItemStack)==0) {
					par1ItemStack.addEnchantment(Enchantment.knockback, 2);
				}

			}
			return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5,
					par6, par7, par8, par9, par10);
		}

		@Override
		public boolean onItemUseFirst(ItemStack stack, EntityPlayer player,
				World world, int x, int y, int z, int side, float hitX,
				float hitY, float hitZ) {

			if (knockback) {
				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack)==0) {
					stack.addEnchantment(Enchantment.knockback, 2);
				}

			}


			return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY,
					hitZ);
		}

		@Override
		public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
				Entity entity) {
			if (knockback) {
				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack)==0) {
					stack.addEnchantment(Enchantment.knockback, 2);
				}

			}
			return super.onLeftClickEntity(stack, player, entity);
		}

	}

	public static class ItemNegationWand extends ItemText {

		public ItemNegationWand(int i,String iconName) {
			super(i,iconName);
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUseFirst(ItemStack itemstack,
				EntityPlayer entityplayer, World world, int x, int y, int z,
				int l, float hitX, float hitY, float hitZ) {

			final Point pos=new Point(x,y,z);

			final int bid=world.getBlockId(x,y,z);

			if ((bid==Block.signPost.blockID) && world.isRemote) {
				BuildingPlan.exportBuilding(entityplayer,world, pos);
				return true;
			}

			if (world.isRemote)
				return false;

			final MillWorld mw=Mill.getMillWorld(world);

			for (int i=0;i<2;i++) {

				VillageList list;

				if (i==0) {
					list=mw.loneBuildingsList;
				} else {
					list=mw.villagesList;
				}

				for (int j=0;j<list.names.size();j++) {

					final Point p=list.pos.get(j);

					final int distance = MathHelper.floor_double(p.horizontalDistanceTo(pos));

					if (distance <= 30) {

						final Building th=mw.getBuilding(p);

						if ((th!=null) && th.isTownhall) {
							if (th.chestLocked) {
								ServerSender.sendTranslatedSentence(entityplayer,MLN.ORANGE, "negationwand.villagelocked",th.villageType.name);
								return true;
							}
							ServerSender.displayNegationWandGUI(entityplayer,th);

						}
					}
				}
			}

			return false;

		}

	}
	public static class ItemSummoningWand extends ItemText {

		public ItemSummoningWand(int i,String iconName) {
			super(i,iconName);
			this.setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUseFirst(ItemStack itemstack,
				EntityPlayer entityplayer, World world, int i, int j, int k,
				int l, float hitX, float hitY, float hitZ) {//client-side

			final Point pos=new Point(i,j,k);

			final int bid=world.getBlockId(i, j, k);

			if ((bid==Block.signPost.blockID)) {
				ClientSender.importBuilding(entityplayer, pos);
				return true;
			} else if ((bid==Mill.lockedChest.blockID))
				return false;

			ClientSender.summoningWandUse(entityplayer, pos);

			return true;

		}
	}



	public static class ItemTapestry extends ItemText
	{

		public int type;

		public ItemTapestry(int i,String iconName,int type)
		{
			super(i,iconName);
			this.type=type;
			this.setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float par8, float par9, float par10)
		{
			if(side == 0)
				return false;
			if(side == 1)
				return false;

			int orientation = Direction.vineGrowth[side];

			final EntityWallDecoration entitypainting = new EntityWallDecoration(world, i, j, k, orientation, type, false);
			if(entitypainting.onValidSurface())
			{
				if(!world.isRemote)
				{
					world.spawnEntityInWorld(entitypainting);
				}
				itemstack.stackSize--;
			}
			return true;
		}
	}

	public static class ItemText extends Item {

		public final String iconName;

		public ItemText(int i,String iconName) {
			super(i);
			this.setCreativeTab(Mill.tabMillenaire);
			this.iconName=iconName;
		}

		@Override
		public void func_94581_a(IconRegister iconRegister)
		{
			iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
		}


	}

	public static final Vector<InvItem> freeGoods=new Vector<InvItem>();


	public static final HashMap<String,InvItem> goodsName=new HashMap<String,InvItem>();



	public static final String BOUDIN="boudin";
	public static final String TRIPES="tripes";
	public static final String CALVA="calva";
	static {
		freeGoods.add(new InvItem(Block.dirt.blockID,0));
		freeGoods.add(new InvItem(Mill.earth_decoration.blockID,1));
		freeGoods.add(new InvItem(Block.waterStill.blockID,0));
		freeGoods.add(new InvItem(Block.sapling.blockID,0));
		freeGoods.add(new InvItem(Block.plantYellow.blockID,0));
		freeGoods.add(new InvItem(Block.plantRed.blockID,0));
		freeGoods.add(new InvItem(Block.tallGrass.blockID,0));
		freeGoods.add(new InvItem(Block.blockClay.blockID,0));
		freeGoods.add(new InvItem(Block.brewingStand.blockID,0));
		freeGoods.add(new InvItem(Block.leaves.blockID,-1));
	}
	public static void generateGoodsList() {

		final File file = new File(Mill.proxy.getBaseDir(),"goods.txt");

		try {
			final BufferedWriter writer = MillCommonUtilities.getWriter(file);
			writer.write("//Item key;item id;item meta;label (indicative only)"+MLN.EOL);
			writer.write("//This file is auto-generated and indicative only. Don't edit it."+MLN.EOL+MLN.EOL);

			final Vector<String> names=new Vector<String>(goodsName.keySet());
			Collections.sort(names);

			for (final String name : names) {
				final InvItem iv=goodsName.get(name);
				if ((iv.id()>=(MLN.itemRangeStart+256)) && (iv.id()<=(MLN.itemRangeStart+256+200))) {
					writer.write(name+";mln"+(iv.id()-MLN.itemRangeStart-256)+";"+iv.meta+";"+iv.getName()+MLN.EOL);
				} else {
					writer.write(name+";"+(iv.id())+";"+iv.meta+";"+iv.getName()+MLN.EOL);
				}
			}
			writer.close();

		} catch (final Exception e) {
			MLN.error(null, "Error when writing goods list: ");
			MLN.printException(e);
		}
	}

	private static void loadGoodList(File file) {

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {
					final String[] temp=line.trim().split(";");

					if (temp.length>2) {
						final String name=temp[0];
						int id;
						if (temp[1].startsWith("mln")) {
							id=Integer.parseInt(temp[1].substring(3, temp[1].length()))+MLN.itemRangeStart+256;
						} else {
							id=Integer.parseInt(MillCommonUtilities.swapConfigBlockId(temp[1]));
						}

						goodsName.put(name, new InvItem(id,Integer.parseInt(temp[2])));
					}
				}
			}
		} catch (final Exception e) {
			MLN.printException(e);
			return;
		}

	}

	public static void loadGoods() {

		loadStaticGoods();

		for (final File loadDir : Mill.loadingDirs) {

			final File mainList = new File(loadDir,"itemlist.txt");

			if (mainList.exists()) {
				loadGoodList(mainList);
			}
		}

		generateGoodsList();

		goodsName.put("anyenchanted", new InvItem(InvItem.ANYENCHANTED));
		goodsName.put("enchantedsword", new InvItem(InvItem.ENCHANTEDSWORD));


	}

	private static void loadStaticGoods() {

		goodsName.put("akwardpotion", new InvItem(Item.potion,16));
		goodsName.put("arrow", new InvItem(Item.arrow,0));
		goodsName.put("beefcooked", new InvItem(Item.beefCooked,0));
		goodsName.put("beefraw", new InvItem(Item.beefRaw,0));
		goodsName.put("bone", new InvItem(Item.bone,0));
		goodsName.put("book", new InvItem(Item.book,0));
		goodsName.put("bookshelves", new InvItem(Block.bookShelf,0));
		goodsName.put("bottle", new InvItem(Item.glassBottle,0));
		goodsName.put("bow", new InvItem(Item.bow,0));
		goodsName.put("bowlempty", new InvItem(Item.bowlEmpty,0));
		goodsName.put("bowlsoup", new InvItem(Item.bowlSoup,0));
		goodsName.put("bread", new InvItem(Item.bread,0));
		goodsName.put("bucketempty", new InvItem(Item.bucketEmpty,0));
		goodsName.put("bucketlava", new InvItem(Item.bucketLava,0));
		goodsName.put("bucketmilk", new InvItem(Item.bucketMilk,0));
		goodsName.put("bucketwater", new InvItem(Item.bucketWater,0));
		goodsName.put("cactus", new InvItem(Block.cactus,0));
		goodsName.put("cake", new InvItem(Block.cake,0));
		goodsName.put("chickenmeat", new InvItem(Item.chickenRaw,0));
		goodsName.put("chickenmeatcooked", new InvItem(Item.chickenCooked,0));
		goodsName.put("clay", new InvItem(Item.clay,0));
		goodsName.put("clock", new InvItem(Item.pocketSundial,0));
		goodsName.put("coal", new InvItem(Item.coal,0));
		goodsName.put("cobblestone", new InvItem(Block.cobblestone,0));
		goodsName.put("compass", new InvItem(Item.compass,0));
		goodsName.put("cookie", new InvItem(Item.cookie,0));
		goodsName.put("diamond", new InvItem(Item.diamond,0));
		goodsName.put("diamondaxe", new InvItem(Item.axeDiamond,0));
		goodsName.put("diamondboots", new InvItem(Item.bootsDiamond,0));
		goodsName.put("diamondchest", new InvItem(Item.plateDiamond,0));
		goodsName.put("diamondhelmet", new InvItem(Item.helmetDiamond,0));
		goodsName.put("diamondhoe", new InvItem(Item.hoeDiamond,0));
		goodsName.put("diamondlegs", new InvItem(Item.legsDiamond,0));
		goodsName.put("diamondpickaxe", new InvItem(Item.pickaxeDiamond,0));
		goodsName.put("diamondshovel", new InvItem(Item.shovelDiamond,0));
		goodsName.put("diamondsword", new InvItem(Item.swordDiamond,0));
		goodsName.put("dirt", new InvItem(Block.dirt,0));
		goodsName.put("dye_black", new InvItem(Item.dyePowder,0));
		goodsName.put("dye_blue", new InvItem(Item.dyePowder,4));
		goodsName.put("dye_brown", new InvItem(Item.dyePowder,3));
		goodsName.put("dye_cyan", new InvItem(Item.dyePowder,6));
		goodsName.put("dye_gray", new InvItem(Item.dyePowder,8));
		goodsName.put("dye_green", new InvItem(Item.dyePowder,2));
		goodsName.put("dye_lightblue", new InvItem(Item.dyePowder,12));
		goodsName.put("dye_lightgray", new InvItem(Item.dyePowder,7));
		goodsName.put("dye_magenta", new InvItem(Item.dyePowder,13));
		goodsName.put("dye_orange", new InvItem(Item.dyePowder,14));
		goodsName.put("dye_pink", new InvItem(Item.dyePowder,9));
		goodsName.put("dye_purple", new InvItem(Item.dyePowder,5));
		goodsName.put("dye_red", new InvItem(Item.dyePowder,1));
		goodsName.put("dye_white", new InvItem(Item.dyePowder,15));
		goodsName.put("dye_yellow", new InvItem(Item.dyePowder,11));
		goodsName.put("dye_lime", new InvItem(Item.dyePowder,10));
		goodsName.put("egg", new InvItem(Item.egg,0));
		goodsName.put("enderpearl", new InvItem(Item.enderPearl,0));
		goodsName.put("feather", new InvItem(Item.feather,0));
		goodsName.put("fishcooked", new InvItem(Item.fishCooked,0));
		goodsName.put("fishraw", new InvItem(Item.fishRaw,0));
		goodsName.put("flint", new InvItem(Item.flint,0));
		goodsName.put("flintandsteel", new InvItem(Item.flintAndSteel,0));
		goodsName.put("glass", new InvItem(Block.glass,0));
		goodsName.put("gold", new InvItem(Item.ingotGold,0));
		goodsName.put("goldore", new InvItem(Block.oreGold,0));
		goodsName.put("gunpowder", new InvItem(Item.gunpowder,0));
		goodsName.put("iron", new InvItem(Item.ingotIron,0));
		goodsName.put("ironore", new InvItem(Block.oreIron,0));
		goodsName.put("leather", new InvItem(Item.leather,0));
		goodsName.put("leatherboots", new InvItem(Item.bootsLeather,0));
		goodsName.put("leatherchest", new InvItem(Item.plateLeather,0));
		goodsName.put("leatherhelmet", new InvItem(Item.helmetLeather,0));
		goodsName.put("leatherlegs", new InvItem(Item.legsLeather,0));
		goodsName.put("melonseeds", new InvItem(Item.melonSeeds,0));
		goodsName.put("melonslice", new InvItem(Item.melon,0));
		goodsName.put("mushroombrown", new InvItem(Block.mushroomBrown,0));
		goodsName.put("mushroomred", new InvItem(Block.mushroomRed,0));
		goodsName.put("netherwart", new InvItem(Item.netherStalkSeeds,0));
		goodsName.put("obsidian", new InvItem(Block.obsidian,0));
		goodsName.put("painting", new InvItem(Item.painting,0));
		goodsName.put("paper", new InvItem(Item.paper,0));
		goodsName.put("planks_birch", new InvItem(Block.planks,2));
		goodsName.put("planks_jungle", new InvItem(Block.planks,3));
		goodsName.put("planks_oak", new InvItem(Block.planks,0));
		goodsName.put("planks_pine", new InvItem(Block.planks,1));
		goodsName.put("porkchops", new InvItem(Item.porkRaw,0));
		goodsName.put("porkchopscooked", new InvItem(Item.porkCooked,0));
		goodsName.put("pumpkin", new InvItem(Block.pumpkin,0));
		goodsName.put("pumpkinseeds", new InvItem(Item.pumpkinSeeds,0));
		goodsName.put("redflower", new InvItem(Block.plantRed,0));
		goodsName.put("redstone", new InvItem(Item.redstone,0));
		goodsName.put("rottenflesh", new InvItem(Item.rottenFlesh,0));
		goodsName.put("saddle", new InvItem(Item.saddle,0));
		goodsName.put("sand", new InvItem(Block.sand,0));
		goodsName.put("sandstone", new InvItem(Block.sandStone,0));
		goodsName.put("sapling", new InvItem(Block.sapling,0));
		goodsName.put("sapling_birch", new InvItem(Block.sapling,2));
		goodsName.put("sapling_jungle", new InvItem(Block.sapling,3));
		goodsName.put("sapling_pine", new InvItem(Block.sapling,1));
		goodsName.put("seeds", new InvItem(Item.seeds,0));
		goodsName.put("shears", new InvItem(Item.shears,0));
		goodsName.put("slimeball", new InvItem(Item.slimeBall,0));
		goodsName.put("snow", new InvItem(Block.snow,0));
		goodsName.put("spidereye", new InvItem(Item.spiderEye,0));
		goodsName.put("steelaxe", new InvItem(Item.axeSteel,0));
		goodsName.put("steelboots", new InvItem(Item.bootsSteel,0));
		goodsName.put("steelchest", new InvItem(Item.plateSteel,0));
		goodsName.put("steelhelmet", new InvItem(Item.helmetSteel,0));
		goodsName.put("steelhoe", new InvItem(Item.hoeSteel,0));
		goodsName.put("steellegs", new InvItem(Item.legsSteel,0));
		goodsName.put("steelpickaxe", new InvItem(Item.pickaxeSteel,0));
		goodsName.put("steelshovel", new InvItem(Item.shovelSteel,0));
		goodsName.put("steelsword", new InvItem(Item.swordSteel,0));
		goodsName.put("stone", new InvItem(Block.stone,0));
		goodsName.put("stoneaxe", new InvItem(Item.axeStone,0));
		goodsName.put("stonehoe", new InvItem(Item.hoeStone,0));
		goodsName.put("stonepickaxe", new InvItem(Item.pickaxeStone,0));
		goodsName.put("stoneshovel", new InvItem(Item.shovelStone,0));
		goodsName.put("stonesword", new InvItem(Item.swordStone,0));
		goodsName.put("string", new InvItem(Item.silk,0));
		goodsName.put("sugarcane", new InvItem(Item.reed,0));
		goodsName.put("carrot", new InvItem(Item.carrot,0));
		goodsName.put("potato", new InvItem(Item.potato,0));


		goodsName.put("alchemistamulet", new InvItem(Mill.alchemist_amulet,0));
		goodsName.put("alchimistexplosive", new InvItem(Mill.stone_decoration,3));
		goodsName.put("boudin", new InvItem(Mill.boudin,0));
		goodsName.put("brickmould", new InvItem(Mill.brickmould,0));
		goodsName.put("calva", new InvItem(Mill.calva,0));
		goodsName.put("chickencurry", new InvItem(Mill.chickencurry,0));
		goodsName.put("cider", new InvItem(Mill.cider,0));
		goodsName.put("ciderapple", new InvItem(Mill.ciderapple,0));
		goodsName.put("cookedbrick", new InvItem(Mill.stone_decoration,0));
		goodsName.put("denier", new InvItem(Mill.denier,0));
		goodsName.put("denierargent", new InvItem(Mill.denier_argent,0));
		goodsName.put("denieror", new InvItem(Mill.denier_or,0));
		goodsName.put("dirtwall", new InvItem(Mill.earth_decoration,1));
		goodsName.put("indianstatue", new InvItem(Mill.indianstatue,0));
		goodsName.put("japaneseguardboots", new InvItem(Mill.japaneseGuardBoots,0));
		goodsName.put("japaneseguardhelmet", new InvItem(Mill.japaneseGuardHelmet,0));
		goodsName.put("japaneseguardlegs", new InvItem(Mill.japaneseGuardLegs,0));
		goodsName.put("japaneseguardplate", new InvItem(Mill.japaneseGuardPlate,0));
		goodsName.put("japanesewarriorblueboots", new InvItem(Mill.japaneseWarriorBlueBoots,0));
		goodsName.put("japanesewarriorbluehelmet", new InvItem(Mill.japaneseWarriorBlueHelmet,0));
		goodsName.put("japanesewarriorbluelegs", new InvItem(Mill.japaneseWarriorBlueLegs,0));
		goodsName.put("japanesewarriorblueplate", new InvItem(Mill.japaneseWarriorBluePlate,0));
		goodsName.put("japanesewarriorredboots", new InvItem(Mill.japaneseWarriorRedBoots,0));
		goodsName.put("japanesewarriorredhelmet", new InvItem(Mill.japaneseWarriorRedHelmet,0));
		goodsName.put("japanesewarriorredlegs", new InvItem(Mill.japaneseWarriorRedLegs,0));
		goodsName.put("japanesewarriorredplate", new InvItem(Mill.japaneseWarriorRedPlate,0));
		goodsName.put("maize", new InvItem(Mill.maize,0));
		goodsName.put("masa", new InvItem(Mill.masa,0));
		goodsName.put("mayanaxe", new InvItem(Mill.mayanAxe,0));
		goodsName.put("mayangold", new InvItem(Mill.stone_decoration,2));
		goodsName.put("mayanhoe", new InvItem(Mill.mayanHoe,0));
		goodsName.put("mayanmace", new InvItem(Mill.mayanMace,0));
		goodsName.put("mayanpickaxe", new InvItem(Mill.mayanPickaxe,0));
		goodsName.put("mayanshovel", new InvItem(Mill.mayanShovel,0));
		goodsName.put("mayanstatue", new InvItem(Mill.mayanstatue,0));
		goodsName.put("mudbrick", new InvItem(Mill.stone_decoration,1));
		goodsName.put("normanaxe", new InvItem(Mill.normanAxe,0));
		goodsName.put("normanboots", new InvItem(Mill.normanBoots,0));
		goodsName.put("normanbroadsword", new InvItem(Mill.normanBroadsword,0));
		goodsName.put("normanchest", new InvItem(Mill.normanPlate,0));
		goodsName.put("normanhelmet", new InvItem(Mill.normanHelmet,0));
		goodsName.put("normanhoe", new InvItem(Mill.normanHoe,0));
		goodsName.put("normanlegs", new InvItem(Mill.normanLegs,0));
		goodsName.put("normanpickaxe", new InvItem(Mill.normanPickaxe,0));
		goodsName.put("normanshovel", new InvItem(Mill.normanShovel,0));
		goodsName.put("obsidianflake", new InvItem(Mill.obsidianFlake,0));
		goodsName.put("paperwall", new InvItem(Mill.paperWall,0));
		goodsName.put("parchmentbuildings", new InvItem(Mill.parchmentBuildings,0));
		goodsName.put("parchmentcomplete", new InvItem(Mill.parchmentComplete,0));
		goodsName.put("parchmentindianbuildings", new InvItem(Mill.parchmentIndianBuildings,0));
		goodsName.put("parchmentindiancomplete", new InvItem(Mill.parchmentIndianComplete,0));
		goodsName.put("parchmentindianitems", new InvItem(Mill.parchmentIndianItems,0));
		goodsName.put("parchmentindianvillagers", new InvItem(Mill.parchmentIndianVillagers,0));
		goodsName.put("parchmentitems", new InvItem(Mill.parchmentItems,0));
		goodsName.put("parchmentmayanbuildings", new InvItem(Mill.parchmentMayanBuildings,0));
		goodsName.put("parchmentmayancomplete", new InvItem(Mill.parchmentMayanComplete,0));
		goodsName.put("parchmentmayanitems", new InvItem(Mill.parchmentMayanItems,0));
		goodsName.put("parchmentmayanvillagers", new InvItem(Mill.parchmentMayanVillagers,0));
		goodsName.put("parchmentjapanesebuildings", new InvItem(Mill.parchmentJapaneseBuildings,0));
		goodsName.put("parchmentjapanesecomplete", new InvItem(Mill.parchmentJapaneseComplete,0));
		goodsName.put("parchmentjapaneseitems", new InvItem(Mill.parchmentJapaneseItems,0));
		goodsName.put("parchmentjapanesevillagers", new InvItem(Mill.parchmentJapaneseVillagers,0));
		goodsName.put("parchmentsadhu", new InvItem(Mill.parchmentSadhu,0));
		goodsName.put("parchmentvillagers", new InvItem(Mill.parchmentVillagers,0));
		goodsName.put("rasgulla", new InvItem(Mill.rasgulla,0));
		goodsName.put("rice", new InvItem(Mill.rice,0));
		goodsName.put("skollhatiamulet", new InvItem(Mill.skoll_hati_amulet,0));
		goodsName.put("tachisword", new InvItem(Mill.tachiSword,0));
		goodsName.put("tapestry", new InvItem(Mill.tapestry,0));
		goodsName.put("thatched", new InvItem(Mill.wood_decoration,2));
		goodsName.put("timberframe_cross", new InvItem(Mill.wood_decoration,1));
		goodsName.put("timberframe_plain", new InvItem(Mill.wood_decoration,0));
		goodsName.put("tripes", new InvItem(Mill.tripes,0));
		goodsName.put("turmeric", new InvItem(Mill.turmeric,0));
		goodsName.put("udon", new InvItem(Mill.udon,0));
		goodsName.put("unknownpowder", new InvItem(Mill.unknownPowder,0));
		goodsName.put("vegcurry", new InvItem(Mill.vegcurry,0));
		goodsName.put("villagewand", new InvItem(Mill.summoningWand,0));
		goodsName.put("vishnuamulet", new InvItem(Mill.vishnu_amulet,0));
		goodsName.put("wah", new InvItem(Mill.wah,0));
		goodsName.put("yddrasilamulet", new InvItem(Mill.yddrasil_amulet,0));
		goodsName.put("yumibow", new InvItem(Mill.yumiBow,0));
		goodsName.put("grapes", new InvItem(Mill.grapes,0));
		goodsName.put("wine", new InvItem(Mill.wineFancy,0));
		goodsName.put("silk", new InvItem(Mill.silk,0));
		goodsName.put("byzantineiconsmall", new InvItem(Mill.byzantineiconsmall,0));
		goodsName.put("byzantineiconmedium", new InvItem(Mill.byzantineiconmedium,0));
		goodsName.put("byzantineiconlarge", new InvItem(Mill.byzantineiconlarge,0));
		goodsName.put("byzantinetiles", new InvItem(Mill.byzantine_tiles,0));
		goodsName.put("byzantinetileslab", new InvItem(Mill.byzantine_tile_slab,0));
		goodsName.put("byzantinestonetiles", new InvItem(Mill.byzantine_stone_tiles,0));
		goodsName.put("silkwormblockempty", new InvItem(Mill.wood_decoration,3));
		goodsName.put("silkwormblockfull", new InvItem(Mill.wood_decoration,4));

		goodsName.put("byzantineboots", new InvItem(Mill.byzantineBoots,0));
		goodsName.put("byzantinehelmet", new InvItem(Mill.byzantineHelmet,0));
		goodsName.put("byzantinelegs", new InvItem(Mill.byzantineLegs,0));
		goodsName.put("byzantineplate", new InvItem(Mill.byzantinePlate,0));
		goodsName.put("byzantinemace", new InvItem(Mill.byzantineMace,0));

		goodsName.put("clothes_byz_wool", new InvItem(Mill.clothes,0));
		goodsName.put("clothes_byz_silk", new InvItem(Mill.clothes,1));

		goodsName.put("winebasic", new InvItem(Mill.wineBasic,0));
		goodsName.put("lambraw", new InvItem(Mill.lambRaw,0));
		goodsName.put("lambcooked", new InvItem(Mill.lambCooked,0));
		goodsName.put("feta", new InvItem(Mill.feta,0));
		goodsName.put("souvlaki", new InvItem(Mill.souvlaki,0));

		goodsName.put("tnt", new InvItem(Block.tnt,0));
		goodsName.put("vines", new InvItem(Block.vine,0));
		goodsName.put("wheat", new InvItem(Item.wheat,0));
		goodsName.put("wood", new InvItem(Block.wood,0));
		goodsName.put("wood_any", new InvItem(Block.wood,-1));
		goodsName.put("wood_oak", new InvItem(Block.wood,0));
		goodsName.put("wood_birch", new InvItem(Block.wood,2));
		goodsName.put("wood_jungle", new InvItem(Block.wood,3));
		goodsName.put("wood_pine", new InvItem(Block.wood,1));
		goodsName.put("woodaxe", new InvItem(Item.axeWood,0));
		goodsName.put("woodhoe", new InvItem(Item.hoeWood,0));
		goodsName.put("woodpickaxe", new InvItem(Item.pickaxeWood,0));
		goodsName.put("woodshovel", new InvItem(Item.shovelWood,0));
		goodsName.put("woodsword", new InvItem(Item.swordWood,0));
		goodsName.put("wool_black", new InvItem(Block.cloth,15));
		goodsName.put("wool_blue", new InvItem(Block.cloth,11));
		goodsName.put("wool_brown", new InvItem(Block.cloth,12));
		goodsName.put("wool_cyan", new InvItem(Block.cloth,9));
		goodsName.put("wool_gray", new InvItem(Block.cloth,7));
		goodsName.put("wool_green", new InvItem(Block.cloth,13));
		goodsName.put("wool_lightblue", new InvItem(Block.cloth,3));
		goodsName.put("wool_limegreen", new InvItem(Block.cloth,5));
		goodsName.put("wool_lightgray", new InvItem(Block.cloth,8));
		goodsName.put("wool_magenta", new InvItem(Block.cloth,2));
		goodsName.put("wool_orange", new InvItem(Block.cloth,1));
		goodsName.put("wool_pink", new InvItem(Block.cloth,6));
		goodsName.put("wool_purple", new InvItem(Block.cloth,10));
		goodsName.put("wool_red", new InvItem(Block.cloth,14));
		goodsName.put("wool_white", new InvItem(Block.cloth,0));
		goodsName.put("wool_yellow", new InvItem(Block.cloth,4));
		goodsName.put("yellowflower", new InvItem(Block.plantYellow,0));
		goodsName.put("bricks", new InvItem(Block.brick,0));
		goodsName.put("stonebrick", new InvItem(Block.stoneBrick,0));
		goodsName.put("bookandquill", new InvItem(Item.writableBook,0));
		goodsName.put("purse", new InvItem(Mill.purse,0));
	}

	public InvItem item;


	public String name;

	private final int sellingPrice;

	private final int buyingPrice;

	public int reservedQuantity;

	public int targetQuantity;

	public int foreignMerchantPrice;

	public String requiredTag;

	public boolean autoGenerate=false;

	public int minReputation;

	public Goods(InvItem iv) {
		//Constructor for automated good creation
		item=iv;
		name=item.getName();
		sellingPrice=0;
		buyingPrice=1;
		requiredTag=null;
	}

	public Goods(String name,InvItem item,int sellingPrice,int buyingPrice,int reservedQuantity,int targetQuantity,int foreignMerchantPrice,boolean autoGenerate,String tag,int minReputation) {
		this.name=name;
		this.item=item;
		this.sellingPrice=sellingPrice;
		this.buyingPrice=buyingPrice;
		this.requiredTag=tag;
		this.autoGenerate=autoGenerate;
		this.reservedQuantity=reservedQuantity;
		this.targetQuantity=targetQuantity;
		this.foreignMerchantPrice=foreignMerchantPrice;
		this.minReputation=minReputation;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (!(obj instanceof Goods))
			return false;

		final Goods g=(Goods)obj;

		return (g.item.equals(obj));
	}

	public int getBuyingPrice(Building townHall) {

		if (townHall==null)
			return buyingPrice;


		if (townHall.villageType.buyingPrices.containsKey(item))
			return townHall.villageType.buyingPrices.get(item);
		else
			return buyingPrice;
	}


	public String getName() {
		return Mill.proxy.getItemName(item.id(),item.meta);
	}

	public int getSellingPrice(Building townHall) {

		if (townHall==null)
			return sellingPrice;

		if (townHall.villageType.sellingPrices.containsKey(item))
			return townHall.villageType.sellingPrices.get(item);
		else
			return sellingPrice;
	}

	@Override
	public String toString() {

		return "Goods@"+item.getItemStack().getItemName();
	}


}
