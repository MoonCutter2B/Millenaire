package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import org.millenaire.common.core.MillCommonUtilities;

public class BlockMillCrops extends BlockCrops
    implements IGrowable
{

    public IPlantable getSeed()
    {
        return seed;
    }

    public void setSeed(IPlantable seed)
    {
        this.seed = seed;
    }

    public BlockMillCrops(String textureNames[], boolean requireIrrigation, boolean slowGrowth)
    {
        textures = new IIcon[8];
        this.textureNames = textureNames;
        this.requireIrrigation = requireIrrigation;
        this.slowGrowth = slowGrowth;
        setTickRandomly(true);
        float f = 0.5F;
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
    }

    @Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return 150;
    }

    @Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return 60;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        for(int i = 0; i < 8; i++)
            textures[i] = MillCommonUtilities.getIcon(iconRegister, textureNames[i]);

    }

    protected float getGrowthRate(World world, int i, int j, int k)
    {
        int irrigation = world.getBlockMetadata(i, j-1, k);
        if(requireIrrigation && irrigation == 0)
            return 0.0F;
        return !slowGrowth ? 8.0F : 4.0F;
    }

    @Override
	public int getRenderType()
    {
        return 6;
    }

    @Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
    {
    	 return (Item)seed;
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    @Override
	public ArrayList getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList ret = new ArrayList();
        ret.add(new ItemStack(func_149865_P(), 1, 0));
        if(metadata == 7)
        {
            for(int n = 0; n < 3 + fortune; n++)
                if(world.rand.nextInt(15) <= metadata)
                    ret.add(new ItemStack(func_149865_P(), 1, 0));

        }
        return ret;
    }

    @Override
	public IIcon getIcon(int p_149691_1_, int meta)
    {
        if(meta < 0 || meta > 7)
            meta = 7;
        return textures[meta];
    }

    @Override
	public void updateTick(World world, int i, int j, int k, Random random)
    {
    	checkAndDropBlock(world, i, j, k);
        if(world.getBlockLightValue(i, j + 1, k) >= 9)
        {
            int l = world.getBlockMetadata(i, j, k);
            if(l < 7)
            {
                float f = getGrowthRate(world, i, j, k);
                if(f > 0.0F && random.nextInt((int)(100F / f)) == 0)
                    MillCommonUtilities.setBlockMetadata(world, i, j, k, l + 1, true);
            }
        }
    }

    private final String textureNames[];
    private IIcon textures[];
    private final boolean requireIrrigation;
    private final boolean slowGrowth;
    private IPlantable seed;
}
