package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;

public class ObserverBehavior extends CartBehavior {

	public static class ObserverBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new ObserverBehavior(cart);
		}
	}

	public ObserverBehavior(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public boolean renderAsItem() {
		return true;
	}

	@Override
	public IBlockState customizeState(IBlockState state) {
		return state.withProperty(BlockDirectional.FACING, EnumFacing.NORTH);
	}
}
