package T145.metaltransport.client.render.entities;

import T145.metaltransport.api.obj.CapabilitiesMT;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTntMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTntCart extends RenderTntMinecart {

	public RenderTntCart(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMinecartTNT cart) {
		if (cart.hasCapability(CapabilitiesMT.CART_TYPE, null)) {
			return cart.getCapability(CapabilitiesMT.CART_TYPE, null).getType().getResource();
		} else {
			return super.getEntityTexture(cart);
		}
	}
}
