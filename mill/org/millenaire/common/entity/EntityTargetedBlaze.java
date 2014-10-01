package org.millenaire.common.entity;

import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.millenaire.common.Point;

public class EntityTargetedBlaze extends EntityBlaze {

	public Point target = null;

	public EntityTargetedBlaze(final World par1World) {
		super(par1World);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	private boolean isCourseTraversable(final double par1, final double par3,
			final double par5, final double par7) {
		final double d4 = (this.target.x - this.posX) / par7;
		final double d5 = (this.target.y - this.posY) / par7;
		final double d6 = (this.target.z - this.posZ) / par7;
		final AxisAlignedBB axisalignedbb = this.boundingBox.copy();

		for (int i = 1; i < par7; ++i) {
			axisalignedbb.offset(d4, d5, d6);

			if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb)
					.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isWet() {
		return false;// prevent damages from rain
	}

	@Override
	public void readFromNBT(final NBTTagCompound par1nbtTagCompound) {

		super.readFromNBT(par1nbtTagCompound);

		target = Point.read(par1nbtTagCompound, "targetPoint");
	}

	@Override
	protected void updateEntityActionState() {

		if (target != null && target.distanceTo(this) > 20) {

			final double d0 = this.target.x - this.posX;
			final double d1 = this.target.y - this.posY;
			final double d2 = this.target.z - this.posZ;
			final double d3 = d0 * d0 + d1 * d1 + d2 * d2;

			if (this.isCourseTraversable(target.x, target.y, target.z, d3)) {
				this.motionX += d0 / d3 * 0.1D;
				this.motionY += d1 / d3 * 0.1D;
				this.motionZ += d2 / d3 * 0.1D;
			}
		}

		super.updateEntityActionState();
	}

	@Override
	public void writeToNBT(final NBTTagCompound par1nbtTagCompound) {

		super.writeToNBT(par1nbtTagCompound);

		if (target != null) {
			target.write(par1nbtTagCompound, "targetPoint");
		}
	}
}
