package org.millenaire.common.block;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockMLNPane extends BlockPane {

	private IIcon sideTexture;
	private final String textureName, sideTextureName;

	public BlockMLNPane(final String textureName, final String sideTexture,
			final Material material, final boolean flag) {
		super(textureName, sideTexture, material, flag);
		this.textureName = textureName;
		this.sideTextureName = sideTexture;
		this.setCreativeTab(Mill.tabMillenaire);
	}

	// was getSideTextureIndex
	@Override
	public IIcon func_150097_e() {
		return this.sideTexture;
	}

	@Override
	public void registerBlockIcons(final IIconRegister iconRegister) {
		this.blockIcon = MillCommonUtilities.getIcon(iconRegister, textureName);
		this.sideTexture = MillCommonUtilities.getIcon(iconRegister,
				sideTextureName);
	}
}
