package org.millenaire;

import org.millenaire.blocks.BlockDecorative;
import org.millenaire.blocks.BlockMillChest;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.blocks.BlockMillPath;
import org.millenaire.items.MillItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Millenaire.MODID, name = Millenaire.NAME, version = Millenaire.VERSION)
public class Millenaire 
{
	public static final String MODID = "millenaire";
	public static final String NAME = "Mill\u00e9naire";
	public static final String VERSION = "7.0.0";
	
	public static final CreativeTabs tabMillenaire = new CreativeTabs(NAME)
	{
		public Item getTabIconItem() 
		{
			return MillItems.denierOr;
		}
	};
	
	@EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(new RaidEvent.RaidEventHandler());
		
		MillItems.preinitialize();
		BlockDecorative.preinitialize();
		BlockMillCrops.preinitialize();
		BlockMillChest.preinitialize();
		BlockMillPath.preinitialize();
		
		if(event.getSide() == Side.CLIENT)
		{
			BlockDecorative.prerender();
			BlockMillPath.prerender();
		}
    }
	
	@EventHandler
    public void init(FMLInitializationEvent event)
    {
		if(event.getSide() == Side.CLIENT)
    	{
			MillItems.render();
			BlockDecorative.render();
			BlockMillCrops.render();
			BlockMillChest.render();
    	}
    }
	
	@EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
		
    }
}
