package blusunrize.immersiveengineering.client.render;

import blusunrize.immersiveengineering.common.entities.EntityIEExplosive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class EntityRenderIEExplosive extends Render<EntityIEExplosive>
{
	public EntityRenderIEExplosive(RenderManager renderManager)
	{
		super(renderManager);
		this.shadowSize = .5f;
	}

	@Override
	public void doRender(EntityIEExplosive entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		if(entity.block==null)
			return;
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y + 0.5F, (float)z);

        if(entity.fuse-partialTicks+1 < 10)
        {
            float f = 1.0F - ((float)entity.fuse - partialTicks + 1.0F) / 10.0F;
            f = MathHelper.clamp_float(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            GlStateManager.scale(f1, f1, f1);
        }
        
        IBlockState state = entity.block.getStateFromMeta(entity.meta);
        float f2 = (1-(entity.fuse-partialTicks+1)/100F) * .8F;
        this.bindEntityTexture(entity);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(state, entity.getBrightness(partialTicks));
        GlStateManager.translate(0.0F, 0.0F, 1.0F);

        if(entity.fuse/5%2 == 0)
        {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 772);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
    protected ResourceLocation getEntityTexture(EntityIEExplosive entity)
    {
        return TextureMap.locationBlocksTexture;
    }
}