package T145.metaltransport.api.profiles;

import net.minecraft.entity.item.EntityMinecart;

public interface ICartProfileFactory extends IFactory<EntityMinecart> {

	@Override
	ICartProfile create(EntityMinecart cart);
}