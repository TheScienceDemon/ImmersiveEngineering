package blusunrize.immersiveengineering.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.IEProperties.PropertyBoolInverted;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.common.Config;
import blusunrize.immersiveengineering.common.EventHandler;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IActiveState;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ISpawnInterdiction;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IUsesBooleanProperty;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;

public class TileEntityElectricLantern extends TileEntityImmersiveConnectable implements ISpawnInterdiction, ITickable, IBlockBounds, IActiveState
{
	public int energyStorage = 0;
	public boolean active = false;
	private boolean interdictionList=false; 

	@Override
	public void update()
	{
		if(worldObj.isRemote)
			return;
		if(!interdictionList && Config.getBoolean("lantern_spawnPrevent"))
		{
			synchronized (EventHandler.interdictionTiles) {
				if (!EventHandler.interdictionTiles.contains(this))
					EventHandler.interdictionTiles.add(this);
			}
			interdictionList=true;
		}
		boolean b = active;
		if(energyStorage>0)
		{
			energyStorage--;
			if(!active)
				active=true;
		}
		else if(active)
			active=false;

		if(active!=b)
		{
			worldObj.markBlockForUpdate(getPos());
			worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos());
			worldObj.addBlockEvent(getPos(), getBlockType(), 1, 0);
		}
	}

	@Override
	public double getInterdictionRangeSquared()
	{
		return active?1024:0;
	}

	@Override
	public void invalidate()
	{
		synchronized (EventHandler.interdictionTiles) {
			if (EventHandler.interdictionTiles.contains(this))
				EventHandler.interdictionTiles.remove(this);
		}
		super.invalidate();
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		super.readCustomNBT(nbt, descPacket);
		active = nbt.getBoolean("active");
		energyStorage = nbt.getInteger("energyStorage");
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		super.writeCustomNBT(nbt, descPacket);
		nbt.setBoolean("active",active);
		nbt.setInteger("energyStorage",energyStorage);
	}

	@Override
	protected boolean canTakeLV()
	{
		return true;
	}
	@Override
	public boolean isEnergyOutput()
	{
		return true;
	}
	@Override
	public int outputEnergy(int amount, boolean simulate, int energyType)
	{
		if(amount>0 && energyStorage<10)
		{
			if(!simulate)
			{
				int rec = Math.min(10-energyStorage, 2);
				energyStorage+=rec;
				return rec;
			}
			return Math.min(10-energyStorage, 2);
		}
		return 0;
	}
	@Override
	public boolean receiveClientEvent(int id, int arg)
	{
		if(id==1)
		{
			worldObj.markBlockForUpdate(getPos());
			worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos());
			return true;
		}
		return super.receiveClientEvent(id, arg);
	}
	@Override
	public Vec3 getRaytraceOffset(IImmersiveConnectable link)
	{
		int xDif = getPos().getX() - ((TileEntity)link).getPos().getX();
		int zDif = getPos().getZ() - ((TileEntity)link).getPos().getZ();
		if(xDif==0&&zDif==0)
			return new Vec3(.5, .0625, .5);
		else if(Math.abs(xDif)>=Math.abs(zDif))
			return new Vec3(xDif<0?.25:xDif>0?.75:.5, .0625, .5);
		else
			return new Vec3(.5, .0625, zDif<0?.25:zDif>0?.75:.5);
	}
	@Override
	public Vec3 getConnectionOffset(Connection con)
	{
		int xDif = (con==null||con.start==null||con.end==null)?0: (con.start.equals(getPos())&&con.end!=null)? con.end.getX()-getPos().getX(): (con.end.equals(getPos())&& con.start!=null)?con.start.getX()-getPos().getX(): 0;
		int zDif = (con==null||con.start==null||con.end==null)?0: (con.start.equals(getPos())&&con.end!=null)? con.end.getZ()-getPos().getZ(): (con.end.equals(getPos())&& con.start!=null)?con.start.getZ()-getPos().getZ(): 0;
		if(Math.abs(xDif)>=Math.abs(zDif))
			return new Vec3(xDif<0?.25:xDif>0?.75:.5, .0625, .5);
		return new Vec3(.5, .0625, zDif<0?.25:zDif>0?.75:.5);
	}

	@Override
	public float[] getBlockBounds()
	{
		return new float[]{.1875f,0,.1875f, .8125f,1,.8125f};
	}
	@Override
	public float[] getSpecialCollisionBounds()
	{
		return null;
	}
	@Override
	public float[] getSpecialSelectionBounds()
	{
		return null;
	}

	@Override
	public PropertyBoolInverted getBoolProperty(Class<? extends IUsesBooleanProperty> inf)
	{
		return IEProperties.BOOLEANS[0];
	}
	@Override
	public boolean getIsActive()
	{
		return active;
	}
}