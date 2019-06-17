package T145.metaltransport.api.carts;

import java.util.Optional;

import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.profiles.ICartProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;

public interface IMetalMinecart {

	public static final String TAG_CART_TYPE = "CartType";
	public static final String TAG_DISPLAY_STACK = "DisplayStack";
	public static final String TAG_HAS_STACK = "HasStack";
	public static final String TAG_CART_PROFILE = "CartProfile";

	CartType getCartType();

	EntityMinecart setCartType(CartType type);

	ItemStack getDisplayStack();

	EntityMinecart setDisplayStack(ItemStack stack);

	boolean hasDisplayStack();

	void setHasDisplayStack(boolean hasStack);

	Block getDisplayBlock();

	boolean hasDisplayBlock();

	Optional<ICartProfile> getCartProfile();

	EntityMinecart setCartProfile();
}
