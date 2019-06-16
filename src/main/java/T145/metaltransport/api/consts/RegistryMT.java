package T145.metaltransport.api.consts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;

public class RegistryMT {

	private RegistryMT() {}

	public static final String ID = "metaltransport";
	public static final String NAME = "MetalTransport";
	public static final Logger LOG = LogManager.getLogger(ID);
	public static final ResourceLocation RECIPE_GROUP = new ResourceLocation(ID);

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(ID, path);
	}

	public static final String KEY_METAL_MINECART = "metal_minecart";
	public static final ResourceLocation RESOURCE_METAL_MINECART = getResource(KEY_METAL_MINECART);

	public static final String KEY_CART_TYPE = "cart_type";
}
