package com.simibubi.create.foundation.config;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.lib.config.Config;
import com.simibubi.create.lib.config.ConfigValue;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CStress extends ConfigBase {

	public Config config = new Config(getName());
	@Override
	public Config getConfig() {
		return config;
	}

	private Map<ResourceLocation, ConfigValue<Double>> capacities = new HashMap<>();
	private Map<ResourceLocation, ConfigValue<Double>> impacts = new HashMap<>();

	@Override
	protected void registerAll() {
//		builder.comment("", Comments.su, Comments.impact)
//			.push("impact");
		StressConfigDefaults.registeredDefaultImpacts
			.forEach((r, i) -> getImpacts().put(r, define(r.getPath(), i)));
//		builder.pop();

//		builder.comment("", Comments.su, Comments.capacity)
//			.push("capacity");
		StressConfigDefaults.registeredDefaultCapacities
			.forEach((r, i) -> getCapacities().put(r, define(r.getPath(), i)));
//		builder.pop();
	}

	public static ConfigValue<Double> define(String path, double i) {
		return new ConfigValue<>(path, i);
	}

	public double getImpactOf(Block block) {
		ResourceLocation key = Registry.BLOCK.getKey(block);
		return getImpacts().containsKey(key) ? getImpacts().get(key)
			.get() : 0;
	}

	public double getCapacityOf(Block block) {
		ResourceLocation key = Registry.BLOCK.getKey(block);
		return getCapacities().containsKey(key) ? getCapacities().get(key)
			.get() : 0;
	}

	@Override
	public String getName() {
		return "stressValues.v" + StressConfigDefaults.forcedUpdateVersion;
	}

	public Map<ResourceLocation, ConfigValue<Double>> getImpacts() {
		return impacts;
	}

	public Map<ResourceLocation, ConfigValue<Double>> getCapacities() {
		return capacities;
	}

	private static class Comments {
		static String su = "[in Stress Units]";
		static String impact =
			"Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
		static String capacity = "Configure how much stress a source can accommodate for.";
	}

}
