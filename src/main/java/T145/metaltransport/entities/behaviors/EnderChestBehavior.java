package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;

public class EnderChestBehavior extends CartBehavior {

	public static class EnderChestBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new EnderChestBehavior(cart);
		}
	}

	public EnderChestBehavior(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		EntityMinecart cart = this.getCart();

		if (!cart.world.isRemote) {
			// Ender inventory is never null, so why does the block check for it Mojang?
			player.displayGUIChest(player.getInventoryEnderChest());
			player.addStat(StatList.ENDERCHEST_OPENED);
		}
	}
}
