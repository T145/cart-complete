package T145.metaltransport.client.render.entities;

import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
		ItemStack data = cart.getDisplayData();

		if (data.getItem() instanceof ItemBlock) {
			super.renderCartContents(cart, partialTicks, state);
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0.5, -0.5);
		GlStateManager.scale(2, 2, 2);
		Minecraft.getMinecraft().getRenderItem().renderItem(data, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
		// coloring handled by the normal minecart renderer
	}
}
