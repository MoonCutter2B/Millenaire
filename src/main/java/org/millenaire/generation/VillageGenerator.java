package org.millenaire.generation;

import java.util.Random;

import org.millenaire.MillConfig;
import org.millenaire.MillCulture;
import org.millenaire.blocks.BlockVillageStone;
import org.millenaire.blocks.StoredPosition;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

public class VillageGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {

		if (world.provider.getDimensionId() == 0) {
			BlockPos pos1 = new BlockPos(chunkX * 16, 0, chunkZ * 16);
			// System.out.println("testing1");
			try {
				generateVillageAt(random, world.getHeight(pos1), world);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Attempt to generate the village
	 */
	protected boolean generateVillageAt(Random rand, BlockPos pos, World world) {
		if (!MillConfig.generateVillages && !MillConfig.generateLoneBuildings
				|| (world.getSpawnPoint().distanceSq(pos) < MillConfig.spawnDistance)) {
			// System.out.println("testing2");
			return false;
		}
		if (world.isRemote) {
			// System.out.println("testing3");
			return false;
		} else {
			EntityPlayer generatingPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), -1);
			// System.out.println("testing4");
			if (rand.nextInt(50) == 1 && world.getChunkFromBlockCoords(pos).isLoaded()) {
				// System.out.println("testing5 " + pos.getX() +"," + pos.getZ());
				world.setBlockState(pos, BlockVillageStone.villageStone.getDefaultState());
			} else if (!world.getChunkFromBlockCoords(pos).isLoaded()) {
				// System.out.println("nope");
			}
			return false;
		}
	}
}