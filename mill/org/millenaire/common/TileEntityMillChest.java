package org.millenaire.common;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;

public class TileEntityMillChest extends TileEntityChest {

	public static void readUpdatePacket(DataInputStream ds,World world) {

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

	@Override
	public void checkForAdjacentChests()
	{
		if (adjacentChestChecked)
			return;
		adjacentChestChecked = true;
		adjacentChestZNeg = null;
		adjacentChestXPos = null;
		adjacentChestXNeg = null;
		adjacentChestZPosition = null;
		if (worldObj.getBlockId(xCoord - 1, yCoord, zCoord) == Mill.lockedChest.blockID)
		{
			adjacentChestXNeg = (TileEntityChest)worldObj.getBlockTileEntity(xCoord - 1, yCoord, zCoord);
		}
		if (worldObj.getBlockId(xCoord + 1, yCoord, zCoord) == Mill.lockedChest.blockID)
		{
			adjacentChestXPos = (TileEntityChest)worldObj.getBlockTileEntity(xCoord + 1, yCoord, zCoord);
		}
		if (worldObj.getBlockId(xCoord, yCoord, zCoord - 1) == Mill.lockedChest.blockID)
		{
			adjacentChestZNeg = (TileEntityChest)worldObj.getBlockTileEntity(xCoord, yCoord, zCoord - 1);
		}
		if (worldObj.getBlockId(xCoord, yCoord, zCoord + 1) == Mill.lockedChest.blockID)
		{
			adjacentChestZPosition = (TileEntityChest)worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + 1);
		}
		if (adjacentChestZNeg != null)
		{
			adjacentChestZNeg.updateContainingBlockInfo();
		}
		if (adjacentChestZPosition != null)
		{
			adjacentChestZPosition.updateContainingBlockInfo();
		}
		if (adjacentChestXPos != null)
		{
			adjacentChestXPos.updateContainingBlockInfo();
		}
		if (adjacentChestXNeg != null)
		{
			adjacentChestXNeg.updateContainingBlockInfo();
		}
	}

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
	public String getInvName() {

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

		if (building.lockedForPlayer(player.username))
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


}
