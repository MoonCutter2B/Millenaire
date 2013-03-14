package org.millenaire.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;

import org.lwjgl.opengl.GL11;
import org.millenaire.common.MLN;

public class TileEntityMillChestRenderer extends TileEntitySpecialRenderer
{
	public class ModelLargeLockedChest extends ModelLockedChest
	{
		public ModelLargeLockedChest()
		{
			chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
			chestLid.addBox(0.0F, -5F, -14F, 30, 5, 14, 0.0F);
			chestLid.rotationPointX = 1.0F;
			chestLid.rotationPointY = 7F;
			chestLid.rotationPointZ = 15F;
			chestKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
			chestKnob.addBox(-2F, -2F, -15F, 4, 4, 1, 0.0F);
			chestKnob.rotationPointX = 16F;
			chestKnob.rotationPointY = 7F;
			chestKnob.rotationPointZ = 15F;
			chestBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64);
			chestBelow.addBox(0.0F, 0.0F, 0.0F, 30, 10, 14, 0.0F);
			chestBelow.rotationPointX = 1.0F;
			chestBelow.rotationPointY = 6F;
			chestBelow.rotationPointZ = 1.0F;
		}
	}
	private class ModelLockedChest extends ModelBase
	{
		public ModelRenderer chestLid;
		public ModelRenderer chestBelow;
		public ModelRenderer chestKnob;

		public ModelLockedChest()
		{
			chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
			chestLid.addBox(0.0F, -5F, -14F, 14, 5, 14, 0.0F);
			chestLid.rotationPointX = 1.0F;
			chestLid.rotationPointY = 7F;
			chestLid.rotationPointZ = 15F;
			chestKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
			chestKnob.addBox(-2F, -2F, -15F, 4, 4, 1, 0.0F);
			chestKnob.rotationPointX = 8F;
			chestKnob.rotationPointY = 7F;
			chestKnob.rotationPointZ = 15F;
			chestBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
			chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
			chestBelow.rotationPointX = 1.0F;
			chestBelow.rotationPointY = 6F;
			chestBelow.rotationPointZ = 1.0F;
		}

		public void func_35402_a()
		{
			chestKnob.rotateAngleX = chestLid.rotateAngleX;
			chestLid.render(0.0625F);
			chestKnob.render(0.0625F);
			chestBelow.render(0.0625F);
		}
	}

	private final ModelLockedChest field_35377_b;

	private final ModelLockedChest field_35378_c;

	public TileEntityMillChestRenderer()
	{
		field_35377_b = new ModelLockedChest();
		field_35378_c = new ModelLargeLockedChest();
	}

	public void func_35376_a(TileEntityChest tileentitychest, double d, double d1, double d2,
			float f)
	{
		int i;
		if (tileentitychest.worldObj == null)
		{
			i = 0;
		}
		else
		{
			final Block block = tileentitychest.getBlockType();
			i = tileentitychest.getBlockMetadata();
			if ((block != null) && (i == 0) && (block instanceof BlockChest))
			{
				((BlockChest)block).unifyAdjacentChests(tileentitychest.worldObj, tileentitychest.xCoord, tileentitychest.yCoord, tileentitychest.zCoord);
				i = tileentitychest.getBlockMetadata();
			}
			tileentitychest.checkForAdjacentChests();
		}
		if ((tileentitychest.adjacentChestZNeg != null) || (tileentitychest.adjacentChestXNeg != null))
			return;
		ModelLockedChest modelchest;
		if ((tileentitychest.adjacentChestXPos != null) || (tileentitychest.adjacentChestZPosition != null))
		{
			modelchest = field_35378_c;
			bindTextureByName(MLN.getLargeLockedChestTexture());
		}
		else
		{
			modelchest = field_35377_b;
			bindTextureByName(MLN.getLockedChestTexture());
		}
		GL11.glPushMatrix();
		GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)d, (float)d1 + 1.0F, (float)d2 + 1.0F);
		GL11.glScalef(1.0F, -1F, -1F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		int j = 0;
		if (i == 2)
		{
			j = 180;
		}
		if (i == 3)
		{
			j = 0;
		}
		if (i == 4)
		{
			j = 90;
		}
		if (i == 5)
		{
			j = -90;
		}
		if ((i == 2) && (tileentitychest.adjacentChestXPos != null))
		{
			GL11.glTranslatef(1.0F, 0.0F, 0.0F);
		}
		if ((i == 5) && (tileentitychest.adjacentChestZPosition != null))
		{
			GL11.glTranslatef(0.0F, 0.0F, -1F);
		}
		GL11.glRotatef(j, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		float f1 = tileentitychest.prevLidAngle + ((tileentitychest.lidAngle - tileentitychest.prevLidAngle) * f);
		if (tileentitychest.adjacentChestZNeg != null)
		{
			final float f2 = tileentitychest.adjacentChestZNeg.prevLidAngle + ((tileentitychest.adjacentChestZNeg.lidAngle - tileentitychest.adjacentChestZNeg.prevLidAngle) * f);
			if (f2 > f1)
			{
				f1 = f2;
			}
		}
		if (tileentitychest.adjacentChestXNeg != null)
		{
			final float f3 = tileentitychest.adjacentChestXNeg.prevLidAngle + ((tileentitychest.adjacentChestXNeg.lidAngle - tileentitychest.adjacentChestXNeg.prevLidAngle) * f);
			if (f3 > f1)
			{
				f1 = f3;
			}
		}
		f1 = 1.0F - f1;
		f1 = 1.0F - (f1 * f1 * f1);
		modelchest.chestLid.rotateAngleX = -((f1 * 3.141593F) / 2.0F);
		modelchest.func_35402_a();
		GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2,
			float f)
	{
		func_35376_a((TileEntityChest)tileentity, d, d1, d2, f);
	}
}
