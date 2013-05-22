package org.millenaire.common.entity;

import org.millenaire.common.Point;

import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityTargetedBlaze extends EntityBlaze {

	public Point target=null;

	public EntityTargetedBlaze(World par1World) {
		super(par1World);
	}

	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	protected void updateEntityActionState()
	{

		if (target!=null && target.distanceTo(this)>20) {
			
			double d0 = this.target.x - this.posX;
	        double d1 = this.target.y - this.posY;
	        double d2 = this.target.z - this.posZ;
	        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
			
			if (this.isCourseTraversable(target.x, target.y, target.z, d3))
			{
				this.motionX += d0 / d3 * 0.1D;
				this.motionY += d1 / d3 * 0.1D;
				this.motionZ += d2 / d3 * 0.1D;
			}
		}

		super.updateEntityActionState();
	}
	
	 @Override
	public boolean isWet() {
		return false;//prevent damages from rain
	}

	private boolean isCourseTraversable(double par1, double par3, double par5, double par7)
	    {
	        double d4 = (this.target.x - this.posX) / par7;
	        double d5 = (this.target.y - this.posY) / par7;
	        double d6 = (this.target.z - this.posZ) / par7;
	        AxisAlignedBB axisalignedbb = this.boundingBox.copy();

	        for (int i = 1; (double)i < par7; ++i)
	        {
	            axisalignedbb.offset(d4, d5, d6);

	            if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty())
	            {
	                return false;
	            }
	        }

	        return true;
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
