package org.millenaire.client;

import java.util.Random;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.millenaire.common.EntityWallDecoration;
import org.millenaire.common.EntityWallDecoration.EnumWallDecoration;

public class RenderWallDecoration extends Render
{
	/** RNG. */
	private final Random rand;

	public RenderWallDecoration()
	{
		rand = new Random();
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		func_158_a((EntityWallDecoration)par1Entity, par2, par4, par6, par8, par9);
	}

	public void func_158_a(EntityWallDecoration ent, double par2, double par4, double par6, float par8, float par9)
	{
		rand.setSeed(187L);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		GL11.glRotatef(par8, 0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		if (ent.type==EntityWallDecoration.NORMAN_TAPESTRY) {
			loadTexture("/graphics/art/ML_Tapestry.png");
		} else {
			loadTexture("/graphics/art/ML_Scultures.png");
		}
		final EnumWallDecoration enumart = ent.art;
		final float var11 = 0.0625F;
		GL11.glScalef(var11, var11, var11);
		this.func_77010_a(ent, enumart.sizeX, enumart.sizeY, enumart.offsetX, enumart.offsetY);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void func_160_a(EntityWallDecoration ent, float par2, float par3)
	{
		int i = MathHelper.floor_double(ent.posX);
		final int j = MathHelper.floor_double(ent.posY + (par3 / 16F));
		int k = MathHelper.floor_double(ent.posZ);

		if (ent.direction == 0)
		{
			i = MathHelper.floor_double(ent.posX + (par2 / 16F));
		}

		if (ent.direction == 1)
		{
			k = MathHelper.floor_double(ent.posZ - (par2 / 16F));
		}

		if (ent.direction == 2)
		{
			i = MathHelper.floor_double(ent.posX - (par2 / 16F));
		}

		if (ent.direction == 3)
		{
			k = MathHelper.floor_double(ent.posZ + (par2 / 16F));
		}

		final int l = renderManager.worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);
		final int i1 = l % 0x10000;
		final int j1 = l / 0x10000;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1, j1);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}

	private void func_77010_a(EntityWallDecoration par1EntityPainting, int par2, int par3, int par4, int par5)
	{
		final float var6 = (-par2) / 2.0F;
		final float var7 = (-par3) / 2.0F;
		final float var8 = -0.5F;
		final float var9 = 0.5F;

		for (int var10 = 0; var10 < (par2 / 16); ++var10)
		{
			for (int var11 = 0; var11 < (par3 / 16); ++var11)
			{
				final float var12 = var6 + ((var10 + 1) * 16);
				final float var13 = var6 + (var10 * 16);
				final float var14 = var7 + ((var11 + 1) * 16);
				final float var15 = var7 + (var11 * 16);
				this.func_160_a(par1EntityPainting, (var12 + var13) / 2.0F, (var14 + var15) / 2.0F);
				final float var16 = ((par4 + par2) - (var10 * 16)) / 256.0F;
				final float var17 = ((par4 + par2) - ((var10 + 1) * 16)) / 256.0F;
				final float var18 = ((par5 + par3) - (var11 * 16)) / 256.0F;
				final float var19 = ((par5 + par3) - ((var11 + 1) * 16)) / 256.0F;
				final float var20 = 0.75F;
				final float var21 = 0.8125F;
				final float var22 = 0.0F;
				final float var23 = 0.0625F;
				final float var24 = 0.75F;
				final float var25 = 0.8125F;
				final float var26 = 0.001953125F;
				final float var27 = 0.001953125F;
				final float var28 = 0.7519531F;
				final float var29 = 0.7519531F;
				final float var30 = 0.0F;
				final float var31 = 0.0625F;
				final Tessellator var32 = Tessellator.instance;
				var32.startDrawingQuads();
				var32.setNormal(0.0F, 0.0F, -1.0F);
				var32.addVertexWithUV(var12, var15, var8, var17, var18);
				var32.addVertexWithUV(var13, var15, var8, var16, var18);
				var32.addVertexWithUV(var13, var14, var8, var16, var19);
				var32.addVertexWithUV(var12, var14, var8, var17, var19);
				var32.setNormal(0.0F, 0.0F, 1.0F);
				var32.addVertexWithUV(var12, var14, var9, var20, var22);
				var32.addVertexWithUV(var13, var14, var9, var21, var22);
				var32.addVertexWithUV(var13, var15, var9, var21, var23);
				var32.addVertexWithUV(var12, var15, var9, var20, var23);
				var32.setNormal(0.0F, 1.0F, 0.0F);
				var32.addVertexWithUV(var12, var14, var8, var24, var26);
				var32.addVertexWithUV(var13, var14, var8, var25, var26);
				var32.addVertexWithUV(var13, var14, var9, var25, var27);
				var32.addVertexWithUV(var12, var14, var9, var24, var27);
				var32.setNormal(0.0F, -1.0F, 0.0F);
				var32.addVertexWithUV(var12, var15, var9, var24, var26);
				var32.addVertexWithUV(var13, var15, var9, var25, var26);
				var32.addVertexWithUV(var13, var15, var8, var25, var27);
				var32.addVertexWithUV(var12, var15, var8, var24, var27);
				var32.setNormal(-1.0F, 0.0F, 0.0F);
				var32.addVertexWithUV(var12, var14, var9, var29, var30);
				var32.addVertexWithUV(var12, var15, var9, var29, var31);
				var32.addVertexWithUV(var12, var15, var8, var28, var31);
				var32.addVertexWithUV(var12, var14, var8, var28, var30);
				var32.setNormal(1.0F, 0.0F, 0.0F);
				var32.addVertexWithUV(var13, var14, var8, var29, var30);
				var32.addVertexWithUV(var13, var15, var8, var29, var31);
				var32.addVertexWithUV(var13, var15, var9, var28, var31);
				var32.addVertexWithUV(var13, var14, var9, var28, var30);
				var32.draw();
			}
		}
	}
}
