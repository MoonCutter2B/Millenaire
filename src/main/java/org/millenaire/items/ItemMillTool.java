package org.millenaire.items;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class ItemMillTool 
{
	static ToolMaterial TOOLS_norman = EnumHelper.addToolMaterial("normanTools", 2, 1561, 10.0F, 4.0F, 10);
	static ToolMaterial TOOLS_obsidian = EnumHelper.addToolMaterial("obsidianTools", 3, 1561, 6.0F, 2.0F, 25);

	protected static class ItemMillAxe extends ItemAxe
	{
		protected ItemMillAxe(ToolMaterial material) { super(material); }
	}

	protected static class ItemMillShovel extends ItemSpade
	{
		protected ItemMillShovel(ToolMaterial material) { super(material); }
	}

	protected static class ItemMillPickaxe extends ItemPickaxe
	{
		protected ItemMillPickaxe(ToolMaterial material) { super(material); }
	}

	protected static class ItemMillHoe extends ItemHoe
	{
		protected ItemMillHoe(ToolMaterial material) { super(material); }
	}
	
	public static class ItemMillMace extends ItemSword
	{
		protected ItemMillMace(ToolMaterial material) { super(material); }
		
		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	    {
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack) == 0) {
				stack.addEnchantment(Enchantment.knockback, 2);
			}
	    }
	}
}