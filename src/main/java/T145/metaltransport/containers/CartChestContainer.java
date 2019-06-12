package T145.metaltransport.containers;

import T145.tbone.api.IInventoryHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

public class CartChestContainer extends Container {

	public final IInventoryHandler handler;
	public final BlockPos pos;

	protected void addPlayerInventory(EntityPlayer player, int offsetY) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, offsetY + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, offsetY + 58));
		}
	}

	public CartChestContainer(IInventoryHandler handler, EntityPlayer player, BlockPos pos) {
		this.handler = handler;
		this.pos = pos;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotItemHandler(handler.getInventory(), j + i * 9, 8 + j * 18, 18 + i * 18));
			}
		}

		this.addPlayerInventory(player, 85);
		handler.openInventory(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			itemStack = slotStack.copy();

			if (index < 27) {
				if (!mergeItemStack(slotStack, 27, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 0, 27, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSq(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= 64;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		handler.closeInventory(player);
	}
}