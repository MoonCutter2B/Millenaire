package org.millenaire.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.millenaire.common.core.MillCommonUtilities;

public class WorldWrapper {

	World world;

	public WorldWrapper() {

	}

	public WorldWrapper(final World pworld) {
		world = pworld;
	}

	public int findTopGroundBlock(final int x, final int z) {

		int y = findTopSoilBlock(x, z);

		while (y < 127
				&& MillCommonUtilities.isBlockIdSolid(world.getBlock(x, y, z))) {
			y++;
		}

		return y;
	}

	public int findTopSoilBlock(final int x, final int z) {
		return MillCommonUtilities.findTopSoilBlock(world, x, z);
	}

	public Block getBlockId(final int x, final int y, final int z) {
		return world.getBlock(x, y, z);
	}

	public EntityPlayer getClosestPlayer(final int i, final int j, final int k,
			final int l) {
		return world.getClosestPlayer(i, j, k, l);
	}

	public boolean isChunkLoaded(final int i, final int j) {
		final Chunk chunk = world.getChunkFromChunkCoords(i, j);
		return chunk.isChunkLoaded;
	}
}
