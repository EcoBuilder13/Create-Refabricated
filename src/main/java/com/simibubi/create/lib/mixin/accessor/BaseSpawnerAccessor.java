package com.simibubi.create.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {
	@Accessor("spawnPotentials")
	SimpleWeightedRandomList<SpawnData> create$spawnPotentials();

	@Accessor("nextSpawnData")
	SpawnData create$nextSpawnData();
}
