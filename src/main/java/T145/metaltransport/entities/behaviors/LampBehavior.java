package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class LampBehavior extends CartBehavior {

	public LampBehavior() {
		super(new Block[] { Blocks.REDSTONE_LAMP, Blocks.LIT_REDSTONE_LAMP });
	}

	@Override
	public void onActivatorRailPass(EntityMinecart cart, int x, int y, int z, boolean powered) {
		World world = cart.world;
		IBlockState state = world.getBlockState(cart.getPosition());
		Block block = state.getBlock();

		if (block instanceof BlockRailPowered && state.getValue(BlockRailPowered.POWERED)) {
			cart.setDisplayTile(Blocks.LIT_REDSTONE_LAMP.getDefaultState());
		} else if (cart.getDisplayTile().getBlock() == Blocks.LIT_REDSTONE_LAMP) {
			cart.setDisplayTile(Blocks.REDSTONE_LAMP.getDefaultState());
		}
	}
}
