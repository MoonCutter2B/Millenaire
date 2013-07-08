package org.millenaire.client.texture;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.world.World;

import org.millenaire.common.Point;

public class TextureAmuletAlchemist extends TextureAtlasSprite {

	private static final int radius=5;
	
	public TextureAmuletAlchemist(String iconName) {
		super(iconName);
	}

	@Override
	public void updateAnimation()
	{

		int iconPos=getScore(Minecraft.getMinecraft());
		
		//Sanity check from paths
		if (iconPos>15)
			iconPos=15;
		if (iconPos<0)
			iconPos=0;

		if (iconPos != this.field_110973_g)
        {
            this.field_110973_g = iconPos;
            TextureUtil.func_110998_a((int[])this.field_110976_a.get(this.field_110973_g), this.width, this.height, this.field_110975_c, this.field_110974_d, false, false);
        }
	}
	
	private int getScore(Minecraft mc) {
		int score=0;

		if((mc.theWorld != null) && (mc.thePlayer != null)) {

			final World world=mc.theWorld;

			final Point p=new Point(mc.thePlayer);

			final int startY=Math.max(p.getiY()-radius, 0);
			final int endY=Math.min(p.getiY()+radius, 127);

			for (int i=p.getiX()-radius;i<(p.getiX()+radius);i++) {
				for (int j=p.getiZ()-radius;j<(p.getiZ()+radius);j++) {
					for (int k=startY;k<endY;k++) {
						final int bid=world.getBlockId(i, k, j);
						if (bid==Block.oreCoal.blockID) {
							score++;
						} else if (bid==Block.oreDiamond.blockID) {
							score+=30;
						} else if (bid==Block.oreEmerald.blockID) {
							score+=30;
						} else if (bid==Block.oreGold.blockID) {
							score+=10;
						} else if (bid==Block.oreIron.blockID) {
							score+=5;
						} else if (bid==Block.oreLapis.blockID) {
							score+=10;
						} else if (bid==Block.oreRedstone.blockID) {
							score+=5;
						} else if (bid==Block.oreRedstoneGlowing.blockID) {
							score+=5;
						}
					}
				}
			}
		}

		if (score>100) {
			score=100;
		}
		
		return (score*15/100);
		
	}

}
