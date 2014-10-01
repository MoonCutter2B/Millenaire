package org.millenaire.common.block;

import java.util.Random;

import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.millenaire.common.Point;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.building.Building;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;

public class BlockPanel extends BlockSign {

	@SuppressWarnings("rawtypes")
	public BlockPanel(final Class class1, final boolean flag) {
		super(class1, flag);
	}

	@Override
	public int getFireSpreadSpeed(final IBlockAccess world, final int x,
			final int y, final int z, final ForgeDirection face) {
		return 60;
	}

	@Override
	public int getFlammability(final IBlockAccess world, final int x,
			final int y, final int z, final ForgeDirection face) {
		return 200;
	}

	@Override
	public boolean onBlockActivated(final World world, final int i,
			final int j, final int k, final EntityPlayer entityplayer,
			final int par6, final float par7, final float par8, final float par9) {

		if (world.isRemote) {
			return true;
		}

		final TileEntityPanel panel = (TileEntityPanel) world.getTileEntity(i,
				j, k);

		if (panel == null || panel.panelType == 0) {
			return false;
		}

		final Building building = Mill.getMillWorld(world).getBuilding(
				panel.buildingPos);

		if (building == null) {
			return false;
		}

		if (panel.panelType == TileEntityPanel.controlledProjects
				&& building.controlledBy(entityplayer.getDisplayName())) {
			ServerSender.displayControlledProjectGUI(entityplayer, building);
			return true;
		}

		if (panel.panelType == TileEntityPanel.controlledMilitary
				&& building.controlledBy(entityplayer.getDisplayName())) {
			ServerSender.displayControlledMilitaryGUI(entityplayer, building);
			return true;
		}

		ServerSender.displayPanel(entityplayer, new Point(i, j, k));

		return true;
	}

	@Override
	public int quantityDropped(final Random random) {
		return 0;
	}

}
