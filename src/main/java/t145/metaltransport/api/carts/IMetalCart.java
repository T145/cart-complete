package t145.metaltransport.api.carts;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import t145.metaltransport.api.profiles.IProfile;

public interface IMetalCart {

	public static final String TAG_DISPLAY_STACK = "DisplayStack";
	public static final String TAG_UNIVERSAL_PROFILE = "UniversalProfile";
	public static final String TAG_PROFILE = "Profile";

	ItemStack getDisplayStack();

	EntityMinecart setDisplayStack(ItemStack stack);

	boolean hasDisplayStack();

	void setHasDisplayStack(boolean hasStack);

	Block getDisplayBlock();

	boolean hasDisplayBlock();

	Optional<IProfile> getProfile();

	EntityMinecart setProfile();
}