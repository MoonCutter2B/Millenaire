package org.millenaire.common.forge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

import org.millenaire.common.MLN;
import org.millenaire.common.building.Building;

public class BuildingChunkLoader {

	public static class ChunkLoaderCallback implements LoadingCallback {

		@Override
		public void ticketsLoaded(final List<Ticket> tickets, final World world) {
			for (final Ticket ticket : tickets) {
				ForgeChunkManager.releaseTicket(ticket);
			}
		}
	}

	Building townHall;

	List<Ticket> tickets = new ArrayList<Ticket>();

	public boolean chunksLoaded = false;

	public BuildingChunkLoader(final Building th) {
		townHall = th;
	}

	private Ticket getTicket() {

		for (final Ticket ticket : tickets) {
			if (ticket.getChunkList().size() < ticket.getChunkListDepth() - 1) {
				return ticket;
			}
		}

		final Ticket ticket = ForgeChunkManager.requestTicket(Mill.instance, townHall.worldObj, Type.NORMAL);

		if (ticket == null) {
			MLN.warning(townHall, "Couldn't get ticket in BuildingChunkLoader. Your Forge chunk loading settings must be interfearing.");
			return null;
		} else {
			tickets.add(ticket);
			return ticket;
		}

	}

	public void loadChunks() {
		if (townHall.winfo != null) {
			for (int cx = townHall.winfo.chunkStartX - 1; cx < townHall.winfo.chunkStartX + townHall.winfo.length / 16 + 1; cx++) {
				for (int cz = townHall.winfo.chunkStartZ - 1; cz < townHall.winfo.chunkStartZ + townHall.winfo.width / 16 + 1; cz++) {
					final Ticket ticket = getTicket();

					if (ticket != null) {
						final ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(cx, cz);
						ForgeChunkManager.forceChunk(ticket, chunkCoords);
					}
				}
			}
			chunksLoaded = true;
		}
	}

	public void unloadChunks() {
		for (final Ticket ticket : tickets) {
			ForgeChunkManager.releaseTicket(ticket);
		}
		tickets.clear();
		chunksLoaded = false;
	}

}
