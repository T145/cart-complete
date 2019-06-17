package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.carts.CartProfile;
import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class LampProfile extends CartProfile {

	public static class LampProfileFactory implements ICartProfileFactory {

		@Override
		public LampProfile create(EntityMinecart cart) {
			return new LampProfile(cart);
		}
	}

	public LampProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean powered) {
		EntityMinecart cart = this.getCart();
		World world = cart.world;
		IBlockState state = world.getBlockState(cart.getPosition());

		if (state.getBlock() instanceof BlockRailPowered && state.getValue(BlockRailPowered.POWERED)) {
			cart.setDisplayTile(Blocks.LIT_REDSTONE_LAMP.getDefaultState());
		} else if (cart.getDisplayTile().getBlock() == Blocks.LIT_REDSTONE_LAMP) {
			cart.setDisplayTile(Blocks.REDSTONE_LAMP.getDefaultState());
		}
	}
}
