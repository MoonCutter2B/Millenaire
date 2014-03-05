package org.millenaire.common;

import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;

public class TileEntityMillChest extends TileEntityChest implements ISidedInventory {

	public static void readUpdatePacket(ByteBufInputStream ds,World world) {

		Point pos=null;
		try {
			pos = StreamReadWrite.readNullablePoint(ds);
		} catch (final IOException e) {
			MLN.printException(e);
			return;
		}

		final TileEntityMillChest te=pos.getMillChest(world);

		if (te!=null) {
			try {
				te.buildingPos=StreamReadWrite.readNullablePoint(ds);
				te.serverDevMode=ds.readBoolean();

				final byte nb=ds.readByte();
				for (int i=0;i<nb;i++) {
					te.setInventorySlotContents(i, StreamReadWrite.readNullableItemStack(ds));
				}

				te.loaded=true;

			} catch (final IOException e) {
				MLN.printException(te+": Error in readUpdatePacket", e);
			}
		}


	}

	public Point buildingPos=null;
	public boolean loaded=false;
	public boolean serverDevMode=false;


	public String getInvLargeName() {

		if (buildingPos==null)
			return MLN.string("ui.largeunlockedchest");


		Building building=null;

		if (Mill.clientWorld!=null) {
			building=Mill.clientWorld.getBuilding(buildingPos);
		}

		if (building==null)
			return MLN.string("ui.largeunlockedchest");

		final String s=building.getNativeBuildingName();

		if (building.chestLocked)
			return s+": "+MLN.string("ui.largelockedchest");
		else
			return s+": "+MLN.string("ui.largeunlockedchest");
	}

	@Override
	public String getInventoryName() {

		if (buildingPos==null)
			return MLN.string("ui.unlockedchest");

		Building building=null;

		if (Mill.clientWorld!=null) {
			building=Mill.clientWorld.getBuilding(buildingPos);
		}

		if (building==null)
			return MLN.string("ui.unlockedchest");

		final String s=building.getNativeBuildingName();

		if (building.chestLocked)
			return s+": "+MLN.string("ui.lockedchest");
		else
			return s+": "+MLN.string("ui.unlockedchest");
	}

	public boolean isLockedFor(EntityPlayer player) {

		if (player==null) {
			MLN.printException("Null player", new Exception());
			return true;
		}

		if ((loaded==false) && worldObj.isRemote)
			return true;

		if (buildingPos==null)
			return false;

		if (!worldObj.isRemote && MLN.DEV)
			return false;

		if (serverDevMode)
			return false;

		final MillWorld mw=Mill.getMillWorld(worldObj);

		if (mw==null) {
			MLN.printException("Null MillWorld", new Exception());
			return true;
		}

		final Building building=mw.getBuilding(buildingPos);

		if (building==null)
			return true;

		if (building.lockedForPlayer(player.getDisplayName()))
			return true;
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);

		buildingPos = Point.read(nbttagcompound, "buildingPos");
	}

	public void sendUpdatePacket(EntityPlayer player) {
		ServerSender.sendLockedChestUpdatePacket(this,player);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);

		if (buildingPos != null) {
			buildingPos.write(nbttagcompound, "buildingPos");
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}


	public static class InventoryMillLargeChest extends InventoryLargeChest implements ISidedInventory {

		public InventoryMillLargeChest(String par1Str,
				IInventory par2iInventory, IInventory par3iInventory) {
			super(par1Str, par2iInventory, par3iInventory);
		}
		
		@Override
		public int[] getAccessibleSlotsFromSide(int var1) {
			return new int[0];
		}

		@Override
		public boolean canInsertItem(int i, ItemStack itemstack, int j) {
			return false;
		}

		@Override
		public boolean canExtractItem(int i, ItemStack itemstack, int j) {
			return false;
		}
		
	}
	
}
