package blusunrize.immersiveengineering.client.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler.MineralMix;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

@SuppressWarnings("deprecation")
public class ModelCoresample implements ISmartItemModel, IPerspectiveAwareModel
{
	Set<BakedQuad> bakedQuads;
	static List<BakedQuad> emptyQuads = Lists.newArrayList();
	MineralMix mineral;

	public ModelCoresample(MineralMix mineral)
	{
		this.mineral = mineral;
	}
	public ModelCoresample()
	{
		this(null);
	}
	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_)
	{
		return emptyQuads;
	}

	@Override
	public List<BakedQuad> getGeneralQuads()
	{
		if(bakedQuads==null)
		{
			try{
				bakedQuads = Collections.synchronizedSet(new LinkedHashSet<BakedQuad>());
				float width = .25f;
				float depth = .25f;
				float wOff = (1-width)/2;
				float dOff = (1-depth)/2;
				int pixelLength = 0;

				HashMap<TextureAtlasSprite, Integer> textureOre = new HashMap();
				if(mineral!=null && mineral.oreOutput!=null)
				{
					for(int i=0; i<mineral.oreOutput.length; i++)
						if(mineral.oreOutput[i]!=null)
						{
							int weight = Math.max(2, Math.round(16*mineral.recalculatedChances[i]));
							Block b = Block.getBlockFromItem(mineral.oreOutput[i].getItem());
							IBlockState state = b!=null?b.getStateFromMeta(mineral.oreOutput[i].getMetadata()): Blocks.stone.getDefaultState();
							IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
							if(model!=null && model.getParticleTexture()!=null)
								textureOre.put(model.getParticleTexture(), weight);
							pixelLength += weight;
						}
				}
				else
					pixelLength=16;
				float length = pixelLength/16f;

				TextureAtlasSprite textureStone = ClientUtils.getSprite(new ResourceLocation("blocks/stone"));

				Vector2f[] stoneUVs = {
						new Vector2f(textureStone.getInterpolatedU(16*wOff),textureStone.getInterpolatedV(16*dOff)),
						new Vector2f(textureStone.getInterpolatedU(16*wOff),textureStone.getInterpolatedV(16*(dOff+depth))),
						new Vector2f(textureStone.getInterpolatedU(16*(wOff+width)),textureStone.getInterpolatedV(16*(dOff+depth))),
						new Vector2f(textureStone.getInterpolatedU(16*(wOff+width)),textureStone.getInterpolatedV(16*dOff))};

				putVertexData(new Vector3f(0,-1,0), new Vector3f[]{new Vector3f(wOff,0,dOff),new Vector3f(wOff+width,0,dOff),new Vector3f(wOff+width,0,dOff+depth),new Vector3f(wOff,0,dOff+depth)}, stoneUVs);
				putVertexData(new Vector3f(0,1,0), new Vector3f[]{new Vector3f(wOff,length,dOff),new Vector3f(wOff,length,dOff+depth),new Vector3f(wOff+width,length,dOff+depth),new Vector3f(wOff+width,length,dOff)}, stoneUVs);
				if(textureOre.isEmpty())
				{
					Vector2f[][] uvs = new Vector2f[4][];
					for(int j=0; j<4; j++)
						uvs[j] = new Vector2f[]{
								new Vector2f(textureStone.getInterpolatedU(j*4),textureStone.getInterpolatedV(0)),
								new Vector2f(textureStone.getInterpolatedU(j*4),textureStone.getInterpolatedV(16)),
								new Vector2f(textureStone.getInterpolatedU((j+1)*4),textureStone.getInterpolatedV(16)),
								new Vector2f(textureStone.getInterpolatedU((j+1)*4),textureStone.getInterpolatedV(0))};

					putVertexData(new Vector3f(0,0,-1), new Vector3f[]{new Vector3f(wOff,0,dOff),new Vector3f(wOff,length,dOff),new Vector3f(wOff+width,length,dOff),new Vector3f(wOff+width,0,dOff)}, uvs[0]);
					putVertexData(new Vector3f(0,0,1), new Vector3f[]{new Vector3f(wOff+width,0,dOff+depth),new Vector3f(wOff+width,length,dOff+depth),new Vector3f(wOff,length,dOff+depth),new Vector3f(wOff,0,dOff+depth)}, uvs[2]);
					putVertexData(new Vector3f(-1,0,0), new Vector3f[]{new Vector3f(wOff,0,dOff+depth),new Vector3f(wOff,length,dOff+depth),new Vector3f(wOff,length,dOff),new Vector3f(wOff,0,dOff)}, uvs[3]);
					putVertexData(new Vector3f(1,0,0), new Vector3f[]{new Vector3f(wOff+width,0,dOff),new Vector3f(wOff+width,length,dOff),new Vector3f(wOff+width,length,dOff+depth),new Vector3f(wOff+width,0,dOff+depth)}, uvs[1]);
				}
				else
				{
					float h=0;
					for(TextureAtlasSprite sprite : textureOre.keySet())
					{
						int weight = textureOre.get(sprite);
						int v = weight>8?16-weight:8;
						Vector2f[][] uvs = new Vector2f[4][];
						for(int j=0; j<4; j++)
							uvs[j] = new Vector2f[]{
									new Vector2f(sprite.getInterpolatedU(j*4),sprite.getInterpolatedV(v)),
									new Vector2f(sprite.getInterpolatedU(j*4),sprite.getInterpolatedV(v+weight)),
									new Vector2f(sprite.getInterpolatedU((j+1)*4),sprite.getInterpolatedV(v+weight)),
									new Vector2f(sprite.getInterpolatedU((j+1)*4),sprite.getInterpolatedV(v))};

						float h1 = weight/(float)pixelLength;
						putVertexData(new Vector3f(0,0,-1), new Vector3f[]{new Vector3f(wOff,h,dOff),new Vector3f(wOff,h+h1,dOff),new Vector3f(wOff+width,h+h1,dOff),new Vector3f(wOff+width,h,dOff)}, uvs[0]);
						putVertexData(new Vector3f(0,0,1), new Vector3f[]{new Vector3f(wOff+width,h,dOff+depth),new Vector3f(wOff+width,h+h1,dOff+depth),new Vector3f(wOff,h+h1,dOff+depth),new Vector3f(wOff,h,dOff+depth)}, uvs[2]);
						putVertexData(new Vector3f(-1,0,0), new Vector3f[]{new Vector3f(wOff,h,dOff+depth),new Vector3f(wOff,h+h1,dOff+depth),new Vector3f(wOff,h+h1,dOff),new Vector3f(wOff,h,dOff)}, uvs[3]);
						putVertexData(new Vector3f(1,0,0), new Vector3f[]{new Vector3f(wOff+width,h,dOff),new Vector3f(wOff+width,h+h1,dOff),new Vector3f(wOff+width,h+h1,dOff+depth),new Vector3f(wOff+width,h,dOff+depth)}, uvs[1]);
						h += h1;
					}
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(bakedQuads!=null && !bakedQuads.isEmpty())
		{
			List<BakedQuad> quadList = Collections.synchronizedList(Lists.newArrayList(bakedQuads));
			return quadList;
		}
		return emptyQuads;
	}

	protected final void putVertexData(Vector3f normal, Vector3f[] vertices, Vector2f[] uvs)
	{
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.ITEM);
		builder.setQuadOrientation(EnumFacing.getFacingFromVector(normal.x, normal.y, normal.z));
		builder.setQuadColored();
		for(int i=0; i<vertices.length; i++)
		{
			builder.put(0, vertices[i].x, vertices[i].y, vertices[i].z, 1);//Pos
			float d = LightUtil.diffuseLight(normal.x, normal.y, normal.z);
			builder.put(1, d, d, d, 1);//Colour
			builder.put(2, uvs[i].x, uvs[i].y, 0, 1);//UV
			builder.put(3, normal.x, normal.y, normal.z, 0);//Normal
			builder.put(4);//padding
		}
		bakedQuads.add(builder.build());
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return true;
	}
	@Override
	public boolean isGui3d()
	{
		return true;
	}
	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}
	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return ItemCameraTransforms.DEFAULT;
	}

	static HashMap<String, IBakedModel> modelCache = new HashMap();
	@Override
	public IBakedModel handleItemState(ItemStack stack)
	{
		if(ItemNBTHelper.hasKey(stack, "mineral"))
		{
			String name = ItemNBTHelper.getString(stack, "mineral");
			if(name!=null && !name.isEmpty())
			{
				if(!modelCache.containsKey(name))
					for(MineralMix mix : ExcavatorHandler.mineralList.keySet())
						if(name.equals(mix.name))
							modelCache.put(name, new ModelCoresample(mix));
				return modelCache.get(name);
			}
		}
		return this;
	}
	@Override
	public VertexFormat getFormat()
	{
		return DefaultVertexFormats.ITEM;
	}

	static HashMap<TransformType, Matrix4> transformationMap = new HashMap<TransformType, Matrix4>();
	static{
		transformationMap.put(TransformType.FIRST_PERSON, new Matrix4().translate(0, .28, 0).rotate(Math.toRadians(180), 1,0,0).rotate(Math.toRadians(-90), 0,1,0));
		transformationMap.put(TransformType.THIRD_PERSON, new Matrix4().translate(0, .0625, -.125).scale(.625, .625, .625).rotate(Math.toRadians(30), 1,0,0).rotate(Math.toRadians(130), 0,1,0));
		transformationMap.put(TransformType.GUI, new Matrix4().scale(1.25,1.25,1.25).rotate(Math.toRadians(180), 1,0,0).rotate(Math.toRadians(20), 0,1,0).rotate(Math.toRadians(-30), 0,0,1));
		transformationMap.put(TransformType.FIXED, new Matrix4().scale(1.5,1.5,1.5).rotate(Math.toRadians(180), 1,0,0));
		transformationMap.put(TransformType.GROUND, new Matrix4().scale(1.5,1.5,1.5).rotate(Math.toRadians(180), 1,0,0));
	}
	@Override
	public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
	{
		if(transformationMap==null)
			return  Pair.of(this, TRSRTransformation.identity().getMatrix());
		Matrix4 matrix = transformationMap.containsKey(cameraTransformType)?transformationMap.get(cameraTransformType):new Matrix4();
		return Pair.of(this, matrix.toMatrix4f());
	}
}