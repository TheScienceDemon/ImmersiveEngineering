package blusunrize.immersiveengineering.common.entities;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityRevolvershotFlare extends EntityRevolvershot
{
	boolean shootUp = false;
	public int colour = -1;
	final static int dataMarker_colour = 13;

	public EntityRevolvershotFlare(World world)
	{
		super(world);
		this.setTickLimit(400);
	}
	public EntityRevolvershotFlare(World world, double x, double y, double z, double ax, double ay, double az, int type)
	{
		super(world, x, y, z, ax, ay, az, type);
		this.setTickLimit(400);
	}
	public EntityRevolvershotFlare(World world, EntityLivingBase living, double ax, double ay, double az, int type, ItemStack stack)
	{
		super(world, living, ax, ay, az, type, stack);
		this.setTickLimit(400);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(dataMarker_colour, Integer.valueOf(-1));
	}

	public void setColourSynced()
	{
		this.dataWatcher.updateObject(dataMarker_colour, colour);
	}
	public int getColourSynced()
	{
		return this.dataWatcher.getWatchableObjectInt(dataMarker_colour);
	}
	public int getColour()
	{
		return colour;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if(colour<0)
			colour = getColourSynced();
		if(worldObj.isRemote && ticksExisted%1==0)
		{
			float r = (getColour()>>16&255)/255f;
			float g = (getColour()>>8&255)/255f;
			float b = (getColour()&255)/255f;
			ImmersiveEngineering.proxy.spawnRedstoneFX(worldObj, posX,posY,posZ, 0,0,0, 1, r,g,b);
			if(ticksExisted>40)
				for(int i=0; i<20; i++)
				{
					Vec3 v = new Vec3(worldObj.rand.nextDouble()-.5,worldObj.rand.nextDouble()-.5,worldObj.rand.nextDouble()-.5);
					ImmersiveEngineering.proxy.spawnRedstoneFX(worldObj, posX+v.xCoord,posY+v.yCoord,posZ+v.zCoord, v.xCoord/10,v.yCoord/10,v.zCoord/10, 1, r,g,b);
				}
		}
		if(ticksExisted==40)
		{
			motionX = 0;
			motionY = -.1;
			motionZ = 0;
			float r = (getColour()>>16&255)/255f;
			float g = (getColour()>>8&255)/255f;
			float b = (getColour()&255)/255f;
			for(int i=0; i<80; i++)
			{
				Vec3 v = new Vec3((worldObj.rand.nextDouble()-.5)*i>40?2:1,(worldObj.rand.nextDouble()-.5)*i>40?2:1,(worldObj.rand.nextDouble()-.5)*i>40?2:1);
				ImmersiveEngineering.proxy.spawnRedstoneFX(worldObj, posX+v.xCoord,posY+v.yCoord,posZ+v.zCoord, v.xCoord/10,v.yCoord/10,v.zCoord/10, 1, r,g,b);
			}
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if(ticksExisted<=40)
		{
			if(!this.worldObj.isRemote)
				if(mop.entityHit != null)
				{
					if(!mop.entityHit.isImmuneToFire())
						mop.entityHit.setFire(8);
				}
				else if(mop.getBlockPos()!=null)
				{
					BlockPos pos = mop.getBlockPos().offset(mop.sideHit);
					if(this.worldObj.isAirBlock(pos))
						this.worldObj.setBlockState(pos, Blocks.fire.getDefaultState());
				}
			float r = (getColour()>>16&255)/255f;
			float g = (getColour()>>8&255)/255f;
			float b = (getColour()&255)/255f;
			for(int i=0; i<80; i++)
			{
				Vec3 v = new Vec3((worldObj.rand.nextDouble()-.5)*i>40?2:1,(worldObj.rand.nextDouble()-.5)*i>40?2:1,(worldObj.rand.nextDouble()-.5)*i>40?2:1);
				ImmersiveEngineering.proxy.spawnRedstoneFX(worldObj, posX+v.xCoord,posY+v.yCoord,posZ+v.zCoord, v.xCoord/10,v.yCoord/10,v.zCoord/10, 1, r,g,b);
			}
		}
		this.setDead();
	}
}