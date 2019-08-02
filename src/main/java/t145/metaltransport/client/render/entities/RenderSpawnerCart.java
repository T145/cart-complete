package t145.metaltransport.client.render.entities;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSpawnerCart extends RenderMetalCart {

	public RenderSpawnerCart(RenderManager manager) {
		super(manager);
	}

	@Override
	public void renderDisplayTile(EntityMinecart cart, float partialTicks) {
		super.renderDisplayTile(cart, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -0.1F, 0);
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		TileEntityMobSpawnerRenderer.renderMob(((EntityMinecartMobSpawner) cart).mobSpawnerLogic, 0, 0, 0, partialTicks);
		GlStateManager.popMatrix();
	}
}