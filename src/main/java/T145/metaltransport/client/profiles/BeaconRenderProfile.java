package T145.metaltransport.client.profiles;

import T145.metaltransport.api.consts.RegistryMT;
import T145.metaltransport.api.profiles.IRenderProfile;
import T145.metaltransport.api.profiles.IRenderProfileFactory;
import net.minecraft.client.renderer.entity.Render;

public class BeaconRenderProfile implements IRenderProfile {
	
	public static class BeaconRenderProfileFactory implements IRenderProfileFactory {

		@Override
		public BeaconRenderProfile create(Render param) {
			return new BeaconRenderProfile();
		}
	}

	@Override
	public void render(double x, double y, double z, float partialTicks, int destroyStage, float alpha, float entityYaw) {
		RegistryMT.LOG.info("RENDERING!");
	}
}
