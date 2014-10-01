package org.millenaire.common.forge;

import org.millenaire.common.MillWorld;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ServerTickHandler {

	@SubscribeEvent
	public void tickStart(final TickEvent.ServerTickEvent event) {

		if (Mill.startupError) {
			return;
		}

		for (final MillWorld mw : Mill.serverWorlds) {
			mw.updateWorldServer();
		}
	}
}
