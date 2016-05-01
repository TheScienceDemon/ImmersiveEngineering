package blusunrize.immersiveengineering.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.BlockIETileProvider;
import blusunrize.immersiveengineering.common.blocks.ItemBlockIEBase;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWallmount;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWoodenPost;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel.OBJProperty;

public class BlockMetalDecoration2 extends BlockIETileProvider
{
	public BlockMetalDecoration2()
	{
		super("metalDecoration2", Material.iron, PropertyEnum.create("type", BlockTypes_MetalDecoration2.class), ItemBlockIEBase.class, IEProperties.FACING_ALL,IEProperties.MULTIBLOCKSLAVE,IEProperties.INT_4,OBJProperty.instance);
		this.setHardness(3.0F);
		this.setResistance(15.0F);
	}

	@Override
	public boolean isFullBlock()
	{
		return false;
	}
	@Override
	public boolean isFullCube()
	{
		return false;
	}
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityWoodenPost)
		{
			return ((TileEntityWoodenPost)te).dummy==0?side==EnumFacing.DOWN: ((TileEntityWoodenPost)te).dummy==3?side==EnumFacing.UP: ((TileEntityWoodenPost)te).dummy>3?side.getAxis()==Axis.Y: side.getAxis()!=Axis.Y;
		}
		return super.isSideSolid(world, pos, side);
	}

	@Override
	public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity)
	{
		return (world.getTileEntity(pos) instanceof TileEntityWoodenPost);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		switch(BlockTypes_MetalDecoration2.values()[meta])
		{
		case STEEL_POST:
			return new TileEntityWoodenPost();
		case STEEL_WALLMOUNT:
			return new TileEntityWallmount();
		case ALUMINUM_POST:
			return new TileEntityWoodenPost();
		case ALUMINUM_WALLMOUNT:
			return new TileEntityWallmount();
		case LANTERN:
			return new TileEntityLantern();
		}
		return null;
	}

}