package org.millenaire;

import org.millenaire.blocks.BlockVillageStone;
import org.millenaire.items.ItemMillWand;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandlerOnServer implements IMessageHandler<MillPacket, IMessage>
{

	@Override
	public IMessage onMessage(final MillPacket message, MessageContext ctx) 
	{
		if(ctx.side != Side.SERVER) 
		{
			System.err.println("MillPacket received on wrong side: " + ctx.side);
			return null;
		}
		if(!message.isMessageValid())
		{
			System.err.println("MillPacket was invalid");
			return null;
		}
		
		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().playerEntity;
		if (sendingPlayer == null) 
		{
			System.err.println("EntityPlayerMP was null when MillPacket was received");
			return null;
		}
		
		final WorldServer playerWorldServer = sendingPlayer.getServerForPlayer();
		playerWorldServer.addScheduledTask(new Runnable() 
		{
			public void run() 
			{
				processMessage(message, sendingPlayer);
			}
		});
		
		return null;
	}

	public void processMessage(MillPacket message, EntityPlayerMP sendingPlayer)
	{
		if(message.getID() == 2)
		{
			ItemStack heldItem = sendingPlayer.getHeldItem();
			if(heldItem.getItem() != ItemMillWand.wandNegation)
			{
				System.err.println("Player not holding Wand of Negation when attempting to delete Village");
			}
			else
			{
				World world = sendingPlayer.worldObj;
				NBTTagCompound nbt = heldItem.getTagCompound();
				int posX = nbt.getInteger("X");
				int posY = nbt.getInteger("Y");
				int posZ = nbt.getInteger("Z");
				
				BlockVillageStone villStone = (BlockVillageStone)world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
				villStone.negate(world, new BlockPos(posX, posY, posZ), sendingPlayer);
			}
		}
		if(message.getID() == 3)
		{
			ItemStack heldItem = sendingPlayer.getHeldItem();
			if(heldItem.getItem() != ItemMillWand.wandNegation)
			{
				System.err.println("Player not holding Wand of Negation when attempting to delete Villager");
			}
			else
			{
				World world = sendingPlayer.worldObj;
				NBTTagCompound nbt = heldItem.getTagCompound();
				int id = nbt.getInteger("ID");
				
				world.createExplosion(world.getEntityByID(id), world.getEntityByID(id).posX, world.getEntityByID(id).posY, world.getEntityByID(id).posZ, 0.0F, false);
				world.playSoundAtEntity(world.getEntityByID(id), "game.player.hurt", 1.0F, 0.4F);
				//Will need to be actual removal (without respawn).
				world.removeEntity(world.getEntityByID(id));
			}
		}
	}
}
