package T145.metaltransport.api.carts;

import T145.metaltransport.api.constants.CartType;
import net.minecraft.entity.item.EntityMinecart;

public interface IMetalMinecart {

	public static final String TAG_CART_TYPE = "CartType";
	public static final String TAG_DISPLAY_DATA = "DisplayData";

	CartType getCartType();

	EntityMinecart setCartType(CartType type);
}
