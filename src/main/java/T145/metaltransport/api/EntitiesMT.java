package T145.metaltransport.api;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(RegistryMT.ID)
public class EntitiesMT {

	private EntitiesMT() {}

	@ObjectHolder(RegistryMT.KEY_METAL_MINECART)
	public static EntityEntry METAL_MINECART;

	@ObjectHolder(RegistryMT.KEY_METAL_MINECART_BLOCK)
	public static EntityEntry METAL_MINECART_BLOCK;
}