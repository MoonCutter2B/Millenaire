package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.millenaire.common.MLN;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockDecorativeSlab extends BlockHalfSlab {

	public static class ItemDecorativeSlab extends ItemSlab {

		BlockDecorativeSlab block;
		
		private final boolean isFullBlockDec;

	    /** Instance of BlockHalfSlab. */
	    private final BlockHalfSlab theHalfSlabDec;

	    /** The double-slab block corresponding to this item. */
	    private final BlockHalfSlab doubleSlabDec;

		public ItemDecorativeSlab(int i,BlockDecorativeSlab halfSlab,BlockDecorativeSlab fullBlock, boolean full)
		{
			super(i,halfSlab,fullBlock,full);
			setMaxDamage(0);
			setHasSubtypes(true);
			this.block=(BlockDecorativeSlab)Block.blocksList[i+256];
			this.theHalfSlabDec = halfSlab;
	        this.doubleSlabDec = fullBlock;
	        this.isFullBlockDec = full;
		}

		@Override
		public Icon getIconFromDamage(int i)
		{
			return block.getIcon(2, i);
		}

		@Override
		public String getUnlocalizedName(ItemStack itemstack)
		{
			return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
		}

		@Override
		public int getMetadata(int i)
		{
			return i;
		}
		

		
		@Override
	    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	    {
	        if (this.isFullBlockDec)
	        {
	            return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
	        }
	        else if (par1ItemStack.stackSize == 0)
	        {
	            return false;
	        }
	        else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
	        {
	            return false;
	        }
	        else
	        {
	            int i1 = par3World.getBlockId(par4, par5, par6);
	            int j1 = par3World.getBlockMetadata(par4, par5, par6);

	            if ((par7 == 1) && i1 == this.theHalfSlabDec.blockID && j1 == par1ItemStack.getItemDamage())
	            {
	                if (par3World.checkBlockCollision(this.doubleSlabDec.getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.doubleSlabDec.blockID, j1, 3))
	                {
	                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.doubleSlabDec.stepSound.getPlaceSound(), (this.doubleSlabDec.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlabDec.stepSound.getPitch() * 0.8F);
	                    --par1ItemStack.stackSize;
	                }

	                return true;
	            }
	            else
	            {
	                return this.func_77888_a(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7) ? true : super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
	            }
	        }
	    }
		
		private boolean func_77888_a(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
	    {
	        if (par7 == 0)
	        {
	            --par5;
	        }

	        if (par7 == 1)
	        {
	            ++par5;
	        }

	        if (par7 == 2)
	        {
	            --par6;
	        }

	        if (par7 == 3)
	        {
	            ++par6;
	        }

	        if (par7 == 4)
	        {
	            --par4;
	        }

	        if (par7 == 5)
	        {
	            ++par4;
	        }

	        int i1 = par3World.getBlockId(par4, par5, par6);
	        int j1 = par3World.getBlockMetadata(par4, par5, par6);
	        int k1 = j1 & 7;

	        if (i1 == this.theHalfSlabDec.blockID && k1 == par1ItemStack.getItemDamage())
	        {
	            if (par3World.checkBlockCollision(this.doubleSlabDec.getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.doubleSlabDec.blockID, k1, 3))
	            {
	                par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.doubleSlabDec.stepSound.getPlaceSound(), (this.doubleSlabDec.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlabDec.stepSound.getPitch() * 0.8F);
	                --par1ItemStack.stackSize;
	            }

	            return true;
	        }
	        else
	        {
	            return false;
	        }
	    }
	}

	public static int getBlockFromDye(int i)
	{
		return i;
	}
	public static int getDyeFromBlock(int i)
	{
		return i;
	}

	HashMap<Integer,String> textureSideNames=new HashMap<Integer,String>();
	HashMap<Integer,String> textureTopNames=new HashMap<Integer,String>();
	HashMap<Integer,String> textureBottomNames=new HashMap<Integer,String>();
	HashMap<Integer,Icon> texturesSide=new HashMap<Integer,Icon>();
	HashMap<Integer,Icon> texturesTop=new HashMap<Integer,Icon>();
	HashMap<Integer,Icon> texturesBottom=new HashMap<Integer,Icon>();

	HashMap<Integer,String> names=new HashMap<Integer,String>();

	public BlockDecorativeSlab(int i, Material material, boolean full) {
		super(i, full, material);
		setTickRandomly(true);
		setLightOpacity(0);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addCreativeItems(@SuppressWarnings("rawtypes") ArrayList itemList) {
		final ArrayList<ItemStack> list=itemList;

		for (final int meta: textureSideNames.keySet()) {
			if (meta>=8)
				list.add(new ItemStack(blockID,1,meta));
		}

	}
	
	@Override
	public int damageDropped(int par1)
    {
        return (par1 & 7) | 8;
    }

	@Override
	public Icon getIcon(int side, int meta)  {

		if (side==1) {
			if (texturesTop.containsKey(meta))
				return texturesTop.get(meta);
			else
				return texturesTop.get(0);
		} else if (side==0) {
			if (texturesBottom.containsKey(meta))
				return texturesBottom.get(meta);
			else
				return texturesBottom.get(0);
		} else {
			if (texturesSide.containsKey(meta))
				return texturesSide.get(meta);
			else
				return texturesTop.get(0);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (final int meta : texturesSide.keySet()) {
			if (meta>=8)
				par3List.add(new ItemStack(par1, 1, meta));
		}
	}

	public void registerTexture(int meta, String name) {
		textureTopNames.put(meta, name);
		textureBottomNames.put(meta, name);
		textureSideNames.put(meta, name);

		textureTopNames.put(meta | 8, name);
		textureBottomNames.put(meta | 8, name);
		textureSideNames.put(meta | 8, name);
	}

	public void registerTexture(int meta, String top,String bottom,String side) {
		textureTopNames.put(meta, top);
		textureBottomNames.put(meta, bottom);
		textureSideNames.put(meta, side);

		textureTopNames.put(meta | 8, top);
		textureBottomNames.put(meta | 8, bottom);
		textureSideNames.put(meta | 8, side);
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		for (int meta : textureTopNames.keySet()) {
			texturesTop.put(meta, MillCommonUtilities.getIcon(iconRegister, textureTopNames.get(meta)));
		}
		for (int meta : textureBottomNames.keySet()) {
			texturesBottom.put(meta, MillCommonUtilities.getIcon(iconRegister, textureBottomNames.get(meta)));
		}
		for (int meta : textureSideNames.keySet()) {
			texturesSide.put(meta, MillCommonUtilities.getIcon(iconRegister, textureSideNames.get(meta)));
		}
	}

	@Override
	public String getFullSlabName(int meta) {
		return names.get(meta);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		if (this.isDoubleSlab)
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		else
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}
	
	@Override
	public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int meta)
    {
        return meta;
    }
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (this.isDoubleSlab)
        {
            return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
        }
        else if (par5 != 1 && par5 != 0 && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5))
        {
            return false;
        }
        else
        {
        	return true;
        }
    }
	
	@Override
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
	{
		MLN.temp(this, "Meta: "+world.getBlockMetadata(i, j, k));
		
		return super.onBlockActivated(world, i, j, k, entityplayer, par6, par7, par8, par9);
	}
}
