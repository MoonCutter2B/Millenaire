package org.millenaire.common.block;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;

import org.millenaire.common.MLN;

public class BlockMLNPane extends BlockPane {

	public BlockMLNPane(int i, int j, int k, Material material, boolean flag) {
		super(i, j, k, material, flag);
	}

	@Override
	public String getTextureFile() {
		return MLN.getSpritesPath();
	}
}
