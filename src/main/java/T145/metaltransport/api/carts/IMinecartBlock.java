package T145.metaltransport.api.carts;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.item.ItemStack;

public interface IMinecartBlock {

	public IBlockState getDisplayState(EntityMinecartEmpty cart, ItemStack data);
}
