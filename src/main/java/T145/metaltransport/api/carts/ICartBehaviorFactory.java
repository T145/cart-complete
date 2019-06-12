package T145.metaltransport.api.carts;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityMinecart;

public interface ICartBehaviorFactory {

	@Nonnull
	ICartBehavior createBehavior(EntityMinecart cart);
}
