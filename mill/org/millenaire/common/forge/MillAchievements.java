package org.millenaire.common.forge;

import org.millenaire.common.MLN;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class MillAchievements extends Achievement {
	
	final String key;
	
	public MillAchievements(int par1, String par2Str, int par3, int par4,
			Block par5Block, Achievement par6Achievement) {
		super(par1, par2Str, par3, par4, par5Block, par6Achievement);
		key=par2Str;
	}
	
	public MillAchievements(int par1, String par2Str, int par3, int par4, Item par5Item, Achievement par6Achievement)
    {
		super(par1, par2Str, par3, par4, par5Item, par6Achievement);
		key=par2Str;
    }

	public MillAchievements(int par1, String par2Str, int par3, int par4, ItemStack par5ItemStack, Achievement par6Achievement)
    {
		super(par1, par2Str, par3, par4, par5ItemStack, par6Achievement);
		key=par2Str;
    }
	
	@Override
	public String getDescription() {
		return MLN.string("achievement."+key+".desc");
	}

	@Override
	public String getName() {
		return MLN.string("achievement."+key+".name");
	}

	public static final Achievement firstContact = new MillAchievements(7470, "firstcontact", 0, 0, Mill.parchmentVillagers, 
			null).registerAchievement();
	
	public static final Achievement cresus = new MillAchievements(7471, "cresus", 2, -1, Mill.denier_or, 
			firstContact).registerAchievement();
	
	public static final Achievement summoningwand = new MillAchievements(7472, "summoningwand", 5, 0, Mill.summoningWand, 
			cresus).registerAchievement();
	
	public static final Achievement villageleader = new MillAchievements(7473, "villageleader", 4, 2, Mill.normanHelmet, 
			summoningwand).setSpecial().registerAchievement();
	
	public static final Achievement thequest = new MillAchievements(7474, "thequest", 0, -4, Block.torchWood, 
			firstContact).registerAchievement();
	
	public static final Achievement maitreapenser = new MillAchievements(7475, "maitreapenser", 2, -5, Item.writableBook, 
			thequest).registerAchievement();
	
	public static final Achievement forbiddenknwoledge = new MillAchievements(7476, "forbiddenknwoledge", 2, -7, Mill.parchmentSadhu, 
			maitreapenser).setSpecial().registerAchievement();
	
	public static final Achievement puja = new MillAchievements(7477, "puja", -1, -6, Mill.indianstatue, 
			maitreapenser).registerAchievement();
	
	public static final Achievement explorer = new MillAchievements(7478, "explorer", -3, 1, Item.bootsLeather, 
			firstContact).registerAchievement();
	
	public static final Achievement marcopolo = new MillAchievements(7479, "marcopolo", -4, 3, Item.map, 
			explorer).registerAchievement();
	
	public static final Achievement magellan = new MillAchievements(7480, "magellan", -6, 5, Item.compass, 
			marcopolo).setSpecial().registerAchievement();
	
	public static final Achievement selfdefense = new MillAchievements(7481, "selfdefense", -5, 2, Mill.byzantinePlate, 
			explorer).registerAchievement();
	
	public static final Achievement pantheon = new MillAchievements(7482, "pantheon", -4, -5, Item.sign, 
			explorer).registerAchievement();
	
	public static final Achievement darkside = new MillAchievements(7483, "darkside", 0, 3, Item.swordStone, 
			firstContact).registerAchievement();
	
	public static final Achievement scipio = new MillAchievements(7484, "scipio", -1, 6, Item.swordSteel, 
			darkside).registerAchievement();
	
	public static final Achievement attila = new MillAchievements(7485, "attila", 2, 9, Mill.normanBroadsword, 
			scipio).setSpecial().registerAchievement();
	
	public static final Achievement cheers = new MillAchievements(7486, "cheers", 2, 2, Mill.calva, 
			firstContact).registerAchievement();
	
	public static final Achievement hired = new MillAchievements(7487, "hired", -1, -2, Mill.normanAxe, 
			firstContact).registerAchievement();
	
	public static final Achievement masterfarmer = new MillAchievements(7488, "masterfarmer", 3, -3, Mill.grapes, 
			firstContact).registerAchievement();
	
	
	//player.addStat(MillAchievements.thequest, 1);
	
	public static MillAchievementPage millAchievements = new MillAchievementPage("", firstContact,cresus,summoningwand,villageleader,
			thequest,maitreapenser,forbiddenknwoledge,puja,explorer,marcopolo,magellan,selfdefense,pantheon,darkside,scipio,attila,
			cheers,hired,masterfarmer);
	
	
	public static class MillAchievementPage extends AchievementPage {

		public MillAchievementPage(String name, Achievement... achievements) {
			super(name, achievements);
		}
		
		public String getName()
	    {
	        return MLN.string("achievementpage.name");
	    }
		
	}
	
}
