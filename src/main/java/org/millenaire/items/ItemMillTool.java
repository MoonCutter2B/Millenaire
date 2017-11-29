package org.millenaire.items;

import org.millenaire.Millenaire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillTool 
{
	static ToolMaterial TOOLS_norman = EnumHelper.addToolMaterial("normanTools", 2, 1561, 10.0F, 4.0F, 10);
	static ToolMaterial TOOLS_obsidian = EnumHelper.addToolMaterial("obsidianTools", 3, 1561, 6.0F, 2.0F, 25);
	
	public static class ItemMillAxe extends ItemAxe
	{
		public ItemMillAxe(ToolMaterial material) 
		{
			super(material);
		}		
	}
	
	public static class ItemMillShovel extends ItemSpade
	{
		public ItemMillShovel(ToolMaterial material) 
		{
			super(material);
		}		
	}
	
	public static class ItemMillPickaxe extends ItemPickaxe
	{
		public ItemMillPickaxe(ToolMaterial material) 
		{
			super(material);
		}		
	}
	
	public static class ItemMillHoe extends ItemHoe
	{
		public ItemMillHoe(ToolMaterial material) 
		{
			super(material);
		}		
	}
	
	public static class ItemMillMace extends ItemSword
	{
		public ItemMillMace(ToolMaterial material) 
		{
			super(material);
		}
		
		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	    {
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack) == 0)
				stack.addEnchantment(Enchantment.knockback, 2);
	    }
	}
}