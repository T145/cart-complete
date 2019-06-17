package T145.metaltransport.api.profiles;

public interface IRenderProfile extends IProfile {

	void render(double x, double y, double z, float partialTicks, int destroyStage, float alpha, float entityYaw);
}
