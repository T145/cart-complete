package T145.metaltransport.api.constants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegistryMT {

	private RegistryMT() {}

	public static final String ID = "metaltransport";
	public static final String NAME = "MetalTransport";
	public static final String VERSION = "@VERSION@";
	public static final String UPDATE_JSON = "https://raw.githubusercontent.com/T145/metaltransport/master/update.json";
	public static final Logger LOG = LogManager.getLogger(ID);

	public static final CreativeTabs TAB = new CreativeTabs(ID) {

		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.MINECART);
		}
	};

	public static boolean inDevMode() {
		return VERSION.contentEquals("@VERSION@");
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(ID, path);
	}

	public static final String KEY_METAL_MINECART = "metal_minecart";
	public static final ResourceLocation RESOURCE_METAL_MINECART = getResource(KEY_METAL_MINECART);
	public static final String KEY_CART_TYPE = "cart_type";
}
