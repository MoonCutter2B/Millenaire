package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.blocks.MillBlocks;
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
}
