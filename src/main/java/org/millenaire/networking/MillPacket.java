package org.millenaire.networking;

import org.millenaire.blocks.BlockVillageStone;
import org.millenaire.items.ItemMillWand;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MillPacket implements IMessage {
	private int eventID;
	private boolean messageIsValid;

	// for use by the message handler only.
	public MillPacket() {
		messageIsValid = false;
	}

	public MillPacket(int IDin) {
		eventID = IDin;
		messageIsValid = true;
	}

	public boolean isMessageValid() {
		return messageIsValid;
	}

	public int getID() {
		return eventID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			eventID = buf.readInt();
		} catch (IndexOutOfBoundsException ioe) {
			System.err.println("Exception while reading MillPacket: " + ioe);
		}
		messageIsValid = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (!messageIsValid)
			return;

		buf.writeInt(eventID);
	}

	public static class PacketHandlerOnServer implements IMessageHandler<MillPacket, IMessage> {
		@Override
		public IMessage onMessage(final MillPacket message, MessageContext ctx) {
			if (ctx.side != Side.SERVER) {
				System.err.println("MillPacket received on wrong side: " + ctx.side);
				return null;
			}
			if (!message.isMessageValid()) {
				System.err.println("MillPacket was invalid");
				return null;
			}

			final EntityPlayerMP sendingPlayer = ctx.getServerHandler().playerEntity;
			if (sendingPlayer == null) {
				System.err.println("EntityPlayerMP was null when MillPacket was received");
				return null;
			}

			final WorldServer playerWorldServer = sendingPlayer.getServerForPlayer();
			playerWorldServer.addScheduledTask(new Runnable() {
				public void run() {
					processMessage(message, sendingPlayer);
				}
			});

			return null;
		}

		public void processMessage(MillPacket message, EntityPlayerMP sendingPlayer) {
			if (message.getID() == 2) {
				ItemStack heldItem = sendingPlayer.getHeldItem();
				if (heldItem.getItem() != ItemMillWand.wandNegation) {
					System.err.println("Player not holding Wand of Negation when attempting to delete Village");
				} else {
					World world = sendingPlayer.worldObj;
					NBTTagCompound nbt = heldItem.getTagCompound();
					int posX = nbt.getInteger("X");
					int posY = nbt.getInteger("Y");
					int posZ = nbt.getInteger("Z");

					BlockVillageStone villStone = (BlockVillageStone) world
							.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
					villStone.negate(world, new BlockPos(posX, posY, posZ), sendingPlayer);
				}
			}
			if (message.getID() == 3) {
				ItemStack heldItem = sendingPlayer.getHeldItem();
				if (heldItem.getItem() != ItemMillWand.wandNegation) {
					System.err.println("Player not holding Wand of Negation when attempting to delete Villager");
				} else {
					World world = sendingPlayer.worldObj;
					NBTTagCompound nbt = heldItem.getTagCompound();
					int id = nbt.getInteger("ID");

					world.createExplosion(world.getEntityByID(id), world.getEntityByID(id).posX,
							world.getEntityByID(id).posY, world.getEntityByID(id).posZ, 0.0F, false);
					world.playSoundAtEntity(world.getEntityByID(id), "game.player.hurt", 1.0F, 0.4F);
					// Will need to be actual removal (without respawn).
					world.removeEntity(world.getEntityByID(id));
				}
			}
			if (message.getID() == 4) {
				ItemStack heldItem = sendingPlayer.getHeldItem();
				if (heldItem.getItem() != ItemMillWand.wandSummoning) {
					System.err.println("Player not holding Wand of Summoning when attempting to create Village");
				} else {
					World world = sendingPlayer.worldObj;
					NBTTagCompound nbt = heldItem.getTagCompound();
					int posX = nbt.getInteger("X");
					int posY = nbt.getInteger("Y");
					int posZ = nbt.getInteger("Z");

					world.setBlockState(new BlockPos(posX, posY, posZ),
							BlockVillageStone.villageStone.getDefaultState());
				}
			}
		}
	}

}
