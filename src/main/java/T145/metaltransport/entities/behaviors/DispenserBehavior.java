package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;

public class DispenserBehavior extends ChestBehavior {

	public static class DispenserBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new DispenserBehavior(cart);
		}
	}

	public DispenserBehavior(EntityMinecart cart) {
		super(cart, 9);
	}

	@Override
	public boolean renderAsItem() {
		return true;
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		super.activate(player, hand);

		if (!player.world.isRemote) {
			if (this instanceof DropperBehavior) {
				player.addStat(StatList.DROPPER_INSPECTED);
			} else {
				player.addStat(StatList.DISPENSER_INSPECTED);
			}
		}
	}

	@Override
	public String getName() {
		return "container.dispenser";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
		return new ContainerDispenser(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		return "minecraft:dispenser";
	}
}
