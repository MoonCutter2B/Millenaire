package org.millenaire.rendering;

import org.lwjgl.opengl.GL11;
import org.millenaire.entities.EntityMillVillager;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderMillVillager extends RenderBiped<EntityMillVillager>
{
	protected ResourceLocation villagerTexture;
	
	protected String name = "Suzy Carmichael";
	protected String quest = null;
	
	public RenderMillVillager(RenderManager rendermanagerIn, ModelBiped modelbaseIn, float shadowsizeIn) 
	{
		super(rendermanagerIn, modelbaseIn, shadowsizeIn);
		this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
	}
	
	@Override
	protected boolean canRenderName(EntityMillVillager entity)
    {
        return true;
    }
	
	@Override
    protected void preRenderCallback(EntityMillVillager entity, float f)
    {
		villagerTexture = new ResourceLocation(entity.getTexture());
		name = entity.getName();
		//if(entity.isChild())
		//	GL11.glScalef(0.6F, 0.5F, 0.6F);
		if(!entity.isChild())
		{
			if(entity.getGender() == 1)
			{
				this.mainModel = new ModelFemaleAsym();
			}
			else if(entity.getGender() == 2)
			{
				this.mainModel = new ModelFemaleSym();
			}
			else
			{
				this.mainModel = new ModelBiped();
			}
    	}
		else
		{
			this.mainModel = new ModelBiped();
		}
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityMillVillager entity) 
	{
		return villagerTexture;
	}
	
	@Override
	protected void rotateCorpse(EntityMillVillager entityIn, float par2, float par3, float partialTicks) 
	{
		/*if (entityIn.isEntityAlive() && entityIn.isVillagerSleeping()) 
		{
			final float orientation = -entityIn.getBedOrientationInDegrees();
			GL11.glRotatef(orientation, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.getDeathMaxRotation(entityIn), 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} 
		else 
		{*/
			super.rotateCorpse(entityIn, par2, par3, partialTicks);
		//}
	}
	
	@Override
	protected void renderOffsetLivingLabel(EntityMillVillager entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_)
    {
		float scale = 0.01666667F;
		float lineHeight = 0.2f;
		
		if(quest != null)
		{
			displayText(quest, scale, 11184810, x, y + entityIn.height + 0.5F, z);
			y += lineHeight;
		}
		displayText(name, scale, 16777215, x, y + entityIn.height + 0.5F, z);
    }
	
	private void displayText(String text, float scale, int color, double x, double y, double z) 
	{
		final FontRenderer fontrenderer = getFontRendererFromRenderManager();
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDepthMask(false);
		GL11.glDisable(2929 /* GL_DEPTH_TEST */);
		GL11.glEnable(3042 /* GL_BLEND */);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		GL11.glDisable(3553 /* GL_TEXTURE_2D */);
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		int textWidth = fontrenderer.getStringWidth(text) / 2;
		renderer.pos(-textWidth - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.8F).endVertex();
		renderer.pos(-textWidth - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.8F).endVertex();
		renderer.pos(textWidth + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.8F).endVertex();
		renderer.pos(textWidth + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.8F).endVertex();
		tessellator.draw();
		GL11.glEnable(3553 /* GL_TEXTURE_2D */);
		fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, 0, color);
		GL11.glEnable(2929 /* GL_DEPTH_TEST */);
		GL11.glDepthMask(true);
		fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, 0, color);
		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glDisable(3042 /* GL_BLEND */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static class millVillagerRenderFactory implements IRenderFactory
	{

		@Override
		public Render createRenderFor(RenderManager manager) 
		{
			return new RenderMillVillager(manager, new ModelBiped(), 0.5F);
		}
		
	}
}
