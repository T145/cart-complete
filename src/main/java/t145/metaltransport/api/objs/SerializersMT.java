package t145.metaltransport.api.objs;

import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.DataSerializerEntry;
import t145.metaltransport.api.consts.CartType;
import t145.metaltransport.api.consts.RegistryMT;

@ObjectHolder(RegistryMT.ID)
public class SerializersMT {

	@ObjectHolder(RegistryMT.KEY_CART_TYPE)
	public static DataSerializerEntry ENTRY_CART_TYPE;
	public static DataSerializer<CartType> CART_TYPE;

	private SerializersMT() {}

}
