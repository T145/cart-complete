package t145.metaltransport.api.consts;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import t145.metaltransport.api.objs.ItemsMT;

public enum ItemCartType implements IStringSerializable {

	COPPER(CartType.COPPER, EntityMinecart.Type.RIDEABLE),
	COPPER_CHEST(CartType.COPPER, EntityMinecart.Type.CHEST),
	COPPER_FURNACE(CartType.COPPER, EntityMinecart.Type.FURNACE),
	COPPER_TNT(CartType.COPPER, EntityMinecart.Type.TNT),
	COPPER_SPAWNER(CartType.COPPER, EntityMinecart.Type.SPAWNER),
	COPPER_HOPPER(CartType.COPPER, EntityMinecart.Type.HOPPER),
	COPPER_COMMAND_BLOCK(CartType.COPPER, EntityMinecart.Type.COMMAND_BLOCK),

	IRON(CartType.IRON, EntityMinecart.Type.RIDEABLE),
	IRON_CHEST(CartType.IRON, EntityMinecart.Type.CHEST),
	IRON_FURNACE(CartType.IRON, EntityMinecart.Type.FURNACE),
	IRON_TNT(CartType.IRON, EntityMinecart.Type.TNT),
	IRON_SPAWNER(CartType.IRON, EntityMinecart.Type.SPAWNER),
	IRON_HOPPER(CartType.IRON, EntityMinecart.Type.HOPPER),
	IRON_COMMAND_BLOCK(CartType.IRON, EntityMinecart.Type.COMMAND_BLOCK),

	SILVER(CartType.SILVER, EntityMinecart.Type.RIDEABLE),
	SILVER_CHEST(CartType.SILVER, EntityMinecart.Type.CHEST),
	SILVER_FURNACE(CartType.SILVER, EntityMinecart.Type.FURNACE),
	SILVER_TNT(CartType.SILVER, EntityMinecart.Type.TNT),
	SILVER_SPAWNER(CartType.SILVER, EntityMinecart.Type.SPAWNER),
	SILVER_HOPPER(CartType.SILVER, EntityMinecart.Type.HOPPER),
	SILVER_COMMAND_BLOCK(CartType.SILVER, EntityMinecart.Type.COMMAND_BLOCK),

	GOLD(CartType.GOLD, EntityMinecart.Type.RIDEABLE),
	GOLD_CHEST(CartType.GOLD, EntityMinecart.Type.CHEST),
	GOLD_FURNACE(CartType.GOLD, EntityMinecart.Type.FURNACE),
	GOLD_TNT(CartType.GOLD, EntityMinecart.Type.TNT),
	GOLD_SPAWNER(CartType.GOLD, EntityMinecart.Type.SPAWNER),
	GOLD_HOPPER(CartType.GOLD, EntityMinecart.Type.HOPPER),
	GOLD_COMMAND_BLOCK(CartType.GOLD, EntityMinecart.Type.COMMAND_BLOCK),

	DIAMOND(CartType.DIAMOND, EntityMinecart.Type.RIDEABLE),
	DIAMOND_CHEST(CartType.DIAMOND, EntityMinecart.Type.CHEST),
	DIAMOND_FURNACE(CartType.DIAMOND, EntityMinecart.Type.FURNACE),
	DIAMOND_TNT(CartType.DIAMOND, EntityMinecart.Type.TNT),
	DIAMOND_SPAWNER(CartType.DIAMOND, EntityMinecart.Type.SPAWNER),
	DIAMOND_HOPPER(CartType.DIAMOND, EntityMinecart.Type.HOPPER),
	DIAMOND_COMMAND_BLOCK(CartType.DIAMOND, EntityMinecart.Type.COMMAND_BLOCK),

	EMERALD(CartType.EMERALD, EntityMinecart.Type.RIDEABLE),
	EMERALD_CHEST(CartType.EMERALD, EntityMinecart.Type.CHEST),
	EMERALD_FURNACE(CartType.EMERALD, EntityMinecart.Type.FURNACE),
	EMERALD_TNT(CartType.EMERALD, EntityMinecart.Type.TNT),
	EMERALD_SPAWNER(CartType.EMERALD, EntityMinecart.Type.SPAWNER),
	EMERALD_HOPPER(CartType.EMERALD, EntityMinecart.Type.HOPPER),
	EMERALD_COMMAND_BLOCK(CartType.EMERALD, EntityMinecart.Type.COMMAND_BLOCK),

	OBSIDIAN(CartType.OBSIDIAN, EntityMinecart.Type.RIDEABLE),
	OBSIDIAN_CHEST(CartType.OBSIDIAN, EntityMinecart.Type.CHEST),
	OBSIDIAN_FURNACE(CartType.OBSIDIAN, EntityMinecart.Type.FURNACE),
	OBSIDIAN_TNT(CartType.OBSIDIAN, EntityMinecart.Type.TNT),
	OBSIDIAN_SPAWNER(CartType.OBSIDIAN, EntityMinecart.Type.SPAWNER),
	OBSIDIAN_HOPPER(CartType.OBSIDIAN, EntityMinecart.Type.HOPPER),
	OBSIDIAN_COMMAND_BLOCK(CartType.OBSIDIAN, EntityMinecart.Type.COMMAND_BLOCK);

	private final CartType type;
	private final EntityMinecart.Type cartType;

	ItemCartType(CartType type, EntityMinecart.Type cartType) {
		this.type = type;
		this.cartType = cartType;
	}

	public CartType getType() {
		return type;
	}

	public EntityMinecart.Type getCartType() {
		return cartType;
	}

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static ItemCartType getEmptyType(CartType type) {
		switch (type) {
		case COPPER:
			return COPPER;
		case SILVER:
			return SILVER;
		case GOLD:
			return GOLD;
		case DIAMOND:
			return DIAMOND;
		case EMERALD:
			return EMERALD;
		case OBSIDIAN:
			return OBSIDIAN;
		default:
			return IRON;
		}
	}

	public static Block getTypeBlock(EntityMinecart.Type type) {
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
		for (ItemCartType type : values()) {
			String recipeName = String.format("%s%s", type.getType().getName(), type.getCartType().getName());

			if (type.getCartType() == EntityMinecart.Type.RIDEABLE) {
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
