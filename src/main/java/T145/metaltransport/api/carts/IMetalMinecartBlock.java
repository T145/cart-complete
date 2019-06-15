package T145.metaltransport.api.carts;

import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.item.ItemStack;

public interface IMetalMinecartBlock {

	/*
	 * Returns the ItemStack rendered by a metal minecart.
	 * 
	 * @param cart Instance of the minecart
	 * 
	 * @param meta The metadata value of the current block
	 * 
	 * @return ItemStack to be rendered
	 */
	public ItemStack getDisplayStack(EntityMinecartEmpty cart, int meta);

	/*
	 * Blocks rendered like this would be pressure plates & carpets.
	 * 
	 * @return Whether or not a block should be flatly rendered in a metal minecart.
	 */
	public boolean shouldRenderFlatly();

	/*
	 * @return Whether or not a block should have its tile entity rendered in a metal minecart.
	 */
	public boolean shouldRenderTileEntity();
}
