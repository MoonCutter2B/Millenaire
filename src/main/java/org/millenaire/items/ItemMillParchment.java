package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.Reference;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMillParchment extends ItemWritableBook {
	public String title;
	public String[] contents;

	public ItemMillParchment(String titleIn, String[] contentIn) {
		title = titleIn;
		contents = contentIn;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if (worldIn.isRemote) {
			playerIn.openGui(Millenaire.instance, 0, worldIn, playerIn.getPosition().getX(),
					playerIn.getPosition().getY(), playerIn.getPosition().getZ());
		}

		return itemStackIn;
	}

	////////////////////////////////////////////////////////// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	// Declarations
	public static Item normanVillagerParchment;
	public static Item normanBuildingParchment;
	public static Item normanItemParchment;
	public static Item normanAllParchment;

	public static Item byzantineVillagerParchment;
	public static Item byzantineBuildingParchment;
	public static Item byzantineItemParchment;
	public static Item byzantineAllParchment;

	public static Item hindiVillagerParchment;
	public static Item hindiBuildingParchment;
	public static Item hindiItemParchment;
	public static Item hindiAllParchment;

	public static Item mayanVillagerParchment;
	public static Item mayanBuildingParchment;
	public static Item mayanItemParchment;
	public static Item mayanAllParchment;

	public static Item japaneseVillagerParchment;
	public static Item japaneseBuildingParchment;
	public static Item japaneseItemParchment;
	public static Item japaneseAllParchment;

	public static void preinitialize() {
		normanVillagerParchment = new ItemMillParchment("scroll.normanVillager.title",
				new String[] { "scroll.normanVillager.leaders", "scroll.normanVillager.men",
						"scroll.normanVillager.women", "scroll.normanVillager.children" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanVillagerParchment");
		GameRegistry.registerItem(normanVillagerParchment, "normanVillagerParchment");
		normanBuildingParchment = new ItemMillParchment("scroll.normanBuilding.title",
				new String[] { "scroll.normanBuilding.centers", "scroll.normanBuilding.houses",
						"scroll.normanBuilding.uninhabited", "scroll.normanBuilding.player" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanBuildingParchment");
		GameRegistry.registerItem(normanBuildingParchment, "normanBuildingParchment");
		normanItemParchment = new ItemMillParchment("scroll.normanItem.title",
				new String[] { "scroll.normanItem.food", "scroll.normanItem.tools", "scroll.normanItem.weapons",
						"scroll.normanItem.construction" }).setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("normanItemParchment");
		GameRegistry.registerItem(normanItemParchment, "normanItemParchment");
		normanAllParchment = new ItemMillParchment("scroll.normanVillager.title",
				new String[] { "scroll.normanVillager.leaders", "scroll.normanVillager.men",
						"scroll.normanVillager.women", "scroll.normanVillager.children",
						"scroll.normanBuilding.centers", "scroll.normanBuilding.houses",
						"scroll.normanBuilding.uninhabited", "scroll.normanBuilding.player", "scroll.normanItem.food",
						"scroll.normanItem.tools", "scroll.normanItem.weapons", "scroll.normanItem.construction" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanAllParchment");
		GameRegistry.registerItem(normanAllParchment, "normanAllParchment");

		byzantineVillagerParchment = new ItemMillParchment("scroll.byzantineVillager.title",
				new String[] { "scroll.byzantineVillager.leaders", "scroll.byzantineVillager.men",
						"scroll.byzantineVillager.women", "scroll.byzantineVillager.children" })
								.setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("byzantineVillagerParchment");
		GameRegistry.registerItem(byzantineVillagerParchment, "byzantineVillagerParchment");
		byzantineBuildingParchment = new ItemMillParchment("scroll.byzantineBuilding.title",
				new String[] { "scroll.byzantineBuilding.centers", "scroll.byzantineBuilding.houses",
						"scroll.byzantineBuilding.uninhabited", "scroll.byzantineBuilding.player" })
								.setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("byzantineBuildingParchment");
		GameRegistry.registerItem(byzantineBuildingParchment, "byzantineBuildingParchment");
		byzantineItemParchment = new ItemMillParchment("scroll.byzantineItem.title",
				new String[] { "scroll.byzantineItem.food", "scroll.byzantineItem.tools",
						"scroll.byzantineItem.weapons", "scroll.byzantineItem.construction" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineItemParchment");
		GameRegistry.registerItem(byzantineItemParchment, "byzantineItemParchment");
		byzantineAllParchment = new ItemMillParchment("scroll.byzantineVillager.title",
				new String[] { "scroll.byzantineVillager.leaders", "scroll.byzantineVillager.men",
						"scroll.byzantineVillager.women", "scroll.byzantineVillager.children",
						"scroll.byzantineBuilding.centers", "scroll.byzantineBuilding.houses",
						"scroll.byzantineBuilding.uninhabited", "scroll.byzantineBuilding.player",
						"scroll.byzantineItem.food", "scroll.byzantineItem.tools", "scroll.byzantineItem.weapons",
						"scroll.byzantineItem.construction" }).setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("byzantineAllParchment");
		GameRegistry.registerItem(byzantineAllParchment, "byzantineAllParchment");

		hindiVillagerParchment = new ItemMillParchment("scroll.hindiVillager.title",
				new String[] { "scroll.hindiVillager.leaders", "scroll.hindiVillager.men", "scroll.hindiVillager.women",
						"scroll.hindiVillager.children" }).setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("hindiVillagerParchment");
		GameRegistry.registerItem(hindiVillagerParchment, "hindiVillagerParchment");
		hindiBuildingParchment = new ItemMillParchment("scroll.hindiBuilding.title",
				new String[] { "scroll.hindiBuilding.centers", "scroll.hindiBuilding.houses",
						"scroll.hindiBuilding.uninhabited", "scroll.hindiBuilding.player" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("hindiBuildingParchment");
		GameRegistry.registerItem(hindiBuildingParchment, "hindiBuildingParchment");
		hindiItemParchment = new ItemMillParchment("scroll.hindiItem.title",
				new String[] { "scroll.hindiItem.food", "scroll.hindiItem.tools", "scroll.hindiItem.weapons",
						"scroll.hindiItem.construction" }).setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("hindiItemParchment");
		GameRegistry.registerItem(hindiItemParchment, "hindiItemParchment");
		hindiAllParchment = new ItemMillParchment("scroll.hindiVillager.title",
				new String[] { "scroll.hindiVillager.leaders", "scroll.hindiVillager.men", "scroll.hindiVillager.women",
						"scroll.hindiVillager.children", "scroll.hindiBuilding.centers", "scroll.hindiBuilding.houses",
						"scroll.hindiBuilding.uninhabited", "scroll.hindiBuilding.player", "scroll.hindiItem.food",
						"scroll.hindiItem.tools", "scroll.hindiItem.weapons", "scroll.hindiItem.construction" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("hindiAllParchment");
		GameRegistry.registerItem(hindiAllParchment, "hindiAllParchment");

		mayanVillagerParchment = new ItemMillParchment("scroll.mayanVillager.title",
				new String[] { "scroll.mayanVillager.leaders", "scroll.mayanVillager.men", "scroll.mayanVillager.women",
						"scroll.mayanVillager.children" }).setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("mayanVillagerParchment");
		GameRegistry.registerItem(mayanVillagerParchment, "mayanVillagerParchment");
		mayanBuildingParchment = new ItemMillParchment("scroll.mayanBuilding.title",
				new String[] { "scroll.mayanBuilding.centers", "scroll.mayanBuilding.houses",
						"scroll.mayanBuilding.uninhabited", "scroll.mayanBuilding.player" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanBuildingParchment");
		GameRegistry.registerItem(mayanBuildingParchment, "mayanBuildingParchment");
		mayanItemParchment = new ItemMillParchment("scroll.mayanItem.title",
				new String[] { "scroll.mayanItem.food", "scroll.mayanItem.tools", "scroll.mayanItem.weapons",
						"scroll.mayanItem.construction" }).setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("mayanItemParchment");
		GameRegistry.registerItem(mayanItemParchment, "mayanItemParchment");
		mayanAllParchment = new ItemMillParchment("scroll.mayanVillager.title",
				new String[] { "scroll.mayanVillager.leaders", "scroll.mayanVillager.men", "scroll.mayanVillager.women",
						"scroll.mayanVillager.children", "scroll.mayanBuilding.centers", "scroll.mayanBuilding.houses",
						"scroll.mayanBuilding.uninhabited", "scroll.mayanBuilding.player", "scroll.mayanItem.food",
						"scroll.mayanItem.tools", "scroll.mayanItem.weapons", "scroll.mayanItem.construction" })
								.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanAllParchment");
		GameRegistry.registerItem(mayanAllParchment, "mayanAllParchment");

		japaneseVillagerParchment = new ItemMillParchment("scroll.japaneseVillager.title",
				new String[] { "scroll.japaneseVillager.leaders", "scroll.japaneseVillager.men",
						"scroll.japaneseVillager.women", "scroll.japaneseVillager.children" })
								.setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("japaneseVillagerParchment");
		GameRegistry.registerItem(japaneseVillagerParchment, "japaneseVillagerParchment");
		japaneseBuildingParchment = new ItemMillParchment("scroll.japaneseBuilding.title",
				new String[] { "scroll.japaneseBuilding.centers", "scroll.japaneseBuilding.houses",
						"scroll.japaneseBuilding.uninhabited", "scroll.japaneseBuilding.player" })
								.setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("japaneseBuildingParchment");
		GameRegistry.registerItem(japaneseBuildingParchment, "japaneseBuildingParchment");
		japaneseItemParchment = new ItemMillParchment("scroll.japaneseItem.title",
				new String[] { "scroll.japaneseItem.food", "scroll.japaneseItem.tools", "scroll.japaneseItem.weapons",
						"scroll.japaneseItem.construction" }).setCreativeTab(Millenaire.tabMillenaire)
								.setUnlocalizedName("japaneseItemParchment");
		GameRegistry.registerItem(japaneseItemParchment, "japaneseItemParchment");
		japaneseAllParchment = new ItemMillParchment("scroll.japaneseVillager.title", new String[] {
				"scroll.japaneseVillager.leaders", "scroll.japaneseVillager.men", "scroll.japaneseVillager.women",
				"scroll.japaneseVillager.children", "scroll.japaneseBuilding.centers", "scroll.japaneseBuilding.houses",
				"scroll.japaneseBuilding.uninhabited", "scroll.japaneseBuilding.player", "scroll.japaneseItem.food",
				"scroll.japaneseItem.tools", "scroll.japaneseItem.weapons", "scroll.japaneseItem.construction" })
						.setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseAllParchment");
		GameRegistry.registerItem(japaneseAllParchment, "japaneseAllParchment");
	}

	public static void prerender() {
		ModelLoader.setCustomModelResourceLocation(normanVillagerParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentVillager"));
		ModelLoader.setCustomModelResourceLocation(normanBuildingParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentBuilding"));
		ModelLoader.setCustomModelResourceLocation(normanItemParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentItem"));
		ModelLoader.setCustomModelResourceLocation(normanAllParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentAll"));

		ModelLoader.setCustomModelResourceLocation(byzantineVillagerParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentVillager"));
		ModelLoader.setCustomModelResourceLocation(byzantineBuildingParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentBuilding"));
		ModelLoader.setCustomModelResourceLocation(byzantineItemParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentItem"));
		ModelLoader.setCustomModelResourceLocation(byzantineAllParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentAll"));

		ModelLoader.setCustomModelResourceLocation(hindiVillagerParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentVillager"));
		ModelLoader.setCustomModelResourceLocation(hindiBuildingParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentBuilding"));
		ModelLoader.setCustomModelResourceLocation(hindiItemParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentItem"));
		ModelLoader.setCustomModelResourceLocation(hindiAllParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentAll"));

		ModelLoader.setCustomModelResourceLocation(mayanVillagerParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentVillager"));
		ModelLoader.setCustomModelResourceLocation(mayanBuildingParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentBuilding"));
		ModelLoader.setCustomModelResourceLocation(mayanItemParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentItem"));
		ModelLoader.setCustomModelResourceLocation(mayanAllParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentAll"));

		ModelLoader.setCustomModelResourceLocation(japaneseVillagerParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentVillager"));
		ModelLoader.setCustomModelResourceLocation(japaneseBuildingParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentBuilding"));
		ModelLoader.setCustomModelResourceLocation(japaneseItemParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentItem"));
		ModelLoader.setCustomModelResourceLocation(japaneseAllParchment, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":parchmentAll"));
	}
}
