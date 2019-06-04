package T145.metaltransport.api.carts;

import T145.metaltransport.api.constants.CartType;

public interface IMetalMinecart {

	public static final String TAG_CART_TYPE = "CartType";

	CartType getCartType();

	void setCartType(CartType type);
}
