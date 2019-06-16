package T145.metaltransport.api.obj;

import T145.metaltransport.api.consts.RegistryMT;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.DataSerializerEntry;

@ObjectHolder(RegistryMT.ID)
public class SerializersMT {

	private SerializersMT() {}

	@ObjectHolder(RegistryMT.KEY_CART_TYPE)
	public static DataSerializerEntry ENTRY_CART_TYPE;
	public static DataSerializer CART_TYPE;
}