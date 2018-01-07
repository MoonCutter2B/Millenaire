package org.millenaire.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillAmulet extends Item
{
	private int[] colorAlchemist = new int[]{9868950, 10132109, 10395268, 10658427, 11053168, 11316327, 11579486, 11842645, 12237387, 12500545, 12763705, 13026863, 13421605, 13684764, 13947923, 14211082};
	private int[] colorVishnu = new int[]{236, 983260, 2031820, 3080380, 4063405, 5111965, 6160525, 7209085, 8192110, 9240670, 10289230, 11337790, 12320815, 13369375, 14417935, 15466496};
	private int[] colorYggdrasil = new int[]{396556, 990493, 1453614, 2113086, 2576206, 3104864, 3698799, 4227457, 4755857, 5350050, 5878706, 6407106, 7001299, 7464165, 8058100, 8388606, 
			8781823, 9306111, 9895935, 10420223, 10944511, 11534335, 12058623, 12648447, 13172735, 13762559, 14286847, 14876671, 15400959, 15925247, 16515071, 16777213};

	ItemMillAmulet()
	{

	}

	@Override
	public ItemStack onItemRightClick(final ItemStack itemstack, final World world, final EntityPlayer entityplayer) 
	{
		if(this == MillItems.amuletSkollHati && !world.isRemote)
		{
			final long time = world.getWorldTime() + 24000L;

			if (time % 24000L > 11000L && time % 24000L < 23500L) 
			{
				world.setWorldTime(time - time % 24000L - 500L);
			} 
			else 
			{
				world.setWorldTime(time - time % 24000L + 13000L);
			}

			itemstack.damageItem(1, entityplayer);
		}

		return itemstack;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(this == MillItems.amuletSkollHati)
			return;

		int visScore = 0;

		if(this == MillItems.amuletAlchemist && entityIn instanceof EntityPlayer)
		{
			int radius = 5;
			BlockPos pos = entityIn.getPosition();

			final int startY = Math.max(pos.getY() - radius, 0);
			final int endY = Math.min(pos.getY() + radius, 127);

			for (int i = pos.getX() - radius; i < pos.getX() + radius; i++) 
			{
				for (int j = pos.getZ() - radius; j < pos.getZ() + radius; j++) 
				{
					for (int k = startY; k < endY; k++) 
					{
						final Block block = worldIn.getBlockState(new BlockPos(i, k, j)).getBlock();
						if (block == Blocks.coal_ore)
							visScore++;
						else if (block == Blocks.diamond_ore)
							visScore += 30;
						else if (block == Blocks.emerald_ore)
							visScore += 30;
						else if (block == Blocks.gold_ore)
							visScore += 10;
						else if (block == Blocks.iron_ore)
							visScore += 5;
						else if (block == Blocks.lapis_ore)
							visScore += 10;
						else if (block == Blocks.redstone_ore)
							visScore += 5;
						else if (block == Blocks.lit_redstone_ore)
							visScore += 5;
					}
				}
			}

			if (visScore > 100)
				visScore = 100;

			visScore = (visScore * 15) / 100;
		}

		if(this == MillItems.amuletVishnu && entityIn instanceof EntityPlayer)
		{
			double level;
			final int radius = 20;
			double closestDistance = Double.MAX_VALUE;

			final List<EntityMob> entities = worldIn.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(entityIn.lastTickPosX, entityIn.lastTickPosY, entityIn.lastTickPosZ, entityIn.lastTickPosX + 1.0D, entityIn.lastTickPosY + 1.0D, entityIn.lastTickPosZ + 1.0D).expand(20, 20, 20));

			for (final Entity ent : entities) 
			{
				if (entityIn.getDistanceToEntity(ent) < closestDistance)
					closestDistance = entityIn.getDistanceToEntity(ent);
			}

			if (closestDistance > radius) {
				level = 0;
			} else {
				level = (radius - closestDistance) / radius;
			}

			visScore = (int) (level * 15);
		}

		if(this == MillItems.amuletYggdrasil && entityIn instanceof EntityPlayer)
		{
			int level = (int) Math.floor(entityIn.posY);

			if(level > 255)
			{
				level = 255;
			}
			else if(level < 0)
			{
			level = 0;
			}

			visScore = level / 8;
		}

		NBTTagCompound nbt;
		if(stack.getTagCompound() == null)
			nbt = new NBTTagCompound();
		else
			nbt = stack.getTagCompound();

		nbt.setInteger("score", visScore);
		stack.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if(renderPass != 0)
		{
			if(nbt== null)
			{
				if(this == MillItems.amuletAlchemist)
					return colorAlchemist[0];
				if(this == MillItems.amuletVishnu)
					return colorVishnu[0];
				if(this == MillItems.amuletYggdrasil)
					return colorYggdrasil[16];
			}
			int score = nbt.getInteger("score");

			if(this == MillItems.amuletAlchemist)
				return colorAlchemist[score];
			if(this == MillItems.amuletVishnu)
				return colorVishnu[score];
			if(this == MillItems.amuletYggdrasil)
				return colorYggdrasil[score];
		}
		return 16777215;
	}

	public int getItemStackLimit(ItemStack stack) { return 1; }

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !(oldStack.getItem() == this && newStack.getItem() == this);
	}
}