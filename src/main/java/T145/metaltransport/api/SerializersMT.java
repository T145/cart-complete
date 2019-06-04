package T145.metaltransport.api;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(RegistryMT.ID)
public class SerializersMT {

	private SerializersMT() {}

	@ObjectHolder(RegistryMT.KEY_CART_TYPE)
	public static DataSerializer CART_TYPE;
}