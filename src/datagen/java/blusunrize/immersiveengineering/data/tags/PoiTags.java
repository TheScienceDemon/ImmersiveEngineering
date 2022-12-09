package blusunrize.immersiveengineering.data.tags;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.world.Villages.Registers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PoiTags extends PoiTypeTagsProvider
{
	public PoiTags(PackOutput output, CompletableFuture<Provider> provider, @Nullable ExistingFileHelper existingFileHelper)
	{
		super(output, provider, Lib.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider p_256206_)
	{
		TagAppender<PoiType> builder = tag(PoiTypeTags.ACQUIRABLE_JOB_SITE);
		for(var entry : Registers.POINTS_OF_INTEREST.getEntries())
			builder.add(entry.getKey());
	}
}
