package org.millenaire.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockAlchemists extends Block
{
	private static final int EXPLOSIONRADIUS = 32;

	BlockAlchemists()
	{
		super(Material.rock);
		
		//this.setResistance(6000000.0F);
	}

	private void alchemistExplosion(final World world, final int i, final int j, final int k)
	{
		world.setBlockToAir(new BlockPos(i, j, k));
		world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, i + 0.5D, j + 0.5D, k+ 0.5D, 0.0D, 0.0D, 0.0D);
		world.playSoundEffect(i, j, k, "random.explode", 8.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.9F);
		
		for (int y = EXPLOSIONRADIUS; y >= -EXPLOSIONRADIUS; y--) 
		{
			if (y + j >= 0 && y + j < 128) 
			{
				for (int x = -EXPLOSIONRADIUS; x <= EXPLOSIONRADIUS; x++) 
				{
					for (int z = -EXPLOSIONRADIUS; z <= EXPLOSIONRADIUS; z++) 
					{
						if (x * x + y * y + z * z <= EXPLOSIONRADIUS * EXPLOSIONRADIUS) 
						{
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, i, j + 0.5D, k, 0.0D, 0.0D, 0.0D);
							final Block block = world.getBlockState(new BlockPos(i + x, j + y, k + z)).getBlock();
							if (block != Blocks.air) 
							{
								world.setBlockToAir(new BlockPos(i + x, j + y, k + z));
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
		alchemistExplosion(worldIn, pos.getX(), pos.getY(), pos.getZ());
    }
}
