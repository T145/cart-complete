package t145.metaltransport.api.profiles;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import t145.metaltransport.api.consts.RegistryMT;

public class ProfileRegistry {

	private static final Object2ObjectOpenHashMap<ResourceLocation, IProfileFactory> PROFILES = new Object2ObjectOpenHashMap<>(); 

	private ProfileRegistry() {}

	public static void register(Block block, IProfileFactory factory) {
		if (block.getDefaultState().getMaterial() != Material.AIR) {
			PROFILES.put(block.getRegistryName(), factory);
		} else {
			RegistryMT.LOG.warn("An airy block registration was attempted, and has been blocked.");
		}
	}

	public static IProfileFactory get(ResourceLocation resource) {
		return PROFILES.get(resource);
	}

	public static IProfileFactory get(Block block) {
		return get(block.getRegistryName());
	}

	public static boolean contains(ResourceLocation resource) {
		return PROFILES.containsKey(resource);
	}

	public static boolean contains(Block block) {
		return contains(block.getRegistryName());
	}

	public static boolean contains(IProfileFactory factory) {
		return PROFILES.containsValue(factory);
	}
}
