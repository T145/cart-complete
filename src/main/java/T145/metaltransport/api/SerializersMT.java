package T145.metaltransport.api;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.DataSerializerEntry;

@ObjectHolder(RegistryMT.ID)
public class SerializersMT {

	private SerializersMT() {}

	@ObjectHolder(RegistryMT.KEY_CART_TYPE)
	public static DataSerializerEntry ENTRY_CART_TYPE;
	public static DataSerializer CART_TYPE;

	@ObjectHolder(RegistryMT.KEY_CART_ACTION)
	public static DataSerializerEntry ENTRY_CART_ACTION;
	public static DataSerializer CART_ACTION;

	public static <T> DataSerializer<T> getSerializer(DataSerializerEntry entry) {
		return (DataSerializer<T>) entry.getSerializer();
	}
}