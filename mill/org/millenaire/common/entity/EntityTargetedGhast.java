package org.millenaire.common.entity;

import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.millenaire.common.Point;

public class EntityTargetedGhast extends EntityGhast {

	public Point target=null;

	public EntityTargetedGhast(World par1World) {
		super(par1World);
	}

	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	protected void updateEntityActionState()
	{
		if (target!=null) {

			if (target.distanceTo(this)>20) {
				waypointX=target.x;
				waypointY=target.y;
				waypointZ=target.z;
				courseChangeCooldown=0; 
			} else if (target.distanceTo(this)<10) {
				this.waypointX = this.posX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
				this.waypointY = this.posY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
				this.waypointZ = this.posZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
				courseChangeCooldown=0; 
			}
		}

		super.updateEntityActionState();
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		
		super.writeToNBT(par1nbtTagCompound);
		
		if (target!=null)
			target.write(par1nbtTagCompound, "targetPoint");
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		
		super.readFromNBT(par1nbtTagCompound);
		
		target=Point.read(par1nbtTagCompound, "targetPoint");
	}
}
