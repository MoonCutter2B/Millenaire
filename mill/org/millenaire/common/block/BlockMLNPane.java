package org.millenaire.common.block;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockMLNPane extends BlockPane {

	private IIcon sideTexture;
	private final String textureName,sideTextureName;
	
	public BlockMLNPane(String textureName, String sideTexture, Material material, boolean flag) {
		super(textureName, sideTexture, material, flag);
		this.textureName=textureName;
		this.sideTextureName=sideTexture;
		this.setCreativeTab(Mill.tabMillenaire);
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = MillCommonUtilities.getIcon(iconRegister, textureName);
        this.sideTexture = MillCommonUtilities.getIcon(iconRegister, sideTextureName);
	}
	
	//was getSideTextureIndex
	@Override
	public IIcon func_150097_e()
    {
        return this.sideTexture;
    }
}
