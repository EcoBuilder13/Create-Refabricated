package com.simibubi.create.lib.mixin.accessor;

import java.util.List;

import net.minecraft.client.gui.components.Widget;

import net.minecraft.client.gui.narration.NarratableEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Coerce;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public interface ScreenAccessor {
	@Accessor("minecraft")
	Minecraft create$client();

	@Accessor("renderables")
	List<Widget> create$getRenderables();

	@Accessor("children")
	List<GuiEventListener> create$getChildren();
}
