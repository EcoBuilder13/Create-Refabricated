package com.simibubi.create.lib.mixin.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
	@Accessor("leftPos")
	int getGuiLeft();

	@Accessor("topPos")
	int getGuiTop();

	@Accessor("imageWidth")
	int getImageWidth();

	@Accessor("imageHeight")
	int getImageHeight();
}