package org.millenaire.common.building;

import net.minecraft.block.Block;

import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.core.MillCommonUtilities;

public class PointType {

	static PointType readColourPoint(final String s) throws MillenaireException {

		final String[] params = s.split(";", -1);

		if (params.length != 5) {
			throw new MillenaireException("Line " + s + " in blocklist.txt does not have five fields.");
		}

		final String[] rgb = params[4].split("/", -1);

		if (rgb.length != 3) {
			throw new MillenaireException("Colour in line " + s + " does not have three values.");
		}

		final int colour = (Integer.parseInt(rgb[0]) << 16) + (Integer.parseInt(rgb[1]) << 8) + (Integer.parseInt(rgb[2]) << 0);

		if (MLN.LogBuildingPlan >= MLN.MAJOR) {
			MLN.major(null, "Loading colour point: " + BuildingPlan.getColourString(colour) + ", " + params[0]);
		}

		if (params[1].length() == 0) {
			return new PointType(colour, params[0]);
		} else {
			return new PointType(colour, params[1], Integer.parseInt(params[2]), Boolean.parseBoolean(params[3]));
		}

	}

	int colour = -1, meta = -1;
	char letter;
	final String name;
	Block block;

	boolean secondStep = false;

	public PointType(final char letter, final Block block, final int meta, final boolean secondStep) {
		this.letter = letter;
		this.block = block;
		this.meta = meta;
		this.secondStep = secondStep;
		name = null;
	}

	public PointType(final char letter, final String name) {
		this.name = name;
		this.letter = letter;
		block = null;
	}

	public PointType(final char letter, final String minecraftBlockName, final int meta, final boolean secondStep) {
		this.letter = letter;
		this.block = Block.getBlockFromName(minecraftBlockName);
		this.meta = meta;
		this.secondStep = secondStep;
		name = null;
	}

	public PointType(final int colour, final String name) {
		this.name = name;
		this.colour = colour;
		block = null;
	}

	public PointType(final int colour, final String minecraftBlockName, final int meta, final boolean secondStep) {
		this.colour = colour;
		this.block = Block.getBlockFromName(minecraftBlockName);
		this.meta = meta;
		this.secondStep = secondStep;
		name = null;
	}

	public Block getBlock() {
		return block;
	}

	public boolean isSubType(final String type) {
		if (name == null) {
			return false;
		}
		return name.startsWith(type);
	}

	public boolean isType(final String type) {
		return type.equalsIgnoreCase(name);
	}

	@Override
	public String toString() {
		return name + "/" + colour + "/" + block + "/" + meta + "/" + MillCommonUtilities.getPointHash(block, meta);
	}
}