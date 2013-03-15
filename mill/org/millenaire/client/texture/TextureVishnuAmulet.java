package org.millenaire.client.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;

public class TextureVishnuAmulet extends TextureStitched {

	private int[] buffer;
	private final Minecraft mc;
	private final int[][] safe=new int[][]{{0,0,255}, {0,0,236}, {0,0,206}};
	private final int[][] danger=new int[][]{{255,0,0}, {236,0,0}, {206,0,0}};
	private final int[][] detect=new int[][]{{127,0,0}, {100,0,0}, {50,0,0}};

	public TextureVishnuAmulet(Minecraft minecraft) {
		super("om_amulet");
		this.mc=minecraft;


		try
		{
			final BufferedImage bufferedimage = ImageIO.read((net.minecraft.client.Minecraft.class).getResource("/graphics/item/ML_om_amulet.png"));
			//"/graphics/item/ML_om_amulet.png"
			buffer = new int[bufferedimage.getWidth()*bufferedimage.getHeight()];
			bufferedimage.getRGB(0, 0, bufferedimage.getHeight(), bufferedimage.getWidth(), buffer, 0, bufferedimage.getHeight());
			
			
			BufferedImage export = new BufferedImage(16, 16*16, BufferedImage.TYPE_4BYTE_ABGR);

			for (int i=0;i<16;i++) {
				drawPict(export,i,i*16,1);
			}

			ImageIO.write(export, "PNG", (new File("./amulet_vishnu.png")));

			export = new BufferedImage(64, 64*16, BufferedImage.TYPE_4BYTE_ABGR);

			for (int i=0;i<16;i++) {
				drawPict(export,i,i*64,4);
			}

			ImageIO.write(export, "PNG", (new File("./amulet_vishnu_64.png")));
			
		}
		catch(final IOException ioexception)
		{
			ioexception.printStackTrace();
		}
	}
	@Override
	public void func_94219_l() {

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

		if (closestDistance>20) {
			level=0;
		} else {
			level=((20-closestDistance)/20);
		}

		final int[][] targetcol=new int[detect.length][3];

		for (int i=0;i<detect.length;i++) {
			for (int j=0;j<3;j++) {
				targetcol[i][j]=(int) ((safe[i][j]*(1-level))+(danger[i][j]*level));
			}
		}


		final byte[] image=new byte[buffer.length*4];

		for(int i = 0; i < 256; i++) {

			final int alpha = (buffer[i] >> 24) & 0xff;
			int red = (buffer[i] >> 16) & 0xff;
			int green = (buffer[i] >> 8) & 0xff;
			int blue = (buffer[i] >> 0) & 0xff;

			

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

		//TODO : handle texture properly
		int[] imageData=new int[image.length];

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
	
	private void drawPict(BufferedImage pict,int score,int pos,int zoomFactor) {
		final int[][] targetcol=new int[detect.length][3];
		
		double level=score/15d;

		for (int i=0;i<detect.length;i++) {
			for (int j=0;j<3;j++) {
				targetcol[i][j]=(int) ((safe[i][j]*(1-level))+(danger[i][j]*level));
			}
		}


		final Color[] image=new Color[buffer.length];

		for(int i = 0; i < 256; i++) {

			final int alpha = (buffer[i] >> 24) & 0xff;
			int red = (buffer[i] >> 16) & 0xff;
			int green = (buffer[i] >> 8) & 0xff;
			int blue = (buffer[i] >> 0) & 0xff;

			

			boolean handled=false;
			for (int c=0;c<detect.length;c++) {
				if ((red==detect[c][0]) && (green==detect[c][1]) && (blue==detect[c][2])) {
					image[i] = new Color(targetcol[c][0]/255f,targetcol[c][1]/255f,targetcol[c][2]/255f,1.0f);
					handled=true;
				}
			}

			if (!handled) {
				image[i] = new Color(red,green,blue,alpha);
			}
		}

		final int originalSize=(int) Math.sqrt(image.length);


		for (int i=0;i<originalSize;i++) {
			for (int j=0;j<originalSize;j++) {
				for (int x=0;x<zoomFactor;x++) {
					for (int y=0;y<zoomFactor;y++) {						
						pict.setRGB(i*zoomFactor+x, j*zoomFactor+y+pos,image[(i)+j*originalSize].getRGB());
					}
				}
			}
		}
	}
}
