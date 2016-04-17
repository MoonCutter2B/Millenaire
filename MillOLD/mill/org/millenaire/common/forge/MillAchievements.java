package org.millenaire.common.forge;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.AchievementPage;

import org.millenaire.common.MLN;

public class MillAchievements extends Achievement {

	public static class MillAchievementPage extends AchievementPage {

		public MillAchievementPage(final String name, final Achievement... achievements) {
			super(name, achievements);
		}

		@Override
		public String getName() {
			return MLN.string("achievementpage.name");
		}

	}

	final String key;

	public static final Achievement firstContact = new MillAchievements("firstcontact", 0, 0, Mill.parchmentVillagers, null).registerStat();

	public static final Achievement cresus = new MillAchievements("cresus", 2, -1, Mill.denier_or, firstContact).registerStat();

	public static final Achievement summoningwand = new MillAchievements("summoningwand", 5, 0, Mill.summoningWand, cresus).registerStat();

	public static final Achievement villageleader = new MillAchievements("villageleader", 4, 2, Mill.normanHelmet, summoningwand).setSpecial().registerStat();

	public static final Achievement thequest = new MillAchievements("thequest", 0, -4, Blocks.torch, firstContact).registerStat();

	public static final Achievement maitreapenser = new MillAchievements("maitreapenser", 2, -5, Items.writable_book, thequest).registerStat();

	public static final Achievement forbiddenknwoledge = new MillAchievements("forbiddenknwoledge", 2, -7, Mill.parchmentSadhu, maitreapenser).setSpecial().registerStat();

	public static final Achievement puja = new MillAchievements("puja", -1, -6, Mill.indianstatue, maitreapenser).registerStat();

	public static final Achievement explorer = new MillAchievements("explorer", -3, 1, Items.leather_boots, firstContact).registerStat();

	public static final Achievement marcopolo = new MillAchievements("marcopolo", -4, 3, Items.map, explorer).registerStat();

	public static final Achievement magellan = new MillAchievements("magellan", -6, 5, Items.compass, marcopolo).setSpecial().registerStat();

	public static final Achievement selfdefense = new MillAchievements("selfdefense", -5, 2, Mill.byzantinePlate, explorer).registerStat();

	public static final Achievement pantheon = new MillAchievements("pantheon", -4, -5, Items.sign, explorer).registerStat();

	public static final Achievement darkside = new MillAchievements("darkside", 0, 3, Items.stone_sword, firstContact).registerStat();

	public static final Achievement scipio = new MillAchievements("scipio", -1, 6, Items.iron_sword, darkside).registerStat();

	public static final Achievement attila = new MillAchievements("attila", 2, 9, Mill.normanBroadsword, scipio).setSpecial().registerStat();

	public static final Achievement cheers = new MillAchievements("cheers", 2, 2, Mill.calva, firstContact).registerStat();

	public static final Achievement hired = new MillAchievements("hired", -1, -2, Mill.normanAxe, firstContact).registerStat();

	public static final Achievement masterfarmer = new MillAchievements("masterfarmer", 3, -3, Mill.grapes, firstContact).registerStat();

	public static MillAchievementPage millAchievements = new MillAchievementPage("", firstContact, cresus, summoningwand, villageleader, thequest, maitreapenser, forbiddenknwoledge, puja, explorer,
			marcopolo, magellan, selfdefense, pantheon, darkside, scipio, attila, cheers, hired, masterfarmer);

	public MillAchievements(final String par2Str, final int par3, final int par4, final Block par5Block, final Achievement par6Achievement) {
		super("achievement." + Mill.modId + par2Str, par2Str, par3, par4, par5Block, par6Achievement);
		key = par2Str;
	}

	public MillAchievements(final String par2Str, final int par3, final int par4, final Item par5Item, final Achievement par6Achievement) {
		super("achievement." + Mill.modId + par2Str, par2Str, par3, par4, par5Item, par6Achievement);
		key = par2Str;
	}

	public MillAchievements(final String par2Str, final int par3, final int par4, final ItemStack par5ItemStack, final Achievement par6Achievement) {
		super("achievement." + Mill.modId + par2Str, par2Str, par3, par4, par5ItemStack, par6Achievement);
		key = par2Str;
	}

	// player.addStat(MillAchievements.thequest, 1);

	@Override
	public IChatComponent func_150951_e() {
		return new ChatComponentText(MLN.string("achievement." + key + ".name"));
	}

	@Override
	public String getDescription() {
		return MLN.string("achievement." + key + ".desc");
	}

}
