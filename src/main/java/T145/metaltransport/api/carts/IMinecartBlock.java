package T145.metaltransport.api.carts;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.item.ItemStack;

public interface IMinecartBlock {

	/*
	 * Returns the block state rendered by a metal minecart.
	 * 
	 * @param cart Instance of the minecart
	 * 
	 * @param data ItemStack representing the initially added stack
	 * 
	 * @return Block state to be rendered
	 */
	public IBlockState getDisplayState(EntityMinecartEmpty cart, ItemStack data);

	/*
	 * Whether or not a block should be flatly rendered in a metal minecart.
	 * Blocks rendered like this would be pressure plates & carpets.
	 * 
	 * @return Whether or not this block should be rendered flatly.
	 */
	public boolean shouldRenderFlatly();
}
