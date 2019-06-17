package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.entity.item.EntityMinecart;

public class DropperProfile extends DispenserProfile {

	public static class DropperProfileFactory implements ICartProfileFactory {

		@Override
		public DropperProfile create(EntityMinecart cart) {
			return new DropperProfile(cart);
		}
	}

	public DropperProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public String getName() {
		return "container.dropper";
	}

	@Override
	public String getGuiID() {
		return "minecraft:dropper";
	}
}