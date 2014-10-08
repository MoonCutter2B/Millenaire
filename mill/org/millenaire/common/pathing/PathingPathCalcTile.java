package org.millenaire.common.pathing;

//A class representing one single chunk in Minecraft.
public class PathingPathCalcTile {

	// true if this tile is a ladder
	public boolean ladder;
	// true if the tile consists of materials on which's surface can be walked
	// cannot be true the same time as ladder!
	public boolean isWalkable;
	// represents the position in the considered block,
	// position[0]=x,position[1]=y,position[2]=z
	public short[] position;

	// constructor for a chunk
	public PathingPathCalcTile(final boolean walkable, final boolean lad, final short[] pos) {
		this.ladder = lad;
		// securing that no tile is ladder and walkable at the same time
		if (this.ladder == true) {
			this.isWalkable = false;
		} else if (this.ladder == false & walkable == true) {
			this.isWalkable = true;
		}
		this.position = pos.clone();
	}

	// copy constructor
	public PathingPathCalcTile(final PathingPathCalcTile c) {
		this.ladder = c.ladder;
		this.isWalkable = c.isWalkable;
		this.position = c.position.clone();
	}

}
