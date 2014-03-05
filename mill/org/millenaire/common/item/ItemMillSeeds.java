// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.millenaire.common.MLN;
import org.millenaire.common.MillWorld;
import org.millenaire.common.UserProfile;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.item.Goods.ItemText;
import org.millenaire.common.network.ServerSender;


// Referenced classes of package net.minecraft.src:
//            Item, World, Block, ItemStack,
//            EntityPlayer

public class ItemMillSeeds extends ItemText
{

	public final Block crop;
	public final int cropMeta;
	public final String cropKey;

	public ItemMillSeeds(String iconName, Block j, int k, String cropKey)
	{
		super(iconName);
		crop = j;
		cropMeta=k;
		this.cropKey=cropKey;
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float hitX, float hitY, float hitZ)
	{
		
		
		if (l != 1)
			return false;
		if (!entityplayer.canPlayerEdit(i, j, k, l, itemstack) || !entityplayer.canPlayerEdit(i, j + 1, k, l, itemstack))
			return false;

		final UserProfile profile=Mill.getMillWorld(world).getProfile(entityplayer.getDisplayName());

		if (!profile.isTagSet(MillWorld.CROP_PLANTING+cropKey) && !MLN.DEV) {
			if (!world.isRemote) {
				ServerSender.sendTranslatedSentence(entityplayer,MLN.WHITE,"ui.cropplantingknowledge","item."+cropKey);
			}
			return false;
		}

		final Block block = world.getBlock(i, j, k);
		if ((block == Blocks.farmland) && world.isAirBlock(i, j + 1, k))
		{
			
			MillCommonUtilities.setBlockAndMetadata(world,i,j+1,k,crop, cropMeta, true, false);
			itemstack.stackSize--;
			
			if (!world.isRemote) {
				entityplayer.addStat(MillAchievements.masterfarmer, 1);
			}
			
			return true;
		} else
			return false;
	}
}
