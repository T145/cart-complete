package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.profiles.ICartProfileFactory;
import T145.metaltransport.client.render.items.EnchantingTableTEISR;
import net.minecraft.entity.item.EntityMinecart;

public class EnchantingTableProfile extends SimpleGuiProfile {

	public static class EnchantingTableProfileFactory implements ICartProfileFactory {

		@Override
		public EnchantingTableProfile create(EntityMinecart cart) {
			return new EnchantingTableProfile(cart);
		}
	}

	private final EnchantingTableTEISR render = new EnchantingTableTEISR();

	public EnchantingTableProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public EnchantingTableTEISR getStackRenderer() {
		return render;
	}
}
