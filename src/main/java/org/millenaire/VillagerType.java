package org.millenaire;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;

public class VillagerType 
{
	final public String id;
	
	final public String nativeName;
	//0 for male, 1 for female, 2 for Sym Female
	final public int gender;
	final public String[] familyNames;
	final public String[] firstNames;
	final public String[] textures;
	
	public List<EntityAIBase>additionalTasks;
	
	public VillagerType(String idIn, String nameIn, int genderIn, String[] familyIn, String[] firstIn, String[] textureIn)
	{
		id = idIn;
		nativeName = nameIn;
		gender = genderIn;
		familyNames = familyIn;
		firstNames = firstIn;
		textures = textureIn;
	}
	
	public VillagerType addAI(EntityAIBase taskIn)
	{
		this.additionalTasks.add(taskIn);
		return this;
	}
	
	public String getTexture()
	{
		Random rand = new Random();
		return textures[rand.nextInt(textures.length)];
	}
}
