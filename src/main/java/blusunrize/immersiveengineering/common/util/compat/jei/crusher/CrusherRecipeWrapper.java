package blusunrize.immersiveengineering.common.util.compat.jei.crusher;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.compat.jei.MultiblockRecipeWrapper;
import mezz.jei.api.IJeiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

public class CrusherRecipeWrapper extends MultiblockRecipeWrapper
{
	public CrusherRecipeWrapper(CrusherRecipe recipe)
	{
		super(recipe);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		GlStateManager.pushMatrix();
		ClientUtils.bindAtlas();
		GlStateManager.translate(70F, 20F, 16.5F);
		GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(80.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(60, -60, 60);
		minecraft.getRenderItem().renderItem(CrusherRecipeCategory.crusherStack, ItemCameraTransforms.TransformType.GUI);
		GlStateManager.popMatrix();
	}

	public static List<CrusherRecipeWrapper> getRecipes(IJeiHelpers jeiHelpers)
	{
		List<CrusherRecipeWrapper> recipes = new ArrayList<>();
		for(CrusherRecipe r : CrusherRecipe.recipeList)
			if(r.input!=null&&(r.input.stack!=null||(r.input.stackList!=null&&!r.input.stackList.isEmpty())||r.input.oreName!=null) && r.output!=null)
				recipes.add(new CrusherRecipeWrapper(r));
		return recipes;
	}
}