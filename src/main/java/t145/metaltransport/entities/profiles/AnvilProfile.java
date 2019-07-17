package t145.metaltransport.entities.profiles;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;

public class AnvilProfile implements IServerProfile {

	public static class ProfileFactoryAnvil implements IProfileFactory {

		@Override
		public AnvilProfile create(EntityMinecart cart) {
			return new AnvilProfile(cart);
		}
	}

	private final EntityMinecart cart;

	public AnvilProfile(EntityMinecart cart) {
		this.cart = cart;
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		BlockPos pos = cart.getPosition();
		player.openGui(RegistryMT.ID, cart.hashCode(), cart.world, pos.getX(), pos.getY(), pos.getZ());
	}
}
