package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.UserProfile;
import org.millenaire.common.VillageType;
import org.millenaire.common.forge.Mill;

public class GuiNewVillage extends GuiText {

	private Vector<VillageType> possibleVillages=new Vector<VillageType>();
	private final Point pos;
	private final EntityPlayer player;

	public GuiNewVillage(EntityPlayer player,Point p) {
		pos=p;
		this.player=player;
	}


	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
			return;

		final VillageType village=possibleVillages.get(guibutton.id);

		ClientSender.newVillageCreation(player, pos, village.culture.key, village.key);

		closeWindow();
	}


	@Override
	protected void customDrawBackground(int i, int j, float f) {
	}

	@Override
	protected void customDrawScreen(int i, int j, float f) {

	}

	@Override
	public int getLineSizeInPx() {
		return 195;
	}

	@Override
	public int getPageSize() {
		return 19;
	}
	
	ResourceLocation background=new ResourceLocation(Mill.modId,"/textures/gui/ML_panel.png");

	@Override
	public ResourceLocation getPNGPath() {
		return background;
	}

	@Override
	public int getXSize() {
		return 204;
	}

	@Override
	public int getYSize() {
		return 220;
	}

	@Override
	public void initData() {
		final Vector<Line> text=new Vector<Line>();

		text.add(new Line(MLN.string("ui.selectavillage"),false));
		text.add(new Line(false));
		text.add(new Line(MLN.string("ui.leadershipstatus")+":"));
		text.add(new Line());

		boolean notleader=false;

		final UserProfile profile=Mill.proxy.getClientProfile();

		for (final Culture culture : Culture.vectorCultures) {
			if (profile.isTagSet(MillWorld.CULTURE_CONTROL+culture.key)) {
				text.add(new Line(MLN.string("ui.leaderin",culture.getCultureGameName())));
			} else {
				text.add(new Line(MLN.string("ui.notleaderin",culture.getCultureGameName())));
				notleader=true;
			}
		}

		if (notleader) {
			text.add(new Line());
			text.add(new Line(MLN.string("ui.leaderinstruction")));
		}
		text.add(new Line());

		possibleVillages = VillageType.spawnableVillages(player);

		for (int i=0;i<possibleVillages.size();i++) {
			String  controlled="";
			if (possibleVillages.get(i).playerControlled) {
				controlled=", "+MLN.string("ui.controlled");
			}

			text.add(new Line(new MillGuiButton(i,0,0,0,0, possibleVillages.get(i).name+" ("+possibleVillages.get(i).culture.getCultureGameName()+controlled+")")));
			text.add(new Line(false));
			text.add(new Line());
		}


		final Vector<Vector<Line>> pages = new Vector<Vector<Line>>();
		pages.add(text);
		descText=adjustText(pages);
	}
}
