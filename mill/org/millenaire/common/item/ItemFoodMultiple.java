package org.millenaire.common.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;


public class ItemFoodMultiple extends ItemFood {

	private final int healthAmount;
	private final int foodAmount;
	private final float saturationModifier;
	private final boolean drink;
	
	public final String iconName;

	public ItemFoodMultiple(int id, String iconName, int healthAmount, int foodAmount, float saturation,int nbUse, boolean drink) {
		super(id,foodAmount,saturation,false);
		this.healthAmount = healthAmount;
		maxStackSize = 1;
		this.drink=drink;
		this.foodAmount=foodAmount;
		this.saturationModifier=saturation;
		setMaxDamage(nbUse);

		if (healthAmount>0) {
			setAlwaysEdible();
		}

		this.setCreativeTab(Mill.tabMillenaire);
		
		this.iconName=iconName;
	}
	
	@Override
	public void updateIcons(IconRegister iconRegister)
	{
		iconIndex = MillCommonUtilities.getIcon(iconRegister, iconName);
	}

	@Override
	public int getHealAmount()
	{
		return this.foodAmount;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		if (drink)
			return EnumAction.drink;

		return EnumAction.eat;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack)
	{
		return 32;
	}

	/**
	 * gets the saturationModifier of the ItemFood
	 */
	@Override
	public float getSaturationModifier()
	{
		return this.saturationModifier;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{

		if (getMaxDamage()==0) {
			itemstack.stackSize--;
		} else {
			itemstack.damageItem(1, entityplayer);
		}

		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, (world.rand.nextFloat() * 0.1F) + 0.9F);

		entityplayer.getFoodStats().addStats(this);
		entityplayer.heal(healthAmount);
		
		if (drink) {
			entityplayer.addStat(MillAchievements.cheers, 1);
		}
		
		return itemstack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));

		return itemstack;
	}
}
