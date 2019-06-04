package T145.metaltransport.api;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(RegistryMT.ID)
public class ItemsMT {

	private ItemsMT() {}

	@ObjectHolder(RegistryMT.KEY_METAL_MINECART)
	public static Item METAL_MINECART;
}