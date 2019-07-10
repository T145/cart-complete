package t145.metaltransport.api.consts;

import com.google.common.collect.Range;

public enum CartWeight {

	FLY(Range.closed(1F, 10F)),
	BANTAM(Range.closed(11F, 20F)),
	FEATHER(Range.closed(21F, 30F)),
	LIGHT(Range.closed(31F, 40F)),
	WELTER(Range.closed(51F, 60F)), // normal carts
	MIDDLE(Range.closed(61F, 70F)),
	LIGHT_HEAVY(Range.closed(71F, 80F)),
	HEAVY(Range.closed(81F, 100F));

	private final Range<Float> durability;

	CartWeight(final Range<Float> durability) {
		this.durability = durability;
	}

	public Range<Float> getDurability() {
		return durability;
	}
}
