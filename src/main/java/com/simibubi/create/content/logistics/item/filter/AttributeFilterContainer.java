package com.simibubi.create.content.logistics.item.filter;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllContainerTypes;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.lib.lba.item.ItemStackHandler;
import com.simibubi.create.lib.lba.item.SlotItemHandler;
import com.simibubi.create.lib.utility.Constants.NBT;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class AttributeFilterContainer extends AbstractFilterContainer {

	public enum WhitelistMode {
		WHITELIST_DISJ, WHITELIST_CONJ, BLACKLIST;
	}

	WhitelistMode whitelistMode;
	List<Pair<ItemAttribute, Boolean>> selectedAttributes;

	public AttributeFilterContainer(ContainerType<?> type, int id, PlayerInventory inv, PacketBuffer extraData) {
		super(type, id, inv, extraData);
	}

	public AttributeFilterContainer(ContainerType<?> type, int id, PlayerInventory inv, ItemStack stack) {
		super(type, id, inv, stack);
	}

	public static AttributeFilterContainer create(int id, PlayerInventory inv, ItemStack stack) {
		return new AttributeFilterContainer(AllContainerTypes.ATTRIBUTE_FILTER.get(), id, inv, stack);
	}

	public void appendSelectedAttribute(ItemAttribute itemAttribute, boolean inverted) {
		selectedAttributes.add(Pair.of(itemAttribute, inverted));
	}

	@Override
	public void clearContents() {
		selectedAttributes.clear();
	}

	@Override
	protected void init() {
		super.init();
		ItemStack stack = new ItemStack(Items.NAME_TAG);
		stack.setDisplayName(
			new StringTextComponent("Selected Tags").formatted(TextFormatting.RESET, TextFormatting.BLUE));
		filterInventory.setStackInSlot(1, stack);
	}

	@Override
	protected ItemStackHandler createFilterInventory() {
		return new ItemStackHandler(2);
	}

	protected void addFilterSlots() {
		this.addSlot(new SlotItemHandler(filterInventory, 0, -34, 22));
		this.addSlot(new SlotItemHandler(filterInventory, 1, -28, 57) {
			@Override
			public boolean canTakeStack(PlayerEntity playerIn) {
				return false;
			}
		});
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (slotId == 37)
			return ItemStack.EMPTY;
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public boolean canDragIntoSlot(Slot slotIn) {
		if (slotIn.slotNumber == 37)
			return false;
		return super.canDragIntoSlot(slotIn);
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		if (slotIn.slotNumber == 37)
			return false;
		return super.canMergeSlot(stack, slotIn);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		if (index == 37)
			return ItemStack.EMPTY;
		if (index == 36) {
			filterInventory.setStackInSlot(37, ItemStack.EMPTY);
			return ItemStack.EMPTY;
		}
		if (index < 36) {
			ItemStack stackToInsert = playerInventory.getStackInSlot(index);
			ItemStack copy = stackToInsert.copy();
			copy.setCount(1);
			filterInventory.setStackInSlot(0, copy);
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected int getInventoryOffset() {
		return 83;
	}

	@Override
	protected void readData(ItemStack filterItem) {
		selectedAttributes = new ArrayList<>();
		whitelistMode = WhitelistMode.values()[filterItem.getOrCreateTag()
			.getInt("WhitelistMode")];
		ListNBT attributes = filterItem.getOrCreateTag()
			.getList("MatchedAttributes", NBT.TAG_COMPOUND);
		attributes.forEach(inbt -> {
			CompoundNBT compound = (CompoundNBT) inbt;
			selectedAttributes.add(Pair.of(ItemAttribute.fromNBT(compound), compound.getBoolean("Inverted")));
		});
	}

	@Override
	protected void saveData(ItemStack filterItem) {
		filterItem.getOrCreateTag()
			.putInt("WhitelistMode", whitelistMode.ordinal());
		ListNBT attributes = new ListNBT();
		selectedAttributes.forEach(at -> {
			if (at == null)
				return;
			CompoundNBT compoundNBT = new CompoundNBT();
			at.getFirst().serializeNBT(compoundNBT);
			compoundNBT.putBoolean("Inverted", at.getSecond());
			attributes.add(compoundNBT);
		});
		filterItem.getOrCreateTag()
			.put("MatchedAttributes", attributes);
	}

}
