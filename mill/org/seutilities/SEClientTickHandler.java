package org.seutilities;

import java.util.EnumSet;
import java.util.List;
import java.util.Vector;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.millenaire.common.MLN;
import org.millenaire.common.core.MillCommonUtilities;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class SEClientTickHandler implements ITickHandler
{
	private final EnumSet<TickType> ticksToGet;

	private final Vector<EntityMob> mobs=new Vector<EntityMob>();

	/*
	 * This Tick Handler will fire for whatever TickType's you construct and register it with.
	 */
	public SEClientTickHandler(EnumSet<TickType> ticksToGet)
	{
		this.ticksToGet = ticksToGet;
	}

	@Override
	public String getLabel()
	{
		return "SEClientTick";
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

		final World world = Minecraft.getMinecraft().theWorld;

		if (world==null)
			return;


		@SuppressWarnings("rawtypes")
		final
		List entities=world.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(455, 36, -100, (455 + 1), (36 + 1), (-100 + 1)).expand(256D, 256D, 256D));

		for (final Object o : entities) {

			final EntityMob mob=(EntityMob)o;

			if (!mobs.contains(mob)) {

				if (mob.posY< 60) {

					int dist=(int) (((455-mob.posX)*(455-mob.posX))+((36-mob.posY)*(36-mob.posY))+((-100-mob.posZ)*(-100-mob.posZ)));

					dist=(int) Math.sqrt(dist);

					MLN.major(null, "New mob "+mob+" at "+(int)mob.posX+"/"+(int)mob.posY+"/"+(int)mob.posZ+" dist: "+dist);
				}
				mobs.add(mob);
			}
		}

		if (MillCommonUtilities.chanceOn(500)) {

			final EntityPlayer player=Minecraft.getMinecraft().thePlayer;

			if (player!=null) {

				int dist=(int) (((455-player.posX)*(455-player.posX))+((36-player.posY)*(36-player.posY))+((-100-player.posZ)*(-100-player.posZ)));

				dist=(int) Math.sqrt(dist);

				MLN.major(null, "Distance to trap: "+dist);
			}

		}

	}
}
