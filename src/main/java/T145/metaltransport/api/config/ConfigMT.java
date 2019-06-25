package T145.metaltransport.api.config;

import T145.metaltransport.api.consts.RegistryMT;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;

@Config(modid = RegistryMT.ID, name = "T145/" + RegistryMT.NAME)
@Config.LangKey(RegistryMT.ID)
public class ConfigMT {

	private ConfigMT() {}

	@Config.Comment("Whether or not all vanilla minecarts are removed (entities & items).")
	public static boolean replaceVanillaMinecarts = true;

	@Config.Comment("Cart classes which should be modified by the MetalTransport system. NOTE: Vanilla handled internally.")
	@Config.RequiresMcRestart
	public static String[] whitelist = new String[] {};

	public static boolean hasRailcraft() {
		return Loader.isModLoaded("railcraft");
	}
}
