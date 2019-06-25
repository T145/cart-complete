package T145.metaltransport.client.render.entities;

import T145.metaltransport.api.obj.CapabilitiesMT;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCart extends RenderMinecart {

	public RenderCart(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMinecart cart) {
		if (cart.hasCapability(CapabilitiesMT.CART_TYPE, null)) {
			return cart.getCapability(CapabilitiesMT.CART_TYPE, null).getType().getResource();
		} else {
			return super.getEntityTexture(cart);
		}
	}
}
