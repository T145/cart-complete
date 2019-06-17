package T145.metaltransport.api.carts;

import java.util.HashMap;
import java.util.Map;

import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class CartProfileRegistry {

	private static final Map<ResourceLocation, ICartProfileFactory> PROFILES = new HashMap<>();

	private CartProfileRegistry() {}

	public static void register(ResourceLocation key, ICartProfileFactory factory) {
		PROFILES.put(key, factory);
	}

	public static void register(Block block, ICartProfileFactory factory) {
		PROFILES.put(block.getRegistryName(), factory);
	}

	public static ICartProfileFactory get(ResourceLocation resource) {
		return PROFILES.get(resource);
	}

	public static ICartProfileFactory get(Block block) {
		return get(block.getRegistryName());
	}

	public static boolean contains(ResourceLocation resource) {
		return PROFILES.containsKey(resource);
	}

	public static boolean contains(Block block) {
		return contains(block.getRegistryName());
	}

	public static boolean contains(ICartProfileFactory factory) {
		return PROFILES.containsValue(factory);
	}
}
