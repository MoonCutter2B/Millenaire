package org.millenaire.common.block;

import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockOrientedSlab extends BlockHalfSlab {
	
	public final String  textureTopVertName, textureTopHorName,textureSideName;
	public Icon  textureTopVert, textureTopHor,textureSide;

	public BlockOrientedSlab(int par1,String textureTopVertName,String textureTopHorName,String textureSideName)
	{
		super(par1, false, Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
		setLightOpacity(0);
		this.textureTopVertName=textureTopVertName;
		this.textureTopHorName=textureTopHorName;
		this.textureSideName=textureSideName;
	}

	@Override
	public void func_94332_a(IconRegister iconRegister)
	{
		textureTopVert=MillCommonUtilities.getIcon(iconRegister, textureTopVertName);
		textureTopHor=MillCommonUtilities.getIcon(iconRegister, textureTopHorName);
		textureSide=MillCommonUtilities.getIcon(iconRegister, textureSideName);
	}
	
	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
	 * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int par1)
	{
		return new ItemStack(blockID, 2, 0);
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int meta)
	{
		meta = meta & 1;

		if ((side == 1) || (side == 0)) {//top & bottom
			if (meta==0)
				return textureSide;
			else
				return textureTopVert;
		}

		int turn=0;

		if ((side==4) || (side==5)) {
			turn=1;
		}

		if (meta==1) {
			turn=1-turn;
		}

		if ((turn == 0))
			return textureTopHor;
		if ((turn == 1))
			return textureTopVert;
		return textureTopVert;
	}

	@Override
	public String getFullSlabName(int var1) {
		return "byzantinebrick";
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack par6ItemStack)
	{

		if(par1World.getBlockId(x, y - 1, z) == blockID){

			final int meta = par1World.getBlockMetadata(x, y-1, z);
			par1World.setBlockAndMetadataWithNotify(x, y-1, z, Mill.byzantine_tiles.blockID,meta & 1,2);
			par1World.setBlockAndMetadataWithNotify(x, y, z, 0,0,2);

		} else {

			final int var6 = MathHelper.floor_double(((par5EntityLiving.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3;

			final int meta = par1World.getBlockMetadata(x, y, z);

			if (var6 == 0)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 0 | meta,2);
			}

			if (var6 == 1)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 1 | meta,2);
			}

			if (var6 == 2)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 0 | meta,2);
			}

			if (var6 == 3)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 1 | meta,2);
			}
		}
	}
}
