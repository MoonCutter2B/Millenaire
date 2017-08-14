package org.millenaire.generation;

import java.util.Random;

import org.millenaire.MillConfig;
import org.millenaire.MillCulture;
import org.millenaire.blocks.BlockVillageStone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

public class VillageGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		//Is the generation thread looping?
		final StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for (int i = 2; i < trace.length; i++) {
			if (trace[i].getClassName().equals(this.getClass().getName())) {
				return;
			}
		}
		
		if(world.provider.getDimensionId() == 0) {
			BlockPos pos1 = new BlockPos(chunkX * 16, 0, chunkZ * 16);
			
			try {
				generateVillageAt(random, world.getHeight(pos1), world);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Attempt to generate the village
	 */
	protected boolean generateVillageAt(Random rand, BlockPos pos, World world) {
		if(!MillConfig.generateVillages && !MillConfig.generateLoneBuildings) {
			return false;
		}
		if(world.isRemote) {
			return false;
		}
		else {
			EntityPlayer generatingPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), -1);
			return false;
		}
	}
}