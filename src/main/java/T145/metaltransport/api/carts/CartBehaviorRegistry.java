package T145.metaltransport.api.carts;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class CartBehaviorRegistry {

	private static final BiMap<ResourceLocation, ICartBehaviorFactory> BEHAVIORS = HashBiMap.create();

	private CartBehaviorRegistry() {}

	public static void register(ResourceLocation resource, ICartBehaviorFactory factory) {
		BEHAVIORS.put(resource, factory);
	}

	public static void register(Block block, ICartBehaviorFactory factory) {
		register(block.getRegistryName(), factory);
	}

	public static ICartBehaviorFactory get(ResourceLocation resource) {
		return BEHAVIORS.get(resource);
	}

	public static ICartBehaviorFactory get(Block block) {
		return get(block.getRegistryName());
	}

	public static ResourceLocation get(ICartBehaviorFactory factory) {
		return BEHAVIORS.inverse().get(factory);
	}

	public static boolean contains(ResourceLocation resource) {
		return BEHAVIORS.containsKey(resource);
	}

	public static boolean contains(ICartBehaviorFactory factory) {
		return BEHAVIORS.containsValue(factory);
	}
}
