package t145.metaltransport.entities.profiles;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import t145.metaltransport.api.profiles.IProfile;
import t145.metaltransport.api.profiles.IProfileFactory;

public class EnderChestProfile implements IProfile {

	public static class ProfileFactoryEnderChest implements IProfileFactory {

		@Override
		public EnderChestProfile create(EntityMinecart cart) {
			return new EnderChestProfile(cart);
		}
	}

	private final EntityMinecart cart;

	public EnderChestProfile(EntityMinecart cart) {
		this.cart = cart;
	}

	@Override
	public void tick(World world, BlockPos pos) {
		if (world.isRemote && world.getTotalWorldTime() % 5L == 0L) {
			Blocks.ENDER_CHEST.randomDisplayTick(null, world, pos, world.rand);
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		if (!cart.world.isRemote) {
			player.displayGUIChest(player.getInventoryEnderChest());
		}
	}
}
