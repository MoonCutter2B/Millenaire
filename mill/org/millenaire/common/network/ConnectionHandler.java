package org.millenaire.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
import org.millenaire.common.MillWorld;
import org.millenaire.common.UserProfile;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler {

	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			INetworkManager manager, Packet1Login login) {
		ClientSender.networkManager=manager;
		Mill.proxy.handleClientLogin();
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		//can't tell who it is in Forge right now so checking everyone

		for (final MillWorld mw : Mill.serverWorlds) {
			mw.checkConnections();
		}
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, INetworkManager manager) {

	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, INetworkManager manager) {
		ClientSender.networkManager=manager;
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			INetworkManager manager) {
		return null;
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			INetworkManager manager) {

		try {
			if (player instanceof EntityPlayer) {
				final UserProfile profile=MillCommonUtilities.getServerProfile(((EntityPlayer)player).worldObj,((EntityPlayer)player).username);
				if (profile!=null) {
					profile.connectUser();
				} else {
					MLN.error(this, "Could not get profile on login for user: "+((EntityPlayer)player).username);
				}
			} else {
				MLN.error(this, "Player on login not of class EntityPlayer: "+player+" - "+player.getClass().getCanonicalName());
			}
		} catch (final Exception e ) {
			MLN.printException("Error in ConnectionHandler.playerLoggedIn:", e);
		}

	}
}
