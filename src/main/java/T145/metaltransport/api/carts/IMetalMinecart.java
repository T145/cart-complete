package T145.metaltransport.api.carts;

import java.util.Optional;

import T145.metaltransport.api.constants.CartType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;

public interface IMetalMinecart {

	public static final String TAG_DISPLAY = "Display";
	public static final String TAG_BEHAVIOR = "Behavior";
	public static final String TAG_CART_TYPE = "CartType";

	ItemStack getDisplayStack();

	IMetalMinecart setDisplayStack(ItemStack data);

	Optional<ICartBehavior> getBehavior();

	IMetalMinecart setBehavior();

	CartType getCartType();

	EntityMinecart setCartType(CartType type);
}
