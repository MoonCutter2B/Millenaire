package org.millenaire.client;


import java.lang.reflect.Field;
import java.util.Vector;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraftforge.client.ForgeHooksClient;

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

	private final ModelBiped modelArmorChestplate;
	private final ModelBiped modelArmor;
	private final ModelBiped modelCloth;

	public RenderMillVillager(ModelBiped modelbiped, float f) {
		super(modelbiped, f);
		modelBipedMain = (ModelBiped)mainModel;
		modelArmorChestplate = new ModelBiped(1.0F);
		modelArmor = new ModelBiped(0.5F);
		if (modelbiped instanceof ModelFemaleAsymmetrical) {
			modelCloth = new ModelFemaleAsymmetrical(0);
		} else if (modelbiped instanceof ModelFemaleSymmetrical) {
			modelCloth = new ModelFemaleSymmetrical(0);
		} else {
			modelCloth = new ModelBiped(0);
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
	public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2,
			float f, float f1)
	{

		final MillVillager villager = (MillVillager) entityliving;

		if (villager.isUsingBow) {
			modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = true;
		}


		super.doRenderLiving(entityliving, d, d1, d2, f, f1);

		modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = false;

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


		final UserProfile profile=Mill.clientWorld.getProfile(Mill.proxy.getTheSinglePlayer().username);

		final float f4 = villager.getDistanceToEntity(renderManager.livingPlayer);

		if(f4 < MLN.VillagersNamesDistance){
			final String gameSpeech=villager.getGameSpeech(Mill.proxy.getTheSinglePlayer().username);
			final String nativeSpeech=villager.getNativeSpeech(Mill.proxy.getTheSinglePlayer().username);

			//gameSpeech=villager.speech_key+"_"+villager.speech_variant;
			//nativeSpeech="" +(villager.worldObj.getWorldTime()-villager.speech_started)+"/"+villager.speech_started+"/"+villager.worldObj.getWorldTime();

			float height=LINE_HEIGHT;

			if (MLN.DEV && (Mill.serverWorlds.size()>0) && Mill.serverWorlds.firstElement().villagers.containsKey(villager.villager_id)) {

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

			if (MLN.displayNames) {
				displayText(villager.getName()+", "+villager.getNativeOccupationName(),SCALE,0xa0ffffff,(float)x,(float)y + villagerSize+height, (float)z);
			}

		}
	}

	@Override
	protected void preRenderCallback(EntityLiving entityliving, float f)
	{
		preRenderScale((MillVillager)entityliving, f);
	}

	protected void preRenderScale(MillVillager villager, float f)
	{
		GL11.glScalef(villager.scale, villager.scale, villager.scale);
	}

	@Override
	protected void rotateCorpse(EntityLiving par1EntityLiving, float par2, float par3, float par4) {

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


	protected int setArmorModel(MillVillager villager, int armourPartId, float f)
	{
		final ItemStack itemstack = villager.getArmourPiece(armourPartId);
		final Field field_armorList = (net.minecraft.client.renderer.entity.RenderPlayer.class).getDeclaredFields()[3];
		field_armorList.setAccessible(true);
		try {

			final String[] armorPrefixes=(String[]) field_armorList.get(null);

			if (itemstack != null)
			{
				final Item var5 = itemstack.getItem();

				if (var5 instanceof ItemArmor)
				{
					final ItemArmor var6 = (ItemArmor)var5;
					this.loadTexture(ForgeHooksClient.getArmorTexture(itemstack, "/armor/" + armorPrefixes[var6.renderIndex] + "_" + (armourPartId == 2 ? 2 : 1) + ".png"));
					final ModelBiped var7 = armourPartId == 2 ? this.modelArmor : this.modelArmorChestplate;
					var7.bipedHead.showModel = armourPartId == 0;
					var7.bipedHeadwear.showModel = armourPartId == 0;
					var7.bipedBody.showModel = (armourPartId == 1) || (armourPartId == 2);
					var7.bipedRightArm.showModel = armourPartId == 1;
					var7.bipedLeftArm.showModel = armourPartId == 1;
					var7.bipedRightLeg.showModel = (armourPartId == 2) || (armourPartId == 3);
					var7.bipedLeftLeg.showModel = (armourPartId == 2) || (armourPartId == 3);
					this.setRenderPassModel(var7);

					if (var7 != null)
					{
						var7.onGround = this.mainModel.onGround;
					}

					if (var7 != null)
					{
						var7.isRiding = this.mainModel.isRiding;
					}

					if (var7 != null)
					{
						var7.isChild = this.mainModel.isChild;
					}

					final float var8 = 1.0F;

					if (var6.getArmorMaterial() == EnumArmorMaterial.CLOTH)
					{
						final int var9 = var6.getColor(itemstack);
						final float var10 = ((var9 >> 16) & 255) / 255.0F;
						final float var11 = ((var9 >> 8) & 255) / 255.0F;
						final float var12 = (var9 & 255) / 255.0F;
						GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);

						if (itemstack.isItemEnchanted())
							return 31;

						return 16;
					}

					GL11.glColor3f(var8, var8, var8);

					if (itemstack.isItemEnchanted())
						return 15;

					return 1;
				}
			}
		} catch (final Exception e) {
			MLN.printException("Error when loading armour: ",e);
		}


		return -1;
	}

	protected int setClothModel(MillVillager villager, int clothPartID, float f)
	{

		try {
			final String clothTexture=villager.getClothTexturePath();

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

			this.loadTexture(clothTexture);

			modelCloth.bipedHead.showModel = true;
			modelCloth.bipedHeadwear.showModel = true;
			modelCloth.bipedBody.showModel = true;
			modelCloth.bipedRightArm.showModel = true;
			modelCloth.bipedLeftArm.showModel = true;
			modelCloth.bipedRightLeg.showModel = true;
			modelCloth.bipedLeftLeg.showModel = true;
			this.setRenderPassModel(modelCloth);

			if (modelCloth != null)
			{
				modelCloth.onGround = this.mainModel.onGround;
			}

			if (modelCloth != null)
			{
				modelCloth.isRiding = this.mainModel.isRiding;
			}

			if (modelCloth != null)
			{
				modelCloth.isChild = this.mainModel.isChild;
			}

			final float var8 = 1.0F;

			GL11.glColor3f(var8, var8, var8);

			return 1;

		} catch (final Exception e) {
			MLN.printException("Error when loading armour: ",e);
		}


		return -1;
	}

	@Override
	protected int shouldRenderPass(EntityLiving entityliving, int i, float f)
	{

		final int armourRes=setArmorModel((MillVillager)entityliving, i, f);
		int clothRes=-1;

		if (i==0) {
			clothRes=setClothModel((MillVillager)entityliving, i, f);
		}

		if (armourRes>0)
			return armourRes;

		return clothRes;
	}
}
