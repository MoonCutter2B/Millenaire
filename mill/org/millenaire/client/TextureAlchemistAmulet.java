package org.millenaire.client;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.world.World;

import org.millenaire.common.Point;
import org.millenaire.common.forge.Mill;

public class TextureAlchemistAmulet extends TextureStitched {

	private int[] buffer;
	private final Minecraft mc;
	private final int[][] neutral=new int[][]{{216,216,216}, {150,150,150}, {114,114,114}};
	private final int[][] shining=new int[][]{{255,255,139}, {222,222,0}, {220,118,20}};
	private final int[][] detect=new int[][]{{127,0,0}, {100,0,0}, {50,0,0}};

	private final int radius=5;

	public TextureAlchemistAmulet(Minecraft minecraft) {
		super(Mill.alchemist_amulet.getIconFromDamage(0));
		this.mc=minecraft;


		tileImage = 1;
		try
		{
			final BufferedImage bufferedimage = ImageIO.read((net.minecraft.client.Minecraft.class).getResource("/graphics/item/ML_alchimist_amulet.png"));
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

		final double mult=(score*1.0)/100;

		final int[][] targetcol=new int[detect.length][3];



		for (int i=0;i<detect.length;i++) {
			for (int j=0;j<3;j++) {

				targetcol[i][j]=(int) ((1.0*neutral[i][j]*(1-mult))+(1.0*shining[i][j]*mult));
			}
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
			for (int c=0;c<detect.length;c++) {
				if ((red==detect[c][0]) && (green==detect[c][1]) && (blue==detect[c][2])) {
					image[(i * 4) + 0] = (byte)targetcol[c][0];
					image[(i * 4) + 1] = (byte)targetcol[c][1];
					image[(i * 4) + 2] = (byte)targetcol[c][2];
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
