package org.millenaire.networking;

import org.millenaire.building.PlanIO;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExportBuilding implements IMessage {

	BlockPos pos;
	
	public PacketExportBuilding() {
		
	}
	
	public PacketExportBuilding(BlockPos startPos) {
		this.pos = startPos;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int x, y, z;
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		pos = new BlockPos(x, y, z);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	public static class Handler implements IMessageHandler<PacketExportBuilding, IMessage> {

		@Override
		public IMessage onMessage(PacketExportBuilding message, MessageContext ctx) {
			MinecraftServer.getServer().addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketExportBuilding message, MessageContext ctx) {
			PlanIO.exportBuilding(ctx.getServerHandler().playerEntity, message.pos);
		}
	}
}
