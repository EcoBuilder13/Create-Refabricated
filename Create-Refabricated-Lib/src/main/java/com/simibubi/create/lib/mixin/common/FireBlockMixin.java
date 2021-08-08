package com.simibubi.create.lib.mixin.common;

import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.lib.extensions.FireBlockExtensions;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends BaseFireBlock implements FireBlockExtensions {
	@Shadow
	@Final
	private static Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP;

	@Shadow
	protected abstract int func_220274_q(BlockState blockState);

	private FireBlockMixin(Properties properties, float f) {
		super(properties, f);
	}

	public int doFunc_220274_q(BlockState state) {
		return func_220274_q(state);
	}

	/**
	 * Even though this is an overwrite it's 100x cleaner than the alternatives I tried
	 * @author Tropheus Jay
	 */
	@Overwrite
	public BlockState getStateForPlacement(BlockGetter iBlockReader, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.below();
		BlockState blockState = iBlockReader.getBlockState(blockPos2);
		if (!this.canBurn(blockState) && !blockState.isFaceSturdy(iBlockReader, blockPos2, Direction.UP)) {
			BlockState blockState2 = this.defaultBlockState();
			Direction[] var6 = Direction.values();
			int var7 = var6.length;

			for (int var8 = 0; var8 < var7; ++var8) {
				Direction direction = var6[var8];
				BooleanProperty booleanProperty = FACING_TO_PROPERTY_MAP.get(direction);
				if (booleanProperty != null) {
					blockState2 = blockState2.setValue(booleanProperty, this.canBurn(blockState2) || canCatchFire(iBlockReader, blockPos, direction));
				}
			}

			return blockState2;
		} else {
			return this.defaultBlockState();
		}
	}

	@Inject(at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 1, target = "Ljava/util/Random;nextInt(I)I"),
			method = "scheduledTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V", cancellable = true)
	public void create$scheduledTick(BlockState blockState, ServerLevel serverWorld, BlockPos blockPos, Random random, CallbackInfo ci) {
		if (blockState.getValue(FireBlock.AGE) == 15 && random.nextInt(4) == 0 && !canCatchFire(serverWorld, blockPos.below(), Direction.UP)) {
			serverWorld.removeBlock(blockPos, false);
			ci.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/block/FireBlock;canBurn(Lnet/minecraft/block/BlockState;)Z"),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			method = "areNeighborsFlammable(Lnet/minecraft/world/BlockGetter;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
	private void create$areNeighborsFlammable(BlockGetter iBlockReader, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir,
											  Direction[] var3, int var4, int var5, Direction direction) {
		if (this.canCatchFire(iBlockReader, blockPos.relative(direction), direction.getOpposite())) {
			cir.setReturnValue(true);
		}
	}
}