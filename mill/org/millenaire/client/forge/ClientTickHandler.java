package org.millenaire.client.forge;

import net.minecraft.client.Minecraft;

import org.millenaire.client.gui.DisplayActions;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientTickHandler {

	private boolean startupMessageShow;


	@SubscribeEvent
	public void tickStart(TickEvent.ClientTickEvent event)
	{
		if ((Mill.clientWorld==null) || !Mill.clientWorld.millenaireEnabled || (Minecraft.getMinecraft().thePlayer==null))
			return;

		final boolean onSurface=(Minecraft.getMinecraft().thePlayer.dimension==0);

		Mill.clientWorld.updateWorldClient(onSurface);


		if (!startupMessageShow) {
			DisplayActions.displayStartupOrError(Minecraft.getMinecraft().thePlayer,Mill.startupError);
			startupMessageShow=true;
		}

		Mill.proxy.handleClientGameUpdate();
	}
}
