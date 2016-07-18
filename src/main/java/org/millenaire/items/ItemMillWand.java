package org.millenaire.items;

import org.millenaire.CommonUtilities;
import org.millenaire.MillCulture;
import org.millenaire.Millenaire;
import org.millenaire.VillageTracker;
import org.millenaire.blocks.BlockMillChest;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.entities.EntityMillVillager;
import org.millenaire.entities.TileEntityMillChest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillWand extends Item
{
	public ItemMillWand() {}
	
	@Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if(this == wandSummoning)
		{
			if(worldIn.getBlockState(pos).getBlock() == Blocks.gold_block)
			{
				if(!worldIn.isRemote)
				{	
					System.out.println("Gold Creation");
					worldIn.setBlockToAir(pos);
					EntityMillVillager entity = new EntityMillVillager(worldIn, 100100, MillCulture.normanCulture);
					System.out.println("cultured: ");
					entity = entity.setTypeAndGender(MillCulture.normanCulture.getVillagerType("normanGirl"), 1);
					System.out.println(entity.getVillagerType());
					entity.setChild();
					entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
					worldIn.spawnEntityInWorld(entity);
				}
				stack.stackSize--;
				return true;
			}
			else if(worldIn.getBlockState(pos).getBlock() == Blocks.obsidian)
			{
				if(!worldIn.isRemote)
				{	
					System.out.println("Obsidian Creation");
					worldIn.setBlockToAir(pos);
					EntityMillVillager entity = new EntityMillVillager(worldIn, 100101, MillCulture.normanCulture);
					System.out.println("cultured: ");
					entity = entity.setTypeAndGender(MillCulture.normanCulture.getVillagerType("normanLady"), 1);
					System.out.println(entity.getVillagerType());
					entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
					worldIn.spawnEntityInWorld(entity);
				}
				stack.stackSize--;
				return true;
			}
		}
		
		if(this == wandCreative)
		{
			//Control whether or not you can plant crops
			if(worldIn.getBlockState(pos).getBlock() instanceof BlockMillCrops)
			{
				if(playerIn.isSneaking())
				{
					boolean hasCrop;

					hasCrop = VillageTracker.get(worldIn).removePlayerUseCrop(playerIn, ((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getItem(worldIn, pos));
					System.out.println(((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getItem(worldIn, pos).toString());
					
					if(worldIn.isRemote)
					{
						if(hasCrop)
							playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can no longer plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
						else
							playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " already could not plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
					}
				}
				else
				{
					boolean succeeded = false;
					if(!VillageTracker.get(worldIn).canPlayerUseCrop(playerIn, ((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getItem(worldIn, pos)))
					{
						VillageTracker.get(worldIn).setPlayerUseCrop(playerIn, ((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getItem(worldIn, pos));
						succeeded = true;
					}
					
					if(worldIn.isRemote)
					{
						if(succeeded)
							playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can now plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
						else
							playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can already plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
					}
				}
			}
			//Allow you to plant all Crops
			else if(worldIn.getBlockState(pos).getBlock() == Blocks.cake)
			{
				if(!VillageTracker.get(worldIn).canPlayerUseCrop(playerIn, BlockMillCrops.grapes))
					VillageTracker.get(worldIn).setPlayerUseCrop(playerIn, BlockMillCrops.grapes);
				if(!VillageTracker.get(worldIn).canPlayerUseCrop(playerIn, BlockMillCrops.maize))
					VillageTracker.get(worldIn).setPlayerUseCrop(playerIn, BlockMillCrops.maize);
				if(!VillageTracker.get(worldIn).canPlayerUseCrop(playerIn, BlockMillCrops.rice))
					VillageTracker.get(worldIn).setPlayerUseCrop(playerIn, BlockMillCrops.rice);
				if(!VillageTracker.get(worldIn).canPlayerUseCrop(playerIn, BlockMillCrops.turmeric))
					VillageTracker.get(worldIn).setPlayerUseCrop(playerIn, BlockMillCrops.turmeric);
				
				if(worldIn.isRemote)
				{
					playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can now plant everything"));
				}
			}
			//Lock and Unlock Chests
			else if(worldIn.getBlockState(pos).getBlock() instanceof BlockMillChest)
			{
				boolean isLocked = ((TileEntityMillChest)worldIn.getTileEntity(pos)).setLock();
				
				if(worldIn.isRemote)
				{
					if(isLocked)
						playerIn.addChatMessage(new ChatComponentText("Chest is now Locked"));
					else
						playerIn.addChatMessage(new ChatComponentText("Chest is now Unlocked"));
				}
			}
			//Fixes All Denier in your inventory (if no specific block/entity is clicked)
			else
			{
				CommonUtilities.changeMoney(playerIn);
				if(worldIn.isRemote)
					playerIn.addChatMessage(new ChatComponentText("Fixing Denier in " + playerIn.getDisplayNameString() + "'s Inventory"));
			}
		}
		
        return false;
    }
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    //Declarations
    	public static Item wandSummoning;
    	public static Item wandNegation;
    	public static Item wandCreative;
    
    public static void preinitialize()
    {
    	wandSummoning = new ItemMillWand().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wandSummoning");
    	GameRegistry.registerItem(wandSummoning, "wandSummoning");
    	wandNegation = new ItemMillWand().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wandNegation");
    	GameRegistry.registerItem(wandNegation, "wandNegation");
    	wandCreative = new ItemMillWand().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wandCreative");
    	GameRegistry.registerItem(wandCreative, "wandCreative");
    }
    
    @SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		renderItem.getItemModelMesher().register(wandSummoning, 0, new ModelResourceLocation(Millenaire.MODID + ":wandSummoning", "inventory"));
		renderItem.getItemModelMesher().register(wandNegation, 0, new ModelResourceLocation(Millenaire.MODID + ":wandNegation", "inventory"));
		renderItem.getItemModelMesher().register(wandCreative, 0, new ModelResourceLocation(Millenaire.MODID + ":wandCreative", "inventory"));
	}
}
