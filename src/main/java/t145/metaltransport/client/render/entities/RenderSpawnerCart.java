package t145.metaltransport.client.render.entities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSpawnerCart extends RenderCart {

	public RenderSpawnerCart(RenderManager manager) {
		super(manager);
	}

	@Override
	protected void renderCartContents(EntityMinecart cart, float partialTicks, IBlockState spawnerState) {
		super.renderCartContents(cart, partialTicks, spawnerState);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0, -0.5);
		TileEntityMobSpawnerRenderer.renderMob(((EntityMinecartMobSpawner) cart).mobSpawnerLogic, 0, 0, 0, partialTicks);
		GlStateManager.popMatrix();
	}
}