package com.simibubi.create.modules.logistics.block.extractor;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.AllShapes;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.modules.logistics.block.IExtractor;
import com.simibubi.create.modules.logistics.block.belts.AttachedLogisiticalBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ExtractorBlock extends AttachedLogisiticalBlock {

	public static BooleanProperty POWERED = BlockStateProperties.POWERED;

	public ExtractorBlock() {
		super();
		setDefaultState(getDefaultState().with(POWERED, false));
	}

	@Override
	protected boolean isVertical() {
		return false;
	}

	@Override
	protected BlockState getVerticalDefaultState() {
		return AllBlocks.VERTICAL_EXTRACTOR.getDefault();
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(POWERED));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ExtractorTileEntity();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(POWERED,
				reactsToRedstone() && context.getWorld().isBlockPowered(context.getPos()));
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		updateObservedInventory(state, worldIn, pos);
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		if (world.isRemote())
			return;
		if (!isObserving(state, pos, neighbor))
			return;
		updateObservedInventory(state, world, pos);
	}

	private void updateObservedInventory(BlockState state, IWorldReader world, BlockPos pos) {
		IExtractor extractor = (IExtractor) world.getTileEntity(pos);
		if (extractor == null)
			return;
		extractor.neighborChanged();
	}

	private boolean isObserving(BlockState state, BlockPos pos, BlockPos observing) {
		return observing.equals(pos.offset(getBlockFacing(state)));
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		if (worldIn.isRemote)
			return;
		if (!reactsToRedstone())
			return;

		boolean previouslyPowered = state.get(POWERED);
		if (previouslyPowered != worldIn.isBlockPowered(pos)) {
			worldIn.setBlockState(pos, state.cycle(POWERED), 2);
			IExtractor extractor = (IExtractor) worldIn.getTileEntity(pos);
			if (extractor == null)
				return;
			extractor.setLocked(!previouslyPowered);
		}
	}

	protected boolean reactsToRedstone() {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return AllShapes.EXTRACTOR.get(getBlockFacing(state));
	}

	public static Vec3d getFilterSlotPosition(BlockState state) {
		float verticalOffset = (state.getBlock() instanceof ExtractorBlock) ? 10.5f : 12.5f;
		
		Vec3d offsetForHorizontal = VecHelper.voxelSpace(8f, verticalOffset, 14f);
		Vec3d offsetForUpward = VecHelper.voxelSpace(8f, 14.15f, 3.5f);
		Vec3d offsetForDownward = VecHelper.voxelSpace(8f, 1.85f, 3.5f);
		Vec3d vec = offsetForHorizontal;

		float yRot = AngleHelper.horizontalAngle(state.get(ExtractorBlock.HORIZONTAL_FACING));
		if (AttachedLogisiticalBlock.isVertical(state))
			vec = state.get(AttachedLogisiticalBlock.UPWARD) ? offsetForUpward : offsetForDownward;

		return VecHelper.rotateCentered(vec, yRot, Axis.Y);
	}

	public static Vec3d getFilterSlotOrientation(BlockState state) {
		float yRot = AngleHelper.horizontalAngle(state.get(ExtractorBlock.HORIZONTAL_FACING));
		float zRot = (AttachedLogisiticalBlock.isVertical(state)) ? 0 : 90;
		return new Vec3d(0, yRot, zRot);
	}

	public static class Vertical extends ExtractorBlock {
		@Override
		protected boolean isVertical() {
			return true;
		}
	}

}