package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleGuiBehavior extends CartBehavior {

	public static class SimpleGuiBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new SimpleGuiBehavior(cart);
		}
	}

	public SimpleGuiBehavior(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		EntityMinecart cart = this.getCart();
		World world = cart.world;

		if (!world.isRemote) {
			IBlockState state = cart.getDisplayTile();
			BlockPos pos = cart.getPosition();
			Block block = state.getBlock();

			player.openGui(RegistryMT.ID, cart.getEntityId(), world, pos.getX(), pos.getY(), pos.getZ());

			if (block instanceof BlockWorkbench) {
				player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
			}
		}
	}
}
