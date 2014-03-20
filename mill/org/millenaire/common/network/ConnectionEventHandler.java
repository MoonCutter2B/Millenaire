package org.millenaire.common.network;

import org.millenaire.common.MLN;
import org.millenaire.common.MillWorld;
import org.millenaire.common.UserProfile;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;

public class ConnectionEventHandler {

	@SubscribeEvent
	public void clientLoggedIn(ClientConnectedToServerEvent event) {
		Mill.proxy.handleClientLogin();
	}

	@SubscribeEvent
	public void connectionClosed(ServerDisconnectionFromClientEvent event) {
		//can't tell who it is in Forge right now so checking everyone

		for (final MillWorld mw : Mill.serverWorlds) {
			mw.checkConnections();
		}
	}

	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {

		try {
				final UserProfile profile=MillCommonUtilities.getServerProfile((event.player).worldObj,(event.player).getDisplayName());
				if (profile!=null) {
					profile.connectUser();
				} else {
					MLN.error(this, "Could not get profile on login for user: "+(event.player).getDisplayName());
				}
		} catch (final Exception e ) {
			MLN.printException("Error in ConnectionHandler.playerLoggedIn:", e);
		}

	}
}
