package com.simibubi.create.lib.util;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings({"UnstableApiUsage"})
public class FluidRenderUtil {
	public static final ResourceLocation WATER_ID = new ResourceLocation("minecraft:blocks/water_still");
	public static final TextureAtlasSprite WATER = Minecraft.getInstance()
			.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WATER_ID);
	public static final ResourceLocation LAVA_ID = new ResourceLocation("minecraft:blocks/lava_still");
	public static final TextureAtlasSprite LAVA = Minecraft.getInstance()
			.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(LAVA_ID);

	public static TextureAtlasSprite getSprite(FluidVariant variant) {
		FluidVariantRenderHandler handler = FluidVariantRendering.getHandler(variant.getFluid());
		if (handler != null) return handler.getSprites(variant)[0];
		Fluid fluid = variant.getFluid();
		if (FluidTags.LAVA.contains(fluid)) return LAVA;
		return WATER;
	}

	public static int getColor(FluidVariant variant) {
		FluidVariantRenderHandler handler = FluidVariantRendering.getHandler(variant.getFluid());
		if (handler != null) return handler.getColor(variant, null, null);
		Fluid fluid = variant.getFluid();
		if (FluidTags.LAVA.contains(fluid)) return 0xFF4914; // gotten from DripParticle.DripstoneLavaFallProvider
		return 0x334CFF; // gotten from DripParticle.DripstoneWaterHangProvider
	}

	public static boolean fillsFromTop(FluidVariant variant) {
		FluidVariantRenderHandler handler = FluidVariantRendering.getHandler(variant.getFluid());
		if (handler != null) return handler.fillsFromTop(variant);
		return false;
	}
}
