package org.millenaire.items;

import org.millenaire.Millenaire;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillBow extends ItemBow
{
	private float speedFactor = 1;
	private float damageBonus = 0;

	private String itemName;

	ItemMillBow(float speedFactor, float damageBonus, String nameIn)
	{
		super();
		this.speedFactor = speedFactor;
		this.damageBonus = damageBonus;
		this.itemName = nameIn;
	}

	@Override
	public void onPlayerStoppedUsing(final ItemStack stack, final World worldIn, final EntityPlayer playerIn, final int timeLeft) 
	{
		int i = this.getMaxItemUseDuration(stack) - timeLeft;
        net.minecraftforge.event.entity.player.ArrowLooseEvent event = new net.minecraftforge.event.entity.player.ArrowLooseEvent(playerIn, stack, i);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;
		i = event.charge;

		final boolean var5 = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		if (var5 || playerIn.inventory.hasItem(Items.arrow)) 
		{
			float var7 = i / 20.0F;
			var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;

			if (var7 < 0.1D) 
			{
				return;
			}

			if (var7 > 1.0F) 
			{
				var7 = 1.0F;
			}

			final EntityArrow entityArrow = new EntityArrow(worldIn, playerIn, var7 * 2.0F * speedFactor);

			if (var7 == 1.0F) 
			{
				entityArrow.setIsCritical(true);
			}

			final int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (power > 0) 
			{
				entityArrow.setDamage(entityArrow.getDamage() + power * 0.5D + 0.5D);
			}

			final int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (punch > 0) 
			{
				entityArrow.setKnockbackStrength(punch);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) 
			{
				entityArrow.setFire(100);
			}

			stack.damageItem(1, playerIn);
			worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);

			if (var5)
			{
                entityArrow.canBePickedUp = 2;
            }
			else
			{
                playerIn.inventory.consumeInventoryItem(Items.arrow);
            }

			// faster MLN arrows
			//entityArrow.motionX *= speedFactor;
			//entityArrow.motionY *= speedFactor;
			//entityArrow.motionZ *= speedFactor;

			// extra arrow damage
			entityArrow.setDamage(entityArrow.getDamage() + damageBonus);

			if (!worldIn.isRemote) 
			{
				worldIn.spawnEntityInWorld(entityArrow);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int ticksRemaining) 
	{
		if (player.isUsingItem()) 
		{
			final int k = stack.getMaxItemUseDuration() - player.getItemInUseCount();
			if (k >= 18)
			{
				System.out.println("step 3");
				return new ModelResourceLocation(Millenaire.MODID + ":" + itemName + "_pulling_3", "inventory");
			}
			else if (k > 13)
			{
				System.out.println("step 2");
				return new ModelResourceLocation(Millenaire.MODID + ":" + itemName + "_pulling_2", "inventory");
			}
			else if (k > 0)
			{
				System.out.println("step 1");
				return new ModelResourceLocation(Millenaire.MODID + ":" + itemName + "_pulling_1", "inventory");
			}
			else
			{
				return new ModelResourceLocation(Millenaire.MODID + ":" + itemName, "inventory");
			}
		}
		else
		{
			return new ModelResourceLocation(Millenaire.MODID + ":" + itemName, "inventory");
		}
	}
}
