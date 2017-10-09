package org.millenaire.gui;

import org.millenaire.Reference;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.items.ItemMillAmulet;
import org.millenaire.items.ItemMillArmor;
import org.millenaire.items.ItemMillFood;
import org.millenaire.items.ItemMillParchment;
import org.millenaire.items.ItemMillTool;
import org.millenaire.items.ItemMillWand;
import org.millenaire.items.MillItems;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class MillAchievement extends Achievement
{
	private final static String SETID = Reference.MOD_ID + ".achievement.";
	
	public MillAchievement(String nameIn, int column, int row, ItemStack iconIn, Achievement parentIn)
    {
		super(SETID + nameIn, nameIn, column, row, iconIn, parentIn);
    }
	
	public MillAchievement(String nameIn, int column, int row, Item iconIn, Achievement parentIn)
    {
		super(SETID + nameIn, nameIn, column, row, new ItemStack(iconIn), parentIn);
    }
	
	public MillAchievement(String nameIn, int column, int row, Block iconIn, Achievement parentIn)
    {
		super(SETID + nameIn, nameIn, column, row, new ItemStack(iconIn), parentIn);
    }

    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    //Declarations
		public static final Achievement firstContact = new MillAchievement("firstcontact", 0, 0, ItemMillParchment.normanVillagerParchment, null).registerStat();
		
		public static final Achievement cheers = new MillAchievement("cheers", 2, 2, ItemMillFood.calva, firstContact).registerStat();
		public static final Achievement hired = new MillAchievement("hired", -1, -2, ItemMillTool.normanAxe, firstContact).registerStat();
		public static final Achievement masterFarmer = new MillAchievement("masterfarmer", 3, -3, BlockMillCrops.grapes, firstContact).registerStat();

		public static final Achievement cresus = new MillAchievement("cresus", 2, -1, MillItems.denierOr, firstContact).registerStat();
		public static final Achievement summoningWand = new MillAchievement("summoningwand", 5, 0, ItemMillWand.wandSummoning, cresus).registerStat();
		public static final Achievement villageLeader = new MillAchievement("villageleader", 4, 2, ItemMillArmor.mayanQuestCrown, summoningWand).setSpecial().registerStat();
		
		public static final Achievement theQuest = new MillAchievement("thequest", 0, -4, Blocks.torch, firstContact).registerStat();
		public static final Achievement maitreapenser = new MillAchievement("maitreapenser", 2, -5, Items.writable_book, theQuest).registerStat();
		public static final Achievement forbiddenKnowledge = new MillAchievement("forbiddenknwoledge", 2, -7, ItemMillParchment.normanAllParchment, maitreapenser).setSpecial().registerStat();
		public static final Achievement puja = new MillAchievement("puja", -1, -6, ItemMillAmulet.amuletVishnu, maitreapenser).registerStat();
		
		public static final Achievement explorer = new MillAchievement("explorer", -3, 1, Items.leather_boots, firstContact).registerStat();
		public static final Achievement marcoPolo = new MillAchievement("marcopolo", -4, 3, Items.map, explorer).registerStat();
		public static final Achievement magellan = new MillAchievement("magellan", -6, 5, Items.compass, marcoPolo).setSpecial().registerStat();
		
		public static final Achievement selfDefense = new MillAchievement("selfdefense", -5, 2, ItemMillArmor.byzantineChestplate, explorer).registerStat();
		public static final Achievement pantheon = new MillAchievement("pantheon", -4, -5, Items.sign, explorer).registerStat();
		
		public static final Achievement darkside = new MillAchievement("darkside", 0, 3, Items.stone_sword, firstContact).registerStat();
		public static final Achievement scipio = new MillAchievement("scipio", -1, 6, Items.iron_sword, darkside).registerStat();
		public static final Achievement attila = new MillAchievement("attila", 2, 9, ItemMillTool.normanSword, scipio).setSpecial().registerStat();
		
		public static void preinitialize()
	    {
			AchievementPage.registerAchievementPage(new AchievementPage("millenaire.achievements", firstContact, cheers, hired, masterFarmer, cresus, summoningWand, villageLeader, theQuest, maitreapenser, 
					forbiddenKnowledge, puja, explorer, marcoPolo, magellan, selfDefense, pantheon, darkside, scipio, attila));
	    }
}
