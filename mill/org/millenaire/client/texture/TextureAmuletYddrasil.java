package org.millenaire.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;

import org.millenaire.common.Point;

public class TextureAmuletYddrasil extends TextureStitched {
	
	public TextureAmuletYddrasil(String iconName) {
		super(iconName);
	}

	public void func_94219_l()
	{

		int iconPos=getScore(Minecraft.getMinecraft());
		
		if (iconPos>15)
			iconPos=15;
		if (iconPos<0)
			iconPos=0;

		if (iconPos != this.field_94222_f)
		{
			this.field_94222_f = iconPos;
			this.field_94228_a.func_94281_a(this.field_94224_d, this.field_94225_e, (Texture)this.field_94226_b.get(this.field_94222_f), this.field_94227_c);
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
