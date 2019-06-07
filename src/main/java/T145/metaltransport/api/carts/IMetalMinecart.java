package T145.metaltransport.api.carts;

import T145.metaltransport.api.constants.CartType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;

public interface IMetalMinecart {

	public static final String TAG_CART_TYPE = "CartType";
	public static final String TAG_DISPLAY = "Display";
	public static final String TAG_ACTION = "Action";

	CartType getCartType();

	EntityMinecart setCartType(CartType type);

	ItemStack getDisplayStack();

	IMetalMinecart setDisplayStack(ItemStack data);
}
