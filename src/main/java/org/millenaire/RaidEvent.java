package org.millenaire;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RaidEvent {
	public RaidEvent() {

	}

	////////////////////////////////////////////////////////// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	public static class RaidEventHandler {
		@SubscribeEvent(priority = EventPriority.NORMAL)
		public void onServerTick(TickEvent.ServerTickEvent event) {

			// Call proper command here
		}
	}
}
