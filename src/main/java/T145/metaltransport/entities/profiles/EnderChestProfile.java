package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.carts.CartProfile;
import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;

public class EnderChestProfile extends CartProfile {

	public static class EnderChestProfileFactory implements ICartProfileFactory {

		@Override
		public EnderChestProfile create(EntityMinecart cart) {
			return new EnderChestProfile(cart);
		}
	}

	public EnderChestProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		// Ender inventory is never null, so why does the block check for it Mojang?
		player.displayGUIChest(player.getInventoryEnderChest());
		player.addStat(StatList.ENDERCHEST_OPENED);
	}
}
