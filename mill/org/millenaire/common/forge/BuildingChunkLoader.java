package org.millenaire.common.forge;

import java.util.List;
import java.util.Vector;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;

public class BuildingChunkLoader {
	
	Building townHall;
	Vector<Ticket> tickets=new Vector<Ticket>();
	
	public boolean chunksLoaded=false;
	
	public BuildingChunkLoader(Building th) {
		townHall=th;
	}
	
	public void loadChunks() {
		if (townHall.winfo!=null) {			
			for (int cx=townHall.winfo.chunkStartX;cx<townHall.winfo.chunkStartX+townHall.winfo.length/16;cx++) {
				for (int cz=townHall.winfo.chunkStartZ;cz<townHall.winfo.chunkStartZ+townHall.winfo.width/16;cz++) {
					Ticket ticket=getTicket();
					
					if (ticket!=null) {
						ChunkCoordIntPair chunkCoords=new ChunkCoordIntPair(cx,cz);
						ForgeChunkManager.forceChunk(ticket, chunkCoords);
					}
				}
			}
			chunksLoaded=true;
		}
	}
	
	public void unloadChunks() {
		for (Ticket ticket : tickets) {
			ForgeChunkManager.releaseTicket(ticket);
		}
		tickets.clear();
		chunksLoaded=false;
	}
	
	private Ticket getTicket() {
		
		for (Ticket ticket : tickets) {
			if (ticket.getChunkList().size()<ticket.getChunkListDepth()-1) {
				return ticket;
			}
		}
		
		Ticket ticket=ForgeChunkManager.requestTicket(Mill.instance, townHall.worldObj, Type.NORMAL);
		
		if (ticket==null) {
			MLN.warning(townHall, "Couldn't get ticket in BuildingChunkLoader. Your Forge chunk loading settings must be interfearing.");
			return null;
		} else {
			tickets.add(ticket);
			return ticket;
		}
		
		
	}
	
	
	
	
	public static class ChunkLoaderCallback implements LoadingCallback {

		@Override
		public void ticketsLoaded(List<Ticket> tickets, World world) {
			for (Ticket ticket : tickets) {
				ForgeChunkManager.releaseTicket(ticket);
			}
		}
	}

}
