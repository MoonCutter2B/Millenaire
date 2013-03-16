package org.millenaire.client.texture;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.world.World;

import org.millenaire.common.Point;

public class TextureAmuletAlchemist extends TextureStitched {

	private static final int radius=5;
	
	public TextureAmuletAlchemist(String iconName) {
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
