package T145.metaltransport.api.consts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public enum CartType implements IStringSerializable {

	COPPER("ingotCopper"),
	IRON("ingotIron"),
	SILVER("ingotSilver"),
	GOLD("ingotGold"),
	DIAMOND("gemDiamond"),
	OBSIDIAN("obsidian");

	private static final Set<CartType> TIERS = new HashSet<>(CartType.values().length);

	static {
		for (CartType type : CartType.values()) {
			if (type.hasOre()) {
				TIERS.add(type);
			}
		}
	}

	private final String oreName;
	private final ResourceLocation modelResource;

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
		return TIERS.contains(this) && hasOre();
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
}
