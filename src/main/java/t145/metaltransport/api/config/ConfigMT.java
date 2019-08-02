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

	private ConfigMT() {}

	public static boolean hasRailcraft() {
		return Loader.isModLoaded("railcraft");
	}
}