package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.entity.item.EntityMinecart;

public class PistonBehavior extends CartBehavior {

	public static class PistonBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new PistonBehavior(cart);
		}
	}

	public PistonBehavior(EntityMinecart cart) {
		super(cart);
	}

	// TODO: Implement piston extension in a cart
}
