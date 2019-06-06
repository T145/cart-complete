package T145.metaltransport.client.render.entities;

import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMetalMinecart extends RenderMinecart<EntityMetalMinecart> {

	public RenderMetalMinecart(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMetalMinecart cart) {
		return cart.getCartType().getResource();
	}
}
