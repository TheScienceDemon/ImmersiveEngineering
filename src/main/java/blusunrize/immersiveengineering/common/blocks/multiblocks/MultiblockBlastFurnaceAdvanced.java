package blusunrize.immersiveengineering.common.blocks.multiblocks;

import org.lwjgl.opengl.GL11;

import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.stone.BlockTypes_StoneDecoration;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityBlastFurnace;
import blusunrize.immersiveengineering.common.util.IEAchievements;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MultiblockBlastFurnaceAdvanced implements IMultiblock
{

	public static MultiblockBlastFurnaceAdvanced instance = new MultiblockBlastFurnaceAdvanced();

	static ItemStack[][][] structure = new ItemStack[4][3][3];
	static{
		for(int h=0;h<4;h++)
			for(int l=0;l<3;l++)
				for(int w=0;w<3;w++)
					if(h==3 && w==1 && l==1)
						structure[h][l][w]=new ItemStack(Blocks.hopper);
					else if(h<3)
						structure[h][l][w]=new ItemStack(IEContent.blockStoneDecoration,1,BlockTypes_StoneDecoration.BLASTBRICK_REINFORCED.getMeta());
	}
	@Override
	public ItemStack[][][] getStructureManual()
	{
		return structure;
	}

	@Override
	public float getManualScale()
	{
		return 14;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean overwriteBlockRender(ItemStack stack, int iterator)
	{
		GL11.glTranslated(0, .5, 0);
		return false;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderFormedStructure()
	{
		return true;
	}
	@SideOnly(Side.CLIENT)
	static ItemStack renderStack;
	@Override
	@SideOnly(Side.CLIENT)
	public void renderFormedStructure()
	{
		if(renderStack==null)
			renderStack = new ItemStack(IEContent.blockStoneDevice,1,BlockTypes_StoneDecoration.BLASTBRICK_REINFORCED.getMeta());
		GlStateManager.scale(6,6,6);
		ClientUtils.mc().getRenderItem().renderItem(renderStack, ItemCameraTransforms.TransformType.GUI);
	}

	@Override
	public String getUniqueName()
	{
		return "IE:BlastFurnaceAdvanced";
	}

	@Override
	public boolean isBlockTrigger(IBlockState state)
	{
		return state.getBlock()==IEContent.blockStoneDecoration && (state.getBlock().getMetaFromState(state)==2);
	}

	@Override
	public boolean createStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player)
	{
		EnumFacing f = EnumFacing.fromAngle(player.rotationYaw);
		pos = pos.offset(f);

		for(int h=-1;h<=2;h++)
			for(int xx=-1;xx<=1;xx++)
				for(int zz=-1;zz<=1;zz++)
					if(h!=2 || (xx==0 && zz==0))
					{
						if(h==2)
						{
							if(!Utils.isBlockAt(world, pos.add(xx, h, zz), Blocks.hopper, -1))
								return false;
						}
						else
						{
							if(!Utils.isBlockAt(world, pos.add(xx, h, zz), IEContent.blockStoneDecoration, BlockTypes_StoneDecoration.BLASTBRICK_REINFORCED.getMeta()))
								return false;
						}
					}

		for(int h=-1;h<=2;h++)
			for(int l=-1;l<=1;l++)
				for(int w=-1;w<=1;w++)
					if(h!=2 || (w==0 && l==0))
					{
						int xx = f==EnumFacing.EAST?l: f==EnumFacing.WEST?-l: f==EnumFacing.NORTH?-w:w;
						int zz = f==EnumFacing.NORTH?l: f==EnumFacing.SOUTH?-l: f==EnumFacing.EAST?w:-w;

						world.setBlockState(pos.add(xx, h, zz), IEContent.blockStoneDevice.getStateFromMeta(2));
						TileEntity curr = world.getTileEntity(pos.add(xx, h, zz));
						if(curr instanceof TileEntityBlastFurnace)
						{
							TileEntityBlastFurnace currBlast = (TileEntityBlastFurnace) curr;
							currBlast.offset=new int[]{xx,h,zz};
							currBlast.pos = (h+1)*9 + (l+1)*3 + (w+1);
							currBlast.facing=f.getOpposite();
							currBlast.formed=true;
							currBlast.markDirty();
						}
					}
		player.triggerAchievement(IEAchievements.mbImprovedBlastFurnace);
		return true;
	}

	static final ItemStack[] materials = new ItemStack[]{new ItemStack(IEContent.blockStoneDecoration,27,BlockTypes_StoneDecoration.BLASTBRICK_REINFORCED.getMeta()),new ItemStack(Blocks.hopper)};
	@Override
	public ItemStack[] getTotalMaterials()
	{
		return materials;
	}
}