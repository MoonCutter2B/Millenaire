package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.entities.TileEntityMillChest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillChest extends BlockChest
{
	protected BlockMillChest() 
	{
		super(2);
	}

	@Override
	public int quantityDropped(final Random random) 
	{
		return 0;
	}
	
	@Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (hasTileEntity(state) && !(this instanceof BlockContainer))
        {
            worldIn.removeTileEntity(pos);
        }
    }
	
	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) 
	{
		return new TileEntityMillChest();
	}
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
	//Declarations
		public static Block blockMillChest;

    public static void preinitialize()
    {
    	blockMillChest = new BlockMillChest().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("blockMillChest");
		GameRegistry.registerBlock(blockMillChest, "blockMillChest");
		
		GameRegistry.registerTileEntity(TileEntityMillChest.class, "tileEntityMillChest");
    }
    
    @SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMillChest.class, new TileEntityChestRenderer());
	}
}
