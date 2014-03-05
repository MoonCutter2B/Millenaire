package org.millenaire.client;


import java.util.Vector;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.UserProfile;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.pathing.atomicstryker.AS_PathEntity;

public class RenderMillVillager extends RenderBiped {

	private static final float SCALE = 0.01666667F * 1.0F;
	private static final float LINE_HEIGHT = 0.2f;
	private static final int LINE_SIZE = 60;

	private final ModelBiped modelCloth;

	public RenderMillVillager(ModelBiped modelbiped, float f) {
		super(modelbiped, f);
		modelBipedMain = (ModelBiped)mainModel;
		if (modelbiped instanceof ModelFemaleAsymmetrical) {
			modelCloth = new ModelFemaleAsymmetrical(0.1f);
		} else if (modelbiped instanceof ModelFemaleSymmetrical) {
			modelCloth = new ModelFemaleSymmetrical(0.1f);
		} else {
			modelCloth = new ModelBiped(0.1f);
		}
	}

	private void displayText(String text,float scale, int colour, float x, float y, float z) {
		final FontRenderer fontrenderer = getFontRendererFromRenderManager();
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDepthMask(false);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glBlendFunc(770, 771);
		final Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		tessellator.startDrawingQuads();
		final int textWidth = fontrenderer.getStringWidth(text) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.8F);
		tessellator.addVertex(-textWidth - 1, -1 , 0.0D);
		tessellator.addVertex(-textWidth - 1, 8 , 0.0D);
		tessellator.addVertex(textWidth + 1, 8 , 0.0D);
		tessellator.addVertex(textWidth + 1, -1, 0.0D);
		tessellator.draw();
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, 0, colour);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
		GL11.glDepthMask(true);
		//fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, 0, -1);
		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	@Override
	public void doRender(EntityLivingBase entityliving, double d, double d1, double d2,
			float f, float f1)
	{

		final MillVillager villager = (MillVillager) entityliving;

		if (villager.isUsingBow) {
			modelCloth.aimedBow = modelBipedMain.aimedBow = true;
		}

		super.doRender(entityliving, d, d1, d2, f, f1);

		modelCloth.aimedBow = modelBipedMain.aimedBow = false;

		doRenderVillagerName(villager, d, d1, d2);
	}

	public void doRenderVillagerName(MillVillager villager, double x, double y, double z)
	{

		float villagerSize=villager.scale*2;

		if (villager.shouldLieDown) {

			final double height=(villager.boundingBox.maxY-villager.boundingBox.minY);

			final float angle=villager.getBedOrientationInDegrees();

			double dx=0,dz=0;


			if (angle==0) {
				dx=-height*0.9;
			} else if (angle==90) {
				dz=-height*0.9;
			} else if (angle==180) {
				dx=height*0.9;
			} else if (angle==270) {
				dz=height*0.9;
			}

			x=(villager.lastTickPosX-RenderManager.renderPosX)+dx;
			z=(villager.lastTickPosZ-RenderManager.renderPosZ)+dz;

			villagerSize=0.5f;
		}


		final UserProfile profile=Mill.clientWorld.getProfile(Mill.proxy.getTheSinglePlayer().getDisplayName());

		final float f4 = villager.getDistanceToEntity(renderManager.livingPlayer);

		if(f4 < MLN.VillagersNamesDistance){
			final String gameSpeech=villager.getGameSpeech(Mill.proxy.getTheSinglePlayer().getDisplayName());
			final String nativeSpeech=villager.getNativeSpeech(Mill.proxy.getTheSinglePlayer().getDisplayName());

			//gameSpeech=villager.speech_key+"_"+villager.speech_variant;
			//nativeSpeech="" +(villager.worldObj.getWorldTime()-villager.speech_started)+"/"+villager.speech_started+"/"+villager.worldObj.getWorldTime();

			float height=LINE_HEIGHT;

			if (MLN.DEV && (Mill.serverWorlds.size()>0) && Mill.serverWorlds.firstElement().villagers.containsKey(villager.villager_id) && !MLN.DEV) {

				final MillVillager dv=Mill.serverWorlds.firstElement().villagers.get(villager.villager_id);

				final AS_PathEntity pe=dv.pathEntity;

				if ((pe!=null) && (pe.pointsCopy!=null)) {
					final PathPoint[] pp=pe.pointsCopy;

					if (pp!=null) {
						if (pp.length>0) {

							String s="";
							for (int i=pe.getCurrentPathIndex();(i<pp.length) && (i<(pe.getCurrentPathIndex()+5));i++) {
								s+="("+pp[i]+") ";
							}
							displayText(s,SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z);
							height+=LINE_HEIGHT;
						}
					}

					if (pe!=null) {
						if (pe.getCurrentPathLength()>0) {

							displayText("Path: "+pe.getCurrentPathLength()+" end: "+pe.getCurrentTargetPathPoint()+" dist: "+(Math.round(villager.getPos().horizontalDistanceTo(pe.getFinalPathPoint())*10)/10)+" index: "+pe.getCurrentPathIndex()
									+" "+dv.hasPath()+" PF: "+dv.pathfailure+", stuck: "+dv.longDistanceStuck,SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z
									);
						} else {
							displayText("Empty path"+" PF: "+dv.pathfailure+", stuck: "+dv.longDistanceStuck,SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z);
						}
						height+=LINE_HEIGHT;
					}

				} else {
					displayText("Null path entity, PF: "+dv.pathfailure+", stuck: "+dv.longDistanceStuck,SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z);
					height+=LINE_HEIGHT;
				}
				if (dv.getEntityToAttack()==null) {
					displayText("Pos: "+dv.getPos()+" Path dest: "+dv.getPathDestPoint()+" Goal dest: "+dv.getGoalDestPoint()+" dist: "+(Math.round(dv.getPos().horizontalDistanceTo(dv.getPathDestPoint())*10)/10)+
							" sm: "+dv.stopMoving+" jps busy: "+dv.jpsPathPlanner.isBusy(),SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z);
				} else {
					displayText("Pos: "+dv.getPos()+" Entity: "+dv.getEntityToAttack()+" dest: "+(new Point(dv.getEntityToAttack()))+" dist: "+(Math.round(dv.getPos().horizontalDistanceTo((new Point(dv.getEntityToAttack())))*10)/10)+
							" sm: "+dv.stopMoving+" jps busy: "+dv.jpsPathPlanner.isBusy(),SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z);
				}

				height+=LINE_HEIGHT;
			}

			if (villager.hiredBy==null) {
				if (gameSpeech!=null) {
					final Vector<String> lines=new Vector<String>();
					String line=gameSpeech;
					while (line.length()>LINE_SIZE) {
						final String subLine=line.substring(0, line.lastIndexOf(' ', LINE_SIZE));
						line=line.substring(subLine.length()).trim();
						lines.add(subLine);
					}
					lines.add(line);

					for (int i=lines.size()-1;i>=0;i--) {
						displayText(lines.get(i),SCALE,0xa0DC6E7B,(float)x,(float)y + villagerSize+height, (float)z);
						height+=LINE_HEIGHT;
					}
				}
				if (nativeSpeech!=null) {
					final Vector<String> lines=new Vector<String>();
					String line=nativeSpeech;
					while (line.length()>LINE_SIZE) {
						final String subLine=line.substring(0, line.lastIndexOf(' ', LINE_SIZE));
						line=line.substring(subLine.length()).trim();
						lines.add(subLine);
					}
					lines.add(line);

					for (int i=lines.size()-1;i>=0;i--) {
						displayText(lines.get(i),SCALE,0xa0706EDC,(float)x,(float)y + villagerSize+height, (float)z);
						height+=LINE_HEIGHT;
					}
				}

				if (MLN.displayNames && Goal.goals.containsKey(villager.goalKey)) {
					displayText(Goal.goals.get(villager.goalKey).gameName(villager),SCALE,0xa0DCCA6E,(float)x,(float)y + villagerSize+height, (float)z);
					height+=LINE_HEIGHT;
				}


				if (profile.villagersInQuests.containsKey(villager.villager_id)) {
					final QuestInstance qi=profile.villagersInQuests.get(villager.villager_id);
					if (qi.getCurrentVillager().id==villager.villager_id) {
						displayText("["+qi.getLabel(profile)+"]",SCALE,0xa0dddddd,(float)x,(float)y + villagerSize+height, (float)z);
						height+=LINE_HEIGHT;
					}
				}

				if (villager.isRaider) {
					displayText(MLN.string("ui.raider"),SCALE,0xa0FF6E7B,(float)x,(float)y + villagerSize+height, (float)z);
					height+=LINE_HEIGHT;
				}
				
				if (villager.vtype.showHealth) {
					displayText(MLN.string("hire.health")+": "+(villager.getHealth()*0.5)+"/"+(villager.getMaxHealth()*0.5),SCALE,0xa0dddddd,(float)x,(float)y + villagerSize+height, (float)z);
					height+=LINE_HEIGHT;
				}

			} else if (villager.hiredBy.equals(profile.playerName)) {
				String s;
				s=MLN.string("hire.health")+": "+(villager.getHealth()*0.5)+"/"+(villager.getMaxHealth()*0.5);

				if (villager.aggressiveStance) {
					s=s+" - "+MLN.string("hire.aggressive");
				} else {
					s=s+" - "+MLN.string("hire.passive");
				}

				displayText(s,SCALE,0xa0DCCA6E,(float)x,(float)y + villagerSize+height, (float)z);
				height+=LINE_HEIGHT;

				s=MLN.string("hire.timeleft",""+Math.round((villager.hiredUntil-villager.worldObj.getWorldTime())/1000));

				displayText(s,SCALE,0xa0dddddd,(float)x,(float)y + villagerSize+height, (float)z);
				height+=LINE_HEIGHT;
			} else {
				final String s=MLN.string("hire.hiredby",villager.hiredBy);

				displayText(s,SCALE,0xa0dddddd,(float)x,(float)y + villagerSize+height, (float)z);
				height+=LINE_HEIGHT;
			}

			if (MLN.displayNames && !villager.vtype.hideName) {
				displayText(villager.getName()+", "+villager.getNativeOccupationName(),SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z);
			}

		}
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f)
	{
		preRenderScale((MillVillager)entityliving, f);
	}

	protected void preRenderScale(MillVillager villager, float f)
	{
		GL11.glScalef(villager.scale, villager.scale, villager.scale);
	}

	@Override
	protected void rotateCorpse(EntityLivingBase par1EntityLiving, float par2, float par3, float par4) {

		final MillVillager v=(MillVillager) par1EntityLiving;

		if (v.isEntityAlive() && v.isVillagerSleeping())
		{
			final float orientation=-v.getBedOrientationInDegrees();
			GL11.glRotatef(orientation, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.getDeathMaxRotation(v), 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		}
		else
		{
			super.rotateCorpse(v, par2, par3, par4);
		}
	}



	protected int setClothModel(MillVillager villager, int clothPartID, float f)
	{

		try {
			final ResourceLocation clothTexture=villager.getClothTexturePath();
			
			if (clothTexture==null) {
				modelCloth.bipedHead.showModel = false;
				modelCloth.bipedHeadwear.showModel = false;
				modelCloth.bipedBody.showModel = false;
				modelCloth.bipedRightArm.showModel = false;
				modelCloth.bipedLeftArm.showModel = false;
				modelCloth.bipedRightLeg.showModel = false;
				modelCloth.bipedLeftLeg.showModel = false;
				return -1;
			} 
			
			this.bindTexture(clothTexture);
			modelCloth.bipedHead.showModel = true;
			modelCloth.bipedHeadwear.showModel = true;
			modelCloth.bipedBody.showModel = true;
			modelCloth.bipedRightArm.showModel = true;
			modelCloth.bipedLeftArm.showModel = true;
			modelCloth.bipedRightLeg.showModel = true;
			modelCloth.bipedLeftLeg.showModel = true;
			this.setRenderPassModel(modelCloth);
			
			
           
			modelCloth.onGround = this.mainModel.onGround;
			modelCloth.isRiding = this.mainModel.isRiding;
			modelCloth.isChild = this.mainModel.isChild;
            float f1 = 1.0F;

            GL11.glColor3f(f1, f1, f1);

            return 1;

		} catch (final Exception e) {
			MLN.printException("Error when loading cloth: ",e);
		}


		return -1;
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entityliving, int i, float f)
	{
		
		final int armourRes=this.inheritRenderPass(entityliving, i, f);
		int clothRes=-1;

		if (i==0) {
			clothRes=setClothModel((MillVillager)entityliving, i, f);
		}

		if (armourRes>0)
			return armourRes;

		return clothRes;
	}
	
	
	@Override
    protected ResourceLocation getEntityTexture(EntityLiving par1EntityLiving)
    {
    	MillVillager villager=(MillVillager)par1EntityLiving;
    	
    	
        return villager.texture;
    }
}
