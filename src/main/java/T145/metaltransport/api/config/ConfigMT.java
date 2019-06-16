package T145.metaltransport.api.config;

import T145.metaltransport.api.consts.RegistryMT;
import net.minecraftforge.common.config.Config;

@Config(modid = RegistryMT.ID, name = "T145/" + RegistryMT.NAME)
@Config.LangKey(RegistryMT.ID)
public class ConfigMT {

	private ConfigMT() {}

	@Config.Comment("Whether or not all vanilla minecarts are removed (entities & items).")
	public static boolean replaceVanillaMinecarts = true;
}
