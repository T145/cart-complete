package T145.metaltransport.api.constants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;

public class RegistryMT {

	private RegistryMT() {}

	public static final String ID = "metaltransport";
	public static final String NAME = "MetalTransport";
	public static final String VERSION = "@VERSION@";
	public static final String UPDATE_JSON = "https://raw.githubusercontent.com/T145/metaltransport/master/update.json";
	public static final Logger LOG = LogManager.getLogger(ID);
	public static final ResourceLocation RECIPE_GROUP = new ResourceLocation(ID);

	public static boolean inDevMode() {
		return VERSION.contentEquals("@VERSION@");
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(ID, path);
	}

	public static final String KEY_METAL_MINECART = "metal_minecart";
	public static final ResourceLocation RESOURCE_METAL_MINECART = getResource(KEY_METAL_MINECART);
	public static final String KEY_METAL_MINECART_BLOCK = "metal_minecart_block";
	public static final ResourceLocation RESOURCE_METAL_MINECART_BLOCK = getResource(KEY_METAL_MINECART_BLOCK);

	public static final String KEY_CART_TYPE = "cart_type";
	public static final String KEY_CART_ACTION = "cart_action";
}
