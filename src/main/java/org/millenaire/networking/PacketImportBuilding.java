package org.millenaire.networking;

import org.millenaire.building.PlanIO;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketImportBuilding implements IMessage {

	BlockPos pos;
	int dimension;
	EntityPlayer player;
	
	public PacketImportBuilding() {
		
	}
	
	public PacketImportBuilding(BlockPos startPos, int dimID) {
		this.pos = startPos;
		this.dimension = dimID;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int x, y, z;
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		pos = new BlockPos(x, y, z);
		dimension = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(dimension);
	}

	public static class Handler implements IMessageHandler<PacketImportBuilding, IMessage> {

		@Override
		public IMessage onMessage(PacketImportBuilding message, MessageContext ctx) {
			MinecraftServer.getServer().addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketImportBuilding message, MessageContext ctx) {
			PlanIO.importBuilding(ctx.getServerHandler().playerEntity, message.pos);
		}
	}
}
