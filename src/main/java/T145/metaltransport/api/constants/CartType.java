package T145.metaltransport.api.constants;

import java.util.Arrays;

import T145.metaltransport.api.ItemsMT;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public enum CartType implements IStringSerializable {

	COPPER("ingotCopper"),
	IRON("ingotIron", "textures/entity/minecart.png"),
	SILVER("ingotSilver"),
	GOLD("ingotGold"),
	DIAMOND("gemDiamond"),
	OBSIDIAN("obsidian");

	private final String oreName;
	private final ResourceLocation modelResource;

	CartType(String oreName, String modelPath) {
		this.oreName = oreName;
		this.modelResource = new ResourceLocation(modelPath);
	}

	CartType(String oreName) {
		this.oreName = oreName;
		this.modelResource = RegistryMT.getResource(String.format("textures/entity/minecart/%s.png", getName()));
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	public ResourceLocation getResource() {
		return modelResource;
	}

	public String getOreName() {
		return oreName;
	}

	public boolean hasOre() {
		return OreDictionary.doesOreNameExist(oreName);
	}

	public boolean isRegistered() {
		return hasOre() /* && TIERS.contains(this) */;
	}

	public static CartType byMetadata(int meta) {
		return values()[meta];
	}

	public static CartType byOreName(String oreName) {
		return Arrays.stream(values()).filter(type -> type.getOreName() == oreName).findFirst().get();
	}

	public String getIdName() {
		return String.format("%s:%s_cart", RegistryMT.ID, getName());
	}

	public static void registerRecipes() {
		for (short i = 0; i < values().length; ++i) {
			CartType type = CartType.byMetadata(i);

			if (type.isRegistered()) {
				GameRegistry.addShapedRecipe(new ResourceLocation(RegistryMT.ID, String.format("recipe_%s_cart", type.getName())), RegistryMT.RECIPE_GROUP,
						new ItemStack(ItemsMT.METAL_MINECART, 1, i),
						"a a", "aaa",
						'a', type.getOreName());
			}
		}
	}
}
