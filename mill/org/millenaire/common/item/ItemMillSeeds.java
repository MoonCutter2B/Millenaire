// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ItemMillSeeds.java

package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import org.millenaire.common.MLN;
import org.millenaire.common.UserProfile;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.network.ServerSender;

// Referenced classes of package org.millenaire.common.item:
//            Goods

public class ItemMillSeeds extends Goods.ItemText implements IPlantable {

	public final Block crop;

	public final String cropKey;

	public ItemMillSeeds(final String iconName, final Block j,
			final String cropKey) {
		super(iconName);
		crop = j;
		this.cropKey = cropKey;
		setCreativeTab(Mill.tabMillenaire);
	}

	@Override
	public Block getPlant(final IBlockAccess world, final int x, final int y,
			final int z) {
		return crop;
	}

	@Override
	public int getPlantMetadata(final IBlockAccess world, final int x,
			final int y, final int i) {
		return 0;
	}

	@Override
	public EnumPlantType getPlantType(final IBlockAccess world, final int x,
			final int y, final int z) {
		return EnumPlantType.Crop;
	}

	@Override
	public boolean onItemUse(final ItemStack itemstack,
			final EntityPlayer entityplayer, final World world, final int i,
			final int j, final int k, final int l, final float hitX,
			final float hitY, final float hitZ) {
		if (l != 1) {
			return false;
		}
		if (!entityplayer.canPlayerEdit(i, j, k, l, itemstack)
				|| !entityplayer.canPlayerEdit(i, j + 1, k, l, itemstack)) {
			return false;
		}
		final UserProfile profile = Mill.getMillWorld(world).getProfile(
				entityplayer.getDisplayName());
		if (!profile.isTagSet(new StringBuilder().append("cropplanting_")
				.append(cropKey).toString())
				&& !MLN.DEV) {
			if (!world.isRemote) {
				ServerSender.sendTranslatedSentence(entityplayer, 'f',
						"ui.cropplantingknowledge",
						new String[] { new StringBuilder().append("item.")
								.append(cropKey).toString() });
			}
			return false;
		}
		final Block block = world.getBlock(i, j, k);
		if (block == Blocks.farmland && world.isAirBlock(i, j + 1, k)) {
			MillCommonUtilities.setBlockAndMetadata(world, i, j + 1, k, crop,
					0, true, false);
			itemstack.stackSize--;
			if (!world.isRemote) {
				entityplayer.addStat(MillAchievements.masterfarmer, 1);
			}
			return true;
		} else {
			return false;
		}
	}
}
