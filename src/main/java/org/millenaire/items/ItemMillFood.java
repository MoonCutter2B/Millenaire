package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.Reference;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.gui.MillAchievement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillFood extends ItemFood
{
	public boolean isDrink;
	private int healAmount;
	private int drunkDuration;
	private int regDuration;
	
	public ItemMillFood(int healIn, int regIn, int drunkIn, int hungerIn, float saturationIn, boolean drinkIn) 
	{	
		super(hungerIn, saturationIn, false);

		healAmount = healIn;
		regDuration = regIn;
		drunkDuration = drunkIn;
		
		isDrink = drinkIn;
		if(isDrink)
			this.setAlwaysEdible();
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
		if(isDrink)
			return EnumAction.DRINK;
		else
			return EnumAction.EAT;
    }
	
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
		player.heal(healAmount);

		if (isDrink) {
			player.addStat(MillAchievement.cheers, 1);
		}

		if (regDuration > 0) {
			player.addPotionEffect(new PotionEffect(Potion.regeneration.id, regDuration * 20, 0));
		}

		if (drunkDuration > 0) {
			player.addPotionEffect(new PotionEffect(Potion.confusion.id, drunkDuration * 20, 0));
		}
		
		super.onFoodEaten(stack, worldIn, player);
    }
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    //Declarations
    	public static Item ciderApple;
    	public static Item cider;
    	public static Item calva;
    	public static Item tripes;
    	public static Item boudinNoir;
    	
    	public static Item vegCurry;
    	public static Item murghCurry;
    	public static Item rasgulla;
    	
    	public static Item cacauhaa;
    	public static Item masa;
    	public static Item wah;
    	
    	public static Item wine;
    	public static Item malvasiaWine;
    	public static Item feta;
    	public static Item souvlaki;
    	
    	public static Item sake;
    	public static Item udon;
    	public static Item ikayaki;
    
    public static void preinitialize()
    {
    	ciderApple = new ItemMillFood(0, 0, 0, 1, 0.05F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("ciderApple");
    	GameRegistry.registerItem(ciderApple, "ciderApple");
    	cider = new ItemMillFood(4, 15, 5, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("cider");
    	GameRegistry.registerItem(cider, "cider");
    	calva = ((ItemMillFood)new ItemMillFood(8, 30, 10, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.damageBoost.id, 180, 0, 1f).setUnlocalizedName("calva");
    	GameRegistry.registerItem(calva, "calva");
    	tripes = new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("tripes");
    	GameRegistry.registerItem(tripes, "tripes");
    	boudinNoir = new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("boudinNoir");
    	GameRegistry.registerItem(boudinNoir, "boudinNoir");
    	
    	vegCurry = new ItemMillFood(0, 0, 0, 6, 0.6F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("vegCurry");
    	GameRegistry.registerItem(vegCurry, "vegCurry");
    	murghCurry = ((ItemMillFood)new ItemMillFood(0, 0, 0, 8, 0.8F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.fireResistance.id, 8 * 60, 0, 1f).setUnlocalizedName("murghCurry");
    	GameRegistry.registerItem(murghCurry, "murghCurry");
    	rasgulla = ((ItemMillFood)new ItemMillFood(2, 30, 0, 0, 0.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.moveSpeed.id, 8 * 60, 1, 1f).setAlwaysEdible().setUnlocalizedName("rasgulla");
    	GameRegistry.registerItem(rasgulla, "rasgulla");
    	
    	cacauhaa = ((ItemMillFood)new ItemMillFood(6, 30, 3, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.nightVision.id, 8 * 60, 0, 1f).setUnlocalizedName("cacauhaa");
    	GameRegistry.registerItem(cacauhaa, "cacauhaa");
    	masa = new ItemMillFood(0, 0, 0, 6, 0.6F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("masa");
    	GameRegistry.registerItem(masa, "masa");
    	wah = ((ItemMillFood)new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.digSpeed.id, 8 * 60, 0, 1f).setUnlocalizedName("wah");
    	GameRegistry.registerItem(wah, "wah");
    	
    	wine = new ItemMillFood(3, 15, 5, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wine");
    	GameRegistry.registerItem(wine, "wine");
    	malvasiaWine = ((ItemMillFood)new ItemMillFood(8, 30, 5, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.resistance.id, 8 * 60, 0, 1f).setUnlocalizedName("malvasiaWine");
    	GameRegistry.registerItem(malvasiaWine, "malvasiaWine");
    	feta = new ItemMillFood(3, 10, 0, 0, 0.0F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("feta");
    	GameRegistry.registerItem(feta, "feta");
    	souvlaki = ((ItemMillFood)new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.heal.id, 1, 0, 1f).setUnlocalizedName("souvlaki");
    	GameRegistry.registerItem(souvlaki, "souvlaki");
    	
    	sake = ((ItemMillFood)new ItemMillFood(8, 30, 10, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.jump.id, 8 * 60, 1, 1f).setUnlocalizedName("sake");
    	GameRegistry.registerItem(sake, "sake");
    	udon = new ItemMillFood(0, 0, 0, 8, 0.8F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("udon");
    	GameRegistry.registerItem(udon, "udon");
    	ikayaki = ((ItemMillFood)new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.waterBreathing.id, 8 * 60, 2, 1f).setUnlocalizedName("ikayaki");
    	GameRegistry.registerItem(ikayaki, "ikayaki");
    	
    	GameRegistry.addShapelessRecipe(new ItemStack(vegCurry, 1), new ItemStack(BlockMillCrops.rice), new ItemStack(BlockMillCrops.turmeric));
    	GameRegistry.addShapelessRecipe(new ItemStack(murghCurry, 1), new ItemStack(BlockMillCrops.rice), new ItemStack(BlockMillCrops.turmeric), new ItemStack(Items.chicken));
    	GameRegistry.addRecipe(new ItemStack(masa, 1), 
    			"AAA",
    			'A', new ItemStack(BlockMillCrops.maize));
    	GameRegistry.addRecipe(new ItemStack(wah, 1), 
    			"ABA",
    			'A', new ItemStack(BlockMillCrops.maize), 'B', new ItemStack(Items.chicken));
    	GameRegistry.addShapelessRecipe(new ItemStack(wine, 1), new ItemStack(BlockMillCrops.grapes), new ItemStack(BlockMillCrops.grapes), new ItemStack(BlockMillCrops.grapes), new ItemStack(BlockMillCrops.grapes), 
    			new ItemStack(BlockMillCrops.grapes), new ItemStack(BlockMillCrops.grapes));
    }
    
    @SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		renderItem.getItemModelMesher().register(ciderApple, 0, new ModelResourceLocation(Reference.MOD_ID + ":ciderApple", "inventory"));
		renderItem.getItemModelMesher().register(cider, 0, new ModelResourceLocation(Reference.MOD_ID + ":cider", "inventory"));
		renderItem.getItemModelMesher().register(calva, 0, new ModelResourceLocation(Reference.MOD_ID + ":calva", "inventory"));
		renderItem.getItemModelMesher().register(tripes, 0, new ModelResourceLocation(Reference.MOD_ID + ":tripes", "inventory"));
		renderItem.getItemModelMesher().register(boudinNoir, 0, new ModelResourceLocation(Reference.MOD_ID + ":boudinNoir", "inventory"));
		
		renderItem.getItemModelMesher().register(vegCurry, 0, new ModelResourceLocation(Reference.MOD_ID + ":vegCurry", "inventory"));
		renderItem.getItemModelMesher().register(murghCurry, 0, new ModelResourceLocation(Reference.MOD_ID + ":murghCurry", "inventory"));
		renderItem.getItemModelMesher().register(rasgulla, 0, new ModelResourceLocation(Reference.MOD_ID + ":rasgulla", "inventory"));
		
		renderItem.getItemModelMesher().register(cacauhaa, 0, new ModelResourceLocation(Reference.MOD_ID + ":cacauhaa", "inventory"));
		renderItem.getItemModelMesher().register(masa, 0, new ModelResourceLocation(Reference.MOD_ID + ":masa", "inventory"));
		renderItem.getItemModelMesher().register(wah, 0, new ModelResourceLocation(Reference.MOD_ID + ":wah", "inventory"));
		
		renderItem.getItemModelMesher().register(wine, 0, new ModelResourceLocation(Reference.MOD_ID + ":wine", "inventory"));
		renderItem.getItemModelMesher().register(malvasiaWine, 0, new ModelResourceLocation(Reference.MOD_ID + ":malvasiaWine", "inventory"));
		renderItem.getItemModelMesher().register(feta, 0, new ModelResourceLocation(Reference.MOD_ID + ":feta", "inventory"));
		renderItem.getItemModelMesher().register(souvlaki, 0, new ModelResourceLocation(Reference.MOD_ID + ":souvlaki", "inventory"));
		
		renderItem.getItemModelMesher().register(sake, 0, new ModelResourceLocation(Reference.MOD_ID + ":sake", "inventory"));
		renderItem.getItemModelMesher().register(udon, 0, new ModelResourceLocation(Reference.MOD_ID + ":udon", "inventory"));
		renderItem.getItemModelMesher().register(ikayaki, 0, new ModelResourceLocation(Reference.MOD_ID + ":ikayaki", "inventory"));
	}
}
