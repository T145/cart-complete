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

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(Items.MINECART);
		}
	}.setBackgroundImageName("item_search.png");

	public static boolean inDevMode() {
		return VERSION.contentEquals("@VERSION@");
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(ID, path);
	}

	public static final String KEY_METAL_MINECART = "metal_minecart";
}
