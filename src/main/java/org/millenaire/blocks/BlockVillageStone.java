package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.entities.TileEntityVillageStone;
import org.millenaire.items.ItemMillWand;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVillageStone extends BlockContainer
{
	boolean willExplode = false;

	protected BlockVillageStone() 
	{
		super(Material.rock);
		
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
	}
	
	@Override
    public int getRenderType()
    {
        return 3;
    }
	
	@Override
    public int quantityDropped(Random random)
    {
        return 0;
    }
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if(worldIn.isRemote)
				playerIn.addChatMessage(new ChatComponentText("§8The Village name almost seems to shimmer in the twilight"));

        return false;
    }
	
	public void negate(World worldIn, BlockPos pos, EntityPlayer playerIn)
	{
		willExplode = true;
		worldIn.scheduleUpdate(pos, this, 60);
		worldIn.playSoundEffect(pos.getX() + 0.5D, pos.getY()+ 0.5D, pos.getZ()+ 0.5D, "portal.portal", 1.0F, 0.01F);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if(willExplode)
		{
			//Do Some Stuff
			worldIn.setBlockToAir(pos);
			worldIn.createExplosion(new EntityTNTPrimed(worldIn, pos.getX() + 0.5D, pos.getY()+ 0.5D, pos.getZ()+ 0.5D, null), pos.getX() + 0.5D, pos.getY()+ 0.5D, pos.getZ()+ 0.5D, 2.0F, true);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityVillageStone();
	}

    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
	//Declarations
		public static Block villageStone;

    public static void preinitialize()
    {
    	villageStone = new BlockVillageStone().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("villageStone");
		GameRegistry.registerBlock(villageStone, "villageStone");
		
		GameRegistry.registerTileEntity(TileEntityVillageStone.class, "tileEntityVillageStone");
    }
    
    @SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(villageStone), 0, new ModelResourceLocation(Millenaire.MODID + ":villageStone", "inventory"));
	}
}
