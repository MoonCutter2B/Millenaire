package org.millenaire.client.texture;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;

public class TextureAmuletVishnu extends TextureAtlasSprite {

	private static final int radius = 20;

	public TextureAmuletVishnu(final String iconName) {
		super(iconName);
	}

	private int getScore(final Minecraft mc) {
		double level = 0;

		double closestDistance = Double.MAX_VALUE;

		if (mc.theWorld != null && mc.thePlayer != null) {

			final World world = mc.theWorld;

			final Point p = new Point(mc.thePlayer);

			final List<Entity> entities = MillCommonUtilities.getEntitiesWithinAABB(world, EntityMob.class, p, 20, 20);

			for (final Entity ent : entities) {
				if (p.distanceTo(ent) < closestDistance) {
					closestDistance = p.distanceTo(ent);
				}
			}
		}

		if (closestDistance > radius) {
			level = 0;
		} else {
			level = (radius - closestDistance) / radius;
		}

		return (int) (level * 15);

	}

	@Override
	public void updateAnimation() {

		int iconPos = getScore(Minecraft.getMinecraft());

		if (iconPos > 15) {
			iconPos = 15;
		}
		if (iconPos < 0) {
			iconPos = 0;
		}

		if (iconPos != this.frameCounter) {
			this.frameCounter = iconPos;
			TextureUtil.uploadTextureMipmap((int[][]) this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
		}
	}

}
