package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;

public class PistonBehavior extends CartBehavior {

	public static class PistonBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new PistonBehavior(cart);
		}
	}

	public PistonBehavior(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public IBlockState customizeState(IBlockState state) {
		return state.withProperty(BlockPistonBase.FACING, EnumFacing.UP);
	}
}
