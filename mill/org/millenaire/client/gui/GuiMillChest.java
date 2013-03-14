package org.millenaire.client.gui;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.block.BlockMillChest;



public class GuiMillChest extends GuiChest {

	public static GuiMillChest createGUI(World world, int i, int j, int k, EntityPlayer entityplayer) {
		final TileEntityMillChest lockedchest = (TileEntityMillChest) world.getBlockTileEntity(i, j, k);

		if ((lockedchest==null) || (world.isRemote && !lockedchest.loaded))
			return null;

		final IInventory chest=BlockMillChest.getChestInventory(lockedchest,world,i,j,k);

		return new GuiMillChest(entityplayer,chest,lockedchest);
	}

	EntityPlayer player;
	boolean locked=true;

	private GuiMillChest(EntityPlayer player, IInventory iinventory1, TileEntityMillChest lockedchest) {
		super(player.inventory, iinventory1);
		this.player=player;

		locked=lockedchest.isLockedFor(player);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		if (!locked) {
			super.mouseClicked(i,j,k);
		}
	}
}
