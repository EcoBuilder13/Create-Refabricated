package com.simibubi.create.content.contraptions.fluids;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.content.contraptions.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedTileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.lib.render.ModelRenderingUtil;
import com.simibubi.create.lib.render.VirtualRenderingStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public class PipeAttachmentModel extends ForwardingBakedModel {

	public PipeAttachmentModel(BakedModel template) {
		wrapped = template;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		if (VirtualRenderingStateManager.getVirtualState()) {
			return;
		}

		PipeModelData data = new PipeModelData();
		FluidTransportBehaviour transport = TileEntityBehaviour.get(blockView, pos, FluidTransportBehaviour.TYPE);
		BracketedTileEntityBehaviour bracket = TileEntityBehaviour.get(blockView, pos, BracketedTileEntityBehaviour.TYPE);

		if (transport != null)
			for (Direction d : Iterate.directions)
				data.putRim(d, transport.getRenderedRimAttachment(blockView, pos, state, d));
		if (bracket != null)
			data.putBracket(bracket.getBracket());

		data.setEncased(FluidPipeBlock.shouldDrawCasing(blockView, pos, state));

		for (Direction d : Iterate.directions)
			if (data.hasRim(d))
				ModelRenderingUtil.emitBlockQuadsChecked(AllBlockPartials.PIPE_ATTACHMENTS.get(data.getRim(d)).get(d).get(), blockView, state, pos, randomSupplier, context);
		if (data.isEncased())
			ModelRenderingUtil.emitBlockQuadsChecked(AllBlockPartials.FLUID_PIPE_CASING.get(), blockView, state, pos, randomSupplier, context);
		BakedModel bracket1 = data.getBracket();
		if (bracket1 != null)
			ModelRenderingUtil.emitBlockQuadsChecked(bracket1, blockView, state, pos, randomSupplier, context);
	}

	private class PipeModelData {
		AttachmentTypes[] rims;
		boolean encased;
		BakedModel bracket;

		public PipeModelData() {
			rims = new AttachmentTypes[6];
			Arrays.fill(rims, AttachmentTypes.NONE);
		}

		public void putBracket(BlockState state) {
			this.bracket = Minecraft.getInstance()
				.getBlockRenderer()
				.getBlockModel(state);
		}

		public BakedModel getBracket() {
			return bracket;
		}

		public void putRim(Direction face, AttachmentTypes rim) {
			rims[face.get3DDataValue()] = rim;
		}

		public void setEncased(boolean encased) {
			this.encased = encased;
		}

		public boolean hasRim(Direction face) {
			return rims[face.get3DDataValue()] != AttachmentTypes.NONE;
		}

		public AttachmentTypes getRim(Direction face) {
			return rims[face.get3DDataValue()];
		}

		public boolean isEncased() {
			return encased;
		}
	}

}
