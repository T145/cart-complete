package t145.metaltransport.api.consts;

import com.google.common.collect.Range;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public enum CartTier implements IStringSerializable {

	COPPER("ingotCopper", CartWeight.BANTAM),
	IRON("ingotIron", CartWeight.LIGHT),
	SILVER("ingotSilver", CartWeight.FEATHER),
	GOLD("ingotGold", CartWeight.FEATHER),
	DIAMOND("gemDiamond", CartWeight.MIDDLE),
	EMERALD("gemEmerald", CartWeight.WELTER),
	OBSIDIAN("obsidian", CartWeight.HEAVY);

	private final String ore;
	private final CartWeight weight;
	private final ResourceLocation model;

	CartTier(String ore, CartWeight weight) {
		this.ore = ore;
		this.weight = weight;
		this.model = RegistryMT.getResource(String.format("textures/entity/minecart/%s.png", getName()));
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	public String getOre() {
		return ore;
	}

	public Range<Float> getDurability() {
		return weight.getDurability();
	}

	public ResourceLocation getModel() {
		return model;
	}
}
