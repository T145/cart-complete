package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;

public class DispenserProfile extends ChestProfile {

	public static class DispenserProfileFactory implements ICartProfileFactory {

		@Override
		public DispenserProfile createProfile(EntityMinecart cart) {
			return new DispenserProfile(cart);
		}
	}

	public DispenserProfile(EntityMinecart cart) {
		super(cart, 9);
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		super.activate(player, hand);

		if (!player.world.isRemote) {
			if (this instanceof DropperProfile) {
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