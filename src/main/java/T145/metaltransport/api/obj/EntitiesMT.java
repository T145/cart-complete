package T145.metaltransport.api.obj;

import T145.metaltransport.api.consts.RegistryMT;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(RegistryMT.ID)
public class EntitiesMT {

	private EntitiesMT() {}

	@ObjectHolder(RegistryMT.KEY_METAL_MINECART)
	public static EntityEntry METAL_MINECART;
}