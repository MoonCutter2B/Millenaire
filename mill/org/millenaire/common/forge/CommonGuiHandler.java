package org.millenaire.common.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.millenaire.common.ContainerPuja;
import org.millenaire.common.ContainerTrade;
import org.millenaire.common.MLN;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.block.BlockMillChest;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;

import cpw.mods.fml.common.network.IGuiHandler;

public class CommonGuiHandler implements IGuiHandler {

	public static final int GUI_MILL_CHEST = 1;
	public static final int GUI_TRADE = 2;
	public static final int GUI_QUEST = 3;
	public static final int GUI_VILLAGECHIEF = 4;
	public static final int GUI_VILLAGEBOOK = 5;
	public static final int GUI_PUJAS = 6;
	public static final int GUI_PANEL = 7;
	public static final int GUI_MERCHANT = 8;
	public static final int GUI_NEGATIONWAND = 9;
	public static final int GUI_NEWBUILDING = 10;
	public static final int GUI_CONTROLLEDPROJECTPANEL = 11;
	public static final int GUI_HIRE = 12;
	public static final int GUI_NEWVILLAGE = 13;
	public static final int GUI_CONTROLLEDMILITARYPANEL = 14;

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player,
			final World world, final int x, final int y, final int z) {

		return null;
	}

	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player,
			final World world, final int x, final int y, final int z) {

		final MillWorld mw = Mill.getMillWorld(world);

		if (ID == GUI_MILL_CHEST) {
			final TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityMillChest) {
				return BlockMillChest.createContainer(world, x, y, z, player);
			}
		} else if (ID == GUI_TRADE) {
			final Building building = mw.getBuilding(new Point(x, y, z));

			if (building != null) {
				return new ContainerTrade(player, building);
			} else {
				MLN.error(this, "Server-side traiding for unknow building at "
						+ new Point(x, y, z) + " in world: " + world);
			}
		} else if (ID == GUI_MERCHANT) {
			final long id = MillCommonUtilities.unpackLong(x, y);
			if (mw.villagers.containsKey(id)) {
				return new ContainerTrade(player, mw.villagers.get(id));
			} else {
				MLN.error(player, "Failed to find merchant: " + id);
			}
		} else if (ID == GUI_PUJAS) {
			final Building building = mw.getBuilding(new Point(x, y, z));

			if (building != null && building.pujas != null) {
				return new ContainerPuja(player, building);
			}
		}

		return null;
	}

}
