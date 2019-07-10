package t145.metaltransport.api.profiles;

import net.minecraft.entity.item.EntityMinecart;

public interface IProfileFactory {

	IProfile create(EntityMinecart cart);
}
