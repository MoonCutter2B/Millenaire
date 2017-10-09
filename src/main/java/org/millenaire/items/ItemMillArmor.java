package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMillArmor {
	static ArmorMaterial ARMOR_norman = EnumHelper.addArmorMaterial("normanArmour", "millenaire:normanArmor", 66,
			new int[] { 3, 8, 6, 3 }, 10);
	static ArmorMaterial ARMOR_japaneseWarriorRed = EnumHelper.addArmorMaterial("japaneseWarriorRed",
			"millenaire:japaneseWarriorArmorRed", 33, new int[] { 2, 6, 5, 2 }, 25);
	static ArmorMaterial ARMOR_japaneseWarriorBlue = EnumHelper.addArmorMaterial("japaneseWarriorBlue",
			"millenaire:japaneseWarriorArmorBlue", 33, new int[] { 2, 6, 5, 2 }, 25);
	static ArmorMaterial ARMOR_japaneseGuard = EnumHelper.addArmorMaterial("japaneseGuard",
			"millenaire:japaneseGuardArmor", 25, new int[] { 2, 5, 4, 1 }, 25);
	static ArmorMaterial ARMOR_byzantine = EnumHelper.addArmorMaterial("byzantineArmour", "millenaire:byzantineArmor",
			33, new int[] { 3, 8, 6, 3 }, 20);
	static ArmorMaterial ARMOR_mayanQuest = EnumHelper.addArmorMaterial("mayanQuest", "millenaire:mayanQuest", 5,
			new int[] { 1, 3, 2, 1 }, 35);

	public static class mayanQuestCrown extends ItemArmor {

		public mayanQuestCrown(ArmorMaterial material, int renderIndex, int armorType) {
			super(material, renderIndex, armorType);
		}

		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, stack) == 0)
				stack.addEnchantment(Enchantment.respiration, 3);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, stack) == 0)
				stack.addEnchantment(Enchantment.aquaAffinity, 1);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) == 0)
				stack.addEnchantment(Enchantment.protection, 4);
		}
	}

	////////////////////////////////////////////////////////// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	// Declarations
	public static Item normanHelmet;
	public static Item normanChestplate;
	public static Item normanLeggings;
	public static Item normanBoots;

	public static Item byzantineHelmet;
	public static Item byzantineChestplate;
	public static Item byzantineLeggings;
	public static Item byzantineBoots;

	public static Item japaneseGuardHelmet;
	public static Item japaneseGuardChestplate;
	public static Item japaneseGuardLeggings;
	public static Item japaneseGuardBoots;

	public static Item japaneseBlueHelmet;
	public static Item japaneseBlueChestplate;
	public static Item japaneseBlueLeggings;
	public static Item japaneseBlueBoots;

	public static Item japaneseRedHelmet;
	public static Item japaneseRedChestplate;
	public static Item japaneseRedLeggings;
	public static Item japaneseRedBoots;

	public static Item mayanQuestCrown;

	public static void preinitialize() {
		normanHelmet = new ItemArmor(ARMOR_norman, 2, 0).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("normanHelmet");
		GameRegistry.registerItem(normanHelmet, "normanHelmet");
		normanChestplate = new ItemArmor(ARMOR_norman, 2, 1).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("normanChestplate");
		GameRegistry.registerItem(normanChestplate, "normanChestplate");
		normanLeggings = new ItemArmor(ARMOR_norman, 2, 2).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("normanLeggings");
		GameRegistry.registerItem(normanLeggings, "normanLeggings");
		normanBoots = new ItemArmor(ARMOR_norman, 2, 3).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("normanBoots");
		GameRegistry.registerItem(normanBoots, "normanBoots");

		byzantineHelmet = new ItemArmor(ARMOR_byzantine, 2, 0).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("byzantineHelmet");
		GameRegistry.registerItem(byzantineHelmet, "byzantineHelmet");
		byzantineChestplate = new ItemArmor(ARMOR_byzantine, 2, 1).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("byzantineChestplate");
		GameRegistry.registerItem(byzantineChestplate, "byzantineChestplate");
		byzantineLeggings = new ItemArmor(ARMOR_byzantine, 2, 2).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("byzantineLeggings");
		GameRegistry.registerItem(byzantineLeggings, "byzantineLeggings");
		byzantineBoots = new ItemArmor(ARMOR_byzantine, 2, 3).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("byzantineBoots");
		GameRegistry.registerItem(byzantineBoots, "byzantineBoots");

		japaneseGuardHelmet = new ItemArmor(ARMOR_japaneseGuard, 2, 0).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseGuardHelmet");
		GameRegistry.registerItem(japaneseGuardHelmet, "japaneseGuardHelmet");
		japaneseGuardChestplate = new ItemArmor(ARMOR_japaneseGuard, 2, 1).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseGuardChestplate");
		GameRegistry.registerItem(japaneseGuardChestplate, "japaneseGuardChestplate");
		japaneseGuardLeggings = new ItemArmor(ARMOR_japaneseGuard, 2, 2).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseGuardLeggings");
		GameRegistry.registerItem(japaneseGuardLeggings, "japaneseGuardLeggings");
		japaneseGuardBoots = new ItemArmor(ARMOR_japaneseGuard, 2, 3).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseGuardBoots");
		GameRegistry.registerItem(japaneseGuardBoots, "japaneseGuardBoots");

		japaneseBlueHelmet = new ItemArmor(ARMOR_japaneseWarriorBlue, 2, 0).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseBlueHelmet");
		GameRegistry.registerItem(japaneseBlueHelmet, "japaneseBlueHelmet");
		japaneseBlueChestplate = new ItemArmor(ARMOR_japaneseWarriorBlue, 2, 1).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseBlueChestplate");
		GameRegistry.registerItem(japaneseBlueChestplate, "japaneseBlueChestplate");
		japaneseBlueLeggings = new ItemArmor(ARMOR_japaneseWarriorBlue, 2, 2).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseBlueLeggings");
		GameRegistry.registerItem(japaneseBlueLeggings, "japaneseBlueLeggings");
		japaneseBlueBoots = new ItemArmor(ARMOR_japaneseWarriorBlue, 2, 3).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseBlueBoots");
		GameRegistry.registerItem(japaneseBlueBoots, "japaneseBlueBoots");

		japaneseRedHelmet = new ItemArmor(ARMOR_japaneseWarriorRed, 2, 0).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseRedHelmet");
		GameRegistry.registerItem(japaneseRedHelmet, "japaneseRedHelmet");
		japaneseRedChestplate = new ItemArmor(ARMOR_japaneseWarriorRed, 2, 1).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseRedChestplate");
		GameRegistry.registerItem(japaneseRedChestplate, "japaneseRedChestplate");
		japaneseRedLeggings = new ItemArmor(ARMOR_japaneseWarriorRed, 2, 2).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseRedLeggings");
		GameRegistry.registerItem(japaneseRedLeggings, "japaneseRedLeggings");
		japaneseRedBoots = new ItemArmor(ARMOR_japaneseWarriorRed, 2, 3).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("japaneseRedBoots");
		GameRegistry.registerItem(japaneseRedBoots, "japaneseRedBoots");

		mayanQuestCrown = new mayanQuestCrown(ARMOR_mayanQuest, 2, 0).setCreativeTab(Millenaire.tabMillenaire)
				.setUnlocalizedName("mayanQuestCrown");
		GameRegistry.registerItem(mayanQuestCrown, "mayanQuestCrown");
	}

	public static void render() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		renderItem.getItemModelMesher().register(normanHelmet, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":normanHelmet", "inventory"));
		renderItem.getItemModelMesher().register(normanChestplate, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":normanChestplate", "inventory"));
		renderItem.getItemModelMesher().register(normanLeggings, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":normanLeggings", "inventory"));
		renderItem.getItemModelMesher().register(normanBoots, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":normanBoots", "inventory"));

		renderItem.getItemModelMesher().register(byzantineHelmet, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":byzantineHelmet", "inventory"));
		renderItem.getItemModelMesher().register(byzantineChestplate, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":byzantineChestplate", "inventory"));
		renderItem.getItemModelMesher().register(byzantineLeggings, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":byzantineLeggings", "inventory"));
		renderItem.getItemModelMesher().register(byzantineBoots, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":byzantineBoots", "inventory"));

		renderItem.getItemModelMesher().register(japaneseGuardHelmet, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseGuardHelmet", "inventory"));
		renderItem.getItemModelMesher().register(japaneseGuardChestplate, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseGuardChestplate", "inventory"));
		renderItem.getItemModelMesher().register(japaneseGuardLeggings, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseGuardLeggings", "inventory"));
		renderItem.getItemModelMesher().register(japaneseGuardBoots, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseGuardBoots", "inventory"));

		renderItem.getItemModelMesher().register(japaneseBlueHelmet, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseBlueHelmet", "inventory"));
		renderItem.getItemModelMesher().register(japaneseBlueChestplate, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseBlueChestplate", "inventory"));
		renderItem.getItemModelMesher().register(japaneseBlueLeggings, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseBlueLeggings", "inventory"));
		renderItem.getItemModelMesher().register(japaneseBlueBoots, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseBlueBoots", "inventory"));

		renderItem.getItemModelMesher().register(japaneseRedHelmet, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseRedHelmet", "inventory"));
		renderItem.getItemModelMesher().register(japaneseRedChestplate, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseRedChestplate", "inventory"));
		renderItem.getItemModelMesher().register(japaneseRedLeggings, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseRedLeggings", "inventory"));
		renderItem.getItemModelMesher().register(japaneseRedBoots, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":japaneseRedBoots", "inventory"));

		renderItem.getItemModelMesher().register(mayanQuestCrown, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":mayanquestcrown", "inventory"));
	}
}
