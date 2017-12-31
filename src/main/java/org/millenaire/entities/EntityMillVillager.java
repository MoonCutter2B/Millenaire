package org.millenaire.entities;

import java.util.List;

import org.millenaire.MillCulture;
import org.millenaire.Millenaire;
import org.millenaire.VillagerType;
import org.millenaire.entities.ai.EntityAIGateOpen;
import org.millenaire.gui.MillAchievement;
import org.millenaire.pathing.MillPathNavigate;
import org.millenaire.rendering.RenderMillVillager;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityMillVillager extends EntityCreature
{
	public int villagerID;
	private MillCulture culture;
	private VillagerType type;
	private final static int TEXTURE = 13;
	private final static int AGE = 14;
	private final static int GENDER = 16;
	private final static int NAME = 17;

	private boolean isVillagerSleeping = false;
	public boolean isPlayerInteracting = false;
	
	private InventoryBasic villagerInventory;
	
	public EntityMillVillager(World worldIn)
	{
		super(worldIn);
		
		this.villagerInventory = new InventoryBasic("Items", false, 16);
		isImmuneToFire = true;
		this.setSize(0.6F, 1.8F);
		addTasks();
	}

	public EntityMillVillager(World worldIn, int idIn, MillCulture cultureIn)
	{
		super(worldIn);
		villagerID = idIn;
		culture = cultureIn;
		
		this.villagerInventory = new InventoryBasic("Items", false, 16);
		isImmuneToFire = true;
		this.setSize(0.6F, 1.8F);
		addTasks();
	}
	
	private void addTasks()
	{
		//((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
		//((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(1, new EntityAIGateOpen(this, true));
		this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 0.5F));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityMillVillager.class, 6.0F));
		this.tasks.addTask(9, new EntityAIWander(this, 0.6D));
	}
	
	@Override
    protected PathNavigate getNewNavigator(World worldIn)
    {
        return new MillPathNavigate(this, worldIn);
    }
	
	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.55D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
    }
	
	@Override
	public void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(TEXTURE, "texture");
        this.dataWatcher.addObject(NAME, "name");
        //0 is Adult
        this.dataWatcher.addObject(AGE, 0);
        //0 for male, 1 for female, 2 for Sym Female
        this.dataWatcher.addObject(GENDER, 0);
    }
	
	public EntityMillVillager setTypeAndGender(VillagerType typeIn, int genderIn)
	{
		this.type = typeIn;
		this.dataWatcher.updateObject(GENDER, genderIn);
		this.dataWatcher.updateObject(TEXTURE, type.getTexture());
		return this;
	}
	
	public void setChild() { this.dataWatcher.updateObject(AGE, 1); }
	
	public void setName(String nameIn) { this.dataWatcher.updateObject(NAME, nameIn); }
	
	public String getTexture() { return this.dataWatcher.getWatchableObjectString(13); }
	
	public int getGender() { return dataWatcher.getWatchableObjectInt(GENDER); }
	
	public String getName() { return this.dataWatcher.getWatchableObjectString(NAME); }

	public VillagerType getVillagerType() { return type; }
	
	@Override
	public boolean isChild() { return (this.dataWatcher.getWatchableObjectInt(AGE) > 0); }
	
    public boolean allowLeashing() { return false; }
    
    @Override
    public void onDeath(DamageSource cause)
    {
    	InventoryHelper.dropInventoryItems(this.worldObj, this.getPosition(), this.villagerInventory);
    }
    
    //Controls what happens when Villager encounters an Item on ground
    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity)
    {
    	
    }
	
	/*@Override
	public void attackEntity(final Entity entity, final float f) {
		if (vtype.isArcher && f > 5 && hasBow()) {
			attackEntityBow(entity, f);
			isUsingBow = true;
		} else {
			if (attackTime <= 0 && f < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY) {
				attackTime = 20;
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength());
				swingItem();
			}
			isUsingHandToHand = true;
		}
	}*/
	

	/*public void attackEntityBow(final Entity entity, final float f) {
		if (!(entity instanceof EntityLivingBase)) {
			return;
		}

		if (f < 10F) {
			final double d = entity.posX - posX;
			final double d1 = entity.posZ - posZ;
			if (attackTime == 0) {

				float speedFactor = 1;
				float damageBonus = 0;

				final ItemStack weapon = getWeapon();

				if (weapon != null) {
					final Item item = weapon.getItem();

					if (item instanceof ItemMillenaireBow) {
						final ItemMillenaireBow bow = (ItemMillenaireBow) item;

						if (bow.speedFactor > speedFactor) {
							speedFactor = bow.speedFactor;
						}
						if (bow.damageBonus > damageBonus) {
							damageBonus = bow.damageBonus;
						}
					}
				}

				final EntityArrow arrow = new EntityArrow(this.worldObj, this, (EntityLivingBase) entity, 1.6F, 12.0F);

				this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
				this.worldObj.spawnEntityInWorld(arrow);

				attackTime = 60;

				// faster MLN arrows
				arrow.motionX *= speedFactor;
				arrow.motionY *= speedFactor;
				arrow.motionZ *= speedFactor;

				// extra arrow damage
				arrow.setDamage(arrow.getDamage() + damageBonus);
			}
			rotationYaw = (float) (Math.atan2(d1, d) * 180D / 3.1415927410125732D) - 90F;
			hasAttacked = true;
		}
	}*/

	/*@Override
	public boolean attackEntityFrom(final DamageSource ds, final float i) {

		if (ds.getSourceOfDamage() == null && ds != DamageSource.outOfWorld) {
			return false;
		}

		final boolean hadFullHealth = getMaxHealth() == getHealth();

		final boolean b = super.attackEntityFrom(ds, i);

		final Entity entity = ds.getSourceOfDamage();

		lastAttackByPlayer = false;

		if (entity != null) {
			if (entity instanceof EntityLivingBase) {
				if (entity instanceof EntityPlayer) {
					lastAttackByPlayer = true;

					final EntityPlayer player = (EntityPlayer) entity;

					if (!isRaider) {
						if (!vtype.hostile) {
							MillCommonUtilities.getServerProfile(player.worldObj, player.getDisplayName()).adjustReputation(getTownHall(), (int) (-i * 10));
						}
						if (worldObj.difficultySetting != EnumDifficulty.PEACEFUL && this.getHealth() < getMaxHealth() - 10) {
							entityToAttack = entity;
							clearGoal();
							if (getTownHall() != null) {
								getTownHall().callForHelp(entity);
							}
						}

						if (hadFullHealth && (player.getHeldItem() == null || MillCommonUtilities.getItemWeaponDamage(player.getHeldItem().getItem()) <= 1) && !worldObj.isRemote) {
							ServerSender.sendTranslatedSentence(player, MLN.ORANGE, "ui.communicationexplanations");
						}
					}

					if (lastAttackByPlayer && getHealth() <= 0) {
						if (vtype.hostile) {
							player.addStat(MillAchievements.selfdefense, 1);
						} else {
							player.addStat(MillAchievements.darkside, 1);
						}
					}

				} else {
					entityToAttack = entity;
					clearGoal();

					if (getTownHall() != null) {
						getTownHall().callForHelp(entity);
					}

				}
			}
		}

		return b;
	}*/
	
	//maybe in other class(if changed to Vanilla Villager)
	@Override
	public boolean canDespawn() 
	{
		return false;
	}
	
	//Goals need to be a thing
	
	/*public void detrampleCrops() 
	{
		if (getPosition().sameBlock(prevPoint) && (previousBlock == Blocks.wheat || previousBlock instanceof BlockMillCrops) && getBlock(getPosition()) != Blocks.air
				&& getBlock(getPosition().getBelow()) == Blocks.dirt) {
			setBlock(getPosition(), previousBlock);
			setBlockMetadata(getPosition(), previousBlockMeta);
			setBlock(getPosition().getBelow(), Blocks.farmland);
		}

		previousBlock = getBlock(getPosition());
		previousBlockMeta = getBlockMeta(getPosition());
	}*/
	
	// emptied to prevent generic code from turning the villagers' heads toward
	// the player
	//@Override
	//public void faceEntity(final Entity par1Entity, final float par2, final float par3) {}
	
	public void faceDirection()
	{
		//Face an Entity or specific BlockPos when we want then to
	}

	//Foreign Merchant leaves at night if stock is empty && price goes up by 1.5 if in a different culture
	
	//Find function for checking armor equipment, should return the equipment if in inventory
	
	//GetBedOrientation()?
	
	//Set up where Villagers have an appropriate tool in inventory, tool updates as village expands/building upgrades
	
	//Pick up EntityItems???
	
	//Remember to use setCurrentItemOrArmor
	
	@Override
	protected int getExperiencePoints(final EntityPlayer playerIn) 
	{
		//return villagertype.expgiven;
		return super.getExperiencePoints(playerIn);
	}
	
	//GetOccupationTitle(EntityPlayer playerIn)
	
	//getSpeech/Dialogue(EntityPlayer playerIn)
	//Make this smart to language learning
	
	/*public Item[] getGoodsToBringBackHome() 
	{
		return vtype.bringBackHomeGoods;
	}

	public Item[] getGoodsToCollect() 
	{
		return vtype.collectGoods;
	}*/

	/*public int getHireCost(final EntityPlayer player) 
	{

		int cost = vtype.hireCost;

		//if (getTownHall().controlledBy(player.getDisplayName())) {
		//	cost = cost / 2;
		//}

		return cost;
	}*/
	
	/*public String getName() 
	{
		return firstName + " " + familyName;
	}*/
	
	//handleDoorsAndFenceGates() needs to be malisis-compatible(dummy player with villagers rotationYaw)
	
	@Override
	public boolean interact(final EntityPlayer playerIn) 
	{
		playerIn.addStat(MillAchievement.firstContact, 1);
		if(type.hireCost > 0)
		{
			this.isPlayerInteracting = true;
			playerIn.openGui(Millenaire.instance, 5, playerIn.worldObj, this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
			return true;
		}
		if(type.isChief)
		{
			this.isPlayerInteracting = true;
			playerIn.openGui(Millenaire.instance, 4, playerIn.worldObj, this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
			return true;
		}
		//for Sadhu and Alchemist maitrepenser achievement
		//Display Quest GUI if appropriate
		//Display Hire GUI if Appropriate
		//Display Chief GUI if Chief
		// Display Trade Window if Trading (Foreign Merchant, trading for Townhall or local shop)
		return false;
	}
	
	/**
	 * Dead and sleeping entities cannot move
	 */
	@Override
	protected boolean isMovementBlocked() 
	{
		return this.getHealth() <= 0 || this.isVillagerSleeping || this.isPlayerInteracting;
	}
	
	//When Villager dies, the entity is dead, per normal.  Drop stuff and display messages. Respawn must just create another instance of the same villager (reason to store culture info in V. Stone)
	//Why villagerID is important
	
	//Local merchants have inn or townhall as 'house', handle moving them, taking items from townhall, and what happens if inn is full
	
	@Override
	public void onLivingUpdate() 
	{
		super.onLivingUpdate();

		this.updateArmSwingProgress();

		//setFacingDirection(); Look toward goal (or entity to attack, but I think attacking something is a goal)(entityToAttack also used in raids and against stupid players, may still be possible to use goal)

		if (isVillagerSleeping) {
			motionX = 0;
			motionY = 0;
			motionZ = 0;
		}
	}
	
	//teenager leaving to find other village...possibly useful in creating new villages?
	
	//be smart with teleportTo, use coordinates or entity, check for surrounding blocks
	
	@Override
	public void onUpdate() 
	{
		//Check(isRemote) and do nothing?
		
		if (this.isDead) 
		{
			super.onUpdate();
			return;
		}
		
		if(isPlayerInteracting)
		{
			List<EntityPlayer> playersNear = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(posX - 5, posY - 1, posZ - 5, posX + 5, posY + 1, posZ + 5));
			
			if(playersNear.isEmpty())
				isPlayerInteracting = false;
		}

		/*if (hiredBy != null) 
		{
			//updateHired();
			super.onUpdate();
			return;
		}*/
		
		//if(village is under attack)
		//{
			//Clear other goals and either hide or defend.
		//}
		
		//Check to Attack something (needs to be more player specific)
		
		//Check Time (day or night)
			//Chance to speak something		SPEAKING SENTENCES AND DIALOGUE CAN BE CLIENT-SIDE!!
			//pick up items?
			//updateLocalMerchant()
			//updateForeignMerchant()
			//checkGoals()
		//checkGoals() (sleep...?)
		
		//Update path finding
			//check Long Distance Stuck
			//check short Distance Stuck
			//handleDoorsAndFenceGates
			//setPathing
		
		//sendUpdatePacket to client
		
		//trigger Mob Attacks on Villagers (currently only spiders, which is odd...villagers will seek out other mobs, but should be targetable).
		
		//update Dialogue?
		
		//put away weapons (might revise this to check if attacking first)
		
		super.onUpdate();
	}
	
	//performNightActions() does not appear to be called in the code anymore...perhaps it has been outdated?  This undoes growChildSize, conception, and ForiegnMerchantNightAction
	
	@Override
	public void writeToNBT(final NBTTagCompound nbt) 
	{
		super.writeToNBT(nbt);
		nbt.setInteger("villagerID", villagerID);
		nbt.setString("culture", culture.cultureName);
		nbt.setInteger("gender", this.dataWatcher.getWatchableObjectInt(GENDER));
		nbt.setString("villagerType", type.id);
		nbt.setBoolean("sleeping", isVillagerSleeping);
		
		nbt.setString("texture", this.dataWatcher.getWatchableObjectString(TEXTURE));
		nbt.setInteger("age", this.dataWatcher.getWatchableObjectInt(AGE));
		nbt.setString("name", this.dataWatcher.getWatchableObjectString(NAME));
		
		NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.villagerInventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.villagerInventory.getStackInSlot(i);

            if (itemstack != null)
            {
                nbttaglist.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
            }
        }
        nbt.setTag("Inventory", nbttaglist);
		//Write in All relevant data
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		villagerID = nbt.getInteger("villagerID");
		try 
		{
			culture = MillCulture.getCulture(nbt.getString("culture"));
		} 
		catch (Exception ex) 
		{
			System.err.println("Villager failed to read from NBT correctly");
			ex.printStackTrace();
		}
		if(culture == null)
		{
			System.out.println("Fix this shit!");
			culture.getChildType(GENDER);
		}
		this.dataWatcher.updateObject(GENDER, nbt.getInteger("gender"));
		type = culture.getVillagerType(nbt.getString("villagerType"));
		isVillagerSleeping = nbt.getBoolean("sleeping");
		
		this.dataWatcher.updateObject(TEXTURE, nbt.getString("texture"));
		this.dataWatcher.updateObject(AGE, nbt.getInteger("age"));
		this.dataWatcher.updateObject(NAME, nbt.getString("name"));
		
		NBTTagList nbttaglist = nbt.getTagList("Inventory", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i));

            if (itemstack != null)
            {
                this.villagerInventory.func_174894_a(itemstack);
            }
        }
		//Read in all relevant data
	}
	
	@Override
	public String toString() 
	{
		return this.getClass().getSimpleName() + "@" + ": " + getName() + "/" + this.villagerID + "/" + worldObj;
	}
	
	//Update Texture for Byzantines with silk clothes, possibly further expand on this
	
	private void updateHired() 
	{
		//find target (base this on stance, change stance in onInteract)
		
		//pathFind to entity you want to attack (or following player)
		//handledoorsandfencegates
	}
	
	//override onlivingsound?
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static void preinitialize()
	{
		EntityRegistry.registerModEntity(EntityMillVillager.class, "millVillager", 0, Millenaire.instance, 80, 3, false);
	}
	
	public static void prerender()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityMillVillager.class, new millVillagerRenderFactory());
	}
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static class millVillagerRenderFactory implements IRenderFactory<EntityMillVillager>
	{
		@Override
		public Render<EntityMillVillager> createRenderFor(RenderManager manager) 
		{
			return new RenderMillVillager(manager, new ModelBiped(), 0.5F);
		}
	}
}
