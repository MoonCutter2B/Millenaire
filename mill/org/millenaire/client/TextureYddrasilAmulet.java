package org.millenaire.client;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texturefx.TextureFX;

import org.millenaire.common.Point;
import org.millenaire.common.forge.Mill;

public class TextureYddrasilAmulet extends TextureFX {

	private int[] buffer;
	private final Minecraft mc;

	public TextureYddrasilAmulet(Minecraft minecraft) {
		super(Mill.yddrasil_amulet.getIconFromDamage(0));
		this.mc=minecraft;


		tileImage = 1;
		try
		{
			final BufferedImage bufferedimage = ImageIO.read((net.minecraft.client.Minecraft.class).getResource("/graphics/gui/ML_yggdrasil_amulet.png"));
			buffer = new int[bufferedimage.getWidth()*bufferedimage.getHeight()];
			bufferedimage.getRGB(0, 0, bufferedimage.getHeight(), bufferedimage.getWidth(), buffer, 0, bufferedimage.getHeight());
		}
		catch(final IOException ioexception)
		{
			ioexception.printStackTrace();
		}
	}
	@Override
	public void onTick() {

		int level=0;

		if((mc.theWorld != null) && (mc.thePlayer != null)) {
			final Point p=new Point(mc.thePlayer);

			level=(int) Math.floor(p.getiY()/8);
		}

		final byte[] image=new byte[buffer.length*4];

		for(int i = 0; i < 256; i++) {

			final int alpha = (buffer[i] >> 24) & 0xff;
			int red = (buffer[i] >> 16) & 0xff;
			int green = (buffer[i] >> 8) & 0xff;
			int blue = (buffer[i] >> 0) & 0xff;

			if(anaglyphEnabled)
			{
				final int j1 = ((red * 30) + (green * 59) + (blue * 11)) / 100;
				final int k1 = ((red * 30) + (green * 70)) / 100;
				final int l1 = ((red * 30) + (blue * 70)) / 100;
				red = j1;
				green = k1;
				blue = l1;
			}

			boolean handled=false;
			for (int j=0;j<16;j++) {
				if ((red==(j*10)) && (green==10) && (blue==10)) {
					if (j==level) {
						image[(i * 4) + 0] = (byte)74;
						image[(i * 4) + 1] = (byte)237;
						image[(i * 4) + 2] = (byte)209;
					} else if (j==(level-1)) {
						image[(i * 4) + 0] = (byte)44;
						image[(i * 4) + 1] = (byte)205;
						image[(i * 4) + 2] = (byte)177;
					} else if (j==(level+1)) {
						image[(i * 4) + 0] = (byte)140;
						image[(i * 4) + 1] = (byte)244;
						image[(i * 4) + 2] = (byte)226;
					} else {
						image[(i * 4) + 0] = 10;
						image[(i * 4) + 1] = 10;
						image[(i * 4) + 2] = 10;
					}
					image[(i * 4) + 3] = (byte)255;
					handled=true;
				} else if ((red==(j*10)) && (green==10) && (blue==100)) {
					if (j==level) {
						image[(i * 4) + 0] = (byte)140;
						image[(i * 4) + 1] = (byte)244;
						image[(i * 4) + 2] = (byte)226;
					} else if (j==(level-1)) {
						image[(i * 4) + 0] = (byte)27;
						image[(i * 4) + 1] = (byte)123;
						image[(i * 4) + 2] = (byte)107;
					} else if (j==(level+1)) {
						image[(i * 4) + 0] = (byte)27;
						image[(i * 4) + 1] = (byte)123;
						image[(i * 4) + 2] = (byte)107;
					} else {
						image[(i * 4) + 0] = 10;
						image[(i * 4) + 1] = 10;
						image[(i * 4) + 2] = 10;
					}
					image[(i * 4) + 3] = (byte)255;
					handled=true;
				}  else if ((red==(j*10)) && (green==100) && (blue==10)) {
					if (j==level) {
						image[(i * 4) + 0] = (byte)44;
						image[(i * 4) + 1] = (byte)205;
						image[(i * 4) + 2] = (byte)177;
					} else if (j==(level-1)) {
						image[(i * 4) + 0] = (byte)27;
						image[(i * 4) + 1] = (byte)123;
						image[(i * 4) + 2] = (byte)107;
					} else if (j==(level+1)) {
						image[(i * 4) + 0] = (byte)27;
						image[(i * 4) + 1] = (byte)123;
						image[(i * 4) + 2] = (byte)107;
					} else {
						image[(i * 4) + 0] = 10;
						image[(i * 4) + 1] = 10;
						image[(i * 4) + 2] = 10;
					}
					image[(i * 4) + 3] = (byte)255;
					handled=true;
				}
			}

			if (!handled) {
				image[(i * 4) + 0] = (byte)red;
				image[(i * 4) + 1] = (byte)green;
				image[(i * 4) + 2] = (byte)blue;
				image[(i * 4) + 3] = (byte)alpha;
			}
		}


		final int zoomFactor=(int) Math.sqrt(imageData.length/image.length);
		final int originalSize=(int) Math.sqrt(image.length/4);

		if (zoomFactor==1) {
			for (int i=0;i<imageData.length;i++) {
				imageData[i]=image[i];
			}
		} else {
			for (int i=0;i<originalSize;i++) {
				for (int j=0;j<originalSize;j++) {
					for (int k=0;k<4;k++) {
						for (int x=0;x<zoomFactor;x++) {
							for (int y=0;y<zoomFactor;y++) {
								imageData[(((i*zoomFactor)+x)*4*originalSize*zoomFactor)+(((j*zoomFactor)+y)*4)+k]=image[(i*originalSize*4)+(j*4)+k];
							}
						}
					}
				}
			}
		}
	}
}
