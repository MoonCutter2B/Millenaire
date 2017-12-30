package org.millenaire;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;

public class VillagerType 
{
	final public String id;
	
	final private String nativeName;
	//0 for male, 1 for female, 2 for Sym Female
	final private int gender;
	final private String[] familyNames;
	final private String[] firstNames;
	final public String[] textures;
	
	final public boolean isChief;
	final public boolean canBuild;
	final public int hireCost;

	private List<EntityAIBase>additionalTasks;

	VillagerType(String idIn, String nameIn, int genderIn, String[] familyIn, String[] firstIn, String[] textureIn, boolean chiefIn, boolean buildIn, int hireIn)
	{
		id = idIn;
		nativeName = nameIn;
		gender = genderIn;
		familyNames = familyIn;
		firstNames = firstIn;
		textures = textureIn;
		
		isChief = chiefIn;
		canBuild = buildIn;
		hireCost = hireIn;
	}
	
	public VillagerType addAI(EntityAIBase taskIn)
	{
		this.additionalTasks.add(taskIn);
		return this;
	}
	
	public String getTexture() { return textures[new Random().nextInt(textures.length)]; }
}
