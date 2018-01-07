package org.millenaire.rendering;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFemaleSym extends ModelBiped 
{
	public ModelRenderer bipedUpperBody;
	public ModelRenderer bipedBreast;

	public boolean heldItemLeft;
	public boolean heldItemRight;

	public ModelFemaleSym() { this(0.0F); }

	public ModelFemaleSym(final float f) { this(f, 0.0F); }

	public ModelFemaleSym(final float f, final float f1) 
	{
		heldItemLeft = false;
		heldItemRight = false;
		bipedHead = new ModelRenderer(this, 0, 0);
		bipedHead.addBox(-4F, -8F, -4F, 8, 8, 8, f);
		bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		bipedHeadwear = new ModelRenderer(this, 32, 0);
		bipedHeadwear.addBox(-4F, -8F, -4F, 8, 8, 8, f + 0.5F);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		bipedUpperBody = new ModelRenderer(this, 18, 17);
		bipedUpperBody.addBox(-3.5F, 0.0F, -1.5F, 7, 12, 3, f);
		bipedUpperBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		bipedBreast = new ModelRenderer(this, 18, 18);
		bipedBreast.addBox(-3.5F, 0.75F, -3F, 7, 4, 2, f);
		bipedBreast.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		bipedRightArm = new ModelRenderer(this, 40, 17);
		bipedRightArm.addBox(-1.5F, -2F, -1.5F, 3, 12, 4, f);
		bipedRightArm.setRotationPoint(-5F, 2.0F + f1, 0.0F);
		bipedLeftArm = new ModelRenderer(this, 40, 17);
		bipedLeftArm.mirror = true;
		bipedLeftArm.addBox(-1.5F, -2F, -1.5F, 3, 12, 4, f);
		bipedLeftArm.setRotationPoint(5F, 2.0F + f1, 0.0F);
		bipedRightLeg = new ModelRenderer(this, 0, 16);
		bipedRightLeg.addBox(-2F, 0.0F, -2F, 4, 12, 4, f);
		bipedRightLeg.setRotationPoint(-2F, 12F + f1, 0.0F);
		bipedLeftLeg = new ModelRenderer(this, 0, 16);
		bipedLeftLeg.mirror = true;
		bipedLeftLeg.addBox(-2F, 0.0F, -2F, 4, 12, 4, f);
		bipedLeftLeg.setRotationPoint(2.0F, 12F + f1, 0.0F);
	}

	@Override
	public void render(final Entity par1Entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) 
	{
		setRotationAngles(f, f1, f2, f3, f4, f5, par1Entity);
		bipedHead.render(f5);
		bipedUpperBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
		bipedRightLeg.render(f5);
		bipedLeftLeg.render(f5);
		bipedHeadwear.render(f5);
		bipedBreast.render(f5);
	}

/*	@Override
	public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5, final Entity par7Entity) {
		bipedHead.rotateAngleY = f3 / 57.29578F;
		bipedHead.rotateAngleX = f4 / 57.29578F;
		bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
		bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
		bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 2.0F * f1 * 0.5F;
		bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
		bipedRightArm.rotateAngleZ = 0.0F;
		bipedLeftArm.rotateAngleZ = 0.0F;
		bipedRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
		bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1;
		bipedRightLeg.rotateAngleY = 0.0F;
		bipedLeftLeg.rotateAngleY = 0.0F;
		if (isRiding) {
			bipedRightArm.rotateAngleX += -0.6283185F;
			bipedLeftArm.rotateAngleX += -0.6283185F;
			bipedRightLeg.rotateAngleX = -1.256637F;
			bipedLeftLeg.rotateAngleX = -1.256637F;
			bipedRightLeg.rotateAngleY = 0.3141593F;
			bipedLeftLeg.rotateAngleY = -0.3141593F;
		}
		if (heldItemLeft) {
			bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - 0.3141593F;
		}
		if (heldItemRight) {
			bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - 0.3141593F;
		}
		bipedRightArm.rotateAngleY = 0.0F;
		bipedLeftArm.rotateAngleY = 0.0F;
		if (onGround > -9990F) {
			float f6 = onGround;
			bipedUpperBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * 3.141593F * 2.0F) * 0.2F;
			bipedRightArm.rotationPointZ = MathHelper.sin(bipedUpperBody.rotateAngleY) * 5F;
			bipedRightArm.rotationPointX = -MathHelper.cos(bipedUpperBody.rotateAngleY) * 5F;
			bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedUpperBody.rotateAngleY) * 5F;
			bipedLeftArm.rotationPointX = MathHelper.cos(bipedUpperBody.rotateAngleY) * 5F;
			bipedRightArm.rotateAngleY += bipedUpperBody.rotateAngleY;
			bipedLeftArm.rotateAngleY += bipedUpperBody.rotateAngleY;
			bipedLeftArm.rotateAngleX += bipedUpperBody.rotateAngleY;
			f6 = 1.0F - onGround;
			f6 *= f6;
			f6 *= f6;
			f6 = 1.0F - f6;
			final float f7 = MathHelper.sin(f6 * 3.141593F);
			final float f8 = MathHelper.sin(onGround * 3.141593F) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
			bipedRightArm.rotateAngleX -= f7 * 1.2D + f8;
			bipedRightArm.rotateAngleY += bipedUpperBody.rotateAngleY * 2.0F;
			bipedRightArm.rotateAngleZ = MathHelper.sin(onGround * 3.141593F) * -0.4F;
		}
		if (isSneak) {
			bipedUpperBody.rotateAngleX = 0.5F;
			bipedRightLeg.rotateAngleX -= 0.0F;
			bipedLeftLeg.rotateAngleX -= 0.0F;
			bipedRightArm.rotateAngleX += 0.4F;
			bipedLeftArm.rotateAngleX += 0.4F;
			bipedRightLeg.rotationPointZ = 4F;
			bipedLeftLeg.rotationPointZ = 4F;
			bipedRightLeg.rotationPointY = 9F;
			bipedLeftLeg.rotationPointY = 9F;
			bipedHead.rotationPointY = 1.0F;
		} else {
			bipedUpperBody.rotateAngleX = 0.0F;
			bipedRightLeg.rotationPointZ = 0.0F;
			bipedLeftLeg.rotationPointZ = 0.0F;
			bipedRightLeg.rotationPointY = 12F;
			bipedLeftLeg.rotationPointY = 12F;
			bipedHead.rotationPointY = 0.0F;
		}
		bipedRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
	}*/
}
