package com.simibubi.create.lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.lib.event.BlockPlaceCallback;
import com.simibubi.create.lib.item.CustomMaxCountItem;
import com.simibubi.create.lib.util.MixinHelper;
import com.simibubi.create.lib.util.NBTSerializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements NBTSerializable {
	@Inject(at = @At("HEAD"), method = "getMaxStackSize()I", cancellable = true)
	public void create$onGetMaxCount(CallbackInfoReturnable<Integer> cir) {
		ItemStack self = (ItemStack) (Object) this;
		Item item = self.getItem();
		if (item instanceof CustomMaxCountItem) {
			cir.setReturnValue(((CustomMaxCountItem) item).getItemStackLimit(self));
		}
	}

	@Override
	public CompoundTag create$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		MixinHelper.<ItemStack>cast(this).save(nbt);
		return nbt;
	}

	@Override
	public void create$deserializeNBT(CompoundTag nbt) {
		MixinHelper.<ItemStack>cast(this).setTag(ItemStack.of(nbt).getTag());
	}

	@Inject(at = @At("HEAD"),
			method = "useOn", cancellable = true)
	public void onItemUse(UseOnContext itemUseContext, CallbackInfoReturnable<InteractionResult> cir) {
		if (!itemUseContext.getLevel().isClientSide) {
			InteractionResult result = BlockPlaceCallback.EVENT.invoker().onBlockPlace(itemUseContext);
			if (result != InteractionResult.PASS)
				cir.setReturnValue(result);
		}
	}
}
