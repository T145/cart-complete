package t145.metaltransport.api.objs;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import t145.metaltransport.api.consts.RegistryMT;

@ObjectHolder(RegistryMT.ID)
public class ItemsMT {

	@ObjectHolder(RegistryMT.KEY_METAL_MINECART)
	public static Item METAL_MINECART;

	private ItemsMT() {}

}
