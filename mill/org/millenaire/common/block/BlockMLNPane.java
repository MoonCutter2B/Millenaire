package org.millenaire.common.block;

import org.millenaire.common.core.MillCommonUtilities;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class BlockMLNPane extends BlockPane {

	private Icon sideTexture;
	private final String textureName,sideTextureName;
	
	public BlockMLNPane(int i, String textureName, String sideTexture, Material material, boolean flag) {
		super(i, textureName, sideTexture, material, flag);
		this.textureName=textureName;
		this.sideTextureName=sideTexture;
	}
	
	@Override
	public void func_94332_a(IconRegister iconRegister)
	{
		this.field_94336_cN = MillCommonUtilities.getIcon(iconRegister, textureName);
        this.sideTexture = MillCommonUtilities.getIcon(iconRegister, sideTextureName);
	}
	
	@Override
	public Icon getSideTextureIndex()
    {
        return this.sideTexture;
    }
}
