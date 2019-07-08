package t145.metaltransport.api.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;
import t145.metaltransport.api.consts.RegistryMT;

@Config(modid = RegistryMT.ID, name = "T145/" + RegistryMT.NAME)
@Config.LangKey(RegistryMT.ID)
public class ConfigMT {

	@Config.Comment("Whether or not ALL empty minecarts should be handled automatically.")
	@Config.RequiresMcRestart
	public static boolean handleEmptyMinecarts = true;

	@Config.Comment("Cart classes which should be modified by the MetalTransport system. NOTE: Vanilla handled internally.")
	@Config.RequiresMcRestart
	public static String[] whitelist = new String[] {};

	@Config.Comment("Cart classes which should ignored by the MetalTransport system.")
	@Config.RequiresMcRestart
	public static String[] blacklist = new String[] {};

	private ConfigMT() {}

	public static boolean hasRailcraft() {
		return Loader.isModLoaded("railcraft");
	}
}