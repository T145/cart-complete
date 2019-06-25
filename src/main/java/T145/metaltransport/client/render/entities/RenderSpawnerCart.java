package T145.metaltransport.client.render.entities;

import T145.metaltransport.api.obj.CapabilitiesMT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecartMobSpawner;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSpawnerCart extends RenderMinecartMobSpawner {

	public RenderSpawnerCart(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMinecartMobSpawner cart) {
		if (cart.hasCapability(CapabilitiesMT.CART_TYPE, null)) {
			return cart.getCapability(CapabilitiesMT.CART_TYPE, null).getType().getResource();
		} else {
			return super.getEntityTexture(cart);
		}
	}

	@Override
	protected void renderCartContents(EntityMinecartMobSpawner cart, float partialTicks, IBlockState spawnerState) {
		super.renderCartContents(cart, partialTicks, spawnerState);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0, -0.5);
		TileEntityMobSpawnerRenderer.renderMob(cart.mobSpawnerLogic, 0, 0, 0, partialTicks);
		GlStateManager.popMatrix();
	}
}
