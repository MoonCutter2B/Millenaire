package org.millenaire.client.forge;

import java.util.EnumSet;

import net.minecraft.src.ModLoader;

import org.millenaire.client.gui.DisplayActions;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler
{
	private final EnumSet<TickType> ticksToGet;

	private boolean startupMessageShow;

	/*
	 * This Tick Handler will fire for whatever TickType's you construct and register it with.
	 */
	public ClientTickHandler(EnumSet<TickType> ticksToGet)
	{
		this.ticksToGet = ticksToGet;
	}

	@Override
	public String getLabel()
	{
		return "MillenaireClientTick";
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return ticksToGet;
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if ((Mill.clientWorld==null) || !Mill.clientWorld.millenaireEnabled || (ModLoader.getMinecraftInstance().thePlayer==null))
			return;

		final boolean onSurface=(ModLoader.getMinecraftInstance().thePlayer.dimension==0);

		Mill.clientWorld.updateWorldClient(onSurface);


		if (!startupMessageShow) {
			DisplayActions.displayStartupOrError(ModLoader.getMinecraftInstance().thePlayer,Mill.startupError);
			startupMessageShow=true;
		}

		Mill.proxy.handleClientGameUpdate();
	}
}
