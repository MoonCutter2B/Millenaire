package org.millenaire.common.forge;

import java.util.EnumSet;

import org.millenaire.common.MillWorld;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler
{
	private final EnumSet<TickType> ticksToGet;

	public ServerTickHandler(EnumSet<TickType> ticksToGet)
	{
		this.ticksToGet = ticksToGet;
	}

	@Override
	public String getLabel()
	{
		return "MillenaireServerTick";
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
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

		if (Mill.startupError)
			return;

		for (final MillWorld mw : Mill.serverWorlds) {
			mw.updateWorldServer();
		}
	}
}

