package org.millenaire.client.texture;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;

public class TextureAmuletVishnu extends TextureStitched {

	private static final int radius=20;
	
	public TextureAmuletVishnu(String iconName) {
		super(iconName);
	}

	public void func_94219_l()
	{

		int iconPos=getScore(Minecraft.getMinecraft());

		if (iconPos != this.field_94222_f)
		{
			this.field_94222_f = iconPos;
			this.field_94228_a.func_94281_a(this.field_94224_d, this.field_94225_e, (Texture)this.field_94226_b.get(this.field_94222_f), this.field_94227_c);
		}
	}
	
	private int getScore(Minecraft mc) {
		double level=0;

		double closestDistance=Double.MAX_VALUE;

		if((mc.theWorld != null) && (mc.thePlayer != null)) {

			final World world=mc.theWorld;

			final Point p=new Point(mc.thePlayer);

			final List<Entity> entities=MillCommonUtilities.getEntitiesWithinAABB(world, EntityMob.class, p, 20, 20);



			for (final Entity ent : entities) {
				if (p.distanceTo(ent)<closestDistance) {
					closestDistance=p.distanceTo(ent);
				}
			}
		}

		if (closestDistance>radius) {
			level=0;
		} else {
			level=((radius-closestDistance)/radius);
		}
		
		return (int) (level*15);
		
	}

}
