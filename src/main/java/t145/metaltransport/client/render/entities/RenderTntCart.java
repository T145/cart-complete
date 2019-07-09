package t145.metaltransport.client.render.entities;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTntMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.caps.CapabilityCartType;

@SideOnly(Side.CLIENT)
public class RenderTntCart extends RenderTntMinecart {

	public RenderTntCart(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMinecartTNT cart) {
		if (cart.hasCapability(CapabilityCartType.instance, null)) {
			return cart.getCapability(CapabilityCartType.instance, null).getType().getModel();
		} else {
			return super.getEntityTexture(cart);
		}
	}
}