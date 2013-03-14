package org.millenaire.client.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.millenaire.client.gui.GuiMillChest;
import org.millenaire.client.gui.GuiPujas;
import org.millenaire.client.gui.GuiTrade;
import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.CommonGuiHandler;
import org.millenaire.common.forge.Mill;

public class ClientGuiHandler extends CommonGuiHandler {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		if (ID==GUI_MILL_CHEST) {
			final TileEntity te = world.getBlockTileEntity(x, y, z);
			if ((te != null) && (te instanceof TileEntityMillChest))
				return GuiMillChest.createGUI(world, x, y, z, player);
		} else if (ID==GUI_TRADE) {
			final Building building=Mill.clientWorld.getBuilding(new Point(x,y,z));

			if ((building!=null) && (building.getTownHall()!=null))
				return new GuiTrade(player,building);
		} else if (ID==GUI_MERCHANT) {
			final long id=MillCommonUtilities.unpackLong(x, y);
			if (Mill.clientWorld.villagers.containsKey(id))
				return new GuiTrade(player,Mill.clientWorld.villagers.get(id));
			else {
				MLN.error(player, "Failed to find merchant: "+id);
			}
		} else if (ID==GUI_PUJAS) {
			final Building building=Mill.clientWorld.getBuilding(new Point(x,y,z));

			if ((building != null) && (building.pujas != null))
				return new GuiPujas(player,building);
		}

		return null;
	}

}
