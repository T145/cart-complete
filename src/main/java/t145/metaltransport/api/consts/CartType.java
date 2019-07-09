package t145.metaltransport.api.consts;

import com.google.common.collect.Range;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public enum CartType implements IStringSerializable {

	COPPER("ingotCopper", Range.closed(11F, 20F)),
	IRON("ingotIron", Range.closed(31F, 40F)),
	SILVER("ingotSilver", Range.closed(21F, 30F)),
	GOLD("ingotGold", Range.closed(21F, 30F)),
	DIAMOND("gemDiamond", Range.closed(61F, 70F)),
	EMERALD("gemEmerald", Range.closed(61F, 70F)),
	OBSIDIAN("obsidian", Range.closed(91F, 100F));

	private final String ore;
	private final Range killRange;
	private final ResourceLocation model;

	CartType(String ore, Range killRange) {
		this.ore = ore;
		this.killRange = killRange;
		this.model = RegistryMT.getResource(String.format("textures/entity/minecart/%s.png", getName()));
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	public String getOre() {
		return ore;
	}

	public Range getKillRange() {
		return killRange;
	}

	public ResourceLocation getModel() {
		return model;
	}
}
