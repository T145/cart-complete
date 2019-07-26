package t145.metaltransport.entities.profiles;

import javax.annotation.Nullable;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.entities.EntityMetalCart;
import t145.tbone.lib.ChestHelper;

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

		if (te != null) {
			EnumFacing targetedSide = front.getOpposite();

			if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, targetedSide)) {
				return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, targetedSide);
			} else if (te instanceof ISidedInventory && targetedSide != null) {
				return new SidedInvWrapper((ISidedInventory) te, targetedSide);
			} else if (te instanceof IInventory) {
				return new InvWrapper((IInventory) te);
			}
		}

		return null;
	}

	@Override
	protected void dispenseStack(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}

		IItemHandler handler = getInventoryOverYonder();
		ItemStack slotStack = ItemStack.EMPTY;

		if (handler == null) {
			slotStack = this.dropBehavior.dispense(new DispenserSource(), stack);
		} else {
			slotStack = stack.copy();
			slotStack.setCount(1);
			ChestHelper.tryToInsertStack(handler, slotStack);
			slotStack.setCount(stack.getCount() - 1);
		}

		cart.world.playEvent(1000, cart.getPosition(), 0);
		this.dispenser.setInventorySlotContents(slot, slotStack);
	}
}
