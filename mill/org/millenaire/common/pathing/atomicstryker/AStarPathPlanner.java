package org.millenaire.common.pathing.atomicstryker;

import java.lang.Thread.State;
import java.util.ArrayList;

import net.minecraft.world.World;

import org.millenaire.common.MLN;

/**
 * Control Class for AstarPath, creates workers and manages returns
 * 
 * @author AtomicStryker
 */

public class AStarPathPlanner
{
	private AStarWorker worker;
	private final World worldObj;
	private final IAStarPathedEntity pathedEntity;
	private boolean accesslock;
	private boolean isJPS;
	private AStarNode lastStart;
	private AStarNode lastEnd;
	public AStarConfig config;

	public AStarPathPlanner(World world, IAStarPathedEntity ent)
	{
		worker = new AStarWorker(this);
		worldObj = world;
		accesslock = false;
		pathedEntity = ent;
		isJPS = true;
	}

	private void flushWorker()
	{
		if (!accesslock) // only flush if we arent starting!
		{
			worker = isJPS ? new AStarWorkerJPS(this) : new AStarWorker(this);
		}
	}

	public void getPath(AStarNode start, AStarNode end, AStarConfig config)
	{
		if (isBusy())
		{
			stopPathSearch(true);
		}

		while (accesslock) { Thread.yield(); }
		flushWorker();
		accesslock = true;

		lastStart = start;
		lastEnd = end;

		this.config=config;

		worker.setup(worldObj, start, end, config.allowDropping);
		try {
			worker.isRunning=true;
			worker.start();
		} catch (final Exception e) {
			MLN.printException(e);
			//DO nothing - pathing occasionaly has threading errors we don't care about
		}
		accesslock = false;
	}

	public void getPath(int startx, int starty, int startz,
			int destx, int desty, int destz, AStarConfig config)
	{

		if (!AStarStatic.isViable(worldObj, startx, starty, startz, 0, config))
		{
			starty--;
		}
		if (!AStarStatic.isViable(worldObj, startx, starty, startz, 0, config))
		{
			starty+=2;
		}
		if (!AStarStatic.isViable(worldObj, startx, starty, startz, 0, config))
		{
			starty--;
		}

		final AStarNode starter = new AStarNode(startx, starty, startz, 0, null);
		final AStarNode finish = new AStarNode(destx, desty, destz, -1, null);

		getPath(starter, finish, config);
	}

	//Kinniken
	//Rewrote isBusy to use the thread's state directly
	public boolean isBusy()
	{
		final State state = worker.getState();

		final boolean busy=!(state.equals(State.NEW) || state.equals(State.TERMINATED));

		return busy;
	}

	public void onFoundPath(ArrayList<AStarNode> result)
	{
		setJPS(true);
		if (pathedEntity != null)
		{
			pathedEntity.onFoundPath(result);
		}
	}

	public void onNoPathAvailable()
	{
		if (isJPS) // in case of JPS failure switch to old best first algorithm
		{
			setJPS(false);
			getPath(lastStart, lastEnd, config);
			return;
		}

		if (pathedEntity != null)
		{
			pathedEntity.onNoPathAvailable();
		}
	}

	public void setJPS(boolean b)
	{
		isJPS = b;
		flushWorker();
	}

	public void stopPathSearch(boolean interrupted)
	{
		flushWorker();
		if ((pathedEntity != null) && !interrupted)
		{
			pathedEntity.onNoPathAvailable();
		}
	}
}