package org.millenaire.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;

import org.millenaire.common.Point;

public class TextureAmuletYddrasil extends TextureAtlasSprite {
	
	public TextureAmuletYddrasil(String iconName) {
		super(iconName);
	}

	@Override
	public void updateAnimation()
	{

		int iconPos=getScore(Minecraft.getMinecraft());
		
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
		int level=0;

		if((mc.theWorld != null) && (mc.thePlayer != null)) {
			final Point p=new Point(mc.thePlayer);

			level=(int) Math.floor(p.getiY());
		}
		
		if (level>127) {
			level=127;
		}
		
		level=level/8;
		
		return level;
		
	}

}
