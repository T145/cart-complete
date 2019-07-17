package t145.metaltransport.entities.profiles;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import t145.metaltransport.api.profiles.IProfileFactory;

public class DropperProfile extends DispenserProfile {

	public static class ProfileFactoryDropper implements IProfileFactory {

		@Override
		public DropperProfile create(EntityMinecart cart) {
			return new DropperProfile(cart);
		}
	}

	private final IBehaviorDispenseItem dropBehavior = new BehaviorDefaultDispenseItem();

	public DropperProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.dropper";
	}

	@Override
	public String getGuiID() {
		return "minecraft:dropper";
	}

	@Override
	protected void dispenseStack(int slot, ItemStack stack) {
		this.setInventorySlotContents(slot, this.dropBehavior.dispense(new DispenserSource(), stack));
	}
}
