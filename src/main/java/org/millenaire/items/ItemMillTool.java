package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.Reference;

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
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    //Declarations
    	public static Item normanAxe;
    	public static Item normanShovel;
    	public static Item normanPickaxe;
    	public static Item normanHoe;
    	public static Item normanSword;
    	
    	public static Item mayanAxe;
    	public static Item mayanShovel;
    	public static Item mayanPickaxe;
    	public static Item mayanHoe;
    	public static Item mayanMace;
    	
    	public static Item byzantineMace;
    	
    	public static Item japaneseSword;
    	public static Item japaneseBow;
    	
    	public static void preinitialize()
    	{
    		normanAxe = new ItemMillAxe(TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanAxe");
    		GameRegistry.registerItem(normanAxe, "normanAxe");
    		normanShovel = new ItemMillShovel(TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanShovel");
    		GameRegistry.registerItem(normanShovel, "normanShovel");
    		normanPickaxe = new ItemMillPickaxe(TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanPickaxe");
    		GameRegistry.registerItem(normanPickaxe, "normanPickaxe");
    		normanHoe = new ItemMillHoe(TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanHoe");
    		GameRegistry.registerItem(normanHoe, "normanHoe");
    		normanSword = new ItemSword(TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanSword");
    		GameRegistry.registerItem(normanSword, "normanSword");
    		
    		mayanAxe = new ItemMillAxe(TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanAxe");
    		GameRegistry.registerItem(mayanAxe, "mayanAxe");
    		mayanShovel = new ItemMillShovel(TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanShovel");
    		GameRegistry.registerItem(mayanShovel, "mayanShovel");
    		mayanPickaxe = new ItemMillPickaxe(TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanPickaxe");
    		GameRegistry.registerItem(mayanPickaxe, "mayanPickaxe");
    		mayanHoe = new ItemMillHoe(TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanHoe");
    		GameRegistry.registerItem(mayanHoe, "mayanHoe");
    		mayanMace = new ItemSword(TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanMace");
    		GameRegistry.registerItem(mayanMace, "mayanMace");
    		
    		byzantineMace = new ItemMillMace(Item.ToolMaterial.IRON).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineMace");
    		GameRegistry.registerItem(byzantineMace, "byzantineMace");
    		
    		japaneseSword = new ItemSword(Item.ToolMaterial.IRON).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseSword");
    		GameRegistry.registerItem(japaneseSword, "japaneseSword");
    		japaneseBow = new ItemMillBow(2, 0.5F, "japaneseBow").setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseBow");
    		GameRegistry.registerItem(japaneseBow, "japaneseBow");
    	}
    	
    	@SideOnly(Side.CLIENT)
    	public static void prerender()
    	{
    		//ModelBakery.addVariantName(japaneseBow_pulling_1, Reference.MOD_ID + ":japaneseBow", Reference.MOD_ID + ":japaneseBow_pulling_1", Reference.MOD_ID + ":japaneseBow_pulling_2", Reference.MOD_ID + ":japaneseBow_pulling_3");
    		ModelBakery.registerItemVariants(japaneseBow, new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow", "inventory"), new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow_pulling_1", "inventory"),
    				new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow_pulling_2", "inventory"), new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow_pulling_3", "inventory"));
    		ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow", "inventory"));
    		/*ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow_pulling_1", "inventory"));
        	ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow_pulling_2", "inventory"));
        	ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow_pulling_3", "inventory"));*/
    	}
    	
    	@SideOnly(Side.CLIENT)
    	public static void render()
    	{
    		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    		
    		renderItem.getItemModelMesher().register(normanAxe, 0, new ModelResourceLocation(Reference.MOD_ID + ":normanAxe", "inventory"));
    		renderItem.getItemModelMesher().register(normanShovel, 0, new ModelResourceLocation(Reference.MOD_ID + ":normanShovel", "inventory"));
    		renderItem.getItemModelMesher().register(normanPickaxe, 0, new ModelResourceLocation(Reference.MOD_ID + ":normanPickaxe", "inventory"));
    		renderItem.getItemModelMesher().register(normanHoe, 0, new ModelResourceLocation(Reference.MOD_ID + ":normanHoe", "inventory"));
    		renderItem.getItemModelMesher().register(normanSword, 0, new ModelResourceLocation(Reference.MOD_ID + ":normanSword", "inventory"));
    		
    		renderItem.getItemModelMesher().register(mayanAxe, 0, new ModelResourceLocation(Reference.MOD_ID + ":mayanAxe", "inventory"));
    		renderItem.getItemModelMesher().register(mayanShovel, 0, new ModelResourceLocation(Reference.MOD_ID + ":mayanShovel", "inventory"));
    		renderItem.getItemModelMesher().register(mayanPickaxe, 0, new ModelResourceLocation(Reference.MOD_ID + ":mayanPickaxe", "inventory"));
    		renderItem.getItemModelMesher().register(mayanHoe, 0, new ModelResourceLocation(Reference.MOD_ID + ":mayanHoe", "inventory"));
    		renderItem.getItemModelMesher().register(mayanMace, 0, new ModelResourceLocation(Reference.MOD_ID + ":mayanMace", "inventory"));
    		
    		renderItem.getItemModelMesher().register(byzantineMace, 0, new ModelResourceLocation(Reference.MOD_ID + ":byzantineMace", "inventory"));
    		
    		renderItem.getItemModelMesher().register(japaneseSword, 0, new ModelResourceLocation(Reference.MOD_ID + ":japaneseSword", "inventory"));
    		//renderItem.getItemModelMesher().register(japaneseBow_pulling_1, 0, new ModelResourceLocation(Reference.MOD_ID + ":japaneseBow_pulling_1", "inventory"));
    	}
}
