package org.millenaire.items;

import org.millenaire.Millenaire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMillArmor 
{
	static ArmorMaterial ARMOR_norman = EnumHelper.addArmorMaterial("normanArmour", "millenaire:normanArmor", 66, new int[] { 3, 8, 6, 3 }, 10);
	static ArmorMaterial ARMOR_japaneseWarriorRed = EnumHelper.addArmorMaterial("japaneseWarriorRed", "millenaire:japaneseWarriorArmorRed", 33, new int[] { 2, 6, 5, 2 }, 25);
	static ArmorMaterial ARMOR_japaneseWarriorBlue = EnumHelper.addArmorMaterial("japaneseWarriorBlue", "millenaire:japaneseWarriorArmorBlue", 33, new int[] { 2, 6, 5, 2 }, 25);
	static ArmorMaterial ARMOR_japaneseGuard = EnumHelper.addArmorMaterial("japaneseGuard", "millenaire:japaneseGuardArmor", 25, new int[] { 2, 5, 4, 1 }, 25);
	static ArmorMaterial ARMOR_byzantine = EnumHelper.addArmorMaterial("byzantineArmour", "millenaire:byzantineArmor", 33, new int[] { 3, 8, 6, 3 }, 20);
	static ArmorMaterial ARMOR_mayanQuest = EnumHelper.addArmorMaterial("mayanQuest", "millenaire:mayanQuest", 5, new int[]{1, 3, 2, 1}, 35);
	
	public static class mayanQuestCrown extends ItemArmor
	{

		public mayanQuestCrown(ArmorMaterial material, int renderIndex, int armorType) 
		{
			super(material, renderIndex, armorType);
		}
		
		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	    {
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, stack) == 0)
				stack.addEnchantment(Enchantment.respiration, 3);
			
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, stack) == 0)
				stack.addEnchantment(Enchantment.aquaAffinity, 1);
			
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) == 0)
				stack.addEnchantment(Enchantment.protection, 4);
	    }
	}
}