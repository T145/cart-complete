package T145.metaltransport.api.carts;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityMinecart;

public interface ICartProfileFactory {

	@Nonnull
	ICartProfile createProfile(EntityMinecart cart);
}