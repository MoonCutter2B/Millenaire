package org.millenaire.common.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;


public class ItemFoodMultiple extends ItemFood {

	private final int healthAmount;
	private final boolean drink;
	private final int regenerationDuration,drunkDuration;

	public final String iconName;

	public ItemFoodMultiple(int id, String iconName, int healthAmount, int regenerationDuration,
			int foodAmount, float saturation, boolean drink, int drunkDuration) {
		super(id,foodAmount,saturation,false);
		this.healthAmount = healthAmount;
		this.drink=drink;
		this.regenerationDuration=regenerationDuration;
		this.drunkDuration=drunkDuration;

		if (healthAmount>0) {
			setAlwaysEdible();
		}

		this.setCreativeTab(Mill.tabMillenaire);

		this.iconName=iconName;
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		itemIcon = MillCommonUtilities.getIcon(iconRegister, iconName);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		if (drink)
			return EnumAction.drink;

		return EnumAction.eat;
	}


	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{

		itemstack.stackSize--;
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, (world.rand.nextFloat() * 0.1F) + 0.9F);

		entityplayer.getFoodStats().addStats(this);
		entityplayer.heal(healthAmount);

		if (drink) {
			entityplayer.addStat(MillAchievements.cheers, 1);
		}
		
		if (regenerationDuration>0) {
			entityplayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, regenerationDuration * 20, 0));
		}
		
		if (drunkDuration>0) {
			entityplayer.addPotionEffect(new PotionEffect(Potion.confusion.id, drunkDuration * 20, 0));
		}

		this.onFoodEaten(itemstack, world, entityplayer);

		return itemstack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));

		return itemstack;
	}
}
