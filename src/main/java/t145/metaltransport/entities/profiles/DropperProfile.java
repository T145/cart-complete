package t145.metaltransport.entities.profiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.entities.EntityMetalCart;

public class DropperProfile extends DispenserProfile {

	public static class ProfileFactoryDropper implements IProfileFactory {

		@Override
		public DropperProfile create(EntityMinecart cart) {
			return new DropperProfile((EntityMetalCart) cart);
		}
	}

	private final IBehaviorDispenseItem dropBehavior = new BehaviorDefaultDispenseItem();

	public DropperProfile(EntityMetalCart cart) {
		super(cart, Blocks.DROPPER);
	}

	@Nullable
	public IItemHandler getInventoryOverYonder() {
		World world = cart.world;
		EnumFacing front = cart.getHorizontalFacing();
		BlockPos pos = this.cart.getPosition().offset(front);
		TileEntity te = world.getTileEntity(pos);
		EnumFacing targetedSide = front.getOpposite();

		// TODO: Fix crashing when the cap is null
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, targetedSide)) {
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, targetedSide);
		}

		return null;
	}

	public static ItemStack tryToInsertStack(final IItemHandler inv, final @Nonnull ItemStack stack) {
		ItemStack result = stack.copy();

		for (int slot = 0; slot < inv.getSlots(); ++slot) {
			if (!inv.getStackInSlot(slot).isEmpty()) {
				result = inv.insertItem(slot, result, false);

				if (result.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}

		for (int slot = 0; slot < inv.getSlots(); ++slot) {
			result = inv.insertItem(slot, result, false);

			if (result.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return result;
	}

	@Override
	protected void dispenseStack(int slot, ItemStack stack) {
		IItemHandler handler = getInventoryOverYonder();
		ItemStack slotStack;

		if (handler == null) {
			slotStack = this.dropBehavior.dispense(new DispenserSource(), stack);
		} else {
			slotStack = tryToInsertStack(handler, stack);

			if (slotStack.isEmpty()) {
				slotStack = stack.copy();
				slotStack.shrink(1);
			} else {
				slotStack = stack.copy();
			}
		}

		this.dispenser.setInventorySlotContents(slot, slotStack);
	}
}
