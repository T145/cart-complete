package T145.metaltransport.client.render.entities;

import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
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

	@Override
	protected void renderCartContents(EntityMetalMinecart cart, float partialTicks, IBlockState state) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0.5, 0.5, -0.5);
		GlStateManager.scale(2, 2, 2);
		Minecraft.getMinecraft().getRenderItem().renderItem(cart.getDisplayData(), ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}
}
