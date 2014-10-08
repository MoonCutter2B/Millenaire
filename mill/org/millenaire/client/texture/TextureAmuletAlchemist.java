package org.millenaire.client.texture;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.millenaire.common.Point;

public class TextureAmuletAlchemist extends TextureAtlasSprite {

	private static final int radius = 5;

	public TextureAmuletAlchemist(final String iconName) {
		super(iconName);
	}

	private int getScore(final Minecraft mc) {
		int score = 0;

		if (mc.theWorld != null && mc.thePlayer != null) {

			final World world = mc.theWorld;

			final Point p = new Point(mc.thePlayer);

			final int startY = Math.max(p.getiY() - radius, 0);
			final int endY = Math.min(p.getiY() + radius, 127);

			for (int i = p.getiX() - radius; i < p.getiX() + radius; i++) {
				for (int j = p.getiZ() - radius; j < p.getiZ() + radius; j++) {
					for (int k = startY; k < endY; k++) {
						final Block block = world.getBlock(i, k, j);
						if (block == Blocks.coal_ore) {
							score++;
						} else if (block == Blocks.diamond_ore) {
							score += 30;
						} else if (block == Blocks.emerald_ore) {
							score += 30;
						} else if (block == Blocks.gold_ore) {
							score += 10;
						} else if (block == Blocks.iron_ore) {
							score += 5;
						} else if (block == Blocks.lapis_ore) {
							score += 10;
						} else if (block == Blocks.redstone_ore) {
							score += 5;
						} else if (block == Blocks.lit_redstone_ore) {
							score += 5;
						}
					}
				}
			}
		}

		if (score > 100) {
			score = 100;
		}

		return score * 15 / 100;

	}

	@Override
	public void updateAnimation() {

		int iconPos = getScore(Minecraft.getMinecraft());

		// Sanity check from paths
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
