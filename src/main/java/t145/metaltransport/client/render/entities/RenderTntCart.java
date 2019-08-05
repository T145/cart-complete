package t145.metaltransport.client.render.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTntCart extends RenderMetalCart {

	public RenderTntCart(RenderManager manager) {
		super(manager);
	}

	@Override
	public void renderDisplayTile(EntityMinecart cart, float partialTicks) {
		EntityMinecartTNT tntCart = (EntityMinecartTNT) cart;

		int i = tntCart.getFuseTicks();

		if (i > -1 && (float) i - partialTicks + 1.0F < 10.0F) {
			float f = 1.0F - ((float) i - partialTicks + 1.0F) / 10.0F;
			f = MathHelper.clamp(f, 0.0F, 1.0F);
			f = f * f;
			f = f * f;
			float f1 = 1.0F + f * 0.3F;
			GlStateManager.scale(f1, f1, f1);
		}

		super.renderDisplayTile(cart, partialTicks);

		if (i > -1 && i / 5 % 2 == 0) {
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, (1.0F - ((float) i - partialTicks + 1.0F) / 100.0F) * 0.8F);
			GlStateManager.pushMatrix();
			blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
		}
	}
}