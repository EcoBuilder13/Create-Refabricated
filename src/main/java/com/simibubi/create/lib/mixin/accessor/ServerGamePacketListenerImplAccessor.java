package com.simibubi.create.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplAccessor {
	@Accessor("aboveGroundTickCount")
	int create$aboveGroundTickCount();

	@Accessor("aboveGroundTickCount")
	void create$aboveGroundTickCount(int floatingTicks);
}
