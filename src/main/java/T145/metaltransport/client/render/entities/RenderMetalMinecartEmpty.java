package T145.metaltransport.client.render.entities;

import T145.metaltransport.entities.EntityMetalMinecartEmpty;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMetalMinecartEmpty extends RenderMinecart<EntityMetalMinecartEmpty> {

	public RenderMetalMinecartEmpty(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMetalMinecartEmpty cart) {
		return cart.getCartType().getResource();
	}
}
