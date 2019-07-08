package t145.metaltransport.client.render.entities;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.caps.CapabilityCartType;

@SideOnly(Side.CLIENT)
public class RenderCart extends RenderMinecart {

	public RenderCart(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMinecart cart) {
		if (cart.hasCapability(CapabilityCartType.instance, null)) {
			return cart.getCapability(CapabilityCartType.instance, null).getType().getModel();
		} else {
			return super.getEntityTexture(cart);
		}
	}
}