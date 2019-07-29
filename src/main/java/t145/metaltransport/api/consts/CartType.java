package t145.metaltransport.api.consts;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart.Type;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import t145.metaltransport.api.objs.ItemsMT;

public enum CartType implements IStringSerializable {

	COPPER_EMPTY(CartTier.COPPER, Type.RIDEABLE),
	COPPER_CHEST(CartTier.COPPER, Type.CHEST),
	COPPER_FURNACE(CartTier.COPPER, Type.FURNACE),
	COPPER_TNT(CartTier.COPPER, Type.TNT),
	COPPER_SPAWNER(CartTier.COPPER, Type.SPAWNER),
	COPPER_HOPPER(CartTier.COPPER, Type.HOPPER),
	COPPER_COMMAND_BLOCK(CartTier.COPPER, Type.COMMAND_BLOCK),

	IRON_EMPTY(CartTier.IRON, Type.RIDEABLE),
	IRON_CHEST(CartTier.IRON, Type.CHEST),
	IRON_FURNACE(CartTier.IRON, Type.FURNACE),
	IRON_TNT(CartTier.IRON, Type.TNT),
	IRON_SPAWNER(CartTier.IRON, Type.SPAWNER),
	IRON_HOPPER(CartTier.IRON, Type.HOPPER),
	IRON_COMMAND_BLOCK(CartTier.IRON, Type.COMMAND_BLOCK),

	SILVER_EMPTY(CartTier.SILVER, Type.RIDEABLE),
	SILVER_CHEST(CartTier.SILVER, Type.CHEST),
	SILVER_FURNACE(CartTier.SILVER, Type.FURNACE),
	SILVER_TNT(CartTier.SILVER, Type.TNT),
	SILVER_SPAWNER(CartTier.SILVER, Type.SPAWNER),
	SILVER_HOPPER(CartTier.SILVER, Type.HOPPER),
	SILVER_COMMAND_BLOCK(CartTier.SILVER, Type.COMMAND_BLOCK),

	GOLD_EMPTY(CartTier.GOLD, Type.RIDEABLE),
	GOLD_CHEST(CartTier.GOLD, Type.CHEST),
	GOLD_FURNACE(CartTier.GOLD, Type.FURNACE),
	GOLD_TNT(CartTier.GOLD, Type.TNT),
	GOLD_SPAWNER(CartTier.GOLD, Type.SPAWNER),
	GOLD_HOPPER(CartTier.GOLD, Type.HOPPER),
	GOLD_COMMAND_BLOCK(CartTier.GOLD, Type.COMMAND_BLOCK),

	DIAMOND_EMPTY(CartTier.DIAMOND, Type.RIDEABLE),
	DIAMOND_CHEST(CartTier.DIAMOND, Type.CHEST),
	DIAMOND_FURNACE(CartTier.DIAMOND, Type.FURNACE),
	DIAMOND_TNT(CartTier.DIAMOND, Type.TNT),
	DIAMOND_SPAWNER(CartTier.DIAMOND, Type.SPAWNER),
	DIAMOND_HOPPER(CartTier.DIAMOND, Type.HOPPER),
	DIAMOND_COMMAND_BLOCK(CartTier.DIAMOND, Type.COMMAND_BLOCK),

	EMERALD_EMPTY(CartTier.EMERALD, Type.RIDEABLE),
	EMERALD_CHEST(CartTier.EMERALD, Type.CHEST),
	EMERALD_FURNACE(CartTier.EMERALD, Type.FURNACE),
	EMERALD_TNT(CartTier.EMERALD, Type.TNT),
	EMERALD_SPAWNER(CartTier.EMERALD, Type.SPAWNER),
	EMERALD_HOPPER(CartTier.EMERALD, Type.HOPPER),
	EMERALD_COMMAND_BLOCK(CartTier.EMERALD, Type.COMMAND_BLOCK),

	OBSIDIAN_EMPTY(CartTier.OBSIDIAN, Type.RIDEABLE),
	OBSIDIAN_CHEST(CartTier.OBSIDIAN, Type.CHEST),
	OBSIDIAN_FURNACE(CartTier.OBSIDIAN, Type.FURNACE),
	OBSIDIAN_TNT(CartTier.OBSIDIAN, Type.TNT),
	OBSIDIAN_SPAWNER(CartTier.OBSIDIAN, Type.SPAWNER),
	OBSIDIAN_HOPPER(CartTier.OBSIDIAN, Type.HOPPER),
	OBSIDIAN_COMMAND_BLOCK(CartTier.OBSIDIAN, Type.COMMAND_BLOCK);

	private final CartTier type;
	private final Type cartType;

	CartType(CartTier type, Type cartType) {
		this.type = type;
		this.cartType = cartType;
	}

	public CartTier getType() {
		return type;
	}

	public Type getCartType() {
		return cartType;
	}

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static CartType getEmptyType(CartTier tier) {
		switch (tier) {
		case COPPER:
			return COPPER_EMPTY;
		case SILVER:
			return SILVER_EMPTY;
		case GOLD:
			return GOLD_EMPTY;
		case DIAMOND:
			return DIAMOND_EMPTY;
		case EMERALD:
			return EMERALD_EMPTY;
		case OBSIDIAN:
			return OBSIDIAN_EMPTY;
		default:
			return IRON_EMPTY;
		}
	}

	public static Block getTypeBlock(Type type) {
		switch (type) {
		case CHEST:
			return Blocks.CHEST;
		case COMMAND_BLOCK:
			return Blocks.COMMAND_BLOCK;
		case FURNACE:
			return Blocks.FURNACE;
		case HOPPER:
			return Blocks.HOPPER;
		case SPAWNER:
			return Blocks.MOB_SPAWNER;
		case TNT:
			return Blocks.TNT;
		default:
			return Blocks.AIR;
		}
	}

	public static void registerRecipes() {
		for (CartType type : values()) {
			String recipeName = String.format("%s%s", type.getType().getName(), type.getCartType().getName());

			if (type.getCartType() == Type.RIDEABLE) {
				GameRegistry.addShapedRecipe(new ResourceLocation(RegistryMT.ID, WordUtils.capitalize(recipeName)), RegistryMT.RECIPE_GROUP,
						new ItemStack(ItemsMT.METAL_MINECART, 1, type.ordinal()),
						"a a", "aaa",
						'a', type.getType().getOre());
			} else {
				GameRegistry.addShapedRecipe(new ResourceLocation(RegistryMT.ID, WordUtils.capitalize(recipeName)), RegistryMT.RECIPE_GROUP,
						new ItemStack(ItemsMT.METAL_MINECART, 1, type.ordinal()),
						"a", "b",
						'a', new ItemStack(getTypeBlock(type.cartType)),
						'b', new ItemStack(ItemsMT.METAL_MINECART, 1, getEmptyType(type.type).ordinal()));
			}

			// register to oredict?
		}
	}
}
