package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.entity.item.EntityMinecart;

public class DropperBehavior extends DispenserBehavior {

	public static class DropperBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new DropperBehavior(cart);
		}
	}

	public DropperBehavior(EntityMinecart cart) {
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
