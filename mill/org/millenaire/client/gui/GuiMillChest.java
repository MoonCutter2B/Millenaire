package org.millenaire.client.gui;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.block.BlockMillChest;

public class GuiMillChest extends GuiChest {

	public static GuiMillChest createGUI(final World world, final int i,
			final int j, final int k, final EntityPlayer entityplayer) {
		final TileEntityMillChest lockedchest = (TileEntityMillChest) world
				.getTileEntity(i, j, k);

		if (lockedchest == null || world.isRemote && !lockedchest.loaded) {
			return null;
		}

		final IInventory chest = BlockMillChest.getInventory(lockedchest,
				world, i, j, k);

		return new GuiMillChest(entityplayer, chest, lockedchest);
	}

	EntityPlayer player;

	boolean locked = true;

	private GuiMillChest(final EntityPlayer player,
			final IInventory iinventory1, final TileEntityMillChest lockedchest) {
		super(player.inventory, iinventory1);
		this.player = player;

		locked = lockedchest.isLockedFor(player);
	}

	@Override
	protected void keyTyped(final char par1, final int par2) {
		if (!locked) {
			super.keyTyped(par1, par2);
		} else {
			if (par2 == 1
					|| par2 == this.mc.gameSettings.keyBindInventory
							.getKeyCode()) {
				this.mc.thePlayer.closeScreen();
			}
		}
	}

	@Override
	protected void mouseClicked(final int i, final int j, final int k) {
		if (!locked) {
			super.mouseClicked(i, j, k);
		}
	}
}
