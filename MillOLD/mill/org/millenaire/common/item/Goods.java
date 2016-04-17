package org.millenaire.common.item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
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
		try {
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
		} catch (final MillenaireException e) {
			MLN.printException(e);
		}
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
				try {

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
									} else {
										goodsName.put(temp[0], new InvItem(block, Integer.parseInt(temp[2])));
									}
								}
							}
						}
					}
				} catch (final Exception e) {
					MLN.printException("Exception while reading line: " + line, e);
				}
			}
		} catch (final IOException e) {
			MLN.printException(e);
			return;
		}
	}

	public static void loadGoods() {

		for (final File loadDir : Mill.loadingDirs) {

			final File mainList = new File(loadDir, "itemlist.txt");

			if (mainList.exists()) {
				loadGoodList(mainList);
			}
		}

		generateGoodsList();

		try {
			goodsName.put("anyenchanted", new InvItem(InvItem.ANYENCHANTED));
			goodsName.put("enchantedsword", new InvItem(InvItem.ENCHANTEDSWORD));
		} catch (final MillenaireException e) {
			MLN.printException(e);
		}

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
