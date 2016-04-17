package org.millenaire.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.millenaire.common.EntityMillDecoration;
import org.millenaire.common.EntityMillDecoration.EnumWallDecoration;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWallDecoration extends Render {
	public static final ResourceLocation textureTapestries = new ResourceLocation(Mill.modId, "textures/painting/ML_Tapestry.png");
	public static final ResourceLocation textureSculptures = new ResourceLocation(Mill.modId, "textures/painting/ML_Scultures.png");

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker
	 * function which does the actual work. In all probabilty, the class Render
	 * is generic (Render<T extends Entity) and this method has signature public
	 * void doRender(T entity, double d, double d1, double d2, float f, float
	 * f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
		this.renderThePainting((EntityMillDecoration) par1Entity, par2, par4, par6, par8, par9);
	}

	private void func_77008_a(final EntityMillDecoration par1EntityPainting, final float par2, final float par3) {
		int i = MathHelper.floor_double(par1EntityPainting.posX);
		final int j = MathHelper.floor_double(par1EntityPainting.posY + par3 / 16.0F);
		int k = MathHelper.floor_double(par1EntityPainting.posZ);

		if (par1EntityPainting.hangingDirection == 2) {
			i = MathHelper.floor_double(par1EntityPainting.posX + par2 / 16.0F);
		}

		if (par1EntityPainting.hangingDirection == 1) {
			k = MathHelper.floor_double(par1EntityPainting.posZ - par2 / 16.0F);
		}

		if (par1EntityPainting.hangingDirection == 0) {
			i = MathHelper.floor_double(par1EntityPainting.posX - par2 / 16.0F);
		}

		if (par1EntityPainting.hangingDirection == 3) {
			k = MathHelper.floor_double(par1EntityPainting.posZ + par2 / 16.0F);
		}

		final int l = this.renderManager.worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);
		final int i1 = l % 65536;
		final int j1 = l / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1, j1);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}

	private void func_77010_a(final EntityMillDecoration par1EntityPainting, final int sizeX, final int sizeY, final int offsetX, final int offsetY) {
		final float f = -sizeX / 2.0F;
		final float f1 = -sizeY / 2.0F;
		final float f2 = 0.5F;
		final float f3 = 0.75F;
		final float f4 = 0.8125F;
		final float f5 = 0.0F;
		final float f6 = 0.0625F;
		final float f7 = 0.75F;
		final float f8 = 0.8125F;
		final float f9 = 0.001953125F;
		final float f10 = 0.001953125F;
		final float f11 = 0.7519531F;
		final float f12 = 0.7519531F;
		final float f13 = 0.0F;
		final float f14 = 0.0625F;

		for (int i1 = 0; i1 < sizeX / 16; ++i1) {
			for (int j1 = 0; j1 < sizeY / 16; ++j1) {
				final float f15 = f + (i1 + 1) * 16;
				final float f16 = f + i1 * 16;
				final float f17 = f1 + (j1 + 1) * 16;
				final float f18 = f1 + j1 * 16;
				this.func_77008_a(par1EntityPainting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
				final float f19 = (offsetX + sizeX - i1 * 16) / 256.0F;
				final float f20 = (offsetX + sizeX - (i1 + 1) * 16) / 256.0F;
				final float f21 = (offsetY + sizeY - j1 * 16) / 256.0F;
				final float f22 = (offsetY + sizeY - (j1 + 1) * 16) / 256.0F;
				final Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 0.0F, -1.0F);
				tessellator.addVertexWithUV(f15, f18, -f2, f20, f21);
				tessellator.addVertexWithUV(f16, f18, -f2, f19, f21);
				tessellator.addVertexWithUV(f16, f17, -f2, f19, f22);
				tessellator.addVertexWithUV(f15, f17, -f2, f20, f22);
				tessellator.setNormal(0.0F, 0.0F, 1.0F);
				tessellator.addVertexWithUV(f15, f17, f2, f3, f5);
				tessellator.addVertexWithUV(f16, f17, f2, f4, f5);
				tessellator.addVertexWithUV(f16, f18, f2, f4, f6);
				tessellator.addVertexWithUV(f15, f18, f2, f3, f6);
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV(f15, f17, -f2, f7, f9);
				tessellator.addVertexWithUV(f16, f17, -f2, f8, f9);
				tessellator.addVertexWithUV(f16, f17, f2, f8, f10);
				tessellator.addVertexWithUV(f15, f17, f2, f7, f10);
				tessellator.setNormal(0.0F, -1.0F, 0.0F);
				tessellator.addVertexWithUV(f15, f18, f2, f7, f9);
				tessellator.addVertexWithUV(f16, f18, f2, f8, f9);
				tessellator.addVertexWithUV(f16, f18, -f2, f8, f10);
				tessellator.addVertexWithUV(f15, f18, -f2, f7, f10);
				tessellator.setNormal(-1.0F, 0.0F, 0.0F);
				tessellator.addVertexWithUV(f15, f17, f2, f12, f13);
				tessellator.addVertexWithUV(f15, f18, f2, f12, f14);
				tessellator.addVertexWithUV(f15, f18, -f2, f11, f14);
				tessellator.addVertexWithUV(f15, f17, -f2, f11, f13);
				tessellator.setNormal(1.0F, 0.0F, 0.0F);
				tessellator.addVertexWithUV(f16, f17, -f2, f12, f13);
				tessellator.addVertexWithUV(f16, f18, -f2, f12, f14);
				tessellator.addVertexWithUV(f16, f18, f2, f11, f14);
				tessellator.addVertexWithUV(f16, f17, f2, f11, f13);
				tessellator.draw();
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(final Entity entity) {
		final EntityMillDecoration ent = (EntityMillDecoration) entity;
		if (ent.type == EntityMillDecoration.NORMAN_TAPESTRY) {
			return textureTapestries;
		} else {
			return textureSculptures;
		}
	}

	public void renderThePainting(final EntityMillDecoration ent, final double par2, final double par4, final double par6, final float par8, final float par9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) par2, (float) par4, (float) par6);
		GL11.glRotatef(par8, 0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		if (ent.type == EntityMillDecoration.NORMAN_TAPESTRY) {
			bindTexture(textureTapestries);
		} else {
			bindTexture(textureSculptures);
		}
		final EnumWallDecoration enumart = ent.millArt;
		final float f2 = 0.0625F;
		GL11.glScalef(f2, f2, f2);
		this.func_77010_a(ent, enumart.sizeX, enumart.sizeY, enumart.offsetX, enumart.offsetY);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}
