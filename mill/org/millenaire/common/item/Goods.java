package org.millenaire.common.item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.EntityMillDecoration;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingPlan;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.VillageList;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Goods {

	public static interface IItemInitialEnchantmens {

		public void applyEnchantments(ItemStack stack);

	}

	public static class ItemAmuletAlchemist extends Item {

		public final String baseIconName;

		public ItemAmuletAlchemist(final String iconName) {
			super();
			this.setCreativeTab(Mill.tabMillenaire);
			this.baseIconName = iconName;
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			Mill.proxy.declareAmuletTextures(iconRegister);
			itemIcon = iconRegister.registerIcon(Mill.modId + ":" + baseIconName + MLN.getTextSuffix());
		}
	}

	public static class ItemAmuletSkollHati extends ItemText {

		public ItemAmuletSkollHati(final String iconName) {
			super(iconName);
		}

		@Override
		public ItemStack onItemRightClick(final ItemStack itemstack, final World world, final EntityPlayer entityplayer) {

			if (MLN.LogOther >= MLN.DEBUG) {
				MLN.debug(this, "Using skoll amulet.");
			}

			if (world.isRemote) {
				return itemstack;
			}

			final long time = world.getWorldTime() + 24000L;

			if (time % 24000L > 11000L && time % 24000L < 23500L) {
				world.setWorldTime(time - time % 24000L - 500L);
			} else {
				world.setWorldTime(time - time % 24000L + 13000L);
			}

			if (!MLN.infiniteAmulet) {
				itemstack.damageItem(1, entityplayer);
			}

			return itemstack;
		}
	}

	public static class ItemAmuletVishnu extends Item {

		public final String baseIconName;

		public ItemAmuletVishnu(final String iconName) {
			super();
			this.setCreativeTab(Mill.tabMillenaire);
			this.baseIconName = iconName;
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {

			Mill.proxy.declareAmuletTextures(iconRegister);
			itemIcon = iconRegister.registerIcon(Mill.modId + ":" + baseIconName + MLN.getTextSuffix());
		}

	}

	public static class ItemAmuletYddrasil extends Item {

		public final String baseIconName;

		public ItemAmuletYddrasil(final String iconName) {
			super();
			this.setCreativeTab(Mill.tabMillenaire);
			this.baseIconName = iconName;
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			Mill.proxy.declareAmuletTextures(iconRegister);
			itemIcon = iconRegister.registerIcon(Mill.modId + ":" + baseIconName + MLN.getTextSuffix());
		}
	}

	public static class ItemBrickMould extends ItemText {

		public ItemBrickMould(final String iconName) {
			super(iconName);
			this.setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUseFirst(final ItemStack itemstack, final EntityPlayer entityplayer, final World world, int i, int j, int k, int l, final float hitX, final float hitY, final float hitZ) {
			if (world.getBlock(i, j, k) == Blocks.snow) {
				l = 0;
			} else {
				if (l == 0) {
					j--;
				}
				if (l == 1) {
					j++;
				}
				if (l == 2) {
					k--;
				}
				if (l == 3) {
					k++;
				}
				if (l == 4) {
					i--;
				}
				if (l == 5) {
					i++;
				}
			}

			if (world.getBlock(i, j, k) != Blocks.air) {
				return false;
			}

			if (MillCommonUtilities.countChestItems(entityplayer.inventory, Blocks.dirt, 0) == 0 || MillCommonUtilities.countChestItems(entityplayer.inventory, Blocks.sand, 0) == 0) {

				if (!world.isRemote) {
					ServerSender.sendTranslatedSentence(entityplayer, MLN.WHITE, "ui.brickinstructions");
				}
				return false;
			}

			MillCommonUtilities.getItemsFromChest(entityplayer.inventory, Blocks.dirt, 0, 1);
			MillCommonUtilities.getItemsFromChest(entityplayer.inventory, Blocks.sand, 0, 1);

			MillCommonUtilities.setBlockAndMetadata(world, i, j, k, Mill.earth_decoration, 0, true, false);

			itemstack.damageItem(1, entityplayer);

			return false;

		}
	}

	public static class ItemClothes extends Item {

		public final String[] iconNames;
		public IIcon[] icons = null;

		public ItemClothes(final String... iconNames) {
			super();
			this.setHasSubtypes(true);
			this.setMaxDamage(0);
			this.iconNames = iconNames;
			this.setCreativeTab(Mill.tabMillenaire);
		}

		public String getClothName(final int meta) {
			if (meta == 0) {
				return "clothes_byz_wool";
			} else {
				return "clothes_byz_silk";
			}
		}

		public int getClothPriority(final int meta) {
			if (meta == 0) {
				return 1;
			} else {
				return 2;
			}
		}

		@Override
		public IIcon getIconFromDamage(final int meta) {

			if (meta < iconNames.length) {
				return icons[meta];
			}

			return icons[0];
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@SideOnly(Side.CLIENT)
		@Override
		public void getSubItems(final Item item, final CreativeTabs par2CreativeTabs, final List par3List) {
			for (int var4 = 0; var4 < 2; ++var4) {
				par3List.add(new ItemStack(item, 1, var4));
			}
		}

		@Override
		public String getUnlocalizedName(final ItemStack par1ItemStack) {
			final int meta = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 15);

			return "item." + getClothName(meta);

		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			icons = new IIcon[iconNames.length];

			for (int i = 0; i < iconNames.length; i++) {
				icons[i] = MillCommonUtilities.getIcon(iconRegister, iconNames[i]);
			}
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconNames[0]);
		}
	}

	public static class ItemMayanQuestCrown extends ItemArmor implements IItemInitialEnchantmens {

		private static final ResourceLocation mayan1 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_mayan_quest_1.png");

		public final String iconName;

		public ItemMayanQuestCrown(final String iconName, final int armourId, final int type) {
			super(ArmorMaterial.DIAMOND, armourId, type);
			setMaxDamage(0);
			this.iconName = iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public void applyEnchantments(final ItemStack stack) {
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, stack) == 0) {
				stack.addEnchantment(Enchantment.respiration, 3);
			}
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, stack) == 0) {
				stack.addEnchantment(Enchantment.aquaAffinity, 1);
			}
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) == 0) {
				stack.addEnchantment(Enchantment.protection, 4);
			}
		}

		@Override
		public String getArmorTexture(final ItemStack par1, final Entity entity, final int slot, final String type) {
			return mayan1.toString();
		}

		@Override
		public boolean onItemUse(final ItemStack stack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8,
				final float par9, final float par10) {

			applyEnchantments(stack);

			return super.onItemUse(stack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
		}

		@Override
		public boolean onItemUseFirst(final ItemStack stack, final EntityPlayer player, final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY,
				final float hitZ) {
			applyEnchantments(stack);
			return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		}

		@Override
		public void onUpdate(final ItemStack par1ItemStack, final World par2World, final Entity par3Entity, final int par4, final boolean par5) {
			applyEnchantments(par1ItemStack);
			super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
		}

	}

	public static class ItemMillenaireArmour extends ItemArmor {

		private static final ResourceLocation norman1 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_norman_1.png");
		private static final ResourceLocation norman2 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_norman_2.png");

		private static final ResourceLocation japaneseGuard1 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_japanese_guard_1.png");
		private static final ResourceLocation japaneseGuard2 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_japanese_guard_2.png");

		private static final ResourceLocation japaneseWarriorBlue1 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_japanese_warrior_blue_1.png");
		private static final ResourceLocation japaneseWarriorBlue2 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_japanese_warrior_blue_2.png");

		private static final ResourceLocation japaneseWarriorRed1 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_japanese_warrior_red_1.png");
		private static final ResourceLocation japaneseWarriorRed2 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_japanese_warrior_red_2.png");

		private static final ResourceLocation byzantine1 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_byzantine_1.png");
		private static final ResourceLocation byzantine2 = new ResourceLocation(Mill.modId, "textures/models/armor/ML_byzantine_2.png");

		public final String iconName;

		public ItemMillenaireArmour(final String iconName, final ArmorMaterial material, final int armourId, final int type) {
			super(material, armourId, type);
			this.iconName = iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public String getArmorTexture(final ItemStack par1, final Entity entity, final int slot, final String type) {
			if (par1.getItem() == Mill.normanHelmet || par1.getItem() == Mill.normanPlate || par1.getItem() == Mill.normanBoots) {
				return norman1.toString();
			}
			if (par1.getItem() == Mill.normanLegs) {
				return norman2.toString();
			}

			if (par1.getItem() == Mill.japaneseGuardHelmet || par1.getItem() == Mill.japaneseGuardPlate || par1.getItem() == Mill.japaneseGuardBoots) {
				return japaneseGuard1.toString();
			}
			if (par1.getItem() == Mill.japaneseGuardLegs) {
				return japaneseGuard2.toString();
			}

			if (par1.getItem() == Mill.japaneseWarriorBlueHelmet || par1.getItem() == Mill.japaneseWarriorBluePlate || par1.getItem() == Mill.japaneseWarriorBlueBoots) {
				return japaneseWarriorBlue1.toString();
			}
			if (par1.getItem() == Mill.japaneseWarriorBlueLegs) {
				return japaneseWarriorBlue2.toString();
			}

			if (par1.getItem() == Mill.japaneseWarriorRedHelmet || par1.getItem() == Mill.japaneseWarriorRedPlate || par1.getItem() == Mill.japaneseWarriorRedBoots) {
				return japaneseWarriorRed1.toString();
			}
			if (par1.getItem() == Mill.japaneseWarriorRedLegs) {
				return japaneseWarriorRed2.toString();
			}

			if (par1.getItem() == Mill.byzantineHelmet || par1.getItem() == Mill.byzantinePlate || par1.getItem() == Mill.byzantineBoots) {
				return byzantine1.toString();
			}
			if (par1.getItem() == Mill.byzantineLegs) {
				return byzantine2.toString();
			}

			return norman1.toString();
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireAxe extends ItemAxe {

		public final String iconName;

		public ItemMillenaireAxe(final String iconName, final ToolMaterial material) {
			super(material);
			this.iconName = iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireBow extends ItemBow {

		public float speedFactor = 1;
		public float damageBonus = 0;
		public int iconPos;
		public final String[] iconNames;
		public IIcon[] icons;

		public ItemMillenaireBow(final float speedFactor, final float damageBonus, final String... iconNames) {
			super();
			this.speedFactor = speedFactor;
			this.damageBonus = damageBonus;
			this.iconNames = iconNames;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public IIcon getIconFromDamage(final int par1) {
			return icons[iconPos];
		}

		/**
		 * called when the player releases the use item button. Args: itemstack,
		 * world, entityplayer, itemInUseCount
		 * 
		 * Taken from ItemBow, MC 1.4.2
		 */

		@Override
		public void onPlayerStoppedUsing(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer, final int par4) {
			int var6 = this.getMaxItemUseDuration(par1ItemStack) - par4;

			final ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, var6);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) {
				return;
			}
			var6 = event.charge;

			final boolean var5 = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;

			if (var5 || par3EntityPlayer.inventory.hasItem(Items.arrow)) {
				float var7 = var6 / 20.0F;
				var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;

				if (var7 < 0.1D) {
					return;
				}

				if (var7 > 1.0F) {
					var7 = 1.0F;
				}

				final EntityArrow var8 = new EntityArrow(par2World, par3EntityPlayer, var7 * 2.0F);

				if (var7 == 1.0F) {
					var8.setIsCritical(true);
				}

				final int var9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, par1ItemStack);

				if (var9 > 0) {
					var8.setDamage(var8.getDamage() + var9 * 0.5D + 0.5D);
				}

				final int var10 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, par1ItemStack);

				if (var10 > 0) {
					var8.setKnockbackStrength(var10);
				}

				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, par1ItemStack) > 0) {
					var8.setFire(100);
				}

				par1ItemStack.damageItem(1, par3EntityPlayer);
				par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);

				if (var5) {
					var8.canBePickedUp = 2;
				} else {
					par3EntityPlayer.inventory.consumeInventoryItem(Items.arrow);
				}

				// faster MLN arrows
				var8.motionX *= speedFactor;
				var8.motionY *= speedFactor;
				var8.motionZ *= speedFactor;

				// extra arrow damage
				var8.setDamage(var8.getDamage() + damageBonus);

				if (!par2World.isRemote) {
					par2World.spawnEntityInWorld(var8);
				}
			}
		}

		@Override
		public void onUpdate(final ItemStack itemstack, final World world, final Entity entity, final int i, final boolean flag) {
			final EntityPlayer entityplayer = (EntityPlayer) entity;
			Mill.proxy.updateBowIcon(this, entityplayer);
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconNames[0]);

			icons = new IIcon[iconNames.length];

			for (int i = 0; i < iconNames.length; i++) {
				icons[i] = MillCommonUtilities.getIcon(iconRegister, iconNames[i]);
			}
		}

		public void setBowIcon(final int pos) {
			iconPos = pos;
		}
	}

	public static class ItemMillenaireHoe extends ItemHoe {

		public final String iconName;

		public ItemMillenaireHoe(final String iconName, final ToolMaterial material) {
			super(material);
			setCreativeTab(Mill.tabMillenaire);
			this.iconName = iconName;
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenairePickaxe extends ItemPickaxe {

		public final String iconName;

		public ItemMillenairePickaxe(final String iconName, final ToolMaterial material) {
			super(material);
			this.iconName = iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireShovel extends ItemSpade {

		public final String iconName;

		public ItemMillenaireShovel(final String iconName, final ToolMaterial material) {
			super(material);

			this.iconName = iconName;

			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
		}
	}

	public static class ItemMillenaireSword extends ItemSword implements IItemInitialEnchantmens {

		float criticalChance;
		int criticalMultiple;

		public final String iconName;

		boolean knockback;

		public ItemMillenaireSword(final String iconName, final ToolMaterial material, final float criticalChance, final int criticalMultiple, final boolean knockback) {
			super(material);
			this.criticalChance = criticalChance;
			this.criticalMultiple = criticalMultiple;
			this.knockback = knockback;
			this.iconName = iconName;
			setCreativeTab(Mill.tabMillenaire);
		}

		// @SuppressWarnings("deprecation")
		// @Override
		// public float getDamageVsEntity(Entity entity, ItemStack itemStack) {
		//
		// if (MillCommonUtilities.probability(criticalChance)) {
		// ServerSender.sendTranslatedSentenceInRange(entity.worldObj, new
		// Point(entity), 10,MLN.DARKRED,
		// "weapon.criticalstrike",""+criticalMultiple);
		// return super.getDamageVsEntity(entity, itemStack)*criticalMultiple;
		// }
		//
		// return super.getDamageVsEntity(entity, itemStack);
		// }

		@Override
		public void applyEnchantments(final ItemStack stack) {
			if (knockback) {
				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack) == 0) {
					stack.addEnchantment(Enchantment.knockback, 2);
				}
			}
		}

		@Override
		public void onCreated(final ItemStack stack, final World par2World, final EntityPlayer par3EntityPlayer) {

			applyEnchantments(stack);
		}

		@Override
		public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7,
				final float par8, final float par9, final float par10) {

			applyEnchantments(par1ItemStack);
			return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
		}

		@Override
		public boolean onItemUseFirst(final ItemStack stack, final EntityPlayer player, final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY,
				final float hitZ) {

			applyEnchantments(stack);
			return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		}

		@Override
		public boolean onLeftClickEntity(final ItemStack stack, final EntityPlayer player, final Entity entity) {
			applyEnchantments(stack);
			return super.onLeftClickEntity(stack, player, entity);
		}

		@Override
		public void registerIcons(final IIconRegister iconRegister) {
			itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
		}

	}

	public static class ItemNegationWand extends ItemText {

		public ItemNegationWand(final String iconName) {
			super(iconName);
			setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUseFirst(final ItemStack itemstack, final EntityPlayer entityplayer, final World world, final int x, final int y, final int z, final int l, final float hitX,
				final float hitY, final float hitZ) {

			final Point pos = new Point(x, y, z);

			final Block block = world.getBlock(x, y, z);

			if (block == Blocks.standing_sign && world.isRemote) {
				BuildingPlan.exportBuilding(entityplayer, world, pos);
				return true;
			}

			if (world.isRemote) {
				return false;
			}

			final MillWorld mw = Mill.getMillWorld(world);

			for (int i = 0; i < 2; i++) {

				VillageList list;

				if (i == 0) {
					list = mw.loneBuildingsList;
				} else {
					list = mw.villagesList;
				}

				for (int j = 0; j < list.names.size(); j++) {

					final Point p = list.pos.get(j);

					final int distance = MathHelper.floor_double(p.horizontalDistanceTo(pos));

					if (distance <= 30) {

						final Building th = mw.getBuilding(p);

						if (th != null && th.isTownhall) {
							if (th.chestLocked) {
								ServerSender.sendTranslatedSentence(entityplayer, MLN.ORANGE, "negationwand.villagelocked", th.villageType.name);
								return true;
							}
							ServerSender.displayNegationWandGUI(entityplayer, th);

						}
					}
				}
			}

			return false;

		}

	}

	public static class ItemSummoningWand extends ItemText {

		public ItemSummoningWand(final String iconName) {
			super(iconName);
			this.setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUseFirst(final ItemStack itemstack, final EntityPlayer entityplayer, final World world, final int i, final int j, final int k, final int l, final float hitX,
				final float hitY, final float hitZ) {// client-side

			final Point pos = new Point(i, j, k);

			final Block block = world.getBlock(i, j, k);

			if (block == Blocks.standing_sign) {
				ClientSender.importBuilding(entityplayer, pos);
				return true;
			} else if (block == Mill.lockedChest) {
				return false;
			}

			ClientSender.summoningWandUse(entityplayer, pos);

			return true;

		}
	}

	public static class ItemTapestry extends ItemText {

		public int type;

		public ItemTapestry(final String iconName, final int type) {
			super(iconName);
			this.type = type;
			this.setCreativeTab(Mill.tabMillenaire);
		}

		@Override
		public boolean onItemUse(final ItemStack itemstack, final EntityPlayer entityplayer, final World world, final int i, final int j, final int k, final int side, final float par8,
				final float par9, final float par10) {
			if (side == 0) {
				return false;
			}
			if (side == 1) {
				return false;
			}

			final int orientation = Direction.facingToDirection[side];

			final EntityMillDecoration entitypainting = new EntityMillDecoration(world, i, j, k, orientation, type, false);
			if (entitypainting.onValidSurface()) {
				if (!world.isRemote) {
					world.spawnEntityInWorld(entitypainting);
				}
				itemstack.stackSize--;
			}
			return true;
		}
	}

	public static class ItemText extends Item {

		public final String iconName;

		public ItemText(final String iconName) {
			super();
			this.setCreativeTab(Mill.tabMillenaire);
			this.iconName = iconName;
			this.setTextureName(Mill.modId + ":" + iconName);
		}
	}

	public static final List<InvItem> freeGoods = new ArrayList<InvItem>();

	public static final HashMap<String, InvItem> goodsName = new HashMap<String, InvItem>();

	public static final String BOUDIN = "boudin";
	public static final String TRIPES = "tripes";
	public static final String CALVA = "calva";
	static {
		freeGoods.add(new InvItem(Blocks.dirt, 0));
		freeGoods.add(new InvItem(Mill.earth_decoration, 1));
		freeGoods.add(new InvItem(Blocks.water, 0));
		freeGoods.add(new InvItem(Blocks.sapling, 0));
		freeGoods.add(new InvItem(Blocks.yellow_flower, 0));
		freeGoods.add(new InvItem(Blocks.red_flower, 0));
		freeGoods.add(new InvItem(Blocks.tallgrass, 0));
		freeGoods.add(new InvItem(Blocks.clay, 0));
		freeGoods.add(new InvItem(Blocks.brewing_stand, 0));
		freeGoods.add(new InvItem(Blocks.leaves, -1));
		freeGoods.add(new InvItem(Blocks.sapling, -1));
		freeGoods.add(new InvItem(Blocks.cake, 0));
		freeGoods.add(new InvItem(Mill.path, -1));
		freeGoods.add(new InvItem(Mill.pathSlab, -1));
	}

	public static void generateGoodsList() {

		final File file = new File(Mill.proxy.getBaseDir(), "goods.txt");

		try {
			final BufferedWriter writer = MillCommonUtilities.getWriter(file);
			writer.write("//Item key;item id;item meta;label (indicative only)" + MLN.EOL);
			writer.write("//This file is auto-generated and indicative only. Don't edit it." + MLN.EOL + MLN.EOL);

			final List<String> names = new ArrayList<String>(goodsName.keySet());
			Collections.sort(names);

			for (final String name : names) {
				final InvItem iv = goodsName.get(name);
				writer.write(name + ";" + Item.itemRegistry.getNameForObject(iv.item) + ";" + iv.meta + ";" + iv.getName() + MLN.EOL);
			}
			writer.close();

		} catch (final Exception e) {
			MLN.error(null, "Error when writing goods list: ");
			MLN.printException(e);
		}
	}

	private static void loadGoodList(final File file) {

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0 && !line.startsWith("//")) {
					final String[] temp = line.trim().split(";");

					if (temp.length > 2) {
						final Item item = (Item) Item.itemRegistry.getObject(temp[1]);

						if (item != null) {
							goodsName.put(temp[0], new InvItem(item, Integer.parseInt(temp[2])));
						} else {
							final Block block = (Block) Block.blockRegistry.getObject(temp[1]);

							if (block == null) {
								MLN.error(null, "Could not load good: " + temp[1]);
							} else {
								if (Item.getItemFromBlock(block) == null) {
									MLN.error(null, "Tried to create good from block with no item: " + line);
								}
								goodsName.put(temp[0], new InvItem(block, Integer.parseInt(temp[2])));
							}
						}
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

			final File mainList = new File(loadDir, "itemlist.txt");

			if (mainList.exists()) {
				loadGoodList(mainList);
			}
		}

		generateGoodsList();

		goodsName.put("anyenchanted", new InvItem(InvItem.ANYENCHANTED));
		goodsName.put("enchantedsword", new InvItem(InvItem.ENCHANTEDSWORD));

	}

	private static void loadStaticGoods() {
		//
		// goodsName.put("akwardpotion", new InvItem(Items.potionitem,16));
		// goodsName.put("arrow", new InvItem(Items.arrow,0));
		// goodsName.put("beefcooked", new InvItem(Item.beefCooked,0));
		// goodsName.put("beefraw", new InvItem(Items.beef,0));
		// goodsName.put("bone", new InvItem(Items.bone,0));
		// goodsName.put("book", new InvItem(Item.book,0));
		// goodsName.put("bookshelves", new InvItem(Blocks.bookshelf,0));
		// goodsName.put("bottle", new InvItem(Items.glass_bottle,0));
		// goodsName.put("bow", new InvItem(Items.bow,0));
		// goodsName.put("bowlempty", new InvItem(Items.bowlEmpty,0));
		// goodsName.put("bowlsoup", new InvItem(Items.bowlSoup,0));
		// goodsName.put("bread", new InvItem(Item.bread,0));
		// goodsName.put("bucketempty", new InvItem(Item.bucketEmpty,0));
		// goodsName.put("bucketlava", new InvItem(Item.bucketLava,0));
		// goodsName.put("bucketmilk", new InvItem(Items.milk_bucket,0));
		// goodsName.put("bucketwater", new InvItem(Item.bucketWater,0));
		// goodsName.put("cactus", new InvItem(Blocks.cactus,0));
		// goodsName.put("cake", new InvItem(Blocks.cake,0));
		// goodsName.put("chickenmeat", new InvItem(Items.chicken,0));
		// goodsName.put("chickenmeatcooked", new
		// InvItem(Item.chickenCooked,0));
		// goodsName.put("clay", new InvItem(Item.clay,0));
		// goodsName.put("clock", new InvItem(Items.clock,0));
		// goodsName.put("coal", new InvItem(Items.coal,0));
		// goodsName.put("cobblestone", new InvItem(Blocks.cobblestone,0));
		// goodsName.put("compass", new InvItem(Item.compass,0));
		// goodsName.put("cookie", new InvItem(Item.cookie,0));
		// goodsName.put("diamond", new InvItem(Items.diamond,0));
		// goodsName.put("diamondaxe", new InvItem(Item.axeDiamond,0));
		// goodsName.put("diamondboots", new InvItem(Item.bootsDiamond,0));
		// goodsName.put("diamondchest", new InvItem(Item.plateDiamond,0));
		// goodsName.put("diamondhelmet", new InvItem(Item.helmetDiamond,0));
		// goodsName.put("diamondhoe", new InvItem(Item.hoeDiamond,0));
		// goodsName.put("diamondlegs", new InvItem(Item.legsDiamond,0));
		// goodsName.put("diamondpickaxe", new InvItem(Item.pickaxeDiamond,0));
		// goodsName.put("diamondshovel", new InvItem(Item.shovelDiamond,0));
		// goodsName.put("diamondsword", new InvItem(Item.swordDiamond,0));
		// goodsName.put("dirt", new InvItem(Blocks.dirt,0));
		// goodsName.put("dye_black", new InvItem(Items.dye,0));
		// goodsName.put("dye_blue", new InvItem(Items.dye,4));
		// goodsName.put("dye_brown", new InvItem(Items.dye,3));
		// goodsName.put("dye_cyan", new InvItem(Items.dye,6));
		// goodsName.put("dye_gray", new InvItem(Items.dye,8));
		// goodsName.put("dye_green", new InvItem(Items.dye,2));
		// goodsName.put("dye_lightblue", new InvItem(Items.dye,12));
		// goodsName.put("dye_lightgray", new InvItem(Items.dye,7));
		// goodsName.put("dye_magenta", new InvItem(Items.dye,13));
		// goodsName.put("dye_orange", new InvItem(Items.dye,14));
		// goodsName.put("dye_pink", new InvItem(Items.dye,9));
		// goodsName.put("dye_purple", new InvItem(Items.dye,5));
		// goodsName.put("dye_red", new InvItem(Items.dye,1));
		// goodsName.put("dye_white", new InvItem(Items.dye,15));
		// goodsName.put("dye_yellow", new InvItem(Items.dye,11));
		// goodsName.put("dye_lime", new InvItem(Items.dye,10));
		// goodsName.put("egg", new InvItem(Items.egg,0));
		// goodsName.put("enderpearl", new InvItem(Items.ender_pearl,0));
		// goodsName.put("feather", new InvItem(Item.feather,0));
		// goodsName.put("fishcooked", new InvItem(Items.cooked_fished,0));
		// goodsName.put("fishraw", new InvItem(Items.fish,0));
		// goodsName.put("flint", new InvItem(Item.flint,0));
		// goodsName.put("flintandsteel", new InvItem(Item.flintAndSteel,0));
		// goodsName.put("glass", new InvItem(Blocks.glass,0));
		// goodsName.put("gold", new InvItem(Items.gold_ingot,0));
		// goodsName.put("goldore", new InvItem(Blocks.gold_ore,0));
		// goodsName.put("gunpowder", new InvItem(Items.gunpowder,0));
		// goodsName.put("iron", new InvItem(Items.iron_ingot,0));
		// goodsName.put("ironore", new InvItem(Blocks.iron_ore,0));
		// goodsName.put("leather", new InvItem(Items.leather,0));
		// goodsName.put("leatherboots", new InvItem(Item.bootsLeather,0));
		// goodsName.put("leatherchest", new InvItem(Item.plateLeather,0));
		// goodsName.put("leatherhelmet", new InvItem(Item.helmetLeather,0));
		// goodsName.put("leatherlegs", new InvItem(Item.legsLeather,0));
		// goodsName.put("melonseeds", new InvItem(Items.melonSeeds,0));
		// goodsName.put("melonslice", new InvItem(Items.melon,0));
		// goodsName.put("mushroombrown", new InvItem(Blocks.brown_mushroom,0));
		// goodsName.put("mushroomred", new InvItem(BBlocks.red_mushroom,0));
		// goodsName.put("netherwart", new InvItem(Items.nether_wart,0));
		// goodsName.put("obsidian", new InvItem(Block.obsidian,0));
		// goodsName.put("painting", new InvItem(Item.painting,0));
		// goodsName.put("paper", new InvItem(Item.paper,0));
		// goodsName.put("planks_birch", new InvItem(Blocks.planks,2));
		// goodsName.put("planks_jungle", new InvItem(Blocks.planks,3));
		// goodsName.put("planks_oak", new InvItem(Blocks.planks,0));
		// goodsName.put("planks_pine", new InvItem(Blocks.planks,1));
		// goodsName.put("porkchops", new InvItem(Items.porkchop,0));
		// goodsName.put("porkchopscooked", new InvItem(Item.porkCooked,0));
		// goodsName.put("pumpkin", new InvItem(Block.pumpkin,0));
		// goodsName.put("pumpkinseeds", new InvItem(Item.pumpkinSeeds,0));
		// goodsName.put("redflower", new InvItem(Blocks.red_flower,0));
		// goodsName.put("redstone", new InvItem(Item.redstone,0));
		// goodsName.put("rottenflesh", new InvItem(Items.rotten_flesh,0));
		// goodsName.put("saddle", new InvItem(Item.saddle,0));
		// goodsName.put("sand", new InvItem(Blocks.sand,0));
		// goodsName.put("sandstone", new InvItem(Blocks.sandstone,0));
		// goodsName.put("sapling", new InvItem(Blocks.sapling,0));
		// goodsName.put("sapling_birch", new InvItem(Blocks.sapling,2));
		// goodsName.put("sapling_jungle", new InvItem(Blocks.sapling,3));
		// goodsName.put("sapling_pine", new InvItem(Blocks.sapling,1));
		// goodsName.put("seeds", new InvItem(Item.seeds,0));
		// goodsName.put("shears", new InvItem(Items.shears,0));
		// goodsName.put("slimeball", new InvItem(Items.slime_ball,0));
		// goodsName.put("snow", new InvItem(Blocks.snow,0));
		// goodsName.put("spidereye", new InvItem(Items.spider_eye,0));
		// goodsName.put("steelaxe", new InvItem(Item.axeIron,0));
		// goodsName.put("steelboots", new InvItem(Item.bootsIron,0));
		// goodsName.put("steelchest", new InvItem(Item.plateIron,0));
		// goodsName.put("steelhelmet", new InvItem(Item.helmetIron,0));
		// goodsName.put("steelhoe", new InvItem(Item.hoeIron,0));
		// goodsName.put("steellegs", new InvItem(Item.legsIron,0));
		// goodsName.put("steelpickaxe", new InvItem(Item.pickaxeIron,0));
		// goodsName.put("steelshovel", new InvItem(Item.shovelIron,0));
		// goodsName.put("steelsword", new InvItem(Item.swordIron,0));
		//
		// goodsName.put("goldaxe", new InvItem(Item.axeGold,0));
		// goodsName.put("goldboots", new InvItem(Item.bootsGold,0));
		// goodsName.put("goldchest", new InvItem(Item.plateGold,0));
		// goodsName.put("goldhelmet", new InvItem(Item.helmetGold,0));
		// goodsName.put("goldhoe", new InvItem(Item.hoeGold,0));
		// goodsName.put("goldlegs", new InvItem(Item.legsGold,0));
		// goodsName.put("goldpickaxe", new InvItem(Item.pickaxeGold,0));
		// goodsName.put("goldshovel", new InvItem(Item.shovelGold,0));
		// goodsName.put("goldsword", new InvItem(Item.swordGold,0));
		//
		// goodsName.put("chainboots", new InvItem(Item.bootsChain,0));
		// goodsName.put("chainchest", new InvItem(Item.plateChain,0));
		// goodsName.put("chainhelmet", new InvItem(Item.helmetChain,0));
		// goodsName.put("chainlegs", new InvItem(Item.legsChain,0));
		//
		// goodsName.put("stone", new InvItem(Blocks.stone,0));
		// goodsName.put("stoneaxe", new InvItem(Item.axeStone,0));
		// goodsName.put("stonehoe", new InvItem(Item.hoeStone,0));
		// goodsName.put("stonepickaxe", new InvItem(Item.pickaxeStone,0));
		// goodsName.put("stoneshovel", new InvItem(Item.shovelStone,0));
		// goodsName.put("stonesword", new InvItem(Item.swordStone,0));
		// goodsName.put("string", new InvItem(Item.silk,0));
		// goodsName.put("sugarcane", new InvItem(Items.reeds,0));
		// goodsName.put("carrot", new InvItem(Item.carrot,0));
		// goodsName.put("potato", new InvItem(Item.potato,0));
		// goodsName.put("ghasttear", new InvItem(Items.ghast_tear,0));
		//
		//
		// goodsName.put("alchemistamulet", new
		// InvItem(Mill.alchemist_amulet,0));
		// goodsName.put("alchimistexplosive", new
		// InvItem(Mill.stone_decoration,3));
		// goodsName.put("boudin", new InvItem(Mill.boudin,0));
		// goodsName.put("brickmould", new InvItem(Mill.brickmould,0));
		// goodsName.put("calva", new InvItem(Mill.calva,0));
		// goodsName.put("chickencurry", new InvItem(Mill.chickencurry,0));
		// goodsName.put("cider", new InvItem(Mill.cider,0));
		// goodsName.put("ciderapple", new InvItem(Mill.ciderapple,0));
		// goodsName.put("cookedbrick", new InvItem(Mill.stone_decoration,0));
		// goodsName.put("denier", new InvItem(Mill.denier,0));
		// goodsName.put("denierargent", new InvItem(Mill.denier_argent,0));
		// goodsName.put("denieror", new InvItem(Mill.denier_or,0));
		// goodsName.put("dirtwall", new InvItem(Mill.earth_decoration,1));
		// goodsName.put("indianstatue", new InvItem(Mill.indianstatue,0));
		// goodsName.put("japaneseguardboots", new
		// InvItem(Mill.japaneseGuardBoots,0));
		// goodsName.put("japaneseguardhelmet", new
		// InvItem(Mill.japaneseGuardHelmet,0));
		// goodsName.put("japaneseguardlegs", new
		// InvItem(Mill.japaneseGuardLegs,0));
		// goodsName.put("japaneseguardplate", new
		// InvItem(Mill.japaneseGuardPlate,0));
		// goodsName.put("japanesewarriorblueboots", new
		// InvItem(Mill.japaneseWarriorBlueBoots,0));
		// goodsName.put("japanesewarriorbluehelmet", new
		// InvItem(Mill.japaneseWarriorBlueHelmet,0));
		// goodsName.put("japanesewarriorbluelegs", new
		// InvItem(Mill.japaneseWarriorBlueLegs,0));
		// goodsName.put("japanesewarriorblueplate", new
		// InvItem(Mill.japaneseWarriorBluePlate,0));
		// goodsName.put("japanesewarriorredboots", new
		// InvItem(Mill.japaneseWarriorRedBoots,0));
		// goodsName.put("japanesewarriorredhelmet", new
		// InvItem(Mill.japaneseWarriorRedHelmet,0));
		// goodsName.put("japanesewarriorredlegs", new
		// InvItem(Mill.japaneseWarriorRedLegs,0));
		// goodsName.put("japanesewarriorredplate", new
		// InvItem(Mill.japaneseWarriorRedPlate,0));
		// goodsName.put("maize", new InvItem(Mill.maize,0));
		// goodsName.put("masa", new InvItem(Mill.masa,0));
		// goodsName.put("mayanaxe", new InvItem(Mill.mayanAxe,0));
		// goodsName.put("mayangold", new InvItem(Mill.stone_decoration,2));
		// goodsName.put("mayanhoe", new InvItem(Mill.mayanHoe,0));
		// goodsName.put("mayanmace", new InvItem(Mill.mayanMace,0));
		// goodsName.put("mayanpickaxe", new InvItem(Mill.mayanPickaxe,0));
		// goodsName.put("mayanshovel", new InvItem(Mill.mayanShovel,0));
		// goodsName.put("mayanstatue", new InvItem(Mill.mayanstatue,0));
		// goodsName.put("mudbrick", new InvItem(Mill.stone_decoration,1));
		// goodsName.put("normanaxe", new InvItem(Mill.normanAxe,0));
		// goodsName.put("normanboots", new InvItem(Mill.normanBoots,0));
		// goodsName.put("normanbroadsword", new
		// InvItem(Mill.normanBroadsword,0));
		// goodsName.put("normanchest", new InvItem(Mill.normanPlate,0));
		// goodsName.put("normanhelmet", new InvItem(Mill.normanHelmet,0));
		// goodsName.put("normanhoe", new InvItem(Mill.normanHoe,0));
		// goodsName.put("normanlegs", new InvItem(Mill.normanLegs,0));
		// goodsName.put("normanpickaxe", new InvItem(Mill.normanPickaxe,0));
		// goodsName.put("normanshovel", new InvItem(Mill.normanShovel,0));
		// goodsName.put("obsidianflake", new InvItem(Mill.obsidianFlake,0));
		// goodsName.put("paperwall", new InvItem(Mill.paperWall,0));
		// goodsName.put("parchmentbuildings", new
		// InvItem(Mill.parchmentBuildings,0));
		// goodsName.put("parchmentcomplete", new
		// InvItem(Mill.parchmentComplete,0));
		// goodsName.put("parchmentindianbuildings", new
		// InvItem(Mill.parchmentIndianBuildings,0));
		// goodsName.put("parchmentindiancomplete", new
		// InvItem(Mill.parchmentIndianComplete,0));
		// goodsName.put("parchmentindianitems", new
		// InvItem(Mill.parchmentIndianItems,0));
		// goodsName.put("parchmentindianvillagers", new
		// InvItem(Mill.parchmentIndianVillagers,0));
		// goodsName.put("parchmentitems", new InvItem(Mill.parchmentItems,0));
		// goodsName.put("parchmentmayanbuildings", new
		// InvItem(Mill.parchmentMayanBuildings,0));
		// goodsName.put("parchmentmayancomplete", new
		// InvItem(Mill.parchmentMayanComplete,0));
		// goodsName.put("parchmentmayanitems", new
		// InvItem(Mill.parchmentMayanItems,0));
		// goodsName.put("parchmentmayanvillagers", new
		// InvItem(Mill.parchmentMayanVillagers,0));
		// goodsName.put("parchmentjapanesebuildings", new
		// InvItem(Mill.parchmentJapaneseBuildings,0));
		// goodsName.put("parchmentjapanesecomplete", new
		// InvItem(Mill.parchmentJapaneseComplete,0));
		// goodsName.put("parchmentjapaneseitems", new
		// InvItem(Mill.parchmentJapaneseItems,0));
		// goodsName.put("parchmentjapanesevillagers", new
		// InvItem(Mill.parchmentJapaneseVillagers,0));
		// goodsName.put("parchmentsadhu", new InvItem(Mill.parchmentSadhu,0));
		// goodsName.put("parchmentvillagers", new
		// InvItem(Mill.parchmentVillagers,0));
		// goodsName.put("rasgulla", new InvItem(Mill.rasgulla,0));
		// goodsName.put("rice", new InvItem(Mill.rice,0));
		// goodsName.put("skollhatiamulet", new
		// InvItem(Mill.skoll_hati_amulet,0));
		// goodsName.put("tachisword", new InvItem(Mill.tachiSword,0));
		// goodsName.put("tapestry", new InvItem(Mill.tapestry,0));
		// goodsName.put("thatched", new InvItem(Mill.wood_decoration,2));
		// goodsName.put("timberframe_cross", new
		// InvItem(Mill.wood_decoration,1));
		// goodsName.put("timberframe_plain", new
		// InvItem(Mill.wood_decoration,0));
		// goodsName.put("tripes", new InvItem(Mill.tripes,0));
		// goodsName.put("turmeric", new InvItem(Mill.turmeric,0));
		// goodsName.put("udon", new InvItem(Mill.udon,0));
		// goodsName.put("unknownpowder", new InvItem(Mill.unknownPowder,0));
		// goodsName.put("vegcurry", new InvItem(Mill.vegcurry,0));
		// goodsName.put("villagewand", new InvItem(Mill.summoningWand,0));
		// goodsName.put("vishnuamulet", new InvItem(Mill.vishnu_amulet,0));
		// goodsName.put("wah", new InvItem(Mill.wah,0));
		// goodsName.put("cacauhaa", new InvItem(Mill.cacauhaa,0));
		// goodsName.put("mayanquestcrown", new
		// InvItem(Mill.mayanQuestCrown,0));
		//
		// goodsName.put("yddrasilamulet", new InvItem(Mill.yddrasil_amulet,0));
		// goodsName.put("yumibow", new InvItem(Mill.yumiBow,0));
		// goodsName.put("sake", new InvItem(Mill.sake,0));
		// goodsName.put("grapes", new InvItem(Mill.grapes,0));
		// goodsName.put("wine", new InvItem(Mill.wineFancy,0));
		// goodsName.put("silk", new InvItem(Mill.silk,0));
		// goodsName.put("byzantineiconsmall", new
		// InvItem(Mill.byzantineiconsmall,0));
		// goodsName.put("byzantineiconmedium", new
		// InvItem(Mill.byzantineiconmedium,0));
		// goodsName.put("byzantineiconlarge", new
		// InvItem(Mill.byzantineiconlarge,0));
		// goodsName.put("byzantinetiles", new InvItem(Mill.byzantine_tiles,0));
		// goodsName.put("byzantinetileslab", new
		// InvItem(Mill.byzantine_tile_slab,0));
		// goodsName.put("byzantinestonetiles", new
		// InvItem(Mill.byzantine_stone_tiles,0));
		// goodsName.put("silkwormblockempty", new
		// InvItem(Mill.wood_decoration,3));
		// goodsName.put("silkwormblockfull", new
		// InvItem(Mill.wood_decoration,4));
		//
		// goodsName.put("byzantineboots", new InvItem(Mill.byzantineBoots,0));
		// goodsName.put("byzantinehelmet", new
		// InvItem(Mill.byzantineHelmet,0));
		// goodsName.put("byzantinelegs", new InvItem(Mill.byzantineLegs,0));
		// goodsName.put("byzantineplate", new InvItem(Mill.byzantinePlate,0));
		// goodsName.put("byzantinemace", new InvItem(Mill.byzantineMace,0));
		//
		// goodsName.put("clothes_byz_wool", new InvItem(Mill.clothes,0));
		// goodsName.put("clothes_byz_silk", new InvItem(Mill.clothes,1));
		//
		// goodsName.put("winebasic", new InvItem(Mill.wineBasic,0));
		// goodsName.put("lambraw", new InvItem(Mill.lambRaw,0));
		// goodsName.put("lambcooked", new InvItem(Mill.lambCooked,0));
		// goodsName.put("feta", new InvItem(Mill.feta,0));
		// goodsName.put("souvlaki", new InvItem(Mill.souvlaki,0));
		//
		// goodsName.put("ikayaki", new InvItem(Mill.ikayaki,0));
		//
		// goodsName.put("tnt", new InvItem(Block.tnt,0));
		// goodsName.put("vines", new InvItem(Block.vine,0));
		// goodsName.put("wheat", new InvItem(Item.wheat,0));
		// goodsName.put("wood", new InvItem(Blocks.log,0));
		// goodsName.put("wood_any", new InvItem(Blocks.log,-1));
		// goodsName.put("wood_oak", new InvItem(Blocks.log,0));
		// goodsName.put("wood_birch", new InvItem(Blocks.log,2));
		// goodsName.put("wood_jungle", new InvItem(Blocks.log,3));
		// goodsName.put("wood_pine", new InvItem(Blocks.log,1));
		// goodsName.put("woodaxe", new InvItem(Item.axeWood,0));
		// goodsName.put("woodhoe", new InvItem(Item.hoeWood,0));
		// goodsName.put("woodpickaxe", new InvItem(Item.pickaxeWood,0));
		// goodsName.put("woodshovel", new InvItem(Item.shovelWood,0));
		// goodsName.put("woodsword", new InvItem(Item.swordWood,0));
		// goodsName.put("wool_black", new InvItem(Blocks.wool,15));
		// goodsName.put("wool_blue", new InvItem(Blocks.wool,11));
		// goodsName.put("wool_brown", new InvItem(Blocks.wool,12));
		// goodsName.put("wool_cyan", new InvItem(Blocks.wool,9));
		// goodsName.put("wool_gray", new InvItem(Blocks.wool,7));
		// goodsName.put("wool_green", new InvItem(Blocks.wool,13));
		// goodsName.put("wool_lightblue", new InvItem(Blocks.wool,3));
		// goodsName.put("wool_limegreen", new InvItem(Blocks.wool,5));
		// goodsName.put("wool_lightgray", new InvItem(Blocks.wool,8));
		// goodsName.put("wool_magenta", new InvItem(Blocks.wool,2));
		// goodsName.put("wool_orange", new InvItem(Blocks.wool,1));
		// goodsName.put("wool_pink", new InvItem(Blocks.wool,6));
		// goodsName.put("wool_purple", new InvItem(Blocks.wool,10));
		// goodsName.put("wool_red", new InvItem(Blocks.wool,14));
		// goodsName.put("wool_white", new InvItem(Blocks.wool,0));
		// goodsName.put("wool_yellow", new InvItem(Blocks.wool,4));
		// goodsName.put("yellowflower", new InvItem(Blocks.yellow_flower,0));
		// goodsName.put("bricks", new InvItem(Blocks.brick_block,0));
		// goodsName.put("stonebrick", new InvItem(Blocks.stonebrick,0));
		// goodsName.put("gravel", new InvItem(Blocks.gravel,0));
		// goodsName.put("bookandquill", new InvItem(Items.writable_book,0));
		// goodsName.put("purse", new InvItem(Mill.purse,0));
		//
		// goodsName.put("pathdirt", new InvItem(Mill.path,0));
		// goodsName.put("pathgravel", new InvItem(Mill.path,1));
		// goodsName.put("pathslabs", new InvItem(Mill.path,2));
		// goodsName.put("pathsandstone", new InvItem(Mill.path,3));
		// goodsName.put("pathochretiles", new InvItem(Mill.path,4));
		// goodsName.put("pathgravelslabs", new InvItem(Mill.path,5));
		//
		// goodsName.put("pathdirt_slab", new InvItem(Mill.pathSlab,0));
		// goodsName.put("pathgravel_slab", new InvItem(Mill.pathSlab,1));
		// goodsName.put("pathslabs_slab", new InvItem(Mill.pathSlab,2));
		// goodsName.put("pathsandstone_slab", new InvItem(Mill.pathSlab,3));
		// goodsName.put("pathochretiles_slab", new InvItem(Mill.pathSlab,4));
		// goodsName.put("pathgravelslabs_slab", new InvItem(Mill.pathSlab,5));
		//
		// goodsName.put("pathdirt_stable", new InvItem(Mill.path,8));
		// goodsName.put("pathgravel_stable", new InvItem(Mill.path,9));
		// goodsName.put("pathslabs_stable", new InvItem(Mill.path,10));
		// goodsName.put("pathsandstone_stable", new InvItem(Mill.path,11));
		// goodsName.put("pathochretiles_stable", new InvItem(Mill.path,12));
		// goodsName.put("pathgravelslabs_stable", new InvItem(Mill.path,13));
		//
		// goodsName.put("pathdirt_slab_stable", new InvItem(Mill.pathSlab,8));
		// goodsName.put("pathgravel_slab_stable", new
		// InvItem(Mill.pathSlab,9));
		// goodsName.put("pathslabs_slab_stable", new
		// InvItem(Mill.pathSlab,10));
		// goodsName.put("pathsandstone_slab_stable", new
		// InvItem(Mill.pathSlab,11));
		// goodsName.put("pathochretiles_slab_stable", new
		// InvItem(Mill.pathSlab,12));
		// goodsName.put("pathgravelslabs_slab_stable", new
		// InvItem(Mill.pathSlab,13));

	}

	public InvItem item;

	public String name;

	private final int sellingPrice;

	private final int buyingPrice;

	public int reservedQuantity;

	public int targetQuantity;

	public int foreignMerchantPrice;

	public String requiredTag;

	public boolean autoGenerate = false;

	public int minReputation;

	public String desc = null;

	public Goods(final InvItem iv) {
		// Constructor for automated good creation
		item = iv;
		name = item.getName();
		sellingPrice = 0;
		buyingPrice = 1;
		requiredTag = null;
	}

	public Goods(final String name, final InvItem item, final int sellingPrice, final int buyingPrice, final int reservedQuantity, final int targetQuantity, final int foreignMerchantPrice,
			final boolean autoGenerate, final String tag, final int minReputation, final String desc) {
		this.name = name;
		this.item = item;
		this.sellingPrice = sellingPrice;
		this.buyingPrice = buyingPrice;
		this.requiredTag = tag;
		this.autoGenerate = autoGenerate;
		this.reservedQuantity = reservedQuantity;
		this.targetQuantity = targetQuantity;
		this.foreignMerchantPrice = foreignMerchantPrice;
		this.minReputation = minReputation;
		this.desc = desc;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Goods)) {
			return false;
		}

		final Goods g = (Goods) obj;

		return g.item.equals(obj);
	}

	public int getBasicBuyingPrice(final Building shop) {

		if (shop == null) {
			return buyingPrice;
		}

		if (shop.getTownHall().villageType.buyingPrices.containsKey(item)) {
			return shop.getTownHall().villageType.buyingPrices.get(item);
		}

		return buyingPrice;
	}

	public int getBasicSellingPrice(final Building shop) {

		if (shop == null) {
			return sellingPrice;
		}

		if (shop.getTownHall().villageType.sellingPrices.containsKey(item)) {
			return shop.getTownHall().villageType.sellingPrices.get(item);
		}

		return sellingPrice;
	}

	public int getCalculatedBuyingPrice(final Building shop, final EntityPlayer player) {

		if (shop == null) {
			return buyingPrice;
		}

		return shop.getBuyingPrice(this, player);
	}

	public int getCalculatedSellingPrice(final Building shop, final EntityPlayer player) {

		if (shop == null) {
			return sellingPrice;
		}

		return shop.getSellingPrice(this, player);
	}

	public int getCalculatedSellingPrice(final MillVillager merchant) {

		if (merchant == null) {
			return foreignMerchantPrice;
		}

		if (merchant.merchantSells.containsKey(this)) {
			return merchant.merchantSells.get(this);
		}

		return foreignMerchantPrice;
	}

	public String getName() {
		return Mill.proxy.getItemName(item.getItem(), item.meta);
	}

	@Override
	public int hashCode() {
		return item.hashCode();
	}

	@Override
	public String toString() {
		return "Goods@" + item.getItemStack().getUnlocalizedName();
	}

}
